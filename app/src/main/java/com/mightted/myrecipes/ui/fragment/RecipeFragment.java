package com.mightted.myrecipes.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.bean.RecipeTag;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.domain.service.RecipeService;
import com.mightted.myrecipes.ui.activity.MainActivity;
import com.mightted.myrecipes.ui.activity.RecipeActivity;
import com.mightted.myrecipes.ui.adapter.QuickPageAdapter;
import com.mightted.myrecipes.utils.RetrofitUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 晓深 on 2017/5/9.
 */

public class RecipeFragment extends Fragment {

    private TextView sumaryText;

    private TextView ingredientText;

    private TextView burdenText;

    private List<RecipeTag> tagList;

    private FlexboxLayout flexboxLayout;

    private LinearLayout linearLayout;

    private ViewPager viewPager;

    private QuickPageAdapter<View> adapter;

    List<View> viewList;

    private String recipeId;


    public RecipeFragment(String recipeId) {
        this.recipeId = recipeId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe,container,false);

        sumaryText = (TextView)view.findViewById(R.id.recipe_sumary_text);
        ingredientText = (TextView)view.findViewById(R.id.recipe_ingredient_text);
        burdenText = (TextView)view.findViewById(R.id.recipe_burden_text);
        flexboxLayout = (FlexboxLayout)view.findViewById(R.id.flex_box);
        linearLayout = (LinearLayout)view.findViewById(R.id.step_content);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        onCreateFragment(recipeId);
        return view;
    }

    private void onCreateFragment(String id) {
        RecipeService service = RetrofitClient.getInstance().create(RecipeService.class);
        Log.i("accept",id);
        service.getRecipe(RetrofitUtil.KEY,id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<RecipeDetail>() {
                    @Override
                    public void accept(RecipeDetail recipeDetail) throws Exception {
                        if(recipeDetail.msg.equals("success")) {
                            Log.i("accept","请求成功");
                            tagList = new ArrayList<>();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecipeDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("initData","onSubscribe is called");
                    }

                    @Override
                    public void onNext(RecipeDetail value) {
                        Log.i("initData","onNext is called");
                        initData(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("initData","onError is called");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("initData","onComplete is called");
                    }
                });
    }

    private void initData(RecipeDetail recipe) {

        //初始化标签部分
        String[] tagNames = recipe.result.ctgTitles.split(",");
        for(int i = 0; i < tagNames.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.tag_item,flexboxLayout,false);
            TextView textView = (TextView)view.findViewById(R.id.tag_text);
            textView.setText(tagNames[i]);
            flexboxLayout.addView(view);
            RecipeTag tag = new RecipeTag();
            tag.setTagId(recipe.result.ctgIds.get(i));
            tag.setTagName(tagNames[i]);
            Log.i("initData",tagNames[i]);
            tagList.add(tag);
        }

        sumaryText.setText(recipe.result.recipe.sumary);

//        Log.i("initData",recipe.result.recipe.ingredients);

//        String[] ingredients = recipe.result.recipe.ingredients.split(",");
        List<String> ingredients = null;
        try {
            if(recipe.result.recipe.ingredients != null) {
                ingredients = LoganSquare.parseList(recipe.result.recipe.ingredients,String.class);
                ingredientText.setText(ingredients.get(0));
                if(ingredients.size() > 1) {
                    burdenText.setText(ingredients.get(1));
                }

            } else {
                ingredientText.setText("无");
                burdenText.setText("无");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        String method = recipe.result.recipe.method;
        List<RecipeDetail.Result.Recipe.Method> methods;
        try {
            if(method != null) {
                viewList = new ArrayList<>();
                methods = LoganSquare.parseList(method,RecipeDetail.Result.Recipe.Method.class);
                for(int i = 0; i < methods.size(); i++) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.step_item,linearLayout,false);
                    ImageView imageView = (ImageView)view.findViewById(R.id.step_img);
                    TextView textView = (TextView)view.findViewById(R.id.step_text);
                    Glide.with(getActivity()).load(methods.get(i).img).into(imageView);
                    textView.setText(methods.get(i).step);

//                    linearLayout.addView(view);

//                    View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.layout_test,null);
//                    ((TextView)view2.findViewById(R.id.text)).setText("Hello");

                    View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.step_item,null);
                    ImageView imageView2 = (ImageView)view2.findViewById(R.id.step_img);
                    TextView textView2 = (TextView)view2.findViewById(R.id.step_text);
                    Glide.with(getActivity()).load(methods.get(i).img).into(imageView2);
                    textView2.setText(methods.get(i).step);

                    viewList.add(view2);
                }
                adapter = new QuickPageAdapter<>(viewList);
                viewPager.setAdapter(adapter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


//        List<RecipeDetail.Result.Recipe.Method> methods = recipe.result.recipe.methods;


    }
}
