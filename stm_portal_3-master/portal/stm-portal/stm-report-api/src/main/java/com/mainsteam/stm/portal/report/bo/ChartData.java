package com.mainsteam.stm.portal.report.bo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("chartData")
@XStreamConverter(value=ToAttributedValueConverter.class,strings={"value"})
public class ChartData {
	
	//资源实例名称
	@XStreamAsAttribute
	private String name;
	
	//ip地址
	@XStreamAsAttribute
	private String ip;
	
	// 街区分布使用的颜色
	@XStreamAsAttribute
	private String color;
	
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
