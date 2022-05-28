package com.unitn.lpsmt.group13.pommidori.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unitn.lpsmt.group13.pommidori.CountUpTimer;
import com.unitn.lpsmt.group13.pommidori.Homepage;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.Utility;

import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	//Variabili
	private FloatingActionButton btnPlay;
	private FloatingActionButton btnPausa;
	private FloatingActionButton btnStop;
	private MaterialProgressBar progressBar;
	private TextView timer;
	private CountDownTimer countDownTimer;      //Timer a conto alla rovescia, oggetto usato sia per i pomodoro sia per la pausa
	private CountUpTimer countUpTimer;          //Timer a contatore
	private SharedPreferences sharedPreferences;
	private Context context;

	//Tutti i tempi long sono espressi in milli secondi
	private long tempoIniziale;
	private long tempoRimasto;
	private long tempoFinale;
	private long tempoTrascorso;
	private final long DURATA_MASSIMA_COUNTUP_TIMER = Utility.DURATA_MASSIMA_COUNTUP_TIMER;
	private StatoTimer statoTimer;
	private StatoTimer statoTimerPrecedente;
	private StatoTimerListener statoTimerListener;

	//I timer per essere persistenti anche con l'activity chiusa necessitano di salvare delle informazioni nelle shared preferences
	private final String SHARED_PREFS_TIMER = Utility.SHARED_PREFS_TIMER;
	private final String ORE_TIMER = Utility.ORE_TIMER;
	private final String MINUTI_TIMER = Utility.MINUTI_TIMER;
	private final String TEMPO_INIZIALE = Utility.TEMPO_INIZIALE;
	private final String TEMPO_RIMASTO = Utility.TEMPO_RIMASTO;
	private final String TEMPO_FINALE = Utility.TEMPO_FINALE;
	private final String TEMPO_TRASCORSO = Utility.TEMPO_TRASCORSO;
	private final String STATO_TIMER = Utility.STATO_TIMER;
	private final String STATO_TIMER_PRECEDENTE = Utility.STATO_TIMER_PRECEDENTE;
	private final String PAUSA = Utility.PAUSA;

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	public TimerFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment TimerFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TimerFragment newInstance(String param1, String param2) {
		TimerFragment fragment = new TimerFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	//Interfaccia che al cambio di stato del timer, aggiorna il titolo della toolbar
	public interface StatoTimerListener {
		public void cambioStatoToolbarTimer(int stato);
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if( context instanceof StatoTimerListener){
			statoTimerListener = (StatoTimerListener) context;
		}else{
			throw new ClassCastException(context.toString()
					+ " must implement TimerFragment.StatoTimerListener");
		}
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
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Inizializzazione variabili
		btnPlay = view.findViewById(R.id.play_timer_fab);
		btnPausa = view.findViewById(R.id.pausa_timer_fab);
		btnStop = view.findViewById(R.id.stop_timer_fab);
		//progressBar = view.findViewById(R.id.progress_bar);
		timer = view.findViewById(R.id.timer);
		sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
		context = view.getContext();

		//Metodi
		setButtonListener();
	}

	/*  Metodo invocato all'avvio o al rientro in activity, permette la selezione del timer corretto
        recuperando i dati salvati del tempo già trascorso per avviare poi un nuovo timer.
    * */
	@Override
	public void onResume() {
		super.onResume();
		loadData();

		//startCountDownTimer e startCountDownPausa usano lo stesso oggetto countDownTimer per il conteggio del tempo, a livello logico sono uguali
		if( statoTimer.isCountDown() || statoTimer.isPausa()){
			aggiornaTimer();
			tempoRimasto = tempoFinale - System.currentTimeMillis();    //Calcolo tempo rimanente
			if( tempoRimasto < 0){
				tempoRimasto = 0;
				statoTimer.setValue( StatoTimer.DISATTIVO);
				aggiornaTimer();
			}else{
				startCountDownTimer();
			}

		}else if( statoTimer.isCountUp()){   //Gestione CountUpTimer

			//Se tempoTrascorso == 0 significa che è un nuovo timer, non è necessario calcolare il tempo trascorso con l'activity chiusa
			if(tempoTrascorso != 0){
				//tempoFinale = System.currentTimeMillis() nel momento della onStop()
				//Calcolo che considera il tempo trascorso con l'activity chiusa
				tempoTrascorso = tempoTrascorso + (System.currentTimeMillis() - tempoFinale);
			}

			startCountUpTimer();
		}
	}

	//Cancella i timer in corso, salva le informazioni dei timer
	@Override
	public void onPause() {
		super.onPause();
		if(countDownTimer != null){
			countDownTimer.cancel();
		}
		if(countUpTimer != null){
			countUpTimer.cancel();
			tempoFinale = System.currentTimeMillis();   //Si salva il momento di chiusura dell'activity
		}

		saveData();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		statoTimerListener = null;
	}

	//Salva i dati nelle shared preferences
	private void saveData() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong( TEMPO_INIZIALE, tempoIniziale);
		editor.putLong( TEMPO_RIMASTO, tempoRimasto);
		editor.putLong( TEMPO_FINALE, tempoFinale);
		editor.putLong( TEMPO_TRASCORSO, tempoTrascorso);
		editor.putInt(STATO_TIMER, statoTimer.getValue());
		editor.putInt(STATO_TIMER_PRECEDENTE, statoTimerPrecedente.getValue());
		editor.apply();
	}

	//Carica i dati dalle shared preferences
	private void loadData() {
		tempoIniziale = sharedPreferences.getLong( TEMPO_INIZIALE, 1800000);
		tempoRimasto = sharedPreferences.getLong( TEMPO_RIMASTO, tempoIniziale);
		tempoFinale = sharedPreferences.getLong( TEMPO_FINALE, 0);
		tempoTrascorso = sharedPreferences.getLong( TEMPO_TRASCORSO, 0);
		statoTimer = new StatoTimer( sharedPreferences.getInt(STATO_TIMER, StatoTimer.DISATTIVO));
		statoTimerPrecedente = new StatoTimer( sharedPreferences.getInt(STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO));
	}

	private void setButtonListener() {
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( statoTimerPrecedente.isCountDown()){
					startCountDownTimer();
				}else{
					startCountUpTimer();
				}
			}
		});

		btnPausa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startCountDownPausa();
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { stopTimer(); }
		});

	}

	//Avvia un countdown timer
	private void startCountDownTimer(){
		tempoFinale = System.currentTimeMillis() + tempoRimasto;
		statoTimer.setValue( StatoTimer.COUNTDOWN);
		aggiornaButtonsAndToolbar();

		//Oggetto countdown timer, con tempo rimanente alla fine timer, metodo onTick richiamato ogni 1000 millisecondi
		countDownTimer = new CountDownTimer( tempoRimasto, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tempoRimasto = millisUntilFinished;
				aggiornaTimer();
			}

			@Override
			public void onFinish() {
				statoTimer.setValue( StatoTimer.DISATTIVO);
				aggiornaTimer();
				aggiornaButtonsAndToolbar();
			}
		}.start();
	}

	//Avvia un countup timer
	private void startCountUpTimer() {
		statoTimer.setValue( StatoTimer.COUNTUP);
		aggiornaButtonsAndToolbar();

		countUpTimer = new CountUpTimer( DURATA_MASSIMA_COUNTUP_TIMER) {
			@Override
			public void onTick(int sec) {
				tempoTrascorso = tempoTrascorso + 1000;
				if(tempoTrascorso >= DURATA_MASSIMA_COUNTUP_TIMER){
					//CountUpTimer arriva a durata massima, interrompi pomodoro
					stopTimer();
				}else{
					aggiornaTimer();
				}
			}
		};

		countUpTimer.start();
	}

	//Avvia un countdown timer per una pausa, modificando dati salvati e alcune views
	private void startCountDownPausa(){
		//Cancellare l'attuale timer
		deleteTimer();

		//Recuperare durata pausa e reimpostare tempi
		long pausaMilliSecondi = 60000 * sharedPreferences.getInt(PAUSA, 5);
		tempoIniziale = pausaMilliSecondi;
		tempoRimasto = pausaMilliSecondi;
		tempoFinale = System.currentTimeMillis() + tempoRimasto;
		tempoTrascorso = 0;     //Il countUpTimer dovrà ripartire da 0 dopo la pausa
		statoTimer.setValue( StatoTimer.PAUSA);
		aggiornaButtonsAndToolbar();
		saveData();

		//Oggetto countdown timer, con tempo rimanente alla fine pausa, metodo onTick richiamato ogni 1000 millisecondi
		countDownTimer = new CountDownTimer( tempoRimasto, 1000){
			@Override
			public void onTick(long millisUntilFinished) {
				tempoRimasto = millisUntilFinished;
				aggiornaTimer();
			}

			@Override
			public void onFinish() {
				statoTimer.setValue( StatoTimer.DISATTIVO);
				aggiornaButtonsAndToolbar();

				if( statoTimerPrecedente.isCountDown()){
					//Riottieni i dati per il countdown e si aggiorna l'interfaccia
					int ore = sharedPreferences.getInt(ORE_TIMER, 0);
					int min = sharedPreferences.getInt(MINUTI_TIMER, 30);
					long milliSecondi = ((ore*3600) +  (min*60)) * 1000;

					tempoIniziale = milliSecondi;
					tempoRimasto = milliSecondi;
					aggiornaTimer();

				}else{	//countup
					tempoTrascorso = 0;
					aggiornaTimer();
				}
			}
		}.start();
	}

	//Aggiorna il timer, formattando una stringa in modo opportuno
	private void aggiornaTimer() {
		String tempoFormattato;
		int ore = 0;
		int minuti = 0;
		int secondi = 0;

		if( statoTimer.isCountDown() || statoTimer.isPausa() || (statoTimer.isDisattivo() && statoTimerPrecedente.isCountDown())){
			ore = (int) (tempoRimasto / 1000) / 3600;
			minuti = (int) ((tempoRimasto / 1000) % 3600) / 60;
			secondi = (int) (tempoRimasto / 1000) % 60;

		}else if( statoTimer.isCountUp() || (statoTimer.isDisattivo() && statoTimerPrecedente.isCountUp())){
			ore = (int) (tempoTrascorso / 1000) / 3600;
			minuti = (int) ((tempoTrascorso / 1000) % 3600) / 60;
			secondi = (int) (tempoTrascorso / 1000) % 60;

		}
		/*else if( statoTimer.isDisattivo() && (statoTimerPrecedente.isCountDown() || statoTimerPrecedente.isCountUp()) ){
			//stato disattivo prima di iniziare una pausa
			minuti = sharedPreferences.getInt(PAUSA, 5);
		}*/

		if( ore > 0){
			tempoFormattato = String.format(Locale.getDefault(),
					"%d:%02d:%02d", ore, minuti, secondi);
		}else{
			tempoFormattato = String.format(Locale.getDefault(),
					"%02d:%02d", minuti, secondi);
		}

		timer.setText( tempoFormattato);
	}

	private void aggiornaButtonsAndToolbar(){
		if( statoTimer.isCountDown() || statoTimer.isCountUp()){
			btnPlay.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);
			btnPausa.setVisibility(View.VISIBLE);
			statoTimerListener.cambioStatoToolbarTimer( R.string.pomodoro_in_corso_timer);

		}else if( statoTimer.isPausa()){
			btnPlay.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);
			btnPausa.setVisibility(View.GONE);
			statoTimerListener.cambioStatoToolbarTimer( R.string.pausa_in_corso_timer);

		}else if( statoTimer.isDisattivo()){
			btnPlay.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.GONE);
			btnPausa.setVisibility(View.VISIBLE);
			statoTimerListener.cambioStatoToolbarTimer( R.string.pomodoro_disattivo_timer);
		}
	}

	//Ferma qualsiasi timer
	private void stopTimer() {
		//Cancellare l'attuale timer
		deleteTimer();

		int ore = sharedPreferences.getInt(ORE_TIMER, 0);
		int min = sharedPreferences.getInt(MINUTI_TIMER, 30);
		long milliSecondi = ((ore*3600) +  (min*60)) * 1000;

		tempoIniziale = milliSecondi;
		tempoRimasto = milliSecondi;
		//tempoFinale = System.currentTimeMillis() + milliSecondi;
		tempoTrascorso = 0;

		if( !statoTimer.isPausa()){	//se setto statoPrecedente a pausa perdo quello stato che avevo prima
			statoTimerPrecedente.setValue( statoTimer.getValue());
		}
		statoTimer.setValue( StatoTimer.DISATTIVO);
		aggiornaTimer();
		aggiornaButtonsAndToolbar();
	}

	private void deleteTimer(){
		if( countDownTimer != null){
			countDownTimer.cancel();
		}
		if( countUpTimer != null){
			countUpTimer.cancel();
		}
	}

	//Funzione puramente di debug, chiude i timer in corso resettando lo statoTimer e riportando alla homepage
	private void reset(){
		statoTimer.setValue( StatoTimer.DISATTIVO);
		if( countDownTimer != null){
			countDownTimer.cancel();
		}
		if( countUpTimer != null){
			countUpTimer.cancel();
		}

		saveData();
		Intent i = new Intent( context, Homepage.class);
		startActivity( i);
	}

}