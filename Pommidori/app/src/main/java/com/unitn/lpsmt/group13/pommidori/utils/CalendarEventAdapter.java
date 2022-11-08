package com.unitn.lpsmt.group13.pommidori.utils;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.unitn.lpsmt.group13.pommidori.CalendarEvent;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.fragments.ModifyActivityFragment;
import com.unitn.lpsmt.group13.pommidori.fragments.ModifySessionFragment;

import java.util.ArrayList;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.CalendarEventHolder>{

    private ArrayList<Event> list;
    private FragmentManager fragmentManager;
    private CalendarEvent calendarEvent;
    private int type;

    public CalendarEventAdapter(ArrayList<Event> list, FragmentManager fragmentManager){
        this.list = list;
        this.fragmentManager = fragmentManager;
    }

    public class CalendarEventHolder extends RecyclerView.ViewHolder{
        private TextView activityName;
        private TextView startTime;
        private TextView endTime;
        private ImageView modify;

        public CalendarEventHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activityName_calendar_event);
            startTime = itemView.findViewById(R.id.startTime_calendar_event);
            endTime = itemView.findViewById(R.id.endTime_calendar_event);
            modify = itemView.findViewById(R.id.calendar_event_modify);
        }
    }

    @NonNull
    @Override
    public CalendarEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CalendarEventHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventHolder holder, int position) {
        calendarEvent = (CalendarEvent) list.get(position).getData();
        type = calendarEvent.getType();

        holder.activityName.setText( calendarEvent.getActivityName());
        holder.startTime.setText( calendarEvent.startTimeToString());

        if( type == CalendarEvent.TYPE_SESSION){
            holder.endTime.setText( calendarEvent.endTimeToString());
        }else if( type == CalendarEvent.TYPE_ACTIVITY){
            holder.activityName.setTypeface(null, Typeface.BOLD);
            holder.endTime.setText("");
        }

        holder.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type){
                    case CalendarEvent.TYPE_ACTIVITY:
                        ModifyActivityFragment fragmentActivity = ModifyActivityFragment.newInstance( calendarEvent.getId());
                        fragmentActivity.show( fragmentManager, "Modify activity");
                        break;
                    case CalendarEvent.TYPE_SESSION:
                        ModifySessionFragment fragmentSession = ModifySessionFragment.newInstance( calendarEvent.getId());
                        fragmentSession.show( fragmentManager, "Modify Session");
                        break;
                    case CalendarEvent.TYPE_INVALID:
                    default:break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
