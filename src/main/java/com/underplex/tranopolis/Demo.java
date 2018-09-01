package com.underplex.tranopolis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class Demo {
	
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String a[]) {

		City city = new City(5, 5);

		// make cross of two roads and add an isolated paved lot at 3,3
		city.getLot(1, 1).makePaved(); // center of the cross

//		b R . b . 
//		. R . R b 
//		. R . . . 
//		R R R R R 
//		b R . . b 
		
		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		city.getLot(3, 3).makePaved();

		city.getLot(3, 4).makeBuilt();
		city.getLot(4, 3).makeBuilt();
		city.getLot(4, 0).makeBuilt();
		

		city.getLot(0, 0).makeBuilt();
		city.getLot(0, 4).makeBuilt();
				
		// start by testing the actual graph...
		
		city.getGraphManager().updateGraph();
		
		DrivableGraph roadGraph = city.getRoadGraph();
		
		// how to add xing

		// test vertice between any locations are added
		// six vertices here -- including 1 isolated vertex
		
		// create locations...
		Location eastLoc = city.getLocationManager().makeLocation(city.getLot(3, 4), "Eastview Apts");
		Location northLoc = city.getLocationManager().makeLocation(city.getLot(4, 3), "North Lake Mall");
		Location southeastLoc = city.getLocationManager().makeLocation(city.getLot(4, 0), "Southern Hills Condos");
		Location southwestLoc = city.getLocationManager().makeLocation(city.getLot(0, 0), "Southwest Mountain Apartments");
		Location northwestLoc = city.getLocationManager().makeLocation(city.getLot(0, 4), "Northwest Heights Office Park");
		
		// connect locations to existing network of roads
		city.connectLocation(eastLoc, city.getLot(3, 3));
		city.connectLocation(northLoc, city.getLot(3, 3));
		city.connectLocation(southeastLoc, city.getLot(4, 1));
		
		city.connectLocation(southwestLoc, city.getLot(1, 0));
		city.connectLocation(northwestLoc, city.getLot(1, 4));
		city.getGraphManager().updateGraph();
		
//		LOGGER.info("There are " + city.getRoadGraph().vertexSet().size() + " vertices.");
//		LOGGER.info("There are " + city.getRoadGraph().edgeSet().size() + " edges.");

		DijkstraShortestPath<Drivable, Drivable> dsp = new DijkstraShortestPath<Drivable, Drivable>(city.getRoadGraph());
		GraphPath<Drivable,Drivable> path = dsp.getPath(eastLoc, northLoc);
//		LOGGER.info("Path is " + path);
		
//		city.getResidentManager().addResidents(makeBasics(1000,eastLoc,northLoc));
		city.getResidentManager().addResidents(makeBasics(1000,southeastLoc,northwestLoc));
//		city.getResidentManager().addResidents(makeBasics(1,southwestLoc,northwestLoc));
		
		LocalDateTime genesis = city.getTimeManager().getCurrentTime();
		Set<Drive> finishedDrives = new HashSet<>();
		// how to study these things?
		List<DriveCount> driveCounts = new ArrayList<>();
		List<DrivableCount> drivableCounts = new ArrayList<>();
		List<LocationCount> locationCounts = new ArrayList<>();
		List<SumCount> sumCounts = new ArrayList<>();
		Set<Drive> allDrives = new HashSet<>();
		
		while (city.getTimeManager().getCurrentTime().isBefore(genesis.plusDays(1))){
			city.advance();

			// do count necessary for info collection
			if (city.getTimeManager().getCurrentTime().getMinute() % 10 == 0){
				String timeString = city.getTimeManager().getCurrentTime().toString();
				
				sumCounts.add(Counter.countSums(city));
								
				DriveCount driveCount = new DriveCount();
				driveCount.setTime(timeString);
				driveCount.setFinishedDrives(city.getLocationManager().getFinishedDrives().size());				
				driveCount.setDayOfWeek(city.getTimeManager().getCurrentTime().getDayOfWeek().toString());
				driveCounts.add(driveCount);
				
				allDrives.addAll(city.getLocationManager().getFinishedDrives());
				
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
					
					allDrives.addAll(lc.getFinishedDrives());
					locationCounts.add(locationCount);
				}
				
				city.getLocationManager().dumpFinishedDrives();
			}
			
		}
		
		// write city counts
		Writer finishedDrivesWriter = null;
		Writer locationWriter = null;
		Writer drivableWriter = null;
		Writer sumWriter = null;

		String experimentName = "";
		experimentName = "_1000_se_nw";
		String directory = "C:/Users/irvin/workspace/tranopolis/target/output-files";
		String driveFile = "finished_drives";
		String locationFile = "locations";
		String drivableFile = "drivables";
		String sumFile = "sums";
		
		try {
			finishedDrivesWriter = new FileWriter(directory + "/" + driveFile + experimentName + ".csv");
			locationWriter = new FileWriter(directory + "/" + locationFile + experimentName + ".csv");
			drivableWriter = new FileWriter(directory  + "/" + drivableFile + experimentName + ".csv");
			sumWriter = new FileWriter(directory + "/" + sumFile + experimentName + ".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    StatefulBeanToCsvBuilder<DriveCount> drivesBuilder = new StatefulBeanToCsvBuilder<DriveCount>(finishedDrivesWriter); 
	    StatefulBeanToCsv<DriveCount> drivesToCsv = drivesBuilder.build();

	    StatefulBeanToCsvBuilder<LocationCount> locationsBuilder = new StatefulBeanToCsvBuilder<LocationCount>(locationWriter); 
	    StatefulBeanToCsv<LocationCount> locationsToCsv = locationsBuilder.build();

	    StatefulBeanToCsvBuilder<DrivableCount> drivableBuilder = new StatefulBeanToCsvBuilder<DrivableCount>(drivableWriter); 
	    StatefulBeanToCsv<DrivableCount> drivablesToCsv = drivableBuilder.build();
	    
	    StatefulBeanToCsvBuilder<SumCount> sumBuilder = new StatefulBeanToCsvBuilder<SumCount>(sumWriter); 
	    StatefulBeanToCsv<SumCount> sumsToCsv = sumBuilder.build();
	    

	    try {
			drivesToCsv.write(driveCounts);
		    finishedDrivesWriter.close();

			locationsToCsv.write(locationCounts);
		    locationWriter.close();

			drivablesToCsv.write(drivableCounts);
		    drivableWriter.close();

			sumsToCsv.write(sumCounts);
		    sumWriter.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Set<Resident> makeBasics(int num,Location home,Location work){
		Set<Resident> rSet = new HashSet<>();
		if (num >= 1){
			for (int i = 1; i <= num; i++){
				rSet.add(new BasicResident(home,work));
			}
		}
		return rSet;
	}
}
