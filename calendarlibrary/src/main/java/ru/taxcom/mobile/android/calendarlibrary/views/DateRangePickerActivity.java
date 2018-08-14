package ru.taxcom.mobile.android.calendarlibrary.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.R2;
import ru.taxcom.mobile.android.calendarlibrary.adapters.PagerMonthAdapter;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerMode;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.implemenattion.DateRangePresenterImpl;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.presener.DateRangePresenter;
import ru.taxcom.mobile.android.calendarlibrary.util.customView.ReverseViewPager;

import static ru.taxcom.mobile.android.calendarlibrary.util.DatePickerValidation.MAX_RANGE_NOT_SELECTED;
import static ru.taxcom.mobile.android.calendarlibrary.util.DatePickerValidation.NOT_SELECTED;

public class DateRangePickerActivity extends AppCompatActivity implements DateRangePickerView {

    public static final String MAX_RANGE = "range";
    public static final int REQUEST_CODE_PICKER = 432;
    public static final String BEGIN_DATE_PICKER = "begin_date_picker";
    public static final String END_DATE_PICKER = "end_date_picker";
    public static final String DATE_TEXT = "date_text";
    public static final String MODE = "mode";
    public static final String SINGLE_DATE = "single_date";
    public static final String TOMORROW_IS_BORDER = "border_to_select";
    public static final String BEGIN_BORDER_DATE = "begin_border_date";

    @BindView(R2.id.title_select_date)
    TextView mTitle;
    @BindView(R2.id.calendar_image)
    ImageView mCalendarImage;
    @BindView(R2.id.reverseViewPager)
    ViewPager mViewPager;
    @BindView(R2.id.month)
    TextView mMonth;
    @BindView(R2.id.next_month)
    ImageView mNextArrow;
    @BindView(R2.id.days_of_week)
    LinearLayout mDaysOfWeek;
    @BindView(R2.id.beginYear)
    TextView mBeginYear;
    @BindView(R2.id.endYear)
    TextView mEndYear;
    @BindView(R2.id.beginDate)
    TextView mBeginDate;
    @BindView(R2.id.endDate)
    TextView mEndDate;
    @BindView(R2.id.ok_btn)
    TextView mOkBtn;

    private DateRangePresenter mPresenter;

    /**
     * @param activity          -
     * @param mode              -
     * @param beginSelectedDate -
     * @param endSelectedDate   -
     * @param maxRange          - максимально возможный период
     * @param tomorrowIsBorder  - возможность выбора будущих дат
     * @param beginBorderDate   - нижняя граница для выбора даты. Использовать этот метод {@link ru.taxcom.mobile.android.calendarlibrary.util.StringUtil#getCalendarUtcNoTime(Date)}
     */
    public static void start(Activity activity,
                             @PickerMode int mode,
                             @Nullable Long beginSelectedDate,
                             @Nullable Long endSelectedDate,
                             @Nullable Integer maxRange,
                             boolean tomorrowIsBorder,
                             @Nullable Long beginBorderDate) {
        Intent intent = new Intent(activity, DateRangePickerActivity.class);
        intent.putExtra(MAX_RANGE, maxRange);
        intent.putExtra(MODE, mode);
        intent.putExtra(BEGIN_DATE_PICKER, beginSelectedDate);
        intent.putExtra(END_DATE_PICKER, endSelectedDate);
        intent.putExtra(TOMORROW_IS_BORDER, tomorrowIsBorder);
        intent.putExtra(BEGIN_BORDER_DATE, beginBorderDate);
        activity.startActivityForResult(intent, REQUEST_CODE_PICKER);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mPresenter = new DateRangePresenterImpl(this);
        mPresenter.bindView(this);
        mPresenter.initialization(getIntent().getIntExtra(MODE, NOT_SELECTED),
                getIntent().getLongExtra(BEGIN_DATE_PICKER, NOT_SELECTED),
                getIntent().getLongExtra(END_DATE_PICKER, NOT_SELECTED),
                getIntent().getIntExtra(MAX_RANGE, MAX_RANGE_NOT_SELECTED),
                getIntent().getBooleanExtra(TOMORROW_IS_BORDER, true),
                getIntent().getLongExtra(BEGIN_BORDER_DATE, NOT_SELECTED));
        initDaysOfWeek();
    }

    @Override
    public void initPager(int countMonths) {
        mViewPager.setAdapter(new PagerMonthAdapter(this, countMonths, PagerMonthAdapter.SELECT_PERIOD,
                mPresenter::onDateSelected, this::createItems, mPresenter::updateItems));
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = ((ReverseViewPager) mViewPager).convertPosition(position);
                mPresenter.updateMonth(position);
                mPresenter.checkArrowBtn(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*ignored*/}

            @Override
            public void onPageScrollStateChanged(int state) {/*ignored*/}
        });
    }

    private void createItems(int position, Consumer<List<PickerModel>> listener) {
        mPresenter.createItems(((ReverseViewPager) mViewPager).convertPosition(position), listener);
    }

    private void initDaysOfWeek() {
        String[] daysArrays = getResources().getStringArray(R.array.date_range_picker_days);
        for (String day : daysArrays) {
            TextView textViewDay = (TextView) getLayoutInflater().inflate(R.layout.item_day_of_week, mDaysOfWeek, false);
            textViewDay.setText(day);
            mDaysOfWeek.addView(textViewDay);
        }
    }

    @Override
    public void showMonth(String currentMonth) {
        mMonth.setText(currentMonth);
    }

    @OnClick(R2.id.prev_month)
    public void onPrevArrowClick() {
        mPresenter.handleArrowClick(mViewPager.getCurrentItem() + 1);
    }

    @OnClick(R2.id.next_month)
    public void onNextArrowClick() {
        //позиции идут в обратную сторону справа налево т.к. viewPager перевернут на 180
        mPresenter.handleArrowClick(mViewPager.getCurrentItem() - 1);
    }

    @Override
    public void swipeToPage(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    @Override
    public void showNextArrowBtn(boolean isVisible) {
        mNextArrow.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showBeginDate(@Nullable String beginYear, @Nullable String beginDate) {
        mBeginDate.setText(beginDate);
        mBeginYear.setText(beginYear);
    }

    @Override
    public void showEndDate(@Nullable String endYear, @Nullable String endDate) {
        mEndDate.setText(endDate);
        mEndYear.setText(endYear);
    }

    @OnClick(R2.id.ok_btn)
    public void okBtnClick() {
        mPresenter.onOkBtnClick();
    }

    @OnClick(R2.id.cancel_btn)
    public void cancelBtnClick() {
        finish();
    }

    @Override
    public void sendResultPeriod(@NonNull Long beginDateLong, @NonNull Long endDateLong, @NonNull String stringPeriod) {
        Intent intent = new Intent();
        intent.putExtra(BEGIN_DATE_PICKER, beginDateLong);
        intent.putExtra(END_DATE_PICKER, endDateLong);
        intent.putExtra(DATE_TEXT, stringPeriod);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void sendResultSingleDate(@NonNull Long date, @NonNull String dateText) {
        Intent intent = new Intent();
        intent.putExtra(SINGLE_DATE, date);
        intent.putExtra(DATE_TEXT, dateText);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void enableOkBtn(boolean isEnable) {
        mOkBtn.setEnabled(isEnable);
        mOkBtn.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.colorPrimary : android.R.color.darker_gray));
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @OnClick(R2.id.month)
    public void onMonthClick() {
        mPresenter.onMonthViewClick(mViewPager.getCurrentItem());
    }

    @Override
    public void selectMonth(long date, int currentYear) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, DateSelectionFragment.newInstance(SelectionMode.SELECT_MONTH, date, currentYear,
                        getIntent().getBooleanExtra(TOMORROW_IS_BORDER, true)))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void hideImage() {
        if (mCalendarImage.getVisibility() != View.GONE) {
            mCalendarImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onMonthAndYearSelect(long dateInSec) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        new Handler().post(() -> mPresenter.updatePage(dateInSec));
    }

    public void onYearSelect(long date, long currentYear) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        selectMonth(date, (int) currentYear);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindView();
    }
}