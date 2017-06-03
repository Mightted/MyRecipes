package com.mightted.myrecipes.ui.model;

import com.mightted.myrecipes.domain.RetrofitClient;

import javax.inject.Inject;

/**
 * Created by 晓深 on 2017/6/2.
 */

public class RecipeModel {

    private RetrofitClient client;

    @Inject
    public RecipeModel(RetrofitClient client) {
        this.client = client;
    }

    public void onDealRecipe() {

    }
}
