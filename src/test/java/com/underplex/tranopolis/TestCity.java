package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TestCity {

	@Test
	public void createCity() {
		System.out.println("**********************");
		System.out.println("***** createCity  ****");
		System.out.println("**********************");
	
		// see if you can make a city
		
		int w = 3;
		int h = 3;
		
		City city = new City(w,h,City.DEFAULT_START);
		
		assertEquals(w, city.getWidth());
		assertEquals(h, city.getHeight());
		
		assertEquals(0, city.getResidentManager().getResidents().size());
		assertEquals(w * h, city.getLotManager().asSet().size());
				
		assertEquals(0,city.getLocationManager().getLocations().size());
		
		assertEquals(City.DEFAULT_START,city.getTimeManager().getCurrentTime());

		LocalDateTime newTime = city.getTimeManager().getCurrentTime().plusSeconds(city.getTimeManager().getPeriod());
		city.advance();
		
		assertEquals(newTime,city.getTimeManager().getCurrentTime());
		
	}
	
	@Test
	public void testFindPavedNeighborsSimple(){
		System.out.println("**********************");
		System.out.println("***** findPavedNeighbors (simple)  ****");
		System.out.println("**********************");
		
		City city = new City(5,5);
		
//		. . R . b 
//		. . R . b 
//		R R R R R 
//		b b R . . 
//		. b R . . 
		
		city.getLot(0, 2).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(3, 2).makePaved();
		city.getLot(4, 2).makePaved();
		
		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		
		city.getLot(2, 3).makePaved();
		city.getLot(2, 4).makePaved();
		
		city.getLot(1, 1).makeBuilt();
		
		city.getLot(1, 0).makeBuilt();
		
		city.getLot(0, 1).makeBuilt();
		
		city.getLot(4, 3).makeBuilt();
		city.getLot(4, 4).makeBuilt();
		
		city.getLotManager().printMap();
		
		// build the graph without any Locations defined or added
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		assertEquals(5, xings.keySet().size());
		
		Lot center = city.getLot(2,2);
		Lot east = city.getLot(4, 2);
		Lot north = city.getLot(2, 4);
		
		assertEquals(4, xings.get(center).size());
		assertEquals(1, xings.get(east).size());
		assertNotEquals(3, xings.get(north).size());
		
		assertTrue(xings.get(north).contains(city.getLot(2,3)));
		
		// define locations
		
		Location offCenterLoc = city.getLocationManager().makeLocation(city.getLot(1, 1));
		Location southCenterLoc = city.getLocationManager().makeLocation(city.getLot(0, 1));
		
		Location northeastLoc = city.getLocationManager().makeLocation(city.getLot(4, 3));
		northeastLoc.addLot(city.getLot(4, 4));
		
		assertNotNull(offCenterLoc);
		assertNotNull(southCenterLoc);
		assertNotNull(northeastLoc);
				
		assertEquals(3, city.getLocationManager().getLocations().size());
		assertEquals(2, northeastLoc.getLots().size());
		
		// now add connections...
		
		assertTrue(offCenterLoc.addConnection(city.getLot(1, 2)));
		assertTrue(offCenterLoc.addConnection(city.getLot(2, 1)));
		assertFalse(offCenterLoc.addConnection(city.getLot(2, 2)));
		
		assertTrue(northeastLoc.addConnection(city.getLot(4, 2)));
		assertFalse(northeastLoc.addConnection(city.getLot(4, 2)));
		assertFalse(northeastLoc.addConnection(city.getLot(3, 3)));
		
		assertFalse(southCenterLoc.addConnection(city.getLot(2, 2)));
				
		// notice these are two distinct locations, despite being adjacent built		
	
		DrivableGraph graph = GraphFinder.findDrivableGraph(city);
		
		// vertices include:
			// 3 Locations
			// 1 xing at paved crossroads
			// 4 xings at end of road
			// 2 connections from location to road network where there isn't already an xing
		assertEquals(10, graph.vertexSet().size());
		// edges include
			// 6 entrances/exits
			// 12 roads between all xings and connection points
		assertEquals(18, graph.edgeSet().size());	
		
	}
	
	@Test
	public void testFindPavedNeighborsComplex(){
		System.out.println("**********************");
		System.out.println("***** findPavedNeighbors (complex)  ****");
		System.out.println("**********************");
		
		City city = new City(5,5);
		
//		. . . R R 
//		. R R R . 
//		R . R . . 
//		R . . . R 
//		R . . R . 
		
		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();

		city.getLot(1, 3).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(2, 3).makePaved();
		city.getLot(3, 3).makePaved();
		city.getLot(3, 4).makePaved();
		city.getLot(4, 4).makePaved();
		
		city.getLot(3, 0).makePaved();
		city.getLot(4, 1).makePaved();
		
		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		
		assertEquals(8, xings.keySet().size());
		
		Lot southwest = city.getLot(0,0);
		Lot west = city.getLot(0, 2);
		
		Lot center = city.getLot(2, 2);
		Lot centerNorth = city.getLot(2, 3);
		
		Lot northeast = city.getLot(4, 4);
		Lot northwest = city.getLot(1, 3);
		
		Lot southeast1 = city.getLot(3, 0);
		Lot southeast2 = city.getLot(4, 1);
		
		assertEquals(1, xings.get(southwest).size());
		assertEquals(1, xings.get(west).size());
		
		assertEquals(3, xings.get(centerNorth).size());
		assertEquals(1, xings.get(center).size());
		assertEquals(1, xings.get(northeast).size());
		assertEquals(1, xings.get(northwest).size());
		
		assertEquals(0, xings.get(southeast1).size());
		assertEquals(0, xings.get(southeast2).size());
		
		// now test whether this road network AS IS is correctly represented by GraphFinder
		
		Map<Xing, Set<Lot>> xingMap = GraphFinder.makeXingMap(xings);
		
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);
		
		assertEquals(8, roads.size());
		
	}
	
}
