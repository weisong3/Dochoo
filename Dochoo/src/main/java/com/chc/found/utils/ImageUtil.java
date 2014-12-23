package com.chc.found.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.makeramen.RoundedDrawable;

/**
 * Created by HenryW on 12/17/13.
 */
public class ImageUtil {
    public static final Drawable createIconImageView(Resources res, Bitmap bitmap) {
        return RoundedDrawable.fromDrawable(new BitmapDrawable(res, bitmap));
    }
}
