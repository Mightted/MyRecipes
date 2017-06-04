package com.mightted.myrecipes.dagger.modules;

import android.support.v7.widget.RecyclerView;

import com.mightted.myrecipes.databindings.models.ListViewModel;
import com.mightted.myrecipes.databindings.models.RecipeItem;
import com.mightted.myrecipes.databindings.models.RecyclerConfiguration;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.ui.adapter.RecipeAdapter;
import com.mightted.myrecipes.ui.model.ListModel;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 晓深 on 2017/5/31.
 */

@Module
public class ListModule {

    private RecyclerView.LayoutManager manager;

    private List<RecipeItem> itemList;

    public ListModule(RecyclerView.LayoutManager manager,List list) {
        this.manager = manager;
        this.itemList = list;
    }

    @Provides
    public ListViewModel provideListViewModule(RetrofitClient client,RecipeAdapter adapter) {
        return new ListViewModel(new ListModel(client),itemList,adapter);
    }

    @Provides
    @Singleton
    public RecyclerView.LayoutManager provideLayoutManager() {
        return manager;
    }


    @Provides
    @Singleton
    public RecipeAdapter provideAdapter() {
        return new RecipeAdapter(itemList);
    }


    @Provides
    @Singleton
    public RecyclerConfiguration provideRecyclerConfiguration(RecyclerView.LayoutManager manager,RecipeAdapter adapter) {
        return new RecyclerConfiguration(manager,adapter);
    }

    @Provides
    @Singleton
    public RetrofitClient provideRetrofitClient() {
        return RetrofitClient.getInstance();
    }


}
