package com.mainsteam.stm.system.itsm.bo;

import java.io.Serializable;

public class ItsmWebServiceBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8880720034366181122L;

	private String CODE;
	
	private String URL;

	public String getCODE() {
		return CODE;
	}

	public void setCODE(String CODE) {
		this.CODE = CODE;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}
}
