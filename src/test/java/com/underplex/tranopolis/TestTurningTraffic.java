package com.underplex.tranopolis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;

/**
 * Test traffic of a single resident in a single, isolated paved lot.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class TestTurningTraffic {
		
	@Test
	public void simpleTraffic() {
		System.out.println("**********************");
		System.out.println("***** turningTraffic   *****");
		System.out.println("**********************");
		
		City city = new City(5, 5);

		// make cross of two roads and add an isolated paved lot at 3,3
		city.getLot(1, 1).makePaved(); // center of the cross

//		b R . b . 
//		. R . R b 
//		. R . . . 
//		R R R R R 
//		b R . . b 
		
		city.getLot(0, 1).makePaved();
		city.getLot(2, 1).makePaved();
		city.getLot(3, 1).makePaved();
		city.getLot(4, 1).makePaved();

		city.getLot(1, 0).makePaved();
		city.getLot(1, 2).makePaved();
		city.getLot(1, 3).makePaved();
		city.getLot(1, 4).makePaved();

		city.getLot(3, 3).makePaved();

		city.getLot(3, 4).makeBuilt();
		city.getLot(4, 3).makeBuilt();
		city.getLot(4, 0).makeBuilt();
		

		city.getLot(0, 0).makeBuilt();
		city.getLot(0, 4).makeBuilt();
		
		city.getLotManager().printMap();
		
		
		
		// start by testing the actual graph...
		
		city.getGraphManager().updateGraph();
		
		DrivableGraph roadGraph = city.getRoadGraph();
		
		// how to add xing

		// test vertice between any locations are added
		// six vertices here -- including 1 isolated vertex
		assertEquals(6, roadGraph.vertexSet().size());
		
		Xing iso = new Xing(city.getLot(3, 3));

		Location eastLoc = city.getLocationManager().makeLocation(city.getLot(3, 4), "Eastview Apts");
		Location northLoc = city.getLocationManager().makeLocation(city.getLot(4, 3), "North Lake Mall");
		Location southeastLoc = city.getLocationManager().makeLocation(city.getLot(4, 0), "Southern Hills Condos");
		Location southwestLoc = city.getLocationManager().makeLocation(city.getLot(0, 0), "Southwest Mountain Apartments");
		Location northwestLoc = city.getLocationManager().makeLocation(city.getLot(0, 4), "Northwest Heights Office Park");
		
		assertTrue(city.connectLocation(eastLoc, city.getLot(3, 3)));
		assertTrue(city.connectLocation(northLoc, city.getLot(3, 3)));
		assertTrue(city.connectLocation(southeastLoc, city.getLot(4, 1)));
		
		assertTrue(city.connectLocation(southwestLoc, city.getLot(1, 0)));
		assertTrue(city.connectLocation(northwestLoc, city.getLot(1, 4)));
		
		// can't "reconnect" these paved lots to the locations
		assertFalse(city.connectLocation(eastLoc, city.getLot(3, 3)));
		assertFalse(city.connectLocation(northLoc, city.getLot(3, 3)));
		assertFalse(city.connectLocation(southeastLoc, city.getLot(4, 1)));

		// places that can't be connected for logical reasons, b/c none of the locations are adjacent
		assertFalse(city.connectLocation(eastLoc, city.getLot(3, 1)));
		assertFalse(city.connectLocation(northLoc, city.getLot(3, 1)));
		assertFalse(city.connectLocation(southeastLoc, city.getLot(3, 1)));

		// places that can't be connected for b/c they're not paved
		assertFalse(city.connectLocation(eastLoc, city.getLot(4, 4)));
		assertFalse(city.connectLocation(northLoc, city.getLot(4, 4)));
		assertFalse(city.connectLocation(southeastLoc, city.getLot(3, 0)));
		
		assertEquals(1,eastLoc.getConnections().size());
		assertEquals(1,northLoc.getConnections().size());
		assertEquals(1,southeastLoc.getConnections().size());
		
		assertTrue(eastLoc.getConnections().contains(city.getLot(3,3)));
		assertTrue(northLoc.getConnections().contains(city.getLot(3,3)));
		assertTrue(southeastLoc.getConnections().contains(city.getLot(4,1)));
	
		city.getGraphManager().updateGraph();
		
		roadGraph = city.getRoadGraph();

		// vertices include:
			// 1 crossroads at center of cross
			// 4 ends of those roads (including 1 that is also a connection point)
			// 5 locations
			// 1 connection point to an isolated paved xing that happens to also be connection point for both locations
		
		assertEquals(11, roadGraph.vertexSet().size());
		
		// edges include:
			// 8 roads in cross
			// 8 entrances/exits for 4 locations connected each to a single connection point
			// 2 entrances/exits for southeast location connected to a single connection point
		
		
		// not clear if the locations are part of the graph network -- check this
		assertTrue(roadGraph.containsVertex(eastLoc));
		assertTrue(roadGraph.containsVertex(northLoc));
		assertTrue(roadGraph.containsVertex(southeastLoc));
				
		assertEquals(18, roadGraph.edgeSet().size());
		
		GraphPath<Drivable, Drivable> path1 = DijkstraShortestPath.findPathBetween(roadGraph, eastLoc, northLoc);
		GraphPath<Drivable, Drivable> path2 = DijkstraShortestPath.findPathBetween(roadGraph, eastLoc, southeastLoc);
		GraphPath<Drivable, Drivable> path3 = DijkstraShortestPath.findPathBetween(roadGraph, southeastLoc, northwestLoc);
		
		assertEquals(null, path2); // per JGraphT documentation, method returns null when no path is possible
		assertNotEquals(null, path1);		
		assertNotEquals(null, path3);		
		
		assertEquals(2, path1.getEdgeList().size());
		assertEquals(3, path1.getVertexList().size());

		assertEquals(eastLoc,path1.getVertexList().get(0));
		assertEquals(new Xing(city.getLot(3, 3)),path1.getVertexList().get(1));
		assertEquals(northLoc,path1.getVertexList().get(2));
		
		assertEquals(4, path3.getEdgeList().size());
		assertEquals(5, path3.getVertexList().size());
		
		Drivable eastLocExit = path1.getEdgeList().get(0);
		Drivable northLocEntrance = path1.getEdgeList().get(1);
		
//		System.out.println(eastLocExit);
//		System.out.println(northLocEntrance);
		
		Resident rez = new BasicResident(eastLoc);
		Resident otherRez = new BasicResident(southeastLoc);
		
		assertEquals(eastLoc,rez.getCurrentLocation());
		assertEquals(eastLoc,rez.getHome());
		
		assertEquals(southeastLoc,otherRez.getCurrentLocation());
		assertEquals(southeastLoc,otherRez.getHome());
		
		Drive drive = new Drive(path1,
				rez,
				city.getTimeManager().getCurrentTime(),
				eastLoc,
				northLoc);

		Drive otherDrive = new Drive(path3,
				otherRez,
				city.getTimeManager().getCurrentTime().plusMinutes(10),
				southeastLoc,
				northwestLoc);
		
		// System.out.println(drive.next(eastLocExit));
		
		assertEquals(eastLocExit, drive.next(eastLoc));
		assertEquals(new Xing(city.getLot(3, 3)), drive.next(eastLocExit));
		assertEquals(northLocEntrance, drive.next(new Xing(city.getLot(3, 3))));
		assertEquals(northLoc, drive.next(northLocEntrance));
		
		city.getResidentManager().addUpcomingDrive(drive);
		city.getResidentManager().addUpcomingDrive(otherDrive);
		
		assertEquals(DriveDisposition.WAITING, drive.getDisposition());
		assertEquals(DriveDisposition.WAITING, otherDrive.getDisposition());
		
		LocalDateTime genesis = city.getTimeManager().getCurrentTime();
		Set<Drive> finished = new HashSet<>();
		
		while ((drive.getDisposition() != DriveDisposition.FINISHED ||
				otherDrive.getDisposition() != DriveDisposition.FINISHED) &&
				city.getTimeManager().getCurrentTime().isBefore(genesis.plusMinutes(30))){
			city.advance();
			finished.addAll(city.getLocationManager().getFinishedDrives());
			city.getLocationManager().dumpFinishedDrives();
		}
		
		assertEquals(DriveDisposition.FINISHED, drive.getDisposition());
		assertEquals(DriveDisposition.FINISHED, otherDrive.getDisposition());
		
		assertEquals(2, finished.size());
//		Drive finishedDrive = new ArrayList<Drive>(finished).get(0);
//		assertEquals(DriveDisposition.FINISHED, finishedDrive.getDisposition());
				
	}
}
