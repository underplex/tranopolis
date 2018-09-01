package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Set;

public interface Resident {

	/**
	 * Returns Drives this Resident wants to take beginning any time at or after <tt>begin</tt> and before <tt>end</tt>.
	 * <p>
	 * Note that this means the earlier point is inclusive and the later point is exclusive.
	 * <p>
	 * It's assumed that this Resident may never be asked again to plan for the given period, so the invocation of this method may be its one and only chance to submit Drive plans for
	 * the given time.
	 * <p>
	 * Any number of Drives may be returned this way.
	 * <p>
	 * There are no guarantees that Drives will be executed, especially if they are found to be invalid or illogical.
	 * <p>
	 * In particular, Drives may not be executed if the road network at the time the Drive begins is not the same as the one passed here.
	 * <p>
	 * With that qualification, <tt>graph</tt> is taken to be the road network that will be in effect between <tt>begin</tt> and <tt>end</tt>.
	 * <p>
	 * Similarly, if a Resident is in Location A but he attempts to start from Location B, the drive will fail.
	 * <p>
	 * Also, if the Drive can't be begun, it will drop and the Resident will have to plan another when queried again.
	 * <p>
	 * No element of the returned Set may be <tt>null</tt> and if no Drives are planned, the returned Set must be empty.
	 * @param currentTime LocalDateTime representing current time
	 * @param graph DrivableGraph for the given period
	 * @param startOfWindow LocalDateTime the beginning of the period when Drive can be requested
	 * @param endOfWindow LocalDateTime the end of the period (not including this time) that requested Drives must be before
	 * @return Drives this Resident wants to take at or after <tt>startOfWindow</tt> and before <tt>endOfWindow</tt>
	 */
	Set<Drive> planDrives(LocalDateTime currentTime, DrivableGraph graph, LocalDateTime startOfWindow, LocalDateTime endOfWindow);

	Location getHome();

	Location getWork();

	void setHome(Location home);

	void setWork(Location work);

	/**
	 * Returns Location if this Resident is not doing a Drive or null if it is 
	 * @return Location if this Resident is not doing a Drive or null if it is
	 */
	Location getCurrentLocation();

	void setCurrentLocation(Location location);
	
	/**
	 * Returns true if location is not null and this Resident is there.
	 * @param onOffPoint
	 * @return
	 */
	boolean isAt(OnOffPoint onOffPoint);
	
	/**
	 * Notify this Resident that they have started a drive.
	 * <p>
	 * It is assumed that this Resident equals Drive.getDriver().
	 */
	void startDrive(Drive drive);

}