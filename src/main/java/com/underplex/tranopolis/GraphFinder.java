package com.underplex.tranopolis;

import java.util.ArrayList;
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
	 * Returns map where all keys are xings and values are sets of their paved
	 * neighbors.
	 * <p>
	 * A lot is considered an xing if it has 1, 3, or 4 paved neighbors.
	 * 
	 * @param city
	 * @return
	 */
	public static Map<Lot, Set<Lot>> findXingNeighbors(City city) {
		if (city.getHeight() < 3 | city.getWidth() < 3) {
			throw new IllegalArgumentException("Can't find xings in cities with any dimension < 3.");
		}
		
		Map<Lot, Set<Lot>> xings = new HashMap<Lot, Set<Lot>>();
		for (Lot lot : city.getLotManager().asSet()) {
			if (lot.isPaved()) {
				Set<Lot> pn = new HashSet<>();
				for (Lot n : city.getLotManager().getNeighbors(lot)) {
					if (n != null & n.isPaved()) { // n is guaranteed not to be
													// null, but double check
						pn.add(n);
					}
				}
				if (pn.size() == 0) {
					xings.put(lot, pn);
				} else if (pn.size() == 1) {
					xings.put(lot, pn);
				} else if (pn.size() == 2) {
					// do nothing
				} else if (pn.size() == 3) {
					xings.put(lot, pn);
				} else if (pn.size() == 4) {
					xings.put(lot, pn);
				}
			}
		}
		return xings;
	}

	/**
	 * Returns graph representing all roads and their connections.
	 * 
	 * @param city
	 * @return
	 */
	public static RoadGraph findRoadGraph(City city) {
		Map<Lot, Set<Lot>> xings = GraphFinder.findXingNeighbors(city);

		// add non-native xings to this
		
		Map<Lot, Set<Lot>> entryPoints = GraphFinder.findEntryPointNeighbors(city);
		for (Lot lot : entryPoints.keySet()){
			if (!xings.keySet().contains(lot)){
				xings.put(lot, entryPoints.get(lot));
			}
		}
		
		// find all edges...
		RoadGraph graph = new RoadGraph(city);
		for (Road r : findRoads(city, xings)) {
			graph.addRoad(r);
		}
		return graph;
	}
	
	/**
	 * Returns paved neighbors of the entry points in a city.
	 * @param city
	 * @return
	 */
	public static Map<Lot, Set<Lot>> findEntryPointNeighbors(City city){
	
		Map<Lot, Set<Lot>> map = new HashMap<>();
		for (Lot lot : city.getLotManager().getEntryPoints()){
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
	
	// Fix so that this algo only provides a single set of uniform Xings that are connected to the roads -- so no roads reference 2 separate Xings
	/**
	 * 
	 * Returns all edges in city given a map of xings to their paved neighbors.
	 * 
	 * @param city
	 * @param xings
	 * @return
	 */
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
}
