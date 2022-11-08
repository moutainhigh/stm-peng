package com.mainsteam.stm.portal.netflow.bo;

public class ResourceInterfaceBo {

	private long id;
	private String name;
	private int index = -99;
	private Boolean checked = false;
	private Long ifSpeed;
	private boolean available = false;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Long getIfSpeed() {
		return ifSpeed;
	}

	public void setIfSpeed(Long ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

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

	public String getInfo() {
		return this.id + "|" + this.name + "|" + this.index;
	}
}
