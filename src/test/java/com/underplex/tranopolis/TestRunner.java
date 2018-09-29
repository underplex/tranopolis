package com.underplex.tranopolis;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(TestSuite.class);

		boolean hasFailed = false;
		if (result.getFailureCount() > 0) {
			
			for (Failure failure : result.getFailures()) {
				System.out.println("Failure was " + failure.getTrace());

			}
			System.out.println("Failed " + result.getFailures().size() + " test(s).");
		
		} else {
			System.out.println("Passed all tests.");
		}

	}
}