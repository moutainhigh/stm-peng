package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;

public class ChartTableTdBo implements Serializable {
	private static final long serialVersionUID = -882726097443587359L;
	private String type = "txt";
	private String text;
	private String metricStatColor = "gray";

	public ChartTableTdBo() {
	}

	public ChartTableTdBo(String text) {
		this.text = text;
	}

	public ChartTableTdBo(String text, String type) {
		this.text = text;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMetricStatColor() {
		return metricStatColor;
	}

	public void setMetricStatColor(String metricStatColor) {
		this.metricStatColor = metricStatColor;
	}

}
