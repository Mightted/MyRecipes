package com.mightted.myrecipes.ui.model;

import android.util.Log;
import android.widget.Toast;

import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.domain.service.RecipeService;
import com.mightted.myrecipes.utils.RetrofitUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 晓深 on 2017/6/2.
 */

public class RecipeModel {

    private RetrofitClient client;

    @Inject
    public RecipeModel(RetrofitClient client) {
        this.client = client;
    }

    public void onDealRecipe(String id,Observer<RecipeDetail> observer) {
        RecipeService service = client.create(RecipeService.class);
        service.getRecipe(RetrofitUtil.KEY,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
