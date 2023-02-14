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

import com.unitn.lpsmt.group13.pommidori.DailyProgress;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;

import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.DayProgressHolder>{

	public static final int GRID_DAYS_CELLS = 42;

	private Context context;
	private ArrayList<DailyProgress> list;
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

	public ProgressAdapter(Context context, ArrayList<DailyProgress> list, int startInterval, int endInterval){
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
		int progress = 0;
		String str;
		if( list.get(position).getObjective() != 0){
			float pro = list.get( position).getProgress();
			float obj = list.get(position).getObjective();
			progress = (int) ((pro / obj) * 100.0);

			str = context.getResources().getString(R.string.objective_toast)+Utility.millisToHoursAndMinutes(list.get(position).getObjective())
					+"\n"+context.getResources().getString(R.string.progress_toast)+Utility.millisToHoursAndMinutes(list.get(position).getProgress());
		}else{
			progress = list.get(position).getProgress();
			str = context.getResources().getString(R.string.progress_toast)+Utility.millisToHoursAndMinutes(list.get(position).getProgress());
		}

		holder.dayNumber.setText( Integer.toString( list.get( position).getDay().getDayOfMonth()));
		holder.progressBar.setProgress( progress);

		//Se è del mese selezionato, seleziono la view e setto onClick, altrimenti deseleziono la view e la coloro di grigio
		if( (position >= startIntervalOfSelectedMonth) && (position < endIntervalOfSelectedMonth)){
			holder.itemView.setSelected(true);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(context, str, Toast.LENGTH_LONG).show();
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
