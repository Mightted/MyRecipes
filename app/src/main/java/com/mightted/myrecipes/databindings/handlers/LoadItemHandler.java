package com.mightted.myrecipes.databindings.handlers;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.mightted.myrecipes.utils.LogUtil;

/**
 * 加载组件handler类
 * Created by 晓深 on 2017/5/31.
 */

public class LoadItemHandler {

    @BindingAdapter("isRefreshing")
    public static void onSetIsRefreshing(SwipeRefreshLayout refreshLayout,boolean isRefreshing) {
        LogUtil.i("onSetIsRefreshing","在这里设置加载状态");
        refreshLayout.setRefreshing(isRefreshing);
    }

}
