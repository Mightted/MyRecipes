package com.mightted.myrecipes.databindings.models;



/**
 * 菜谱列表项bean
 * Created by 晓深 on 2017/5/8.
 */

public class RecipeItem {

    private String title;
    private String img;
    private String recipeId;
    private String recipeImg;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        this.recipeImg = recipeImg;
    }
}
