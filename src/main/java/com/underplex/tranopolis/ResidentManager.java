package com.underplex.tranopolis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages residents for an instance of <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class ResidentManager {

	private final City city;
	private final Set<AbstractResident> residents;
	// number of seconds in advance this manage requires to plan before the simulation actually simulates a given time
	
	private Set<Drive> upcomingDrives;
	private LocalDateTime lastTimePlanned;
	
	/**
	 * Constructor.
	 * @param city City this manages Residents for
	 * @param required long number of seconds of advance warning required for residents to plan trips
	 * @param window long number of seconds that residents will plan their activities in advance 
	 */
	public ResidentManager(City city) {

		if (city == null) throw new IllegalArgumentException("Parameter city may not be null.");
		
		this.city = city;

		this.residents = new HashSet<>();
		this.upcomingDrives = new HashSet<>();
		// depending on how you want to set up the first day of activities, this might need to change
		this.lastTimePlanned = null;
	}

	/**
	 * Advance all residents through the current time and updates upcoming planned drives.
	 * <p>
	 * Uses period from City to find window in which drive plans are requested from Residents.
	 */
	public void advance(LocalDateTime time){
		
		LocalDateTime end = time.plusSeconds(city.getTimeManager().getPeriod());
		for (Resident r : residents){
			upcomingDrives.addAll(r.planDrives(time, city.getRoadGraph(), time, end));
		}
	}
		
	/**
	 * Returns Drives to start at or before <tt>time</tt>.
	 * <p> 
	 * May return empty Set if none are to be.
	 * @see AbstractResident
	 * @param time LocalDateTime that is the current time of the simulation
	 * @return Set of Drives to begin immediately
	 */
	public Set<Drive> surveyDrives(LocalDateTime time){
		if (time == null) throw new IllegalArgumentException("Time cannot be null.");

		List<Drive> up = new ArrayList<>(this.upcomingDrives);
 
		Collections.sort(up, new Comparator<Drive>() { 
			@Override
			public int compare(Drive o1, Drive o2) {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		});

		Set<Drive> starters = new HashSet<>();
		for (Drive d : up){
			if (d.getStartTime().isBefore(time)){
				starters.add(d);
			} else {
				break;
			}
		}
		return starters;
	}
	
}
