package com.unitn.lpsmt.group13.pommidori;

public class Rating {
	private float rating;
	private String activityName;

	public Rating(float rating, String activityName) {
		this.rating = rating;
		this.activityName = activityName;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
