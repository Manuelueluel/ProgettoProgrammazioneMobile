package com.unitn.lpsmt.group13.pommidori;

import android.os.CountDownTimer;

/*	Versione modificata di un CountDownTimer, permette la creazione di un cronometro che conta in maniera crescente
* */
public abstract class CountUpTimer extends CountDownTimer {
	private static final long INTERVALLO_MS = 1000;
	private final long durata;

	protected CountUpTimer( long durataMs){
		super( durataMs, INTERVALLO_MS);
		this.durata = durataMs;
	}

	public abstract void onTick(int sec);


	@Override
	public void onTick( long msUntilFinished){
		int second = (int) ((durata-msUntilFinished)/1000);
		onTick(second);
	}

	@Override
	public void onFinish() {
		onTick(durata / 1000);
	}
}
