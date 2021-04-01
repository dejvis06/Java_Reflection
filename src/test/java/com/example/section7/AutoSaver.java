package com.example.section7;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;

@InitializerClass
public class AutoSaver {

	@InitializerMethod
	public void startAutoSavingThreads() {
		System.err.println("Start automatic data saving to disk.");
	}
}
