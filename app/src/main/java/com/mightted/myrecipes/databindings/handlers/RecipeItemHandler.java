package com.mightted.myrecipes.databindings.handlers;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.mightted.myrecipes.databindings.models.RecipeItem;
import com.mightted.myrecipes.databindings.models.RecyclerConfiguration;
import com.mightted.myrecipes.ui.activity.RecipeActivity;
import com.mightted.myrecipes.utils.ImageUtil;

/**
 * Created by 晓深 on 2017/5/31.
 */

public class RecipeItemHandler {

    @BindingAdapter("imageUrl")
    public static void loadImg(ImageView view,String imageUrl) {
        ImageUtil.loadImage(view,imageUrl);
    }

    public void intoRecipePage(Context context, RecipeItem item) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("recipeId",item.getRecipeId());
        intent.putExtra("recipeTitle",item.getTitle());
        intent.putExtra("recipeImg",item.getRecipeImg());
        context.startActivity(intent);
    }

    @BindingAdapter("recyclerConfigure")
    public static void recyclerConfigure(RecyclerView recyclerView,RecyclerConfiguration configuration) {
        recyclerView.setLayoutManager(configuration.getManager());
        recyclerView.setAdapter(configuration.getAdapter());
    }


}
