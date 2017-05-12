package com.mightted.myrecipes.domain.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * 加载菜谱标签的pojo
 * Created by 晓深 on 2017/5/8.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class RecType {

    @JsonField
    public String msg;

    @JsonField(name = "retCode")
    String resultCode;

    @JsonField
    public Result result;

    @JsonObject
    public static class Result {

        @JsonField(name = "categoryInfo")
        AllRecipe all;

        @JsonField(name = "childs")
        public List<SearchType> types;


        @JsonObject
        static class AllRecipe {

            @JsonField
            public String ctgId;

            @JsonField
            public String name;
        }


        @JsonObject
        public static class SearchType {

            @JsonField(name = "categoryInfo")
            public SearchInfo info;

            @JsonField(name = "childs")
            public List<Type> types;

            @JsonObject
            static class SearchInfo {

                @JsonField
                String ctgId;

                @JsonField
                public String name;

                @JsonField
                String parentId;
            }

            @JsonObject
            public static class Type{

                @JsonField(name = "categoryInfo")
                public TypeInfo info;

                @JsonObject
                public static class TypeInfo {

                    @JsonField
                    public String ctgId;

                    @JsonField
                    public String name;

                    @JsonField
                    public String parentId;
                }

            }
        }

    }

}
