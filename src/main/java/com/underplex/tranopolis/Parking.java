package com.underplex.tranopolis;

public class Parking extends Road{

	private static final double PARKING_COEFFICIENT = 2.0 * 60.0; // number of seconds per lot
	private final long parkingTime;
	
	public Parking(Drivable source, Drivable target, Location parkingLocation){
		super(source, target, Road.MINIMUM_ROAD_LENGTH, (int)Road.DEFAULT_MAX_SPEED);
		this.parkingTime = (long)(parkingLocation.getLots().size() * PARKING_COEFFICIENT);
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
		return parkingTime;
	}
}
