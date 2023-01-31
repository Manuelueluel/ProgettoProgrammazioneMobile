package com.unitn.lpsmt.group13.pommidori;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*	Classe indicante il tipo di timer di studio in corso, e se è attivo o meno
* */
public class StatoTimer {
	public static final int COUNTDOWN = 0;
	public static final int COUNTUP = 1;
	public static final int PAUSA = 2;
	public static final int DISATTIVO = 3;

	private int value;
	public StatoTimer(){ value = DISATTIVO; }	//Il costruttore di default assegna lo stato disattivo

	public StatoTimer(int val){
		value = val;
	}

	public int getValue(){
		return value;
	}

	public void setValue( int val){
		value = val;
	}

	public boolean isCountDown() { return value == COUNTDOWN; }

	public boolean isCountUp() { return value == COUNTUP; }

	public boolean isPausa() { return value == PAUSA; }

	public boolean isDisattivo() { return value == DISATTIVO; }

}
