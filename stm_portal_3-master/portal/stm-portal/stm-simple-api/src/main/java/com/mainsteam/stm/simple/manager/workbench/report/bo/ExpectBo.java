package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: ExpectBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 期望值</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月17日
 * @author   ziwenwen
 */
public class ExpectBo implements Serializable{
	private static final long serialVersionUID = 4668242882094566475L;
	
	public static final int IS_NOTICE_CONTACT = 1;
	private Long id;
	
	private Long reportId;
	
	/**
	 * 可用率
	 */
	private Integer available;
	
	/**
	 * 
	 */
	private Integer mttr;
	
	/**
	 * 
	 */
	private Integer mtbf;
	
	/**
	 * 宕机次数
	 */
	private Integer downTimes;
	
	/**
	 * 宕机时长
	 */
	private Integer downDuration;
	
	/**
	 * 告警数量
	 */
	private Integer alarmTimes;
	
	/**
	 * 未恢复告警数量
	 */
	private Integer unrecoveryAlarmTimes;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	private String createTimeStr;
	
	/**
	 * 创建人ID
	 */
	private Long creatorId;
	/**
	 * 创建人姓名
	 */
	private String creatorName;
	
	/**
	 * 期望值操作状态，是否通知过联系人（0未通知，1已通知过）
	 */
	private int state = 0;
	
	/**
	 * 联系人通知时间
	 */
	private Date noticeDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Integer getAvailable() {
		return available;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public Integer getMttr() {
		return mttr;
	}

	public void setMttr(Integer mttr) {
		this.mttr = mttr;
	}

	public Integer getMtbf() {
		return mtbf;
	}

	public void setMtbf(Integer mtbf) {
		this.mtbf = mtbf;
	}

	public Integer getDownTimes() {
		return downTimes;
	}

	public void setDownTimes(Integer downTimes) {
		this.downTimes = downTimes;
	}

	public Integer getDownDuration() {
		return downDuration;
	}

	public void setDownDuration(Integer downDuration) {
		this.downDuration = downDuration;
	}

	public Integer getAlarmTimes() {
		return alarmTimes;
	}

	public void setAlarmTimes(Integer alarmTimes) {
		this.alarmTimes = alarmTimes;
	}

	public Integer getUnrecoveryAlarmTimes() {
		return unrecoveryAlarmTimes;
	}

	public void setUnrecoveryAlarmTimes(Integer unrecoveryAlarmTimes) {
		this.unrecoveryAlarmTimes = unrecoveryAlarmTimes;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getNoticeDate() {
		return noticeDate;
	}

	public void setNoticeDate(Date noticeDate) {
		this.noticeDate = noticeDate;
	}
	
	
}


