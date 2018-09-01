package com.underplex.tranopolis;

/**
 * Static utility methods for counting.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Counter {

	private Counter(){
		// don't instantiate
	}
	
	/**
	 * Returns SumCount representing an accounting of the total population compared to the number of current residents that can be found in a Location and Drives.
	 * @return
	 */
	public static SumCount countSums(City city){
		
		SumCount sc = new SumCount();
		
		sc.setTime(city.getTimeManager().getCurrentTime().toString());
		sc.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());
		
		int tv = 0;
		for (Drivable dc : city.getRoadGraph().vertexSet()){
			tv = tv + dc.getDrives().size();
		}
		sc.setDrivesAtVertices(tv);

		int te = 0;
		for (Drivable dc : city.getRoadGraph().edgeSet()){
			te = te + dc.getDrives().size();		
		}
		sc.setDrivesAtEdges(te);
		
		int lp = 0;
		for (Location lc : city.getLocationManager().getLocations()){
			lp = lp + lc.getResidents().size();
		}
		sc.setResidentsAtLocations(lp);
		
		sc.setPopulation(city.getResidentManager().getPopulation());
		
		sc.setError(sc.getPopulation() - tv - te - lp);
		
		return sc;
	}
	
}
