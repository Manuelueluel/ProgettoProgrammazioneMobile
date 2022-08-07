package com.unitn.lpsmt.group13.pommidori;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rating rating1 = (Rating) o;
		return Float.compare(rating1.getRating(), getRating()) == 0 && Objects.equals(getActivityName(), rating1.getActivityName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRating(), getActivityName());
	}
}
