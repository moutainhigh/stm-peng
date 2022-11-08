package com.mainsteam.stm.alarm.obj;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;

public class AlarmSenderParamter {
	/**策略ID，只用于资源监控*/
	private long profileID;
	private AlarmRuleProfileEnum ruleType;
	private AlarmProviderEnum provider;
	/**告警产生模块*/
	private SysModuleEnum sysID;
	/**告警来源ID*/
	private String sourceID; 
	/**告警来源*/
	private String sourceName;
	/**告警级别*/
	private InstanceStateEnum level;
	private String sourceIP;
	/**默认告警标题，用于Email*/
	private String defaultMsgTitle;
	/**默认告警消息*/
	private String defaultMsg;
	/**产生时间*/
	private Date generateTime;
	private List<AlarmSenderProp> prop;
	private String[] recoverKeyValue;
	/**扩展字段*/
	private String ext0;
	/**扩展字段*/
	private String ext1;
	/**扩展字段*/
	private String ext2;
	/**扩展字段*/
	private String ext3;
	private String ext4;
	private String ext5;
	private String ext6;
	private String ext7;
	private String ext8;
	private String ext9;
	/**告警通知规则*/
	private List<AlarmRule> notifyRules;
	
	
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
	public long getProfileID() {
		return profileID;
	}
	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}
	public String getDefaultMsg() {
		return defaultMsg;
	}
	public void setDefaultMsg(String defaultMsg) {
		this.defaultMsg = defaultMsg;
	}
	public List<AlarmSenderProp> getProp() {
		return prop;
	}
	public void setProp(List<AlarmSenderProp> prop) {
		this.prop = prop;
	}
	public AlarmProviderEnum getProvider() {
		return provider;
	}
	public void setProvider(AlarmProviderEnum provider) {
		this.provider = provider;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public String getSourceIP() {
		return sourceIP;
	}
	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public InstanceStateEnum getLevel() {
		return level;
	}
	public void setLevel(InstanceStateEnum level) {
		this.level = level;
	}
	public Date getGenerateTime() {
		return generateTime;
	}
	public void setGenerateTime(Date generateTime) {
		this.generateTime = generateTime;
	}
	public SysModuleEnum getSysID() {
		return sysID;
	}
	public void setSysID(SysModuleEnum sysID) {
		this.sysID = sysID;
	}
	public String[] getRecoverKeyValue() {
		return recoverKeyValue;
	}
	public void setRecoverKeyValue(String[] recoverKeyValue) {
		this.recoverKeyValue = recoverKeyValue;
	}
	public AlarmRuleProfileEnum getRuleType() {
		return ruleType;
	}
	public void setRuleType(AlarmRuleProfileEnum ruleType) {
		this.ruleType = ruleType;
	}
	public String getDefaultMsgTitle() {
		return defaultMsgTitle;
	}
	public void setDefaultMsgTitle(String defaultMsgTitle) {
		this.defaultMsgTitle = defaultMsgTitle;
	}
	public List<AlarmRule> getNotifyRules() {
		return notifyRules;
	}
	public void setNotifyRules(List<AlarmRule> notifyRules) {
		this.notifyRules = notifyRules;
	}

	@Override
	public String toString() {
		return "AlarmSenderParamter{" +
				"profileID=" + profileID +
				", ruleType=" + ruleType +
				", provider=" + provider +
				", sysID=" + sysID +
				", sourceID='" + sourceID + '\'' +
				", sourceName='" + sourceName + '\'' +
				", level=" + level +
				", sourceIP='" + sourceIP + '\'' +
				", defaultMsgTitle='" + defaultMsgTitle + '\'' +
				", defaultMsg='" + defaultMsg + '\'' +
				", generateTime=" + generateTime +
				", prop=" + prop +
				", recoverKeyValue=" + Arrays.toString(recoverKeyValue) +
				", ext0='" + ext0 + '\'' +
				", ext1='" + ext1 + '\'' +
				", ext2='" + ext2 + '\'' +
				", ext3='" + ext3 + '\'' +
				", ext4='" + ext4 + '\'' +
				", ext5='" + ext5 + '\'' +
				", ext6='" + ext6 + '\'' +
				", ext7='" + ext7 + '\'' +
				", ext8='" + ext8 + '\'' +
				", ext9='" + ext9 + '\'' +
				", notifyRules=" + notifyRules +
				'}';
	}
}
