package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * <li>文件名称: ConfigWarnViewBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月7日
 * @author   caoyong
 */
public class ConfigWarnViewBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 告警记录ID
	 */
	private Long id;
	/**
	 * 告警资源ID
	 */
	private Long sourceId;
	/**
	 * 告警内容
	 */
	private String content;
	/**
	 * 告警时间
	 */
	private Date warnTime;
	/**
	 * 告警设备名称
	 */
	private String name;
	/**
	 * 告警设备IP
	 */
	private String ipAddress;
	/**
	 * 告警关联的规则（接收人和接收方式）
	 */
	private List<ConfigWarnRuleBo> configWarnRuleBos;
	/**
	 * 两个文件比较的上一个文件ID
	 */
	private Long lastFileId;
	/**
	 * 两个文件比较的下一个文件ID
	 */
	private Long currFileId;
	/**
	 * 两个文件比较的上一个文件名称
	 */
	private String lastFileName;
	/**
	 * 两个文件比较的下一个文件名称
	 */
	private String currFileName;
	/**
	 * 两个文件比较的下一个文件名称
	 */
	private String userNames;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getWarnTime() {
		return warnTime;
	}
	public void setWarnTime(Date warnTime) {
		this.warnTime = warnTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public List<ConfigWarnRuleBo> getConfigWarnRuleBos() {
		return configWarnRuleBos;
	}
	public void setConfigWarnRuleBos(List<ConfigWarnRuleBo> configWarnRuleBos) {
		this.configWarnRuleBos = configWarnRuleBos;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getLastFileId() {
		return lastFileId;
	}
	public void setLastFileId(Long lastFileId) {
		this.lastFileId = lastFileId;
	}
	public Long getCurrFileId() {
		return currFileId;
	}
	public void setCurrFileId(Long currFileId) {
		this.currFileId = currFileId;
	}
	public String getLastFileName() {
		return lastFileName;
	}
	public void setLastFileName(String lastFileName) {
		this.lastFileName = lastFileName;
	}
	public String getCurrFileName() {
		return currFileName;
	}
	public void setCurrFileName(String currFileName) {
		this.currFileName = currFileName;
	}
	public String getUserNames() {
		return userNames;
	}
	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}
}
