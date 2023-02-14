package com.unitn.lpsmt.group13.pommidori.fragments;

import static android.content.Context.ALARM_SERVICE;

import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_ACTIVITY_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_START_HOUR_INTENT;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.broadcastReceivers.ReminderBroadcastReceiver;
import com.unitn.lpsmt.group13.pommidori.db.Database;
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

    private static final String TAG = "NewSessionFragment";

    private final int START_HOUR_SELECTED = 0;
    private final int END_HOUR_SELECTED = 1;
    private final int FIVE_MINUTES = 5*60000;
    private final int TEN_MINUTES = 10*60000;
    private final int FIFTEEN_MINUTES = 15*60000;
    private final int THIRTY_MINUTES = 30*60000;
    private final int ONE_HOUR = 60*60000;
    private final int TWO_HOURS = 120*60000;

    private DatePickerDialog datePickerDialog;
    private Button dateButton,hourStartButton,hourEndButton, creaButton, annullaButton;
    private TableActivityModel activityModel;

    private AutoCompleteTextView listaAttivita,listaAvviso;
    private Database db;
    private List<TableActivityModel> activity;
    private AlarmManager alarmManager;

    //Dati
    private int year, month, day, startHour, startMinute, endHour, endMinute, reminder;
    private boolean hasSelectedStartHour, hasSelectedEndHour;

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

        listaAttivita = view.findViewById( R.id.dropdown_activity_new_session);
        listaAvviso = view.findViewById( R.id.dropdown_avviso_new_session);

        dateButton = view.findViewById(R.id.date_session_picker);
        hourStartButton = view.findViewById(R.id.start_hour_picker);
        hourEndButton = view.findViewById(R.id.end_hour_picker);
        creaButton = view.findViewById(R.id.crea_session);
        annullaButton = view.findViewById(R.id.annulla_session);
        dateButton.setText(R.string.date_not_selected);
        hourStartButton.setText(R.string.hour_not_selected);
        hourEndButton.setText(R.string.hour_not_selected);
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        day = 0;
        month = 0;
        year = 0;
        startHour = 0;
        startMinute = 0;
        endHour = 0;
        endMinute = 0;
        reminder = -1;  //Default nessuna notifica

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

        listaAvviso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        reminder = FIVE_MINUTES;
                        break;
                    case 2:
                        reminder = TEN_MINUTES;
                        break;
                    case 3:
                        reminder = FIFTEEN_MINUTES;
                        break;
                    case 4:
                        reminder = THIRTY_MINUTES;
                        break;
                    case 5:
                        reminder = ONE_HOUR;
                        break;
                    case 6:
                        reminder = TWO_HOURS;
                        break;
                    case 0: //MAI, caso di default
                        reminder = -1;
                    default:
                        break;
                }
            }
        });

        creaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableSessionProgModel s = new TableSessionProgModel();
                long startHourMillis = 0;
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
                        Date dateFomatted = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).parse(start);
                        startHourMillis = dateFomatted.toInstant().toEpochMilli();

                        //Selezionato di non volere notifiche
                        if( reminder != -1){
                            createReminder( startHourMillis);
                        }

                        s.setOraInizio( dateFomatted);
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

        listaAttivita.setText(activityAdapter.getItem(0).toString());
        activityModel = activity.get(0);
        listaAvviso.setText(avvisoAdapter.getItem(0));

        listaAttivita.setAdapter( activityAdapter);
        listaAvviso.setAdapter(avvisoAdapter);
    }

    private void createReminder( long startTime){
        //Crea la notifica solo se l'inizio della sessione è successivo ad adesso
        if( new Date( startTime).after( new Date())){

            Intent intent = new Intent( getContext(), ReminderBroadcastReceiver.class);
            intent.putExtra( REMINDER_ACTIVITY_INTENT, activityModel.getName());//Activity associata
            intent.putExtra( REMINDER_START_HOUR_INTENT, startTime);//Orario inizio
            PendingIntent pendingIntent = PendingIntent.getBroadcast( getContext(), Utility.getPendingIntentRequestCode(), intent, PendingIntent.FLAG_IMMUTABLE);

            //La notifica viene anticipata di reminder
            long triggerTime = startTime - reminder;
            alarmManager.setExact( AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}