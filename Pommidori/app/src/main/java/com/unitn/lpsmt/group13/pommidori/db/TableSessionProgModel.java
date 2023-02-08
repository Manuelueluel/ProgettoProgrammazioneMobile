package com.unitn.lpsmt.group13.pommidori.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TableSessionProgModel implements Comparable<TableSessionProgModel>{

    //Variabili tabella Sessioni Programmate
    public static final String TABLE_NAME = "sessioni_programmate";
    public static final String COLUMN_SESSION_ID = "_id";
    public static final String COLUMN_ID_ACTIVITY = "id_attivita";
    public static final String COLUMN_ORA_INIZIO = "ora_inizio";
    public static final String COLUMN_ORA_FINE = "ora_fine";
    public static final String COLUMN_AVVISO = "avviso";
    public static final String COLUMN_RIPETIZIONE = "ripetizione";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ID_ACTIVITY + " INTEGER, " +
                    COLUMN_ORA_INIZIO + " INTEGER, " +      //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_ORA_FINE + " INTEGER, " +        //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_AVVISO + " TEXT, " +
                    COLUMN_RIPETIZIONE + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_ID_ACTIVITY +") REFERENCES " + TableActivityModel.TABLE_NAME + "(" + TableActivityModel.COLUMN_ACTIVITY_ID +"));";

    //Modello
    private int id;
    private TableActivityModel activity;
    private Date oraInizio;
    private Date oraFine;
    private String avviso;
    private String ripetizione;

    //Costruttori
    public TableSessionProgModel() {
        this.id = 0000;
        this.activity = new TableActivityModel();
        this.oraInizio = new Date(0);
        this.oraFine = new Date(1);
        this.avviso = "???";
        this.ripetizione = "???";
    }

    public TableSessionProgModel(int id ,TableActivityModel activity, Date oraInizio, Date oraFine, String avviso, String ripetizione) {
        this.id = id;
        this.activity = activity;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.avviso = avviso;
        this.ripetizione = ripetizione;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TableActivityModel getActivity() {
        return activity;
    }

    public void setActivity(TableActivityModel activity) {
        this.activity = activity;
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

    public String getAvviso() {
        return avviso;
    }

    public void setAvviso(String avviso) {
        this.avviso = avviso;
    }

    public String getRipetizione() {
        return ripetizione;
    }

    public void setRipetizione(String ripetizione) {
        this.ripetizione = ripetizione;
    }

    @Override
    public String toString() {
        SimpleDateFormat smDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        SimpleDateFormat smHour = new SimpleDateFormat("HH:mm",Locale.ITALY);
        return activity.getName() + " " + smHour.format(oraInizio) + "-" + smHour.format(oraFine) + " " + smDate.format(oraInizio);
    }

    @Override
    public int compareTo(TableSessionProgModel sessionProgModel) {
        return getOraInizio().compareTo( sessionProgModel.getOraInizio());
    }
}
