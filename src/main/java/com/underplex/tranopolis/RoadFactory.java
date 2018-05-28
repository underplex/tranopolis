package com.underplex.tranopolis;

import org.jgrapht.EdgeFactory;

public class RoadFactory implements EdgeFactory<Xing, Road> {

	@Override
	public Road createEdge(Xing xingBegin, Xing xingEnd) {
		return new Road(xingBegin, xingEnd);
	}
	
}
