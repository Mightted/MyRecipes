package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecType;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 晓深 on 2017/5/8.
 */

public interface TypeService {
    @GET("category/query")
    Observable<RecType> initRecipeType(@Query("key")String key);
}
