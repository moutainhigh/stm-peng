package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;

public class ConfigScriptBo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2973872524778629555L;
	
	private long id;
	
	private String name;
	
	private String oid;
	
	private long directoryId;

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

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public long getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(long directoryId) {
		this.directoryId = directoryId;
	}
	
}
