package com.mightted.myrecipes.utils.ImageLoader;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mightted.myrecipes.R;

/**
 * Created by 晓深 on 2017/5/31.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void loadImage(ImageView imageView, String imageUrl) {
        if(imageUrl == null || imageUrl.equals("")) {
//            imageView.setImageDrawable(null);
            Glide.with(imageView.getContext()).load(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
        }
    }
}
