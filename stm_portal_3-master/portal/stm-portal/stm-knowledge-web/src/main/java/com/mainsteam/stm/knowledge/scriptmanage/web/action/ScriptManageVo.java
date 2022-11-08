package com.mainsteam.stm.knowledge.scriptmanage.web.action;

import java.util.Date;

import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;


public class ScriptManageVo {
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
	
	private String userName;
	
	private String fileContent;
	

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public static String getFileSizeUnit() {
		return fileSizeUnit;
	}

	public static void setFileSizeUnit(String fileSizeUnit) {
		ScriptManageVo.fileSizeUnit = fileSizeUnit;
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

	public ScriptManageTypeEnum getScriptManageType() {
		return scriptManageType;
	}

	public void setScriptManageType(ScriptManageTypeEnum scriptManageType) {
		this.scriptManageType = scriptManageType;
	}

	public int getScriptTypeCode() {
		return scriptTypeCode;
	}

	public void setScriptTypeCode(int scriptTypeCode) {
		this.scriptTypeCode = scriptTypeCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
