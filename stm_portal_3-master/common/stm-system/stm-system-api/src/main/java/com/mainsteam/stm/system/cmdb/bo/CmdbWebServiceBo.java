package com.mainsteam.stm.system.cmdb.bo;

import java.io.Serializable;

public class CmdbWebServiceBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8880720034366181122L;

	private String CODE;
	
	private String URL;

	private boolean open;
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

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
