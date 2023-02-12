package com.unitn.lpsmt.group13.pommidori.db;

import java.util.Date;

public class TablePomodoroModel implements Comparable<TablePomodoroModel>{

    //Variabili tabella Pomodori
    public static final String TABLE_NAME = "pomodoro";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOME_ACTIVITY = "nome_attivita";
    public static final String COLUMN_DATA_INIZIO = "data_pomodoro";
    public static final String COLUMN_DURATA = "durata_pomodoro";
    public static final String COLUMN_COLORE = "colore";
    public static final String COLUMN_RATING = "rating";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME_ACTIVITY + " TEXT, " +
                    COLUMN_DATA_INIZIO + " INTEGER, " +    //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_DURATA + " INTEGER, " +         //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_COLORE + " INTEGER NOT NULL, " +
                    COLUMN_RATING + " REAL, " +
                    "FOREIGN KEY (" + COLUMN_NOME_ACTIVITY +") REFERENCES " + TableActivityModel.TABLE_NAME + "(" + TableActivityModel.COLUMN_NOME +"));";

    //Modello
    private int id;
    private String name;
    private Date inizio;
    private long durata;
    private int color;
    private float rating;

    //Costrutori
    public TablePomodoroModel() {
    }

    public TablePomodoroModel(int id, String name, Date inizio, long durata, int color, float rating) {
        this.id = id;
        this.name = name;
        this.inizio = inizio;
        this.durata = durata;
        this.color = color;
        this.rating = rating;
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

    public Date getInizio() {
        return inizio;
    }

    public void setInizio(Date inizio) {
        this.inizio = inizio;
    }

    public long getDurata() {
        return durata;
    }

    public void setDurata(long durata) {
        this.durata = durata;
    }

    public int getColor(){ return color; }

    public void setColor(int color){ this.color = color; }

    public float getRating() { return rating; }

    public void setRating(float rating) { this.rating = rating; }

    @Override
    public int compareTo(TablePomodoroModel o) {
        return getInizio().compareTo( o.getInizio());
    }

    @Override
    public String toString() {
        return "TablePomodoroModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inizio=" + inizio +
                ", durata=" + durata +
                ", color=" + color +
                ", rating=" + rating +
                '}';
    }
}
