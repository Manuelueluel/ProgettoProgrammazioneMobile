package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.utils.CalendarAdapter;
import com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils;
import com.unitn.lpsmt.group13.pommidori.utils.CustomArrayAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils.daysInWeekArray;
import static com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils.monthYearFormatDate;

public class CustomCalendarWeekFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private TextView monthYearText;
    private Button toMonthlyyView, previousWeek, nextWeek;
    private RecyclerView calendarRecycleView;
    private ListView mondayRecycleView,tusdayRecycleView,wensdayRecycleView,thusdayRecycleView,fridayRecycleView,saturdayRecycleView,sundayRecycleView;
    private ArrayList<ListView> week;
    private ArrayList<TableSessionProgModel> tSPMArray;
    private CustomArrayAdapter adapter;
    private Database db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_calendar_week_view,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeLayout( view);
        setWeekView();
        setButtonListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        clearDayList();
        addEvents();
    }

    private void initializeLayout( View view) {
        toMonthlyyView = view.findViewById(R.id.to_monthly_view_btn);
        previousWeek = view.findViewById(R.id.previous_month_btn_week);
        nextWeek = view.findViewById(R.id.next_month_btn_week);
        calendarRecycleView = view.findViewById(R.id.month_calendar_view);
        monthYearText = view.findViewById(R.id.current_month_calendar_week);

        week = new ArrayList<>();
        tSPMArray = new ArrayList<>();

        mondayRecycleView = view.findViewById(R.id.monday_calendar_view);
        week.add(mondayRecycleView);
        tusdayRecycleView = view.findViewById(R.id.tuesday_calendar_view);
        week.add(tusdayRecycleView);
        wensdayRecycleView = view.findViewById(R.id.wensday_calendar_view);
        week.add(wensdayRecycleView);
        thusdayRecycleView = view.findViewById(R.id.thustay_calendar_view);
        week.add(thusdayRecycleView);
        fridayRecycleView = view.findViewById(R.id.friday_calendar_view);
        week.add(fridayRecycleView);
        saturdayRecycleView = view.findViewById(R.id.saturday_calendar_view);
        week.add(saturdayRecycleView);
        sundayRecycleView = view.findViewById(R.id.sunday_calendar_view);
        week.add(sundayRecycleView);
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFormatDate(CalendarUtils.selectDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext().getApplicationContext(), 7);
        calendarRecycleView.setLayoutManager(layoutManager);
        calendarRecycleView.setAdapter(calendarAdapter);
    }

    private void setButtonListeners() {

        toMonthlyyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.calendar_fragment, new CustomCalendarMonthFragment());
                fragmentTransaction.commit();
            }
        });
        previousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectDate = CalendarUtils.selectDate.minusWeeks(1);
                setWeekView();
                clearDayList();
                addEvents();
            }
        });
        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectDate = CalendarUtils.selectDate.plusWeeks(1);
                setWeekView();
                clearDayList();
                addEvents();
            }
        });
    }

    @Override
    public void OnItemClick(int position, LocalDate date) {
        // on press a day in calendar
        CalendarUtils.selectDate = date;
        setWeekView();
    }

    private void clearDayList() {
        tSPMArray.clear();
    }

    //da ordinare in base all'ora
    private void addEvents() {
        db = Database.getInstance( getContext());
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectDate);
        for(int i=0; i<days.size();i++) {
            tSPMArray = (ArrayList<TableSessionProgModel>) db.getAllProgrammedSessionsByDay(java.sql.Date.valueOf(days.get(i).toString()));
            adapter = new CustomArrayAdapter(getContext(),R.layout.row,tSPMArray);
            week.get(i).setAdapter(adapter);
        }
    }
}
