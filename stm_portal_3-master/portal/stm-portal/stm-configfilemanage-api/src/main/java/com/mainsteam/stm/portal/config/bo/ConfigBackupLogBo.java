package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.Date;
/**
 * <li>文件名称: ConfigBackupLog.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public class ConfigBackupLogBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 资源ID
	 */
	private Long id;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 备份时间
	 */
	private Date backupTime;
	/**
	 * 文件大小
	 */
	private String fileSize;
	/**
	 * 备份方式
	 */
	private Integer backupType;
	/**
	 * 备份状态
	 */
	private Integer backupState;
	/**
	 * 配置变更状态
	 */
	private Integer changeState;
	/**
	 * 文件ID
	 */
	private Long fileId;
	/**
	 * 是否查询备份失败的log
	 */
	private boolean all;
	
	private String remoteInfo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getBackupTime() {
		return backupTime;
	}
	public void setBackupTime(Date backupTime) {
		this.backupTime = backupTime;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public Integer getBackupType() {
		return backupType;
	}
	public void setBackupType(Integer backupType) {
		this.backupType = backupType;
	}
	public Integer getBackupState() {
		return backupState;
	}
	public void setBackupState(Integer backupState) {
		this.backupState = backupState;
	}
	public Integer getChangeState() {
		return changeState;
	}
	public void setChangeState(Integer changeState) {
		this.changeState = changeState;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public String getRemoteInfo() {
		return remoteInfo;
	}
	public void setRemoteInfo(String remoteInfo) {
		this.remoteInfo = remoteInfo;
	}
}
