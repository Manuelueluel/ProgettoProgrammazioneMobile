package com.unitn.lpsmt.group13.pommidori;

import java.time.LocalDate;

public class DailyProgress {
	//Progress e objective sono espressi in millisecondi
	private int progress;
	private int objective;
	private LocalDate day;

	public DailyProgress(int progress, int objective, LocalDate day){
		this.progress = progress;
		this.objective = objective;
		this.day = day;
	}

	public int getProgress() {
		return progress;
	}

	public int getObjective() {
		return objective;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setObjective(int objective) {
		this.objective = objective;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "DailyProgress{" +
				"progress=" + progress +
				", objective=" + objective +
				", day=" + day +
				'}';
	}
}
