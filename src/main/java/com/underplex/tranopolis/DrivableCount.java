package com.underplex.tranopolis;

import com.opencsv.bean.CsvBindByName;

public class DrivableCount extends Stat {

	@CsvBindByName
	private String drivable;

	@CsvBindByName
	private int numberOfDrives;
	
	public String getDrivable() {
		return drivable;
	}

	public int getNumberOfDrives() {
		return numberOfDrives;
	}

	public void setDrivable(String drivable) {
		this.drivable = drivable;
	}

	public void setNumberOfDrives(int numberOfDrives) {
		this.numberOfDrives = numberOfDrives;
	}

}
