package com.unitn.lpsmt.group13.pommidori.db;

import java.util.Date;

public class TableActivityModel {

    //Variabili tabella Attività
    public static final String TABLE_NAME = "attivita";
    public static final String COLUMN_ACTIVITY_ID = "_id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_SCADENZA = "scadenza";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_SCADENZA + " DATETIME);";

    //Modello
    private String name;
    private Date scadenza;

    //Costruttore
    public TableActivityModel() {
    }
    public TableActivityModel(String name, Date scadenza) {
        this.name = name;
        this.scadenza = scadenza;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getScadenza() { return scadenza; }

    public void setScadenza(Date scadenza) { this.scadenza = scadenza;}
}
