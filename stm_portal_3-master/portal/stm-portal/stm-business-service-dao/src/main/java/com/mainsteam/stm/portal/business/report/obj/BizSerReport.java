package com.mainsteam.stm.portal.business.report.obj;

/**
 * <li>文件名称: BizSerReport.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: 业务报表</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   caoyong
 */
public class BizSerReport {
	/**业务应用id**/
	private Long id;
	/**业务应用name**/
	private String name;
	/**业务应用负责人ID**/
	private Long userId;
	/**业务应用负责人名称**/
	private String userName;
	
	/**可用率(%)**/
	private Float availableRate;
	/**MTTR(小时)**/
	private Float mttr;
	/**MTBF(天)**/
	private Float mtbf;
	/**宕机次数(次)**/
	private Integer outageTimes;
	/**宕机时长(小时)**/
	private Float downTime;
	/**告警数量**/
	private Integer warnNum;
	/**未恢复告警数量**/
	private Integer unrecoveredWarnNum;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getOutageTimes() {
		return outageTimes;
	}
	public void setOutageTimes(Integer outageTimes) {
		this.outageTimes = outageTimes;
	}
	public Float getAvailableRate() {
		return availableRate;
	}
	public void setAvailableRate(Float availableRate) {
		this.availableRate = availableRate;
	}
	public Float getMttr() {
		return mttr;
	}
	public void setMttr(Float mttr) {
		this.mttr = mttr;
	}
	public Float getMtbf() {
		return mtbf;
	}
	public void setMtbf(Float mtbf) {
		this.mtbf = mtbf;
	}
	public Float getDownTime() {
		return downTime;
	}
	public void setDownTime(Float downTime) {
		this.downTime = downTime;
	}
	public Integer getWarnNum() {
		return warnNum;
	}
	public void setWarnNum(Integer warnNum) {
		this.warnNum = warnNum;
	}
	public Integer getUnrecoveredWarnNum() {
		return unrecoveredWarnNum;
	}
	public void setUnrecoveredWarnNum(Integer unrecoveredWarnNum) {
		this.unrecoveredWarnNum = unrecoveredWarnNum;
	}
}
