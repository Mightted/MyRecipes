package com.mightted.myrecipes.ui.adapter;

import android.support.v7.widget.RecyclerView;
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

import java.util.List;


/**
 * Created by 晓深 on 2017/5/7.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int NORMAL_ITEM_TYPE = 0;
    private static int FOOTER_ITEM_TYPE = 1;

    List<RecipeItem> recipeItems;

    public RecipeAdapter(List<RecipeItem> list) {
        this.recipeItems = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == NORMAL_ITEM_TYPE) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item,parent,false));
        } else {
            return new FootHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            RecipeItem item = recipeItems.get(position);
            ((ViewHolder)holder).titleView.setText(item.getTitle());
            Glide.with(MyApplication.getContext()).load(item.getImg()).into(((ViewHolder)holder).imageView);
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
//            return position == recipeItems.size() ? FOOTER_ITEM_TYPE : NORMAL_ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return recipeItems == null ? 0 : recipeItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            titleView = (TextView)view.findViewById(R.id.recipe_title);
            imageView = (ImageView)view.findViewById(R.id.recipe_img);
        }
    }

    public static class FootHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public FootHolder(View itemView) {
            super(itemView);
        }
    }
}
