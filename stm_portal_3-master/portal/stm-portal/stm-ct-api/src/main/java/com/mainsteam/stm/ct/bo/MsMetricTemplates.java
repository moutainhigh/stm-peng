package com.mainsteam.stm.ct.bo;

public class MsMetricTemplates {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String metric_name;

    private Integer enable;

    private Integer attention_value;

    private Integer alarm_value;

    private String profilelib_id;

    private String compare_type;

    private String alarm_message;
    
    private String c_name;
    
	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMetric_name() {
		return metric_name;
	}

	public void setMetric_name(String metric_name) {
		this.metric_name = metric_name;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getAttention_value() {
		return attention_value;
	}

	public void setAttention_value(Integer attention_value) {
		this.attention_value = attention_value;
	}

	public Integer getAlarm_value() {
		return alarm_value;
	}

	public void setAlarm_value(Integer alarm_value) {
		this.alarm_value = alarm_value;
	}

	public String getProfilelib_id() {
		return profilelib_id;
	}

	public void setProfilelib_id(String profilelib_id) {
		this.profilelib_id = profilelib_id;
	}

	public String getCompare_type() {
		return compare_type;
	}

	public void setCompare_type(String compare_type) {
		this.compare_type = compare_type;
	}

	public String getAlarm_message() {
		return alarm_message;
	}

	public void setAlarm_message(String alarm_message) {
		this.alarm_message = alarm_message;
	}

	
}
