package com.mainsteam.stm.alarm.obj;

import java.util.Map;

@Deprecated
public class AlarmSenderProp {
	private Map<String,String> map;
	private String language;
	
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
