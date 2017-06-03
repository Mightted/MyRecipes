package com.mightted.myrecipes.dagger.components;

import com.mightted.myrecipes.dagger.modules.ListModule;
import com.mightted.myrecipes.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 晓深 on 2017/6/1.
 */

@Singleton
@Component(modules = ListModule.class)
public interface ListComponent {
    void inject(MainActivity activity);
}
