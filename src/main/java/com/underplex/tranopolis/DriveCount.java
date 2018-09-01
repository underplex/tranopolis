package com.underplex.tranopolis;

import com.opencsv.bean.CsvBindByName;

/**
 * Bean for use with <tt>opencsv</tt> library.
 * 
 * @author Brandon Irvine, brandon@underplex.com
 *
 */
public class DriveCount extends Stat{
	
	@CsvBindByName
	private int finishedDrives;

	public int getFinishedDrives() {
		return finishedDrives;
	}

	public void setFinishedDrives(int finishedDrives) {
		this.finishedDrives = finishedDrives;
	}

}
