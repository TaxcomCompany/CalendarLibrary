package ru.taxcom.mobile.android.calendarlibrary.presentetion.presener;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerMode;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerView;

public interface DateRangePresenter {
    void initialization(@PickerMode int mode, long beginDate, long endDate, int maxRange, boolean tomorrowIsBorder, long beginBorderDate);

    void bindView(DateRangePickerView view);

    void checkArrowBtn(int position);

    void unbindView();

    void createItems(int monthPosition, Consumer<List<PickerModel>> listConsumer);

    void updateItems(List<PickerModel> list, Action onComplete);

    void onDateSelected(Long dateInSec);

    void updateMonth(int monthPosition);

    void handleArrowClick(int position);

    void onOkBtnClick();

    void onMonthViewClick(int monthPosition);

    void updatePage(long dateInSec);
}