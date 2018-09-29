package com.underplex.tranopolis;

/**
 * Represents one tile or square of space in a city.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Lot {

	public static final double LENGTH_IN_M = 100.0; // length of a lot in meters
	private final int x;
	private final int y;
	private int value;

	public Lot(int x, int y) {
		this.value = 0;
		this.x = x;
		this.y = y;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public void makePaved(){
		value = 1;
	}

	public void makeBuilt(){
		value = 2;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public String coordinates(){
		return x + ", " + y;
	}
	
	/**
	 * Returns <tt>true</tt> iff this is paved.
	 * @return
	 */
	public boolean isPaved(){
		return (value == 1);
	}

	/**
	 * Returns <tt>true</tt> iff this is built.
	 * @return
	 */
	public boolean isBuilt(){
		return (value == 2);
	}
	public String toString(){
		return "Lot at (" + this.coordinates() + ")";
	}	
	
}
