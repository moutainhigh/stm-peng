package com.mainsteam.stm.resourcelog.strategy.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <li>文件名称: StrategyBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: syslog策略对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日
 * @author   ziwenwen
 */
public class StrategyBo implements Serializable{
	
	private static final long serialVersionUID = 4655184563349190522L;

	/**
	 * 策略id
	 */
	Long id;
	
	/**
	 * 策略名称
	 */
	String name;
	
	/**
	 * 策略关联的域，可能用于多个域
	 */
	Long domainId;
	
	/**
	 * 策略关联的域，用于多个域
	 */
	private List<Long> domainIds;
	
	/**
	 * 域名称(用于前台展示)
	 */
	private String domainName;
	
	/**
	 * 创建人id
	 */
	Long creatorId;
	
	/**
	 * 创建人姓名(用于前台展示)
	 */
	private String creator;
	
	/**
	 * 创建日期
	 */
	Date createDate;
	
	/**
	 * 接收表单时间数据
	 */
	private String createTime;
	
	/**
	 * 修改人id
	 */
	Long updaterId;
	
	/**
	 * 修改人姓名
	 */
	private String updater;
	
	/**
	 * 修改时间
	 */
	Date updateDate;
	
	/**
	 * 接收表单时间数据
	 */
	private String updateTime;
	
	private String alarmLevel;
	/**
	 * 类型ID
	 * 1-syslog 2-snmptrap
	 */
	Long typeId;
	
	/**
	 * SNMPTrap普通类型
	 */
	String commonType;
	
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

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUpdaterId() {
		return updaterId;
	}

	public void setUpdaterId(Long updaterId) {
		this.updaterId = updaterId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getCommonType() {
		return commonType;
	}

	public void setCommonType(String commonType) {
		this.commonType = commonType;
	}

	public List<Long> getDomainIds() {
		return domainIds;
	}

	public void setDomainIds(List<Long> domainIds) {
		this.domainIds = domainIds;
	}
}


