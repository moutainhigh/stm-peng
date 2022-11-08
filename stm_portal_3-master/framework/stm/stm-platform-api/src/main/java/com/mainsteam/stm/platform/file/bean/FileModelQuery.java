package com.mainsteam.stm.platform.file.bean;


public class FileModelQuery {

	//ID
	private long id;
	//文件的业务分组
	private String fileGroup;
	//文件的真实名称
	private String fileName;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}
	
	
	
	
}
