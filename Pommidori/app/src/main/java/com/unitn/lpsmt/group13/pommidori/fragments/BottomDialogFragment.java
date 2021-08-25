package com.unitn.lpsmt.group13.pommidori.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.Homepage;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoSessione;
import com.unitn.lpsmt.group13.pommidori.Timer;
import com.unitn.lpsmt.group13.pommidori.Utility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BottomDialogFragment extends DialogFragment {

    //Variabili
    Button menoDurataOre, piuDurataOre, menoDurataMin, piuDurataMin, avvia;
    TextView durataPerOre,durataPerMin;
    CheckBox checkDurataLibera;
    LinearLayout riquadroSelezioneTimer;
    StatoSessione statoSessione;

    AutoCompleteTextView dropdownSessionePers;

    //valore provvisorio pausa
    int ore, min;

    //Shared Preferances key
    private final String SHARED_PREFS_SESSIONE = Utility.SHARED_PREFS_SESSIONE;
    private final String ORE_SESSIONE = Utility.ORE_SESSIONE;
    private final String MINUTI_SESSIONE = Utility.MINUTI_SESSIONE;
    private final String TEMPO_INIZIALE = Utility.TEMPO_INIZIALE;
    private final String TEMPO_RIMASTO = Utility.TEMPO_RIMASTO;
    private final String TEMPO_FINALE = Utility.TEMPO_FINALE;
    private final String TEMPO_TRASCORSO = Utility.TEMPO_TRASCORSO;
    private final String STATO_SESSIONE = Utility.STATO_SESSIONE;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View i = inflater.inflate(R.layout.layout_dialog_fragment,container,false);


        //Inizializzazione variabili
        menoDurataOre = i.findViewById(R.id.menoDurataOre);
        piuDurataOre = i.findViewById(R.id.piuDurataOre);
        menoDurataMin = i.findViewById(R.id.menoDurataMin);
        piuDurataMin = i.findViewById(R.id.piuDurataMin);
        durataPerOre = i.findViewById(R.id.durataPerOre);
        durataPerMin = i.findViewById(R.id.durataPerMin);
        dropdownSessionePers = i.findViewById(R.id.dropdownSessionePers);
        checkDurataLibera = i.findViewById(R.id.checkDurata);
        riquadroSelezioneTimer = i.findViewById(R.id.timerDurata);
        avvia = i.findViewById(R.id.avviaSessione);
        statoSessione = new StatoSessione( StatoSessione.COUNTDOWN);    //Valore di default per il fragment

        //Metodi
        loadData();
        setButtonListener();
        setDropDownLists();

        return i;
    }

    protected void setButtonListener(){
        checkDurataLibera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( checkDurataLibera.isChecked()){
                    //Seleziona tipo di timer countup
                    statoSessione.setValue( StatoSessione.COUNTUP);
                    //Riattiva selezione ore e minuti nel caso fossero disabled
                    setViewAndChildrenEnabled( riquadroSelezioneTimer, false);
                    durataPerOre.setTextColor(Color.GRAY);
                    durataPerMin.setTextColor(Color.GRAY);
                }else{
                    //Seleziona tipo di timer countdown
                    statoSessione.setValue( StatoSessione.COUNTDOWN);
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
                }
                if(min<59) { //durata massima 59 minuti
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
                }
                if(min>0) { //la pausa deve essere maggiore di 0
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
            }
        });
    }

    private void setDropDownLists(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.planets_array,
                R.layout.dropdown_item
        );

        dropdownSessionePers.setText( adapter.getItem(0));

        dropdownSessionePers.setAdapter( adapter);
    }

    void saveData(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_SESSIONE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Trasformo ore:minuti in millisecondi
        long milliSecondi = ((ore*3600) +  (min*60)) * 1000;

        editor.putInt(ORE_SESSIONE, ore);
        editor.putInt(MINUTI_SESSIONE, min);
        editor.putLong(TEMPO_INIZIALE, milliSecondi);
        editor.putLong(TEMPO_RIMASTO, milliSecondi);
        editor.putLong(TEMPO_FINALE, (System.currentTimeMillis() + milliSecondi));
        editor.putLong(TEMPO_TRASCORSO, 0);
        editor.putInt(STATO_SESSIONE, statoSessione.getValue());
        editor.apply();
    }

    void loadData(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_SESSIONE, Context.MODE_PRIVATE);

        ore = sharedPreferences.getInt(ORE_SESSIONE, 0);
        min = sharedPreferences.getInt(MINUTI_SESSIONE, 30);

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
