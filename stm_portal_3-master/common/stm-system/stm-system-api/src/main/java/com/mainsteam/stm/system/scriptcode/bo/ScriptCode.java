package com.mainsteam.stm.system.scriptcode.bo;

import java.io.Serializable;

public class ScriptCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4845471867568662588L;

	private Long codeId;
	
	private String scriptCode;
	
	private String codeDiscription;

	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getCodeDiscription() {
		return codeDiscription;
	}

	public void setCodeDiscription(String codeDiscription) {
		this.codeDiscription = codeDiscription;
	}
	
}
