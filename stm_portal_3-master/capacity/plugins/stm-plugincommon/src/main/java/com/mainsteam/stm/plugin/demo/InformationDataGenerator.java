package com.mainsteam.stm.plugin.demo;

public class InformationDataGenerator implements DemoDataGenerator {
	
	public final String data;
	
	public InformationDataGenerator(String data) {
		this.data = data;
	}
	
	@Override
	public String getDemoData() {
		return data;
	}

}
