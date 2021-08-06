package com.unitn.lpsmt.group13.pommidori.db;

import java.util.Date;

public class TableSessionProgModel {

    //Variabili tabella Sessioni Programmate
    public static final String TABLE_NAME = "sessioni_programmate";
    public static final String COLUMN_SESSION_ID = "_id";
    public static final String COLUMN_NOME_ACTIVITY = "nome_attivita";
    public static final String COLUMN_ORA_INIZIO = "ora_inizio";
    public static final String COLUMN_ORA_FINE = "ora_fine";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME_ACTIVITY + " TEXT, " +
                    COLUMN_ORA_INIZIO + " DATETIME, " +
                    COLUMN_ORA_FINE + " DATETIME, " +
                    "FOREIGN KEY (" + COLUMN_NOME_ACTIVITY +") REFERENCES " + TableActivityModel.TABLE_NAME + "(" + TableActivityModel.COLUMN_NOME +"));";

    //Modello
    private String nameActivity;
    private Date oraInizio;
    private Date oraFine;

    //Costruttori
    public TableSessionProgModel() {
    }
    public TableSessionProgModel(String nameActivity, Date oraInizio, Date oraFine) {
        this.nameActivity = nameActivity;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
    }

    public String getNameActivity() {
        return nameActivity;
    }

    public void setNameActivity(String nameActivity) {
        this.nameActivity = nameActivity;
    }

    public Date getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(Date oraInizio) {
        this.oraInizio = oraInizio;
    }

    public Date getOraFine() {
        return oraFine;
    }

    public void setOraFine(Date oraFine) {
        this.oraFine = oraFine;
    }
}
