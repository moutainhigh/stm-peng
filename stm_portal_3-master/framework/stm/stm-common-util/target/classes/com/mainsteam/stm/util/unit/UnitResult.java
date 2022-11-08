package com.mainsteam.stm.util.unit;

public class UnitResult {
	private String unit;
	private String value;
	
	public UnitResult( String value,String unit) {
		this.unit = unit;
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
