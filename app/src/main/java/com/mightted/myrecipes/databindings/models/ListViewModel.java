package com.mightted.myrecipes.databindings.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.mightted.myrecipes.BR;
import com.mightted.myrecipes.domain.entity.RecipeList;
import com.mightted.myrecipes.ui.adapter.RecipeAdapter;
import com.mightted.myrecipes.ui.adapter.RecipeListAdapter;
import com.mightted.myrecipes.ui.model.ListModel;
import com.mightted.myrecipes.utils.LogUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 晓深 on 2017/6/2.
 */

public class ListViewModel extends BaseObservable {

    private String oldType = "";
    private String currentType = "";
    private String oldRecipe = "";
    private String currentRecipe = "";
    private int currentPage = 1;

    private ListModel model;

    private List<RecipeItem> itemList;

    private RecipeListAdapter recipeListAdapter;

    private RecipeAdapter adapter;

    private boolean refreshing;

    private Disposable disposable;

    public ListViewModel(ListModel model, List list , RecipeAdapter adapter) {
        this.model = model;
        this.itemList = list;
        recipeListAdapter = new RecipeListAdapter();
        this.adapter = adapter;
//        setRefreshing(true);
    }

    @Bindable
    public List<RecipeItem> getItemList() {
        return itemList;
    }

    public void setItemList(List list) {
        itemList.addAll(list);
        LogUtil.i("setItemList","刷新数据");
        adapter.notifyDataSetChanged();
    }

    public void setCurrentType(String type) {
        this.oldType = currentType;
        this.currentType = type;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentRecipe(String recipe) {
        this.oldRecipe = currentRecipe;
        this.currentRecipe = recipe;
    }


    @Bindable
    public boolean getRefreshing() {
        return refreshing;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if(refreshing) {
            currentPage = 1;
            onDealList();
        }
        notifyPropertyChanged(BR.refreshing);
    }

    public void onDealList() {
        model.onDealRecipeList(currentType, currentRecipe, currentPage,new Observer<RecipeList>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(RecipeList value) {
                //根据当前页来判断当前加载状态是加载更多还是刷新
                if(currentPage != 1) {
                    itemList.add(new RecipeItem());
                    adapter.notifyItemInserted(itemList.size()-1);
                }
                List<RecipeItem> items = recipeListAdapter.getRecipeItems(value);
                //注意，当输入关键字时，我们的当前查询结果是会被刷掉的，因此搜不出东西来的时候，在此对数据进行恢复
                if(items == null) {
                    currentType = oldType;
                    currentRecipe = oldRecipe;
                    return;
                }

                if(refreshing) {
                    itemList.clear();
                } else {
                    itemList.remove(itemList.size()-1);
                }

                setItemList(items);
                currentPage ++;

            }

            @Override
            public void onError(Throwable e) {
                setRefreshing(false);
                LogUtil.i("onDealRecipeList","失败");
            }

            @Override
            public void onComplete() {
                setRefreshing(false);
                LogUtil.i("onDealRecipeList","成功");
            }
        });
    }




}
