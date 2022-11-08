package com.mainsteam.stm.knowledge.snapshotfile.web.action;

import java.util.Date;

public class SnapshotFileVo {
	private long id;
	private String ipAdress;
	//文件的业务分组
	private String fileGroup;
	//文件的真实名称
	private String fileName;
	//文件的大小
	private long fileSize;
	//文件的大小单位
	private String fileUnit;
	//文件的创建日期
	private Date createDatetime;
	
	private String fileContent;
	
	
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getIpAdress() {
		return ipAdress;
	}
	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}
	public String getFileUnit() {
		return fileUnit;
	}
	public void setFileUnit(String fileUnit) {
		this.fileUnit = fileUnit;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFileGroup() {
		return fileGroup;
	}
	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public Date getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
}
