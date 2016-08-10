package com.codepath.nytsearch.fragments;

import com.codepath.nytsearch.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsDialogFragment extends DialogFragment implements DatePickerDialogFragment.DatePickerDialogListener {

    @BindView(R.id.etBeginDate) EditText etBeginDate;
    private Unbinder unbinder;

    public SettingsDialogFragment() {
        // Required empty public constructor
    }

    public static SettingsDialogFragment newInstance(String title) {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Settings");
        getDialog().setTitle(title);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.etBeginDate)
    public void showDatePickerDialog() {
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setTargetFragment(SettingsDialogFragment.this, 300);
        newFragment.show(getFragmentManager(), "fragment_date_picker");
    }

    // handle the date selected
    @Override
    public void onDatePicked(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(calendar.getTime());
        etBeginDate.setText(sDate);
    }
}
