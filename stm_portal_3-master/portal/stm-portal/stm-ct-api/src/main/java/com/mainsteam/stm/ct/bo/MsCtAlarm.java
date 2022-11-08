package com.mainsteam.stm.ct.bo;

import java.util.Date;

public class MsCtAlarm {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String message;

    private String resource_id;

    private String result_id;

    private Integer alarm_level;

    private Integer confirmed;

    private String alarm_time;

    private Date create_time;
    
    private String resourceName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getResult_id() {
		return result_id;
	}

	public void setResult_id(String result_id) {
		this.result_id = result_id;
	}

	public Integer getAlarm_level() {
		return alarm_level;
	}

	public void setAlarm_level(Integer alarm_level) {
		this.alarm_level = alarm_level;
	}

	public Integer getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Integer confirmed) {
		this.confirmed = confirmed;
	}

	public String getAlarm_time() {
		return alarm_time;
	}

	public void setAlarm_time(String alarm_time) {
		this.alarm_time = alarm_time;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	
    
    
}
