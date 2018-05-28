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
 * Manages all the <tt>RoadGraph</tt> instances associated with a single instance of a <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class RoadGraphManager {

	private final City city;
	private RoadGraph currentGraph;
	
	public RoadGraphManager(City city){
		this.city = city;
		this.currentGraph = null;
	}

	/**
	 * Returns the current graph or <tt>null</tt> if it hasn't been set yet.
	 * @return <tt>RoadGraph</tt> representing the current road graph
	 */
	public RoadGraph getCurrentGraph() {
		return currentGraph;
	}
	
	/**
	 * Update the internal representation of the road graph.
	 */
	public void updateGraph(){
		this.currentGraph = GraphFinder.findRoadGraph(city);
	}
	
}
