package com.underplex.tranopolis;

import java.util.Queue;
import java.util.Set;

/**
 * Represents a place where a <tt>Drive</tt> can get on the road network or get off the road network.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public interface OnOffPoint {
	
	/**
	 * Adds all the <tt>Drive</tt>s to this <tt>OnOffPoint</tt>.
	 * <p>
	 * This <tt>OnOffPoint</tt> cannot reject these.
	 * @param drives <tt>Drive</tt>s to add to this <tt>OnOffPoint</tt>
	 */
	void turnOn(Queue<Drive> drives);

	/**
	 * Add a <tt>Drive</tt>s to this <tt>OnOffPoint</tt>.
	 * <p>
	 * This <tt>OnOffPoint</tt> cannot reject a <tt>Drive</tt>.
	 * <p>
	 * This <tt>OnOffPoint</tt> might throw an <tt>IllegalArgumentException</tt> if one of the <tt>Drive</tt> elements is judged invalid, on the basis of constituting an impossible state
	 * for the traffic network to be in.
	 * @param drive <tt>Drive</tt> to add to this <tt>OnOffPoint</tt>
	 */
	void turnOn(Drive drive);

	/**
	 * Returns all Drives completed at this <tt>OnOffPoint</tt>.
	 * <p>
	 * The <tt>Set</tt> returned may be changed; it is meant to be a defensive copy of any internal representation
	 * of the <tt>Drive</tt>s.
	 */ 
	Set<Drive> getFinishedDrives();
}
