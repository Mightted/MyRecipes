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

    /**
     * 图片加载的自定义属性
     * @param view
     * @param imageUrl
     */
    @BindingAdapter("imageUrl")
    public static void loadImg(ImageView view,String imageUrl) {
        ImageUtil.loadImage(view,imageUrl);
    }

    /**
     * 列表项点击动作的请求处理
     * @param context 上下文
     * @param item 列表项
     */
    public void intoRecipePage(Context context, RecipeItem item) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("recipeId",item.getRecipeId());
        intent.putExtra("recipeTitle",item.getTitle());
        intent.putExtra("recipeImg",item.getRecipeImg());
        context.startActivity(intent);
    }

    /**
     * RecyclerView加载配置的自定义属性
     * @param recyclerView 组件
     * @param configuration 配置对象
     */
    @BindingAdapter("recyclerConfigure")
    public static void recyclerConfigure(RecyclerView recyclerView,RecyclerConfiguration configuration) {
        recyclerView.setLayoutManager(configuration.getManager());
        recyclerView.setAdapter(configuration.getAdapter());
    }


}
