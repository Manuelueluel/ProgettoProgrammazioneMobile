package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.unitn.lpsmt.group13.pommidori.CalendarEvent;
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.utils.CalendarEventAdapter;
import com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils.monthYearFormatDate;

public class CustomCalendarMonthFragment extends Fragment {

    private TextView monthYearText;
    private Button toWeeklyView, previousMoth, nextMonth;
    private CompactCalendarView compactCalendarView;
    private Database db;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_calendar_month_view,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeLayout( view);
        setMonthText();
        setButtonListeners();

    }

    @Override
    public void onResume() {
        super.onResume();
        compactCalendarView.removeAllEvents();
        showEventsToCalendar();

        ZoneId zoneId = ZoneId.systemDefault();
        ZoneOffset zoneOffset = zoneId.getRules().getOffset( LocalDateTime.now());
        loadCalendarEventList( new Date( LocalDateTime.now().toInstant( zoneOffset).toEpochMilli()));
    }

    private void initializeLayout( View view) {
        toWeeklyView = view.findViewById(R.id.to_weekly_view_btn);
        previousMoth = view.findViewById(R.id.previous_month_btn_month);
        nextMonth = view.findViewById(R.id.next_month_btn_month);
        monthYearText = view.findViewById(R.id.current_month_calendar_month);

        if (CalendarUtils.selectDate == null)
            CalendarUtils.selectDate = LocalDate.now();

        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.month_calendar_view);
        compactCalendarView.setLocale(TimeZone.getTimeZone(ZoneId.systemDefault()), Locale.getDefault());
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setCurrentDate(java.sql.Date.from(CalendarUtils.selectDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        fragmentManager = getParentFragmentManager();
        recyclerView = view.findViewById(R.id.recyclerview_calendar_events);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager( layoutManager);

        db = Database.getInstance( getContext());
    }

    private void setMonthText() {
        monthYearText.setText(monthYearFormatDate(CalendarUtils.selectDate));
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
                CalendarUtils.selectDate = CalendarUtils.selectDate.minusMonths(1);
                compactCalendarView.scrollLeft();
                setMonthText();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectDate = CalendarUtils.selectDate.plusMonths(1);
                compactCalendarView.scrollRight();
                setMonthText();
            }
        });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                CalendarUtils.selectDate = dateClicked.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                loadCalendarEventList( dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                CalendarUtils.selectDate = firstDayOfNewMonth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                setMonthText();
            }
        });
    }

    public void showEventsToCalendar(){
        List<TableSessionProgModel> sessProg = db.getAllProgrammedSessions();
        List<TableActivityModel> activity = db.getAllActivities();

        for(TableSessionProgModel tb : sessProg){
            CalendarEvent ce = new CalendarEvent(
                    tb.getId(),
                    tb.getActivity().getName(),
                    tb.getActivity().getColore(),
                    tb.getOraInizio().getTime(),
                    tb.getOraFine().getTime(),
                    CalendarEvent.TYPE_SESSION);

            Event event = new Event(ce.getColor(), ce.getStartTimeInMillis(), ce);

            compactCalendarView.addEvent(event);
        }

        for(TableActivityModel tb : activity){
            CalendarEvent ce = new CalendarEvent(
                    tb.getId(),
                    tb.getName(),
                    tb.getColore(),
                    tb.getScadenza().getTime(),
                    0,
                    CalendarEvent.TYPE_ACTIVITY);

            Event event = new Event(ce.getColor(), ce.getStartTimeInMillis(), ce);

            compactCalendarView.addEvent(event);
        }
    }

    public void loadCalendarEventList(Date dateSelected){
        List<Event> events = compactCalendarView.getEvents( dateSelected);
        adapter = new CalendarEventAdapter((ArrayList<Event>) events, fragmentManager);
        recyclerView.setAdapter( adapter);
    }

    public void clearCalendar(){
        compactCalendarView.removeAllEvents();
    }
}
