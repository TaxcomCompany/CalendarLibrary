package ru.taxcom.mobile.android.calendarlibrary.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

public class PickerModel {
    @ColorRes
    private int mTextColor;
    @DrawableRes
    private int mBackgroundColor;
    private final String mDateText;
    private final Long mDate;
    private boolean mDisabled;


    public PickerModel(@ColorRes int textColor, Long date, @DrawableRes int backgroundColor, String text) {
        mTextColor = textColor;
        mDate = date;
        mBackgroundColor = backgroundColor;
        mDateText = text;
    }

    public PickerModel(@ColorRes int textColor, Long dateInSec, @DrawableRes int backgroundColor, String text, boolean isDisabled) {
        this(textColor, dateInSec, backgroundColor, text);
        mDisabled = isDisabled;
    }

    public boolean isDisabled() {
        return mDisabled;
    }

    @ColorRes
    public int getTextColor() {
        return mTextColor;
    }

    @DrawableRes
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public String getText() {
        return mDateText;
    }

    public Long getDate() {
        return mDate;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setDisabled(boolean disabled) {
        mDisabled = disabled;
    }
}