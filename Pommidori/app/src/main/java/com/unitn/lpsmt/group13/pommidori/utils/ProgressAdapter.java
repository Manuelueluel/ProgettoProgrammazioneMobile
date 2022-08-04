package com.unitn.lpsmt.group13.pommidori.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unitn.lpsmt.group13.pommidori.DayProgress;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.DayProgressHolder>{

	public static final int GRID_DAYS_CELLS = 42;

	private Context context;
	private ArrayList<DayProgress> list;
	private int startIntervalOfSelectedMonth;
	private int endIntervalOfSelectedMonth;


	public class DayProgressHolder extends RecyclerView.ViewHolder{
		private ProgressBar progressBar;
		private TextView dayNumber;

		public DayProgressHolder(View itemView){
			super(itemView);
			progressBar = itemView.findViewById(R.id.progressBar);
			dayNumber = itemView.findViewById(R.id.day_number);
		}
	}

	public ProgressAdapter(Context context, ArrayList<DayProgress> list, int startInterval, int endInterval){
		this.context = context;
		this.list = list;
		this.startIntervalOfSelectedMonth = startInterval;
		this.endIntervalOfSelectedMonth = endInterval;
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
		System.out.println(
				"day "+list.get( position).getDay()
						+" progress "+list.get( position).getProgress()
						+" objective "+list.get( position).getObjective());

		//Se è del mese selezionato, seleziono la view e setto onClick, altrimenti deseleziono la view e la coloro di grigio
		if( (position >= startIntervalOfSelectedMonth) && (position < endIntervalOfSelectedMonth)){
			holder.itemView.setSelected(true);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(context, "Completamento obiettivo: "+ progress, Toast.LENGTH_LONG).show();
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

}
