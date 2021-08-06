package com.unitn.lpsmt.group13.pommidori.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.Homepage;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Timer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BottomDialogFragment extends DialogFragment {

    //Variabili
    Button menoDurataOre, piuDurataOre, menoDurataMin, piuDurataMin, avvia;
    TextView durataPerOre,durataPerMin;

    AutoCompleteTextView dropdownSessionePers;

    //valore provvisorio pausa
    int ore, min;

    //Shared Preferances key
    public static final String SHARED_PREFS = Homepage.SHARED_PREFS;
    public static final String ORE_SESSIONE = "ore";
    public static final String MINUTI_SESSIONE = "minuti";

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
        avvia = i.findViewById(R.id.avviaSessione);

        //Metodi
        loadData();
        setButtonListener();
        setDropDownLists();

        return i;
    }

    protected void setButtonListener(){
        piuDurataOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ore<12) { //durata massima sessione 12 ore
                    ore++;
                    saveData();
                }
            }
        });
        menoDurataOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ore>0) { //la sessione deve essere maggiore di 0
                    ore--;
                    saveData();
                }
            }
        });

        piuDurataMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((min==59)&&(ore<12)){ //se si va oltre i 59 minuti, cambia ora e resetta i minuti
                    min=0;
                    ore++;
                    saveData();
                }
                if(min<59) { //durata massima 59 minuti
                    min++;
                    saveData();;
                }
            }
        });
        menoDurataMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((ore>0)&&(min==0)){ //se sii scende sotto l'ora, cambia ora e si porta i minuti a 59
                    min=59;
                    ore--;
                    saveData();
                }
                if(min>0) { //la pausa deve essere maggiore di 0
                    min--;
                    saveData();
                }
            }
        });

        avvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Timer.class);
                i.putExtra("ore", Integer.toString(ore));
                i.putExtra("minuti",Integer.toString(min));
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(ORE_SESSIONE, ore);
        editor.putInt(MINUTI_SESSIONE, min);
        editor.apply();

        durataPerOre.setText(Integer.toString(ore));
        durataPerMin.setText(Integer.toString(min));
    }

    void loadData(){
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        ore = sharedPreferences.getInt(ORE_SESSIONE, 0);
        min = sharedPreferences.getInt(MINUTI_SESSIONE, 30);

        durataPerOre.setText(Integer.toString(ore));
        durataPerMin.setText(Integer.toString(min));
    }
}
