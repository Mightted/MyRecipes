package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecipeList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 晓深 on 2017/5/7.
 */

public interface ListService {

    @GET("menu/search")
    Observable<RecipeList> getList(@Query("key")String appKey,@Query("cid")String ctgId,@Query("name")String name,@Query("page")int page,@Query("size")int size);
}
