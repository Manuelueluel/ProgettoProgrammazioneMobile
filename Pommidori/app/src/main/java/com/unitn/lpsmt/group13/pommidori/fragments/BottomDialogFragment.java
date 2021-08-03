package com.unitn.lpsmt.group13.pommidori.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

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
    int ore = 0, min = 30;

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
        durataPerOre.setText(Integer.toString(ore));

        durataPerMin = i.findViewById(R.id.durataPerMin);
        durataPerMin.setText(Integer.toString(min));

        dropdownSessionePers = i.findViewById(R.id.dropdownSessionePers);

        avvia = i.findViewById(R.id.avviaSessione);

        //Metodi
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
                if((min==59)&&(ore<12)){
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
                if((ore>0)&&(min==0)){
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
}
