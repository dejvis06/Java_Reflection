package com.example.section7;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;

@InitializerClass
public class DatabaseConnection {

	@InitializerMethod
	public void connectToDatabase1() {
		System.err.println("Connecting to database 1.");
	}

	@InitializerMethod
	public void connectToDatabase2() {
		System.err.println("Connecting to database 2.");
	}

}
