package com.mainsteam.stm.portal.alarm.web.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 * <li>文件名称: AlarmVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月
 * @author xhf
 */
public class AlarmVo implements Serializable{
	private static final DateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 告警ID
	 */
	private Long alarmId;
	
	/**
	 * 告警内容
	 */
	private String alarmContent;
	
	/**
	 * 资源ID
	 */
	private Long resourceId;
	
	/**
	 * 资源名称
	 */
	private String resourceName;
	
	/**
	 * IP地址
	 */
	private String ipAddress;
	
	/**
	 *监控类型
	 */
	private String alarmType;
     
	/**
	 * 告警产生时间
	 */
	private Date collectionTime;
	private String collectionTimeShow;
	
	/**
	 * 工单状态
	 */
	private String itmsData;
	
	/**
	 * 工单ID
	 */
	private String itsmOrderId;

	/**
	 * 最近采集时间
	 */
	private  Date acquisitionTime;
	
	/**
	 * 告警恢复时间
	 */
	private  Date recoveryTime;
	private String recoveryTimeShow;
	  /**
     * 资源状态(致命,严重,告警,正常,未知)
     */
    private String instanceStatus;
    
    private Map<String, String> relation;
    
    private String prentCategory;
    
    private String childCategory;
    
    private String isCheckedRadioOne;
    
    private String isCheckedRadioTwo;
    
    private String queryTime;
    
	private Date startTime;
	private Date endTime;
    
	private String dataClass;
	
	//指标id
	private String metricId;
	//指标key
	private String recoverKey;
	
	private String alarmChecked;
	//是否点击资源告警列表界面
	private  String onclickAlarm;
	
	/**
     * 根据IP或者名字查询
     */
    private String iPorName;
	
	/**
	 *已恢复告警关联查询条件
	 */
	private Long recoveryAlarmId;
	
	/**
	 * 快照json
	 */
	private String snapShotJSON;
	/**
	 * 告警处理方式
	 */
	private String handType;
	/**
	 * 告警类型
	 */
	private String SysID;
	
	/**
	 * 告警重复次数
	 */
	private long repeatNum;
	/**
	 * 告警产生时间
	 */
	private String collection_Time;
	/**
	 * 告警恢复时间
	 */
	private String recover_Time;
	private String sys_type;
	public String getItsmOrderId() {
		return itsmOrderId;
	}

	public void setItsmOrderId(String itsmOrderId) {
		this.itsmOrderId = itsmOrderId;
	}

	public String getItmsData() {
		return itmsData;
	}

	public void setItmsData(String itmsData) {
		this.itmsData = itmsData;
	}

	public Long getRecoveryAlarmId() {
		return recoveryAlarmId;
	}

	public void setRecoveryAlarmId(Long recoveryAlarmId) {
		this.recoveryAlarmId = recoveryAlarmId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getOnclickAlarm() {
		return onclickAlarm;
	}

	public void setOnclickAlarm(String onclickAlarm) {
		this.onclickAlarm = onclickAlarm;
	}

	public Date getRecoveryTime() {
		return recoveryTime;
	}

	public void setRecoveryTime(Date recoveryTime) {
		this.recoveryTime = recoveryTime;
	}

	public String getAlarmChecked() {
		return alarmChecked;
	}

	public void setAlarmChecked(String alarmChecked) {
		this.alarmChecked = alarmChecked;
	}

	public String getDataClass() {
		return dataClass;
	}

	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}

	public String getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}

	public String getIsCheckedRadioTwo() {
		return isCheckedRadioTwo;
	}

	public void setIsCheckedRadioTwo(String isCheckedRadioTwo) {
		this.isCheckedRadioTwo = isCheckedRadioTwo;
	}

	public String getIsCheckedRadioOne() {
		return isCheckedRadioOne;
	}

	public void setIsCheckedRadioOne(String isCheckedRadioOne) {
		this.isCheckedRadioOne = isCheckedRadioOne;
	}

	public Date getStartTime() {
		return startTime;
	}

	
	
	public String getPrentCategory() {
		return prentCategory;
	}

	public void setPrentCategory(String prentCategory) {
		this.prentCategory = prentCategory;
	}

	public void setStartTime(String startTime) {
		try {
			if (startTime != null)
				this.startTime = format.parse(startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		try {
			if (endTime != null)
				this.endTime = format.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public String getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(String childCategory) {
		this.childCategory = childCategory;
	}

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public Long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(Long alarmId) {
		this.alarmId = alarmId;
	}

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public Date getCollectionTime() {
		
		return collectionTime;
	}

	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}

	public Map<String, String> getRelation() {
		return relation;
	}

	public void setRelation(Map<String, String> relation) {
		this.relation = relation;
	}

	public Date getAcquisitionTime() {
		return acquisitionTime;
	}

	public void setAcquisitionTime(Date acquisitionTime) {
		this.acquisitionTime = acquisitionTime;
	}

	public String getiPorName() {
		return iPorName;
	}

	public void setiPorName(String iPorName) {
		this.iPorName = iPorName;
	}

	public String getSnapShotJSON() {
		return snapShotJSON;
	}

	public void setSnapShotJSON(String snapShotJSON) {
		this.snapShotJSON = snapShotJSON;
	}

	public String getHandType() {
		return handType;
	}

	public void setHandType(String handType) {
		this.handType = handType;
	}

	public String getSysID() {
		return SysID;
	}

	public void setSysID(String sysID) {
		SysID = sysID;
	}

	public String getRecoverKey() {
		return recoverKey;
	}

	public void setRecoverKey(String recoverKey) {
		this.recoverKey = recoverKey;
	}

	public String getCollectionTimeShow() {
		return collectionTimeShow;
	}

	public void setCollectionTimeShow(String collectionTimeShow) {
		this.collectionTimeShow = collectionTimeShow;
	}

	public String getRecoveryTimeShow() {
		return recoveryTimeShow;
	}

	public void setRecoveryTimeShow(String recoveryTimeShow) {
		this.recoveryTimeShow = recoveryTimeShow;
	}

	public long getRepeatNum() {
		return repeatNum;
	}

	public void setRepeatNum(long repeatNum) {
		this.repeatNum = repeatNum;
	}

	public String getCollection_Time() {
		return collection_Time;
	}

	public void setCollection_Time(String collection_Time) {
		this.collection_Time = collection_Time;
	}

	public String getRecover_Time() {
		return recover_Time;
	}

	public void setRecover_Time(String recover_Time) {
		this.recover_Time = recover_Time;
	}

	public String getSys_type() {
		return sys_type;
	}

	public void setSys_type(String sys_type) {
		this.sys_type = sys_type;
	}
	
}
