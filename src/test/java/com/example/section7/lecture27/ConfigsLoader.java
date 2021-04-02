package com.example.section7.lecture27;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;

@InitializerClass
public class ConfigsLoader {

	@InitializerMethod
	public void loadAllConfigs() {
		System.err.println("Loading all configs.");
	}
}
