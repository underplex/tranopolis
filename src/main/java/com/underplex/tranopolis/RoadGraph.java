package com.underplex.tranopolis;

import org.jgrapht.graph.DirectedPseudograph;

@SuppressWarnings("serial")

/**
 * Represents the network of roads and xings as a graph.
 * <p>
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class RoadGraph extends DirectedPseudograph<Xing,Road>{
	 
	private final City city;
	
	public RoadGraph(City city) {
		super(new RoadFactory(), true);
		this.city = city;
	}
	
	/**
	 * Specifically for adding a road.
	 * @param road
	 */
	public void addRoad(Road road){
		this.addVertex(road.getSourceXing());
		this.addVertex(road.getTargetXing());
		this.addEdge(road.getSourceXing(), road.getTargetXing(), road);
	}
	
	/**
	 * Prints to console a complete accounting of this graph.
	 */
	public void extensiveReport(){
		System.out.println("*** REPORT ON ROAD GRAPH ***");
		
		for (Xing xing : this.vertexSet()){
			System.out.println("____" + xing + ": ");
			for (Road road : this.incomingEdgesOf(xing)){
				System.out.println("_______ has " + road + " leading into it.");
			}
			for (Road road : this.outgoingEdgesOf(xing)){
				System.out.println("_______ has " + road + " leaving from it.");
			}
		}
	}
}
