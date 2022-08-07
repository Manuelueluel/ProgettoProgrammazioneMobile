package com.unitn.lpsmt.group13.pommidori.db;

import java.util.Date;

public class TableSessionModel implements Comparable<TableSessionModel>{

    //Variabili tabella Sessioni Svolte
    public static final String TABLE_NAME = "sessioni";
    public static final String COLUMN_SESSION_ID = "_id";
    public static final String COLUMN_NOME_ACTIVITY = "nome_attivita";
    public static final String COLUMN_DATA = "data_session";
    public static final String COLUMN_VALUTAZIONE = "valutazione";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME_ACTIVITY + " TEXT, " +
                    COLUMN_DATA + " INTEGER, " +                            //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_VALUTAZIONE + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_NOME_ACTIVITY +") REFERENCES " + TableActivityModel.TABLE_NAME + "(" + TableActivityModel.COLUMN_NOME +"));";

    //Modello
    private int id;
    private String name;
    private Date scadenza;
    private int valutazione;

    //Costruttori
    public TableSessionModel() {
    }
    public TableSessionModel(int id, String name, Date scadenza, int valutazione) {
        this.id = id;
        this.name = name;
        this.scadenza = scadenza;
        this.valutazione = valutazione;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    @Override
    public int compareTo(TableSessionModel sessionModel) {
        return getScadenza().compareTo( sessionModel.getScadenza());
    }
}
