package com.codepath.nytsearch.fragments;

import com.codepath.nytsearch.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DatePickerDialogFragment extends DialogFragment {

    @BindView(R.id.dpBeginDate) DatePicker dpBeginDate;
    Unbinder unbinder;

    public interface DatePickerDialogListener {
        void onDatePicked(Calendar calendar);
    }

    public DatePickerDialogFragment() {}

    public static DatePickerDialogFragment newInstance(String title) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        Calendar calendar = Calendar.getInstance();
        dpBeginDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                sendBackResult();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed

        int year = dpBeginDate.getYear();
        int month = dpBeginDate.getMonth();
        int day = dpBeginDate.getDayOfMonth();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month, day);

        DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
        listener.onDatePicked(calendar);
        dismiss();
    }

}
