package com.example.section7.lecture27;

import java.io.IOException;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;
import com.example.section7.annotations.RetryOperation;

@InitializerClass
public class DatabaseConnection {

	private static final String CONNECTING_TO_DATABASE_2 = "Connecting to database 2.";
	private static final String CONNECTING_TO_DATABASE_1 = "Connecting to database 1.";
	private static final String CONNECTION_FAILED = "Connection failed!";

	private int failCounter = 5;

	@RetryOperation(numberOfRetries = 10, retryExceptions = IOException.class, durationBetweenRetriesMs = 1000, failureMessage = CONNECTION_FAILED)
	@InitializerMethod
	public void connectToDatabase1() throws IOException {
		System.err.println(CONNECTING_TO_DATABASE_1);

		if (failCounter > 0) {
			failCounter--;
			throw new IOException(CONNECTION_FAILED);
		}
	}

	@InitializerMethod
	public void connectToDatabase2() {
		System.err.println(CONNECTING_TO_DATABASE_2);
	}

}
