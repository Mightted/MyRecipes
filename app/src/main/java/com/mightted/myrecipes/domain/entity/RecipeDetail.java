package com.mightted.myrecipes.domain.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by 晓深 on 2017/5/7.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class RecipeDetail {

    public List<String> ctgIds; //分类ID

    public String ctgTitles;  //分类标签

    public String menuId; //菜谱id

    public String name; //菜谱名称

    @JsonObject
    public static class Recipe {

        public String img;

        public String ingredients; //菜谱配料

        @JsonField(name = "method")
        public List<Method> methods; //菜谱步骤

        @JsonObject
        public static class Method {

            public String img;

            public String step;

        }

        public String sumary; //菜谱描述

        public String title; //菜谱标题

    }

    public String thumbnail;
}
