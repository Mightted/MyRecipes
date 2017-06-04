package com.mightted.myrecipes.databindings.handlers;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.flexbox.FlexboxLayout;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.databinding.StepItemBinding;
import com.mightted.myrecipes.databinding.TagItemBinding;
import com.mightted.myrecipes.databindings.models.RecipeViewModel;
import com.mightted.myrecipes.ui.adapter.QuickPageAdapter;
import java.util.ArrayList;
import java.util.List;
import me.relex.circleindicator.CircleIndicator;

/**
 * recipe展示界面动态加载handler
 * Created by 晓深 on 2017/6/3.
 */

public class RecipeViewHandler {

    /**
     * 初始化ViewPager，主要是动态加载
     * @param pager 传入的viewPager
     * @param viewModel
     */
    public void initViewPager(ViewPager pager, RecipeViewModel viewModel) {
        List<View> viewList = new ArrayList<>();
        for(int i = 0; i < viewModel.getMethodList().size(); i++) {

            StepItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(pager.getContext()),R.layout.step_item,null,false);
            binding.setMethod(viewModel.getMethodList().get(i));
            viewList.add(binding.getRoot());

//            View view = LayoutInflater.from(pager.getContext()).inflate(R.layout.step_item,null);
//            ImageView imageView = (ImageView)view.findViewById(R.id.step_img);
//            TextView textView = (TextView)view.findViewById(R.id.step_text);
//            Glide.with(pager.getContext()).load(viewModel.getMethodList().get(i).getMethodImg()).into(imageView);
//            textView.setText(viewModel.getMethodList().get(i).getMethodText());

//            viewList.add(view);

        }
        viewModel.setAdapter(new QuickPageAdapter<>(viewList));
        pager.setAdapter(viewModel.getAdapter());
    }

    /**
     * 初始化圆点指示器，动态加载
     * @param indicator
     * @param viewModel
     * @param viewPager
     */
    public void initIndicator(CircleIndicator indicator,RecipeViewModel viewModel,ViewPager viewPager) {
        indicator.setViewPager(viewPager);
        viewModel.getAdapter().registerDataSetObserver(indicator.getDataSetObserver());
    }

    /**
     * 初始化标签列表，动态加载
     * @param flexboxLayout
     * @param viewModel
     */
    public void initTag(FlexboxLayout flexboxLayout,RecipeViewModel viewModel) {
        for(int i = 0; i < viewModel.getTags().size(); i++) {
            TagItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(flexboxLayout.getContext()),R.layout.tag_item,null,false);
            binding.setTag(viewModel.getTags().get(i));
            binding.setTagHandler(new TagHandler());
            flexboxLayout.addView(binding.getRoot());
//            View view = LayoutInflater.from(flexboxLayout.getContext()).inflate(R.layout.tag_item,flexboxLayout,false);
//            final RecipeTag tag = viewModel.getTags().get(i);
//            TextView textView = (TextView)view.findViewById(R.id.tag_text);
//            textView.setText(tag.getTagName());
//            flexboxLayout.addView(view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), MainActivity.class);
//                    intent.putExtra("recipeType",tag.getTagId());
//                    v.getContext().startActivity(intent);
//                }
//            });
        }
    }
}
