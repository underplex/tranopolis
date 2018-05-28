package com.underplex.tranopolis;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Point {

	private final int x;
	private final int y;
	private static final List<Point> master;
	
	static {
		master = new ArrayList<Point>();
	}
	
	private Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public static Point makePoint(int x, int y){
		Point p = new Point(x,y);
		if (master.contains(p)){
			int findP = master.indexOf(p);
			p = master.get(findP);
		} else {
			master.add(p);
		}
		
		return p;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
