package com.unitn.lpsmt.group13.pommidori;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;

public class CalendarEvent {

    public static final int TYPE_INVALID = -1;
    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_SESSION = 1;

    private int id;
    private String activityName;
    private int color;
    private long startTimeInMillis;
    private long endTimeInMillis;
    private int type;

    public CalendarEvent(int id, String activityName, int color, long startTimeInMillis, long endTimeInMillis, int type) {
        this.id = id;
        this.activityName = activityName;
        this.color = color;
        this.startTimeInMillis = startTimeInMillis;
        this.endTimeInMillis = endTimeInMillis;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(long endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
    }

    public int getType(){ return type; }

    public void setType( int type){ this.type = type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarEvent that = (CalendarEvent) o;
        return id == that.id && color == that.color && startTimeInMillis == that.startTimeInMillis && endTimeInMillis == that.endTimeInMillis && activityName.equals(that.activityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activityName, color, startTimeInMillis, endTimeInMillis);
    }

    @NonNull
    @Override
    public String toString() {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimeInMillis), ZoneId.systemDefault());

        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        int hour = date.getHour();
        int minute = date.getMinute();

        String str = day + "-" + month + "-" + year + " "+hour + ":" + minute;
        date = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimeInMillis), ZoneId.systemDefault());
        hour = date.getHour();
        minute = date.getMinute();

        str = str + "/" + hour + ":" + minute;
        return activityName + " " + str;
    }

    public String startTimeToString(){
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimeInMillis), ZoneId.systemDefault());
        int hour = date.getHour();
        int min = date.getMinute();
        return (hour < 10? "0"+hour : hour) + ":" + (min < 10? "0"+min : min);
    }

    public String endTimeToString(){
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimeInMillis), ZoneId.systemDefault());
        int hour = date.getHour();
        int min = date.getMinute();
        return (hour < 10? "0"+hour : hour) + ":" + (min < 10? "0"+min : min);
    }
}




















