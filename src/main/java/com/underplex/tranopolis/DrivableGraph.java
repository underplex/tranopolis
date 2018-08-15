package com.underplex.tranopolis;

import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;

@SuppressWarnings("serial")

/**
 * Represents the network of roads, xings, and locations as a graph.
 * <p>
 * The primary purpose of building such a graph would probably be to be able to perform graph operations on a representation of the road network.
 * <p>
 * For example, Dijkstra's algorithm can be used on this to determine shortest paths given weights.
 * <p>
 * Instances of Location may serve as vertices, but may not be present if they aren't actually connected to any Drivable instances that can serve as edges, such as Road.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class DrivableGraph extends DirectedPseudograph<Drivable, Drivable>{
	 
	private final City city;
	
	public DrivableGraph(City city) {
		super(null, null, true);
		this.city = city;
	}
	
	/**
	 * Prints to console a complete accounting of this graph.
	 */
	public void extensiveReport(){
		System.out.println("*** REPORT ON DRIVABLE GRAPH ***");
		
		for (Drivable xing : this.vertexSet()){
			System.out.println("____" + xing + ": ");
			for (Drivable d : this.incomingEdgesOf(xing)){
				System.out.println("_______ has " + d + " leading to it.");
			}
			for (Drivable d : this.outgoingEdgesOf(xing)){
				System.out.println("_______ has " + d + " leaving from it.");
			}
		}
	}

}
