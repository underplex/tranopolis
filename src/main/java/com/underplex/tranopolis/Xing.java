package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;

/**
 * Represents an intersection or turn-on at a single <tt>Lot</tt>.
 * <p>
 * "Xing" is a common abbreviation for "crossing".
 * 
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Xing implements Drivable, OnOffPoint {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    private final Set<Drive> done;
	private final Lot lot;
	//private final Set<Road> outgoingEdges;
	private final List<Drive> turnOns;
	
	/**
	 * Constructs Xing.
	 * @param lot Lot represented by this Xing
	 */
	public Xing(Lot lot) {
		this.lot = lot;
		//this.outgoingEdges = new HashSet<Road>(outgoingEdges);
		this.turnOns = new ArrayList<>();
		this.done = new HashSet<Drive>();
	}	
	
	public Lot getLot() {
		return lot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lot == null) ? 0 : lot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Xing other = (Xing) obj;
		if (lot == null) {
			if (other.lot != null)
				return false;
		} else if (!lot.equals(other.lot))
			return false;
		return true;
	}


	@Override
	public Set<Drive> take(Queue<Drive> drives, LocalDateTime time) {
		if (time == null || drives == null ) throw new IllegalArgumentException("Arguments to take method cannot be null.");

		Map<Road,Queue<Drive>> map = new HashMap<Road,Queue<Drive>>();
				
		while (!drives.isEmpty()){
			Drive d = drives.remove();

			if (d.getEndVertex().equals(this)){
				// this drive is done!
				LOGGER.info(d + " finishes at " + time + " because it has reached " + this);
				d.finish(time);
				this.done.add(d);
				LOGGER.info(this + " has added a finished drive and now has finished drives of size " + this.done.size());
				
			} else {
				Road r = d.getOutgoingRoad(this);
				if (r == null){
					throw new IllegalArgumentException("One of the drives is not valid for this Xing.");
				}
				LOGGER.info(d + " attempts to turn onto " + r);

				// TODO: implement fatal error-throwing for when the drive doesn't actually use this road, since that shouldn't be possible under any circumstance

				// if we've already seen the road this drive wants to get onto...
				if (map.keySet().contains(r)){ 
					map.get(r).add(d);
		
				// if we haven't already seen the road this drive wants to get onto...	
				} else { 
					Queue<Drive> q = new ArrayDeque<Drive>();
					q.add(d);
					map.put(r,q); 
				}

			}
		}

		Set<Drive> rejects = new HashSet<>();
		
		// now actually attempt to move drive off...
		for (Road r : map.keySet()){
			LOGGER.info(map.get(r).size() + " car(s) attempting to turn onto " + r); 
			Set<Drive> localRejects = r.take(map.get(r), time);
			LOGGER.info(localRejects.size() + " car(s) cannot turn onto " + r); 
			rejects.addAll(localRejects);
		}

		return rejects;
	}

	@Override
	public void flow(LocalDateTime time) {
		List<Drive> ts = new ArrayList<>(this.turnOns);
		LOGGER.info("Turn ons includes: " + ts.toString());
		Set<Drive> rejects = this.take(new ArrayDeque<Drive>(ts), time);
		ts.removeAll(rejects);
		this.turnOns.removeAll(ts);
	}

	@Override
	public void turnOn(Queue<Drive> drives) {
		this.turnOns.addAll(drives);		
	}	

	@Override
	public void turnOn(Drive drive) {
		this.turnOns.add(drive);		
	}	

	public Set<Drive> getDrives(){
		return new HashSet<>(turnOns);
	}

	public String toString(){
		return "Xing at (" + lot.getX() + ", " + lot.getY() + ")";
	}

	@Override
	public Set<Drive> getFinishedDrives() {
		return new HashSet<Drive>(this.done);
	}
}
