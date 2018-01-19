package ru.taxcom.mobile.android.calendarlibrary.util.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public final class RegularTextView extends AppCompatTextView {

    public RegularTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Roboto-Regular.ttf", context);
        setTypeface(customFont);
    }
}
