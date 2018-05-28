package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.GraphPath;

/**
 * Represents a single drive/trip by a single person with a defined way to get from start to end.
 * <p>
 * @author Brandon Irvine, brandon@underplex.com
 */
public class Drive implements Comparable<Drive>{

	private final GraphPath<Xing, Road> path; // primary path to be used, minus any other information
	private final LocalDateTime startTime;
	private LocalDateTime endTime;
	private final Resident driver;
	private final Map<Xing, Road> outgoingRoads;
	private final Location startLocation;
	private final Location endLocation;
	
	public Drive(GraphPath<Xing, Road> path, 
			Resident driver, 
			LocalDateTime startTime,
			Location startLocation,
			Location endLocation) {
		this.path = path;
		this.driver = driver;
		this.startTime = startTime;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.endTime = null;
		this.outgoingRoads = new HashMap<>();
		for (Road road : path.getEdgeList()){
			outgoingRoads.put(road.getSourceXing(), road);
		}
	}

	public Xing getStartVertex(){
		return path.getStartVertex();
	}
	
	public Xing getEndVertex() {
		return path.getEndVertex();
	}

	/**
	 * Marks this <tt>Drive</tt> as finished, in which case it is immutable. Returns true iff this method actually made this finished, or false if it was already finished.
	 * @param time end time of this drive
	 * @return true iff this method actually made this finished
	 */
	public boolean finish(LocalDateTime time){
		if (this.endTime == null){
			this.endTime = time;
		}
		return false;
	}
	
	public boolean isFinished(){
		return (this.endTime != null);
	}

	/**
	 * Given Xing, return the outgoing <tt>Road</tt> to be taken or null if Xing is the end of this <tt>Drive</tt>.
	 * @param xing Xing from which we need outgoing Road
	 * @return the outgoing <tt>Road</tt> to be taken or null if Xing is the end of this
	 */
	public Road getOutgoingRoad(Xing xing){
		return this.outgoingRoads.get(xing);
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public String toString(){
		return "Drive: " + driver.toString() + "; " + this.getStartVertex() + ", " + this.getEndVertex() + " starting at " + this.startTime; 
	}

	public GraphPath<Xing, Road> getGraphPath(){
		return this.path;
	}
	
	@Override
	public int compareTo(Drive other) {
		Double w = this.path.getWeight();
		return w.compareTo(other.getGraphPath().getWeight());
	}

	public Resident getDriver() {
		return driver;
	}
}


