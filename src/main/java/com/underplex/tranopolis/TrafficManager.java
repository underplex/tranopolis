package com.underplex.tranopolis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrafficManager {

	private final City city;
	private final Map<Resident, Drive> upcoming;
	private final Comparator<Drive> sorter;
		
	public TrafficManager(City city) {
		this.city = city;
		this.sorter = new Comparator<Drive>() 
			{
			@Override
			public int compare(Drive arg0, Drive arg1) {
				return arg0.getStartTime().compareTo(arg1.getStartTime());
			}
			
		};
		
		this.upcoming = new HashMap<>();
	}
	
	/**
	 * Moves all traffic forward a step at <tt>time</tt>.
	 * @param time LocalDateTime representing time being moved forward to
	 * @param beginngers Set of Drives that are due to begin
	 */
	public void forward(LocalDateTime time, Set<Drive> beginners){
		
		// get any turn ons that need to happen
		for (Drive drive : beginners){
			drive.getOnPoint().turnOn(drive);
		}
		
		// shuffle all xings and roads and iterate through them 
		List<Drivable> list = new ArrayList<Drivable>(city.getRoadGraph().edgeSet());
		
		list.addAll(city.getRoadGraph().vertexSet());
		
		Collections.shuffle(list);
	
		for (Drivable drivable : list){
			drivable.flow(time);
		}
	}

	/**
	 * Adds drives to be tracked by this.
	 * @param drives <tt>Drive<\tt> elements to be tracked and processed
	 */
	public void addDrives(Set<Drive> drives){
		for (Drive drive : drives){
			upcoming.put(drive.getDriver(), drive);
		}
	}
	
	/**
	 * Returns Set of Residents with a Drive already registered.
	 * <p>
	 * The Set may be changed without any ill effects to this TrafficManager.
	 * @return Set of Residents with a Drive already registered
	 */
	public Set<Resident> getResidentsWithDrive(){
		return new HashSet<Resident>(upcoming.keySet());
	}
	
}
