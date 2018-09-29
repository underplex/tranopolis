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
				
		// create locations...
		Location northviewApts = city.getLocationManager().makeLocation(city.getLot(3, 4), "Northview Apts");
		Location eastLakeMall = city.getLocationManager().makeLocation(city.getLot(4, 3), "East Lake Mall");
		Location southernHillsCondos = city.getLocationManager().makeLocation(city.getLot(4, 0), "Southern Hills Condos");
		Location swMountainApts = city.getLocationManager().makeLocation(city.getLot(0, 0), "Southwest Mountain Apartments");
		Location nwHghtsOfficePark = city.getLocationManager().makeLocation(city.getLot(0, 4), "Northwest Heights Office Park");
		
		// connect locations to existing network of roads
		city.connectLocation(northviewApts, city.getLot(3, 3));
		city.connectLocation(eastLakeMall, city.getLot(3, 3));
		city.connectLocation(southernHillsCondos, city.getLot(4, 1));
		
		city.connectLocation(swMountainApts, city.getLot(1, 0));
		city.connectLocation(nwHghtsOfficePark, city.getLot(1, 4));

		// update the graph!
		city.getGraphManager().updateGraph();
		
//		LOGGER.info("There are " + city.getRoadGraph().vertexSet().size() + " vertices.");
//		LOGGER.info("There are " + city.getRoadGraph().edgeSet().size() + " edges.");

		DijkstraShortestPath<Drivable, Drivable> dsp = new DijkstraShortestPath<Drivable, Drivable>(city.getRoadGraph());
		GraphPath<Drivable,Drivable> path = dsp.getPath(northviewApts, eastLakeMall);
//		LOGGER.info("Path is " + path);
		
		city.getResidentManager().addResidents(makeBasics(1000,northviewApts,eastLakeMall));
//		city.getResidentManager().addResidents(makeBasics(1000,southeastLoc,northwestLoc));
//		city.getResidentManager().addResidents(makeBasics(1,southwestLoc,northwestLoc));
		
		LocalDateTime genesis = city.getTimeManager().getCurrentTime();

		Tabulator tabulator = new Tabulator(city);
		
		System.out.println(System.getProperty("user.dir"));
		while (city.getTimeManager().getCurrentTime().isBefore(genesis.plusDays(1))){
			city.advance();

			if (city.getTimeManager().getCurrentTime().getMinute() % 10 == 0){
				tabulator.update();
			}
		
		}

		
		try {
			Filer.writeToFiles(tabulator, "C:/Users/irvin/workspace/tranopolis/target/output-files", "_demo");
		} catch (Exception e) {
			LOGGER.info("File write failed.");
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
