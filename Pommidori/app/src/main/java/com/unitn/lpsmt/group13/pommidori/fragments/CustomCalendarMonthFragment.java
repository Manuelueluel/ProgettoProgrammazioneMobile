package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.adapter.CalendarAdapter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomCalendarMonthFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private View view;
    private TextView monthYearText;
    private Button toWeeklyView, previousMoth, nextMonth;
    private RecyclerView calendarRecycleView;
    private LocalDate selectDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.custom_calendar_month_view,container,false);

        initializeLayout();
        setMonthView();
        setButtonListeners();

        return view;
    }

    private void initializeLayout() {
        toWeeklyView = view.findViewById(R.id.to_weekly_view_btn);
        previousMoth = view.findViewById(R.id.previous_month_btn_month);
        nextMonth = view.findViewById(R.id.next_month_btn_month);
        calendarRecycleView = view.findViewById(R.id.month_calendar_view);
        monthYearText = view.findViewById(R.id.current_month_calendar_month);
        selectDate = LocalDate.now();
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFormatDate(selectDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext().getApplicationContext(), 7);
        calendarRecycleView.setLayoutManager(layoutManager);
        calendarRecycleView.setAdapter(calendarAdapter);
    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth  = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i=2; i<=42; i++){
            if(i<=dayOfWeek || i>daysInMonth+dayOfWeek){
                daysInMonthArray.add(null);
            }else{
                daysInMonthArray.add(LocalDate.of(selectDate.getYear(),selectDate.getMonth(),i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFormatDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALY);
        return date.format(formatter);
    }

    private void setButtonListeners() {
        toWeeklyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.calendar_fragment, new CustomCalendarWeekFragment());
                fragmentTransaction.commit();
            }
        });
        previousMoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate = selectDate.minusMonths(1);
                setMonthView();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate = selectDate.plusMonths(1);
                setMonthView();
            }
        });
    }

    @Override
    public void OnItemClick(int position, String dayText) {
        // on press a day in calendar
    }
}
