package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
	private final Set<Resident> residents;
	private final String id;
	private final Map<Drivable, List<Drive>> turnOns; // exits and their associated drives
    private final Set<Drive> done;
    private final String label;

    /**
     * Generally, not the preferred way to create a Lot. Use LocationManager.makeLocation instead.
     * @param city
     * @param lots
     * @param id
     */
	public Location(City city, Set<Lot> lots, String id, String label){
		this.city = city;
		this.lots = new HashSet<>(lots);
		this.connections = new HashSet<>();
		this.residents = new HashSet<>();
		this.id = id;
		this.done = new HashSet<>();
		this.turnOns = new HashMap<Drivable, List<Drive>>();
		this.label = label;
	}
	
    /**
     * Generally, not the preferred way to create a Lot. Use LocationManager.makeLocation instead.
     * @param city
     * @param lots
     * @param id
     */
	public Location(City city, Set<Lot> lots, String id){
		this(city, lots, id, "Location" + id);
	}

    /**
     * Generally, not the preferred way to create a Lot. Use LocationManager.makeLocation instead.
     * @param city
     * @param lots
     * @param id
     */
	public Location(City city, Lot lot, String id, String label){
		this(city, Collections.singleton(lot), id, label);
	}
	
    /**
     * Generally, not the preferred way to create a Lot. Use LocationManager.makeLocation instead.
     * @param city
     * @param lots
     * @param id
     */
	public Location(City city, Lot lot, String id){
		this(city, Collections.singleton(lot), id, "Location " + id);
	}
	
	/**
	 * Adds Resident and returns true as if this were a Set.
	 * @param resident
	 * @return
	 */
	public boolean addResident(Resident resident){
		return residents.add(resident);
	}
	
	/**
	 * Removes Resident and returns boolean as if this were a Set.
	 * @param resident
	 * @return
	 */
	public boolean removeResident(Resident resident){
		return residents.remove(resident);
	}

	/**
	 * Attempts to add a Lot and returns true iff it is.
	 * <p>
	 * Lots can only be added if they are built.
	 * <p>
	 * Adding a second reference of a Lot already added will return false.
	 * @param lot
	 * @return
	 */
	public boolean addLot(Lot lot){
		if (lot.isBuilt()){
			return lots.add(lot);
		}
		return false;
	}
	
	/**
	 * Adds a connection to the road network at Lot, returns true iff this added a new Lot to the set of connections.
	 * <p>
	 * This will fail if the Lot is not paved, or is not adjacent to any Lot in the Location.
	 * <p>
	 * Also, this will return false if attempting to add a connection that is already established.	 
	 * 
	 */
	public boolean addConnection(Lot lot){
		
		boolean flag = false;
		for (Lot myLot : this.lots){
			if (city.getLotManager().getNeighbors(myLot).contains(lot)){
				flag = true;				
			}
		}
		
		if (flag && lot.isPaved()){
			return this.connections.add(lot);
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
	public Set<Resident> getResidents(){
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
	public boolean dumpFinishedDrives() {
		if (this.done.isEmpty()){
			return false;
		} else {
			this.done.clear();
		}
		return true;
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
		// LOGGER.info("Attempting to add " + drive + " to turn ons.");
		Drivable out = drive.getGraphPath().getEdgeList().get(0);
		this.removeResident(drive.getDriver());
		// the driver is physically removed from the location and becomes a "drive"
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
				d.getDriver().setCurrentLocation(this);
				this.addResident(d.getDriver());
				LOGGER.info(d + " finishes at " + this);
			} else {
				Drivable r = d.next(this);
				LOGGER.info(d + " drives into " + this);
				
				if (r == null)
					throw new IllegalArgumentException("One of the drives has nowhere to go from this Location.");
				
				// LOGGER.info(d + " attempts to turn onto " + r);
	
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
		
		// attempts to simply move the Drives through, much like an Xing
		// now actually attempt to move drive off...
		for (Drivable r : map.keySet()){
			// LOGGER.info(map.get(r).size() + " car(s) attempting to turn onto " + r); 
			Set<Drive> localRejects = r.take(map.get(r), time);
			// LOGGER.info(localRejects.size() + " car(s) cannot turn onto " + r); 
			rejects.addAll(localRejects);
		}

		return rejects;
	}

	@Override
	public void flow(LocalDateTime time) {
		// LOGGER.info(this + " flow is triggered.");
		for (Drivable d : turnOns.keySet()){
			List<Drive> drives = new ArrayList<>(turnOns.get(d));
			// sort by time...
			drives.sort(
					(Drive drive1, Drive drive2) -> drive1.getAttemptStartTime().compareTo(drive2.getAttemptStartTime()));
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

	public String toString(){
		return "Location " + id + " (" + label + ")";
	}

	public int getNumberOfLots(){
		return this.lots.size();
	}
	
	public Set<Drive> getDrives(){
		Set<Drive> rSet = new HashSet<>();
		for (Drivable destination : turnOns.keySet()){
			rSet.addAll(turnOns.get(destination));
		}
		return rSet;
	}
	
}
