package com.unitn.lpsmt.group13.pommidori.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.Timer;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StartNewSessionFragment extends DialogFragment {

    //Variabili
    Button menoDurataOre, piuDurataOre, menoDurataMin, piuDurataMin, avvia;
    TextView durataPerOre,durataPerMin;
    CheckBox checkDurataLibera;
    LinearLayout riquadroSelezioneTimer;
    StatoTimer statoTimer;
    TableActivityModel activitySelezionata;
    AutoCompleteTextView dropdownSessionePers;

    //valori pausa
    int ore, min;

    //Shared Preferances key
    private final String SHARED_PREFS_POMODORO = Utility.SHARED_PREFS_TIMER;
    private final String ORE_TIMER = Utility.ORE_TIMER;
    private final String MINUTI_TIMER = Utility.MINUTI_TIMER;
    private final String TEMPO_INIZIALE = Utility.TEMPO_INIZIALE;
    private final String TEMPO_RIMASTO = Utility.TEMPO_RIMASTO;
    private final String TEMPO_FINALE = Utility.TEMPO_FINALE;
    private final String TEMPO_TRASCORSO = Utility.TEMPO_TRASCORSO;
    private final String STATO_TIMER = Utility.STATO_TIMER;
    private final String STATO_TIMER_PRECEDENTE = Utility.STATO_TIMER_PRECEDENTE;
    private final String NOME_ACTIVITY_ASSOCIATA = Utility.NOME_ACTIVITY_ASSOCIATA;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_dialog_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione variabili
        menoDurataOre = view.findViewById(R.id.menoDurataOre);
        piuDurataOre = view.findViewById(R.id.piuDurataOre);
        menoDurataMin = view.findViewById(R.id.menoDurataMin);
        piuDurataMin = view.findViewById(R.id.piuDurataMin);
        durataPerOre = view.findViewById(R.id.durataPerOre);
        durataPerMin = view.findViewById(R.id.durataPerMin);
        dropdownSessionePers = view.findViewById(R.id.dropdownSessionePers);
        checkDurataLibera = view.findViewById(R.id.checkDurata);
        riquadroSelezioneTimer = view.findViewById(R.id.timerDurata);
        avvia = view.findViewById(R.id.avviaSessione);
        statoTimer = new StatoTimer( StatoTimer.COUNTDOWN);     //Valore di default per il fragment
        activitySelezionata = new TableActivityModel();         //Default activity

        //Metodi
        loadData();
        setButtonListener();
        setDropDownLists();
    }

    protected void setButtonListener(){
        checkDurataLibera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( checkDurataLibera.isChecked()){
                    //Seleziona tipo di timer countup
                    statoTimer.setValue( StatoTimer.COUNTUP);
                    //Riattiva selezione ore e minuti nel caso fossero disabled
                    setViewAndChildrenEnabled( riquadroSelezioneTimer, false);
                    durataPerOre.setTextColor(Color.GRAY);
                    durataPerMin.setTextColor(Color.GRAY);
                }else{
                    //Seleziona tipo di timer countdown
                    statoTimer.setValue( StatoTimer.COUNTDOWN);
                    //Disattiva selezione ore e minuti
                    setViewAndChildrenEnabled( riquadroSelezioneTimer, true);
                    durataPerOre.setTextColor(Color.BLACK);
                    durataPerMin.setTextColor(Color.BLACK);
                }
            }
        });

        piuDurataOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ore<12) { //durata massima sessione 12 ore
                    ore++;
                    durataPerOre.setText(Integer.toString(ore));
                }
            }
        });

        menoDurataOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ore>0) { //la sessione deve essere maggiore di 0
                    ore--;
                    durataPerOre.setText(Integer.toString(ore));
                }
            }
        });

        piuDurataMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((min==59)&&(ore<12)){ //se si va oltre i 59 minuti, cambia ora e resetta i minuti
                    min=0;
                    ore++;
                    durataPerOre.setText(Integer.toString(ore));
                    durataPerMin.setText(Integer.toString(min));
                }else if(min<59) { //durata massima 59 minuti
                    min++;
                    durataPerMin.setText(Integer.toString(min));
                }
            }
        });

        menoDurataMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((ore>0)&&(min==0)){ //se sii scende sotto l'ora, cambia ora e si porta i minuti a 59
                    min=59;
                    ore--;
                    durataPerOre.setText(Integer.toString(ore));
                    durataPerMin.setText(Integer.toString(min));
                } else if((ore==0) && (min>1)) { //la pausa deve essere maggiore di 0
                    min--;
                    durataPerMin.setText(Integer.toString(min));
                }else if(ore!=0){
                    min--;
                    durataPerMin.setText(Integer.toString(min));
                }
            }
        });

        avvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Timer.class);
                saveData();

                startActivity(i);
                StartNewSessionFragment.this.dismiss();
            }
        });
    }

    private void setDropDownLists(){
        Database db = Database.getInstance( getContext());
        List<TableActivityModel> allActivity = db.getAllActivity();

        if(allActivity.isEmpty()){
            allActivity.add(new TableActivityModel());
        }

        ArrayAdapter adapter = new ArrayAdapter<TableActivityModel>(
                getContext(),
                R.layout.dropdown_item,
                allActivity
        );

        dropdownSessionePers.setText( ((TableActivityModel)adapter.getItem(0)).toString());
        dropdownSessionePers.setAdapter( adapter);

        dropdownSessionePers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activitySelezionata = (TableActivityModel) parent.getAdapter().getItem( position);
            }
        });
    }

    void saveData(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_POMODORO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Trasformo ore:minuti in millisecondi
        long milliSecondi = ((ore*3600) +  (min*60)) * 1000;

        editor.putInt(ORE_TIMER, ore);
        editor.putInt(MINUTI_TIMER, min);
        editor.putLong(TEMPO_INIZIALE, milliSecondi);
        editor.putLong(TEMPO_RIMASTO, milliSecondi);
        editor.putLong(TEMPO_FINALE, (System.currentTimeMillis() + milliSecondi));
        editor.putLong(TEMPO_TRASCORSO, 0);
        editor.putInt(STATO_TIMER, statoTimer.getValue());
        editor.putInt(STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO);
        editor.putString(NOME_ACTIVITY_ASSOCIATA, activitySelezionata.getName());
        editor.apply();
    }

    void loadData(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_POMODORO, Context.MODE_PRIVATE);

        ore = sharedPreferences.getInt(ORE_TIMER, 0);
        min = sharedPreferences.getInt(MINUTI_TIMER, 30);

        durataPerOre.setText(Integer.toString(ore));
        durataPerMin.setText(Integer.toString(min));
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

}
