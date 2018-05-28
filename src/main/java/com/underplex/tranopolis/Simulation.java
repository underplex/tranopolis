package com.underplex.tranopolis;

import java.time.LocalDateTime;

public class Simulation {

	private static int DEFAULT_WIDTH = 10;
	private static int DEFAULT_HEIGHT = 10;
	private static long DEFAULT_STEP = 60;
	private static LocalDateTime DEFAULT_START = LocalDateTime.of(1900, 1, 1, 0, 0);
	private static LocalDateTime DEFAULT_END = LocalDateTime.of(1900, 1, 3, 0, 0);
	
	private LocalDateTime end;
	
	public Simulation(){
		this.end = DEFAULT_END;
	}
	
	public void run(){
		City city = new City(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_START);
		// set up the city here
		
		while (city.getTimeManager().getCurrentTime().isBefore(this.end)){
			city.advance(DEFAULT_STEP);
		}
	}
}
