package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
		System.out.println("***** testRoad1   *****");
		System.out.println("**********************");
		
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

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
		
		Set<Lot> xings = GraphFinder.findXingNeighbors(city).keySet();

		assertEquals(6, xings.size());

		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		assertEquals(6, roadGraph.vertexSet().size());
		assertEquals(9, roadGraph.edgeSet().size());
		// xings are equal if they refer to same Lot in the City...
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(0, 1))));
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(3, 3))));
		assertTrue(roadGraph.vertexSet().contains(new Xing(city.getLot(1, 1))));

		assertFalse(roadGraph.vertexSet().contains(new Xing(city.getLot(1, 2))));

		assertEquals(4, roadGraph.inDegreeOf(new Xing(city.getLot(1, 1))));
		assertEquals(4, roadGraph.outDegreeOf(new Xing(city.getLot(1, 1))));

		assertEquals(1, roadGraph.outDegreeOf(new Xing(city.getLot(0, 1))));
		assertEquals(1, roadGraph.inDegreeOf(new Xing(city.getLot(1, 4))));

		assertEquals(1, roadGraph.inDegreeOf(new Xing(city.getLot(3, 3))));
		assertEquals(1, roadGraph.outDegreeOf(new Xing(city.getLot(3, 3))));

	}

	@Test
	public void testRoad2() {
		System.out.println("**********************");
		System.out.println("***** testRoad2   *****");
		System.out.println("**********************");
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

		counter = 0;

		for (Lot p : city.getLotManager().asSet()) {
			if (p.isPaved())
				counter++;
		}

		assertEquals(19, counter);

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		// GraphFinder.extensiveXingReport(xings);

		assertEquals(4, xings.keySet().size());

		RoadGraph roadGraph = GraphFinder.findRoadGraph(city);
		assertEquals(4, roadGraph.vertexSet().size());
		assertEquals(10, roadGraph.edgeSet().size());

		// xings are equal if they refer to same Lot in the City...

		// set up experiment to make sure weightings properly affect the choice
		// of path
		DijkstraShortestPath<Xing, Road> pathFinder = new DijkstraShortestPath<Xing, Road>(roadGraph);

		GraphPath<Xing, Road> path;

		path = pathFinder.getPath(new Xing(city.getLot(0, 2)), new Xing(city.getLot(4, 0)));
		assertEquals(2, path.getLength());

		// change some weights...

		// for (Road rp : path.getEdgeList()){
		// System.out.println(rp);
		// System.out.println("This road has weight " +
		// roadGraph.getEdgeWeight(rp));
		// }

		Road upperRoad;
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

		city.getLot(0, 0).makePaved();
		city.getLot(0, 1).makePaved();
		city.getLot(0, 2).makePaved();

		city.getLot(2, 0).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(2, 2).makePaved();

		city.getLotManager().printMap();

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		assertEquals(4, xings.keySet().size());

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		//
		//
		// }
		Set<Road> roads = GraphFinder.findRoads(city, xings);

		assertEquals(4, roads.size());

		counter = 0;

		for (Road road : roads) {
			// System.out.println(road.toString());
			assertNotEquals(road.getSourceXing(), road.getTargetXing());
			// for (Lot rw : road.getSegments()){
			//
			// System.out.println(rw.toString());
			//
			// }

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
		Set<Lot> neighbors;
		Lot lot;
		int counter;

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

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		assertEquals(2, xings.keySet().size());

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }

		Set<Road> roads = GraphFinder.findRoads(city, xings);

		assertEquals(2, roads.size());
		Road aRoad = Picker.selectRandom(roads);

		assertEquals(9, aRoad.getSegments().size());

		counter = 0;

		for (Road road : roads) {
			// System.out.println(road.toString());
			assertNotEquals(road.getSourceXing(), road.getTargetXing());
			// for (Lot rw : road.getSegments()){
			//
			// System.out.println(rw.toString());
			//
			// }

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
		Set<Lot> neighbors;
		Lot lot;
		int counter;

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

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		assertEquals(4, xings.keySet().size());
		assertTrue(xings.keySet().contains(city.getLot(4, 4)));
		assertTrue(xings.keySet().contains(city.getLot(0, 0)));
		assertTrue(xings.keySet().contains(city.getLot(2, 2)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }

		Set<Road> roads = GraphFinder.findRoads(city, xings);

		assertEquals(6, roads.size());

		for (Road r : roads) {
			if (r.getSourceXing().equals(city.getLot(2, 2)) && r.getTargetXing().equals(city.getLot(4, 4))) {
				assertEquals(7, r.getSegments().size());
			} else if (r.getSourceXing().equals(city.getLot(1, 4)) && r.getTargetXing().equals(city.getLot(2, 2))) {
				assertEquals(2, r.getSegments().size());
			}
		}

	}

	@Test
	/**
	 * A test of a fork with 4 road attached, including two no-segment roads, as
	 * well as a single xing unconnected to anything.
	 */
	public void testRoad6() {
		System.out.println("**********************");
		System.out.println("***** testRoad6   *****");
		System.out.println("**********************");
		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

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

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }
		assertEquals(6, xings.keySet().size());

		assertTrue(xings.keySet().contains(city.getLot(1, 1)));

		assertTrue(xings.keySet().contains(city.getLot(0, 1)));
		assertTrue(xings.keySet().contains(city.getLot(4, 1)));

		assertTrue(xings.keySet().contains(city.getLot(1, 0)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));
		assertTrue(xings.keySet().contains(city.getLot(3, 3)));

		assertFalse(xings.keySet().contains(city.getLot(4, 4)));

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }

		Set<Road> roads = GraphFinder.findRoads(city, xings);

		assertEquals(9, roads.size()); // 4 bidirectional ways to go and a
										// single loop

		for (Road r : roads) {
			if (r.getSourceXing().equals(city.getLot(3, 3)) && r.getTargetXing().equals(city.getLot(3, 3))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSourceXing().equals(city.getLot(0, 1)) && r.getTargetXing().equals(city.getLot(1, 1))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSourceXing().equals(city.getLot(1, 1)) && r.getTargetXing().equals(city.getLot(1, 0))) {
				assertEquals(0, r.getSegments().size());
			}
		}
	}

	@Test
	public void testRoad7() {
		System.out.println("**********************");
		System.out.println("***** testRoad7   *****");
		System.out.println("**********************");

		// this version of the test incorporates entry points

		City city = new City(5, 5);
		Set<Lot> neighbors;
		Lot lot;
		int counter;

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

		city.getLotManager().addEntryPoint(city.getLot(3, 1));

		Map<Lot, Set<Lot>> entryPoints = GraphFinder.findEntryPointNeighbors(city);
		assertEquals(1, entryPoints.keySet().size());
		assertTrue(entryPoints.keySet().contains(city.getLot(3, 1)));

		assertEquals(4, city.getLotManager().getNeighbors(city.getLot(3, 1)).size());

		// assertEquals(2, entryPoints.get(city.getLot(3, 1)).size());

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }

		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);
		assertEquals(6, xings.keySet().size());

		// check the entry point -- was it made into an xing?

		assertTrue(xings.keySet().contains(city.getLot(1, 1)));

		assertTrue(xings.keySet().contains(city.getLot(0, 1)));
		assertTrue(xings.keySet().contains(city.getLot(4, 1)));

		assertTrue(xings.keySet().contains(city.getLot(1, 0)));
		assertTrue(xings.keySet().contains(city.getLot(1, 4)));
		assertTrue(xings.keySet().contains(city.getLot(3, 3)));

		assertFalse(xings.keySet().contains(city.getLot(4, 4)));

		// System.out.println("Xings found: ");
		// for (Lot x : xings.keySet()){
		// System.out.println(x.toString() + " has paved neighbors:");
		// for (Lot pn : xings.get(x)){
		// System.out.println(".... at " + pn.toString());
		// }
		// }

		Set<Road> roads = GraphFinder.findRoads(city, xings);

		assertEquals(9, roads.size()); // 4 bidirectional ways to go and a
										// single loop

		for (Road r : roads) {
			if (r.getSourceXing().equals(city.getLot(3, 3)) && r.getTargetXing().equals(city.getLot(3, 3))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSourceXing().equals(city.getLot(0, 1)) && r.getTargetXing().equals(city.getLot(1, 1))) {
				assertEquals(0, r.getSegments().size());
			} else if (r.getSourceXing().equals(city.getLot(1, 1)) && r.getTargetXing().equals(city.getLot(1, 0))) {
				assertEquals(0, r.getSegments().size());
			}
		}
	}

}
