package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;

public class TestTraffic {

	@Test
	public void testGraphFinder() {
		
		
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("***             testGraphFinder     ****");
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
	public void testGraphFinder2() {
		
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("***             testGraphFinder2      ****");
		System.out.println("***********************************************************");
		System.out.println();
		
		// test of multi-leg trip
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(2, 3).makePaved();
		city.getLot(2, 4).makePaved();
		
		city.getLotManager().addEntryPoint(city.getLot(2, 2));

		// start by testing the actual graph...
		
		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		
		// how to add xing
		
		Xing north = null;
		Xing mid = null;
		Xing south = null;
		Road road1 = null;
		Road road2 = null;
		
		Location northLocation = city.getLocationManager().makeLocation(new HashSet<>());
		Location southLocation = city.getLocationManager().makeLocation(new HashSet<>());
		city.connectLocation(northLocation, city.getLot(2, 4));
		city.connectLocation(northLocation, city.getLot(2, 0));
		
		for (Xing xing : roadGraph.vertexSet()){
			if (xing.getLot().coordinates().equals("2, 4")){
				north = xing;
			} else if (xing.getLot().coordinates().equals("2, 2")){
				mid = xing;
			} else if (xing.getLot().coordinates().equals("2, 0")){
				south = xing;
			}
		}
		
		assertNotEquals(south, north);
		assertNotEquals(mid, north);
		assertNotEquals(south, mid);
		
		
		
		for (Road r : roadGraph.edgeSet()){
			if (r.getSourceXing().equals(north) && r.getTargetXing().equals(mid)){
				road1 = r;
			} else if (r.getSourceXing().equals(mid) && r.getTargetXing().equals(south)){
				road2 = r;
			}
		}
		
		assertNotNull(road1);
		assertNotNull(road2);
		assertNotNull(north);
		assertNotNull(south);
		assertNotNull(mid);
		
		assertTrue(road1.availableCapacity() > 0);
		assertTrue(road2.availableCapacity() > 0);

		assertEquals(north, road1.getSourceXing());
		assertEquals(mid, road1.getTargetXing());
		assertEquals(mid, road2.getSourceXing());
		assertEquals(south, road2.getTargetXing());
//	
//		System.out.println("Road 1: " + road1);
//		System.out.println("Road 2: " + road2);
		
		
		Resident rez = new BasicResident(northLocation);
				
//		roadGraph.extensiveReport();
		assertEquals(4, roadGraph.edgeSet().size());
		
		GraphPath<Xing, Road> p = DijkstraShortestPath.findPathBetween(roadGraph, north, south);
		
//		// TODO: associate a location with a Drive
		Drive d = new Drive(p, 
				rez, 
				city.getTimeManager().getCurrentTime(),
				northLocation,
				southLocation);
		
		assertEquals(north, d.getStartVertex());
		assertEquals(south, d.getEndVertex());
		assertEquals(road1, d.getOutgoingRoad(north));
		assertEquals(road2, d.getOutgoingRoad(mid));
		assertEquals(null, d.getOutgoingRoad(south));
		
		Queue<Drive> drives = new ArrayDeque<>();
		drives.add(d);

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));
		assertEquals(0, road1.getDrives().size());
		assertTrue(!road1.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, road2.getDrives().size());
		assertTrue(!road2.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		// test turnOn		
		north.turnOn(drives);

		assertEquals(1, north.getDrives().size());
		assertTrue(north.getDrives().containsAll(drives));
		assertEquals(0, road1.getDrives().size());
		assertTrue(!road1.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, road2.getDrives().size());
		assertTrue(!road2.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		// try this the artificial way to move traffic through...
	
		while(!north.getDrives().isEmpty()){
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Time is: " + city.getTimeManager().getCurrentTime());
			north.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));
		
		assertEquals(1, road1.getDrives().size());
		assertTrue(road1.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		
		assertEquals(0, road2.getDrives().size());
		assertTrue(!road2.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		while(!road1.getDrives().isEmpty()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Drive etaDrive : road1.getDrives()){
				System.out.println("Time is: " + city.getTimeManager().getCurrentTime());
				System.out.println(etaDrive + " has ETA of : " + road1.getEta(etaDrive));
			}
			road1.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, road1.getDrives().size());
		assertTrue(!road1.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(1, road2.getDrives().size());
		assertTrue(road2.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		while(!road2.getDrives().isEmpty()){
			road2.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, road1.getDrives().size());
		assertTrue(!road1.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(0, road2.getDrives().size());
		assertTrue(!road2.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));
		
		assertEquals(1, south.getFinishedDrives().size());
	}
	
	@Test
	public void testGraphFinder3() {
		
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("***             testGraphFinder3      ****");
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
