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
 * Test traffic of a single resident in a single, isolated paved lot.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class TestIsolatedTraffic {

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
		Road isoRoad = null;
		
		// six vertices here -- including 1 isolated vertex
		assertEquals(6, roadGraph.vertexSet().size());
		
		for (Xing xing : roadGraph.vertexSet()){
			if (xing.getLot().coordinates().equals("3, 3")){
				iso = xing;
			}
		}		
	
		Location isoLocation1 = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(3, 4)));
		Location isoLocation2 = city.getLocationManager().makeLocation(Collections.singleton(city.getLot(4, 3)));
		
		assertTrue(city.connectLocation(isoLocation1, iso.getLot()));
		assertTrue(city.connectLocation(isoLocation2, iso.getLot()));

		assertEquals(1,isoLocation1.getOnOffPoints().size());
		assertEquals(1,isoLocation2.getOnOffPoints().size());
		
		assertTrue(isoLocation1.getOnOffPoints().contains(iso));
		assertTrue(isoLocation2.getOnOffPoints().contains(iso));

		for (Road r : roadGraph.edgeSet()){
			if (r.getSource().equals(iso) && r.getTargetXing().equals(iso)){
				isoRoad = r;
			}
		}

		assertNotNull(isoRoad);

		assertTrue(isoRoad.availableCapacity() > 0);

		assertTrue(iso == isoRoad.getSource());
		assertTrue(iso == isoRoad.getTargetXing());
						
		Resident rez = new BasicResident(isoLocation1);
				
//		roadGraph.extensiveReport();
		// we have 9 edges:
		//		4 x 2 connecting our "main" network of 5 xings, and then 1 looping around from isolated xing back to itself
		
		assertEquals(9, roadGraph.edgeSet().size());
		
		GraphPath<Xing, Road> p = DijkstraShortestPath.findPathBetween(roadGraph, iso, iso);
		
		System.out.println(p.getEdgeList().size());
		
		Drive drive = new Drive(p, 
				rez, 
				city.getTimeManager().getCurrentTime(),
				isoLocation1,
				isoLocation2);
		
		
	
	}
}
