package com.unitn.lpsmt.group13.pommidori;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*	Classe indicante il tipo di sessione di studio in corso, e se è attiva o meno
* */
public class StatoSessione {
	public static final int COUNTDOWN = 0;
	public static final int COUNTUP = 1;
	public static final int PAUSA = 2;
	public static final int DISATTIVO = 3;

	private int value;
	public StatoSessione(){
		value = 3;
	}	//Il costruttore di default assegna lo stato disattivo

	public StatoSessione( int val){
		value = val;
	}

	public int getValue(){
		return value;
	}

	public void setValue( int val){
		value = val;
	}

	public boolean isSessioneLibera(){
		return value == COUNTUP;
	}
}
