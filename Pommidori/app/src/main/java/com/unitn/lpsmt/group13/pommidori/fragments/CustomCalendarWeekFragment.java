package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unitn.lpsmt.group13.pommidori.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CustomCalendarWeekFragment extends Fragment {

    private View view;

    private Button toMonthlyyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.custom_calendar_week_view,container,false);

        initializeLayout();
        setButtonListeners();

        return view;
    }

    private void initializeLayout() {
        toMonthlyyView = view.findViewById(R.id.to_monthly_view_btn);
    }

    private void setButtonListeners() {

        toMonthlyyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment();
            }
        });
    }

    public void setFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calendar_fragment, new CustomCalendarMonthFragment());
        fragmentTransaction.commit();
    }
}
