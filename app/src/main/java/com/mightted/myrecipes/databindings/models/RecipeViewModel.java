package com.mightted.myrecipes.databindings.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;
import com.mightted.myrecipes.BR;
import com.mightted.myrecipes.bean.RecipeMethod;
import com.mightted.myrecipes.bean.RecipeTag;
import com.mightted.myrecipes.databindings.handlers.RecipeViewHandler;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.ui.adapter.QuickPageAdapter;
import com.mightted.myrecipes.ui.adapter.RecipeDataAdapter;
import com.mightted.myrecipes.ui.model.RecipeModel;
import com.mightted.myrecipes.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;

/**
 * Recipe展示界面Model
 * Created by 晓深 on 2017/6/3.
 */

public class RecipeViewModel extends BaseObservable {

    //菜谱描述
    private String recipeSumary;

    //菜谱名字
    private String recipeName;

    //菜谱原料
    private String recipeIngredient;

    //菜谱配料
    private String recipeBurden;

    //菜谱标签列表
    private List<RecipeTag> tags;

    private RecipeModel model;

    //菜谱制作步骤
    private List<RecipeMethod> methodList;

    private ViewPager viewPager;

    private CircleIndicator indicator;

    private FlexboxLayout flexboxLayout;

    //用于动态刷新上面三个组件的handler
    private RecipeViewHandler handler = new RecipeViewHandler();

    //用于ViewPager的适配器
    private QuickPageAdapter<View> adapter;

    private Disposable disposable;

    public QuickPageAdapter<View> getAdapter() {
        return adapter;
    }


    public void setAdapter(QuickPageAdapter<View> adapter) {
        this.adapter = adapter;
    }


    /**
     * 主构造方法
     * @param model
     */
    public RecipeViewModel(RecipeModel model) {
        this.model = model;
        tags = new ArrayList<>();
        methodList = new ArrayList<>();
    }


    /**
     * 这个构造方法，主要是用于传递数据而已
     */
    public RecipeViewModel() {
        tags = new ArrayList<>();
        methodList = new ArrayList<>();
    }


    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }


    public void setCircleIndicator(CircleIndicator indicator) {
        this.indicator = indicator;
    }


    public void setFlexboxLayout(FlexboxLayout flexboxLayout) {
        this.flexboxLayout = flexboxLayout;
    }


    @Bindable
    public List<RecipeTag> getTags() {
        return tags;
    }


    /**
     * 后面这些set方法，内部都会实现更新，主要是因为获取数据的延时，会在视图加载完之后才能获取完成，因此是需要在获取完毕后通知视图进行更新
     * @param tags
     */
    public void setTags(List<RecipeTag> tags) {
        this.tags = tags;
        notifyPropertyChanged(BR.tags);
    }


    @Bindable
    public List<RecipeMethod> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<RecipeMethod> methodList) {
        this.methodList = methodList;
        notifyPropertyChanged(BR.methodList);
    }


    @Bindable
    public String getRecipeSumary() {
        return recipeSumary;
    }

    public void setRecipeSumary(String recipeSumary) {
        this.recipeSumary = recipeSumary;
        notifyPropertyChanged(BR.recipeSumary);
    }


    @Bindable
    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        notifyPropertyChanged(BR.recipeName);
    }


    @Bindable
    public String getRecipeIngredient() {
        return recipeIngredient;
    }


    public void setRecipeIngredient(String recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
        notifyPropertyChanged(BR.recipeIngredient);
    }


    @Bindable
    public String getRecipeBurden() {
        return recipeBurden;
    }


    public void setRecipeBurden(String recipeBurden) {
        this.recipeBurden = recipeBurden;
        notifyPropertyChanged(BR.recipeBurden);
    }


    public Disposable getDisposable() {
        return disposable;
    }


    /**
     * 加载recipe视图实现方法
     * @param id
     */
    public void onDealRecipeImpl(String id) {
        model.onDealRecipe(id, new Observer<RecipeDetail>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                LogUtil.i("initData","onSubscribe is called");
            }

            @Override
            public void onNext(RecipeDetail value) {
                LogUtil.i("initData","onNext is called");
                RecipeViewModel viewModel = RecipeDataAdapter.getRecipeView(value);
                setMethodList(viewModel.getMethodList());
                setRecipeBurden(viewModel.getRecipeBurden());
                setRecipeSumary(viewModel.getRecipeSumary());
                setRecipeBurden(viewModel.getRecipeBurden());
                setRecipeName(viewModel.getRecipeName());
                setTags(viewModel.getTags());
            }

            @Override
            public void onError(Throwable e) {
//                Toast.makeText(getActivity(),"服务器或网络错误",Toast.LENGTH_SHORT).show();
                LogUtil.i("initData","onError is called");
            }

            @Override
            public void onComplete() {
                //操作视图，动态刷新，主要还是这些家伙都是动态加载的，不这么做不行
                handler.initViewPager(viewPager,RecipeViewModel.this);
                handler.initIndicator(indicator,RecipeViewModel.this,viewPager);
                handler.initTag(flexboxLayout,RecipeViewModel.this);
                disposable = null;
                LogUtil.i("initData","onComplete is called");
            }
        });
    }
}
