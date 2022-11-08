package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;


/**
 * <li>文件名称: FileMetricDataBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月17日
 * @author   pengl
 */
public class FileMetricDataBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9011094029174851072L;

	/**
	 * 
	 */
	private final static String NULL_DATAGRID_VALUE = "--";
	
	//资源实例ID
	private long resourceId;
	
	//文件路径
	private String filePath = NULL_DATAGRID_VALUE;
	
	//文件名称
	private String fileName = NULL_DATAGRID_VALUE;
	
	//文件大小
	private String fileSize = NULL_DATAGRID_VALUE;
	
	private String fileSizeValue = "-1";
	
	//文件修改时间
	private String fileModifyTime = NULL_DATAGRID_VALUE;
	
	//文件可用性
	private String fileAvail = NULL_DATAGRID_VALUE;
	
	//MD5
	private String fileMdCollect = NULL_DATAGRID_VALUE;
	
	public String getFileMdCollect() {
		return fileMdCollect;
	}

	public void setFileMdCollect(String fileMdCollect) {
		this.fileMdCollect = fileMdCollect;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileSizeValue() {
		return fileSizeValue;
	}

	public void setFileSizeValue(String fileSizeValue) {
		this.fileSizeValue = fileSizeValue;
	}

	public String getFileModifyTime() {
		return fileModifyTime;
	}

	public void setFileModifyTime(String fileModifyTime) {
		this.fileModifyTime = fileModifyTime;
	}

	public String getFileAvail() {
		return fileAvail;
	}

	public void setFileAvail(String fileAvail) {
		this.fileAvail = fileAvail;
	}

	
	
	
}
