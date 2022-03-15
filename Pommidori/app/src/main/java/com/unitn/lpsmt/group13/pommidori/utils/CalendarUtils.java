package com.unitn.lpsmt.group13.pommidori.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarUtils {

    public static LocalDate selectDate;

    public static String monthYearFormatDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALY);
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate date) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate currrent = mondayForDate(date);
        LocalDate endDate = currrent.plusWeeks(1);

        while (currrent.isBefore(endDate)){
            days.add(currrent);
            currrent = currrent.plusDays(1);
        }

        return days;
    }

    private static LocalDate mondayForDate(LocalDate current){
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while(current.isAfter(oneWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.MONDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }
}
