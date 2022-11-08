package com.unitn.lpsmt.group13.pommidori.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
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
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;

import java.util.Date;
import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {

	//Views
	private FloatingActionButton btnPlay;
	private FloatingActionButton btnPausa;
	private FloatingActionButton btnStop;
	private MaterialProgressBar progressBar;
	private TextView timer;
	private CountDownTimer countDownTimer;      //Timer a conto alla rovescia, oggetto usato sia per i pomodoro sia per la pausa
	private CountUpTimer countUpTimer;          //Timer a contatore
	private SharedPreferences sharedPreferences;
	private Context context;
	private Database database;

	//Variabili: Tutti i tempi long sono espressi in milli secondi
	private long tempoIniziale;
	private long tempoRimasto;
	private long tempoFinale;
	private long tempoTrascorso;
	int oreTimer, minutiTimer;
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
	private final String ACTIVITY_ASSOCIATA = Utility.NOME_ACTIVITY_ASSOCIATA;
	private final long DURATA_MASSIMA_COUNTUP_TIMER = Utility.DURATA_MASSIMA_COUNTUP_TIMER;
	private final String COLORE_ACTIVITY_ASSOCIATA = Utility.COLORE_ACTIVITY_ASSOCIATA;

	//Interfaccia che al cambio di stato del timer, aggiorna il titolo della toolbar
	public interface StatoTimerListener {
		public void cambioStatoToolbarTimer(int stato);
	}

	public TimerFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment.
	 *
	 * @return A new instance of fragment TimerFragment.
	 */
	public static TimerFragment newInstance() {
		return new TimerFragment();
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

	/*  Metodo invocato all'avvio o al rientro in fragment, permette la selezione del timer corretto
        recuperando i dati salvati del tempo già trascorso per avviare poi un nuovo timer.
    * */
	@Override
	public void onResume() {
		super.onResume();
		loadData();

		//startCountDownTimer e startCountDownPausa usano entrambi tempoRimasto e tempoFinale
		if( statoTimer.isCountDown() || statoTimer.isPausa()){
			aggiornaTimer();
			tempoRimasto = tempoFinale - System.currentTimeMillis();    //Calcolo tempo rimanente
			long milliSecondi = ((oreTimer*3600) +  (minutiTimer*60)) * 1000;
			tempoTrascorso = milliSecondi - tempoRimasto;	//se tempoRimasto negativo allora tempoTrascorso eccederà la durata effettiva del pomodoro

			if( tempoRimasto < 0){	//timer completato con fragment chiuso
				tempoTrascorso = milliSecondi;

				deleteTimer();
				if( statoTimer.isCountDown()) {
					addPomodoroCompletato();
					statoTimerPrecedente.setValue( StatoTimer.COUNTDOWN);
				}

				tempoRimasto = milliSecondi;
				statoTimer.setValue( StatoTimer.DISATTIVO);
				aggiornaButtonsAndToolbar();
				aggiornaTimer();

			}else if( statoTimer.isCountDown()){
				startCountDownTimer();

			}else if( statoTimer.isPausa()){
				startCountDownPausa();
			}

		}else if( statoTimer.isCountUp()){   //Gestione CountUpTimer

			//Se tempoTrascorso == 0 significa che è un nuovo timer, non è necessario calcolare il tempo trascorso con l'activity chiusa
			if(tempoTrascorso != 0){
				//tempoFinale = System.currentTimeMillis() nel momento della onPause()
				//Calcolo che considera il tempo trascorso con il fragment chiuso
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
			//tempoFinale non viene utilizzato da countUpTimer dato che non ha una fine, ma lo si
			//usa solo per salvare il momento di chiusura del fragment
			tempoFinale = System.currentTimeMillis();
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
		editor.putInt( STATO_TIMER, statoTimer.getValue());
		editor.putInt( STATO_TIMER_PRECEDENTE, statoTimerPrecedente.getValue());
		editor.putString( ACTIVITY_ASSOCIATA, sharedPreferences.getString(ACTIVITY_ASSOCIATA, "Nessuna attività"));
		editor.apply();
	}

	//Carica i dati dalle shared preferences
	private void loadData() {
		tempoIniziale = sharedPreferences.getLong( TEMPO_INIZIALE, 1800000);
		tempoRimasto = sharedPreferences.getLong( TEMPO_RIMASTO, tempoIniziale);
		tempoFinale = sharedPreferences.getLong( TEMPO_FINALE, 0);
		tempoTrascorso = sharedPreferences.getLong( TEMPO_TRASCORSO, 0);
		oreTimer = sharedPreferences.getInt( ORE_TIMER, 0);
		minutiTimer = sharedPreferences.getInt( MINUTI_TIMER, 30);
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
		if( statoTimer.isDisattivo() || statoTimerPrecedente.isDisattivo()){ //Ad inizio timer
			tempoIniziale = System.currentTimeMillis();
			tempoFinale = tempoIniziale + tempoRimasto;
		}
		statoTimer.setValue( StatoTimer.COUNTDOWN);
		aggiornaButtonsAndToolbar();

		//Oggetto countdown timer, con tempo rimanente alla fine timer, metodo onTick richiamato ogni 1000 millisecondi
		countDownTimer = new CountDownTimer( tempoRimasto, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tempoRimasto = millisUntilFinished;
				tempoTrascorso = tempoTrascorso + 1000;
				aggiornaTimer();
			}

			@Override
			public void onFinish() {
				stopTimer();
			}
		}.start();
	}

	//Avvia un countdown timer per una pausa, modificando dati salvati e alcune views
	private void startCountDownPausa(){
		stopTimer();

		//Recuperare durata pausa e reimpostare tempi
		long pausaMilliSecondi = 60000 * sharedPreferences.getInt(PAUSA, 5);
		tempoRimasto = pausaMilliSecondi;
		tempoFinale = System.currentTimeMillis() + tempoRimasto;
		tempoTrascorso = 0;     //Il countUpTimer dovrà ripartire da 0 dopo la pausa
		statoTimer.setValue( StatoTimer.PAUSA);
		saveData();
		aggiornaButtonsAndToolbar();


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
					long milliSecondi = ((oreTimer*3600) +  (minutiTimer*60)) * 1000;
					tempoRimasto = milliSecondi;
					aggiornaTimer();

				}else{	//countup
					tempoTrascorso = 0;
					aggiornaTimer();
				}
			}
		}.start();
	}

	//Avvia un countup timer
	private void startCountUpTimer() {
		if( statoTimer.isDisattivo() || statoTimerPrecedente.isDisattivo()){ //Ad inizio timer
			tempoIniziale = System.currentTimeMillis();
		}

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
			statoTimerListener.cambioStatoToolbarTimer( R.string.pomodoro_in_corso);

		}else if( statoTimer.isPausa()){
			btnPlay.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);
			btnPausa.setVisibility(View.GONE);
			statoTimerListener.cambioStatoToolbarTimer( R.string.pausa_in_corso);

		}else if( statoTimer.isDisattivo()){
			btnPlay.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.GONE);
			btnPausa.setVisibility(View.VISIBLE);
			statoTimerListener.cambioStatoToolbarTimer( R.string.pomodoro_disattivo);
		}
	}

	//Ferma timer
	private void stopTimer() {
		//Cancellare l'attuale timer
		deleteTimer();
		if( !statoTimer.isPausa()){
			addPomodoroCompletato();
			//statoTimerPrecedente mai settato a Pausa, per non perdere lo stato che avevo prima in modo da aggiornare l'interfaccia correttamente
			statoTimerPrecedente.setValue( statoTimer.getValue());
		}

		//Riottieni i dati per il countdown e si aggiorna l'interfaccia
		long milliSecondi = ((oreTimer*3600) +  (minutiTimer*60)) * 1000;

		tempoRimasto = milliSecondi;
		tempoTrascorso = 0;
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

	private boolean addPomodoroCompletato(){
		database = Database.getInstance( context);
		TablePomodoroModel pomodoro = new TablePomodoroModel();

		pomodoro.setName( sharedPreferences.getString(ACTIVITY_ASSOCIATA, "Nessuna attività"));
		pomodoro.setInizio( new Date( tempoIniziale));
		pomodoro.setDurata( tempoTrascorso);
		pomodoro.setColor( sharedPreferences.getInt(COLORE_ACTIVITY_ASSOCIATA, 0));

		return database.addCompletedPomodoro( pomodoro);
	}
}