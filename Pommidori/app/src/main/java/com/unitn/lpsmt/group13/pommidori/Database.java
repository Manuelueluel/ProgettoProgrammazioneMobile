package com.unitn.lpsmt.group13.pommidori;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    //Variabili database
    private Context context;
    private static final String DATABASE_NAME = "PommidoriTimer.db";
    private static final int DATABASE_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

    public List<TableActivityModel> getAllActivity(){
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

    public List<TableActivityModel> getAllActivityFromNow(){
        List<TableActivityModel> returnActivity = new ArrayList<>();
        List<TableActivityModel> getActivity = getAllActivity();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            //se la data è dopo oggi, aggiungi l'attività al return
            if(a.getScadenza().after(new Date()) || a.getScadenza().getTime()==0)
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

    //Sessioni Programmate
    public boolean addSessioneProgrammata(TableSessionProgModel tableSessionProgModel){
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

    public List<TableSessionProgModel> getAllSessioniProgrammate(){
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

    public List<TableSessionProgModel> getAllSessioniProgrammateByActivity(String attivita){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableSessionProgModel> getSession = getAllSessioniProgrammate();

        //loop su tutti gli elementi
        for(TableSessionProgModel s : getSession) {
            //se il nome dell'attività corrisponde, aggiungi la sesione al return
            if(s.getActivity().getName().equalsIgnoreCase(attivita))
                returnSession.add(s);
        }

        return returnSession;
    }

    public List<TableSessionProgModel> getFirstSessionByActivityFromNow(){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableActivityModel> getActivity = getAllActivityFromNow();

        //loop su tutti gli elementi
        for(TableActivityModel a : getActivity) {
            List<TableSessionProgModel> returnTemp = new ArrayList<>();
            List<TableSessionProgModel> getSession = getAllSessioniProgrammateByActivity(a.getName());

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
    public List<TableSessionProgModel> getSessionByMonth(String month, String year){
        List<TableSessionProgModel> returnSession = new ArrayList<>();
        List<TableSessionProgModel> getSession= getAllSessioniProgrammate();

        String date = month+"-"+year;

        //loop su tutti gli elementi
        for(TableSessionProgModel s : getSession) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy", Locale.ITALY);
            if(dateFormat.format(s.getOraInizio()).equalsIgnoreCase(date))
                returnSession.add(s);
        }

        return returnSession;
    }
}
