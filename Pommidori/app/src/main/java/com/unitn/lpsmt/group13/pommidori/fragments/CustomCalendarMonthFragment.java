package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.unitn.lpsmt.group13.pommidori.utils.CalendarUtils.monthYearFormatDate;

public class CustomCalendarMonthFragment extends Fragment {

    private View view;
    private TextView monthYearText;
    private Button toWeeklyView, previousMoth, nextMonth;
    private CompactCalendarView compactCalendarView;
    private Database db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.custom_calendar_month_view,container,false);

        initializeLayout();
        setMonthText();
        setButtonListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        compactCalendarView.removeAllEvents();
        addEvents();
    }

    private void initializeLayout() {
        toWeeklyView = view.findViewById(R.id.to_weekly_view_btn);
        previousMoth = view.findViewById(R.id.previous_month_btn_month);
        nextMonth = view.findViewById(R.id.next_month_btn_month);
        monthYearText = view.findViewById(R.id.current_month_calendar_month);

        if (CalendarUtils.selectDate == null)
            CalendarUtils.selectDate = LocalDate.now();

        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.month_calendar_view);
        String[] gironi = {"LUN", "MAR", "MER", "GIO", "VEN", "SAB", "DOM"};
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setDayColumnNames(gironi);

        compactCalendarView.setCurrentDate(java.sql.Date.from(CalendarUtils.selectDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

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
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                CalendarUtils.selectDate = firstDayOfNewMonth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                setMonthText();
            }
        });
    }

    public void addEvents(){
        List<TableSessionProgModel> sessProg = db.getAllProgrammedSessions();
        for(TableSessionProgModel tb : sessProg){
            Event e = new Event(tb.getActivity().getColore(),tb.getOraInizio().getTime(),tb.toString());
            compactCalendarView.addEvent(e);
        }
    }
}
