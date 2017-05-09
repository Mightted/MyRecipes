package com.mightted.myrecipes.domain.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by 晓深 on 2017/5/7.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class RecipeDetail {

    public String msg;

    public Result result;

    @JsonObject
    public static class Result {
        @JsonField
        public List<String> ctgIds; //分类ID

        @JsonField
        public String ctgTitles;  //分类标签

        @JsonField
        public String menuId; //菜谱id

        @JsonField
        public String name; //菜谱名称

        @JsonField
        public Recipe recipe;

        @JsonObject
        public static class Recipe {

            @JsonField
            public String img;

            @JsonField
            public String ingredients; //菜谱配料，API没做处理

            @JsonField
            public String method;  //做菜步骤，API没做处理

//            @JsonObject
//            public static class Ingredient {
//
//                @JsonField
//                public String ingredient;  //原料
//
//                @JsonField
//                public String burden;  //配料
//            }

            @JsonObject
            public static class Method {

                @JsonField
                public String img;

                @JsonField
                public String step;

            }

            @JsonField
            public String sumary; //菜谱描述

            @JsonField
            public String title; //菜谱标题

        }

        @JsonField
        public String thumbnail;
    }



}
