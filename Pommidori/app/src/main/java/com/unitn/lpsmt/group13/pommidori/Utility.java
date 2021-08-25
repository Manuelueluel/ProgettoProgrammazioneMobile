package com.unitn.lpsmt.group13.pommidori;

public class Utility {

	//Shared preferences per gestione sessione
	public static final String SHARED_PREFS_SESSIONE = "sharedPreferencesSessione";
	public static final String ORE_SESSIONE = "ore";
	public static final String MINUTI_SESSIONE = "minuti";
	public static final String TEMPO_INIZIALE = "tempoIniziale";
	public static final String TEMPO_RIMASTO = "tempoRimasto";
	public static final String TEMPO_FINALE = "tempoFinale";
	public static final String TEMPO_TRASCORSO = "tempoTrascorso";
	public static final String STATO_SESSIONE = "statoSessione";
	public static final String STATO_SESSIONE_PRECEDENTE_PAUSA = "statoSessionePrecedentePausa";
	public static final String PAUSA = "pausa";

	//Costanti
	public static final long DURATA_MASSIMA_COUNTUP_TIMER = 86400000;	//Usato per CountUpTimer, corrisponde a 24 ore

}
