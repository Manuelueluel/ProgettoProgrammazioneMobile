package com.unitn.lpsmt.group13.pommidori.adapter;

import android.view.View;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final View parentView;
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.calendar_day);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemListener.OnItemClick(getAdapterPosition(),(String) dayOfMonth.getText());
    }
}
