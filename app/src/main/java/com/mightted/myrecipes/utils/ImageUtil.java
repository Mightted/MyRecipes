package com.mightted.myrecipes.utils;

import android.widget.ImageView;

import com.mightted.myrecipes.utils.ImageLoader.GlideImageLoader;
import com.mightted.myrecipes.utils.ImageLoader.ImageLoader;

/**
 * 图片加载类，使用单例模式，策略模式
 * Created by 晓深 on 2017/5/31.
 */

public class ImageUtil {

    private static ImageLoader loader = new GlideImageLoader();

    private static ImageUtil util = new ImageUtil();

    private ImageUtil() {}

    public static ImageUtil getInstance() {
        return util;
    }

    public void setLoader(ImageLoader loader) {
        this.loader = loader;
    }

    public static void loadImage(ImageView imageView, String imageUrl) {
        loader.loadImage(imageView,imageUrl);
    }
}
