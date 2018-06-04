package com.underplex.tranopolis;

import java.util.HashSet;
import java.util.Set;

/**
 * Location in a <tt>City</tt> that is a place for living, working, or recreation.
 * <p>
 * A Location wouldn't be, for example, on a highway, which is represented as a road network.
 * <p>
 * Conceptually, a <tt>Location</tt> is where <tt>BasicResident</tt>s can be; <tt>BasicResident</tt>s' exact place within a Location is not specified.
 * <p>
 * There is no guarantee that the Lots provided to the contructor are valid or a good implementation of the rules used to define a Location.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class Location {

	private final City city;
	private final Set<Lot> lots;
	private final Set<BasicResident> residents;
	private final Set<Xing> onOffPoints;
	private final String id;
	
	public Location(City city, Set<Lot> lots, String id){
		this.city = city;
		this.lots = new HashSet<>(lots);
		this.residents = new HashSet<>();
		this.onOffPoints = new HashSet<>();
		this.id = id;
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
	 * Removes Lot and returns boolean as if this were a Set.
	 * @param lot
	 * @return
	 */
	public boolean removeLot(Lot lot){
		return lots.remove(lot);
	}	
	
	/**
	 * Returns true iff adding xing to a Set would return true.
	 * @param xing
	 * @return
	 */
	public boolean addOnOffPoint(Xing xing){
		return this.onOffPoints.add(xing);
	}
	
	/**
	 * Returns true iff removing xing from a Set would return true.
	 * @param xing
	 * @return
	 */
	public boolean removeOnOffPoint(Xing xing){
		return this.onOffPoints.remove(xing);
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

	/**
	 * Returns defensive copy of on-off points for this location.
	 * @return
	 */
	public Set<Xing> getOnOffPoints(){
		return new HashSet<>(onOffPoints);
	}
	
}
