package com.mightted.myrecipes.ui.fragment;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.flexbox.FlexboxLayout;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.dagger.components.DaggerRecipeComponent;
import com.mightted.myrecipes.databinding.FragmentRecipeBinding;
import com.mightted.myrecipes.databindings.handlers.RecipeViewHandler;
import com.mightted.myrecipes.databindings.models.RecipeViewModel;

import javax.inject.Inject;
/**
 * 菜谱详细展示Fragment
 * Created by 晓深 on 2017/5/9.
 */

@SuppressLint("ValidFragment")
public class RecipeFragment extends Fragment {

    private String recipeId;

    @Inject
    RecipeViewModel viewModel;


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

        FragmentRecipeBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recipe,container,false);

        DaggerRecipeComponent.builder().build().inject(this);
        viewModel.setViewPager(binding.viewPager);
        viewModel.setCircleIndicator(binding.indicator);
        viewModel.setFlexboxLayout((FlexboxLayout)binding.getRoot().findViewById(R.id.flex_box));

        if(recipeId == null) {
            if(savedInstanceState != null) {
                recipeId = savedInstanceState.getString("recipeId");
            } else {
                getActivity().finish();
            }
        }

        viewModel.onDealRecipeImpl(recipeId);
        binding.setRecipeModel(viewModel);
        binding.setRecipeViewHandler(new RecipeViewHandler());
        binding.indicator.setViewPager(binding.viewPager);

        return binding.getRoot();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("recipeId",recipeId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(viewModel.getDisposable() != null) {
            viewModel.getDisposable().dispose();
        }
    }
}
