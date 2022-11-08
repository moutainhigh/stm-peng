package com.mainsteam.stm.portal.inspect.bo;

public class Option {

	private String id;
	private String name;

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

	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Option) {
			Option op = (Option) obj;
			return this.id == op.id || this.id.equals(op.id);
		}
		return false;
	}
}
