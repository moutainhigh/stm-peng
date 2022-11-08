package com.mainsteam.stm.portal.config.util.jaxb;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Model")
public class Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5328609658011815651L;

	//与数据库configScript关联
	@XStreamAsAttribute
	private long configScriptId;
	
	@XStreamAsAttribute
	private String name;
	
	@XStreamAsAttribute
	private String oid;
	
	@XStreamImplicit(itemFieldName="Script")
	private List<Script> scripts;

	public List<Script> getScripts() {
		return scripts;
	}

	public void setScripts(List<Script> scripts) {
		this.scripts = scripts;
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

	public long getConfigScriptId() {
		return configScriptId;
	}

	public void setConfigScriptId(long configScriptId) {
		this.configScriptId = configScriptId;
	}
	
}
