package com.unitn.lpsmt.group13.pommidori.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PieChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartFragment extends Fragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private int counter;
	private String mParam2;

	private PieChart pieChart;
	private RadioButton week;
	private RadioButton month;
	private RadioButton year;
	private RadioGroup radioGroup;
	private Button next;
	private Button previous;
	private TextView textViewTimeInterval;
	private LocalDate selectedDate;
	private List<TablePomodoroModel> pomodoroList;
	private Database database;


	public PieChartFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param counter Parameter 1.
	 * @return A new instance of fragment PieChartFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static PieChartFragment newInstance(int counter) {
		PieChartFragment fragment = new PieChartFragment();
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
			counter = getArguments().getInt(ARG_PARAM1);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_pie_chart, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		week = view.findViewById(R.id.radioButton_week);
		month = view.findViewById(R.id.radioButton_month);
		year = view.findViewById(R.id.radioButton_year);
		radioGroup = view.findViewById(R.id.radioGroup);
		next = view.findViewById(R.id.btn_next);
		previous = view.findViewById(R.id.btn_previous);
		textViewTimeInterval = view.findViewById(R.id.text_view_time_interval);
		pieChart = view.findViewById(R.id.pie_chart);

		setupPieChart();
		//loadPieChartData();
		setButtonListeners();

		//TODO selezione intervallo di tempo da considerare settimana, mese, anno e la selezione tra essi
	}

	@Override
	public void onResume() {
		super.onResume();
		database = Database.getInstance( getContext());
		selectedDate = LocalDate.now();
		radioGroup.check(R.id.radioButton_week);	//Selezionato di default
		updateTimeInterval();
		pomodoroList = database.getPomodoroByWeek( selectedDate);
		load();
	}

	private void setButtonListeners(){
		week.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Selezionare tutti i pomodoro della settimana, intesa come lunedi-domenica
				//timeInterval mostra la settimana selezionata
				updateTimeInterval();
				pomodoroList = database.getPomodoroByWeek( selectedDate);
				load();
			}
		});

		month.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTimeInterval();
				pomodoroList = database.getPomodoroByMonth( selectedDate);
				load();
			}
		});

		year.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTimeInterval();
				pomodoroList = database.getPomodoroByYear( selectedDate);
				load();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//aggiorna selectedDate e updateTimerInterval
				switch ( radioGroup.getCheckedRadioButtonId() ){
					case R.id.radioButton_week:
						selectedDate = selectedDate.plusWeeks(1);
						pomodoroList = database.getPomodoroByWeek( selectedDate);
						break;
					case R.id.radioButton_month:
						selectedDate = selectedDate.plusMonths(1);
						pomodoroList = database.getPomodoroByMonth( selectedDate);
						break;
					case R.id.radioButton_year:
						selectedDate = selectedDate.plusYears(1);
						pomodoroList = database.getPomodoroByYear( selectedDate);
						break;
					default:
						break;
				}
				updateTimeInterval();
				load();
			}
		});

		previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//aggiorna selectedDate e updateTimerInterval
				switch ( radioGroup.getCheckedRadioButtonId()){
					case R.id.radioButton_week:
						selectedDate = selectedDate.minusWeeks(1);
						pomodoroList = database.getPomodoroByWeek( selectedDate);
						break;
					case R.id.radioButton_month:
						selectedDate = selectedDate.minusMonths(1);
						pomodoroList = database.getPomodoroByMonth( selectedDate);
						break;
					case R.id.radioButton_year:
						selectedDate = selectedDate.minusYears(1);
						pomodoroList = database.getPomodoroByYear( selectedDate);
						break;
					default:
						break;
				}
				updateTimeInterval();
				load();
			}
		});
	}

	private void updateTimeInterval(){
		switch ( radioGroup.getCheckedRadioButtonId()){
			case R.id.radioButton_week:
				LocalDate firstDayOfWeek = selectedDate.with( TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
				LocalDate lastDayOfWeek = firstDayOfWeek.plus( Period.ofDays(6));
				String timeInterval = firstDayOfWeek.getDayOfMonth()
						+ " " + Utility.capitalize( firstDayOfWeek.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()).substring(0, 3))
						+ " - " + lastDayOfWeek.getDayOfMonth()
						+ " "+ Utility.capitalize( lastDayOfWeek.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault()).substring(0, 3));
				textViewTimeInterval.setText( timeInterval);
				break;
			case R.id.radioButton_month:
				textViewTimeInterval.setText( Utility.capitalize( selectedDate.getMonth().getDisplayName( TextStyle.FULL, Locale.getDefault())));
				break;
			case R.id.radioButton_year:
				textViewTimeInterval.setText( Integer.toString( selectedDate.getYear()));
				break;
			default:
				break;
		}
	}

	private void setupPieChart(){
		pieChart.setDrawHoleEnabled(true);
		pieChart.setUsePercentValues(true);
		pieChart.setEntryLabelTextSize(14);
		//pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
		pieChart.setEntryLabelColor(Color.BLACK);
		//pieChart.setCenterText("Totale");
		pieChart.setCenterTextSize(24);
		pieChart.getDescription().setEnabled(false);
		pieChart.setRotationEnabled(false);
		pieChart.setTouchEnabled(false);


		Legend l = pieChart.getLegend();
		l.setVerticalAlignment( Legend.LegendVerticalAlignment.BOTTOM);
		l.setHorizontalAlignment( Legend.LegendHorizontalAlignment.LEFT);
		l.setOrientation( Legend.LegendOrientation.HORIZONTAL);
		l.setTextSize(12f);
		l.setDrawInside(false);
		l.setEnabled(true);

	}

	private void loadPieChartData(){
		ArrayList<PieEntry> entries = new ArrayList<>();
		entries.add( new PieEntry(0.2f, "Food & Dining"));
		entries.add( new PieEntry(0.15f, "Medical"));
		entries.add( new PieEntry(0.10f, "Entertainment"));
		entries.add( new PieEntry(0.25f, "Electricity & gas"));
		entries.add( new PieEntry(0.3f, "Housing"));

		ArrayList<Integer> colors = new ArrayList<>();
		for(int color: ColorTemplate.MATERIAL_COLORS){
			colors.add( color);
		}

		for(int color: ColorTemplate.VORDIPLOM_COLORS){
			colors.add( color);
		}

		PieDataSet dataSet = new PieDataSet(entries, "Expense category");
		dataSet.setColors( colors);

		PieData data = new PieData(dataSet);
		data.setDrawValues(true);
		data.setValueFormatter( new PercentFormatter(pieChart));
		data.setValueTextSize(12f);
		data.setValueTextColor(Color.BLACK);

		pieChart.setData( data);
		pieChart.invalidate();

		pieChart.animateY(1400, Easing.EaseInOutBack);
	}

	private void load(){
		LinkedHashMap<String, Long> activityDuration = new LinkedHashMap<>();

		/*	TODO problema colori: provare con una LinkedHashMap dove l'ordine di inserimento è quello di loop
		 	usare una lista a parte per inserire i colori, quindi allo stesso indice dovrebbe corrispondere
		 	activity con colore
		 */


		//Ricavare dalla pomodoroList le activity, e per ognuna di esse sommare i propri pomodoro
		pomodoroList.forEach(pomodoro -> {
			try {
				if (activityDuration.containsKey(pomodoro.getName())) {
					activityDuration.put(pomodoro.getName(), (pomodoro.getDurata() + activityDuration.get(pomodoro.getName())));
				}else{
					activityDuration.put( pomodoro.getName(), pomodoro.getDurata());
				}
			}catch (NullPointerException ex){
				ex.getStackTrace();
			}
		});

		ArrayList<Integer> col = new ArrayList<>();
		for(int i=0; i<activityDuration.size(); i++){
			col.add( pomodoroList.get(i).getColor());
		}
		System.out.println(activityDuration);
		System.out.println(col);



		/*
		ArrayList<Integer> colors = new ArrayList<>();
		for(int color: ColorTemplate.MATERIAL_COLORS){
			colors.add( color);
		}

		for(int color: ColorTemplate.VORDIPLOM_COLORS){
			colors.add( color);
		}*/

		long sum = 0;
		ArrayList<PieEntry> entries = new ArrayList<>();
		for( Map.Entry<String, Long> item : activityDuration.entrySet()){
			sum = sum + item.getValue();
			entries.add( new PieEntry( item.getValue(), item.getKey()));
		}

		PieDataSet dataSet = new PieDataSet( entries,"");//"Pomodoros by activity"
		dataSet.setColors( col);
		pieChart.setCenterText( millisToHoursAndMinutes( sum));

		PieData data = new PieData(dataSet);
		data.setDrawValues(true);
		data.setValueFormatter( new PercentFormatter(pieChart));
		data.setValueTextSize(12f);
		data.setValueTextColor(Color.BLACK);

		pieChart.setData( data);
		pieChart.invalidate();
	}

	private String millisToHoursAndMinutes(long milliseconds){
		return ((int) (milliseconds / 1000) / 3600) + "h " + (((int) (milliseconds / 1000) % 3600) / 60) + "m";
	}
}