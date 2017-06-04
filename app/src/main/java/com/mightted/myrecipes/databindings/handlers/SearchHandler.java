package com.mightted.myrecipes.databindings.handlers;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mightted.myrecipes.databindings.models.ListViewModel;

/**
 *
 * Created by 晓深 on 2017/5/31.
 */

public class SearchHandler {

    /**
     * EditText的onTextChanged方法实现，原生实现
     * @param view 组件
     * @param hasFocus 是否拥有焦点
     */
    public void onTextChange(View view,boolean hasFocus) {
        if(!hasFocus) {
            view.setVisibility(View.GONE);
            InputMethodManager manager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    /**
     * EditText的onEditorAction方法实现，自定义实现
     * @param v 组件
     * @param actionId 动作id
     * @param event 事件
     * @param listViewModel
     * @return
     */
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event, ListViewModel listViewModel) {
        if(actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(v.getText())) {
            listViewModel.setCurrentType("");
            listViewModel.setCurrentRecipe(v.getText().toString());
            listViewModel.setRefreshing(true);
        }
        return false;
    }
}
