package com.unitn.lpsmt.group13.pommidori.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    //Variabili database
    private Context context;
    private static final String DATABASE_NAME = "PommidoriTimer.db";
    private static final int DATABASE_VERSION = 1;
    private static Database instance = null;

    private Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static Database getInstance(Context ctx){
        if( instance == null)   instance = new Database(ctx);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableActivityModel.queryCreate);
        db.execSQL(TableSessionProgModel.queryCreate);
        db.execSQL(TablePomodoroModel.queryCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TableActivityModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSessionProgModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TablePomodoroModel.TABLE_NAME);

        onCreate(db);
    }

    //Controllo esistenza database
    public boolean exist(Context ctx){
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    //Attività
    public boolean addActivity(TableActivityModel tableActivityModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableActivityModel.COLUMN_NOME, tableActivityModel.getName());
        cv.put(TableActivityModel.COLUMN_COLORE, tableActivityModel.getColore());
        cv.put(TableActivityModel.COLUMN_SCADENZA, tableActivityModel.getScadenza().getTime());
        cv.put(TableActivityModel.COLUMN_AVVISO, tableActivityModel.getAvviso());

        long result = db.insert(TableActivityModel.TABLE_NAME,null,cv);
        db.close();

        return result == -1 ? false : true;
    }

    public List<TableActivityModel> getAllActivities(){
        List<TableActivityModel> returnActivity = new ArrayList<>();

        //get data from the database
        String query = "SELECT * FROM " + TableActivityModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int activityId = cursor.getInt(0);
                String activityName = cursor.getString(1);
                int activityColor = cursor.getInt(2);
                Date activityDate = new Date(cursor.getLong(3));
                String activityAvviso = cursor.getString(4);
                TableActivityModel t = new TableActivityModel(activityId,activityName,activityColor,activityDate,activityAvviso);

                returnActivity.add(t);
            }while (cursor.moveToNext());
        } else{
            //fallimento nell'accedere al database
            returnActivity.add(new TableActivityModel());
        }

        cursor.close();
        db.close();

        return returnActivity;
    }

    public List<TableActivityModel> getAllActivitiesFromNow(){
        List<TableActivityModel> returnActivity = new ArrayList<>();
        List<TableActivityModel> getActivity = getAllActivities();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            if(a.getScadenza().after(new Date()))
                returnActivity.add(a);
        }

        return returnActivity;
    }

    public List<TableActivityModel> getAllActivitiesFromNowAndWithoutEnding(){
        List<TableActivityModel> returnActivity = new ArrayList<>();
        List<TableActivityModel> getActivity = getAllActivities();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            if( a.getScadenza().after(new Date()) || a.getScadenza().equals( new Date(0L)))
                //Activities senza scadenza hanno data scadenza 01:00 01-01-1970 cioè Date(0l)
                returnActivity.add(a);
        }

        return returnActivity;
    }

    public TableActivityModel getActivity(int id){
        TableActivityModel result = new TableActivityModel();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TableActivityModel.TABLE_NAME +
                " WHERE " + TableActivityModel.COLUMN_ACTIVITY_ID + " = " + id;

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            String activityName = cursor.getString(1);
            int activityColor = cursor.getInt(2);
            Date activityDate = new Date(cursor.getLong(3));
            String activityAvviso = cursor.getString(4);

            result.setId(id);
            result.setName(activityName);
            result.setColore(activityColor);
            result.setScadenza(activityDate);
            result.setAvviso(activityAvviso);
        } else{
            //fallimento nell'accedere al database
        }

        cursor.close();
        db.close();

        return result;
    }

    public TableActivityModel getActivity( String activityName){
        TableActivityModel result = new TableActivityModel();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TableActivityModel.TABLE_NAME +
                " WHERE " + TableActivityModel.COLUMN_NOME + " = '" + activityName + "';";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int activityColor = cursor.getInt(2);
            Date activityDate = new Date(cursor.getLong(3));
            String activityAvviso = cursor.getString(4);

            result.setId(id);
            result.setName(name);
            result.setColore(activityColor);
            result.setScadenza(activityDate);
            result.setAvviso(activityAvviso);
        } else{
            //fallimento nell'accedere al database
            result = null;
        }

        cursor.close();
        db.close();
        return result;
    }

    public boolean deleteActivity(int id){
        String s_id = Integer.toString(id);
        SQLiteDatabase db = this.getWritableDatabase();

        deleteAllProgrammedSessionsOfSelectedActivity( id);

        long result = db.delete(TableActivityModel.TABLE_NAME, TableActivityModel.COLUMN_ACTIVITY_ID + "=" + s_id,null);

        if( db.isOpen())    db.close();

        return result == -1 ? false : true;
    }

    public boolean updateActivty(int id, TableActivityModel activity){
        String s_id = Integer.toString(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableActivityModel.COLUMN_ACTIVITY_ID , activity.getId());
        cv.put(TableActivityModel.COLUMN_NOME, activity.getName());
        cv.put(TableActivityModel.COLUMN_COLORE, activity.getColore());
        cv.put(TableActivityModel.COLUMN_SCADENZA, activity.getScadenza().getTime());
        cv.put(TableActivityModel.COLUMN_AVVISO, activity.getAvviso());

        long result = db.update(TableActivityModel.TABLE_NAME, cv, "_id = ?", new String[]{s_id});

        db.close();
        return result == -1 ? false : true;
    }

    //Sessioni Programmate
    public boolean addProgrammedSession(TableSessionProgModel tableSessionProgModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableSessionProgModel.COLUMN_ID_ACTIVITY, tableSessionProgModel.getActivity().getId());
        cv.put(TableSessionProgModel.COLUMN_ORA_INIZIO, tableSessionProgModel.getOraInizio().getTime());
        cv.put(TableSessionProgModel.COLUMN_ORA_FINE, tableSessionProgModel.getOraFine().getTime());
        cv.put(TableSessionProgModel.COLUMN_AVVISO, tableSessionProgModel.getAvviso());
        cv.put(TableSessionProgModel.COLUMN_RIPETIZIONE, tableSessionProgModel.getRipetizione());

        long result = db.insert(TableSessionProgModel.TABLE_NAME,null,cv);
        db.close();

        return result == -1 ? false : true;
    }

    public List<TableSessionProgModel> getAllProgrammedSessions(){
        List<TableSessionProgModel> returnSession = new ArrayList<>();

        //get data from the database
        String query = "SELECT * FROM " + TableSessionProgModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int sessionId = cursor.getInt(0);
                int sessionActivityId = cursor.getInt(1);
                TableActivityModel sessionActivity = getActivity(sessionActivityId);
                Date sessionStartDate = new Date(cursor.getLong(2));
                Date sessionEndDate = new Date(cursor.getLong(3));
                String sessionAvviso = cursor.getString(4);
                String sessionRipetizione = cursor.getString(5);
                TableSessionProgModel t = new TableSessionProgModel(sessionId,sessionActivity,sessionStartDate,sessionEndDate,sessionAvviso,sessionRipetizione);

                returnSession.add(t);
            }while (cursor.moveToNext());
        } else{
            //fallimento nell'accedere al database
            returnSession.add(new TableSessionProgModel());
        }

        cursor.close();
        db.close();

        return returnSession;
    }

    public List<TableSessionProgModel> getAllProgrammedSessionsByActivity(String attivita){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableSessionProgModel> getSession = getAllProgrammedSessions();

        //loop su tutti gli elementi
        for(TableSessionProgModel s : getSession) {
            //se il nome dell'attività corrisponde, aggiungi la sesione al return
            if(s.getActivity().getName().equalsIgnoreCase(attivita))
                returnSession.add(s);
        }

        return returnSession;
    }

    public List<TableSessionProgModel> getFirstProgrammedSessionFromEveryActivityFromNow(){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableActivityModel> getActivity = getAllActivitiesFromNowAndWithoutEnding();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            List<TableSessionProgModel> getSession = getAllProgrammedSessionsByActivity(a.getName());

            //loop su tutti gli elementi
            for(TableSessionProgModel s : getSession) {
                //se la data è dopo oggi, aggiungi l'attività al return
                if( s.getOraInizio().after(new Date())) {
                    returnSession.add(s);
                    break;
                }
            }
        }

        return returnSession;
    }

    public List<TableSessionProgModel> getAllProgrammedSessionsByDay(Date date){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableSessionProgModel> getSession= getAllProgrammedSessions();

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");

        //loop su tutti gli elementi
        for(TableSessionProgModel s : getSession) {
            String d = sfd.format(s.getOraInizio());
            if(d.equalsIgnoreCase(sfd.format(date)))
                returnSession.add(s);
        }

        return returnSession;
    }

    public List<TableSessionProgModel> getAllProgrammedSessionsByMonth(LocalDate date){
        List<TableSessionProgModel> sessioniProgrammateMensili = new ArrayList<>();
        LocalDateTime inizioMese = date.withDayOfMonth(1).atStartOfDay();   //Primo giorno del mese, alle 00:00
        LocalDateTime fineMese = date.withDayOfMonth( date.getMonth().length( date.isLeapYear())).atTime(LocalTime.MAX); //Ultimo giorno del mese, alle 23:59:59
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(inizioMese);   //Zone orarie, GMT +02:00 per italia

        String query = "SELECT * FROM " + TableSessionProgModel.TABLE_NAME
                + " WHERE " + TableSessionProgModel.COLUMN_ORA_INIZIO
                + " BETWEEN " + inizioMese.toInstant(zoneOffset).toEpochMilli()
                + " AND " + fineMese.toInstant(zoneOffset).toEpochMilli();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int sessionId = cursor.getInt(0);
                TableActivityModel activity = getActivity(sessionId);
                long oraInizio = cursor.getLong(2);
                long oraFine = cursor.getLong(3);
                String avviso = cursor.getString(4);
                String ripetizione = cursor.getString( 5);

                TableSessionProgModel p = new TableSessionProgModel(
                        sessionId,
                        activity,
                        new Date(oraInizio),
                        new Date(oraFine),
                        avviso,
                        ripetizione
                );

                sessioniProgrammateMensili.add(p);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sessioniProgrammateMensili;
    }

    public List<TableSessionProgModel> getAllPastProgrammedSessions(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TableSessionProgModel> returnSessions = new ArrayList<>();

        Date now = new Date();
        Cursor cursor = db.query(TableSessionProgModel.TABLE_NAME, null,
                TableSessionProgModel.COLUMN_ORA_INIZIO + " <= ?",
                new String[]{now.toInstant().toString()},
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            do{
                int sessionId = cursor.getInt(0);
                int sessionActivityId = cursor.getInt(1);
                TableActivityModel sessionActivity = getActivity(sessionActivityId);
                Date sessionStartDate = new Date(cursor.getLong(2));
                Date sessionEndDate = new Date(cursor.getLong(3));
                String sessionAvviso = cursor.getString(4);
                String sessionRipetizione = cursor.getString(5);
                TableSessionProgModel t = new TableSessionProgModel(sessionId,sessionActivity,sessionStartDate,sessionEndDate,sessionAvviso,sessionRipetizione);

                returnSessions.add(t);
            }while(cursor.moveToNext());
        }else {
            returnSessions = null;
        }

        cursor.close();
        db.close();

        return returnSessions;
    }

    public List<TableSessionProgModel> getAllPastProgrammedSessionsByActivity( TableActivityModel activity){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TableSessionProgModel> returnSessions = new ArrayList<>();
        Cursor cursor;

        Date now = new Date();

        cursor = db.query(
                TableSessionProgModel.TABLE_NAME, null,
                TableSessionProgModel.COLUMN_ORA_INIZIO + " <= ? AND " +
                TableSessionProgModel.COLUMN_ID_ACTIVITY + " = ?",
                new String[]{ String.valueOf(now.toInstant().toEpochMilli()), String.valueOf(activity.getId())},
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            do{
                int sessionId = cursor.getInt(0);
                Date sessionStartDate = new Date(cursor.getLong(2));
                Date sessionEndDate = new Date(cursor.getLong(3));
                String sessionAvviso = cursor.getString(4);
                String sessionRipetizione = cursor.getString(5);

                TableSessionProgModel t = new TableSessionProgModel(
                        sessionId,
                        activity,
                        sessionStartDate,
                        sessionEndDate,
                        sessionAvviso,
                        sessionRipetizione);

                returnSessions.add(t);
            }while(cursor.moveToNext());
        }else {
        }

        cursor.close();
        db.close();

        return returnSessions;
    }

    public TableSessionProgModel getProgrammedSession(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableSessionProgModel.TABLE_NAME + " WHERE _id = " + id;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int sessionId = cursor.getInt(0);
        int sessionActivityId = cursor.getInt(1);
        TableActivityModel sessionActivity = getActivity(sessionActivityId);
        Date sessionStartDate = new Date(cursor.getLong(2));
        Date sessionEndDate = new Date(cursor.getLong(3));
        String sessionAvviso = cursor.getString(4);
        String sessionRipetizione = cursor.getString(5);
        TableSessionProgModel t = new TableSessionProgModel(sessionId,sessionActivity,sessionStartDate,sessionEndDate,sessionAvviso,sessionRipetizione);

        return t;
    }

    public boolean deleteProgrammedSession(int id){
        String s_id = Integer.toString(id);
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(
                TableSessionProgModel.TABLE_NAME,
                TableSessionProgModel.COLUMN_SESSION_ID + "=" + s_id,null);
        db.close();

        return result == -1 ? false : true;
    }

    public boolean deleteAllProgrammedSessionsOfSelectedActivity(int activity_id){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(
                TableSessionProgModel.TABLE_NAME,
                TableSessionProgModel.COLUMN_ID_ACTIVITY + "=" + activity_id,
                null
        );

        return result == -1 ? false : true;
    }

    public boolean updateProgrammedSession(int id, TableSessionProgModel session){
        String s_id = Integer.toString(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableSessionProgModel.COLUMN_ID_ACTIVITY,session.getActivity().getId());
        cv.put(TableSessionProgModel.COLUMN_ORA_INIZIO,session.getOraInizio().getTime());
        cv.put(TableSessionProgModel.COLUMN_ORA_FINE, session.getOraFine().getTime());
        cv.put(TableSessionProgModel.COLUMN_AVVISO, session.getAvviso());
        cv.put(TableSessionProgModel.COLUMN_RIPETIZIONE, session.getRipetizione());

        long result = db.update(TableSessionProgModel.TABLE_NAME, cv, "_id = ?", new String[]{s_id});

        return result == -1 ? false : true;
    }

    //Pomodoro
    public boolean addCompletedPomodoro(@NonNull TablePomodoroModel tablePomodoroModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TablePomodoroModel.COLUMN_NOME_ACTIVITY, tablePomodoroModel.getName());
        cv.put(TablePomodoroModel.COLUMN_DATA_INIZIO, tablePomodoroModel.getInizio().getTime());
        cv.put(TablePomodoroModel.COLUMN_DURATA, tablePomodoroModel.getDurata());
        cv.put(TablePomodoroModel.COLUMN_COLORE, tablePomodoroModel.getColor());
        cv.put(TablePomodoroModel.COLUMN_RATING, tablePomodoroModel.getRating());

        long result = db.insert(TablePomodoroModel.TABLE_NAME,null,cv);
        db.close();
        return result == -1 ? false : true;
    }

    public List<TablePomodoroModel> getAllPastPomodoro(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TablePomodoroModel> returnPomodoros = new ArrayList<>();

        Date now = new Date();
        Cursor cursor = db.query(TablePomodoroModel.TABLE_NAME, null,
                TablePomodoroModel.COLUMN_DATA_INIZIO + " <= ?",
                new String[]{now.toInstant().toString()},
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            do{
                int pomodoroId = cursor.getInt(0);
                String activityName = cursor.getString(1);
                Date inizio = new Date( cursor.getLong(2));
                long durata = cursor.getLong(3);
                int color = cursor.getInt(4);
                float rating = cursor.getFloat( 5);
                TablePomodoroModel t = new TablePomodoroModel(pomodoroId, activityName, inizio, durata, color, rating);

                returnPomodoros.add(t);
            }while(cursor.moveToNext());
        }else {
            returnPomodoros = null;
        }

        cursor.close();
        db.close();

        return returnPomodoros;
    }

    public List<TablePomodoroModel> getAllPomodorosByActivity( String activityName){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TablePomodoroModel> returnPomodoros = new ArrayList<>();

        Cursor cursor = db.query(
                TablePomodoroModel.TABLE_NAME, null,
                TablePomodoroModel.COLUMN_NOME_ACTIVITY + " = ?",
                new String[]{activityName},
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            do{
                int pomodoroId = cursor.getInt(0);
                String name = cursor.getString(1);
                Date inizio = new Date( cursor.getLong(2));
                long durata = cursor.getLong(3);
                int color = cursor.getInt(4);
                float rating = cursor.getFloat( 5);

                TablePomodoroModel t = new TablePomodoroModel(pomodoroId, name, inizio, durata, color, rating);
                System.out.println(t);
                returnPomodoros.add(t);
            }while(cursor.moveToNext());
        }else {
        }

        cursor.close();
        db.close();

        return returnPomodoros;
    }

    public List<TablePomodoroModel> getPomodorosByWeek(@NonNull LocalDate date){
        //Date è un giorno qualsiasi della settimana, da cui ricavo inizio e fine settimana
        List<TablePomodoroModel> pomodoroSettimanali = new ArrayList<>();
        date = date.with( WeekFields.of(Locale.ITALY).dayOfWeek(), 1);  //Lunedì primo giorno della settimana
        LocalDateTime inizioSettimana = date.atStartOfDay();                     //Orario impostato ad inizio giorno
        LocalDateTime fineSettimana = date.plus(Period.ofDays(6)).atTime(LocalTime.MAX);    //ottengo ultimo giorno della settimana alle 23:59:59
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(inizioSettimana);   //Zone orarie, GMT +02:00 per italia

        String query = "SELECT * FROM " + TablePomodoroModel.TABLE_NAME
                + " WHERE " + TablePomodoroModel.COLUMN_DATA_INIZIO
                + " BETWEEN " + inizioSettimana.toInstant(zoneOffset).toEpochMilli()
                + " AND " + fineSettimana.toInstant(zoneOffset).toEpochMilli();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int pomodoroId = cursor.getInt(0);
                String activityName = cursor.getString(1);
                long dataInizioPomodoro = cursor.getLong(2);
                long durataPomodoro = cursor.getLong(3);
                int color = cursor.getInt( 4);
                float rating = cursor.getFloat( 5);


                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color,
                        rating
                );

                pomodoroSettimanali.add(p);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return pomodoroSettimanali;
    }

    public List<TablePomodoroModel> getPomodorosByMonth(@NonNull LocalDate date){
        //Date è un giorno qualsiasi del mese, da cui ricavo inizio e fine mese
        List<TablePomodoroModel> pomodoroMensili = new ArrayList<>();
        LocalDateTime inizioMese = date.withDayOfMonth(1).atStartOfDay();   //Primo giorno del mese, alle 00:00
        LocalDateTime fineMese = date.withDayOfMonth( date.getMonth().length( date.isLeapYear())).atTime(LocalTime.MAX); //Ultimo giorno del mese, alle 23:59:59
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(inizioMese);   //Zone orarie, GMT +02:00 per italia

        String query = "SELECT * FROM " + TablePomodoroModel.TABLE_NAME
                + " WHERE " + TablePomodoroModel.COLUMN_DATA_INIZIO
                + " BETWEEN " + inizioMese.toInstant(zoneOffset).toEpochMilli()
                + " AND " + fineMese.toInstant(zoneOffset).toEpochMilli();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int pomodoroId = cursor.getInt(0);
                String activityName = cursor.getString(1);
                long dataInizioPomodoro = cursor.getLong(2);
                long durataPomodoro = cursor.getLong(3);
                int color = cursor.getInt( 4);
                float rating = cursor.getFloat( 5);

                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color,
                        rating
                );

                pomodoroMensili.add(p);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return pomodoroMensili;
    }

    public List<TablePomodoroModel> getPomodorosByYear(@NonNull  LocalDate date){
        //Date è un giorno qualsiasi dell'anno, da cui ricavo inizio e fine anno
        List<TablePomodoroModel> pomodoroAnnuali = new ArrayList<>();
        LocalDateTime inizioAnno = date.withDayOfYear(1).atStartOfDay();    //Primo giorno dell'anno alle 00:00
        LocalDateTime fineAnno = date.withDayOfYear( date.lengthOfYear()).atTime( LocalTime.MAX); //Ultimo giorno dell'anno alle 23:59:59
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(inizioAnno);   //Zone orarie, GMT +02:00 per italia

        String query = "SELECT * FROM " + TablePomodoroModel.TABLE_NAME
                + " WHERE " + TablePomodoroModel.COLUMN_DATA_INIZIO
                + " BETWEEN " + inizioAnno.toInstant(zoneOffset).toEpochMilli()
                + " AND " + fineAnno.toInstant(zoneOffset).toEpochMilli();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
                int pomodoroId = cursor.getInt(0);
                String activityName = cursor.getString(1);
                long dataInizioPomodoro = cursor.getLong(2);
                long durataPomodoro = cursor.getLong(3);
                int color = cursor.getInt( 4);
                float rating = cursor.getFloat( 5);

                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color,
                        rating
                );

                pomodoroAnnuali.add(p);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return pomodoroAnnuali;
    }

}
