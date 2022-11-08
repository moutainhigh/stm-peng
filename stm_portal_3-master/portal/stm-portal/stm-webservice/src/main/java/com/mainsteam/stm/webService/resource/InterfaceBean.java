package com.mainsteam.stm.webService.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class InterfaceBean {

	// 接口ID
	@XmlElement(required = true)
	private long id;

	// 接口名称
	@XmlElement(required = true)
	private String name;

	// 接口索引
	@XmlElement(required = true)
	private int index;

	// 接口速率
	@XmlElement(required = true)
	private Long ifSpeed;

	// 是否可用
	@XmlElement(required = true)
	private boolean available;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Long getIfSpeed() {
		return ifSpeed;
	}

	public void setIfSpeed(Long ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

}
