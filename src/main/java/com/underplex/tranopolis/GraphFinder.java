package com.underplex.tranopolis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for detecting roads and building them into a graph.
 * 
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class GraphFinder {

	private GraphFinder() {
		// don't instantiate
	}
	
	/**
	 * Returns graph representing the networks of Drivables (paved surfaces, intersections, and starting and ending points).
	 * <p>
	 * For example, Roads might be the edges, while Xings and Locations might be the vertices in the returned graph.
	 * <p>
	 * Generally, the returned graph will treat isolated paved Lots as isolated vertices.
	 * <p>
	 * Any given Xing-Xing pair connected by any number of Lots between them (0 or more) will have a Road in each direct between them.
	 * <p>
	 * Any Xing-Location pair will have a Road in each direct between them.
	 * <p>
	 * Any Xing or Location connected to itself (and nothing else) in a loop of paved Lots will have a Road edge going in both directions, clockwise and counter-clockwise.
	 * <p>
	 * Any paved lots with a number of paved neighbors that is not 2 will be considered an Xing.
	 * @param city
	 * @return
	 */
	public static DrivableGraph findDrivableGraph(City city) {
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingLots(city);

		// Map<Lot, Set<Lot>> entryPoints = GraphFinder.findEntryPointNeighbors(city);

		// add entry points that may not already be xings
		xings.putAll(GraphFinder.findEntryPointNeighbors(city));
		
		// convert them all into Xings with map to neighboring paved lots
		Map<Xing, Set<Lot>> xingMap = makeXingMap(xings);
		
		// find all Roads between Xings (but not Locations and the network)
		DrivableGraph graph = new DrivableGraph(city);

		// first add all possible vertices...
		// addEdge requires that vertices already be present in the graph before it's called, so we add all xings and locations as vertices

		for (Drivable v : xingMap.keySet()){
			graph.addVertex(v);
		}
		for (Drivable v : city.getLocationManager().getLocations()){
			graph.addVertex(v);
		}
		
		for (Road r : findRoadsWithXings(city, xingMap)) {
			// add needed vertices before edges b/c addEdge method requires vertices to already exist

			graph.addEdge(r.getSource(),r.getTarget(),r);
		}
		
//		for (Drivable d : graph.vertexSet()){
//			System.out.println("Graph has vertex at " + d.toString());
//		}
			
		Map<Location, Set<Lot>> locMap = city.getLocationManager().connectionMap();
		for (Location loc : locMap.keySet()){
			for (Lot lot : locMap.get(loc)){
				Xing xing = findXing(xingMap.keySet(),lot);
				Road entrance = new Road(xing,loc);
				Road exit = new Road(loc,xing);
				graph.addEdge(entrance.getSource(),entrance.getTarget(),entrance);
				graph.addEdge(exit.getSource(),exit.getTarget(),exit);
			}
		}
		return graph;
	}
	
	/**
	 * Returns map where all keys are Lots where an Xing should be and values are sets of their paved
	 * neighbors.
	 * <p>
	 * A lot is considered an xing if it has anything but 2 paved neighbors.
	 * <p>
	 * 
	 * @param city
	 * @return
	 */
	public static Map<Lot, Set<Lot>> findXingLots(City city) {
		if (city.getHeight() < 3 | city.getWidth() < 3) {
			throw new IllegalArgumentException("Can't find xings in cities with any dimension < 3.");
		}
		
		Map<Lot, Set<Lot>> xings = new HashMap<Lot, Set<Lot>>();
		for (Lot lot : city.getLotManager().asSet()) {
			if (lot.isPaved()) {
				Set<Lot> pn = new HashSet<>();
				for (Lot n : city.getLotManager().getNeighbors(lot)) {
					if (n == null) throw new IllegalStateException("LotManager.getNeighbors(Lot lot) seems to have returned a null Lot");
					if (n.isPaved()) {
						pn.add(n);
					}
				}
				if (pn.size() != 2) {
					xings.put(lot, pn);
				}
			}
		}
		return xings;
	}



	/**
	 * Returns Map similar to argument but where keys are all Xings representing the Lots.
	 * @param xings
	 * @return
	 */
	public static Map<Xing, Set<Lot>> makeXingMap(Map<Lot, Set<Lot>> xings) {
		Map<Xing, Set<Lot>> xingMap = new HashMap<Xing, Set<Lot>>();
		for (Lot lot : xings.keySet()){
			xingMap.put(new Xing(lot), xings.get(lot));
		}
		return xingMap;
	}
	
	/**
	 * Returns paved neighbors of the entry points in a city.
	 * @param city
	 * @return
	 */
	public static Map<Lot, Set<Lot>> findEntryPointNeighbors(City city){
		Set<Lot> lots = city.getLocationManager().connectionLots();
		Map<Lot, Set<Lot>> map = new HashMap<>();
		for (Lot lot : lots){
			Set<Lot> pn = new HashSet<>();

			for (Lot n : city.getLotManager().getNeighbors(lot)) {
				
				if (n != null & n.isPaved()) { // n is guaranteed not to be
												// null, but double check
					pn.add(n);
				}
			}
			map.put(lot, pn);
		}
		return map;
	}

	/**
	 * Returns all Roads in city given a map of all Xings to their paved neighbors.
	 * <p>
	 * The <tt>xings</tt> map is supposed to be an exhaustive set of the Xings in the city, including Lots that Locations will use to connect to the rest of the road network.
	 * <p>
	 * Xings that are isolated (meaning they have no paved neighbors) don't have any Roads associated with them.
	 * @param city
	 * @param xings
	 * @return
	 */
	public static Set<Road> findRoadsWithXings(City city, Map<Xing, Set<Lot>> xings) {
		Set<Road> roadSet = new HashSet<>();
		for (Xing sourceXing : xings.keySet()) {
			Set<Lot> pavedNeighbors = xings.get(sourceXing);
			
			// notice that isolated Xings will simply not have any paved neighbors to form a Road with
			for (Lot pn : pavedNeighbors) {
				Xing targetXing = null;
				Lot currentLot = pn;
				Lot lastLot = sourceXing.getLot();
				List<Lot> segments = new ArrayList<>();
				while (targetXing == null) {
					Xing currentXing = findXing(xings.keySet(), currentLot);
					if (currentXing != null) { // if you've
																// reached
																// another
																// xing...
						targetXing = currentXing;
					} else {
						// logically, currentLot must be paved but not be an Xing,
						// which implies that it has exactly 1 paved neighbor besides from the one we already processed (or the sourceXing itself)
						// we need to find this paved neighbor among the lots surrounding currentLot
						segments.add(currentLot);
						Lot temp = currentLot;
						for (Lot n : city.getLotManager().getNeighbors(currentLot)) {
							if (!n.equals(lastLot) && n.isPaved()) {
								// n is lot we're looking for!
								currentLot = n;
								lastLot = temp;
								break; // for loop shouldn't execute any
										// more checks
							}

						}
						// System.out.println();
					}
				}
				roadSet.add(new Road(sourceXing, targetXing, segments));
			}
			
		}

		return roadSet;
	}

	/**
	 * 
	 * Returns all edges in city given a map of xings to their paved neighbors.
	 * Deprecated - use findRoadsWithXings instead.
	 * @param city
	 * @param xings
	 * @return
	 */
	@Deprecated
	public static Set<Road> findRoads(City city, Map<Lot, Set<Lot>> xings) {
		Set<Road> edges = new HashSet<>();
		for (Lot sourceLot : xings.keySet()) {

			Xing sourceXing = new Xing(sourceLot);
			// filter for special case where xing is actually just a single,
			// solitary edge
			Set<Lot> pavedNeighbors = xings.get(sourceLot);

			if (pavedNeighbors.size() == 0) {
				// create single loop edge
				edges.add(new Road(sourceXing, sourceXing));

				// inefficiency might be here b/c we look at every edge about
				// twice, once for each direction
			} else { // deal with normal case, where sourceLot has multiple
						// neighbors
				for (Lot pn : pavedNeighbors) {
					Xing targetXing = null;
					Lot currentLot = pn;
					Lot lastLot = sourceLot;
					List<Lot> segments = new ArrayList<>();
					while (targetXing == null) {
						if (xings.keySet().contains(currentLot)) { // if you've
																	// reached
																	// another
																	// xing...
							targetXing = new Xing(currentLot);
						} else {
							// logically, currentLot must be paved and must have
							// exactly one other paved neighbor besides the one
							// we already processed (or the sourceXing itself)
							segments.add(currentLot);
							Lot temp = currentLot;
							for (Lot n : city.getLotManager().getNeighbors(currentLot)) {
								if (!n.equals(lastLot) && n.isPaved()) {
									// lot we're looking for!
									currentLot = n;
									lastLot = temp;
									break; // for loop shouldn't execute any
											// more checks
								}

							}
							// System.out.println();
						}
					}
					edges.add(new Road(sourceXing, targetXing, segments));
				}
			}
		}

		return edges;
	}

	public static void extensiveXingReport(Map<Lot, Set<Lot>> xings) {
		System.out.println("Xings found: ");
		for (Lot x : xings.keySet()) {
			System.out.println(x.toString() + " has paved neighbors:");
			for (Lot pn : xings.get(x)) {
				System.out.println(".... at " + pn.toString());
			}
		}
	}
	
	/**
	 * Return the first Xing in a Collection of Xings that is located at Lot.
	 * @param xings
	 * @param lot
	 * @return
	 */
	private static Xing findXing(Collection<Xing> xings, Lot lot){
		for (Xing xing : xings){
			if (xing.getLot().equals(lot)){
				return xing;
			}
		}
		return null;
	}
	
	public static Set<Xing> convertToXings(Collection<Lot> lots){
		Set<Xing> xings = new HashSet<>();
		for (Lot lot : lots){
			xings.add(new Xing(lot));
		}
		return xings;
	}
}
