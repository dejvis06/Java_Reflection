package com.example.section7.lecture27;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;

@InitializerClass
public class ServiceRegistry {

	@InitializerMethod
	public void registerService() {
		System.err.println("Service successfully registered.");
	}
}
