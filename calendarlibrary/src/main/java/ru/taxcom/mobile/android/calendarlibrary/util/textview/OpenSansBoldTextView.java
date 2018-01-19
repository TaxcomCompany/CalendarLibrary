package ru.taxcom.mobile.android.calendarlibrary.util.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public final class OpenSansBoldTextView extends AppCompatTextView {

    public OpenSansBoldTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public OpenSansBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public OpenSansBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/OpenSansBold.ttf", context);
        setTypeface(customFont);
    }
}
