package com.unitn.lpsmt.group13.pommidori.fragments;

import static com.unitn.lpsmt.group13.pommidori.Utility.MINUTI_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.NOME_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.ORE_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER_PRECEDENTE;

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

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.activities.Timer;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StartNewSessionFragment extends DialogFragment {

    //Variabili
    private Button menoDurataOre, piuDurataOre, menoDurataMin, piuDurataMin, avvia;
    private TextView durataPerOre,durataPerMin;
    private CheckBox checkDurataLibera;
    private LinearLayout riquadroSelezioneTimer;
    private StatoTimer statoTimer;
    private AutoCompleteTextView dropdownSessionePers;
    private TableActivityModel activitySelezionata;

    //valori pausa
    private int ore, min;


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
        activitySelezionata = new TableActivityModel();

        //Metodi
        loadSharedPreferences();

        durataPerOre.setText(Integer.toString(ore));
        durataPerMin.setText(Integer.toString(min));

        setButtonListener();
        setDropDownLists();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSharedPreferences();
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
                saveSharedPreferences();

                startActivity(i);
                StartNewSessionFragment.this.dismiss();
            }
        });
    }

    private void setDropDownLists(){
        Database db = Database.getInstance( getContext());
        List<TableActivityModel> allActivity = db.getAllActivities();

        if(allActivity.isEmpty()){
            allActivity.add(new TableActivityModel());
        }

        ArrayAdapter adapter = new ArrayAdapter<TableActivityModel>(
                getContext(),
                R.layout.dropdown_item,
                allActivity
        );

        dropdownSessionePers.setText( ((TableActivityModel)adapter.getItem(0)).getName());
        dropdownSessionePers.setAdapter( adapter);

        dropdownSessionePers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activitySelezionata = (TableActivityModel) parent.getAdapter().getItem( position);
            }
        });
    }

    void saveSharedPreferences(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_TIMER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(ORE_TIMER, ore);
        editor.putInt(MINUTI_TIMER, min);
        editor.putInt(STATO_TIMER, statoTimer.getValue());
        editor.putInt(STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO);
        editor.putString(NOME_ACTIVITY_ASSOCIATA, activitySelezionata.getName());
        editor.apply();
    }

    void loadSharedPreferences(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_TIMER, Context.MODE_PRIVATE);

        ore = sharedPreferences.getInt(ORE_TIMER, 0);
        min = sharedPreferences.getInt(MINUTI_TIMER, 30);
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
