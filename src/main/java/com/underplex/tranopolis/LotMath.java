package com.underplex.tranopolis;

/**
 * Utility class for doing math involving <tt>Lot</tt> locations.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class LotMath {

	private LotMath(){
		// don't instantiate
	}
	
	public static double distance(Lot lot1, Lot lot2){
		return (Math.hypot(lot1.getX() - lot2.getX(), lot1.getY() - lot2.getY()));
	}
}
