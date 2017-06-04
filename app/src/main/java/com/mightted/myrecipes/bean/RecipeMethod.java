package com.mightted.myrecipes.bean;

/**
 * recipe做菜步骤类
 * Created by 晓深 on 2017/6/3.
 */

public class RecipeMethod {

    private String methodImg; //步骤图片
    private String methodText; //步骤文字说明

    public String getMethodImg() {
        return methodImg;
    }

    public void setMethodImg(String methodImg) {
        this.methodImg = methodImg;
    }

    public String getMethodText() {
        return methodText;
    }

    public void setMethodText(String methodText) {
        this.methodText = methodText;
    }
}
