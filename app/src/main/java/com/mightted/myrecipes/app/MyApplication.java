package com.mightted.myrecipes.app;

import android.app.Application;
import android.content.Context;


import org.litepal.LitePal;


/**
 * 自定义Application
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

}
