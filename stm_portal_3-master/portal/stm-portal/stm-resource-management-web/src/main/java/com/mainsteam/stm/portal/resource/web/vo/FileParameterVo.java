package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class FileParameterVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911393371479856954L;
	
	private String fileList;
	
	private long mainInstanceId;
	
	private String filePath;
	
	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	public long getMainInstanceId() {
		return mainInstanceId;
	}

	public void setMainInstanceId(long mainInstanceId) {
		this.mainInstanceId = mainInstanceId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
