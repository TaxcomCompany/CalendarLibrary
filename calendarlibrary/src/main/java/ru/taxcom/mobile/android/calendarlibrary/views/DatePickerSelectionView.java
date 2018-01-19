package ru.taxcom.mobile.android.calendarlibrary.views;

import ru.taxcom.mobile.android.calendarlibrary.adapters.PagerMonthAdapter;

public interface DatePickerSelectionView {
    void initPager(int pagesCount, int currentPage, PagerMonthAdapter.CreateDataListener createListener);

    void onMonthSelect(Long dateInSec);

    void showSwitchText(String currentYear);

    void swipeToPage(int position);

    void showNextArrowBtn(boolean isVisible);

    void setTitle(String title);

    void onYearSelect(long date, long currentYear);

    void selectYear(long date, int currentYear);
}
