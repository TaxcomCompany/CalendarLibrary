package ru.taxcom.mobile.android.calendarlibrary.model;

import android.support.annotation.IntDef;

import static ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode.SELECT_MONTH;
import static ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode.SELECT_YEAR;

@IntDef({SELECT_MONTH, SELECT_YEAR})
public @interface SelectionMode {
    int SELECT_MONTH = 1;
    int SELECT_YEAR = 2;
}
