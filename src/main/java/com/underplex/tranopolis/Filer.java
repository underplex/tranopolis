package com.underplex.tranopolis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 * Utility class for writing files for tranopolis.
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class Filer {
	
	public static void writeToFiles(Tabulator tabulator, String directory, String identifier){
		Writer finishedDrivesWriter = null;
		Writer locationWriter = null;
		Writer drivableWriter = null;
		Writer sumWriter = null;

		String experimentName = "";
		experimentName = identifier;
		String driveFile = "finished_drives";
		String locationFile = "locations";
		String drivableFile = "drivables";
		String sumFile = "sums";
		
		try {
			finishedDrivesWriter = new FileWriter(directory + "/" + driveFile + experimentName + ".csv");
			locationWriter = new FileWriter(directory + "/" + locationFile + experimentName + ".csv");
			drivableWriter = new FileWriter(directory  + "/" + drivableFile + experimentName + ".csv");
			sumWriter = new FileWriter(directory + "/" + sumFile + experimentName + ".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    StatefulBeanToCsvBuilder<DriveCount> drivesBuilder = new StatefulBeanToCsvBuilder<DriveCount>(finishedDrivesWriter); 
	    StatefulBeanToCsv<DriveCount> drivesToCsv = drivesBuilder.build();

	    StatefulBeanToCsvBuilder<LocationCount> locationsBuilder = new StatefulBeanToCsvBuilder<LocationCount>(locationWriter); 
	    StatefulBeanToCsv<LocationCount> locationsToCsv = locationsBuilder.build();

	    StatefulBeanToCsvBuilder<DrivableCount> drivableBuilder = new StatefulBeanToCsvBuilder<DrivableCount>(drivableWriter); 
	    StatefulBeanToCsv<DrivableCount> drivablesToCsv = drivableBuilder.build();
	    
	    StatefulBeanToCsvBuilder<SumCount> sumBuilder = new StatefulBeanToCsvBuilder<SumCount>(sumWriter); 
	    StatefulBeanToCsv<SumCount> sumsToCsv = sumBuilder.build();
	    

	    try {
			drivesToCsv.write(tabulator.getDriveCounts());
		    finishedDrivesWriter.close();

			locationsToCsv.write(tabulator.getLocationCounts());
		    locationWriter.close();

			drivablesToCsv.write(tabulator.getDrivableCounts());
		    drivableWriter.close();

			sumsToCsv.write(tabulator.getSumCounts());
		    sumWriter.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
