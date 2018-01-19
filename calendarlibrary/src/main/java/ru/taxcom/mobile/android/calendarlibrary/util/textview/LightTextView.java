package ru.taxcom.mobile.android.calendarlibrary.util.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public final class LightTextView extends AppCompatTextView {

    public LightTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public LightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public LightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Roboto-Light.ttf", context);
        setTypeface(customFont);
    }
}
