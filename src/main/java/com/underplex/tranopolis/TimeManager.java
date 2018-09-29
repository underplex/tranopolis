package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Manages time for an instance of <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class TimeManager {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private LocalDateTime current;
	private final City city;
	private final long period;
	
	public TimeManager(City city, LocalDateTime start, long period){
		this.city = city;
		this.current = start;
		this.period = period;
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
		// LOGGER.info("Time advanced by " + seconds + " second(s), is now " + current);
	}
	
	public void advancePeriod(){
		this.advanceSeconds(period);
	}

	public long getPeriod(){
		return this.period;
	}
}
