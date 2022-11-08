package com.mainsteam.stm.portal.config.util.jaxb;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Script")
public class Script implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -592413966293140491L;

	@XStreamAsAttribute
	private String type;
	
	@XStreamAsAttribute
	private String cmd;
	
	@XStreamAsAttribute
	private String fileName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
