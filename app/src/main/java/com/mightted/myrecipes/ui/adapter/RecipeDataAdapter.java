package com.mightted.myrecipes.ui.adapter;

import com.bluelinelabs.logansquare.LoganSquare;
import com.mightted.myrecipes.bean.RecipeMethod;
import com.mightted.myrecipes.bean.RecipeTag;
import com.mightted.myrecipes.databindings.models.RecipeViewModel;
import com.mightted.myrecipes.domain.entity.RecipeDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * API获取菜谱数据处理的适配器
 * Created by 晓深 on 2017/6/3.
 */

public class RecipeDataAdapter {

    /**
     * 将原生的网络数据，转换为完整可用的视图数据，中间经过缺省处理
     * @param recipe 传入的原生数据（破损有）
     * @return
     */
    public static RecipeViewModel getRecipeView(RecipeDetail recipe) {
        RecipeViewModel viewModel = new RecipeViewModel();
        String[] tagNames = recipe.result.ctgTitles.split(",");
        List<RecipeTag> tagList = new ArrayList<>();
        for(int i = 0; i < tagNames.length; i++) {
            RecipeTag tag = new RecipeTag();
            tag.setTagId(recipe.result.ctgIds.get(i));
            tag.setTagName(tagNames[i]);

            tagList.add(tag);
        }
        viewModel.setTags(tagList);

        try {
            viewModel.setRecipeSumary(recipe.result.recipe.sumary);
        } catch (NullPointerException e) {
            viewModel.setRecipeSumary("无");
        }


        viewModel.setRecipeName(recipe.result.name);


        //大量的空指针捕获，最容易出问题的地方
        List<String> ingredients;
        try {
            if(recipe.result.recipe.ingredients != null) {
                ingredients = LoganSquare.parseList(recipe.result.recipe.ingredients,String.class);
                viewModel.setRecipeIngredient(ingredients.get(0));
                if(ingredients.size() > 1) {
                    viewModel.setRecipeBurden(ingredients.get(1));
                } else {
                    viewModel.setRecipeBurden("无");
                }

            } else {
                viewModel.setRecipeIngredient("无");
                viewModel.setRecipeBurden("无");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            viewModel.setRecipeIngredient("无");
            viewModel.setRecipeBurden("无");
        }


        List<RecipeDetail.Result.Recipe.Method> methods;
        try {
            String method = recipe.result.recipe.method;
            if(method != null) {
                List<RecipeMethod> methodList = new ArrayList<>();
                methods = LoganSquare.parseList(method,RecipeDetail.Result.Recipe.Method.class);
                for(int i = 0; i < methods.size(); i++) {


                    RecipeMethod recipeMethod = new RecipeMethod();
                    recipeMethod.setMethodImg(methods.get(i).img);
                    recipeMethod.setMethodText(methods.get(i).step);

                    methodList.add(recipeMethod);
                }
                viewModel.setMethodList(methodList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO: 2017/5/12 空值异常进行处理，但是步骤为空我有什么办法~
        }
        return viewModel;
    }
}
