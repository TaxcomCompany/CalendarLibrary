package ru.taxcom.mobile.android.calendarlibrary.presentetion.presener;


import java.util.List;

import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode;
import ru.taxcom.mobile.android.calendarlibrary.views.DatePickerSelectionView;

public interface DatePickerSelectionPresenter {
    void initialization(@SelectionMode int mode, long currentDate, int currentYear);

    void bindView(DatePickerSelectionView view);

    void checkArrowBtn(int position);

    void createMonths(int pagePosition, Consumer<List<PickerModel>> listConsumer);

    void createYears(int pagePosition, Consumer<List<PickerModel>> listConsumer);

    void updateSwitch(int pagePosition);

    void handleArrowClick(int position);

    void unbindView();

    void handleSelection(long date);

    void handleYearClick(int pagePosition);
}