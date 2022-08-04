package com.unitn.lpsmt.group13.pommidori;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class Utility {

	//Shared preferences per gestione timer
	public static final String SHARED_PREFS_TIMER = "sharedPreferencesTimer";
	public static final String ORE_TIMER = "ore";
	public static final String MINUTI_TIMER = "minuti";
	public static final String TEMPO_INIZIALE = "tempoIniziale";
	public static final String TEMPO_RIMASTO = "tempoRimasto";
	public static final String TEMPO_FINALE = "tempoFinale";
	public static final String TEMPO_TRASCORSO = "tempoTrascorso";
	public static final String STATO_TIMER = "statoTimer";
	public static final String STATO_TIMER_PRECEDENTE = "statoTimerPrecedente";
	public static final String PAUSA = "pausa";
	public static final String NOME_ACTIVITY_ASSOCIATA = "nomeActivityAssociata";
	public static final String COLORE_ACTIVITY_ASSOCIATA = "coloreActivityAssociata";

	//Costanti
	public static final long DURATA_MASSIMA_COUNTUP_TIMER = 86400000;	//Usato per CountUpTimer, corrisponde a 24 ore


	public static String capitalize( String str){
		if( !(str == null || str.isEmpty())){
			str = str.substring(0,1).toUpperCase() + str.substring(1);
		}
		return str;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static LocalDate getPreviousMonday(LocalDate selectedDate){
		return selectedDate.with(TemporalAdjusters.previous( DayOfWeek.MONDAY));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static LocalDate getFirstDayOfMonth(LocalDate selectedDate){
		return LocalDate.of( selectedDate.getYear(), selectedDate.getMonth(), 1);
	}

	public static String millisToHoursAndMinutes(long milliseconds){
		return ((int) (milliseconds / 1000) / 3600) + "h " + (((int) (milliseconds / 1000) % 3600) / 60) + "m";
	}
}

