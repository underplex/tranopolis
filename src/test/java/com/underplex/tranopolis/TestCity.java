package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
		
		// make a cross
		
		city.getLot(0, 2).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(3, 2).makePaved();
		city.getLot(4, 2).makePaved();
		
		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		
		city.getLot(2, 3).makePaved();
		city.getLot(2, 4).makePaved();
		
		city.getLotManager().printMap();
		
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		assertEquals(5, xings.keySet().size());
		
		Lot center = city.getLot(2,2);
		Lot east = city.getLot(4, 2);
		Lot north = city.getLot(2, 4);
		
		assertEquals(4, xings.get(center).size());
		assertEquals(1, xings.get(east).size());
		assertNotEquals(3, xings.get(north).size());
		
		assertTrue(xings.get(north).contains(city.getLot(2,3)));
		
		
	}
	
	@Test
	public void testFindPavedNeighborsComplex(){
		System.out.println("**********************");
		System.out.println("***** findPavedNeighbors (complex)  ****");
		System.out.println("**********************");
		
		City city = new City(5,5);
		
		// make a cross
		
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
		
		// assertTrue(xings.get(north).contains(city.getLot(2,3)));
		
		// now test whether this road network is correctly represented by GraphFinder
		
		Map<Xing, Set<Lot>> xingMap = GraphFinder.makeXingMap(xings);
		
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);
		
		assertEquals(8, roads.size());
		
		// check that locations are adequately added, etc.
		
		Location loc1 = city.getLocationManager().makeLocation(city.getLot(0, 4));
		Location loc2 = city.getLocationManager().makeLocation(city.getLot(3, 1));
		
		Set<Lot> officeSet = new HashSet<>();
		officeSet.add(city.getLot(1, 4));
		officeSet.add(city.getLot(2, 4));
	
		Location development = city.getLocationManager().makeLocation(city.getLot(0, 3));
		Location officePark = city.getLocationManager().makeLocation(officeSet);

		// test basic assumptions about locations
		
		assertTrue(development.addConnection(northeast));
		
		assertTrue(officePark.addConnection(northwest));
		assertTrue(loc1.addConnection(southeast1));
		assertTrue(loc1.addConnection(southeast2));
		assertTrue(loc2.addConnection(southeast1));
		assertTrue(loc2.addConnection(southeast2));
		
		// we can't add the lots again
		
		assertTrue(!development.addConnection(northeast));
		
		assertTrue(!officePark.addConnection(northwest));
		assertTrue(!loc1.addConnection(southeast1));
		assertTrue(!loc1.addConnection(southeast2));
		
		assertTrue(!loc2.addConnection(southeast1));
		assertTrue(!loc2.addConnection(southeast2));
		
		// now build a graph

		assertEquals(4, city.getLocationManager().connectionMap().keySet().size());
		city.getLocationManager().extensiveReport();
		
		DrivableGraph roadNetwork = GraphFinder.findDrivableGraph(city);
		
		// in addition to the 8 existing roads, we've added 6 entrances/6 exits by adding locations and connecting them
		roadNetwork.extensiveReport();
		assertEquals(4,city.getLocationManager().connectionMap().keySet().size());
		assertEquals(20,roadNetwork.edgeSet().size());
				
	}


}
