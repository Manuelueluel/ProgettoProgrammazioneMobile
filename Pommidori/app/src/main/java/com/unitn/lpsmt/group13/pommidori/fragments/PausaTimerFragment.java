package com.unitn.lpsmt.group13.pommidori.fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.unitn.lpsmt.group13.pommidori.Utility.PAUSA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER_PRECEDENTE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.services.CountDownTimerService;
import com.unitn.lpsmt.group13.pommidori.services.CountUpTimerService;
import com.unitn.lpsmt.group13.pommidori.services.PausaTimerService;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PausaTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PausaTimerFragment extends Fragment {

    private static final String TAG = "PausaTimerFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Views
    private FloatingActionButton btnPlay;
    private FloatingActionButton btnPausa;
    private FloatingActionButton btnStop;
    private TextView timer;
    private SharedPreferences sharedPreferences;
    private Context context;
    private StatoTimer statoTimer, statoTimerPrecedente;


    public interface SwitchFragment{
        public void switchToCountDown();
        public void switchToCountup();
    }

    public PausaTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PausaTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PausaTimerFragment newInstance(String param1, String param2) {
        PausaTimerFragment fragment = new PausaTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione views
        btnPlay = view.findViewById(R.id.play_timer_fab);
        btnPausa = view.findViewById(R.id.pausa_timer_fab);
        btnStop = view.findViewById(R.id.stop_timer_fab);
        timer = view.findViewById(R.id.timer);
        sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        statoTimer = new StatoTimer();
        statoTimerPrecedente = new StatoTimer();

        //Metodi
        setButtonListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreferences();

        if( !CountDownTimerService.isRunning && !CountUpTimerService.isRunning &&
                !PausaTimerService.isRunning && statoTimer.isPausa()){

            context.startService( new Intent( context, PausaTimerService.class));
            long pausaMilliSecondi = 60000 * sharedPreferences.getInt(PAUSA, 5);
            updateTimerView(pausaMilliSecondi);
        }

        aggiornaButtons();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setButtonListener() {
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.stopService( new Intent(getContext(), PausaTimerService.class));
            }
        });
    }

    //Carica i dati dalle shared preferences
    private void loadSharedPreferences() {
        statoTimer.setValue( sharedPreferences.getInt(STATO_TIMER, StatoTimer.DISATTIVO));
        statoTimerPrecedente.setValue( sharedPreferences.getInt(STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO));
    }

    //Salva i dati nelle shared preferences
    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());
        editor.putInt( STATO_TIMER_PRECEDENTE, statoTimerPrecedente.getValue());
        editor.apply();
    }

    public void aggiornaButtons(){
        btnPlay.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        btnPausa.setVisibility(View.GONE);
    }

    public void updateTimerView(long timeMillis){
        String tempoFormattato;
        int ore = 0;
        int minuti = 0;
        int secondi = 0;

        ore = (int) (timeMillis / 1000) / 3600;
        minuti = (int) ((timeMillis / 1000) % 3600) / 60;
        secondi = (int) (timeMillis / 1000) % 60;

        if( ore > 0){
            tempoFormattato = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", ore, minuti, secondi);
        }else{
            tempoFormattato = String.format(Locale.getDefault(),
                    "%02d:%02d", minuti, secondi);
        }

        timer.setText( tempoFormattato);
    }
}