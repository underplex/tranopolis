package com.underplex.tranopolis;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Implementation of <tt>Resident</tt> interface that only plans drives from home to work and from work to home.
 * <p>
 * Starting at 7 am and 4 pm, 
 * @author Brandon Irvine, brandon@underplex.com
 */
public class BasicResident extends AbstractResident implements Resident {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	Drive last;
	boolean hasGoneToWork = false;
	GraphPath<Drivable, Drivable> toWork;
	GraphPath<Drivable, Drivable> toHome;
 	
	public BasicResident(Location home, Location work){
		super(home, work);
	}

	public BasicResident(Location home){
		this(home,null);
	}
	
	@Override
	public Set<Drive> planDrives(LocalDateTime currentTime, DrivableGraph graph, LocalDateTime begin, LocalDateTime end) {
		
		Drive drive = null;
			
		LocalDate date = begin.toLocalDate();
		
		if (!begin.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !begin.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
			if (begin.getHour() >= 7 && 
					begin.getHour() <= 8 &&
					!hasGoneToWork){
				LOGGER.info("Adding a commute to work...");
				// find a home path
				if (toWork == null){
					toWork = findPreferred(graph, getHome(), getWork());
				}
				
				LocalDateTime startTime = date.atTime(7, 0).plusMinutes(new Random().nextInt(121));
				LocalDateTime dropTime = date.atTime(9, 0);
				
				drive = new Drive(toWork, this, startTime, this.getHome(), this.getWork(), dropTime);
				hasGoneToWork = true;
			} else if (begin.getHour() >= 16 && 
					begin.getHour() <= 23 &&
					hasGoneToWork){
				// find a work path
				LOGGER.info("Adding a commute home...");
				if (toHome == null){
					toHome = findPreferred(graph, getWork(), getHome());
				}
				LocalDateTime startTime = date.atTime(16, 0).plusMinutes(new Random().nextInt(121));
				LocalDateTime dropTime = date.atTime(23, 0);

				drive = new Drive(toHome, this, startTime, this.getWork(), this.getHome(), dropTime);
				hasGoneToWork = false;
			}
			
		}
		Set<Drive> rSet = new HashSet<>();
		if (drive != null){
			rSet.add(drive);
		}
		return rSet;				
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
