package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewSessionFragment extends Fragment {

    private final int START_HOUR_SELECTED = 0;
    private final int END_HOUR_SELECTED = 1;

    private DatePickerDialog datePickerDialog;
    private Button dateButton,hourStartButton,hourEndButton, creaButton, annullaButton;
    private TableActivityModel activityModel;

    private AutoCompleteTextView listaAttivita,listaAvviso,listaRipetizione;
    private Database db;
    private List<TableActivityModel> activity;

    //Dati
    int year, month, day, startHour, startMinute, endHour, endMinute;
    boolean hasSelectedStartHour, hasSelectedEndHour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaAttivita = (AutoCompleteTextView) view.findViewById( R.id.dropdown_activity_new_session);
        listaAvviso = (AutoCompleteTextView) view.findViewById( R.id.dropdown_avviso_new_session);
        listaRipetizione = (AutoCompleteTextView) view.findViewById( R.id.dropdown_ripetizione_new_session);

        dateButton = view.findViewById(R.id.date_session_picker);
        hourStartButton = view.findViewById(R.id.start_hour_picker);
        hourEndButton = view.findViewById(R.id.end_hour_picker);
        creaButton = view.findViewById(R.id.crea_session);
        annullaButton = view.findViewById(R.id.annulla_session);
        dateButton.setText(R.string.date_not_selected);
        hourStartButton.setText(R.string.hour_not_selected);
        hourEndButton.setText(R.string.hour_not_selected);

        day = 0;
        month = 0;
        year = 0;
        startHour = 0;
        startMinute = 0;
        endHour = 0;
        endMinute = 0;

        hasSelectedStartHour = false;
        hasSelectedEndHour = false;

        //Metodi
        initDatePicker();
        setButtonListeners();
        setDropDownLists();
    }

    private void setButtonListeners(){
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        hourStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker(START_HOUR_SELECTED);
            }
        });

        hourEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker(END_HOUR_SELECTED);
            }
        });

        listaAttivita.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for(TableActivityModel t : activity){
                    if(t.getName().equalsIgnoreCase((String)adapterView.getItemAtPosition(i)))
                        activityModel = t;
                }
            }
        });

        creaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableSessionProgModel s = new TableSessionProgModel();
                s.setActivity(activityModel);

                //Controllo selezione campi
                if( year == 0 || month == 0 || day == 0 ){
                    Toast.makeText(getContext(), R.string.select_date_for_programmed_session, Toast.LENGTH_SHORT).show();
                }else if( !hasSelectedStartHour){
                    Toast.makeText(getContext(), R.string.select_start_hour_for_programmed_session, Toast.LENGTH_SHORT).show();
                }else if( !hasSelectedEndHour){
                    Toast.makeText(getContext(), R.string.select_end_hour_for_programmed_session, Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        String start = year + "-" + month + "-" + day + " " + startHour + ":" + startMinute;
                        s.setOraInizio(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).parse(start));
                    } catch (Exception e){
                        return;
                    }

                    try {
                        String end = year + "-" + month + "-" + day + " " + endHour + ":" + endMinute;
                        s.setOraFine(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ITALY).parse(end));
                    } catch (Exception e){
                        return;
                    }

                    if (db.addProgrammedSession(s)) {
                        Toast.makeText(getContext(), R.string.programmed_session_created_successfully, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), R.string.programmed_session_creation_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
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
                        hourStartButton.setText(time);
                        break;
                    case END_HOUR_SELECTED:
                        endHour = selectedHour;
                        endMinute = selectedMinute;
                        hasSelectedEndHour = true;
                        time = endHour+":"+(endMinute<10?"0"+endMinute:endMinute);
                        //testo bottone timePicker
                        hourEndButton.setText(time);
                        break;
                    default:
                        break;
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, startHour, startMinute, true);

        timePickerDialog.show();
    }

    //Attiva o disattiva (enabled) tutte le Views che sono figlie di view, essa stessa compresa
    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    private void setDropDownLists(){
        db = Database.getInstance( getContext());
        activity = db.getAllActivities();
        List<String> activityName = new ArrayList<>();

        if(activity.isEmpty()){
            activity.add(new TableActivityModel());
        }

        for(TableActivityModel a : activity){
            activityName.add(a.getName());
        }

        ArrayAdapter activityAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.dropdown_item,
                activityName
        );
        ArrayAdapter<CharSequence> avvisoAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.avviso,
                R.layout.dropdown_item
        );
        ArrayAdapter<CharSequence> ripetizioneAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.ripetizione,
                R.layout.dropdown_item
        );

        listaAttivita.setText(activityAdapter.getItem(0).toString());
        activityModel = activity.get(0);
        listaAvviso.setText(avvisoAdapter.getItem(0));
        listaRipetizione.setText(ripetizioneAdapter.getItem(0));

        listaAttivita.setAdapter( activityAdapter);
        listaAvviso.setAdapter(avvisoAdapter);
        listaRipetizione.setAdapter(ripetizioneAdapter);


    }
}