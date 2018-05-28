package com.underplex.tranopolis;

import java.time.LocalDateTime;

/**
 * Manages time for an instance of <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class TimeManager {

	private LocalDateTime current;
	private final City city;
	private final long period;
	
	// default period is 3 hours
	private static final long DEFAULT_PERIOD = (60 * 60 * 3);
	
	public TimeManager(City city, LocalDateTime start, long period){
		this.city = city;
		this.current = start;
		this.period = period;
	}
	
	public TimeManager(City city, LocalDateTime start) {
		this(city,start,DEFAULT_PERIOD);
	}

	/**
	 * Returns the current time.
	 * <p>
	 * In practice, this is pass-by-value since <tt>LocalDateTime</tt> is immutable.	 * 
	 * @return the time
	 */
	public LocalDateTime getCurrentTime() {
		return current;
	}
	
	public void advanceSeconds(long seconds){
		this.current = this.current.plusSeconds(seconds);
	}
	
	public void advancePeriod(){
		this.advanceSeconds(period);
	}

	public long getPeriod(){
		return this.period;
	}
}
