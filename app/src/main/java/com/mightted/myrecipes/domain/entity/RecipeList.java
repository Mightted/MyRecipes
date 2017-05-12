package com.mightted.myrecipes.domain.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * 用于加载菜谱列表数据的pojo
 * Created by 晓深 on 2017/5/7.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class RecipeList {

    public String msg;

    public Result result;

    @JsonObject
    public static class Result {

        @JsonField(name = "curPage")
        public String currentPage;

        @JsonField(name = "list")
        public List<RecipeDetail.Result> recipeList;

        @JsonField
        public int total;
    }

    @JsonField(name = "retCode")
    public String resultCode;
}
