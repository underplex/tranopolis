package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Instance represents a city.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class City {

	/**
	 * Default date that the simulation begins.
	 */
	public static final LocalDateTime DEFAULT_START = LocalDateTime.of(1900, 1, 1, 0, 0);
	
	/**
	 * Default number of seconds the simulation advances at each step.
	 */
	private static final long DEFAULT_PERIOD_SECONDS = 60;

	private final LotManager lots;
	private final LocationManager locations;
	private final DrivableGraphManager roadGraphs;

	private final TrafficManager traffic;
	private final ResidentManager residents;
	private final TimeManager timer;
	
	public City(int width, int height, LocalDateTime start){
		if (width < 3 | height < 3){
			throw new IllegalArgumentException("Can't instantiate city of any dimension < 3.");
		}
		this.lots = new LotManager(this, width, height);
		this.traffic = new TrafficManager(this);
		this.residents = new ResidentManager(this);
		this.timer = new TimeManager(this, start, DEFAULT_PERIOD_SECONDS);
		this.roadGraphs = new DrivableGraphManager(this);
		this.locations = new LocationManager(this);
		roadGraphs.updateGraph();
	}
	
	public City(int width, int height){
		this(width, height, DEFAULT_START);
	}

	public int getWidth() {
		return lots.getWidth();
	}

	public int getHeight() {
		return lots.getHeight();
	}
	
	public Lot getLot(int x, int y){
		return this.lots.getLot(x, y);
	}
	
	public TrafficManager getTrafficManager() {
		return traffic;
	}
	
	public ResidentManager getResidentManager() {
		return residents;
	}

	public DrivableGraph getRoadGraph(){
		return this.roadGraphs.getCurrentGraph();
	}

	public LocationManager getLocationManager() {
		return locations;
	}

	public LotManager getLotManager() {
		return lots;
	}
	
	public TimeManager getTimeManager() {
		return timer;
	}

	public DrivableGraphManager getGraphManager() {
		return roadGraphs;
	}
	
	public boolean connectLocation(Location location, Lot lot){
		return location.addConnection(lot);
	}	
	
	/**
	 * Advances city to the current number of seconds.
	 */
	public void advance(){
		this.advance(timer.getPeriod());
	}
	
	/**
	 * Advance simulation by number of seconds.
	 * <p>
	 * Note that there's nothing preventing an invalid number of seconds from being attempted this way.
	 */
	public void advance(long seconds){
		this.timer.advanceSeconds(seconds);
		// notify residents that the time has changed
		this.residents.advance(this.timer.getCurrentTime());
		Set<Drive> drives = this.residents.surveyDrives(this.timer.getCurrentTime());
		// notify drivers that there drives have started

		this.traffic.forward(this.timer.getCurrentTime(), drives);
	}

}
