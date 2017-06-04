package com.mightted.myrecipes.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mightted.myrecipes.R;
import com.mightted.myrecipes.databinding.FooterItemBinding;
import com.mightted.myrecipes.databinding.RecipeItemBinding;
import com.mightted.myrecipes.databindings.handlers.RecipeItemHandler;
import com.mightted.myrecipes.databindings.models.RecipeItem;

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
            RecipeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.recipe_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(binding.getRoot());
            viewHolder.setBinding(binding);
            return viewHolder;

        } else {
            FooterItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.footer_item,parent,false);
            FootHolder footHolder = new FootHolder(binding.getRoot());
            footHolder.setBinding(binding);
            return footHolder;
        }

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ((ViewHolder) holder).getBinding().setRecipeItem(recipeItems.get(position));
            ((ViewHolder) holder).getBinding().setRecipeItemHandler(new RecipeItemHandler());

            ((ViewHolder) holder).getBinding().executePendingBindings();
        } else {
            ((FootHolder) holder).getBinding().executePendingBindings();
        }


    }



    @Override
    public int getItemViewType(int position) {
        if(recipeItems == null) {
            return NORMAL_ITEM_TYPE;
        } else {
            RecipeItem item = recipeItems.get(position);
            if(item.getTitle() == null || item.getTitle().equals("")) {
                return FOOTER_ITEM_TYPE;
            } else {
                return NORMAL_ITEM_TYPE;
            }
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



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RecipeItemBinding binding;

        ViewHolder(View view) {
            super(view);
        }

        public void setBinding(RecipeItemBinding binding) {
            this.binding = binding;
        }

        public RecipeItemBinding getBinding() {
            return binding;
        }
    }



    public static class FootHolder extends RecyclerView.ViewHolder {

        private FooterItemBinding binding;

        FootHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(FooterItemBinding binding) {
            this.binding = binding;
        }

        public FooterItemBinding getBinding() {
            return binding;
        }
    }
}
