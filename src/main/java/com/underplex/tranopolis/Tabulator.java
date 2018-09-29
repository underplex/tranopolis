package com.underplex.tranopolis;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Keeps ongoing counts for standard statistics requested/needed.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Tabulator {

	private final List<DriveCount> driveCounts;
	private final List<DrivableCount> drivableCounts;
	private final List<LocationCount> locationCounts;
	private final List<SumCount> sumCounts;
	private final City city;
	

	
	public Tabulator(City city) {
		
		this.driveCounts = new ArrayList<>();
		this.drivableCounts = new ArrayList<>();
		this.locationCounts = new ArrayList<>();
		this.sumCounts = new ArrayList<>();
		this.city = city;
	}


	/**
	 * Updates this tabulator. Has side-effect of dumping existing drives from Locations.
	 */
	public void update(){
		String timeString = city.getTimeManager().getCurrentTime().toString();
		
		sumCounts.add(Counter.countSums(city));
						
		DriveCount driveCount = new DriveCount();
		driveCount.setTime(timeString);
		driveCount.setFinishedDrives(city.getLocationManager().getFinishedDrives().size());				
		driveCount.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());
		driveCounts.add(driveCount);
				
		// finishedDrives.addAll(city.getLocationManager().getFinishedDrives());
		// city.getLocationManager().dumpFinishedDrives();
		
		for (Drivable dc : city.getRoadGraph().vertexSet()){
			DrivableCount drivableCount = new DrivableCount();
			drivableCount.setTime(timeString);
			drivableCount.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());

			drivableCount.setNumberOfDrives(dc.getDrives().size());
			drivableCount.setDrivable(dc.toString());
			drivableCounts.add(drivableCount);
		}

		for (Drivable dc : city.getRoadGraph().edgeSet()){
			DrivableCount drivableCount = new DrivableCount();
			drivableCount.setTime(timeString);
			drivableCount.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());

			drivableCount.setNumberOfDrives(dc.getDrives().size());
			drivableCount.setDrivable(dc.toString());
			drivableCounts.add(drivableCount);
		}
		
		Set<Drive> lcDrives = new HashSet<>();
		for (Location lc : city.getLocationManager().getLocations()){
			LocationCount locationCount = new LocationCount();
			locationCount.setTime(timeString);
			locationCount.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());

			locationCount.setNumberOfResidents(lc.getResidents().size());
			locationCount.setLocation(lc.toString());
			
			int lengthSum = 0;
			for (Drive ld : lc.getFinishedDrives()){
				lengthSum += ChronoUnit.SECONDS.between(ld.getActualStartTime(),ld.getActualEndTime());
			}
			int n = lc.getFinishedDrives().size();
			if (n > 0){
				locationCount.setAverageDriveTime((double)lengthSum/(double)n);
			} else {
				locationCount.setAverageDriveTime(0.0);
			}
			
			locationCounts.add(locationCount);
		}
		
		city.getLocationManager().dumpFinishedDrives();

	}


	public List<DriveCount> getDriveCounts() {
		return new ArrayList<>(driveCounts);
	}


	public List<DrivableCount> getDrivableCounts() {
		return new ArrayList<>(drivableCounts);
	}


	public List<LocationCount> getLocationCounts() {
		return new ArrayList<>(locationCounts);
	}


	public List<SumCount> getSumCounts() {
		return new ArrayList<>(sumCounts);
	}

	public City getCity() {
		return city;
	}
	
}
