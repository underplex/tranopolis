package com.underplex.tranopolis;

import com.opencsv.bean.CsvBindByName;

public class SumCount extends Stat{

	@CsvBindByName
	private int population;
	
	@CsvBindByName
	private int drivesAtVertices;
	
	@CsvBindByName
	private int drivesAtEdges;
	
	@CsvBindByName
	private int residentsAtLocations;
	
	@CsvBindByName
	private int error;

	public int getPopulation() {
		return population;
	}

	public int getDrivesAtVertices() {
		return drivesAtVertices;
	}

	public int getDrivesAtEdges() {
		return drivesAtEdges;
	}

	public int getResidentsAtLocations() {
		return residentsAtLocations;
	}

	public int getError() {
		return error;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public void setDrivesAtVertices(int drivesAtVertices) {
		this.drivesAtVertices = drivesAtVertices;
	}

	public void setDrivesAtEdges(int drivesAtEdges) {
		this.drivesAtEdges = drivesAtEdges;
	}

	public void setResidentsAtLocations(int residentsAtLocations) {
		this.residentsAtLocations = residentsAtLocations;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	
}
