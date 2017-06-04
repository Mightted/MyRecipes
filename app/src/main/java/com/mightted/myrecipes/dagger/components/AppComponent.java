package com.mightted.myrecipes.dagger.components;

import android.app.Application;

import com.mightted.myrecipes.dagger.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 晓深 on 2017/6/1.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(Application application);
}
