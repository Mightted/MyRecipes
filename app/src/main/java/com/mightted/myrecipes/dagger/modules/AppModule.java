package com.mightted.myrecipes.dagger.modules;

import android.content.Context;

import com.mightted.myrecipes.domain.RetrofitClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 晓深 on 2017/5/31.
 */

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mContext;
    }

}
