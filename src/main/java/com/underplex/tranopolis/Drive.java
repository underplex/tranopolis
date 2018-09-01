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
	
	private static final int DEFAULT_DROP_SECONDS = 60 * 60;
	
	private final GraphPath<Drivable, Drivable> path; // primary path to be used, minus any other information
	private final LocalDateTime attemptStartTime; // earliest time the Drive should start, if possible; otherwise will begin as soon as possible
	private final LocalDateTime dropTime; // latest time the Drive could begin, if possible; after this time this Drive will not even be started
	private DriveDisposition disposition;

	private LocalDateTime actualStartTime;


	private LocalDateTime actualEndTime;
	private final Resident driver;
	private final List<Drivable> route;
	private final OnOffPoint onPoint;
	private final OnOffPoint offPoint;

	public Drive(GraphPath<Drivable, Drivable> path, 
			Resident driver,
			LocalDateTime startTime,
			Location startLocation,
			Location endLocation,
			LocalDateTime dropTime) {
		
		if (path == null) throw new IllegalArgumentException("The path argument cannot be null.");
		if (path.getEdgeList().size() < 1) throw new IllegalArgumentException("GraphPath must have at least 1 edge.");
		if (driver == null) throw new IllegalArgumentException("The driver argument cannot be null.");
		if (startTime == null) throw new IllegalArgumentException("The attemptStartTime argument cannot be null.");
		if (dropTime == null) throw new IllegalArgumentException("The dropTime argument cannot be null.");
		if (startLocation == null) throw new IllegalArgumentException("The startLocation argument cannot be null.");
		if (endLocation == null) throw new IllegalArgumentException("The endLocaton argument cannot be null.");
		
		if (path.getEdgeList().size() < 1) throw new IllegalArgumentException("GraphPath must have at least 1 edge.");

		this.path = path;
		this.driver = driver;
		this.attemptStartTime = startTime;
		this.actualEndTime = null;
		this.route = new ArrayList<>();
		this.onPoint = startLocation;
		this.offPoint = endLocation;
		this.dropTime = dropTime;
		this.disposition = DriveDisposition.WAITING;
		
		// notice that a Drive has to have at least 1 edge...
		
		
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
	
	public Drive(GraphPath<Drivable, Drivable> path, 
			Resident driver,
			LocalDateTime startTime,
			Location startLocation,
			Location endLocation) {
		this(path, driver, startTime, startLocation, endLocation, startTime.plusSeconds(DEFAULT_DROP_SECONDS));

	}

	/**
	 * If possible, make this <tt>Drive</tt> finished, in which case it is immutable. Returns true iff this method actually made this finished.
	 * @param time end time of this drive
	 * @return true iff this method actually made this finished
	 */
	public boolean finish(LocalDateTime time){
		boolean rVal = false;
		if (this.actualEndTime == null && disposition == DriveDisposition.BEGUN){
			this.actualEndTime = time;
			this.disposition = DriveDisposition.FINISHED;
			rVal = true;
		}		
		return rVal;
	}
	
	/**
	 * Notifies this Drive that it has begun. Returns truee iff this method actually began the Drive.
	 * @param time
	 * @return
	 */
	public boolean begin(LocalDateTime time){
		boolean rVal = false;
		if (this.actualStartTime == null && disposition == DriveDisposition.WAITING){
			this.actualStartTime = time;
			this.disposition = DriveDisposition.BEGUN;
			rVal = true;
		}		
		return rVal;
	}

	/**
	 * If possible, make this <tt>Drive</tt> dropped, in which case it is immutable. Returns true iff this method actually made this dropped.
	 * @return true iff this method actually made this dropped
	 */
	public boolean drop(){
		boolean rVal = false;
		if (disposition == DriveDisposition.WAITING){
			this.disposition = DriveDisposition.DROPPED;
			rVal = true;
		}		
		return rVal;
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
	
	public LocalDateTime getAttemptStartTime() {
		return attemptStartTime;
	}

	public String toString(){
		return "Drive (" + this.driver +  " from " + this.getGraphPath().getStartVertex() + " to " + this.getGraphPath().getEndVertex() + ")"; 
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
	
	public DriveDisposition getDisposition() {
		return disposition;
	}

	/**
	 * Time after which this Drive should not be started.
	 * <p>
	 * Null is never returned.
	 * @return LocalDateTime.
	 */
	public LocalDateTime getDropTime() {
		return dropTime;
	}
	
	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}

	public LocalDateTime getActualEndTime() {
		return actualEndTime;
	}

}


