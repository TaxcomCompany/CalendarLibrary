package ru.taxcom.mobile.android.calendarlibrary.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.PrivateKey;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.R2;
import ru.taxcom.mobile.android.calendarlibrary.adapters.PagerMonthAdapter;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;
import ru.taxcom.mobile.android.calendarlibrary.model.SelectionMode;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.implemenattion.DateSelectionPresenterImpl;
import ru.taxcom.mobile.android.calendarlibrary.presentetion.presener.DatePickerSelectionPresenter;
import ru.taxcom.mobile.android.calendarlibrary.util.customView.ReverseViewPager;

import static ru.taxcom.mobile.android.calendarlibrary.util.DatePickerValidation.NOT_SELECTED;
import static ru.taxcom.mobile.android.calendarlibrary.views.DateRangePickerActivity.TOMORROW_IS_BORDER;


public class DateSelectionFragment extends Fragment implements DatePickerSelectionView {

    private static final String SELECTION_MODE = "mode";
    private static final String CLICKED_DATE = "clicked_month";
    private static final String CLICKED_YEAR = "clicked_year";
    private static final String BEGIN_BORDER_DATE = "begin_border_date";

    @BindView(R2.id.title_picker_selection)
    TextView mTitle;
    @BindView(R2.id.reversePager)
    ViewPager mViewPager;
    @BindView(R2.id.year)
    TextView mCurrentYear;
    @BindView(R2.id.year_switch_container)
    RelativeLayout mYearContainer;
    @BindView(R2.id.next_year)
    ImageView mNextArrow;

    private DatePickerSelectionPresenter mPresenter;

    public static DateSelectionFragment newInstance(@SelectionMode int mode,
                                                    long currentDateInSec,
                                                    int currentYear,
                                                    boolean tomorrowIsBorder,
                                                    long beginBorderDate) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTION_MODE, mode);
        bundle.putLong(CLICKED_DATE, currentDateInSec);
        bundle.putInt(CLICKED_YEAR, currentYear);
        bundle.putBoolean(TOMORROW_IS_BORDER, tomorrowIsBorder);
        bundle.putLong(BEGIN_BORDER_DATE, beginBorderDate);
        DateSelectionFragment fr = new DateSelectionFragment();
        fr.setArguments(bundle);
        return fr;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker_selection, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mPresenter = new DateSelectionPresenterImpl(getActivity());
        mPresenter.bindView(this);
        mPresenter.initialization(
                getArguments().getInt(SELECTION_MODE, -1),
                getArguments().getLong(CLICKED_DATE, -1),
                getArguments().getInt(CLICKED_YEAR, -1),
                getArguments().getBoolean(TOMORROW_IS_BORDER, true),
                getArguments().getLong(BEGIN_BORDER_DATE, NOT_SELECTED));
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void initPager(int pagesCount, int currentPage, PagerMonthAdapter.CreateDataListener createListener) {
        mViewPager.setAdapter(new PagerMonthAdapter(getActivity(), pagesCount, PagerMonthAdapter.SELECT_MONTH_OR_YEAR,
                this::onDateSelected,
                ((position, listener) -> createItems(position, listener, createListener)),
                null));
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(currentPage, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = ((ReverseViewPager) mViewPager).convertPosition(position);
                mPresenter.updateSwitch(position);
                mPresenter.checkArrowBtn(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*ignored*/}

            @Override
            public void onPageScrollStateChanged(int state) {/*ignored*/}
        });
    }

    private void createItems(int position, Consumer<List<PickerModel>> listener,
                             PagerMonthAdapter.CreateDataListener createListener) {
        createListener.create(((ReverseViewPager) mViewPager).convertPosition(position), listener);
    }

    private void onDateSelected(Long date) {
        mPresenter.handleSelection(date);
    }

    @Override
    public void onMonthSelect(Long dateInSec) {
        ((DateRangePickerActivity) getActivity()).onMonthAndYearSelect(dateInSec);
    }

    @Override
    public void onYearSelect(long date, long currentYear) {
        ((DateRangePickerActivity) getActivity()).onYearSelect(date, currentYear);
    }

    @Override
    public void showSwitchText(String currentYear) {
        mCurrentYear.setText(currentYear);
    }

    @Override
    public void swipeToPage(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    @Override
    public void showNextArrowBtn(boolean isVisible) {
        mNextArrow.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick(R2.id.prev_year)
    public void onPrevArrowClick() {
        //позиции идут в обратную сторону справа налево т.к. viewPager перевернут на 180
        mPresenter.handleArrowClick(mViewPager.getCurrentItem() + 1);
    }

    @OnClick(R2.id.next_year)
    public void onNextArrowClick() {
        //позиции идут в обратную сторону справа налево т.к. viewPager перевернут на 180
        mPresenter.handleArrowClick(mViewPager.getCurrentItem() - 1);
    }

    @OnClick(R2.id.year)
    public void onYearClick() {
        mPresenter.handleYearClick(mViewPager.getCurrentItem());
    }

    @Override
    public void selectYear(long date, int currentYear) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, DateSelectionFragment.newInstance(SelectionMode.SELECT_YEAR,
                        date, currentYear,
                        getArguments().getBoolean(TOMORROW_IS_BORDER, true),
                        getArguments().getLong(BEGIN_BORDER_DATE, NOT_SELECTED)))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unbindView();
    }

}