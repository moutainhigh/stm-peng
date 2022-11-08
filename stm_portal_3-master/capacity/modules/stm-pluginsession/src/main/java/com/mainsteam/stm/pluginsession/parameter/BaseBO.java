package com.mainsteam.stm.pluginsession.parameter;

public abstract class BaseBO implements java.io.Serializable {

	/**
	 * 
	 */
	protected String type;

	private static final long serialVersionUID = 5293533304747591041L;

	public BaseBO(String type) {
		this.type = type;
	}

	public String getType(String type) {
		return this.type;
	}
}
