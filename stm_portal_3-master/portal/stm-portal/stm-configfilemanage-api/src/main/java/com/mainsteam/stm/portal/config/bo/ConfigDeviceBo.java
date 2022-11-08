package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.Date;
/**
 * <li>文件名称: ConfigDeviceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public class ConfigDeviceBo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2250221237382359220L;
	/**
	 * 资源ID
	 */
	private Long id;
	/**
	 * IP地址
	 */
	private String ipAddress;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * Enable用户名
	 */
	private String enableUserName;
	/**
	 * Enable密码
	 */
	private String enablePassword;
	/**
	 * 登录方式：0：Telnet；1：SSH
	 */
	private String loginType;
	/**
	 * 共同体名
	 */
	private String publicName;
	/**
	 * SNMP端口
	 */
	private String snmpPort;
	/**
	 * 备份计划
	 */
	private Long backupId;
	/**
	 * 设备名称
	 */
	private String intanceName;
	/**
	 * 配置变更状态
	 */
	private String changeState;
	/**
	 * 软件版本
	 */
	private String softVersion;
	/**
	 * 备份状态
	 */
	private String backupState;
	/**
	 * 最后一次备份时间
	 */
	private Date lastBackupTime;
	/**
	 * OS类型
	 */
	private String osType;
	/**
	 * 厂商
	 */
	private String vendorName;
	/**
	 * 资源类型
	 */
	private String resourceTypeId;
	/**
	 * 设备类型
	 */
	private String deviceType;
	
	/**
	 * 当前用户是否可以操作连接信息
	 */
	private boolean handle;
	
	/**
	 * 是否保存连接信息0：否；1：是
	 */
	private Integer isSave;
	
	private Long groupId;
	
	
	public boolean isHandle() {
		return handle;
	}
	public void setHandle(boolean handle) {
		this.handle = handle;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * 设备描述
	 */
	private String deviceDesc;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEnableUserName() {
		return enableUserName;
	}
	public void setEnableUserName(String enableUserName) {
		this.enableUserName = enableUserName;
	}
	public String getEnablePassword() {
		return enablePassword;
	}
	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getPublicName() {
		return publicName;
	}
	public void setPublicName(String publicName) {
		this.publicName = publicName;
	}
	public String getSnmpPort() {
		return snmpPort;
	}
	public void setSnmpPort(String snmpPort) {
		this.snmpPort = snmpPort;
	}
	public Long getBackupId() {
		return backupId;
	}
	public void setBackupId(Long backupId) {
		this.backupId = backupId;
	}
	public String getResourceTypeId() {
		return resourceTypeId;
	}
	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}
	public String getIntanceName() {
		return intanceName;
	}
	public void setIntanceName(String intanceName) {
		this.intanceName = intanceName;
	}
	public String getChangeState() {
		return changeState;
	}
	public void setChangeState(String changeState) {
		this.changeState = changeState;
	}
	public String getSoftVersion() {
		return softVersion;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public String getBackupState() {
		return backupState;
	}
	public void setBackupState(String backupState) {
		this.backupState = backupState;
	}
	public Date getLastBackupTime() {
		return lastBackupTime;
	}
	public void setLastBackupTime(Date lastBackupTime) {
		this.lastBackupTime = lastBackupTime;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getDeviceDesc() {
		return deviceDesc;
	}
	public void setDeviceDesc(String deviceDesc) {
		this.deviceDesc = deviceDesc;
	}
	public Integer getIsSave() {
		return isSave;
	}
	public void setIsSave(Integer isSave) {
		this.isSave = isSave;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
}
