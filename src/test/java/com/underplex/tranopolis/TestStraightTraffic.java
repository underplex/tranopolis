package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;

public class TestStraightTraffic {

	@Test
	public void testStraightTraffic1() {
		System.out.println("**********************");
		System.out.println("***** testStraightTraffic1   *****");
		System.out.println("**********************");
		
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
		
		city.getLotManager().printMap();
		
		city.getLotManager().addEntryPoint(city.getLot(2, 2));

		// start by testing the actual graph...
		
		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		
		// how to add xing
		
		Xing north = null;
		Xing mid = null;
		Xing south = null;
		Road northMidRoad = null;
		Road midSouthRoad = null;
		
		assertEquals(3, roadGraph.vertexSet().size());
		
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
		
		Location northLocation = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(1, 4)));
		
		Location southLocation = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(3, 0)));
		
		assertTrue(city.connectLocation(northLocation, north.getLot()));
		assertTrue(city.connectLocation(southLocation, south.getLot()));

		assertEquals(1,southLocation.getOnOffPoints().size());
		assertEquals(1,northLocation.getOnOffPoints().size());
		
		assertTrue(southLocation.getOnOffPoints().contains(south));
		assertTrue(northLocation.getOnOffPoints().contains(north));
		
		assertTrue(!southLocation.getOnOffPoints().contains(mid));
		assertTrue(!northLocation.getOnOffPoints().contains(mid));
		
		for (Road r : roadGraph.edgeSet()){
			if (r.getSourceXing().equals(north) && r.getTargetXing().equals(mid)){
				northMidRoad = r;
			} else if (r.getSourceXing().equals(mid) && r.getTargetXing().equals(south)){
				midSouthRoad = r;
			}
		}

		assertNotNull(northMidRoad);
		assertNotNull(midSouthRoad);
		assertNotNull(north);
		assertNotNull(south);
		assertNotNull(mid);
		
		assertTrue(northMidRoad.availableCapacity() > 0);
		assertTrue(midSouthRoad.availableCapacity() > 0);

		assertTrue(north == northMidRoad.getSourceXing());
		assertTrue(mid == northMidRoad.getTargetXing());
		assertTrue(mid == midSouthRoad.getSourceXing());
		assertTrue(south == midSouthRoad.getTargetXing());
				
		AbstractResident rez = new BasicResident(northLocation);
				
		assertEquals(4, roadGraph.edgeSet().size());
		
		GraphPath<Xing, Road> p = DijkstraShortestPath.findPathBetween(roadGraph, north, south);
		
		Drive d = new Drive(p, 
				rez, 
				city.getTimeManager().getCurrentTime(),
				northLocation,
				southLocation);
		
		assertEquals(north, d.getStartVertex());
		assertEquals(south, d.getEndVertex());
		assertEquals(northMidRoad, d.getOutgoingRoad(north));
		assertEquals(midSouthRoad, d.getOutgoingRoad(mid));
		assertEquals(null, d.getOutgoingRoad(south));
		
		Queue<Drive> drives = new ArrayDeque<>();
		drives.add(d);

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));
		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, midSouthRoad.getDrives().size());
		assertTrue(!midSouthRoad.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		// test turnOn		
		north.turnOn(drives);

		assertEquals(1, north.getDrives().size());
		assertTrue(north.getDrives().containsAll(drives));
		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, midSouthRoad.getDrives().size());
		assertTrue(!midSouthRoad.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		// try this the artificial way to move traffic through...
	
		while(!north.getDrives().isEmpty()){
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.out.println("Time is: " + city.getTimeManager().getCurrentTime());
			north.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));
		
		assertEquals(1, northMidRoad.getDrives().size());
		assertTrue(northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		
		assertEquals(0, midSouthRoad.getDrives().size());
		assertTrue(!midSouthRoad.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

		while(!northMidRoad.getDrives().isEmpty()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Drive etaDrive : northMidRoad.getDrives()){
//				System.out.println("Time is: " + city.getTimeManager().getCurrentTime());
//				System.out.println(etaDrive + " has ETA of : " + northMidRoad.getEta(etaDrive));
			}
			northMidRoad.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(1, midSouthRoad.getDrives().size());
		assertTrue(midSouthRoad.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));

//		System.out.println("south is " + south.toString());
		while(!midSouthRoad.getDrives().isEmpty()){
			midSouthRoad.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
//			System.out.println("South finished drives size:" + south.getFinishedDrives().size());
		}
		
//		System.out.println("South finished drives size:" + south.getFinishedDrives().size());
		assertEquals(1, south.getFinishedDrives().size());

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(0, midSouthRoad.getDrives().size());
		assertTrue(!midSouthRoad.getDrives().containsAll(drives));

		assertEquals(0, south.getDrives().size());
		assertTrue(!south.getDrives().containsAll(drives));
		
		assertEquals(1, south.getFinishedDrives().size());
	}
}
