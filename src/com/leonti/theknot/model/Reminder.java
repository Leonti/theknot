package com.leonti.theknot.model;

public class Reminder {

	public enum Type {
		TIME,
		LOCATION
	}
	
	public static class Time {
		
		private final long when;

		public Time(long when) {
			this.when = when;
		}

		public long getWhen() {
			return when;
		}
		
	}
	
	public static class Location {
		
		private final double latitude;
		private final double longitude;
		
		public Location(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}

	}
	
	private final Long id;
	private final String title;
	private final String description;
	private final Type type;
	private final Time time;
	private final Location location;
	private final boolean done;
	
	public Reminder(Long id, String title, String description, Time time, boolean done) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.type = Type.TIME;
		this.time = time;
		this.location = null;
		this.done = done;
	}
	
	public Reminder(Long id, String title, String description, Location location, boolean done) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.type = Type.LOCATION;
		this.time = null;
		this.location = location;
		this.done = done;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Type getType() {
		return type;
	}

	public Time getTime() {
		
		if (type != Type.TIME) {
			throw new RuntimeException("Can only get time info for time reminders");
		}
		
		return time;
	}

	public Location getLocation() {
		
		if (type != Type.LOCATION) {
			throw new RuntimeException("Can only get location info for location reminders");
		}		
		
		return location;
	}
	
	public boolean isDone() {
	    return done;
	}
	
}
