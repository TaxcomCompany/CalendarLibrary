package ru.taxcom.mobile.android.calendarlibrary.presentetion.implemenattion;

import android.content.Context;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.presener.DatePickerSelectionPresenter;
import ru.taxcom.mobile.android.calendarlibrary.util.DateSelectionValidation;
import ru.taxcom.mobile.android.calendarlibrary.views.DatePickerSelectionView;


public class DateSelectionPresenterImpl implements DatePickerSelectionPresenter {
    private final Context mContext;
    private final DateSelectionValidation mValidation;
    private DatePickerSelectionView mView;
    private int mMode;

    public DateSelectionPresenterImpl(Context context) {
        mContext = context;
        mValidation = new DateSelectionValidation();
    }

    @Override
    public void bindView(DatePickerSelectionView view) {
        mView = view;
    }

    @Override
    public void initialization(@SelectionMode int mode, long clickedDate, int currentYear, boolean tomorrowIsBorder) {
        mMode = mode;
        int currentPosition;
        mValidation.setTomorrowIsBorder(tomorrowIsBorder);
        switch (mode) {
            case SelectionMode.SELECT_MONTH:
                mValidation.calculateCountYear();
                mValidation.calcNextMonth();
                currentPosition = mValidation.getCurrentMonthPosition(clickedDate, currentYear);
                mView.initPager(mValidation.getPagesCount(), currentPosition, this::createMonths);
                mView.setTitle(mContext.getString(R.string.date_range_picker_select_month));
                break;
            case SelectionMode.SELECT_YEAR:
            default:
                mValidation.calculateCountDecades();
                mValidation.calcNextYear();
                currentPosition = mValidation.getCurrentYearPosition(clickedDate, currentYear);
                mView.initPager(mValidation.getPagesCount(), currentPosition, this::createYears);
                mView.setTitle(mContext.getString(R.string.date_range_picker_select_year));
                break;
        }
        updateSwitch(currentPosition);
        checkArrowBtn(currentPosition);
    }

    @Override
    public void createMonths(int pagePosition, Consumer<List<PickerModel>> listConsumer) {
        Single.fromCallable(mValidation.getListOfMonth(pagePosition))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listConsumer);
    }

    @Override
    public void createYears(int pagePosition, Consumer<List<PickerModel>> listConsumer) {
        Single.fromCallable(mValidation.getListOfYears(pagePosition))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listConsumer);
    }

    @Override
    public void updateSwitch(int pagePosition) {
        switch (mMode) {
            case SelectionMode.SELECT_MONTH:
                mView.showSwitchText(mValidation.getYear(pagePosition));
                break;
            case SelectionMode.SELECT_YEAR:
                mView.showSwitchText(mValidation.getDecide(pagePosition));
                break;
        }
    }

    @Override
    public void handleArrowClick(int position) {
        mView.swipeToPage(position);
        checkArrowBtn(position);
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
    public void handleSelection(long date) {
        switch (mMode) {
            case SelectionMode.SELECT_MONTH:
                mView.onMonthSelect(date);
                break;
            case SelectionMode.SELECT_YEAR:
                mView.onYearSelect(mValidation.getSelectedDate(), date);
                break;
        }
    }

    @Override
    public void handleYearClick(int pagePosition) {
        switch (mMode) {
            case SelectionMode.SELECT_MONTH:
                mView.selectYear(mValidation.getSelectedDate(), mValidation.getYearForPosition(pagePosition));
                break;
            case SelectionMode.SELECT_YEAR:
                break;
        }
    }

    @Override
    public void unbindView() {
        mView = null;
    }
}