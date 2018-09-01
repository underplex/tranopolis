package com.underplex.tranopolis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages residents for an instance of <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class ResidentManager {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final City city;
	private final Set<Resident> residents;
	// number of seconds in advance this manage requires to plan before the simulation actually simulates a given time
	
	private final Set<Drive> upcomingDrives;
	private final Set<Drive> droppedDrives;
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
		this.droppedDrives = new HashSet<>();
		
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
			// TODO: maybe filter/validate these Drives in some way
		}
	}
		
	/**
	 * Returns Drives to start at or before <tt>time</tt>.
	 * <p>
	 * May return empty Set if none are to be.
	 * <p>
	 * This method will not return Drives that are deemed to be logically invalid, where, for example, that a Resident attempts an impossible Drive.
	 * @see Resident
	 * @param time LocalDateTime that is the current time of the simulation
	 * @return Set of Drives to begin immediately
	 */
	public Set<Drive> surveyDrives(LocalDateTime time){
		if (time == null) throw new IllegalArgumentException("Time cannot be null.");

		List<Drive> up = new ArrayList<>(this.upcomingDrives);
 
		Collections.sort(up, new Comparator<Drive>() { 
			@Override
			public int compare(Drive o1, Drive o2) {
				return o1.getAttemptStartTime().compareTo(o2.getAttemptStartTime());
			}
		});

		Set<Drive> starters = new HashSet<>();
		// pseudo code for adding drives...
			
		for (Drive d : up){
			
			if (d.getAttemptStartTime().isBefore(time) || d.getAttemptStartTime().isEqual(time)){
				
				// check that the driver can leave from the start location
				if (d.getDriver().isAt(d.getOnPoint())){
					starters.add(d);
					upcomingDrives.remove(d);

					LOGGER.info(d + " is validated and set to begin.");
				} else if (time.isBefore(d.getDropTime())){
					droppedDrives.add(d); // TODO: dump dropped drives at some point to improve memory
					d.drop();
					upcomingDrives.remove(d);
					LOGGER.info(d + " is dropped.");
				}
				
			} else {
				break;
			}
		}
		
		return starters;
	}
	
	/**
	 * Returns defensive copy of the Set of Residents.
	 * @return defensive copy of the Set of Residents
	 */
	public Set<Resident> getResidents(){
		return new HashSet<Resident>(this.residents);
	}

	/**
	 * Force this manager to add a Drive.
	 * <p>
	 * Not generally used for the purposes of the simulation.
	 * @return
	 */
	public void addUpcomingDrive(Drive drive){
		upcomingDrives.add(drive);		
	}
	
	public void addResidents(Set<Resident> residents){
		for (Resident rez : residents){
			rez.getHome().addResident(rez);
			rez.setCurrentLocation(rez.getHome());
		}
		this.residents.addAll(residents);
	}
	
	public void removeResidents(Set<Resident> residents){
		for (Resident rez : residents){
			rez.getCurrentLocation().removeResident(rez);
			rez.setCurrentLocation(null);
		}
		this.residents.removeAll(residents);
	}
	
	public int getPopulation(){
		return this.residents.size();
	}
	
}
