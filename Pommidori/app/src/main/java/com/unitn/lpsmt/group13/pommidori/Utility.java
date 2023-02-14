package com.unitn.lpsmt.group13.pommidori;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utility {

	private static final String TAG = "Utility";

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
	public static final String DAILY_PROGRESS_OBJECTIVE = "daily_progress_objective";
	public static final String NOME_ACTIVITY_ASSOCIATA = "nomeActivityAssociata";
	public static final String COLORE_ACTIVITY_ASSOCIATA = "coloreActivityAssociata";
	public static final String ACCELEROMETER = "accelerometer";

	//Intent broadcast receivers
	public static final String TIME_MILLIS = "TIME_MILLIS";
	public static final String TIMER_ACTION_INTENT = "TIMER_ACTION_INTENT";	//Broadcast action per timer
	public static final String TOOLBAR_BUTTONS_STATO_TIMER = "TOOLBAR_BUTTONS_STATO_TIMER";
	public static final String TOOLBAR_BUTTONS_ACTION_INTENT = "TOOLBAR_BUTTONS_ACTION_INTENT";
	public static final String END_OF_PAUSA_INTENT = "END_OF_PAUSA_INTENT";
	public static final String END_OF_PAUSA_TIMER = "END_OF_PAUSA_TIMER";

	//Notifications
	public static final int ONGOING_COUNTDOWN_NOTIFICATION_ID = 1;
	public static final int ONGOING_COUNTUP_NOTIFICATION_ID = 2;
	public static final int ONGOING_PAUSA_NOTIFICATION_ID = 3;
	public static final String TIMER_CHANNEL_ID = "TIMER_CHANNEL_ID";
	public static final int REMINDER_NOTIFICATION_ID = 10;
	public static final String REMINDER_CHANNEL_ID = "REMINDER_CHANNEL_ID";
	public static final String REMINDER_ACTIVITY_INTENT = "REMINDER_ACTIVITY_INTENT";
	public static final String REMINDER_START_HOUR_INTENT = "REMINDER_START_HOUR_INTENT";
	private static int pendingIntentRequestCode = 0;

	//Permission codes
	public static final int POST_NOTIFICATIONS_PERMISSION_CODE = 101;
	public static final int USE_EXACT_ALARM_PERMISSION_CODE = 102;

	//Rating threshold
	public static final float BONUS_THRESHOLD = 0.8F;
	public static final float MALUS_THRESHOLD = 0.5F;
	public static final float BONUS = 1.1F;
	public static final float MALUS = 0.9F;
	public static final float TRIGGERS_WEIGHT = 0.5F;
	public static final float N_STARS = 5.0F;

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

	//Crea il channel e lo associa al notification manager se la versione di API >= 26
	public static void createNotificationChannel(Context context, String channelID, String channelName, String desc) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
			channel.setDescription(desc);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel( channel);
		}
	}

	public static int getPendingIntentRequestCode(){
		if( pendingIntentRequestCode == Integer.MAX_VALUE){
			pendingIntentRequestCode = Integer.MIN_VALUE;
		}
		return ++pendingIntentRequestCode;
	}

	public static List<Rating> calculateRatings( Context context){
		Database database = Database.getInstance( context);
		List<TableActivityModel> activities = database.getAllActivities();
		List<Rating> ratings = new ArrayList<>();

		activities.forEach( activity -> {
			Rating rat = new Rating( calculateRatingByActivity( database, activity.getName()), activity.getName());
			ratings.add( rat);
		});

		return ratings;
	}

	private static float calculateRatingByActivity( Database database, String activityName) {
		List<TablePomodoroModel> pomoList = database.getAllPomodorosByActivity( activityName);
		TableActivityModel activity = database.getActivity( activityName);
		List<TableSessionProgModel> sessionList = database.getAllPastProgrammedSessionsByActivity( activity);
		float rating = 0F;
		float pomoTotali = (float) pomoList.size();

		if( !pomoList.isEmpty() && !sessionList.isEmpty()){
			Log.d(TAG, "calculateRatingByActivity pomo e sessione");
			//Ordino le liste per tempo d'inizio
			Collections.sort( pomoList);
			Collections.sort( sessionList);

			for(int j=0; j<sessionList.size(); j++){
				Date inizioSessione = sessionList.get(j).getOraInizio();
				Date fineSessione = sessionList.get(j).getOraFine();
				long durataSessione = fineSessione.toInstant().toEpochMilli() - inizioSessione.toInstant().toEpochMilli();
				long sommaDurataPomo = 0;

				for(int i=0; i<pomoList.size(); i++){

					//Se il pomodoro è iniziato all'interno della sessione
					if( pomoList.get(i).getInizio().after( inizioSessione) && pomoList.get(i).getInizio().before( fineSessione)){
						sommaDurataPomo = sommaDurataPomo + pomoList.get(i).getDurata();
						rating = rating + pomoList.get(i).getRating();

						//Rimuovo i pomodoro che ho già usato per una sessione
						pomoList.remove( i);

					}else{
						//Calcolo bonus/malus per aver rispettato o meno la sessione programmata
						if( sommaDurataPomo >= (durataSessione * BONUS_THRESHOLD)){
							rating = rating * BONUS;

						}else if( sommaDurataPomo < (durataSessione * MALUS_THRESHOLD)){
							rating = rating * MALUS;
						}
						break;	//Finiti i pomodori che rientravano nella sessione
					}
				}
			}

			//Alla fine rimangono solamente i pomodoro che non rientrano in una sessione
			for (int i=0; i<pomoList.size(); i++){
				rating = rating + pomoList.get(i).getRating();
			}
			rating = rating / pomoTotali;

		}else if( !pomoList.isEmpty()){
			Log.d(TAG, "calculateRatingByActivity solo pomo");

			//Solo pomodoro, nessuna sessione
			for (int i=0; i<pomoList.size(); i++){
				rating = rating + pomoList.get(i).getRating();
			}
			rating = rating / pomoTotali;
		}

		Log.d(TAG, "ACTIVITY "+activityName+" RATING "+rating);
		return ensureRange( rating, N_STARS, 0);
	}

	public static float ensureRange( float value, float max, float min){
		return Math.min( Math.max( value, min), max);
	}

}

