package com.underplex.tranopolis;

import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;

@SuppressWarnings("serial")

/**
 * Represents the network of roads and xings as a graph.
 * <p>
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
