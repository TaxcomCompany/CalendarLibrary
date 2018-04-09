package ru.taxcom.mobile.android.calendarlibrary.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;

public class PagerMonthAdapter extends PagerAdapter {

    public interface CreateDataListener {
        void create(int position, Consumer<List<PickerModel>> listener);
    }

    public interface UpdateDataListener {
        void update(List<PickerModel> list, Action listener);
    }

    public interface OnDateSelected {
        void onDateSelected(Long dateInSec);
    }

    public static final int SELECT_PERIOD = 1;
    public static final int SELECT_MONTH_OR_YEAR = 2;

    private final Context mContext;
    private final OnDateSelected mOnDateSelectedListener;
    private final CreateDataListener mCreateDataListener;
    private int mMode;
    @Nullable
    private UpdateDataListener mUpdateDataListener;
    private final int mCount;

    private final ArrayMap<Integer, PickerAdapter> mAdapters;

    public PagerMonthAdapter(Context context, int count, int mode, OnDateSelected listener,
                             CreateDataListener createDataListener, @Nullable UpdateDataListener updateDataListener) {
        mContext = context;
        mCount = count;
        mMode = mode;
        mOnDateSelectedListener = listener;
        mCreateDataListener = createDataListener;
        mUpdateDataListener = updateDataListener;
        mAdapters = new ArrayMap<>();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_month, container, false);
        initView(layout, position);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mAdapters.remove(position);
        container.removeView((View) object);
    }

    private void initView(ViewGroup layout, int position) {
        int span;
        PickerAdapter pickerAdapter;
        int widthViewDp;
        switch (mMode) {
            case SELECT_PERIOD:
                pickerAdapter = new PickerAdapter(this::onPeriodSelected, R.layout.item_date_range, 0);
                widthViewDp = 40;
                span = 7;
                break;
            case SELECT_MONTH_OR_YEAR:
            default:
                pickerAdapter = new PickerAdapter(mOnDateSelectedListener, R.layout.item_date_month, R.drawable.click_month_shape);
                widthViewDp = 90;
                span = 3;
                break;
        }

        initList(layout, position, span, pickerAdapter, widthViewDp);
    }

    private void initList(ViewGroup layout, int position, int span, PickerAdapter pickerAdapter, int widthViewDp) {
        RecyclerView calendarList = layout.findViewById(R.id.calendar_list);
        calendarList.setLayoutManager(new DisableScrollGridLayoutManager(mContext, span));
        calendarList.setHasFixedSize(true);
        calendarList.setAdapter(pickerAdapter);
        calendarList.addItemDecoration(new GridSpacingItemDecoration(mContext, span, widthViewDp));

        mAdapters.put(position, pickerAdapter);
        mCreateDataListener.create(position, pickerAdapter::update);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private void onPeriodSelected(Long dateInSec) {
        mOnDateSelectedListener.onDateSelected(dateInSec);

        int index = mAdapters.size() == 3 ? 1 : 0;
        mUpdateDataListener.update(mAdapters.valueAt(index).getList(), () -> update(index));
    }

    private void update(int index) {
        mUpdateDataListener.update(mAdapters.valueAt(index).getList(), mAdapters.valueAt(index)::update);
        for (int i = 0; i < mAdapters.size(); i++) {
            if (i == index) {
                continue;
            }
            int finalI = i;
            mUpdateDataListener.update(mAdapters.valueAt(i).getList(), () -> post(mAdapters.valueAt(finalI)));
        }
    }

    private void post(PickerAdapter pickerAdapter) {
        new Handler().post(pickerAdapter::update);
    }
}