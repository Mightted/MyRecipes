package com.mightted.myrecipes.dagger.components;

import com.mightted.myrecipes.dagger.modules.RecipeModule;
import com.mightted.myrecipes.ui.fragment.RecipeFragment;

import dagger.Component;

/**
 *
 * Created by 晓深 on 2017/6/3.
 */
@Component(modules = RecipeModule.class)
public interface RecipeComponent {
    void inject(RecipeFragment fragment);

}
