package com.mainsteam.stm.plugin.demo;

public class PerformanceDataGenerator implements DemoDataGenerator {
	
	public final double max, min; 
	
	public PerformanceDataGenerator(double max, double min) {
		this.max = max;
		this.min = min;
	}
	
	@Override
	public String getDemoData() {
		return String.valueOf(Math.rint(min + (max - min) * Math.random()));
	}

}
