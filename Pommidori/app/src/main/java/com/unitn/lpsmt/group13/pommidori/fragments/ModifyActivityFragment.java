package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyActivityFragment extends DialogFragment {

    private static final String ACTIVITY_ID_PARAM = "activityIdParameter";
    private int activityIdParameter;

    private Button dateButton, deadlineButton, deleteButton, modifyButton, cancelButton;
    private TextInputLayout nameEdit;
    private DatePickerDialog datePickerDialog;
    private Database db;
    private TableActivityModel selectedActivity;
    private LocalDateTime selectedDate;
    private ModifySessionFragment.UpdateCalendarEventListListener updateCalendarEventListListener;


    int year, month, day, deadlineHour, deadlineMinute;
    String activityName;
    boolean hasSelectedDate, hasSelectedDeadline, nameHasChanged;

    public ModifyActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param activityIdParameter id della activity da modificare
     * @return A new instance of fragment ModifyActivityFragment.
     */
    public static ModifyActivityFragment newInstance(int activityIdParameter) {
        ModifyActivityFragment fragment = new ModifyActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ACTIVITY_ID_PARAM, activityIdParameter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if( context instanceof ModifySessionFragment.UpdateCalendarEventListListener){
            updateCalendarEventListListener = (ModifySessionFragment.UpdateCalendarEventListListener) context;
        }else{
            throw new ClassCastException(context.toString()
                    + " must implement ModifySessionFragment.UpdateEventListListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityIdParameter = getArguments().getInt(ACTIVITY_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Database.getInstance( getContext());
        selectedActivity = db.getActivity( activityIdParameter);
        selectedDate = selectedActivity.getScadenza().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        day = selectedDate.getDayOfMonth();
        month = selectedDate.getMonthValue();
        year = selectedDate.getYear();
        deadlineHour = selectedDate.getHour();
        deadlineMinute = selectedDate.getMinute();
        activityName = selectedActivity.getName();

        hasSelectedDate = false;
        hasSelectedDeadline = false;
        nameHasChanged = false;

        dateButton = view.findViewById(R.id.modify_activity_date);
        deadlineButton = view.findViewById(R.id.modify_activity_deadline);
        deleteButton = view.findViewById(R.id.delete_activity);
        modifyButton = view.findViewById(R.id.confirm_modified_activity);
        cancelButton = view.findViewById(R.id.cancel_modified_activity);
        nameEdit = view.findViewById(R.id.modify_activity_name);

        initDatePicker();
        setEditTextListener();
        setButtonListeners();

    }

    private void setButtonListeners(){
       dateButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               datePickerDialog.show();
           }
       });

       deadlineButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                popTimePiker();
           }
       });

       deleteButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                db.deleteActivity( activityIdParameter);
               updateCalendarEventListListener.updateEventList();
               ModifyActivityFragment.this.dismiss();
           }
       });

       modifyButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(nameHasChanged && !validateName()){
                   return;
               }

                if( hasSelectedDate || hasSelectedDeadline || nameHasChanged){
                    try {
                        String scadenza = selectedDate.getYear() + "-" + selectedDate.getMonthValue() + "-" + selectedDate.getDayOfMonth() + " " + deadlineHour + ":" + deadlineMinute;
                        String name = nameEdit.getEditText().getText().toString();
                        selectedActivity.setScadenza(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(scadenza));
                        selectedActivity.setName(name);

                    }catch (ParseException e){
                        e.printStackTrace();
                    }

                    db.updateActivty( selectedActivity.getId(), selectedActivity);
                    updateCalendarEventListListener.updateEventList();
                }

                ModifyActivityFragment.this.dismiss();
           }
       });

       cancelButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                ModifyActivityFragment.this.dismiss();
           }
       });
    }

    private void setEditTextListener(){
        nameEdit.getEditText().addTextChangedListener(editText);
    }

    private TextWatcher editText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String name = nameEdit.getEditText().getText().toString();
            nameHasChanged = true;

            if(!name.isEmpty())
                nameEdit.setError(null);
        }
    };

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

    private void popTimePiker(){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time = "";
                deadlineHour = selectedHour;
                deadlineMinute = selectedMinute;
                hasSelectedDeadline = true;
                time = selectedHour+":"+(selectedMinute<10?"0"+selectedMinute:selectedMinute);
                deadlineButton.setText(time);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, deadlineHour, deadlineMinute, true);

        timePickerDialog.show();
    }

    private boolean validateName(){
        String name = nameEdit.getEditText().getText().toString().trim();

        if(name.isEmpty()){
            nameEdit.setError("Campo obbligatorio");
            return  false;
        }else{
            nameEdit.setError(null);
            return true;
        }
    }

}



















