package ru.taxcom.mobile.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerMode;
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
        DateRangePickerActivity.start(this, PickerMode.SELECT_PERIOD_DATE, null, null, 6);
    }

    @OnClick(R.id.single_date)
    public void onSingleClick() {
        DateRangePickerActivity.start(this, PickerMode.SELECT_SINGLE_DATE, new Date().getTime() / 1000, null, 6);
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
