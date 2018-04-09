package ru.taxcom.mobile.android.calendarlibrary.util.customView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Left to Right view pager
 */
public class CustomViewPager extends ViewPager {
    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(convertPosition(item), smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        int currentItem = super.getCurrentItem();
        return convertPosition(currentItem);
    }

    public int convertPosition(int i) {
        int count = getAdapter().getCount();
        return count - 1 - i;
    }
}
