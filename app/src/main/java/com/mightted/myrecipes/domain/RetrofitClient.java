package com.mightted.myrecipes.domain;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by 晓深 on 2017/5/7.
 */

public class RetrofitClient {

    private static RetrofitClient client = new RetrofitClient();
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://apicloud.mob.com/v1/cook/")
                .addConverterFactory(LoganSquareConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static RetrofitClient getInstance() {
        return client;
    }

    public <T>T create(Class<?> clazz) {
        return (T)retrofit.create(clazz);
    }
}
