package ru.taxcom.mobile.android.calendarlibrary.presentetion.implemenattion;

import android.content.Context;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerMode;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.presener.DateRangePresenter;
import ru.taxcom.mobile.android.calendarlibrary.util.DatePickerValidation;
import ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerView;

public class DateRangePresenterImpl implements DateRangePresenter {
    public static final int NOT_SELECTED = -1;
    private final DatePickerValidation mValidation;
    private DateRangePickerView mView;
    @PickerMode
    private int mMode;
    private Context mContext;

    public DateRangePresenterImpl(Context context) {
        mContext = context;
        mValidation = new DatePickerValidation();
    }

    @Override
    public void bindView(DateRangePickerView view) {
        mView = view;
    }

    @Override
    public void initialization(@PickerMode int mode, long beginDate, long endDate, int maxRange) {
        if (mMode == NOT_SELECTED) {
            throw new RuntimeException("mode is required field");
        }
        mMode = mode;
        setSelectedDates(beginDate, endDate);
        setTitle();
        mView.showMonth(mValidation.getMonth(0));
        mValidation.setMaxRange(maxRange);
        mView.initPager(mValidation.getCountMonths());
        updatePage(beginDate);
    }

    private void setSelectedDates(long beginDate, long endDate) {
        if (beginDate == NOT_SELECTED) {
            return;
        }
        switch (mMode) {
            case PickerMode.SELECT_SINGLE_DATE:
                selectSingleDate(beginDate);
                break;
            case PickerMode.SELECT_PERIOD_DATE:
                mValidation.setPeriod(beginDate, endDate);
                mView.showBeginDate(mValidation.getBeginYear(), mValidation.getBeginDate());
                mView.showEndDate(mValidation.getEndYear(), mValidation.getEndDate());
                break;
        }
    }

    private void setTitle() {
        switch (mMode) {
            case PickerMode.SELECT_SINGLE_DATE:
                mView.setTitle(mContext.getString(R.string.date_range_picker_select_date));
                break;
            case PickerMode.SELECT_PERIOD_DATE:
                mView.setTitle(mContext.getString(R.string.date_range_picker_select_range));
                break;
        }
    }

    @Override
    public void createItems(int monthPosition, Consumer<List<PickerModel>> listConsumer) {
        Single.fromCallable(mValidation.getListOfDays(monthPosition))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listConsumer);
    }

    @Override
    public void updateItems(List<PickerModel> list, Action onComplete) {
        Completable.fromAction(mValidation.updateList(list))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }

    @Override
    public void onDateSelected(Long dateInSec) {
        mView.hideImage();
        switch (mMode) {
            case PickerMode.SELECT_SINGLE_DATE:
                selectSingleDate(dateInSec);
                mView.enableOkBtn(true);
                break;
            case PickerMode.SELECT_PERIOD_DATE:
                selectPeriod(dateInSec);
                break;
        }
    }

    private void selectSingleDate(Long dateInSec) {
        mValidation.selectSingleDate(dateInSec);
        mView.showBeginDate(mValidation.getBeginYear(), mValidation.getBeginDate());
        mView.setTitle(mContext.getString(R.string.date_range_picker_selected_date));
    }

    private void selectPeriod(Long dateInSec) {
        mValidation.selectPeriod(dateInSec);
        mView.showBeginDate(mValidation.getBeginYear(), mValidation.getBeginDate());
        mView.showEndDate(mValidation.getEndYear(), mValidation.getEndDate());
        mView.enableOkBtn(mValidation.isEndDateSelected());
        mView.setTitle(mContext.getString(R.string.date_range_picker_selected_range));
    }

    @Override
    public void updateMonth(int monthPosition) {
        mView.showMonth(mValidation.getMonth(monthPosition));
    }

    @Override
    public void handleArrowClick(int position) {
        mView.swipeToPage(position);
        checkArrowBtn(position);
    }

    @Override
    public void onOkBtnClick() {
        switch (mMode) {
            case PickerMode.SELECT_SINGLE_DATE:
                sendResultSingleDate();
                break;
            case PickerMode.SELECT_PERIOD_DATE:
                sendResultPeriod();
                break;
        }
    }

    private void sendResultSingleDate() {
        Long beginDateLong = mValidation.getBeginDateLong();
        mView.sendResultSingleDate(beginDateLong, mValidation.getSingleDateString());
    }

    private void sendResultPeriod() {
        Long beginDateLong = mValidation.getBeginDateLong();
        Long endDateLong = mValidation.getEndDateLong();
        if (endDateLong == null) {
            endDateLong = beginDateLong;
        }
        mView.sendResultPeriod(beginDateLong, endDateLong, mValidation.getStringPeriod());
    }

    @Override
    public void checkArrowBtn(int position) {
        if (position == 0) {
            mView.showNextArrowBtn(false);
        } else {
            mView.showNextArrowBtn(true);
        }
    }

    @Override
    public void onMonthViewClick(int monthPosition) {
        mView.selectMonth(mValidation.getDate(monthPosition), mValidation.getCurrentYear(monthPosition));
    }

    @Override
    public void updatePage(long dateInSec) {
        if (dateInSec == NOT_SELECTED) {
            return;
        }
        int position = mValidation.getPosition(dateInSec);
        mView.showMonth(mValidation.getMonth(position));
        mView.swipeToPage(position);
        checkArrowBtn(position);
    }

    @Override
    public void unbindView() {
        mView = null;
    }
}