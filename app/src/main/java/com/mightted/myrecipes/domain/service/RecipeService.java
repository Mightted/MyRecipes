package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecipeDetail;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 获取相关菜谱详细信息的service
 * Created by 晓深 on 2017/5/9.
 */

public interface RecipeService {

    /**
     * 获取菜谱的详细信息
     * @param key 接口appKey
     * @param id 菜谱id
     * @return 菜谱详细信息
     */
    @GET("menu/query")
    Observable<RecipeDetail> getRecipe(@Query("key")String key,@Query("id")String id);
}
