package com.unitn.lpsmt.group13.pommidori.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.unitn.lpsmt.group13.pommidori.R;

import java.util.ArrayList;

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
	private String counter;
	private String mParam2;
	private PieChart pieChart;

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
			counter = getArguments().getString(ARG_PARAM1);
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

		pieChart = view.findViewById(R.id.pie_chart);
		setupPieChart();
		loadPieChartData();
	}

	private void setupPieChart(){
		pieChart.setDrawHoleEnabled(true);
		pieChart.setUsePercentValues(true);
		pieChart.setEntryLabelTextSize(12);
		pieChart.setEntryLabelColor(Color.BLACK);
		pieChart.setCenterText("Spendig by category");
		pieChart.setCenterTextSize(24);
		pieChart.getDescription().setEnabled(false);

		Legend l = pieChart.getLegend();
		l.setVerticalAlignment( Legend.LegendVerticalAlignment.TOP);
		l.setHorizontalAlignment( Legend.LegendHorizontalAlignment.RIGHT);
		l.setOrientation( Legend.LegendOrientation.VERTICAL);
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
}