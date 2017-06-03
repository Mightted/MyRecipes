package com.mightted.myrecipes.ui.model;

import android.util.Log;
import android.widget.Toast;

import com.mightted.myrecipes.app.MyApplication;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecipeList;
import com.mightted.myrecipes.domain.service.ListService;
import com.mightted.myrecipes.utils.LogUtil;
import com.mightted.myrecipes.utils.RecipeUtil;
import com.mightted.myrecipes.utils.RetrofitUtil;

import javax.inject.Inject;

import dagger.Module;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 晓深 on 2017/5/31.
 */

@Module
public class ListModel {

    private RetrofitClient client;

    @Inject
    public ListModel(RetrofitClient client) {
        this.client = client;
    }



    public void onDealRecipeList(String currentType, String currentRecipe, final int currentPage, Observer<RecipeList> observer) {
        ListService service = client.create(ListService.class);
//        ListService service = RetrofitClient.getInstance().create(ListService.class);
        LogUtil.i("MainActivity",RetrofitUtil.KEY + " :" + currentType + " :" + currentRecipe + " :" + currentPage + " :" + RecipeUtil.PageCount);
        service.getList(RetrofitUtil.KEY,currentType,currentRecipe,currentPage, RecipeUtil.PageCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RecipeList>() {
                    @Override
                    public void accept(RecipeList recipeList) throws Exception {
                        LogUtil.i("MainActivity","内部请求");
                        if(recipeList.resultCode.equals("200")) {
                            LogUtil.i("MainActivity","请求成功返回");
                        } else {
                            Log.i("MainActivity","请求错误");
                            Toast.makeText(MyApplication.getContext(),"无更多结果",Toast.LENGTH_SHORT).show();
//                            successRequest = false;
                        }
                    }
                }).subscribe(observer);
    }
}
