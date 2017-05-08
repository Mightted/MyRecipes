package com.mightted.myrecipes.app;

import android.app.Application;
import android.content.Context;

import com.mightted.myrecipes.db.RecipeType;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by 晓深 on 2017/5/8.
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        LitePal.getDatabase();
        mContext = getApplicationContext();
    }


    public static Context getContext() {
        return mContext;
    }

    private void initDB() {

        List<RecipeType> recipeTypes = DataSupport.findAll(RecipeType.class);
        if(recipeTypes.size() == 0) {

        }
    }
}
