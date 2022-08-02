package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.TextKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.DayProgress;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.utils.ProgressAdapter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

	private LocalDate selectedDate;
	private ArrayList<DayProgress> listaObbiettivi;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
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
		meseAnno = view.findViewById(R.id.text_view_time_interval);
		meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
		setHeaderDaysOfWeek();
		setButtons();
		aggiornaGriglia();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void setButtons(){
		previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedDate = selectedDate.minusMonths( 1);
				meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
				aggiornaGriglia();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedDate = selectedDate.plusMonths( 1);
				meseAnno.setText( Utility.capitalize(selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()) + " " + selectedDate.getYear()));
				aggiornaGriglia();
			}
		});
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void aggiornaGriglia(){
		LocalDate monday = getFirstDayOfMonth(selectedDate);
		if( !( monday.getDayOfWeek().equals( DayOfWeek.MONDAY))){
			monday = getPreviousMonday( monday);
		}

		listaObbiettivi = new ArrayList<DayProgress>();

		for(int i=0; i<ProgressAdapter.GRID_DAYS_CELLS; i++){
			listaObbiettivi.add( new DayProgress(
					ThreadLocalRandom.current().nextInt( 0, 101),
					ThreadLocalRandom.current().nextInt( 0, 101),
					monday));
			monday = monday.plusDays(1);
		}

		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_view);
		layoutManager = new GridLayoutManager( view.getContext(), 7);
		recyclerView.setLayoutManager( layoutManager);

		adapter = new ProgressAdapter( view.getContext(), listaObbiettivi, selectedDate);
		recyclerView.setAdapter( adapter);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private LocalDate getPreviousMonday(LocalDate selectedDate){
		return selectedDate.with(TemporalAdjusters.previous( DayOfWeek.MONDAY));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private LocalDate getFirstDayOfMonth(LocalDate selectedDate){
		return LocalDate.of( selectedDate.getYear(), selectedDate.getMonth(), 1);
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