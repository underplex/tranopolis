package com.underplex.tranopolis;

import java.time.LocalDateTime;

/**
 * Instance represents a city.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class City {

	/**
	 * Default date that the simulation begins.
	 */
	private static final LocalDateTime DEFAULT_START = LocalDateTime.of(1900, 1, 1, 0, 0);
	
	private final LotManager lots;
	private final LocationManager locations;
	private final RoadGraphManager roadGraphs;
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
		this.timer = new TimeManager(this, start);
		this.roadGraphs = new RoadGraphManager(this);
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

	public RoadGraph getRoadGraph(){
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

	/**
	 * Connects <tt>Location</tt> to the <tt>Xing</tt> at the <tt>Lot</tt> given and updates network.
	 * <p>
	 * This method helps hook Location up to a network.
	 * <p>
	 * Has side-effect of changing the road network to include this new connection.
	 * <p>
	 * 
	 * @param location Location to be connected
	 * @param lot Lot where location can access road network
	 * @return true iff the connection was made
	 */
	public boolean connectLocation(Location location, Lot lot){
		lots.addEntryPoint(lot);
		roadGraphs.updateGraph();
		Xing xing = null;
		for (Xing x : roadGraphs.getCurrentGraph().vertexSet()){
			if (x.getLot().equals(lot)){
				xing = x;
				break;
			}
		}
		if (xing != null){
			location.addOnOffPoint(xing);
		}
		return xing != null;
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
	 * Note that there's nothing preventing an invalide number of seconds from being attempted this way.
	 */
	public void advance(long seconds){
		this.timer.advanceSeconds(seconds);
		// notify residents that the time has changed
		this.residents.advance(this.timer.getCurrentTime());
		this.traffic.forward(this.timer.getCurrentTime(),this.residents.surveyDrives(this.timer.getCurrentTime()));
	}
}
