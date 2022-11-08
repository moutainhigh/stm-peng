package com.mainsteam.stm.alarm.obj;

import java.util.Date;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;


public class AlarmEvent {
	/**告警ID */
	private long eventID;
	/**告警内容 */
	private String content;
	/**告警级别 */
	private InstanceStateEnum level;
	/**来源IP*/
	private String sourceIP;
	/**来源ID*/
	private String sourceID;
	/**来源名称 */
	private String sourceName;
	/**告警提供者：OC4、otherSys */
	@Deprecated
	private AlarmProviderEnum provider;
	/**告警模块 */
	private SysModuleEnum sysID;
	/**是否为恢复告警 */
	@Deprecated
	private boolean isRecoveryEvent;
	/**是否已恢复 */
	private boolean recovered;
	/**恢复时间 */
	@Deprecated
	private Date recoveryTime;
	/**恢复告警ID */
	@Deprecated
	private long recoveryEventID;
	/**itsm订单状态 */
	private ItsmAlarmData itsmData;
	/**告警产生时间 */
	private Date collectionTime;
	/**用于恢复（未恢复告警）的KEY */
	private String recoverKey;
	/**记录操作类型 */
	private HandleType handleType;
	/**扩展字段 */
	private String ext0;
	private String ext1;
	private String ext2;
	private String ext3;
	private String ext4;
	private String ext5;
	private String ext6;
	private String ext7;
	private String ext8;
	private String ext9;
	
	public String getExt0() {
		return ext0;
	}
	public void setExt0(String ext0) {
		this.ext0 = ext0;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getExt4() {
		return ext4;
	}
	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
	public String getExt5() {
		return ext5;
	}
	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}
	public String getExt6() {
		return ext6;
	}
	public void setExt6(String ext6) {
		this.ext6 = ext6;
	}
	public String getExt7() {
		return ext7;
	}
	public void setExt7(String ext7) {
		this.ext7 = ext7;
	}
	public String getExt8() {
		return ext8;
	}
	public void setExt8(String ext8) {
		this.ext8 = ext8;
	}
	public String getExt9() {
		return ext9;
	}
	public void setExt9(String ext9) {
		this.ext9 = ext9;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public InstanceStateEnum getLevel() {
		return level;
	}
	public void setLevel(InstanceStateEnum level) {
		this.level = level;
	}
	public String getSourceIP() {
		return sourceIP;
	}
	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public SysModuleEnum getSysID() {
		return sysID;
	}
	public void setSysID(SysModuleEnum sysID) {
		this.sysID = sysID;
	}
	public boolean isRecoveryEvent() {
		return isRecoveryEvent;
	}
	public void setRecoveryEvent(boolean isRecoveryEvent) {
		this.isRecoveryEvent = isRecoveryEvent;
	}
	public Date getRecoveryTime() {
		return recoveryTime;
	}
	public void setRecoveryTime(Date recoveryTime) {
		this.recoveryTime = recoveryTime;
	}
	public Date getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}
	public String getRecoverKey() {
		return recoverKey;
	}
	public void setRecoverKey(String recoverKey) {
		this.recoverKey = recoverKey;
	}
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public AlarmProviderEnum getProvider() {
		return provider;
	}
	public void setProvider(AlarmProviderEnum provider) {
		this.provider = provider;
	}

	public boolean isRecovered() {
		return recovered;
	}

	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}

	public long getRecoveryEventID() {
		return recoveryEventID;
	}
	public void setRecoveryEventID(long recoveryEventID) {
		this.recoveryEventID = recoveryEventID;
	}
	public ItsmAlarmData getItsmData() {
		return itsmData;
	}
	public void setItsmData(ItsmAlarmData itsmData) {
		this.itsmData = itsmData;
	}
	public HandleType getHandleType() {
		return handleType;
	}
	public void setHandleType(HandleType handleType) {
		this.handleType = handleType;
	}

	@Override
	public String toString() {
		return "AlarmEvent{" +
				"eventID=" + eventID +
				", content='" + content + '\'' +
				", level=" + level +
				", recovered=" + recovered +
				", collectionTime=" + collectionTime +
				", recoverKey='" + recoverKey + '\'' +
				'}';
	}
}
