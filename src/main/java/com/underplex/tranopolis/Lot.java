package com.underplex.tranopolis;

/**
 * Represents one tile or square of space in a city.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Lot {

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
	
	public String toString(){
		return "Lot at (" + this.coordinates() + ")";
	}	
	
}
