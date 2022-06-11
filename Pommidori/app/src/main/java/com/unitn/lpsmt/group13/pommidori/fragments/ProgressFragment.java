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

import com.unitn.lpsmt.group13.pommidori.DayProgress;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.utils.ProgressAdapter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String counter;
	private String mParam2;
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
	 * this fragment using the provided parameters.
	 *
	 * @param counter Parameter 1.
	 * @return A new instance of fragment ProgressFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProgressFragment newInstance(int counter) {
		ProgressFragment fragment = new ProgressFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, counter);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			counter = getArguments().getString(ARG_PARAM1);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
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
		previous = view.findViewById(R.id.btn_previous_month);
		next = view.findViewById(R.id.btn_successive_month);
		meseAnno = view.findViewById(R.id.mese_anno);
		meseAnno.setText( ""+ selectedDate.getMonth()+" "+ selectedDate.getYear());
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
				meseAnno.setText( ""+ selectedDate.getMonth()+" "+ selectedDate.getYear());
				aggiornaGriglia();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedDate = selectedDate.plusMonths( 1);
				meseAnno.setText( ""+ selectedDate.getMonth()+" "+ selectedDate.getYear());
				aggiornaGriglia();
			}
		});
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void aggiornaGriglia(){
		LocalDate previousMonday = getPreviousMonday(selectedDate);
		listaObbiettivi = new ArrayList<DayProgress>();

		for(int i=0; i<ProgressAdapter.GRID_DAYS_CELLS; i++){
			listaObbiettivi.add( new DayProgress(
					ThreadLocalRandom.current().nextInt( 0, 101),
					ThreadLocalRandom.current().nextInt( 0, 101),
					previousMonday));
			previousMonday = previousMonday.plusDays(1);
		}

		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_view);
		layoutManager = new GridLayoutManager( view.getContext(), 7);
		recyclerView.setLayoutManager( layoutManager);

		adapter = new ProgressAdapter( view.getContext(), listaObbiettivi, selectedDate);
		recyclerView.setAdapter( adapter);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private LocalDate getPreviousMonday(LocalDate selectedDate){
		LocalDate returnPreviousMonday = LocalDate.of( selectedDate.getYear(), selectedDate.getMonth(), 1);
		if( !( returnPreviousMonday.getDayOfWeek().equals( DayOfWeek.MONDAY))){
			returnPreviousMonday = returnPreviousMonday.with(TemporalAdjusters.previous( DayOfWeek.MONDAY));
		}
		System.out.println("MainActivity getPreviousMonday selectedDate = "+selectedDate);
		System.out.println("MainActivity getPreviousMonday returnPreviousMonday = "+returnPreviousMonday);
		return returnPreviousMonday;
	}

	private void setHeaderDaysOfWeek(){
		TextView firstDayOfWeek = view.findViewById(R.id.firstDayOfWeek);
		TextView secondDayOfWeek = view.findViewById(R.id.secondDayOfWeek);
		TextView thirdDayOfWeek = view.findViewById(R.id.thirdDayOfWeek);
		TextView fourthDayOfWeek = view.findViewById(R.id.fourthDayOfWeek);
		TextView fifthDayOfWeek = view.findViewById(R.id.fifthDayOfWeek);
		TextView sixthDayOfWeek = view.findViewById(R.id.sixthDayOfWeek);
		TextView seventhDayOfWeek = view.findViewById(R.id.seventhDayOfWeek);

		String dayOfWeek[] = getResources().getStringArray(R.array.daysOfWeekAbbreviated_En);

		firstDayOfWeek.setText( dayOfWeek[0]);
		secondDayOfWeek.setText( dayOfWeek[1]);
		thirdDayOfWeek.setText( dayOfWeek[2]);
		fourthDayOfWeek.setText( dayOfWeek[3]);
		fifthDayOfWeek.setText( dayOfWeek[4]);
		sixthDayOfWeek.setText( dayOfWeek[5]);
		seventhDayOfWeek.setText( dayOfWeek[6]);
	}
}