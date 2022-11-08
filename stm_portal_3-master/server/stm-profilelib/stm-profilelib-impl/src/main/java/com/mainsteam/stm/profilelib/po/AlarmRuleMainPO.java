/**
 * 
 */
package com.mainsteam.stm.profilelib.po;

/**
 * 告警规则主表
 * @author ziw
 * 
 */
public class AlarmRuleMainPO {

	/**
	 * 告警规则主键
	 */
	private long id = -1;

	/**
	 * 告警规则接收人
	 */
	private String userId;

	/**
	 * 告警规则名称
	 */
	private String name;

	/**
	 * 告警规则绑定的策略
	 */
	private long profileId = -1;

	/**
	 * 告警规则绑定的策略类型
	 */
	private String profileType;

	/**
	 * 
	 */
	public AlarmRuleMainPO() {
	}

	/**
	 * @return the userId
	 */
	public final String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public final void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the profileId
	 */
	public final long getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId
	 *            the profileId to set
	 */
	public final void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the profileType
	 */
	public final String getProfileType() {
		return profileType;
	}

	/**
	 * @param profileType
	 *            the profileType to set
	 */
	public final void setProfileType(String profileType) {
		this.profileType = profileType;
	}
}
