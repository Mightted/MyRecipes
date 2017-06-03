package com.mightted.myrecipes.databindings.handlers;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.mightted.myrecipes.utils.LogUtil;

/**
 * Created by 晓深 on 2017/5/31.
 */

public class LoadItemHandler {

//    @BindingAdapter(value = "android:onRefresh",requireAll = false)
//    public static void onRefreshList(SwipeRefreshLayout view,final OnRefresh onRefresh) {
//        SwipeRefreshLayout.OnRefreshListener newListener = null;
//        if(onRefresh != null) {
//            newListener = new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//
//                }
//            };
//        }
//
//        view.setOnRefreshListener(newListener);
////        SwipeRefreshLayout.OnRefreshListener oldListener = ListenerUtil.trackListener(view,newListener, R.id.swipe_refresh);
////        if(oldListener != null) {
////            view.
////        }
//    }

    @BindingAdapter("isRefreshing")
    public static void onSetIsRefreshing(SwipeRefreshLayout refreshLayout,boolean isRefreshing) {
        LogUtil.i("onSetIsRefreshing","在这里设置加载状态");
        refreshLayout.setRefreshing(isRefreshing);
    }

    public interface OnRefresh {
        public void onRefresh();
    }

    public void onLoadMore(){

    }
}
