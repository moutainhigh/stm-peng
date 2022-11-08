package com.mainsteam.stm.profilelib.fault.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: Profilefault</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 故障策略</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月23日 下午5:06:10
 * @author   zhangjunfeng
 */
public class Profilefault implements Serializable {
	private static final long serialVersionUID = -748823238577249456L;
	
	/**
	 * 策略状态启用
	 */
	public static final int PROFILE_IS_USE_STATE_ENABLE = 1;
	/**
	 * 策略状态禁用
	 */
	public static final int PROFILE_IS_USE_STATE_DISABLE = 0;
	
	/**
	 * 策略ID
	 */
	private long profileId;
	/**
	 * 策略名称
	 */
	private String profileName;
	/**
	 * 策略说明
	 */
	private String profileDesc;
	/**
	 * 是否启用策略
	 */
	private int isUse;
	
	/**
	 * 资源类型ID
	 */
	private String resourceId;
	
	/**
	 * 上级资源类型
	 */
	private String parentResourceId;
	
	/**
	 * 域ID
	 */
	private String domainId;
	
	/**
	 * 创建者ID
	 */
	private String createUser;
	/**
	 * 最后更新用户ID
	 */
	private String updateUser;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 最后更新时间
	 */
	private Date updateTime;
	/**
	 * 告警级别，多个级别使用“,”分隔
	 */
	private String alarmLevel;
	/**
	 * 快照脚本ID
	 */
	private String snapshotScriptId;
	/**
	 * 恢复脚本ID
	 */
	private String recoveryScriptId;
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileDesc() {
		return profileDesc;
	}
	public void setProfileDesc(String profileDesc) {
		this.profileDesc = profileDesc;
	}
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getParentResourceId() {
		return parentResourceId;
	}
	public void setParentResourceId(String parentResourceId) {
		this.parentResourceId = parentResourceId;
	}
	public String getSnapshotScriptId() {
		return snapshotScriptId;
	}
	public void setSnapshotScriptId(String snapshotScriptId) {
		this.snapshotScriptId = snapshotScriptId;
	}
	public String getRecoveryScriptId() {
		return recoveryScriptId;
	}
	public void setRecoveryScriptId(String recoveryScriptId) {
		this.recoveryScriptId = recoveryScriptId;
	}
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
		
}
