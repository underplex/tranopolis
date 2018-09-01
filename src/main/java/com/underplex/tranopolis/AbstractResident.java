package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents an individual living and working in the city.
 * <p>
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public abstract class AbstractResident implements Resident {

	private static long counter = 0;
	private final String id;
	private Location home; // where person lives
	private Location work; // where person works
	private Location currentLocation; // current currentLocation
	private Drive drive; // current Drive, if any
	
	/**
	 * Creates AbstractResident at home and assigns them that home.
	 * @param home AbstractResident's home
	 */
	public AbstractResident(Location home, Location work){
		this.id = Long.toString(++counter);
		this.home = home;
		this.work = work;
		this.currentLocation = home;
		this.drive = null;
	}
	
	public AbstractResident(Location home){
		this(home, null);
	}
	

	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#planDrives(java.time.LocalDateTime, com.underplex.tranopolis.DrivableGraph, java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public abstract Set<Drive> planDrives(LocalDateTime currentTime, DrivableGraph graph, LocalDateTime begin, LocalDateTime end);
	
	
	@Override
	public void startDrive(Drive drive){
		this.drive = drive;
		this.currentLocation = null;
	}
	
	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#getHome()
	 */
	@Override
	public Location getHome() {
		return home;
	}
	
	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#getWork()
	 */
	@Override
	public Location getWork() {
		return work;
	}
	
	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#setHome(com.underplex.tranopolis.Location)
	 */
	@Override
	public void setHome(Location home) {
		this.home = home;
	}
	
	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#setWork(com.underplex.tranopolis.Location)
	 */
	@Override
	public void setWork(Location work) {
		this.work = work;
	}

	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#getCurrentLocation()
	 */
	@Override
	public Location getCurrentLocation() {
		return currentLocation;
	}

	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#setCurrentLocation(com.underplex.tranopolis.Location)
	 */
	@Override
	public void setCurrentLocation(Location location) {
		this.currentLocation = location;
	}	
	
	/* (non-Javadoc)
	 * @see com.underplex.tranopolis.Resident#toString()
	 */
	@Override
	public String toString(){
		return "Resident " + id;
	}
	
	@Override
	public boolean isAt(OnOffPoint onOffPoint){
		if (this.getCurrentLocation() != null &&
				this.getCurrentLocation().equals(onOffPoint)){
			return true;
		}
		return false;
	}

}
