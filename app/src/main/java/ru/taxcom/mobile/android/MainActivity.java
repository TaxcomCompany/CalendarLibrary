package ru.taxcom.mobile.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerMode;
import ru.taxcom.mobile.android.calendarlibrary.util.StringUtil;
import ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity;

import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.BEGIN_DATE_PICKER;
import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.DATE_TEXT;
import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.END_DATE_PICKER;
import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.REQUEST_CODE_PICKER;
import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.SINGLE_DATE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.period_date)
    public void onPeriodClick() {
        DateRangePickerActivity.start(this, PickerMode.SELECT_PERIOD_DATE, null, null, 6, true);
    }

    @OnClick(R.id.single_date)
    public void onSingleClick() {
        DateRangePickerActivity.start(this, PickerMode.SELECT_SINGLE_DATE, null,
                null, null, true);
    }

    @OnClick(R.id.single_date2070)
    public void onSingle2070Click() {
        DateRangePickerActivity.start(this, PickerMode.SELECT_SINGLE_DATE,
                StringUtil.getUtcNoTime(new Date()).getTime() / 1000,
                null,
                null, false);
    }

    @OnClick(R.id.period_date2070)
    public void onPeriod2070Click() {
        DateRangePickerActivity.start(this, PickerMode.SELECT_PERIOD_DATE,
                StringUtil.getUtcNoTime(getDate1()).getTime() / 1000,
                StringUtil.getUtcNoTime(getDate2()).getTime() / 1000,
                null, false);
    }

    private Date getDate1() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 30);
        return instance.getTime();
    }

    private Date getDate2() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 60);
        return instance.getTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK) {
            Long begin = data.getLongExtra(BEGIN_DATE_PICKER, -1);
            Long end = data.getLongExtra(END_DATE_PICKER, -1);
            Long single = data.getLongExtra(SINGLE_DATE, -1);
            String text = data.getStringExtra(DATE_TEXT);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }
}
