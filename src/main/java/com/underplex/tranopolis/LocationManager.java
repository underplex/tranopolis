package com.underplex.tranopolis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Manages and creates the Location instances for a given City instance.
 * <p>
 * Implements the rules and regulations for creating Locations.
 * 
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class LocationManager {

	private static long locationCounter = 0;
	private final Set<Location> locations;
	private final City city;
	private final Set<Location> labels;
	
	public LocationManager(City city) {
		this.city = city;
		this.locations = new HashSet<>();
		this.labels = new HashSet<>();
	}
	
	/**
	 * Creates and returns a <tt>Location</tt> formed by <tt>lots</tt> or returns <tt>null</tt> if not possible.
	 * <p>
	 * One reason this might fail is if one of the <tt>Lot</tt>s passed is already associated with an existing Location.
	 * Another is if a lot is paved.
	 * <p>
	 * The Set of Lots may be changed.
	 * <p>
	 * @param lots Set of Lots to form the Location
	 * @return <tt>Location</tt> associated with some lots or null if the Location can't be created
	 */
	public Location makeLocation(Set<Lot> lots, String label){
		Location loc = null;
		boolean okay = true;
		for (Location existing : this.locations){
			if (Sets.intersection(lots, existing.getLots()).size() != 0){
				okay = false;
				break;
			}
		}
		for (Lot lot : lots){
			if (lot.isPaved()){
				okay = false;
				break;
			}
		}
		
		if (okay){
			if (label == null || label.equals("")){
				loc = new Location(this.city, lots, Long.toString(++locationCounter));
				
			} else {
				loc = new Location(this.city, lots, Long.toString(++locationCounter), label);

			}
			this.locations.add(loc);

		}
		return loc; 
	}
	
	/**
	 * Creates and returns a <tt>Location</tt> formed by <tt>lots</tt> or returns <tt>null</tt> if not possible.
	 * <p>
	 * One reason this might fail is if one of the <tt>Lot</tt>s passed is already associated with an existing Location.
	 * Another is if a lot is paved.
	 * <p>
	 * The Set of Lots may be changed.
	 * <p>
	 * @param lots Set of Lots to form the Location
	 * @return <tt>Location</tt> associated with some lots or null if the Location can't be created
	 */
	public Location makeLocation(Set<Lot> lots){
		return this.makeLocation(lots, null);
	}
	
	public Location makeLocation(Lot lot){
		return this.makeLocation(Collections.singleton(lot));
	}
	
	public Location makeLocation(Lot lot, String label){
		return this.makeLocation(Collections.singleton(lot), label);
	}
	
	/**
	 * Returns defensive copy of the Set of all Locations.
	 * @return defensive copy of the Set of all Locations
	 */
	public Set<Location> getLocations(){
		return new HashSet<>(this.locations);
	}
	
	/**
	 * Returns Set of all Lots used by any Locations to connect to the road network.
	 * @return
	 */
	public Set<Lot> connectionLots(){
		Set<Lot> r = new HashSet<>();
		for (Location loc : this.locations){
			r.addAll(loc.getConnections());
		}
		return r;
	}
	
	/**
	 * Returns Map with keys representing all Locations, and keys representing each Location's set of connection Lots.
	 * @return
	 */
	public Map<Location, Set<Lot>> connectionMap(){
		Map<Location, Set<Lot>> map = new HashMap<>();
		for (Location loc : this.locations){
			map.put(loc, loc.getConnections());
		}
		return map;
	}

	/**
	 * Returns all finished Drives in all Locations.
	 * @return
	 */
	public Set<Drive> getFinishedDrives(){
		Set<Drive> rSet = new HashSet<>();
		for (Location l : this.locations){
			rSet.addAll(l.getFinishedDrives());
		}
		return rSet;
	}
	
	/**
	 * Clears all references to finished Drives in all Locations, and returns true iff any of them dereferenced Drives this way.
	 * @return
	 */
	public boolean dumpFinishedDrives(){
		boolean rBool = false;
		for (Location l : this.locations){
			if (l.dumpFinishedDrives()){
				rBool = true;
			}
		}
		return rBool;
	}
	
	public void extensiveReport(){
		System.out.println("LocationManager recognizes " + locations.size() +" location(s):");
		for (Location loc : locations){
			System.out.println("____ " + loc.toString());
		}
	}

}