package com.chc.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chc.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HenryW on 12/17/13.
 */
public class TypefacedTextView extends TextView {

    private static final Map<String, Typeface> typefaceMap = new HashMap<String, Typeface>();

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
        styledAttrs.recycle();

        if (fontName != null) {
            Typeface typeface;
            if (typefaceMap.containsKey(fontName)) {
                typeface = typefaceMap.get(fontName);
            } else {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
                typefaceMap.put(fontName, typeface);
            }
            setTypeface(typeface);
        }
    }

    public static final Typeface getTypeface(AssetManager assetManager, String fontName) {
        Typeface typeface;
        if (typefaceMap.containsKey(fontName)) {
            typeface = typefaceMap.get(fontName);
        } else {
            typeface = Typeface.createFromAsset(assetManager, fontName);
            typefaceMap.put(fontName, typeface);
        }
        return typeface;
    }

}