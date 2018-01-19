package ru.taxcom.mobile.android.calendarlibrary.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.functions.Action;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;

public class DatePickerValidation {

    private static final String MONTH_FORMAT = "LLLL yyyy";
    private static final String PERIOD_FORMAT = "EE, LLL dd";
    private static final String YEAR_FORMAT = "yyyy";
    public static final int MAX_RANGE_NOT_SELECTED = -1;

    private final int mCountMonthsEpoch;
    @Nullable
    private Long mBeginDateInSec;
    @Nullable
    private Long mEndDateInSec;
    @Nullable
    private Long mBeginDisabledDate;
    @Nullable
    private Long mEndDisabledDate;
    private Long mDateSinceTomorrow;
    private int mMaxRange;

    public DatePickerValidation() {
        mCountMonthsEpoch = calcCountMonths();
        mDateSinceTomorrow = calcTomorrowDate();
    }

    public void setMaxRange(int maxRange) {
        mMaxRange = maxRange;
    }

    public String getMonth(int monthPosition) {
        Calendar calendar = getCalendarWithMonthOffset(monthPosition);
        String month = format(calendar.getTimeInMillis() / 1000, MONTH_FORMAT);
        return month.substring(0, 1).toUpperCase() + month.substring(1);
    }

    public String getBeginYear() {
        return getYear(mBeginDateInSec);
    }

    public String getEndYear() {
        return getYear(mEndDateInSec);
    }

    public String getBeginDate() {
        return getPeriodDate(mBeginDateInSec);
    }

    public String getEndDate() {
        return getPeriodDate(mEndDateInSec);
    }

    private String getYear(Long dateInSec) {
        if (dateInSec == null) {
            return null;
        }
        return format(dateInSec, YEAR_FORMAT);
    }

    private String getPeriodDate(Long dateInSec) {
        if (dateInSec == null) {
            return null;
        }
        String date = format(dateInSec, PERIOD_FORMAT);

        String[] arrayDate = date.split(",");
        String part1 = arrayDate[0].substring(0, 1).toUpperCase().concat(arrayDate[0].substring(1));
        String part2 = arrayDate[1].substring(1, 2).toUpperCase().concat(arrayDate[1].substring(2));
        return part1.concat(", ").concat(part2);
    }

    public int getCountMonths() {
        return mCountMonthsEpoch;
    }

    public void selectSingleDate(Long dateInSec) {
        mBeginDateInSec = dateInSec;
    }

    public void selectPeriod(Long dateInSec) {
        if (mBeginDateInSec == null) {
            mBeginDateInSec = dateInSec;
            calcPossiblePeriod();
        } else if (mEndDateInSec == null) {
            if (dateInSec > mBeginDateInSec) {
                mEndDateInSec = dateInSec;
            } else if (dateInSec < mBeginDateInSec) {
                mEndDateInSec = mBeginDateInSec;
                mBeginDateInSec = dateInSec;
            } else if (dateInSec.equals(mBeginDateInSec)) {
                mEndDateInSec = dateInSec;
            }
            mBeginDisabledDate = null;
            mEndDisabledDate = mDateSinceTomorrow;
        } else {
            mBeginDateInSec = dateInSec;
            mEndDateInSec = null;
            calcPossiblePeriod();
        }
    }

    private void calcPossiblePeriod() {
        if (mMaxRange != MAX_RANGE_NOT_SELECTED) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(mBeginDateInSec * 1000));
            calendar.add(Calendar.DAY_OF_MONTH, -mMaxRange);
            mBeginDisabledDate = calendar.getTimeInMillis() / 1000;
            calendar.add(Calendar.DAY_OF_MONTH, mMaxRange * 2);
            long endDisabled = calendar.getTimeInMillis() / 1000;
            if (endDisabled <= mDateSinceTomorrow) {
                mEndDisabledDate = endDisabled;
            } else {
                mEndDisabledDate = mDateSinceTomorrow;
            }
        }
    }

    public Callable<List<PickerModel>> getListOfDays(int monthPosition) {
        return () -> {
            List<PickerModel> list = new ArrayList<>();
            Calendar calendar = getCalendarWithMonthOffset(monthPosition);
            int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 1; i <= dayCount; i++) {
                addItem(list, calendar, i);
            }
            applyOffset(list, calendar.getTime());
            return list;
        };
    }

    @NonNull
    private Calendar getCalendarWithMonthOffset(int monthPosition) {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.MONTH, -monthPosition);
        return calendar;
    }

    private void addItem(List<PickerModel> list, Calendar calendar, int i) {
        Long dateInSec = getDateWithDay(calendar, i);
        if (isActiveDate(dateInSec)) {
            if (isSelectedDay(dateInSec)) {
                list.add(new PickerModel(R.color.white, dateInSec, R.drawable.selected_day_circle_shape, String.valueOf(i)));
            } else {
                list.add(new PickerModel(R.color.black, dateInSec, android.R.color.transparent, String.valueOf(i)));
            }
        } else {
            list.add(new PickerModel(android.R.color.darker_gray, dateInSec, android.R.color.transparent, String.valueOf(i), true));
        }
    }

    public Action updateList(List<PickerModel> oldList) {
        return () -> {
            for (PickerModel m : oldList) {
                if (m == null) {
                    continue;
                }
                Long date = m.getDate();
                if (isActiveDate(date)) {
                    m.setDisabled(false);
                    if (isSelectedDay(date)) {
                        m.setTextColor(R.color.white);
                        m.setBackgroundColor(R.drawable.selected_day_circle_shape);
                    } else {
                        m.setTextColor(R.color.black);
                        m.setBackgroundColor(android.R.color.transparent);
                    }
                } else {
                    m.setDisabled(true);
                    m.setTextColor(android.R.color.darker_gray);
                    m.setBackgroundColor(android.R.color.transparent);
                }
            }
        };
    }

    private void applyOffset(List<PickerModel> list, Date date) {
        int offsetDay = getOffsetDay(date);
        // начинаем с пн
        for (int i = 0; i < offsetDay; i++) {
            list.add(i, null);
        }
    }

    @NonNull
    private Calendar getCurrentCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(StringUtil.getUtcNoTime(new Date()));
        return calendar;
    }

    private boolean isActiveDate(Long dateInSec) {
        if (mEndDisabledDate == null) {
            mEndDisabledDate = mDateSinceTomorrow;
        }
        if (dateInSec >= mEndDisabledDate) {
            return false;
        } else if (mBeginDisabledDate != null && dateInSec <= mBeginDisabledDate) {
            return false;
        }
        return true;
    }

    private Long calcTomorrowDate() {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis() / 1000;
    }

    private boolean isSelectedDay(Long dateInSec) {
        if (mBeginDateInSec == null) {
            return false;
        } else if (mEndDateInSec == null) {
            return dateInSec.equals(mBeginDateInSec);
        }
        return dateInSec >= mBeginDateInSec && dateInSec <= mEndDateInSec;
    }

    private Long getDateWithDay(Calendar calendar, int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis() / 1000;
    }

    private int getOffsetDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //1-7 от Вс до Сб
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayWeek != 1) {
            return dayWeek - 2;
        } else {
            return 6; //вс
        }
    }

    private int calcCountMonths() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(0));
        Calendar endCalendar = getCurrentCalendar();

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return diffYear * 12 + endCalendar.get(Calendar.MONTH) + 1;
    }

    private String format(Long dateInSeconds, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(dateInSeconds * 1000);
    }

    public Long getBeginDateLong() {
        return mBeginDateInSec;
    }

    public Long getEndDateLong() {
        return mEndDateInSec;
    }

    public String getStringPeriod() {
        String begin = format(mBeginDateInSec, "dd.MM.yyyy");
        if (mEndDateInSec != null) {
            return begin.concat(" - ").concat(format(mEndDateInSec, "dd.MM.yyyy"));
        }
        return begin.concat(" - ").concat(begin);

    }

    public String getSingleDateString() {
        return format(mBeginDateInSec, "dd.MM.yyyy");
    }

    public void setPeriod(long beginDate, long endDate) {
        mBeginDateInSec = beginDate;
        mEndDateInSec = endDate;
    }

    public boolean isEndDateSelected() {
        return mEndDateInSec != null;
    }

    public long getDate(int monthPosition) {
        Calendar calendar = getCalendarWithMonthOffset(monthPosition);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis() / 1000;
    }

    public int getPosition(long dateInSec) {
        Calendar currentCalendar = getCurrentCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInSec * 1000);
        int monthsBetweenYear = (currentCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)) * 12;
        int diffMonth = calendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH);
        return monthsBetweenYear - diffMonth;
    }

    public int getCurrentYear(int monthPosition) {
        Calendar calendar = getCalendarWithMonthOffset(monthPosition);
        return calendar.get(Calendar.YEAR);
    }
}