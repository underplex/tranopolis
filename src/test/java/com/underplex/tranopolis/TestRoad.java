package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;

import com.underplex.tool.Picker;

/**
 * JUnit tests of road finding algo.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class TestRoad {

	@Test
	public void testRoad1() {
		System.out.println("**********************");
		System.out.println("***** testRoad1   ****");
		System.out.println("**********************");
		
		City city = new City(5, 5);
	
//		. R . . . 
//		. R . R . 
//		. R . . . 
//		R R R R R 
//		. R . . . 
		
		// make cross of two roads and add an isolated vertex paved lot at 3,3
		
		city.getLot(1, 1).makePaved(); // center of the cross

		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		// isolated paved lot at 3,3 
		city.getLot(3, 3).makePaved();

		city.getLotManager().printMap();
		
		Set<Lot> xings = GraphFinder.findXingLots(city).keySet();

		// xings at tips of cross, at center, at at isolated lot
		assertEquals(6, xings.size());

		DrivableGraph roadGraph = GraphFinder.findDrivableGraph(city);
		assertEquals(6, roadGraph.vertexSet().size());
		
		// no road is made using the isolated piece of road b/c it's not attached to anything
		
		assertEquals(8, roadGraph.edgeSet().size());
		
		// xings are equal if they refer to same Lot in the City...
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(0, 1))));
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(3, 3))));
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(1, 1))));

		assertFalse(roadGraph.vertexSet().contains(new Xing(city.getLot(1, 2))));

		assertEquals(4, roadGraph.inDegreeOf(new Xing(city.getLot(1, 1))));
		assertEquals(4, roadGraph.outDegreeOf(new Xing(city.getLot(1, 1))));

		assertEquals(1, roadGraph.outDegreeOf(new Xing(city.getLot(0, 1))));
		assertEquals(1, roadGraph.inDegreeOf(new Xing(city.getLot(1, 4))));

		assertEquals(0, roadGraph.inDegreeOf(new Xing(city.getLot(3, 3))));
		assertEquals(0, roadGraph.outDegreeOf(new Xing(city.getLot(3, 3))));

	}

	@Test
	public void testRoad2() {
		System.out.println("**********************");
		System.out.println("***** testRoad2   *****");
		System.out.println("**********************");
		City city = new City(5, 5);

		int counter;

		
//		R R R R R 
//		R . . . R 
//		R R R R R 
//		R . R . R 
//		R R R . R 
		
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

		counter = 0;

		for (Lot p : city.getLotManager().asSet()) {
			if (p.isPaved())
				counter++;
		}

		assertEquals(19, counter);

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		// GraphFinder.extensiveXingReport(xings);

		assertEquals(4, xings.keySet().size());

		DrivableGraph roadGraph = GraphFinder.findDrivableGraph(city);
		assertEquals(4, roadGraph.vertexSet().size());
		assertEquals(10, roadGraph.edgeSet().size());

		// xings are equal if they refer to same Lot in the City...

		// set up experiment to make sure weightings properly affect the choice
		// of path
		DijkstraShortestPath<Drivable, Drivable> pathFinder = new DijkstraShortestPath<Drivable, Drivable>(roadGraph);

		GraphPath<Drivable, Drivable> path;

		path = pathFinder.getPath(new Xing(city.getLot(0, 2)), new Xing(city.getLot(4, 0)));
		assertEquals(2, path.getLength());

		Drivable upperRoad;
		upperRoad = roadGraph.getEdge(new Xing(city.getLot(0, 2)), new Xing(city.getLot(4, 2)));
		roadGraph.setEdgeWeight(upperRoad, 99.0);

		path = pathFinder.getPath(new Xing(city.getLot(0, 2)), new Xing(city.getLot(4, 0)));
		assertEquals(3, path.getLength());

	}

	@Test
	public void testRoad3() {
		System.out.println("**********************");
		System.out.println("***** testRoad3   *****");
		System.out.println("**********************");
		City city = new City(3, 3);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

		
//		R . R 
//		R . R 
//		R . R 
		
		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();

		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(2, 2).makePaved();

		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		assertEquals(4, xings.keySet().size());

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(4, roads.size());

		counter = 0;

		for (Road road : roads) {
			assertNotEquals(road.getSource(), road.getTarget());

			if (road.getSegments().size() == 1) {
				counter++;
			}
		}

		assertEquals(4, counter);

	}

	@Test
	/**
	 * A test of a twisty road that doesn't fork.
	 */
	public void testRoad4() {
		System.out.println("**********************");
		System.out.println("***** testRoad4   *****");
		System.out.println("**********************");

		City city = new City(5, 5);
		
//		. . . . . 
//		. . . . . 
//		R R R . R 
//		R . R . R 
//		R . R R R 
		
		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();

		city.getLot(1, 2).makePaved();
		city.getLot(2, 2).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(2, 0).makePaved();

		city.getLot(3, 0).makePaved();
		city.getLot(4, 0).makePaved();

		city.getLot(4, 1).makePaved();
		city.getLot(4, 2).makePaved();

		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		assertEquals(2, xings.keySet().size());

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(2, roads.size());
		Road aRoad = Picker.selectRandom(roads);

		assertEquals(9, aRoad.getSegments().size());

		for (Road road : roads) {
			// System.out.println(road.toString());
			assertNotEquals(road.getSource(), road.getTarget());
		}
	}

	@Test
	/**
	 * A test of a forked road (so 3 roads).
	 */
	public void testRoad5() {
		System.out.println("**********************");
		System.out.println("***** testRoad5   *****");
		System.out.println("**********************");

		City city = new City(5, 5);

//		. R R . R 
//		. . R . R 
//		R R R . R 
//		R . R . R 
//		R . R R R 
		
		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();

		city.getLot(1, 2).makePaved();
		city.getLot(2, 2).makePaved();

		// fork 1

		city.getLot(2, 3).makePaved();
		city.getLot(2, 4).makePaved();
		city.getLot(1, 4).makePaved();

		// fork 2

		city.getLot(2, 1).makePaved();
		city.getLot(2, 0).makePaved();
		city.getLot(3, 0).makePaved();

		city.getLot(4, 0).makePaved();
		city.getLot(4, 1).makePaved();
		city.getLot(4, 2).makePaved();
		city.getLot(4, 3).makePaved();
		city.getLot(4, 4).makePaved();

		city.getLotManager().printMap();
		
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		
		assertEquals(4, xings.keySet().size());
		assertTrue(xings.keySet().contains(city.getLot(4, 4)));
		assertTrue(xings.keySet().contains(city.getLot(0, 0)));
		assertTrue(xings.keySet().contains(city.getLot(2, 2)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(6, roads.size());

		int found = 0;
		for (Road r : roads) {
			if (r.getSource().equals(new Xing(city.getLot(2, 2))) && r.getTarget().equals(new Xing(city.getLot(4, 4)))) {
				assertEquals(7, r.getSegments().size());
				found++;
			} else if (r.getSource().equals(new Xing(city.getLot(1, 4))) && r.getTarget().equals(new Xing(city.getLot(2, 2)))) {
				assertEquals(2, r.getSegments().size());
				found++;
			}
		}
		assertEquals(2,found);

	}

	@Test
	/**
	 * A test of a fork with 4 road attached, including two no-segment roads, as
	 * well as a single xing unconnected to anything.
	 * 
	 * The xing unconnected to anything should be an xing (to be turned into a vertex) but not have any road attached to it
	 */
	public void testRoad6() {
		System.out.println("**********************");
		System.out.println("***** testRoad6   *****");
		System.out.println("**********************");
		City city = new City(5, 5);
		
//		. R . . . 
//		. R . R . 
//		. R . . . 
//		R R R R R 
//		. R . . . 

		city.getLot(1, 1).makePaved();

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

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);

		assertEquals(6, xings.keySet().size());

		assertTrue(xings.keySet().contains(city.getLot(1, 1)));

		assertTrue(xings.keySet().contains(city.getLot(0, 1)));
		assertTrue(xings.keySet().contains(city.getLot(4, 1)));

		assertTrue(xings.keySet().contains(city.getLot(1, 0)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));
		assertTrue(xings.keySet().contains(city.getLot(3, 3)));

		assertFalse(xings.keySet().contains(city.getLot(4, 4)));

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(8, roads.size()); // 4 bidirectional ways to go

		int found = 0;
		for (Road r : roads) {
			if (r.getSource().equals(new Xing(city.getLot(0, 1))) && r.getTarget().equals(new Xing(city.getLot(1, 1)))) {
				assertEquals(0, r.getSegments().size());
				found++;
			} else if (r.getSource().equals(new Xing(city.getLot(1, 1))) && r.getTarget().equals(new Xing(city.getLot(1, 0)))) {
				assertEquals(0, r.getSegments().size());
				found++;
			}
		}
		
		assertEquals(2,found);
	}

	@Test
	/**
	 * Test that while forks a treated correctly, circles themselves are not turned into graphs, since they don't actually represent a network of any sort.
	 */
	public void testRoad7() {
		System.out.println("**********************");
		System.out.println("***** testRoad7   *****");
		System.out.println("**********************");

		City city = new City(7, 7);
		
//		. . . . . . . 
//		. . . R R R . 
//		. R . R . R . 
//		. R . R R R . 
//		. R . . . . . 
//		R R R R R . . 
//		. R . . . . . 
		
		city.getLot(1, 1).makePaved();

		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		// now make a circle with no xings
		city.getLot(3, 3).makePaved();
		city.getLot(4, 3).makePaved();
		city.getLot(5, 3).makePaved();

		city.getLot(3, 5).makePaved();
		city.getLot(4, 5).makePaved();
		city.getLot(5, 5).makePaved();
		
		city.getLot(3, 4).makePaved();
		city.getLot(5, 4).makePaved();
		
		// note that b/c there are no locations that create any entry point, no graph is made out of the circle
		
		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		
		// 3, 3 is NOT an xing
		assertEquals(5, xings.keySet().size());

		assertTrue(xings.keySet().contains(city.getLot(1, 1)));

		assertTrue(xings.keySet().contains(city.getLot(0, 1)));
		assertTrue(xings.keySet().contains(city.getLot(4, 1)));

		assertTrue(xings.keySet().contains(city.getLot(1, 0)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));
		
		// 3, 3 is not an xing because it's part of a circle
		assertFalse(xings.keySet().contains(city.getLot(3, 3)));

		assertFalse(xings.keySet().contains(city.getLot(4, 4)));

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(8, roads.size()); // 4 bidirectional ways to go and a

		int found = 0;
		for (Road r : roads) {
			if (r.getSource().equals(new Xing(city.getLot(0, 1))) && r.getTarget().equals(new Xing(city.getLot(1, 1)))) {
				assertEquals(0, r.getSegments().size());
				found++;
			} else if (r.getSource().equals(new Xing(city.getLot(1, 1))) && r.getTarget().equals(new Xing(city.getLot(1, 0)))) {
				assertEquals(0, r.getSegments().size());
				found++;
			}
		}
		assertEquals(2,found);
		
	}
	
	@Test
	/**
	 * Test that while forks a treated correctly, circles themselves are not turned into graphs, since they don't actually represent a network of any sort.
	 */
	public void testRoad8() {
		System.out.println("**********************");
		System.out.println("***** testRoad8   *****");
		System.out.println("**********************");

		City city = new City(7, 7);
		
//		. . . . . . . 
//		. . . R R R . 
//		. R . R . R . 
//		. R . R R R . 
//		. R . . R . . 
//		R R R R R . . 
//		. R . . . . . 
		
		city.getLot(1, 1).makePaved();

		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		// now make a circle with no xings
		city.getLot(3, 3).makePaved();
		city.getLot(4, 3).makePaved();
		city.getLot(5, 3).makePaved();

		city.getLot(3, 5).makePaved();
		city.getLot(4, 5).makePaved();
		city.getLot(5, 5).makePaved();
		
		city.getLot(3, 4).makePaved();
		city.getLot(5, 4).makePaved();
		city.getLot(4, 2).makePaved();
				
		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		
		// 3, 4 is an xing, but 4, 1 isn't
		assertEquals(5, xings.keySet().size());

		assertTrue(xings.keySet().contains(city.getLot(1, 1)));
		assertTrue(xings.keySet().contains(city.getLot(0, 1)));
		assertTrue(xings.keySet().contains(city.getLot(1, 0)));

		assertTrue(xings.keySet().contains(city.getLot(1, 4)));

		assertTrue(xings.keySet().contains(city.getLot(4, 3)));

		// 4, 1 is just an elbow, not an xing
		assertFalse(xings.keySet().contains(city.getLot(4, 1)));
		
		// 3, 3 is not an xing because it's part of a circle
		assertFalse(xings.keySet().contains(city.getLot(3, 3)));
		assertFalse(xings.keySet().contains(city.getLot(4, 4)));

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(10, roads.size()); // includes a circle with roads in either direction 

		Road circle1 = null;
		Road circle2 = null;
		System.out.println("Circle1 starts as : " + circle1);
		System.out.println("Circle2 starts as : " + circle2);
		for (Road r : roads) {
			if (r.getSource().equals(new Xing(city.getLot(0, 1))) && 
					r.getTarget().equals(new Xing(city.getLot(1, 1)))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSource().equals(new Xing(city.getLot(1, 1))) && 
					r.getTarget().equals(new Xing(city.getLot(1, 0)))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSource().equals(new Xing(city.getLot(4, 3))) && 
					r.getTarget().equals(new Xing(city.getLot(4, 3)))) {
				System.out.println();
				if (circle1 == null){
					circle1 = r;
					System.out.println("Circle1 is assigned r: " + r);
					
				} else {
					circle2 = r;
					System.out.println("Circle2 is assigned r: " + r);
				}
				assertEquals(r.getTarget(), r.getSource());
				assertEquals(7, r.getSegments().size()); 
			}		
			
		}
		
		System.out.println("Circle1 is " + circle1);
		System.out.println("Circle2 is " + circle2);
		
		// check that the two circle directionals are not exactly the same but actually the reverse by checking their segments
		assertFalse(circle1.equals(circle2));
		assertFalse(circle1.getSegments().equals(circle2.getSegments()));
		List<Lot> c2 = circle2.getSegments();
		Collections.reverse(c2);
		assertTrue(circle1.getSegments().equals(c2));
		
	}

	@Test
	/**
	 * Test that blobs of Xings.
	 */
	public void testRoad9() {
		System.out.println("**********************");
		System.out.println("***** testRoad9   *****");
		System.out.println("**********************");

//		. . . . . . . 
//		. . . R R R . 
//		. . . R R . . 
//		. . . R R R . 
//		. . . . . . . 
//		R R . . . . . 
//		R R . . . . . 
		
		City city = new City(7, 7);
		
		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(1, 0).makePaved();
		city.getLot(1, 1).makePaved();

		// now make a circle with no xings
		city.getLot(3, 3).makePaved();
		city.getLot(4, 3).makePaved();
		city.getLot(5, 3).makePaved();

		city.getLot(3, 5).makePaved();
		city.getLot(4, 5).makePaved();
		city.getLot(5, 5).makePaved();
		
		city.getLot(3, 4).makePaved();
		city.getLot(4, 4).makePaved();
				
		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);
		
		assertEquals(6, xings.keySet().size());

		assertTrue(xings.keySet().contains(city.getLot(4, 4)));
		assertTrue(xings.keySet().contains(city.getLot(4, 3)));
		assertTrue(xings.keySet().contains(city.getLot(4, 5)));
		assertTrue(xings.keySet().contains(city.getLot(3, 4)));

		assertTrue(xings.keySet().contains(city.getLot(5, 3)));

		assertTrue(xings.keySet().contains(city.getLot(5, 5)));
		
		assertFalse(xings.keySet().contains(city.getLot(0, 0)));
		assertFalse(xings.keySet().contains(city.getLot(1, 1)));

		Map<Xing,Set<Lot>> xingMap = new HashMap<>();
		for (Lot myLot : xings.keySet()){
			xingMap.put(new Xing(myLot), xings.get(myLot));
		}
			
		Set<Road> roads = GraphFinder.findRoadsWithXings(city, xingMap);

		assertEquals(14, roads.size()); // includes a circle with roads in either direction 

		int found = 0;
		for (Road r : roads) {
			if (r.getSource().equals(new Xing(city.getLot(5, 5))) && 
					r.getTarget().equals(new Xing(city.getLot(4, 5)))) {
				assertEquals(0, r.getSegments().size());
				found++;
			} else if (r.getSource().equals(new Xing(city.getLot(4, 3))) && 
					r.getTarget().equals(new Xing(city.getLot(3, 4)))) {
				assertEquals(1, r.getSegments().size());
				found++;
			}				
		}
		
		assertEquals(2, found);
				
	}

}
