package com.underplex.tranopolis;

import com.opencsv.bean.CsvBindByName;

public class LocationCount extends Stat{
	
	@CsvBindByName
	private String location;
	
	@CsvBindByName
	private int numberOfResidents;
	
	@CsvBindByName
	private double averageDriveTime;

	public double getAverageDriveTime() {
		return averageDriveTime;
	}
	
	/**
	 * Returns average drive time for this location for drives finished between the current time and the last time measured.
	 * @param averageDriveTime
	 */
	public void setAverageDriveTime(double averageDriveTime) {
		this.averageDriveTime = averageDriveTime;
	}

	public String getLocation() {
		return location;
	}

	public int getNumberOfResidents() {
		return numberOfResidents;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setNumberOfResidents(int numberOfResidents) {
		this.numberOfResidents = numberOfResidents;
	}
	
}
