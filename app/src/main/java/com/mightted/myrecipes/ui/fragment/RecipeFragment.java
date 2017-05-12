package com.mightted.myrecipes.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.bean.RecipeTag;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.domain.service.RecipeService;
import com.mightted.myrecipes.ui.activity.MainActivity;
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
import me.relex.circleindicator.CircleIndicator;

/**
 * 菜谱详细展示Fragment
 * Created by 晓深 on 2017/5/9.
 */

@SuppressLint("ValidFragment")
public class RecipeFragment extends Fragment {

    private TextView sumaryText;

    private TextView ingredientText;

    private TextView burdenText;

    private TextView nameText;

    private List<RecipeTag> tagList;

    private FlexboxLayout flexboxLayout;

//    private LinearLayout linearLayout;

    private ViewPager viewPager;

    private CircleIndicator indicator;

    private QuickPageAdapter<View> adapter;

    List<View> viewList;

    private String recipeId;

    private Disposable disposable;


    /**
     *  正常情况下调用的有参构造函数
     * @param recipeId 传入的菜谱id
     */
    public RecipeFragment(String recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * 后台被kill从而重新唤醒时调用该函数，由于recipeId是必要的，因此会从savedInstanceState中获得保存的值
     */
    public RecipeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe,container,false);

        sumaryText = (TextView)view.findViewById(R.id.recipe_sumary_text);
        ingredientText = (TextView)view.findViewById(R.id.recipe_ingredient_text);
        burdenText = (TextView)view.findViewById(R.id.recipe_burden_text);
        nameText = (TextView)view.findViewById(R.id.recipe_name);
        flexboxLayout = (FlexboxLayout)view.findViewById(R.id.flex_box);
//        linearLayout = (LinearLayout)view.findViewById(R.id.step_content);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        indicator = (CircleIndicator)view.findViewById(R.id.indicator);

        //从savedInstanceState中获取recipeId
        if(recipeId == null) {
            if(savedInstanceState != null) {
                recipeId = savedInstanceState.getString("recipeId");
            } else {
                getActivity().finish();
            }
        }

        onCreateFragment(recipeId);
        return view;
    }

    /**
     * 完全主要数据初始化和布局初始化工作
     * @param id 加载的菜谱的recipeId
     */
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
                        disposable = d;
                        Log.i("initData","onSubscribe is called");
                    }

                    @Override
                    public void onNext(RecipeDetail value) {
                        Log.i("initData","onNext is called");
                        initData(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),"服务器或网络错误",Toast.LENGTH_SHORT).show();
                        Log.i("initData","onError is called");
                    }

                    @Override
                    public void onComplete() {
                        if(disposable != null) {
                            disposable = null;
                        }
                        Log.i("initData","onComplete is called");
                    }
                });
    }

    /**
     * 处理返回的菜谱数据，并加载到布局中
     * @param recipe 返回的菜谱数据
     */
    private void initData(RecipeDetail recipe) {

        //初始化标签部分
        String[] tagNames = recipe.result.ctgTitles.split(",");
        for(int i = 0; i < tagNames.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.tag_item,flexboxLayout,false);
            TextView textView = (TextView)view.findViewById(R.id.tag_text);
            textView.setText(tagNames[i]);
            flexboxLayout.addView(view);
            final RecipeTag tag = new RecipeTag();
            tag.setTagId(recipe.result.ctgIds.get(i));
            tag.setTagName(tagNames[i]);
            Log.i("initData",tagNames[i]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("recipeType",tag.getTagId());
                    startActivity(intent);
                }
            });
            tagList.add(tag);
        }

        try {
            sumaryText.setText(recipe.result.recipe.sumary);
        } catch (NullPointerException e) {
            sumaryText.setText("无");
        }


        nameText.setText(recipe.result.name);

//        Log.i("initData",recipe.result.recipe.ingredients);

        List<String> ingredients;
        try {
            if(recipe.result.recipe.ingredients != null) {
                ingredients = LoganSquare.parseList(recipe.result.recipe.ingredients,String.class);
                ingredientText.setText(ingredients.get(0));
                if(ingredients.size() > 1) {
                    burdenText.setText(ingredients.get(1));
                } else {
                    burdenText.setText("无");
                }

            } else {
                ingredientText.setText("无");
                burdenText.setText("无");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            ingredientText.setText("无");
            burdenText.setText("无");
        }


        List<RecipeDetail.Result.Recipe.Method> methods;
        try {
            String method = recipe.result.recipe.method;
            if(method != null) {
                viewList = new ArrayList<>();
                methods = LoganSquare.parseList(method,RecipeDetail.Result.Recipe.Method.class);
                for(int i = 0; i < methods.size(); i++) {
//                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.step_item,linearLayout,false);
//                    ImageView imageView = (ImageView)view.findViewById(R.id.step_img);
//                    TextView textView = (TextView)view.findViewById(R.id.step_text);
//                    Glide.with(getActivity()).load(methods.get(i).img).into(imageView);
//                    textView.setText(methods.get(i).step);

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
                indicator.setViewPager(viewPager);
                adapter.registerDataSetObserver(indicator.getDataSetObserver());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO: 2017/5/12 空值异常进行处理，但是步骤为空我有什么办法~
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("recipeId",recipeId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null) {
            disposable.dispose();
        }
    }
}
