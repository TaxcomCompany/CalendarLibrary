package ru.taxcom.mobile.android.calendarlibrary.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

public class DisableScrollGridLayoutManager extends GridLayoutManager {

    public DisableScrollGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}