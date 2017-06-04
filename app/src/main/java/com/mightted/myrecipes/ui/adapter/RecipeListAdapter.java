package com.mightted.myrecipes.ui.adapter;
import com.mightted.myrecipes.databindings.models.RecipeItem;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.domain.entity.RecipeList;
import com.mightted.myrecipes.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * API获得的菜谱列表处理的适配器
 * 因为API数据有各种各样的问题，因此这里需要进行处理才行
 * Created by 晓深 on 2017/6/2.
 */

public class RecipeListAdapter {


    public RecipeListAdapter() {
    }

    public List<RecipeItem> getRecipeItems(RecipeList recipeList) {
        List<RecipeDetail.Result> list;
        try {
            list = recipeList.result.recipeList;
        }catch (NullPointerException e) {
            list = new ArrayList<>();
        }


        if(list.size() > 0) {
            List<RecipeItem> itemList = new ArrayList<>();
            for(RecipeDetail.Result recipe:list) {
                RecipeItem item = new RecipeItem();
                //由于由于部分菜谱没有title标签，取消之
                // TODO: 2017/5/10 之前由于部分数据没有title而导致无法处理的NullPointException，现在可以使用异常捕获解决了
                try {
                    item.setTitle(recipe.recipe.title);

                } catch (NullPointerException e) {
                    item.setTitle(recipe.name);
                }
                item.setImg(recipe.thumbnail);

                item.setRecipeId(recipe.menuId);

                try {
                    item.setRecipeImg(recipe.recipe.img);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                itemList.add(item);

                LogUtil.i("onNext","name为:" + item.getTitle());
            }
            return itemList;
        } else {
            return null;
        }
    }
}
