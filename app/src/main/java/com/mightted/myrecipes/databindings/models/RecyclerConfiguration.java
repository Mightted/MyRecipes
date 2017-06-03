package com.mightted.myrecipes.databindings.models;

import android.support.v7.widget.RecyclerView;

import com.mightted.myrecipes.ui.adapter.RecipeAdapter;

/**
 * Created by 晓深 on 2017/5/31.
 */

public class RecyclerConfiguration {

    private RecyclerView.LayoutManager manager;
    private RecipeAdapter adapter;

    public RecyclerConfiguration(RecyclerView.LayoutManager manager,RecipeAdapter adapter) {
        this.manager = manager;
        this.adapter = adapter;
    }

    public RecyclerView.LayoutManager getManager() {
        return manager;
    }

    public RecipeAdapter getAdapter() {
        return adapter;
    }
}
