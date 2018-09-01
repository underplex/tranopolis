package com.underplex.tranopolis;

import java.util.Queue;
import java.util.Set;

/**
 * Represents a place where a <tt>Drive</tt> can get on the road network or get off the road network. Must also itself be a part of the road network.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public interface OnOffPoint extends Drivable {
	
	/**
	 * Adds all the <tt>Drive</tt>s to this <tt>OnOffPoint</tt>.
	 * <p>
	 * This <tt>OnOffPoint</tt> cannot reject these.
	 * <p>
	 * This <tt>OnOffPoint</tt> might throw an <tt>IllegalArgumentException</tt> if one of the <tt>Drive</tt> elements is judged invalid, on the basis of constituting an impossible state
	 * for the traffic network to be in.
	 * <p>
	 * All <tt>Drive</tt> elements are assumed to be ready to begin their journey as soon as possible.
	 * <p>
	 * The only criterion this OnOffPoint will use to order Drives is their start time. No other order is guaranteed.
	 * @param drives <tt>Drive</tt>s to add to this <tt>OnOffPoint</tt>
	 */
	void turnOn(Queue<Drive> drives);

	/**
	 * Add a <tt>Drive</tt>s to this <tt>OnOffPoint</tt>.
	 * <p>
	 * This <tt>OnOffPoint</tt> cannot reject a <tt>Drive</tt>.
	 * <p>
	 * All <tt>Drive</tt> elements are assumed to be ready to begin their journey as soon as possible.
	 * <p>
	 * The only criterion this OnOffPoint will use to order Drives is their start time. No other order is guaranteed.
	 * @param drive <tt>Drive</tt> to add to this <tt>OnOffPoint</tt>
	 */
	void turnOn(Drive drive);

	/**
	 * Returns all Drives completed at this <tt>OnOffPoint</tt>.
	 * <p>
	 * The <tt>Set</tt> returned can be changed without any change to the underlying Set; it is meant to be a defensive copy of any internal representation.
	 */ 
	Set<Drive> getFinishedDrives();
	
	/**
	 * Clears all references to finished drives. Used for memory management.
	 * <p>
	 * Returns true iff any references were dropped this way. 
	 */
	boolean dumpFinishedDrives();
}
