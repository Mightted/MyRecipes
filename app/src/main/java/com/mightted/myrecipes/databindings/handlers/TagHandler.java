package com.mightted.myrecipes.databindings.handlers;

import android.content.Intent;
import android.view.View;
import com.mightted.myrecipes.ui.activity.MainActivity;

/**
 * 执行标签点击动作的Handler类
 * Created by 晓深 on 2017/6/3.
 */

public class TagHandler {

    /**
     * 点击菜谱标签时执行动作，跳转到标签相关的菜谱列表
     * @param view
     * @param tagId
     */
    public void onHandleTag(View view,String tagId) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.putExtra("recipeType",tagId);
        view.getContext().startActivity(intent);

    }
}
