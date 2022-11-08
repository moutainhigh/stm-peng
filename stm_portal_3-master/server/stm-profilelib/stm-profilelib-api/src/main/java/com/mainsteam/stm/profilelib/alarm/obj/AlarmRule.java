package com.mainsteam.stm.profilelib.alarm.obj;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 定义告警规则
 * 
 * @author ziw
 * 
 */
public class AlarmRule implements Serializable{

	private static final long serialVersionUID = -5220164805951051019L;
	/**
	 * 告警规则id
	 */
	private long id;

	/**
	 * 告警规则所属的策略id
	 */
	private long profileId;
	
	/**
	 * 告警规则发送的用户  @Deprecated
	 */
	
	private String userId;
	
	/**
	 * 告警规则绑定的策略类型
	 */
	private AlarmRuleProfileEnum profileType;

	/**
	 * 告警的发送条件。每种发送方式，只有一个发送条件
	 */
	private transient List<AlarmSendCondition> sendConditions;

	/**
	 * 
	 */
	public AlarmRule() {
	}

	
	/**
	 * @return the profileType
	 */
	public final AlarmRuleProfileEnum getProfileType() {
		return profileType;
	}



	/**
	 * @param profileType the profileType to set
	 */
	public final void setProfileType(AlarmRuleProfileEnum profileType) {
		this.profileType = profileType;
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
	 * @return the sendConditions
	 */
	public final List<AlarmSendCondition> getSendConditions() {
		return sendConditions;
	}

	/**
	 * @param sendConditions
	 *            the sendConditions to set
	 */
	public final void setSendConditions(
			List<AlarmSendCondition> sendConditions) {
		this.sendConditions = sendConditions;
	}

	@Deprecated
	public String getUserId() {
		return userId;
	}

	@Deprecated
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AlarmRule{" +
				"id=" + id +
				", profileId=" + profileId +
				", userId='" + userId + '\'' +
				", profileType=" + profileType +
				", sendConditions=" + sendConditions +
				'}';
	}
}
