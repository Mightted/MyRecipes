package com.mightted.myrecipes.domain.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by 晓深 on 2017/5/7.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class RecipeRespon {

    public String msg;

    public RecipeDetail result;

    @JsonField(name = "retCode")
    public String resultCode;
}
