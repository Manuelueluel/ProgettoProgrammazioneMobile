package com.unitn.lpsmt.group13.pommidori;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

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
        db.execSQL(TableSessionModel.queryCreate);
        db.execSQL(TablePomodoroModel.queryCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TableActivityModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSessionProgModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSessionModel.TABLE_NAME);
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
        cv.put(TableActivityModel.COLUMN_SIGLA, tableActivityModel.getSigla());
        cv.put(TableActivityModel.COLUMN_COLORE, tableActivityModel.getColore());
        cv.put(TableActivityModel.COLUMN_NOME_SCADENZA, tableActivityModel.getNomeScadenza());
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
                String activitySigla = cursor.getString(2);
                int activityColor = cursor.getInt(3);
                String activityNameScad = cursor.getString(4);
                Date activityDate = new Date(cursor.getLong(5));
                String activityAvviso = cursor.getString(6);
                TableActivityModel t = new TableActivityModel(activityId,activityName,activitySigla,activityColor,activityNameScad,activityDate,activityAvviso);

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
            //se la data è dopo oggi, aggiungi l'attività al return
            if(a.getScadenza().after(new Date()))
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
            String activitySigla = cursor.getString(2);
            int activityColor = cursor.getInt(3);
            String activityNameScad = cursor.getString(4);
            Date activityDate = new Date(cursor.getLong(5));
            String activityAvviso = cursor.getString(5);

            result.setId(id);
            result.setName(activityName);
            result.setSigla(activitySigla);
            result.setColore(activityColor);
            result.setNomeScadenza(activityNameScad);
            result.setScadenza(activityDate);
            result.setAvviso(activityAvviso);
        } else{
            //fallimento nell'accedere al database
        }

        cursor.close();
        db.close();

        return result;
    }

    public boolean deleteActivity(int id){

        String s_id = Integer.toString(id);
        SQLiteDatabase db = this.getWritableDatabase();

        //String query = "DELETE FROM " + TableActivityModel.TABLE_NAME + " WHERE " + TableActivityModel.COLUMN_ACTIVITY_ID + " = " + id;

        long result = db.delete(TableActivityModel.TABLE_NAME, TableActivityModel.COLUMN_ACTIVITY_ID + "=" + s_id,null);

        return result == -1 ? false : true;
    }

    //Sessioni
    public List<TableSessionModel> getAllSessions(){
        List<TableSessionModel> sessions = new ArrayList<>();

        //get data from the database
        String query = "SELECT * FROM " + TableSessionModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            //loop su tutti gli elementi
            do{
               int sessionId = cursor.getInt(0);
               String activity = cursor.getString(1);
               Date scadenza = new Date(cursor.getLong(2));
               int valutazione = cursor.getInt(3);
               TableSessionModel t = new TableSessionModel(sessionId, activity, scadenza, valutazione);

                sessions.add(t);
            }while (cursor.moveToNext());
        } else{
            //fallimento nell'accedere al database
            sessions.add(new TableSessionModel());
        }

        cursor.close();
        db.close();

        return sessions;
    }

    //Sessioni Programmate
    public boolean addProgrammedSession(TableSessionProgModel tableSessionProgModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableSessionProgModel.COLUMN_ID_ACTIVITY,tableSessionProgModel.getActivity().getId());
        cv.put(TableSessionProgModel.COLUMN_ORA_INIZIO,tableSessionProgModel.getOraInizio().getTime());
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
        List<TableActivityModel> getActivity = getAllActivitiesFromNow();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            List<TableSessionProgModel> returnTemp = new ArrayList<>();
            List<TableSessionProgModel> getSession = getAllProgrammedSessionsByActivity(a.getName());

            //loop su tutti gli elementi
            for(TableSessionProgModel s : getSession) {
                //se la data è dopo oggi, aggiungi l'attività al return
                if(s.getOraInizio().after(new Date()))
                    returnTemp.add(s);
            }

            if(!returnTemp.isEmpty())
                returnSession.add(returnTemp.get(0));
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
    //TODO getAllProgrammedSessionsByMonth da testare
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

    //Pomodoro
    public boolean addCompletedPomodoro(@NonNull TablePomodoroModel tablePomodoroModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TablePomodoroModel.COLUMN_NOME_ACTIVITY, tablePomodoroModel.getName());
        cv.put(TablePomodoroModel.COLUMN_DATA_INIZIO, tablePomodoroModel.getInizio().getTime());
        cv.put(TablePomodoroModel.COLUMN_DURATA, tablePomodoroModel.getDurata());
        cv.put(TablePomodoroModel.COLUMN_COLORE, tablePomodoroModel.getColor());

        long result = db.insert(TablePomodoroModel.TABLE_NAME,null,cv);
        db.close();
        return result == -1 ? false : true;
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

                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color
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

                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color
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

                TablePomodoroModel p = new TablePomodoroModel(
                        pomodoroId,
                        activityName,
                        new Date(dataInizioPomodoro),
                        durataPomodoro,
                        color
                );

                pomodoroAnnuali.add(p);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return pomodoroAnnuali;
    }

}
