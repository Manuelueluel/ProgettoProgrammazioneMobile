package com.unitn.lpsmt.group13.pommidori;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import java.text.SimpleDateFormat;

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

    public void addActivity(TableActivityModel tableActivityModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableActivityModel.COLUMN_NOME, tableActivityModel.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");//Formattazione la data
        cv.put(TableActivityModel.COLUMN_SCADENZA, sdf.format(tableActivityModel.getScadenza()));

        db.insert(TableActivityModel.TABLE_NAME,null,cv);
    }
}
