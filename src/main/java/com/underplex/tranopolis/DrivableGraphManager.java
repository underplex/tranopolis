package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages all the <tt>DrivableGraph</tt> instances associated with a single instance of a <tt>City</tt>.
 * <p>
 * Also manages parts of the drivable graph that are not locations, specifically Xings and Roads.
 * 
 * @author Brandon Irvine, brandon@underplex.com
 */
public class DrivableGraphManager {

	private final City city;
	private DrivableGraph currentGraph;
	private Set<Xing> xings;
	private Set<Road> roads;
	
	public DrivableGraphManager(City city){
		this.city = city;
		this.currentGraph = null;
		this.roads = new HashSet<>();
		this.xings = new HashSet<>();
	}

	/**
	 * Returns the current graph or <tt>null</tt> if it hasn't been set yet.
	 * @return <tt>DrivableGraph</tt> representing the current road graph
	 */
	public DrivableGraph getCurrentGraph() {
		return currentGraph;
	}
	
	/**
	 * Update the internal representation of the road graph.
	 */
	public void updateGraph(){
		// update set of Xings
		// update set of Roads
		this.currentGraph = GraphFinder.findDrivableGraph(city);
	}

	public City getCity() {
		return city;
	}

	/**
	 * Returns defensive copy of set of Xings.
	 * @return
	 */
	public Set<Xing> getXings() {
		return new HashSet<>(xings);
	}

	/**
	 * Returns defensive copy of set of Roads.
	 * @return
	 */
	public Set<Road> getRoads() {
		return new HashSet<>(roads);
	}
	
}
