package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

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

    private DatePickerDialog datePickerDialog;
    private Button dateButton,hourStartButton,hourEndButton, creaButton, annullaButton;
    private CheckBox hourEndCheckBox;
    private TableActivityModel activityModel;

    AutoCompleteTextView listaAttivita,listaAvviso,listaRipetizione;
    Database db;
    List<TableActivityModel> activity;
    View view;
    LinearLayout layoutEndSession;

    //Dati
    int year, month, day, startHour, startMinute, endHour, endMinute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_session, container, false);

        listaAttivita = (AutoCompleteTextView) view.findViewById( R.id.dropdown_activity_new_session);
        listaAvviso = (AutoCompleteTextView) view.findViewById( R.id.dropdown_avviso_new_session);
        listaRipetizione = (AutoCompleteTextView) view.findViewById( R.id.dropdown_ripetizione_new_session);

        dateButton = view.findViewById(R.id.date_session_picker);
        dateButton.setText(getTodayDate());
        hourStartButton = view.findViewById(R.id.start_hour_picker);
        hourEndButton = view.findViewById(R.id.end_hour_picker);
        hourStartButton.setText(getNowHour());
        hourEndButton.setText(getNowHour());

        layoutEndSession = view.findViewById(R.id.layout_end_session);
        hourEndCheckBox = view.findViewById(R.id.checkbox_end_hour);
        hourEndCheckBox.setChecked(false);

        creaButton = view.findViewById(R.id.crea_session);
        annullaButton = view.findViewById(R.id.annulla_session);

        //Metodi
        initDatePicker();
        setButtonListeners();
        setDropDownLists();

        return view;
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
                popTimePiker(true);
            }
        });
        hourEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker(false);
            }
        });
        hourEndCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Disattiva/Riattiva selezione ore e minuti
                setViewAndChildrenEnabled(layoutEndSession, !hourEndCheckBox.isChecked());
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
                try {
                    String start = year + "-" + month + "-" + day + " " + startHour + ":" + startMinute;
                    s.setOraInizio(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ITALY).parse(start));
                } catch (Exception e){
                    return;
                }
                if (hourEndCheckBox.isChecked()) {   //una attività "infinita" ha la data settata all'anno 0 (1/1/1970)
                    s.setOraFine(new Date(0));
                } else {
                    try {
                        String end = year + "-" + month + "-" + day + " " + endHour + ":" + endMinute;
                        s.setOraFine(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ITALY).parse(end));
                    } catch (Exception e){
                        return;
                    }
                }

                if (db.addSessioneProgrammata(s)) {
                    Toast.makeText(getContext(), "Sessione creata!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Errore creazione sessione", Toast.LENGTH_SHORT).show();
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

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        return "--/--/--";  //non visualizzo la data così da far capire che bisogna selezionarla
    }
    private String getNowHour(){
        Calendar cal = Calendar.getInstance();
        startHour = cal.get(Calendar.HOUR);
        endHour = cal.get(Calendar.HOUR);
        startMinute = cal.get(Calendar.MINUTE);
        endMinute = cal.get(Calendar.MINUTE);

        return "--:--"; //non visualizzo la data così da far capire che bisogna selezionarla
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
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this.getContext(),dateSetListener,year,month,day);
    }

    private void popTimePiker(boolean b){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(b){
                    startHour = selectedHour;
                    startMinute = selectedMinute;
                    String time = startHour+":"+(startMinute<10?"0"+startMinute:startMinute);
                    //testo bottone timePicker
                    hourStartButton.setText(time);
                }else {
                    endHour = selectedHour;
                    endMinute = selectedMinute;
                    String time = endHour+":"+(endMinute<10?"0"+endMinute:endMinute);
                    //testo bottone timePicker
                    hourEndButton.setText(time);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, startHour, startMinute, true);

        timePickerDialog.setTitle("Scegli l'ora");
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
        activity = db.getAllActivity();
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