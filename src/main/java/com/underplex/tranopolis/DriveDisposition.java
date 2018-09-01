package com.underplex.tranopolis;

public enum DriveDisposition {
	
	WAITING, // not yet FINISHED or DROPPED
	FINISHED, // completed 
	DROPPED, // never begun, and never will be begun
	BEGUN, // begun but not completed
	;

}
