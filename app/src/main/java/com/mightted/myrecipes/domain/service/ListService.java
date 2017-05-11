package com.mightted.myrecipes.domain.service;

import com.mightted.myrecipes.domain.entity.RecipeList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 菜谱列表相关service
 * Created by 晓深 on 2017/5/7.
 */

public interface ListService {

    /**
     * 获取相关类型的菜谱item
     * @param appKey 接口appKey
     * @param ctgId 菜谱烈性标签ID
     * @param name 查询菜谱名字
     * @param page 当前要加载的页
     * @param size 要加载的页数
     * @return 菜谱items
     */
    @GET("menu/search")
    Observable<RecipeList> getList(@Query("key")String appKey,@Query("cid")String ctgId,@Query("name")String name,@Query("page")int page,@Query("size")int size);
}
