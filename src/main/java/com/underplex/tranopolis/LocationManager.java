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
	
	public LocationManager(City city) {
		this.city = city;
		this.locations = new HashSet<>();
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
			loc = new Location(this.city, lots, Long.toString(++locationCounter));
			this.locations.add(loc);
		}
		return loc; 
	}
	
	public Location makeLocation(Lot lot){
		return this.makeLocation(Collections.singleton(lot));
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

	public void extensiveReport(){
		System.out.println("LocationManager recognizes " + locations.size() +" location(s):");
		for (Location loc : locations){
			System.out.println("____ " + loc.toString());
		}
	}

}