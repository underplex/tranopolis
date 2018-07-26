package com.underplex.tranopolis;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Implementation of <tt>Resident</tt> interface that only plans drives from home to work and from work to home.
 * @author Brandon Irvine, brandon@underplex.com
 */
public class BasicResident extends AbstractResident implements Resident {

	public BasicResident(Location home){
		super(home);
	}

	@Override
	public Set<Drive> planDrives(LocalDateTime currentTime, DrivableGraph graph, LocalDateTime begin, LocalDateTime end) {
		
		Set<Drive> set = new HashSet<>();

		GraphPath<Drivable, Drivable> toWork = null;
		GraphPath<Drivable, Drivable> toHome = null;
		
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
	private GraphPath<Drivable, Drivable> findPreferred(DrivableGraph graph,Location begin, Location terminus){
		DijkstraShortestPath<Drivable, Drivable> dsp = new DijkstraShortestPath<Drivable, Drivable>(graph);
		return dsp.getPath(begin, terminus);
		
	}

}
