package com.mightted.myrecipes.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.app.MyApplication;
import com.mightted.myrecipes.bean.RecipeItem;
import com.mightted.myrecipes.ui.activity.MainActivity;
import com.mightted.myrecipes.ui.activity.RecipeActivity;

import java.util.List;


/**
 * 展示菜谱列表的adapter，通过两个ViewHolder实现加载更多的动画
 * Created by 晓深 on 2017/5/7.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //根据不同类型而选择当前加载的是普通item还是加载状态的item
    private static int NORMAL_ITEM_TYPE = 0;
    private static int FOOTER_ITEM_TYPE = 1;

    private Context mContext;

    private List<RecipeItem> recipeItems;

    public RecipeAdapter(List<RecipeItem> list) {
        this.recipeItems = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        if(viewType == NORMAL_ITEM_TYPE) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recipe_item,parent,false));
        } else {
            return new FootHolder(LayoutInflater.from(mContext).inflate(R.layout.footer_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            final RecipeItem item = recipeItems.get(position);
            ((ViewHolder)holder).titleView.setText(item.getTitle());

            /**
             * 由于API数据会出现残缺的问题，部分菜谱可能会没有预览图
             * 因此这里要进行判断，如果获取的是空数据，那么使用相关的图片代替预览图
             */
            if(item.getImg() == null || item.getImg().equals("")) {
                Glide.with(MyApplication.getContext()).load(R.drawable.no_image).into(((ViewHolder)holder).imageView);
            } else {
                Glide.with(MyApplication.getContext()).load(item.getImg()).into(((ViewHolder)holder).imageView);
            }

            ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RecipeActivity.class);
                    intent.putExtra("recipeId",item.getRecipeId());
                    intent.putExtra("recipeTitle",item.getTitle());
                    intent.putExtra("recipeImg",item.getRecipeImg());
                    mContext.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemViewType(int position) {
        if(recipeItems == null) {
            return NORMAL_ITEM_TYPE;
        } else {
            RecipeItem item = recipeItems.get(position);
            if(item.getTitle() == null || item.getTitle().equals("")) {
                Log.i("RecipeAdapter","加载item");
                return FOOTER_ITEM_TYPE;
            } else {
//                Log.i("RecipeAdapter","普通item");
//                Log.i("RecipeAdapter",item.getTitle());
                return NORMAL_ITEM_TYPE;
            }
            /**
             * 原来通过判断当前位置是否到达list末尾而选择加载的
             * 但是可能会出现重复加载的问题
             * 因此将改为诱导式（？）方式加载等待动画
             * list数据填充的时候，在填充完毕的时候会有意在最后加入一个空的item
             * 从而诱导Adapter加载特殊ViewHolder
             */
//            return position == recipeItems.size() ? FOOTER_ITEM_TYPE : NORMAL_ITEM_TYPE;
        }
    }


    @Override
    public int getItemCount() {
        /**
         * 一直以来都是返回的list的size，上次看了别人的代码，发现原来还能这样写
         * 妈妈再也不用担心我list为空时会抛出异常了
         */
        return recipeItems == null ? 0 : recipeItems.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView)view.findViewById(R.id.recipe_title);
            imageView = (ImageView)view.findViewById(R.id.recipe_img);

        }
    }

    private static class FootHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        FootHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
        }
    }
}
