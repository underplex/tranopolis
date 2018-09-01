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
	
	private final static int INVERSE_SCALING = 20; // scaling factor used for a very rough scaling
	private final static long BASELINE_SECONDS = 60; // baseline per road segment 
	private final static int PER_LOT_CAPACITY = 5;
	private final static int DEFAULT_CAPACITY = 100;
	private final List<Lot> segments; //ordered bunch of lots
	private final int length;
	private final Drivable source;
	private final Drivable target;
	private int currentCount; 
	private final Set<Drive> drives;
	private final List<Eta> etas;
	private final int totalCap;

	/**
	 * Constructs a road.
	 * <p>
	 * While the segments used to form the road can be inferred, it's generally better to actually count the segments and order them, since certain odd or circular road types
	 * might not lead directly from the source xing to the target xing.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target, List<Lot> segments, int totalCap) {
		if (source == null || target == null){
			throw new IllegalArgumentException("Source or target of Road cannot be null.");
		}
		this.source = source;
		this.target = target;
		// order the segments according to the path they take from sourceXing to targetXing
		this.segments = new ArrayList<>(segments);
		this.length = segments.size() + 1;
		this.currentCount = 0;
		this.etas = new ArrayList<Eta>();
		this.totalCap = totalCap;
		this.drives = new HashSet<>();
	}

	/**
	 * Constructs a road.
	 * <p>
	 * While the segments used to form the road can be inferred, it's generally better to actually count the segments and order them, since certain odd or circular road types
	 * might not lead directly from the source xing to the target xing.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target, List<Lot> segments) {
		this(source,
				target,
				segments,
				Math.max(PER_LOT_CAPACITY, segments.size() + PER_LOT_CAPACITY) * 5);
	}
	
	/**
	 * Constructs a road.
	 * <p>
	 * While the segments used to form the road can be inferred, it's generally better to actually count the segments and order them, since certain odd or circular road types
	 * might not lead directly from the source xing to the target xing.
	 * @param sourceXing
	 * @param targetXing
	 * @param segments List ordered by the lots stemming from sourceXing and leading to targetXing 
	 */
	public Road(Drivable source, Drivable target) {
		this(source,
				target,
				new ArrayList<Lot>(),
				Road.DEFAULT_CAPACITY);
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

	/**
	 * Returns overall capacity of this Road (not current capacity).
	 * <p>
	 * This will be the same number as if there were no <tt>Drive</tt>s currently on this <tt>Road</tt>.
	 * @return
	 */
	public int totalCapacity() {
		return this.totalCap;
	}
	
	/**
	 * Returns integer for number of <tt>Drive</tt>s that this Road could take on.
	 * <p>
	 * This will be <tt>totalCapacity()</tt> minus the number of vehicles already on the road.
	 * @return
	 */
	public int availableCapacity(){
		return totalCap - currentCount;
	}
	
	@Override
	public Set<Drive> take(Queue<Drive> merging, LocalDateTime time) {
		
		Queue<Drive> pushing = new ArrayDeque<Drive>(merging);
		
		while (this.availableCapacity() > 0 && pushing.size() > 0 ){
			
			Drive d = pushing.remove();
			LOGGER.info(d + " is added to " + this);
			Eta e = new Eta(d, time.plusSeconds(this.estimateTravelSeconds(d)));
			this.drives.add(d);
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
	 * Estimate the seconds that would be needed for <tt>drive</tt> to travel from source to target xing.
	 * <p>
	 * This method purports to represent the speed based on slow-down caused by traffic, but not the stopping effect of actual traffic jams, which are modeled by a refusal to allow this
	 * drive onto the roadway.
	 * @param drive
	 * @return long seconds that would be needed for <tt>Drive</tt> to travel from source to target xing given current traffic conditions and assuming no jam
	 */
	private long estimateTravelSeconds(Drive drive){
		// when this is 1, no delay because of other traffic...when 0.1, traffic is very tight (we should never be calling this when = 0)
		double prop = (double)(this.totalCapacity() - this.availableCapacity())/ (double) this.totalCapacity();
		double c = Math.min(1.5, 1.0 + 1.0/(prop * (double)INVERSE_SCALING)); // quick way to get value from ~1.0 to ~1.5; for prop > 0.1 will just scale, for < 0.1 will max at 1.5
		long seconds = (long)(BASELINE_SECONDS * this.length * prop);

		return seconds;
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
}
