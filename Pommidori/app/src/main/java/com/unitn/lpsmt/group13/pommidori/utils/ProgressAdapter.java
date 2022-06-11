package com.unitn.lpsmt.group13.pommidori.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.unitn.lpsmt.group13.pommidori.DayProgress;
import com.unitn.lpsmt.group13.pommidori.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.DayProgressHolder>{

	public static final int GRID_DAYS_CELLS = 42;

	private Context context;
	private ArrayList<DayProgress> list;
	private LocalDate selectedDate;
	private LocalDate monday;


	public class DayProgressHolder extends RecyclerView.ViewHolder{
		private ProgressBar progressBar;
		private TextView dayNumber;

		public DayProgressHolder(View itemView){
			super(itemView);
			progressBar = itemView.findViewById(R.id.progressBar);
			dayNumber = itemView.findViewById(R.id.day_number);
		}
	}

	public ProgressAdapter(Context context, ArrayList<DayProgress> list, LocalDate selectedDate){
		this.context = context;
		this.list = list;
		this.selectedDate = selectedDate;
		this.monday = getFirstDayOfMonth( selectedDate);
		//Se il primo del mese è di lunedì, tengo quello, altrimenti cerco il lunedì precedente al primo del mese
		if( !( monday.equals( DayOfWeek.MONDAY))){
			monday = getPreviousMonday( selectedDate);
		}
	}

	@NonNull
	@Override
	public DayProgressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new DayProgressHolder( LayoutInflater.from( parent.getContext()).inflate( R.layout.daily_progress_bar, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull DayProgressHolder holder, int position) {
		int progress = list.get( position).getProgress();

		holder.dayNumber.setText( Integer.toString( list.get( position).getDay().getDayOfMonth()));
		holder.progressBar.setProgress( list.get( position).getProgress());

		int inizioIntervalloMese = 0;
		//Se monday non è il primo lunedì del mese
		if( !(monday.getDayOfMonth() == 1)){
			inizioIntervalloMese = monday.getMonth().length( monday.isLeapYear()) - monday.getDayOfMonth() + 1;
		}

		int fineIntervalloMese = inizioIntervalloMese + selectedDate.getMonth().length( selectedDate.isLeapYear());

		//Se è del mese selezionato, seleziono la view e setto onClick, altrimenti deseleziono la view e la coloro di grigio
		if( (position >= inizioIntervalloMese) && (position < fineIntervalloMese)){
			holder.itemView.setSelected(true);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(context, "Completamento obiettivo: "+progress, Toast.LENGTH_LONG).show();
				}
			});
		}else{
			holder.itemView.setSelected(false);
			holder.dayNumber.setTextColor(Color.LTGRAY);
		}
	}

	@Override
	public int getItemCount() {
		return GRID_DAYS_CELLS;
	}

	public String getDayOfWeek( int position){
		String arr[] = context.getResources().getStringArray( R.array.daysOfWeekAbbreviated_En);
		if( position<7) return arr[position];
		return null;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private LocalDate getPreviousMonday(LocalDate selectedDate){
		return selectedDate.with(TemporalAdjusters.previous( DayOfWeek.MONDAY));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private LocalDate getFirstDayOfMonth(LocalDate selectedDate){
		return LocalDate.of( selectedDate.getYear(), selectedDate.getMonth(), 1);
	}


}
