package com.unitn.lpsmt.group13.pommidori.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Rating;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingHolder> {
	private ArrayList<Rating> list;

	public class RatingHolder extends RecyclerView.ViewHolder{
		private TextView activityName;
		private RatingBar ratingBar;

		public RatingHolder(View itemView){
			super(itemView);
			activityName = itemView.findViewById(R.id.activity_name);
			ratingBar = itemView.findViewById(R.id.ratingBar);
		}
	}

	public RatingAdapter( ArrayList<Rating> list) {
		this.list = list;
	}

	@NonNull
	@Override
	public RatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new RatingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_card, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull RatingHolder holder, int position) {
		float rating = list.get( position).getRating();
		String activityName = list.get( position).getActivityName();

		holder.ratingBar.setRating( rating);
		holder.activityName.setText( activityName);
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}
