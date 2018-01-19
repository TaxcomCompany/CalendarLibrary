package ru.taxcom.mobile.android.calendarlibrary.util.textview;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public final class FontCache {

    private static final HashMap<String, Typeface> FONT_CACHE = new HashMap<>();

    @Nullable
    public static Typeface getTypeface(@NonNull String fontName, Context context) {
        Typeface typeface = FONT_CACHE.get(fontName);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                return null;
            }
            FONT_CACHE.put(fontName, typeface);
        }
        return typeface;
    }

    private FontCache() {
    }
}
