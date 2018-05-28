package com.underplex.tranopolis;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Manages the <tt>Lot</tt>s in a <tt>City</tt>.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class LotManager {

	private final City city;
	private final int width;
	private final int height;
	private final Lot[][] lots;
	private final Set<Lot> set;
	private final Set<Lot> entryPoints;
	
	public LotManager(City city, int width, int height) {
		this.city = city;
		this.width = width;
		this.height = height;
		this.entryPoints = new HashSet<>();
		this.lots = new Lot[width][height];
		this.set = new HashSet<>();
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				Lot aLot = new Lot(x, y);
				lots[x][y] = aLot;
				set.add(aLot);
			}
		}
	}

	/**
	 * Returns <tt>Lot</tt> at x, y or <tt>null</tt> if none.
	 * @param x int coordinate at x
	 * @param y int coordinate at y
	 * @return <tt>Lot</tt> at x, y or <tt>null</tt> if none
	 */
	public Lot getLot(int x, int y) {
		if (x >= 0 && x < this.width && y < this.height && y >= 0 ){
			return lots[x][y];
		}
		return null;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the <tt>Set</tt> of <tt>Lot</tt>s neighboring <tt>lot</tt>.
	 * <p>
	 * The returned <tt>Set</tt> is just all legal neighbors of <tt>lot</tt>. No <tt>null</tt> value is present.
	 * <p>
	 * Obviously, in a field of 3 x 3 or more, a corner <tt>lot</tt> will have 2 neighbors; a lot on the side of the field but not in a corner will have 3 neighbors; 
	 * all others will have 4 neighbors.
	 * <p>
	 * In cases where the field is less than 3 x 3, other numbers of neighbors are possible, including even none, which would be represented by an empty <tt>Set</tt>.
	 * @param lot center <tt>Lot</tt> whose neighbors are returned
	 * @return <tt>Set<//t> of <tt>Lot</tt>s neighboring <tt>lot</tt>
	 */
	public Set<Lot> getNeighbors(Lot lot){
		int x = lot.getX();
		int y = lot.getY();
		
		// get north, south, east, west points...
		Set<Lot> rSet = new HashSet<Lot>();

		// we can get non-existent lots, they just return as null
		
		Lot n; // neighbor
		n = this.getLot(x, y + 1);
		if (n != null)
			rSet.add(n);

		n = this.getLot(x, y - 1);
		if (n != null)
			rSet.add(n);

		n = this.getLot(x - 1, y);
		if (n != null)
			rSet.add(n);

		n = this.getLot(x + 1, y);
		if (n != null)
			rSet.add(n);

		return rSet;
	}
	
	/**
	 * Returns all Lots as a Set.
	 * <p>
	 * The returned set is a defensive copy of a set in the manager; changing it has no effect on that set, though changing the Lots will have an effect on those Lots.
	 */
	public Set<Lot> asSet(){
		return new HashSet<Lot>(this.set);
	}
	
	/**
	 * Prints to console basic map of paved/unpaved fields.
	 * <p>
	 * 
	 * O represents unpaved, P represents paved.
	 */
	public void printMap(){
		for (int y = height - 1; y >= 0; y--){
			for (int x = 0; x < width; x++){
				char rep = (lots[x][y].isPaved()) ? 'R' : '.'; 
				System.out.print(rep + " ");
			}
			System.out.println();
		}
	}
	
	public void addEntryPoint(Lot lot){
		if (!lot.isPaved()) throw new IllegalArgumentException(lot.toString() + "is not a paved lot.");
		this.entryPoints.add(lot);
	}
	
	/**
	 * Returns defensive copy of Set for every entry point.
	 * <p>
	 * An entry point is a Lot where Drives may turn on the road network even though it's not an Xing.
	 * <p>
	 * An entry point is not necessarily a crossing of roads.
	 * <p>
	 * Any entry point may also be an Xing.
	 * @return defensive copy of Set for every entry point.
	 * 
	 */
	public Set<Lot> getEntryPoints(){
		return new HashSet<>(entryPoints);
	}

}
