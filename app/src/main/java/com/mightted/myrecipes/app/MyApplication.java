package com.mightted.myrecipes.app;

import android.app.Application;
import android.content.Context;
import com.mightted.myrecipes.dagger.components.DaggerAppComponent;
import com.mightted.myrecipes.dagger.modules.AppModule;
import com.mightted.myrecipes.db.RecipeType;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecType;
import com.mightted.myrecipes.domain.service.TypeService;
import com.mightted.myrecipes.utils.LogUtil;
import com.mightted.myrecipes.utils.RetrofitUtil;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 自定义Application
 * Created by 晓深 on 2017/5/8.
 */

public class MyApplication extends Application {

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        LitePal.getDatabase();
        DaggerAppComponent.builder().appModule(new AppModule(this)).build().inject(this);
        mContext = getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<RecipeType> recipeTypes = DataSupport.findAll(RecipeType.class);
                if(recipeTypes.size() == 0) {
                    initDB();

                }
            }
        }).start();

    }

    /**
     * 初次选择菜谱类型选择时，将菜谱类型保存进数据库中
     */
    private void initDB() {
        TypeService service = RetrofitClient.getInstance().create(TypeService.class);
        service.initRecipeType(RetrofitUtil.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RecType>() {
                    @Override
                    public void accept(RecType recType) throws Exception {
                        if(recType.msg.equals("success")) {
                            LogUtil.i("initRecipeType","分类标签查询请求成功");
                        } else {
                            LogUtil.i("initRecipeType","分类标签查询请求失败");
                            LogUtil.i("initRecipeType",recType.msg);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<RecType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.i("initRecipeType","onSubscribe is called");
                    }
                    @Override
                    public void onNext(RecType value) {
                        LogUtil.i("initRecipeType","onNext is called");
                        List<RecType.Result.SearchType> types = value.result.types;
                        for(RecType.Result.SearchType type:types) {

                            List<RecType.Result.SearchType.Type> recipeTypes = type.types;
                            for(RecType.Result.SearchType.Type recipeType:recipeTypes) {

                                RecipeType recipe = new RecipeType();
                                recipe.setCtgId(recipeType.info.ctgId);
                                recipe.setName(recipeType.info.name);
                                recipe.setParentId(recipeType.info.parentId);
                                recipe.setChoosed(false);
//                                LogUtil.i("initRecipeType",recipeType.info.ctgId +" " + recipeType.info.name +" " +recipeType.info.parentId);
                                recipe.save();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static Context getContext() {
        return mContext;
    }

}
