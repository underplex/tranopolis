package com.underplex.tranopolis;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;

/**
 * Basic implementation of the <tt>Resident</tt> interface.
 * <p>
 * The only Drives this Resident will plan are to and from work.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class BasicResident extends Resident{

	public BasicResident(Location home){
		super(home);
	}

	@Override
	public Set<Drive> planDrives(LocalDateTime currentTime, RoadGraph graph, LocalDateTime begin, LocalDateTime end) {
		
		Set<Drive> set = new HashSet<>();

		GraphPath<Xing,Road> toWork = null;
		GraphPath<Xing,Road> toHome = null;
		
		LocalDateTime i = LocalDateTime.of(begin.toLocalDate(), begin.toLocalTime());
		while (i.isBefore(end)){
			if (!i.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !i.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				if (i.getHour() == 8){
					// find a home path
					if (toWork == null){
						toWork = findPreferred(graph, getHome(), getWork());
					}
					set.add(new Drive(toWork, this, i, this.getHome(), this.getWork()));
									
				} else if (i.getHour() == 5){
					// find a work path
					if (toHome == null){
						toHome = findPreferred(graph, getWork(),getHome());
					}
					set.add(new Drive(toHome, this, i, this.getWork(), this.getHome()));
				}
				
			}
			i = i.plusHours(1);
		}
		return set;				
	}	

	/**
	 * Find the preferred path of this resident to get from one place to another.
	 * @param graph
	 * @param begin
	 * @param terminus
	 * @return
	 */
	private GraphPath<Xing, Road> findPreferred(RoadGraph graph,Location begin, Location terminus){
		List<GraphPath<Xing, Road>> list = new ArrayList<>(PathFinder.findShortestPaths(graph, begin, terminus)); 
		GraphPath<Xing, Road> best = Collections.max(list, new Comparator<GraphPath<Xing, Road>>() {
				@Override
				public int compare(GraphPath<Xing, Road> path1, GraphPath<Xing, Road> path2) {
					Double w = path1.getWeight();
					return -(w.compareTo(path2.getWeight()));
				}
			});
		return best;
	}

}
