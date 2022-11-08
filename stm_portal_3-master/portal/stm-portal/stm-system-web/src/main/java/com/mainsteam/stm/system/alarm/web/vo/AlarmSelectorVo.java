package com.mainsteam.stm.system.alarm.web.vo;

import java.io.Serializable;


public class AlarmSelectorVo implements Serializable{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private Long id;

private String name;

private Boolean alarmDefalut;

private String sysModuleEnum;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public Boolean getAlarmDefalut() {
	return alarmDefalut;
}

public void setAlarmDefalut(Boolean alarmDefalut) {
	this.alarmDefalut = alarmDefalut;
}

public String getSysModuleEnum() {
	return sysModuleEnum;
}

public void setSysModuleEnum(String sysModuleEnum) {
	this.sysModuleEnum = sysModuleEnum;
}


}
