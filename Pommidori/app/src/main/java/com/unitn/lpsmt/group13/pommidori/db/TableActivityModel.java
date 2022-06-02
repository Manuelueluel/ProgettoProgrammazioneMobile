package com.unitn.lpsmt.group13.pommidori.db;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TableActivityModel implements Comparable<TableActivityModel>{

    //Variabili tabella Attività
    public static final String TABLE_NAME = "attivita";
    public static final String COLUMN_ACTIVITY_ID = "_id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_SIGLA = "sigla";
    public static final String COLUMN_COLORE = "colore";
    public static final String COLUMN_NOME_SCADENZA = "nome_scadenza";
    public static final String COLUMN_SCADENZA = "scadenza";
    public static final String COLUMN_AVVISO = "avviso";

    public static final String queryCreate =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_SIGLA + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_COLORE + " INTEGER NOT NULL, " +
                    COLUMN_NOME_SCADENZA + " TEXT NOT NULL, " +
                    COLUMN_SCADENZA + " INTEGER, " +    //viene salvato il numero di millisecondi dal 1/1/1970
                    COLUMN_AVVISO + " TEXT);";

    //Model
    private int id;
    private String name;
    private String sigla;
    private int colore;
    private String nomeScadenza;
    private Date scadenza;
    private String avviso;

    //Costruttore
    public TableActivityModel() {
        this.id = 0000;
        this.name = "Nessuna attività"; //Attività di default associata alle sessioni che sono senza attività associate
        this.sigla = "";
        this.colore = 0;
        this.nomeScadenza = "";
        this.scadenza = new Date(0l);; //new Date(0l);
        this.avviso = null;
    }
    public TableActivityModel(int id, String name, String sigla, int colore, String nomeScadenza, Date scadenza, String avviso) {
        this.id = id;
        this.name = name;
        this.sigla = sigla;
        this.colore = colore;
        this.nomeScadenza = nomeScadenza;
        this.scadenza = scadenza;
        this.avviso = avviso;
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public int getColore() {
        return colore;
    }

    public void setColore(int colore) {
        this.colore = colore;
    }

    public String getNomeScadenza() {
        return nomeScadenza;
    }

    public void setNomeScadenza(String nomeScadenza) {
        this.nomeScadenza = nomeScadenza;
    }

    public Date getScadenza() {
        return scadenza;
    }

    public void setScadenza(Date scadenza) {
        this.scadenza = scadenza;
    }

    public String getAvviso() {
        return avviso;
    }

    public void setAvviso(String avviso) {
        this.avviso = avviso;
    }

    @Override
    public String toString() {
        SimpleDateFormat smDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
        SimpleDateFormat smHour = new SimpleDateFormat("HH:mm", Locale.ITALY);
        return scadenza.getTime()==0 ? "["+sigla.toUpperCase()+"] "+name.toUpperCase() : "["+sigla.toUpperCase()+"] "+ nomeScadenza +" ore "+smHour.format(scadenza) +" "+ smDate.format(scadenza);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableActivityModel that = (TableActivityModel) o;
        return getId() == that.getId() && getColore() == that.getColore() && getName().equals(that.getName()) && getSigla().equals(that.getSigla()) && getNomeScadenza().equals(that.getNomeScadenza()) && getScadenza().equals(that.getScadenza()) && getAvviso().equals(that.getAvviso());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSigla(), getColore(), getNomeScadenza(), getScadenza(), getAvviso());
    }

    @Override
    public int compareTo(TableActivityModel activityModel){
        return getScadenza().compareTo( activityModel.getScadenza());
    }

}
