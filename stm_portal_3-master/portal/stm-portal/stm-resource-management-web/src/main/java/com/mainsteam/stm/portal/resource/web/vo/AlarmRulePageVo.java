package com.mainsteam.stm.portal.resource.web.vo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class AlarmRulePageVo implements BasePageVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1046404777120192322L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<AlarmRuleVo> alarmRules;
	//告警规则批量更新发送方式的启用状态时用到
	//设置为启用状态的集合
	private String[] enableMessageRulList;
	private String[] enableEmailRulList;
	private String[] enableAlertRulList;
	//进行过编辑的集合
	private String[] messageRulList;
	private String[] emailRulList;
	private String[] alertRulList;
	
	//初始化告警规则页面时用到
	private long profileId;
	private String profileType;
	//0:默认策略,1:自定义策略,2:个性化策略  ,3:其他模块调用,无权限限制,返回所有用户,4:其他模块调用,也根据域过滤告警接收人
	private int profileNameType = 0;
	private String queryType;
	private Long[] domainIdArr;
	
	
	public int getProfileNameType() {
		return profileNameType;
	}
	public void setProfileNameType(int profileNameType) {
		this.profileNameType = profileNameType;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public Long[] getDomainIdArr() {
		return domainIdArr;
	}
	public void setDomainIdArr(Long[] domainIdArr) {
		this.domainIdArr = domainIdArr;
	}
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String[] getMessageRulList() {
		return messageRulList;
	}
	public void setMessageRulList(String[] messageRulList) {
		this.messageRulList = messageRulList;
	}
	public String[] getEmailRulList() {
		return emailRulList;
	}
	public void setEmailRulList(String[] emailRulList) {
		this.emailRulList = emailRulList;
	}
	public String[] getEnableMessageRulList() {
		return enableMessageRulList;
	}
	public void setEnableMessageRulList(String[] enableMessageRulList) {
		this.enableMessageRulList = enableMessageRulList;
	}
	public String[] getEnableEmailRulList() {
		return enableEmailRulList;
	}
	public void setEnableEmailRulList(String[] enableEmailRulList) {
		this.enableEmailRulList = enableEmailRulList;
	}
	public long getStartRow() {
		return startRow;
	}
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	public long getRowCount() {
		return rowCount;
	}
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<AlarmRuleVo> getAlarmRules() {
		return alarmRules;
	}
	public void setAlarmRules(List<AlarmRuleVo> alarmRules) {
		this.alarmRules = alarmRules;
	}
	public String[] getEnableAlertRulList() {
		return enableAlertRulList;
	}
	public void setEnableAlertRulList(String[] enableAlertRulList) {
		this.enableAlertRulList = enableAlertRulList;
	}
	public String[] getAlertRulList() {
		return alertRulList;
	}
	public void setAlertRulList(String[] alertRulList) {
		this.alertRulList = alertRulList;
	}
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.alarmRules;
	}
	
}
