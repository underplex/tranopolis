package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Set;

public interface Resident {

	/**
	 * Returns Drives this AbstractResident wants to take beginning any time at or after <tt>begin</tt> and before <tt>end</tt>.
	 * <p>
	 * Note that this means the earlier point is inclusive and the later point is exclusive.
	 * <p>
	 * It's assumed that this AbstractResident may never be asked again to plan for the given period, so this is probably its one and only chance to submit Drive plans for
	 * the given time.
	 * <p>
	 * Any number of Drives may be returned this way.
	 * <p>
	 * Drives may not be executed if the road network at the time the Drive begins is not the same as the one submitted here.	 * 
	 * <p>
	 * With that qualification, <tt>graph</tt> is taken to be the road network that will be in effect between <tt>begin</tt> and <tt>end</tt>.
	 * <p>
	 * Drives may not be started on time if this AbstractResident is still in the middle of another <tt>Drive</tt>. 
	 * In this case, the second Drive will be started (if still otherwise valid) as soon as the first is complete.
	 * <p>
	 * If a Drive has the AbstractResident beginning from a place that he/she isn't currently at, the Drive will be simply be ignored as though it had never been planned.
	 * <p>
	 * No element of the returned Set may be <tt>null</tt> and if no Drives are planned, the returned Set must be empty.
	 * @param currentTime LocalDateTime representing current time
	 * @param graph DrivableGraph for the given period
	 * @param beginTime LocalDateTime the time to begin
	 * @param endTime LocalDateTime the time to end
	 * @return Drives this AbstractResident wants to take between the times of <tt>begin</tt> and <tt>end</tt>
	 */
	Set<Drive> planDrives(LocalDateTime currentTime, DrivableGraph graph, LocalDateTime begin, LocalDateTime end);

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

}