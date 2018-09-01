package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Set;

/**
 * Represents an aspect of a road network that can direct and process traffic, each vehicle of which is represented by an instance of <tt>Drive</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public interface Drivable {

	/**
	 * Attempts to take as many of <tt>Drive</tt>s as possible and returns <tt>Drive</tt>s that can't be taken onto this.
	 * <p>
	 * The <tt>Queue</tt> passed might be changed by this method. An empty <tt>Queue</tt> can be passed.
	 * <p>
	 * The <tt>time</tt> is provided so that this <tt>Drivable</tt> does not need to keep its 
	 * own internal time.
	 * @param drives <tt>Queue</tt> of <tt>Drive</tt>s to attempt to move onto this; cannot be null; may be mutated
	 * @param time <tt>LocalDateTime</tt> in the current system when this process begins
	 * @return <tt>Drive</tt>s that can't be taken onto this
	 */
	public Set<Drive> take(Queue<Drive> drives, LocalDateTime time);
	
	/**
	 * Moves as many <tt>Drive</tt> elements as possible off this <tt>Drivable</tt>.
	 * <p>
	 * Vehicles scheduled to move off this <tt>Drivable</tt> before and at <tt>time</tt> should be allowed to do so if possible. More importantly, vehicles scheduled to move
	 * off after <tt>time</tt> should not be moved.
	 * @param time <tt>LocalDateTime</tt> that this process begins
	 */
	public void flow(LocalDateTime time);
	
	/**
	 * Returns number of lots that traversing this Drivable would represent.
	 * <p>
	 * Generally, represents the number of lots this takes up.
	 */
	public int getNumberOfLots();

	/**
	 * Returns Drives that are currently at or on this Drivable.
	 */
	public Set<Drive> getDrives();

}
