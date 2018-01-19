package ru.taxcom.mobile.android.calendarlibrary.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpanCount;
    private final int mSpacing;
    private final int mWidthViewDp;
    private Context mContext;

    public GridSpacingItemDecoration(@NonNull Context context, int spanCount, int widthViewDp) {
        mSpanCount = spanCount;
        mContext = context;
        mWidthViewDp = widthViewDp;
        mSpacing = resizeSpacing();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % mSpanCount;

        outRect.left = column * mSpacing / mSpanCount;
        outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;
        if (position >= mSpanCount) {
            outRect.top = mSpacing;
        }
    }

    private int resizeSpacing() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int widthSpace = metrics.widthPixels - pxFromDp(metrics, 32);
        int widthViews = pxFromDp(metrics, mWidthViewDp) * mSpanCount;
        return (widthSpace - widthViews) / (mSpanCount - 1);
    }

    private int pxFromDp(DisplayMetrics metrics, int demInDp) {
        return (int) (demInDp * metrics.density);
    }
}