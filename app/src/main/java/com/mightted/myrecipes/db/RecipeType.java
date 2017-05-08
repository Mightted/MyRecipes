package com.mightted.myrecipes.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 晓深 on 2017/5/8.
 */

public class RecipeType extends DataSupport {
    private String ctgId;
    private String name;
    private String parentId;
    private boolean isChoosed;

    public String getCtgId() {
        return ctgId;
    }

    public void setCtgId(String ctgId) {
        this.ctgId = ctgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
