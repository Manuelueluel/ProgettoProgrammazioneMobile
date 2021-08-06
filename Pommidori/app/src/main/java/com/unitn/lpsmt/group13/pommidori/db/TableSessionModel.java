package com.unitn.lpsmt.group13.pommidori.db;

import java.util.Date;

public class TableSessionModel {

    //Variabili tabella Sessioni Svolte
    public static final String TABLE_NAME = "sessioni";
    public static final String COLUMN_SESSION_ID = "_id";
    public static final String COLUMN_NOME_ACTIVITY = "nome_attivita";
    public static final String COLUMN_DATA = "data_session";
    public static final String COLUMN_AUTOVALUTAZIONE = "autovalutazione";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME_ACTIVITY + " TEXT, " +
                    COLUMN_DATA + " DATATIME, " +
                    COLUMN_AUTOVALUTAZIONE + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_NOME_ACTIVITY +") REFERENCES " + TableActivityModel.TABLE_NAME + "(" + TableActivityModel.COLUMN_NOME +"));";

    //Modello
    private String name;
    private Date scadenza;
    private int autovalutazione;

    //Costruttori
    public TableSessionModel() {
    }
    public TableSessionModel(String name, Date scadenza, int autovalutazione) {
        this.name = name;
        this.scadenza = scadenza;
        this.autovalutazione = autovalutazione;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getScadenza() {
        return scadenza;
    }

    public void setScadenza(Date scadenza) {
        this.scadenza = scadenza;
    }

    public int getAutovalutazione() {
        return autovalutazione;
    }

    public void setAutovalutazione(int autovalutazione) {
        this.autovalutazione = autovalutazione;
    }
}
