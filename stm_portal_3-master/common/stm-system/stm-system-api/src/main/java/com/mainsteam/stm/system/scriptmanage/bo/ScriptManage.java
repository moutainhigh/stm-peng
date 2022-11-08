package com.mainsteam.stm.system.scriptmanage.bo;

import java.io.Serializable;
import java.util.Date;

public class ScriptManage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8440531871189833445L;
	
	private static String fileSizeUnit = "Byte"; 
	
	private Long scriptId; 
	
	private String docName;
	
	private String discription;
	
	private Long fileSizeNum;
	
	private Date updateTime;
	
	private ScriptManageTypeEnum scriptManageType;
	
	private int scriptTypeCode;
	
	private Long userId;
	
	private Long fileId; 
	
	public static String getFileSizeUnit() {
		return fileSizeUnit;
	}

	public static void setFileSizeUnit(String fileSizeUnit) {
		ScriptManage.fileSizeUnit = fileSizeUnit;
	}

	public Long getScriptId() {
		return scriptId;
	}

	public void setScriptId(Long scriptId) {
		this.scriptId = scriptId;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public Long getFileSizeNum() {
		return fileSizeNum;
	}

	public void setFileSizeNum(Long fileSizeNum) {
		this.fileSizeNum = fileSizeNum;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public ScriptManageTypeEnum getScriptManageType() {
		return scriptManageType;
	}

	public void setScriptManageType(ScriptManageTypeEnum scriptManageType) {
		this.scriptManageType = scriptManageType;
		this.scriptTypeCode = scriptManageType.getScriptCode();
	}

	public int getScriptTypeCode() {
		return scriptTypeCode;
	}

	public void setScriptTypeCode(int scriptTypeCode) {
		this.scriptTypeCode = scriptTypeCode;
		this.scriptManageType = ScriptManageTypeEnum.getByScriptCode(scriptTypeCode);
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

}
