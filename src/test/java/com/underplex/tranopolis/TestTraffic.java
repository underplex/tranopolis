package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;

public class TestTraffic {

	@Test
	public void testTraffic1() {
				
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("***             testTraffic1     ****");
		System.out.println("***********************************************************");
		System.out.println();
		
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(2, 3).makePaved();
		city.getLot(2, 4).makePaved();
		
		Location home = city.getLocationManager().makeLocation(new HashSet<Lot>());
		Location work = city.getLocationManager().makeLocation(new HashSet<Lot>());
		city.connectLocation(home, city.getLot(2, 1));
		city.connectLocation(work, city.getLot(2, 3));
		
		city.getLotManager().printMap();
		
		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		// the whole system is built now
		
		Xing north = null;
		Xing south = null;
		Xing homeOnOff = null;
		Xing workOnOff = null;

		Road road = null;
		
		for (Xing xing : roadGraph.vertexSet()){
			if (xing.getLot().coordinates().equals("2, 4")){
				north = xing;
			} else if (xing.getLot().coordinates().equals("2, 0")){
				south = xing;
			} if (xing.getLot().coordinates().equals("2, 1")){
				homeOnOff = xing;
			} else if (xing.getLot().coordinates().equals("2, 3")){
				workOnOff = xing;
			}
		}
		
		for (Road r : roadGraph.edgeSet()){
			if (r.getSourceXing().equals(homeOnOff) & r.getTargetXing().equals(workOnOff)){
				road = r;
			} 
		}
		
		assertNotNull(road);
		assertNotNull(north);
		assertNotNull(south);
		assertNotNull(homeOnOff);
		assertNotNull(workOnOff);
		
		Resident resident = new BasicResident(home);
		resident.setWork(work);
				
		LocalDateTime curretTime = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.NOON);
		System.out.println(curretTime);
		
		GraphPath<Xing, Road> path = DijkstraShortestPath.findPathBetween(roadGraph, homeOnOff, workOnOff);
		
		Drive d = new Drive(path, resident, curretTime, home, work);
		// test this guy
		
		assertEquals(homeOnOff, d.getStartVertex());
		assertEquals(workOnOff, d.getEndVertex());
		assertEquals(road, d.getOutgoingRoad(homeOnOff));
		assertEquals(null, d.getOutgoingRoad(workOnOff));

		assertEquals(homeOnOff, road.getSourceXing());
		assertEquals(workOnOff, road.getTargetXing());

		Queue<Drive> drives = new ArrayDeque<>();
		drives.add(d);
		// set up copy of call drives

		assertEquals(0, homeOnOff.getDrives().size());
		assertTrue(!homeOnOff.getDrives().containsAll(drives));
		assertEquals(0, road.getDrives().size());
		assertTrue(!road.getDrives().containsAll(drives));
		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		// test turnOn		
		homeOnOff.turnOn(drives);

		assertEquals(1, homeOnOff.getDrives().size());
		assertTrue(homeOnOff.getDrives().containsAll(drives));
		assertEquals(0, road.getDrives().size());
		assertTrue(!road.getDrives().containsAll(drives));
		assertEquals(0, workOnOff.getDrives().size());
		assertTrue(!workOnOff.getDrives().containsAll(drives));
				
		while(!homeOnOff.getDrives().isEmpty()){
			homeOnOff.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		assertEquals(0, homeOnOff.getDrives().size());
		assertTrue(!homeOnOff.getDrives().containsAll(drives));
		assertEquals(1, road.getDrives().size());
		assertTrue(road.getDrives().containsAll(drives));
		assertEquals(0, workOnOff.getDrives().size());
		assertTrue(!workOnOff.getDrives().containsAll(drives));
		
		while(!road.getDrives().isEmpty()){
			road.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		// the drive is never supposed to actually spend time on South!
		
		assertEquals(0, homeOnOff.getDrives().size());
		assertTrue(!homeOnOff.getDrives().containsAll(drives));
		assertEquals(0, road.getDrives().size());
		assertTrue(!road.getDrives().containsAll(drives));
		assertEquals(0, workOnOff.getDrives().size());
		assertTrue(!workOnOff.getDrives().containsAll(drives));
		
	}


	
	@Test
	public void testTraffic2() {
		
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("***             testTraffic2      ****");
		System.out.println("***********************************************************");
		System.out.println();
		
		// test of two multileg multi-leg trips
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();

		city.getLot(0, 2).makePaved();
		city.getLot(0, 3).makePaved();
		city.getLot(0, 4).makePaved();

		city.getLot(1, 2).makePaved();

		city.getLot(1, 4).makePaved();
		city.getLot(2, 4).makePaved();
		city.getLot(3, 4).makePaved();
		city.getLot(4, 4).makePaved();

		city.getLot(4, 3).makePaved();
		city.getLot(4, 2).makePaved();
		city.getLot(4, 1).makePaved();
		city.getLot(4, 0).makePaved();
		
		city.getLot(3, 2).makePaved();
		city.getLot(2, 2).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();

		city.getLotManager().printMap();
		
		city.getLotManager().addEntryPoint(city.getLot(2, 4));
		city.getLotManager().addEntryPoint(city.getLot(0, 0));

		
		// start by testing the actual graph...
		
		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		
		// how to add xing
		
		Xing north = null; // 2, 4
		Xing southeast = null; // 4, 0
		Xing east = null; // 4, 2
		Xing west = null; // 0, 2
		Xing southwest = null; // 0, 0
		Xing central = null; // 2, 2
		
		Road northeastRoad = null;
		Road northwestRoad = null;
		Road southwestRoad = null;
		Road southcentralRoad = null;
		Road southeastRoad = null;
		Road westcentralRoad = null;
		Road eastcentralRoad = null;
		
		assertEquals(6, roadGraph.vertexSet().size());
		assertEquals(14, roadGraph.edgeSet().size());
				
		for (Xing xing : roadGraph.vertexSet()){
			if (xing.getLot().coordinates().equals("0, 0")){
				southwest = xing;
			} else if (xing.getLot().coordinates().equals("2, 2")){
				central = xing;
			} else if (xing.getLot().coordinates().equals("0, 2")){
				west = xing;
			} else if (xing.getLot().coordinates().equals("4, 2")){
				east  = xing;
			} else if (xing.getLot().coordinates().equals("4, 0")){
				southeast = xing;
			} else if (xing.getLot().coordinates().equals("2, 4")){
				north = xing;
			}
		}
		
		// all the roads we care about head away from southwest up northish and eastish
		for (Road r : roadGraph.edgeSet()){
			if (r.getSourceXing().equals(southwest) && r.getTargetXing().equals(central)){
				southcentralRoad = r;
			} else if (r.getSourceXing().equals(southwest) && r.getTargetXing().equals(west)){
				southwestRoad = r;
			} else if (r.getSourceXing().equals(west) && r.getTargetXing().equals(north)){
				northwestRoad = r;
			} else if (r.getSourceXing().equals(west) && r.getTargetXing().equals(central)){
				westcentralRoad = r;
			} else if (r.getSourceXing().equals(central) && r.getTargetXing().equals(east)){
				eastcentralRoad = r;
			} else if (r.getSourceXing().equals(north) && r.getTargetXing().equals(east)){
				northeastRoad = r;
			} else if (r.getSourceXing().equals(east) && r.getTargetXing().equals(southeast)){
				southeastRoad = r;
			}
		}
	
	}
}
