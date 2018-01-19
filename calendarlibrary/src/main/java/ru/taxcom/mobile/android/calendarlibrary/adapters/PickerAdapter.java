package ru.taxcom.mobile.android.calendarlibrary.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.taxcom.mobile.android.calendarlibrary.R;
import ru.taxcom.mobile.android.calendarlibrary.R2;
import ru.taxcom.mobile.android.calendarlibrary.model.PickerModel;

public class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.DateHolder> {

    private final PagerMonthAdapter.OnDateSelected mListener;
    private final int mShape;
    private final int mLayout;
    private List<PickerModel> mList;

    public PickerAdapter(PagerMonthAdapter.OnDateSelected listener, int selectedDrawable, int shape) {
        mListener = listener;
        mShape = shape;
        mList = new ArrayList<>();
        mLayout = selectedDrawable;
    }

    @Override
    public DateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new DateHolder(view);
    }

    @Override
    public void onBindViewHolder(DateHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void update(List<PickerModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void update() {
        notifyDataSetChanged();
    }

    public List<PickerModel> getList() {
        return mList;
    }

    class DateHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.date_container)
        LinearLayout mContainer;
        @BindView(R2.id.date_text)
        TextView mDay;

        public DateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(PickerModel pickerModel) {
            if (pickerModel != null) {
                mDay.setText(pickerModel.getText());
                mDay.setTextColor(ContextCompat.getColor(mDay.getContext(), pickerModel.getTextColor()));
                mDay.setBackgroundResource(pickerModel.getBackgroundColor());
                if (!pickerModel.isDisabled()) {

                    mContainer.setOnClickListener((v) -> {
                        if (mShape != 0) {
                            mDay.setBackgroundResource(mShape);
                            mDay.setTextColor(ContextCompat.getColor(mDay.getContext(), R.color.white));
                        }
                        mListener.onDateSelected(pickerModel.getDate());
                    });
                }
            } else {
                mDay.setText(null);
                mDay.setBackground(null);
            }
        }
    }
}