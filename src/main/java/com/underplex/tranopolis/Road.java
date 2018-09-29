package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A one-way section of road serving as an edge in the road network.
 * <p>
 * This is a directed edge in the graph model used for the network of roads where <tt>Xing</tt>s are vertices. For this purpose, getSourceXing and getTargetXing should return the appropriate
 * Xings for a graph.
 * <p>
 * Note that this <tt>Drivable</tt> doesn't guarantee that <tt>Drives</tt> that are passing through it will leave in the same order they came on, even if are moving on to the same
 * Road after.
 * <p>
 * Note that Roads don't allow Drives to exit or enter the road network they're on.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class Road implements Drivable {
	
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
    protected final static double AVERAGE_CAR_M = 4.8; // the average length of a car in meters
    protected final static double MINIMUM_FOLLOW_M = 0.3; // the minimum distance between cars in meters
    protected final static double FOLLOW_SECONDS = 2.0; // the amount of time each car follows one another
    protected final static double DEFAULT_MAX_SPEED = 50;
    protected final static double MINIMUM_ROAD_LENGTH = AVERAGE_CAR_M * 5.0;
    
	private final List<Lot> segments; //ordered bunch of lots
	private final Drivable source;
	private final Drivable target;
	private final Set<Drive> drives;
	private final List<Eta> etas;
	private final double length;
	private final double maxSpeed; // speed limit in km/h

	/**
	 * Constructs a road.
	 * <p>
	 * The length of this Road in meters will be inferred, but may not be less than a defined constant.
	 * @param source
	 * @param target
	 * @param segments List ordered by the lots stemming from source and leading to target
	 */
	public Road(Drivable source, Drivable target, double lengthInM, int maxSpeed) {
		if (source == null || target == null){
			throw new IllegalArgumentException("Source or target of Road cannot be null.");
		}

		this.source = source;
		this.target = target;
		// order the segments according to the path they take from sourceXing to targetXing
		this.segments = new ArrayList<>();
		this.length = Math.min(lengthInM, MINIMUM_ROAD_LENGTH);
		
		this.maxSpeed = (double)maxSpeed;
		this.etas = new ArrayList<Eta>();
		this.drives = new HashSet<>();
	}

	/**
	 * Constructs a road.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target, double lengthInM) {
		this(source, target, lengthInM, (int)DEFAULT_MAX_SPEED);		
	}
	
	
	/**
	 * Constructs a road.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target, List<Lot> segments, int maxSpeed) {
		this(source, target, (double)segments.size() * Lot.LENGTH_IN_M, maxSpeed);
		this.segments.addAll(segments);
	}
	
	/**
	 * Constructs a road.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target, List<Lot> segments) {
		this(source, target, segments, (int)DEFAULT_MAX_SPEED);
	}

	/**
	 * Returns defensive copy of the list representing all the segments/lots making up this road.
	 * @return defensive copy of the list representing all the segments/lots making up this road 
	 */
	public List<Lot> getSegments() {
		return new ArrayList<>(segments);
	}

	public Drivable getSource() {
		return source;
	}

	public Drivable getTarget() {
		return target;
	}
	
	@Override
	public Set<Drive> take(Queue<Drive> merging, LocalDateTime time) {
		
		Queue<Drive> pushing = new ArrayDeque<Drive>(merging);
		
		while (canAdd(pushing.peek()) && pushing.size() > 0 ){
			
			Drive d = pushing.remove();
			LOGGER.info(d + " is added to " + this);
			this.drives.add(d);
			Eta e = new Eta(d, time.plusSeconds(this.estimateTravelSeconds(d)));
			this.etas.add(e);
		}
		
		return new HashSet<Drive>(pushing);
	}

	@Override
	public void flow(LocalDateTime time){

		Queue<Drive> transfer = new ArrayDeque<Drive>();

		// assume that the earlier etas have precedence over later etas
		// note that this doesn't assume that if you got on first you will get off first (b/c of passing)
		Collections.sort(etas);
		
		for (int i = 0; i < this.etas.size(); i++){
			if (etas.get(i).getTime().isBefore(time)){
				transfer.add(etas.get(i).getDrive());
			} else {
				break;
			}
		}
		
		// deal with transfers... pass them to the xing
		Set<Drive> rejected = this.target.take(new ArrayDeque<Drive>(transfer), time);
		
		transfer.removeAll(rejected); 
		
		Set<Eta> e = new HashSet<>();
		for (Eta eta : this.etas){
			if (transfer.contains(eta.getDrive()) ){
				e.add(eta);
			}
		}
		
		// update lists of etas and drives
		this.etas.removeAll(e);
		for (Eta drive : e){
			this.drives.remove(drive.getDrive());
		}
	}

	/**
	 * Returns number of seconds the drive is expected to take to drive the length of this Road.
	 * <p>
	 * Assumes that drive has already been added to the Road, so that number of drives is assumed to be >= 1.
	 * <p>
	 * Assumes that the total length of cars doesn't exceed length of road.
	 * <p>
	 * The drive is assumed to be not null.
	 * @param drive
	 * @return
	 */
	public long estimateTravelSeconds(Drive drive){
		double n = (double)getDrives().size();
		double d = length - AVERAGE_CAR_M; // default with 1-car case
		if (n > 1.0){
			d = (length - (n * AVERAGE_CAR_M))/(n - 1.0); // average distance between cars, leaving no room at ends
		}
		
		double v = Math.min((d/Road.FOLLOW_SECONDS), maxSpeed/3.6); // v is m/s
		
		long s = Math.max((long)1, (long)Math.round(length/v));
		return s;
	}
	
	/**
	 * Returns LocalDateTime best guess of time to traverse this road or null if Drive is not on this Road.
	 * @param drive
	 * @return
	 */
	public LocalDateTime getEta(Drive drive){
		for (Eta e : etas){
			if (e.getDrive().equals(drive)){
				return (e.getTime());
			}
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((segments == null) ? 0 : segments.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Road other = (Road) obj;
		if (segments == null) {
			if (other.segments != null)
				return false;
		} else if (!segments.equals(other.segments))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}	
	
	@Override
	public String toString(){
		return "Road from " + this.source + " to " + this.target;
	}
	
	private class Eta implements Comparable<Eta>{
		private final Drive drive;
		private final LocalDateTime time;
		
		public Eta(Drive drive, LocalDateTime time) {
			super();
			this.drive = drive;
			this.time = time;
		}

		public Drive getDrive() {
			return drive;
		}

		public LocalDateTime getTime() {
			
			return time;
		}

		@Override
		public int compareTo(Eta other) {
		
			return this.getTime().compareTo(other.getTime());
		}
	
		public String toString(){
			return "Eta at " + this.time + " for " + this.drive;
		}
	}
	
	/**
	 * Returns defensive copy of the current Drives on this.
	 */
	public Set<Drive> getDrives(){
		return new HashSet<>(this.drives);
	}

	public int getNumberOfLots(){
		return this.segments.size();
	}
	
	/**
	 * Returns true iff drive != null and this Road can add drive to the road.
	 * @param drive
	 * @return
	 */
	private boolean canAdd(Drive drive){
		if (drive != null){
			double l = (Road.MINIMUM_FOLLOW_M + Road.AVERAGE_CAR_M) * (double)this.getDrives().size();
			if (Road.AVERAGE_CAR_M + l < this.length){
				return true;
			}
		}
		return false;
	}
}
