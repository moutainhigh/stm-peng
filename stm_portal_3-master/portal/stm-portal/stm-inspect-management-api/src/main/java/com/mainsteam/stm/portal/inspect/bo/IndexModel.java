package com.mainsteam.stm.portal.inspect.bo;

public class IndexModel {

	private String id;
	private String name;
	private String unit;
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof IndexModel) {
			IndexModel in = (IndexModel) obj;
			return this.id == in.id || this.id.equals(in.id);
		}
		return false;
	}
}
