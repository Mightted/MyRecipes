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

    //上一个菜谱类型
    private String oldType = "";

    //当前菜谱类型
    private String currentType = "";

    //上一个菜谱查询关键字
    private String oldRecipe = "";

    //当前菜谱查询关键字
    private String currentRecipe = "";

    //当前页数
    private int currentPage = 1;

    private ListModel model;

    private List<RecipeItem> itemList;

    //列表数据转换适配器
    private RecipeListAdapter recipeListAdapter;

    //RecyclerView适配器
    private RecipeAdapter adapter;

    //判断当前是否为刷新状态
    private boolean refreshing;

    private Disposable disposable;


    public ListViewModel(ListModel model, List list , RecipeAdapter adapter) {
        this.model = model;
        this.itemList = list;
        recipeListAdapter = new RecipeListAdapter();
        this.adapter = adapter;
    }


    @Bindable
    public List<RecipeItem> getItemList() {
        return itemList;
    }


    /**
     * 关键方法，这里只要对list进行修改，就会用Adapter触发视图刷新
     * @param list
     */
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

    /**
     * 超关键的一个方法，只要触发这个，就会触发列表刷新，懒人必备！
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if(refreshing) {
            currentPage = 1;
            onDealList();
        }
        notifyPropertyChanged(BR.refreshing);
    }

    /**
     * 加载列表方法实现
     */
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

                //当前如果是刷新状态的话，那么将清空列表项，如果是加载状态的话，将删除加载item
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
