package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents an individual living and working in the city.
 * <p>
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public abstract class Resident {

	private static long counter = 0;
	private final String id;
	private Location home; // where person lives
	private Location work; // where person works
	private Location currentLocation; // current currentLocation
	
	/**
	 * Creates Resident at home and assigns them that home.
	 * @param home Resident's home
	 */
	public Resident(Location home){
		this.id = Long.toString(++counter);
		this.home = home;
		this.work = null;
		this.currentLocation = home;
	}

	/**
	 * Returns Drives this Resident wants to take beginning any time at or after <tt>begin</tt> and before <tt>end</tt>.
	 * <p>
	 * Note that this means the earlier point is inclusive and the later point is exclusive.
	 * <p>
	 * It's assumed that this Resident may never be asked again to plan for the given period, so this is probably it's one and only chance to submit Drive plans for
	 * the given time.
	 * <p>
	 * Any number of Drives may be returned this way.
	 * <p>
	 * Drives may not be executed if the road networks changes in some way between the <tt>currentTime</tt> and the time the Drive would begin.
	 * <p>
	 * With that qualification, <tt>graph</tt> is taken to be the road network that will be in effect between <tt>begin</tt> and <tt>end</tt>.
	 * <p>
	 * Drives may not be started on time if this Resident is still in the middle of another <tt>Drive</tt>. 
	 * In this case, the second Drive will be started as soon as the first is complete.
	 * <p>
	 * If a Drive has the Resident beginning from a place that he/she isn't currently at, the Drive will be simply be ignored as though it had never been planned.
	 * <p>
	 * No element of the returned Set may be <tt>null</tt> and if no Drives are planned, the returned Set must be empty.
	 * @param currentTime LocalDateTime representing current time
	 * @param graph RoadGraph for the given period
	 * @param beginTime LocalDateTime the time to begin
	 * @param endTime LocalDateTime the time to end
	 * @return Drives this Resident wants to take between the times of <tt>begin</tt> and <tt>end</tt>
	 */
	public abstract Set<Drive> planDrives(LocalDateTime currentTime, RoadGraph graph, LocalDateTime begin, LocalDateTime end);
	
	public Location getHome() {
		return home;
	}
	
	public Location getWork() {
		return work;
	}
	
	public void setHome(Location home) {
		this.home = home;
	}
	
	public void setWork(Location work) {
		this.work = work;
	}

	/**
	 * Returns Location if this Resident is not doing a Drive or null if it is 
	 * @return Location if this Resident is not doing a Drive or null if it is
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location location) {
		this.currentLocation = location;
	}	
	
	public String getId() {
		return id;
	}

	@Override
	public String toString(){
		return "Resident " + id;
	}
}
