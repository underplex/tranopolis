package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TestXingFinder {
	
	@Test
	public void testNeighbors() {
		
		City city = new City(3, 3);
		Set<Lot> neighbors;
		Lot lot;
		
		lot = city.getLot(0, 2);
		neighbors = city.getLotManager().getNeighbors(lot);
		assertEquals(2, neighbors.size());

		assertEquals(true, neighbors.contains(city.getLot(0, 1)));
		assertEquals(true, neighbors.contains(city.getLot(1, 2)));

		assertEquals(false, neighbors.contains(city.getLot(0, 0)));
		assertEquals(false, neighbors.contains(city.getLot(2, 0)));

		lot = city.getLot(1, 1);
		neighbors = city.getLotManager().getNeighbors(lot);
		assertEquals(4, neighbors.size());

		assertEquals(true, neighbors.contains(city.getLot(0, 1)));
		assertEquals(true, neighbors.contains(city.getLot(1, 2)));

		assertEquals(false, neighbors.contains(city.getLot(2, 2)));
		assertEquals(false, neighbors.contains(city.getLot(0, 0)));

	}

	@Test
	public void testXingFinder() {
		
		City city = new City(3, 3);
		Set<Lot> neighbors;
		Lot lot;
		
		city.getLot(1, 1).makePaved();
		
		assertEquals(true, city.getLot(1, 1).isPaved());
		
		assertEquals(false, city.getLot(0, 1).isPaved());
		assertEquals(false, city.getLot(2, 2).isPaved());

		city.getLot(0, 1).makePaved();

		assertEquals(true, city.getLot(0, 1).isPaved());
		assertEquals(false, city.getLot(2, 2).isPaved());

		city.getLot(2, 1).makePaved();
		
		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		
		neighbors = city.getLotManager().getNeighbors(city.getLot(1, 1));
		
		assertEquals(4, neighbors.size());
		
		assertEquals(9, city.getLotManager().asSet().size());
		
		int counter = 0;
		for (Lot paved : city.getLotManager().asSet()){
			if (paved.isPaved())  counter++;
		}
		
		assertEquals(5, counter);
		
		Map<Lot, Set<Lot>> xingMap = GraphFinder.findXingLots(city);
		
		assertEquals(5, xingMap.keySet().size());
		assertEquals(true, xingMap.keySet().contains(city.getLot(1, 1)));
		assertEquals(true, xingMap.keySet().contains(city.getLot(0, 1)));
		assertEquals(true, xingMap.keySet().contains(city.getLot(1, 2)));
		assertEquals(false, xingMap.keySet().contains(city.getLot(2, 2)));
		
		
	}
	
	@Test
	/**
	 * Tests isolated vertices in GraphFinder.
	 */
	public void testXingFinderIsolated() {
		
		City city = new City(3, 3);
		Set<Lot> neighbors;
		Lot lot;
		
		city.getLot(1, 1).makePaved();
		
		Map<Lot, Set<Lot>> xingMap = GraphFinder.findXingLots(city);
		
		assertEquals(1, xingMap.keySet().size());
		assertEquals(true, xingMap.keySet().contains(city.getLot(1, 1)));
		assertEquals(false, xingMap.keySet().contains(city.getLot(0, 1)));
		assertEquals(false, xingMap.keySet().contains(city.getLot(1, 2)));
		assertEquals(false, xingMap.keySet().contains(city.getLot(2, 2)));
		
		
	}
	
	@Test
	public void testXingFinder5x5() {
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();
		city.getLot(0, 3).makePaved();
		city.getLot(0, 4).makePaved();
		
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
		
		counter = 0;
		
		for (Lot p : city.getLotManager().asSet()){
			if (p.isPaved())  counter++;
		}
		
		assertEquals(18, counter);
				
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		GraphFinder.extensiveXingReport(xings);
		assertEquals(2, xings.keySet().size());
	}
	
}
