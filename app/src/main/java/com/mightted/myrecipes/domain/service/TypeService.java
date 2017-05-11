package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecType;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 获取菜谱类型信息的service
 * Created by 晓深 on 2017/5/8.
 */


public interface TypeService {

    /**
     * 获取全部菜谱类型
     * @param key 接口appKey
     * @return 菜谱类型信息
     */
    @GET("category/query")
    Observable<RecType> initRecipeType(@Query("key")String key);
}
