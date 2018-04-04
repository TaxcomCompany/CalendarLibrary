package ru.taxcom.mobile.android.calendarlibrary.util;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;

public class DateSelectionValidation {

    private int mCountPages;
    @NonNull
    private Long mNextMonthOrYear;
    private long mSelectedDate;
    private int mCurrentYear;
    private boolean mTomorrowIsBorder;

    public void calculateCountYear() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(0));
        Calendar endCalendar = getCurrentCalendar();

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        mCountPages = diffYear + 1;
    }

    public void calculateCountDecades() {
        int years = getCurrentCalendar().get(Calendar.YEAR) - 1970;
        int pages = years / 10;
        if (years % 10 != 0) {
            pages++;
        }
        mCountPages = pages;
    }

    public void calcNextMonth() {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.MONTH, 1);
        mNextMonthOrYear = calendar.getTimeInMillis() / 1000;
    }

    public void calcNextYear() {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.YEAR, 1);
        mNextMonthOrYear = (long) calendar.get(Calendar.YEAR);
    }

    public int getCurrentYearPosition(long currentDate, int currentYear) {
        mSelectedDate = currentDate;
        mCurrentYear = currentYear;

        int nextDecide = getStartDecide(0) + 10;
        int diff = nextDecide - currentYear;
        int pos = diff / 10;
        if (diff % 10 != 0) {
            pos++;
        }
        return pos - 1;
    }

    public int getCurrentMonthPosition(long currentDate, int currentYear) {
        mSelectedDate = currentDate;
        Calendar calendarCurrent = getCurrentCalendar();
        return calendarCurrent.get(Calendar.YEAR) - currentYear;
    }

    public int getPagesCount() {
        return mCountPages;
    }

    public String getYear(int pagePosition) {
        Calendar calendar = getCalendarWithPageOffset(pagePosition);
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public String getDecide(int pagePosition) {
        int yearStart = getStartDecide(pagePosition);
        return String.valueOf(yearStart + " - " + (yearStart + 9));
    }

    public int getYearForPosition(int pagePosition) {
        Calendar currentCalendar = getCalendarWithPageOffset(pagePosition);
        return currentCalendar.get(Calendar.YEAR);
    }

    public Callable<List<PickerModel>> getListOfMonth(int pagePosition) {
        return () -> {
            List<PickerModel> list = new ArrayList<>();
            Calendar calendar = getCalendarWithPageOffset(pagePosition);
            int monthCount = 12;
            for (int i = 0; i < monthCount; i++) {
                Long dateInSec = getDateWithMonth(calendar, i);
                String month = getMonth(calendar);
                if (isActiveMonth(dateInSec)) {
                    if (isSelectedMonth(dateInSec)) {
                        list.add(new PickerModel(R.color.colorPrimary, dateInSec, R.drawable.select_shape, month));
                    } else {
                        list.add(new PickerModel(R.color.colorPrimary, dateInSec, android.R.color.transparent, month));
                    }
                } else {
                    list.add(new PickerModel(android.R.color.darker_gray, dateInSec, android.R.color.transparent, month, true));
                }
            }
            return list;
        };
    }

    public Callable<List<PickerModel>> getListOfYears(int pagePosition) {
        return () -> {
            List<PickerModel> list = new ArrayList<>();
            int decideCount = 12;
            long year = getStartDecide(pagePosition);
            for (int i = 0; i < decideCount; i++) {
                if (isActiveYear(year)) {
                    if (isSelectedYear(year, i)) {
                        list.add(new PickerModel(R.color.colorPrimary, year, R.drawable.select_shape, String.valueOf(year)));
                    } else {
                        list.add(new PickerModel(R.color.colorPrimary, year, android.R.color.transparent, String.valueOf(year)));
                    }
                } else {
                    list.add(new PickerModel(android.R.color.darker_gray, year, android.R.color.transparent, String.valueOf(year), true));
                }
                year++;
            }
            return list;
        };
    }

    private int getStartDecide(int pagePosition) {
        return 1970 + (mCountPages - (pagePosition + 1)) * 10;
    }

    private String getMonth(Calendar calendar) {
        String m = format(calendar, "LLLL");
        return m.substring(0, 1).toUpperCase().concat(m.substring(1));
    }

    private boolean isSelectedYear(long year, int i) {
        return i <= 9 && year == mCurrentYear;
    }

    private boolean isActiveYear(long year) {
        return year < mNextMonthOrYear;
    }

    private boolean isSelectedMonth(Long dateInSec) {
        return dateInSec == mSelectedDate;
    }

    private boolean isActiveMonth(Long dateInSec) {
        return dateInSec < mNextMonthOrYear;
    }

    private Long getDateWithMonth(Calendar calendar, int i) {
        calendar.set(Calendar.MONTH, i);
        return calendar.getTimeInMillis() / 1000;
    }

    private Calendar getCalendarWithPageOffset(int pagePosition) {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.YEAR, -pagePosition);
        return calendar;
    }

    @NonNull
    private Calendar getCurrentCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(StringUtil.getUtcNoTime(new Date()));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // set max date to select
        if (!mTomorrowIsBorder)
            calendar.set(2070, 11, 1);
        return calendar;
    }

    private String format(Calendar calendar, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public long getSelectedDate() {
        return mSelectedDate;
    }

    public void setTomorrowIsBorder(boolean tomorrowIsBorder) {
        mTomorrowIsBorder = tomorrowIsBorder;
    }
}