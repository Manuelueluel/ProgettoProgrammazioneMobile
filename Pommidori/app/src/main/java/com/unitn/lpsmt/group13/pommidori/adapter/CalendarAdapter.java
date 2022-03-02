package com.unitn.lpsmt.group13.pommidori.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unitn.lpsmt.group13.pommidori.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarAdapter  extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.13333333);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = daysOfMonth.get(position);
        if(date==null){
            holder.dayOfMonth.setText("");
        }else{
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.getDayOfWeek()== DayOfWeek.SUNDAY){
                holder.parentView.setBackgroundColor(Color.parseColor("#dddddd"));
            }
            if(date.isEqual(LocalDate.now())){
                holder.dayOfMonth.setTextColor(Color.parseColor("#FF0000"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener{
        void OnItemClick(int position, String dayText);
    }
}
