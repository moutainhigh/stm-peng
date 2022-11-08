package com.mainsteam.stm.resourcelog.syslog.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <li>文件名称: SyslogResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月11日
 * @author   ziwenwen
 */
public class SyslogResourceBo implements Serializable{

	private static final long serialVersionUID = 7234245467116368194L;
	
	private String resourceIdStr;
	
	private Long id;
	
	private Long userId;
	/**
	 * 资源id
	 */
	private Long resourceId;

	/**
	 * 策略id
	 */
	private Long strategyId;
	
	/**
	 * 策略名称
	 */
	private String strategyName;
	/**
	 * 资源种类id
	 */
	private String categoryId;
	
	/**
	 * 是否开启监测
	 */
	private int isMonitor;

	/**
	 * 策略类型 1-syslog 2-snmptrap
	 */
	private int strategyType;

	/**
	 * 当日产生日志数量
	 */
	private int curDateCount;

	/**
	 * 历史总日志数量
	 */
	private int allCount;

	/**
	 * 最后一次产生日志时间
	 */
	private Date lastDate;

	/**
	 * 显示名称
	 */
	private String name;

	/**
	 * 资源ip，通过资源接口获得
	 */
	private String resourceIp;

	/**
	 * 资源类型
	 */
	private String resourceType;

	/**
	 * 日志策略
	 */
	private String logStrategy;
	
	/**
	 * IP
	 */
	private String snmptrapIp;
	
	/**
	 * 所属域ID集合
	 */
	private List<Long> domainIdsList;
	
	private Long domainId;
	
	private String selecedResourceIds;
	
	public SyslogResourceBo() {
	}

	/**
	 * @param id
	 * @param curDateCount
	 * @param allCount
	 * @param lastDate
	 * @param displanName
	 * @param resourceIp
	 * @param resourceType
	 * @param logStrategy
	 * @param name 
	 */
	public SyslogResourceBo(Long id, String displanName, String resourceIp,
			String resourceType, int curDateCount, int allCount, Date lastDate,
			String logStrategy,int isMonitor, String name) {
		super();
		this.id = id;
		this.curDateCount = curDateCount;
		this.allCount = allCount;
		this.lastDate = lastDate;
		this.name = name;
		this.resourceIp = resourceIp;
		this.resourceType = resourceType;
		this.logStrategy = logStrategy;
		this.isMonitor = isMonitor;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}

	public int getIsMonitor() {
		return isMonitor;
	}

	public void setIsMonitor(int isMonitor) {
		this.isMonitor = isMonitor;
	}

	public int getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(int strategyType) {
		this.strategyType = strategyType;
	}

	public int getCurDateCount() {
		return curDateCount;
	}

	public void setCurDateCount(int curDateCount) {
		this.curDateCount = curDateCount;
	}

	public int getAllCount() {
		return allCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the resourceIp
	 */
	public String getResourceIp() {
		return resourceIp;
	}

	/**
	 * @param resourceIp
	 *            the resourceIp to set
	 */
	public void setResourceIp(String resourceIp) {
		this.resourceIp = resourceIp;
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType
	 *            the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the logStrategy
	 */
	public String getLogStrategy() {
		return logStrategy;
	}

	/**
	 * @param logStrategy
	 *            the logStrategy to set
	 */
	public void setLogStrategy(String logStrategy) {
		this.logStrategy = logStrategy;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String getSnmptrapIp() {
		return snmptrapIp;
	}

	public void setSnmptrapIp(String snmptrapIp) {
		this.snmptrapIp = snmptrapIp;
	}

	public List<Long> getDomainIdsList() {
		return domainIdsList;
	}

	public void setDomainIdsList(List<Long> domainIdsList) {
		this.domainIdsList = domainIdsList;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getResourceIdStr() {
		return resourceIdStr;
	}

	public void setResourceIdStr(String resourceIdStr) {
		this.resourceIdStr = resourceIdStr;
	}

	public String getSelecedResourceIds() {
		return selecedResourceIds;
	}

	public void setSelecedResourceIds(String selecedResourceIds) {
		this.selecedResourceIds = selecedResourceIds;
	}
}
