package com.underplex.tranopolis;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class TestCity {

	@Test
	public void createCity() {
		System.out.println("**********************");
		System.out.println("***** createCity   ****");
		System.out.println("**********************");
	
		// see if you can make a city
		
		int w = 3;
		int h = 3;
		
		City city = new City(w,h);
		
		assertEquals(w, city.getWidth());
		assertEquals(h, city.getHeight());
		
		assertEquals(0, city.getResidentManager().getResidents().size());
		assertEquals(w * h, city.getLotManager().asSet().size());
		
		assertEquals(LocalDateTime.of(1900, 1, 1, 0, 0),
				City.DEFAULT_START);
		
		assertEquals(0,city.getLocationManager().getLocations().size());
	}

}
