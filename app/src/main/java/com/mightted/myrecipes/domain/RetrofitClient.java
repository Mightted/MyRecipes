package com.mightted.myrecipes.domain;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 单例模式的初始化retrofit
 * Created by 晓深 on 2017/5/7.
 */

public class RetrofitClient {

    private static RetrofitClient client = new RetrofitClient();
    private Retrofit retrofit;

    /**
     * 初始化retrofit
     */
    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://apicloud.mob.com/v1/cook/")
                .addConverterFactory(LoganSquareConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 获取retrofit实例
     * @return retrofit实例
     */
    public static RetrofitClient getInstance() {
        return client;
    }

    /**
     * 动态获取相关service的实例
     * @param clazz 相应接口
     * @param <T> 泛型表示
     * @return create相关类实例
     */
    public <T>T create(Class<?> clazz) {
        return (T)retrofit.create(clazz);
    }
}
