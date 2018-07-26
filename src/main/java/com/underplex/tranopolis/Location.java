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

import com.google.common.collect.Lists;

/**
 * Location in a <tt>City</tt> that is a place for living, working, or recreation.
 * <p>
 * A Location wouldn't be, for example, on a highway, which is represented as a road network.
 * <p>
 * Conceptually, a <tt>Location</tt> is where <tt>BasicResident</tt>s can be; <tt>BasicResident</tt>s' exact place within a Location is not specified.
 * <p>
 * There is no guarantee that the Lots provided to the constructor are valid or a good implementation of the rules used to define a Location.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class Location implements OnOffPoint, Drivable {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final City city;
	private final Set<Lot> connections;
	private final Set<Lot> lots;
	private final Set<BasicResident> residents;
	private final String id;
	private final Map<Drivable, List<Drive>> turnOns; // exits and their associated drives
    private final Set<Drive> done;

	public Location(City city, Set<Lot> lots, String id){
		this.city = city;
		this.lots = new HashSet<>(lots);
		this.connections = new HashSet<>();
		this.residents = new HashSet<>();
		this.id = id;
		this.done = new HashSet<>();
		this.turnOns = new HashMap<Drivable, List<Drive>>();
	}
	
	/**
	 * Adds BasicResident and returns true as if this were a Set.
	 * @param resident
	 * @return
	 */
	public boolean addResident(BasicResident resident){
		return residents.add(resident);
	}
	
	/**
	 * Removes BasicResident and returns boolean as if this were a Set.
	 * @param resident
	 * @return
	 */
	public boolean removeResident(BasicResident resident){
		return residents.remove(resident);
	}

	/**
	 * Adds Lot and returns true as if this were a Set.
	 * @param lot
	 * @return
	 */
	public boolean addLot(Lot lot){
		return lots.add(lot);
	}
	
	/**
	 * Adds a connection to the road network at Lot, returns true iff this was successful.
	 */
	public boolean addConnection(Lot lot){
		
		if (lot.isPaved()){
			this.connections.add(lot);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes Lot and returns boolean as if this were a Set.
	 * @param lot
	 * @return
	 */
	public boolean removeLot(Lot lot){
		return lots.remove(lot);
	}	
	
	/**
	 * Returns defensive copy of Set of Residents currently at this Location.
	 * @return defensive copy of Set of Residents currently at this Location
	 */
	public Set<BasicResident> getResidents(){
		return new HashSet<>(residents);
	}
	
	/**
	 * Returns defensive copy of Set of Lots currently at this Location.
	 * @return defensive copy of Set of Lots currently at this Location
	 */
	public Set<Lot> getLots(){
		return new HashSet<>(lots);
	}

	@Override
	public Set<Drive> getFinishedDrives() {
		return new HashSet<Drive>(this.done);
	}
	
	@Override
	/**
	 * Adds Drive instances to this Location to move eventually onto the network of roads.
	 * <p>
	 * Drives are all guaranteed to be legal and valid.
	 */
	public void turnOn(Queue<Drive> drives) {
		for (Drive d : drives){
			turnOn(d);
		}
	}	

	@Override
	/**
	 * Adds Drive instance to this Location to move eventually onto the network of roads.
	 * <p>
	 * Drive is guaranteed to be legal and valid.
	 */	
	public void turnOn(Drive drive) {
		Drivable out = drive.getGraphPath().getEdgeList().get(0);
		if (this.turnOns.keySet().contains(out)){
			turnOns.get(out).add(drive);
		} else {
			turnOns.put(out, Lists.newArrayList(drive));
		}
	}

	@Override
	public Set<Drive> take(Queue<Drive> drives, LocalDateTime time) {
		if (time == null || drives == null ) throw new IllegalArgumentException("Arguments to take method cannot be null.");

		Map<Drivable, Queue<Drive>> map = new HashMap<Drivable, Queue<Drive>>();
				
		while (!drives.isEmpty()){
			Drive d = drives.remove();

				
			if (this.equals(d.getEnd())){
				d.finish(time);
				this.done.add(d);
			} else {
				Drivable r = d.next(this);
				
				if (r == null)
					throw new IllegalArgumentException("One of the drives has nowhere to go from this Location.");
				
				LOGGER.info(d + " attempts to turn onto " + r);
	
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
		for (Drivable r : map.keySet()){
			LOGGER.info(map.get(r).size() + " car(s) attempting to turn onto " + r); 
			Set<Drive> localRejects = r.take(map.get(r), time);
			LOGGER.info(localRejects.size() + " car(s) cannot turn onto " + r); 
			rejects.addAll(localRejects);
		}

		return rejects;
	}

	@Override
	public void flow(LocalDateTime time) {
		for (Drivable d : turnOns.keySet()){
			List<Drive> drives = new ArrayList<>(turnOns.get(d));
			// sort by time...
			drives.sort(
					(Drive drive1, Drive drive2) -> drive1.getStartTime().compareTo(drive2.getStartTime()));
			List<Drive> rejects = new ArrayList<Drive>(d.take(new ArrayDeque<Drive>(drives), time));
			turnOns.put(d, rejects);			
		}
	}
	
	/**
	 * Return Set of Lots where this Location would connect to the network of Drivable things.
	 * @return
	 */
	public Set<Lot> getConnections() {
		return connections;
	}
}
