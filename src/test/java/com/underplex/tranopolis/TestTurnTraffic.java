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

/**
 * Test traffic of a single resident that needs to turn.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class TestTurnTraffic {

	@Test
	public void testStraightTraffic1() {
		System.out.println("**********************");
		System.out.println("***** testTurnTraffic1   *****");
		System.out.println("**********************");
		
		City city = new City(5, 5);

		// make cross of two roads and add an isolated paved lot at 3,3
		city.getLot(1, 1).makePaved(); // center of the cross

		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		city.getLot(3, 3).makePaved();

		city.getLotManager().printMap();
		
		// no need to add entry points; all the entry points are on natural vertices

		// start by testing the actual graph...
		
		DrivableGraph roadGraph = GraphFinder.findDrivableGraph(city);
		
		// how to add xing
		
		Xing iso = null;
		Xing north = null;
		Xing mid = null;
		Xing east = null;
		Road northMidRoad = null;
		Road midEastRoad = null;
		Road isoRoad = null;
		
		// six vertices here -- including 1 isolated vertex
		assertEquals(6, roadGraph.vertexSet().size());
		
		for (Xing xing : roadGraph.vertexSet()){
			if (xing.getLot().coordinates().equals("1, 4")){
				north = xing;
			} else if (xing.getLot().coordinates().equals("1, 1")){
				mid = xing;
			} else if (xing.getLot().coordinates().equals("4, 1")){
				east = xing;
			} else if (xing.getLot().coordinates().equals("3, 3")){
				iso = xing;
			}
		}		

		assertNotEquals(east, iso);
		assertNotEquals(east, north);
		assertNotEquals(mid, north);
		assertNotEquals(east, mid);	
		
		Location northLocation = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(0, 4)));
		
		Location eastLocation = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(4, 0)));
		
		Location isoLocation1 = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(3, 4)));
		Location isoLocation2 = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(4, 3)));
		
		assertTrue(city.connectLocation(northLocation, north.getLot()));
		assertTrue(city.connectLocation(eastLocation, east.getLot()));
		assertTrue(city.connectLocation(isoLocation1, iso.getLot()));
		assertTrue(city.connectLocation(isoLocation2, iso.getLot()));

		assertEquals(1,eastLocation.getOnOffPoints().size());
		assertEquals(1,northLocation.getOnOffPoints().size());
		assertEquals(1,isoLocation1.getOnOffPoints().size());
		assertEquals(1,isoLocation2.getOnOffPoints().size());
		
		assertTrue(eastLocation.getOnOffPoints().contains(east));
		assertTrue(northLocation.getOnOffPoints().contains(north));
		assertTrue(isoLocation1.getOnOffPoints().contains(iso));
		assertTrue(isoLocation2.getOnOffPoints().contains(iso));

		assertTrue(!isoLocation1.getOnOffPoints().contains(north));
		assertTrue(!eastLocation.getOnOffPoints().contains(mid));
		assertTrue(!northLocation.getOnOffPoints().contains(mid));
		
		for (Road r : roadGraph.edgeSet()){
			if (r.getSource().equals(north) && r.getTargetXing().equals(mid)){
				northMidRoad = r;
			} else if (r.getSource().equals(mid) && r.getTargetXing().equals(east)){
				midEastRoad = r;
			} else if (r.getSource().equals(iso) && r.getTargetXing().equals(iso)){
				isoRoad = r;
			}
		}

		assertNotNull(isoRoad);

		assertNotNull(northMidRoad);
		assertNotNull(midEastRoad);

		assertTrue(isoRoad.availableCapacity() > 0);

		assertTrue(northMidRoad.availableCapacity() > 0);
		assertTrue(midEastRoad.availableCapacity() > 0);

		assertTrue(north == northMidRoad.getSource());
		assertTrue(mid == northMidRoad.getTargetXing());
		assertTrue(mid == midEastRoad.getSource());
		assertTrue(east == midEastRoad.getTargetXing());
		assertTrue(iso == isoRoad.getSource());
		assertTrue(iso == isoRoad.getTargetXing());
						
		Resident rez = new BasicResident(northLocation);
				
//		roadGraph.extensiveReport();
		// we have 9 edges:
		//		4 x 2 connecting our "main" network of 5 xings, and then 1 looping around from isolated xing back to itself
		
		assertEquals(9, roadGraph.edgeSet().size());
		
		GraphPath<Xing, Road> p = DijkstraShortestPath.findPathBetween(roadGraph, north, east);
		// if not using Dijstra, we need to construct a "Path" that is just using the circular road of isolated paved lot
		
//		GraphPath<Xing, Road> p = DijkstraShortestPath.findPathBetween(roadGraph, north, east);

		
		Drive drive1 = new Drive(p, 
				rez, 
				city.getTimeManager().getCurrentTime(),
				northLocation,
				eastLocation);

		Drive drive2 = new Drive(p, 
				rez, 
				city.getTimeManager().getCurrentTime(),
				isoLocation1,
				isoLocation2);
		
		assertEquals(north, drive1.getStartVertex());
		assertEquals(east, drive1.getEndVertex());
		assertEquals(northMidRoad, drive1.next(north));
		assertEquals(midEastRoad, drive1.next(mid));
		assertEquals(null, drive1.next(east));
		
		assertEquals(iso, drive2.getStartVertex());
		assertEquals(iso, drive2.getEndVertex());
		assertEquals(isoRoad, drive2.next(iso));		
		
		Queue<Drive> drives = new ArrayDeque<>();
		drives.add(drive1);

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));
		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, midEastRoad.getDrives().size());
		assertTrue(!midEastRoad.getDrives().containsAll(drives));

		assertEquals(0, east.getDrives().size());
		assertTrue(!east.getDrives().containsAll(drives));

		// test turnOn		
		north.turnOn(drives);

		assertEquals(1, north.getDrives().size());
		assertTrue(north.getDrives().containsAll(drives));
		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));
		assertEquals(0, midEastRoad.getDrives().size());
		assertTrue(!midEastRoad.getDrives().containsAll(drives));

		assertEquals(0, east.getDrives().size());
		assertTrue(!east.getDrives().containsAll(drives));

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
		
		assertEquals(0, midEastRoad.getDrives().size());
		assertTrue(!midEastRoad.getDrives().containsAll(drives));

		assertEquals(0, east.getDrives().size());
		assertTrue(!east.getDrives().containsAll(drives));

		while(!northMidRoad.getDrives().isEmpty()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			for (Drive etaDrive : northMidRoad.getDrives()){
//				System.out.println("Time is: " + city.getTimeManager().getCurrentTime());
//				System.out.println(etaDrive + " has ETA of : " + northMidRoad.getEta(etaDrive));
//			}
			northMidRoad.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(1, midEastRoad.getDrives().size());
		assertTrue(midEastRoad.getDrives().containsAll(drives));

		assertEquals(0, east.getDrives().size());
		assertTrue(!east.getDrives().containsAll(drives));

		while(!midEastRoad.getDrives().isEmpty()){
			midEastRoad.flow(city.getTimeManager().getCurrentTime());
			city.getTimeManager().advanceSeconds(60);
		}
		
		assertEquals(1, east.getFinishedDrives().size());

		assertEquals(0, north.getDrives().size());
		assertTrue(!north.getDrives().containsAll(drives));

		assertEquals(0, northMidRoad.getDrives().size());
		assertTrue(!northMidRoad.getDrives().containsAll(drives));

		assertEquals(0, mid.getDrives().size());
		assertTrue(!mid.getDrives().containsAll(drives));

		assertEquals(0, midEastRoad.getDrives().size());
		assertTrue(!midEastRoad.getDrives().containsAll(drives));

		assertEquals(0, east.getDrives().size());
		assertTrue(!east.getDrives().containsAll(drives));
		
		assertEquals(1, east.getFinishedDrives().size());
	}
}
