package com.foxridge.towerfox.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.foxridge.towerfox.R;


public class CustomFontUtils {

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomFont);

        String fontStyle = attributeArray.getString(R.styleable.CustomFont_font_style);

        assert fontStyle != null;
        Typeface customFont = selectTypeface(context, fontStyle);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, String fontStyle) {
        if (fontStyle.equals("black")){
            return FontCache.getTypeface("proxima-nova-soft-black-webfont.ttf", context);
        }

        if (fontStyle.equals("bold")){
            return FontCache.getTypeface("proxima-nova-soft-bold-webfont.ttf", context);
        }

        if (fontStyle.equals("medium")){
            return FontCache.getTypeface("proxima-nova-soft-medium-webfont.ttf", context);
        }

        if (fontStyle.equals("regular")){
            return FontCache.getTypeface("proxima-nova-soft-regular-webfont.ttf", context);
        }

        if (fontStyle.equals("semibold")){
            return FontCache.getTypeface("proxima-nova-soft-semibold-webfont.ttf", context);
        }

        if (fontStyle.equals("light")){
            return FontCache.getTypeface("proxima-nova-soft-light-webfont.ttf", context);
        }

        return FontCache.getTypeface("proxima-nova-soft-normal-webfont.ttf", context);
    }
}