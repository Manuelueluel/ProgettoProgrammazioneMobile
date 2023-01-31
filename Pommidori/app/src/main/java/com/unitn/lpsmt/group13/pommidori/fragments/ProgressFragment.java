package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.DayProgress;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.utils.ProgressAdapter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

	private LocalDate selectedDate;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private Database database;

	private View view;
	private Button previous;
	private Button next;
	private TextView meseAnno;

	public ProgressFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment.
	 *
	 * @return A new instance of fragment ProgressFragment.
	 */
	public static ProgressFragment newInstance() {
		return new ProgressFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_progress, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.view = view;
		selectedDate = LocalDate.now();
		previous = view.findViewById(R.id.btn_previous);
		next = view.findViewById(R.id.btn_next);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_view);
		layoutManager = new GridLayoutManager( view.getContext(), 7);
		recyclerView.setLayoutManager( layoutManager);
		meseAnno = view.findViewById(R.id.text_view_time_interval);
		meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
		setHeaderDaysOfWeek();
		setButtonsListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		database = Database.getInstance( this.getContext());
		updateGrid();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void setButtonsListeners(){
		previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedDate = selectedDate.minusMonths( 1);
				meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
				updateGrid();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedDate = selectedDate.plusMonths( 1);
				meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
				updateGrid();
			}
		});
	}

	private void updateGrid(){
		List<TableSessionProgModel> sessioniProgrammate = database.getAllProgrammedSessionsByMonth( selectedDate);
		List<TablePomodoroModel> pomodoroCompletati = database.getPomodorosByMonth( selectedDate);
		ArrayList<DayProgress> monthlyProgress = new ArrayList<>( selectedDate.getMonth().length( selectedDate.isLeapYear()));
		ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset( selectedDate.atStartOfDay());
		int startIntervalOfSelectedMonth;
		int endIntervalOfSelectedMonth;

		//Identificare se il primo del mese selezionato è di lunedi, se non lo è usare l'ultimo lunedi del mese precedente
		LocalDate monday = Utility.getFirstDayOfMonth( selectedDate);
		if( !( monday.getDayOfWeek().equals( DayOfWeek.MONDAY))){
			monday = Utility.getPreviousMonday( monday);
		}

		//Se monday non è il primo lunedì del mese selezionato, allora sarà l'ultimo lunedì del mese precedente
		if( !(monday.getDayOfMonth() == 1)){
			startIntervalOfSelectedMonth = monday.getMonth().length( monday.isLeapYear()) - monday.getDayOfMonth() + 1;
		}else{
			startIntervalOfSelectedMonth = 0;
		}

		endIntervalOfSelectedMonth = startIntervalOfSelectedMonth + selectedDate.getMonth().length( selectedDate.isLeapYear());

		//Inizializzazione monthlyProgress
		//Primi x giorni nella griglia che non appartengono al mese selezionato
		for(int i=0; i<startIntervalOfSelectedMonth; i++){
			monthlyProgress.add( new DayProgress(0, 0, monday.plusDays(i)));
		}

		//Giorni del mese selezionato e i loro progressi
		for(int i=1; i<=selectedDate.getMonth().length( selectedDate.isLeapYear()); i++){
			monthlyProgress.add( new DayProgress(0, 0, LocalDate.now().withDayOfMonth(i)));
		}

		//Ultimi x giorni nella griglia che non appartengono al mese selezionato
		for(int i=endIntervalOfSelectedMonth; i<ProgressAdapter.GRID_DAYS_CELLS; i++){
			monthlyProgress.add( new DayProgress(0, 0, monday.plusDays(i)));
		}

		//Calcolo obbiettivo giornaliero di studio, sommando la durata delle varie sessioni di studio programmate
		sessioniProgrammate.forEach( sessione -> {
			int i = startIntervalOfSelectedMonth + sessione.getOraInizio().toInstant().atZone( zoneOffset).getDayOfMonth()-1;
			monthlyProgress.get(i).setObjective(
					monthlyProgress.get(i).getObjective()
							+(int) (sessione.getOraFine().toInstant().toEpochMilli() - sessione.getOraInizio().toInstant().toEpochMilli()));
			//TODO la sessione programmata può non avere una oraFine
		});

		//Calcolo progressi effettuati, sommando i vari pomodoro di ogni giornata
		pomodoroCompletati.forEach( pomodoro -> {
			int i = startIntervalOfSelectedMonth + pomodoro.getInizio().toInstant().atZone( zoneOffset).getDayOfMonth()-1;
			monthlyProgress.get(i).setProgress( monthlyProgress.get(i).getProgress()+(int) (pomodoro.getDurata()));
		});

		adapter = new ProgressAdapter( view.getContext(), monthlyProgress, startIntervalOfSelectedMonth, endIntervalOfSelectedMonth);
		recyclerView.setAdapter( adapter);
	}

	private void setHeaderDaysOfWeek(){
		TextView firstDayOfWeek = view.findViewById(R.id.firstDayOfWeek);
		TextView secondDayOfWeek = view.findViewById(R.id.secondDayOfWeek);
		TextView thirdDayOfWeek = view.findViewById(R.id.thirdDayOfWeek);
		TextView fourthDayOfWeek = view.findViewById(R.id.fourthDayOfWeek);
		TextView fifthDayOfWeek = view.findViewById(R.id.fifthDayOfWeek);
		TextView sixthDayOfWeek = view.findViewById(R.id.sixthDayOfWeek);
		TextView seventhDayOfWeek = view.findViewById(R.id.seventhDayOfWeek);

		firstDayOfWeek.setText( Utility.capitalize(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		secondDayOfWeek.setText( Utility.capitalize(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		thirdDayOfWeek.setText( Utility.capitalize(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		fourthDayOfWeek.setText( Utility.capitalize(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		fifthDayOfWeek.setText( Utility.capitalize(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		sixthDayOfWeek.setText( Utility.capitalize(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
		seventhDayOfWeek.setText( Utility.capitalize(DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0, 3)));
	}
}