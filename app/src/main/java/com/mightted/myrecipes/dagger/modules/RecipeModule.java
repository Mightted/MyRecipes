package com.mightted.myrecipes.dagger.modules;

import com.mightted.myrecipes.databindings.models.RecipeViewModel;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.ui.model.RecipeModel;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by 晓深 on 2017/6/3.
 */

@Module
public class RecipeModule {

    @Provides
    public RecipeViewModel provideRecipeViewHolder(RetrofitClient client) {
        return new RecipeViewModel(new RecipeModel(client));
    }

    @Provides
    public RetrofitClient provideRetrofitClient() {
        return RetrofitClient.getInstance();
    }



}
