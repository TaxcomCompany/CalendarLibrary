package ru.taxcom.mobile.android.calendarlibrary.model;

import android.support.annotation.IntDef;

@IntDef({PickerMode.SELECT_PERIOD_DATE, PickerMode.SELECT_SINGLE_DATE})
public @interface PickerMode {
    int SELECT_SINGLE_DATE = 1;
    int SELECT_PERIOD_DATE = 2;
}
