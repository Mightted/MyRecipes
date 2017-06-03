package com.mightted.myrecipes.databindings.handlers;

import android.view.View;
import android.widget.Toast;

/**
 * 执行标签点击动作的Handler类
 * Created by 晓深 on 2017/6/3.
 */

public class TagHandler {

    public void onHandleTag(View view) {
        Toast.makeText(view.getContext(),"跳转",Toast.LENGTH_SHORT).show();

    }
}
