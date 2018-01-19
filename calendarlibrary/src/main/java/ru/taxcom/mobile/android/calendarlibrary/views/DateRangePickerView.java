package ru.taxcom.mobile.android.calendarlibrary.views;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface DateRangePickerView {
    void showMonth(String currentMonth);

    void initPager(int countMonths);

    void swipeToPage(int position);

    void showNextArrowBtn(boolean isVisible);

    void showBeginDate(@Nullable String beginYear, @Nullable String beginDate);

    void showEndDate(@Nullable String endYear, @Nullable String endDate);

    void setTitle(String title);

    void sendResultPeriod(@NonNull Long beginDateLong, @NonNull Long endDateLong, @NonNull String stringPeriod);

    void sendResultSingleDate(@NonNull Long beginDateLong, @NonNull String singleDateString);

    void enableOkBtn(boolean isEnable);

    void selectMonth(long date, int currentYear);

    void hideImage();
}
