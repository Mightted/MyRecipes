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
 * Created by 晓深 on 2017/5/31.
 */

public class SearchHandler {


    public SearchHandler() {

    }

    public void onTextChange(View view,boolean hasFocus) {
        if(!hasFocus) {
            view.setVisibility(View.GONE);
            InputMethodManager manager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event, ListViewModel listViewModel) {
        if(actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(v.getText())) {
            listViewModel.setCurrentType("");
            listViewModel.setCurrentRecipe(v.getText().toString());
            listViewModel.setRefreshing(true);
        }
        return false;
    }
}
