package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecipeDetail;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 晓深 on 2017/5/9.
 */

public interface RecipeService {

    @GET("menu/query")
    Observable<RecipeDetail> getRecipe(@Query("key")String key,@Query("id")String id);
}
