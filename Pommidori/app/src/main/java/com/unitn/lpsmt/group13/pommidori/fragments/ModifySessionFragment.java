package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifySessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifySessionFragment extends DialogFragment {

    private final int START_HOUR_SELECTED = 0;
    private final int END_HOUR_SELECTED = 1;

    private Button deleteButton, modifyButton, cancelButton, dateButton, startHourButton, endHourButton;
    private DatePickerDialog datePickerDialog;
    private Database db;
    private TableSessionProgModel selectedSession;
    private LocalDateTime selectedDate;
    private UpdateCalendarEventListListener updateCalendarEventListListener;

    int year, month, day, startHour, startMinute, endHour, endMinute;
    boolean hasSelectedDate, hasSelectedStartHour, hasSelectedEndHour;

    private static final String PROG_SESSION_ID_PARAM = "programmedSessionIdParameter";
    private int programmedSessionIdParameter;

    public interface UpdateCalendarEventListListener {
        public void updateEventList();
    }

    public ModifySessionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param programmedSessionIdParameter id della sessione da modificare
     * @return A new instance of fragment ModifySessionOrActivityFragment.
     */
    public static ModifySessionFragment newInstance(int programmedSessionIdParameter) {
        ModifySessionFragment fragment = new ModifySessionFragment();
        Bundle arg = new Bundle();
        arg.putInt(PROG_SESSION_ID_PARAM, programmedSessionIdParameter);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if( context instanceof UpdateCalendarEventListListener){
            updateCalendarEventListListener = (UpdateCalendarEventListListener) context;
        }else{
            throw new ClassCastException(context.toString()
                    + " must implement ModifySessionFragment.UpdateEventListListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            programmedSessionIdParameter = getArguments().getInt(PROG_SESSION_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Database.getInstance( getContext());
        selectedSession = db.getProgrammedSession( programmedSessionIdParameter);
        selectedDate = selectedSession.getOraInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        day = selectedDate.getDayOfMonth();
        month = selectedDate.getMonthValue();
        year = selectedDate.getYear();
        startHour = selectedDate.getHour();
        startMinute = selectedDate.getMinute();
        endHour = selectedSession.getOraFine().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getHour();
        endMinute = selectedSession.getOraFine().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getMinute();

        hasSelectedDate = false;
        hasSelectedStartHour = false;
        hasSelectedEndHour = false;

        deleteButton = view.findViewById(R.id.delete_session);
        modifyButton = view.findViewById(R.id.confirm_modified_session);
        cancelButton = view.findViewById(R.id.cancel_modified_session);
        dateButton = view.findViewById(R.id.modify_session_date);
        startHourButton = view.findViewById(R.id.modify_session_start_hour);
        endHourButton = view.findViewById(R.id.modify_session_end_hour);
        dateButton.setText(day+"/"+month+"/"+year);
        startHourButton.setText(startHour+":"+(startMinute<10?"0"+startMinute:startMinute));
        endHourButton.setText(endHour+":"+(endMinute<10?"0"+endMinute:endMinute));

        initDatePicker();
        setButtonListeners();
    }

    private void setButtonListeners() {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        startHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker(START_HOUR_SELECTED);
            }
        });

        endHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker(END_HOUR_SELECTED);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteProgrammedSession( programmedSessionIdParameter);
                updateCalendarEventListListener.updateEventList();
                ModifySessionFragment.this.dismiss();
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( hasSelectedDate || hasSelectedStartHour || hasSelectedEndHour){
                    try {
                        String start = year + "-" + month + "-" + day + " " + startHour + ":" + startMinute;
                        selectedSession.setOraInizio(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(start));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        String end = year + "-" + month + "-" + day + " " + endHour + ":" + endMinute;
                        selectedSession.setOraFine(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    db.updateProgrammedSession( selectedSession.getId(), selectedSession);
                    updateCalendarEventListListener.updateEventList();
                }

                ModifySessionFragment.this.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModifySessionFragment.this.dismiss();
            }
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                year = _year;
                month = _month+1;
                day = _day;
                //testo bottone datePicker
                dateButton.setText(day+"/"+month+"/"+year);
                hasSelectedDate = true;
            }
        };

        Calendar cal = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this.getContext(),dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
    }

    private void popTimePiker(int selected){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time = "";
                switch (selected){
                    case START_HOUR_SELECTED:
                        startHour = selectedHour;
                        startMinute = selectedMinute;
                        hasSelectedStartHour = true;
                        time = startHour+":"+(startMinute<10?"0"+startMinute:startMinute);
                        //testo bottone timePicker
                        startHourButton.setText(time);
                        break;
                    case END_HOUR_SELECTED:
                        endHour = selectedHour;
                        endMinute = selectedMinute;
                        hasSelectedEndHour = true;
                        time = endHour+":"+(endMinute<10?"0"+endMinute:endMinute);
                        //testo bottone timePicker
                        endHourButton.setText(time);
                        break;
                    default:
                        break;
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, startHour, startMinute, true);

        timePickerDialog.show();
    }


}




















