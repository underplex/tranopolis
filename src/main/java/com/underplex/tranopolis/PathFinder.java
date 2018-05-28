package com.underplex.tranopolis;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Utility class with static methods for finding <tt>GraphPath<Xing, Road></tt> paths and otherwise dealing with them.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class PathFinder {

	private PathFinder(){
		// don't instantiate
	}
	
	/**
	 * Returns Set representing the shortest paths for every pair of from and to. 
	 * @param from
	 * @param to
	 * @return
	 */
	public static Set<GraphPath<Xing, Road>> findShortestPaths(Graph<Xing, Road> graph, Location from, Location to){
		Set<AbstractMap.SimpleImmutableEntry<Xing, Xing>> pairs = findPairs(from.getOnOffPoints(), to.getOnOffPoints());
		Set<GraphPath<Xing, Road>> set = new HashSet<>();
		for (AbstractMap.SimpleImmutableEntry<Xing, Xing> p : pairs){
			set.add(DijkstraShortestPath.findPathBetween(graph, p.getKey(), p.getValue()));
		}
		return set;
	}
	
	/**
	 * This method and its javadoc are cut-and-pasted from com.underplex.tool.Combiner, but that isn't released or tested yet.
	 * <p>
	 * Returns <tt>Set</tt> of every possible combination of a <tt>T</tt> key matched to a <tt>U</tt> value.
	 * <p>
	 * Note that there is nothing stopping a key from mapping to itself if the element is in both passed Sets.
	 * <p>
	 * Returns an empty <tt>Set</tt> if either <tt>keys</tt> or values are empty or <tt>null</tt>.
	 * @return <tt>Set</tt> of every possible combination of a <tt>T</tt> key matched to a <tt>U</tt> value 
	 */
	public static <T, U> Set<AbstractMap.SimpleImmutableEntry<T, U>> findPairs(Set<T> keys, Set<U> values){
		Set<AbstractMap.SimpleImmutableEntry<T, U>> set = new HashSet<>();
		if (keys == null || values == null){
			return set; // not early return!
		}
		for (T t : keys){
			for (U u : values){
				set.add(new AbstractMap.SimpleImmutableEntry<T, U>(t, u));
			}
		}
		return set;
	}
}
