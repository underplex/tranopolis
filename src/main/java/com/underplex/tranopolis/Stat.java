package com.underplex.tranopolis;

import com.opencsv.bean.CsvBindByName;

public abstract class Stat {

	@CsvBindByName
	private String time;
	
	@CsvBindByName
	private String dayOfWeek;

	public String getTime() {
		return time;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
}
