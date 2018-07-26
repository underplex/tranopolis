package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;

/**
 * Represents a single Drive by a single Resident with a defined way to get from start to end.
 * <p>
 * @author Brandon Irvine, brandon@underplex.com
 */
public class Drive implements Comparable<Drive>{

	private final GraphPath<Drivable, Drivable> path; // primary path to be used, minus any other information
	private final LocalDateTime startTime; // time the Drive should start, if possible; otherwise will begin as soon as possible
	private LocalDateTime endTime;
	private final Resident driver;
	private final List<Drivable> route;
	private final OnOffPoint onPoint;
	private final OnOffPoint offPoint;
	
	public Drive(GraphPath<Drivable, Drivable> path, 
			Resident driver,
			LocalDateTime startTime,
			Location startLocation,
			Location endLocation) {
		this.path = path;
		this.driver = driver;
		this.startTime = startTime;
		this.endTime = null;
		this.route = new ArrayList<>();
		this.onPoint = startLocation;
		this.offPoint = endLocation;
		
		// notice that a Drive has to have at least 1 edge...
		
		if (path.getEdgeList().size() < 1) throw new IllegalArgumentException("GraphPath must have at least 1 edge.");
		
		boolean g = true;
		int i = 0;
		while (g){
			Drivable v = path.getVertexList().get(i);
			route.add(v);
			if (!v.equals(path.getEndVertex())){
				route.add(path.getEdgeList().get(i));
			} else {
				g = false;
			}
			i++;
		}
		
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
	 * Given Drivable, returns the next <tt>Drivable</tt> for the planned route or null if last is the end of this Drive or if last is not on this route.
	 * @param Drivable that is last item
	 * @return Drivable that is next item
	 */
	public Drivable next(Drivable last){
		Drivable r = null;
		int i = this.route.indexOf(last);
		if (i < route.size() - 1){
			r = route.get(i + 1);
		}
		return r;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public String toString(){
		return "Drive: " + driver.toString() + "; " + this.getGraphPath().getStartVertex() + ", " + this.getGraphPath().getEndVertex() + " starting at " + this.startTime; 
	}

	public GraphPath<Drivable, Drivable> getGraphPath(){
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
	
	public Drivable getStart(){
		return this.route.get(0);
	}
	
	public Drivable getEnd(){
		return this.route.get(this.route.size() - 1);
	}
	
	public OnOffPoint getOnPoint() {
		return onPoint;
	}

	public OnOffPoint getOffPoint() {
		return offPoint;
	}
}


