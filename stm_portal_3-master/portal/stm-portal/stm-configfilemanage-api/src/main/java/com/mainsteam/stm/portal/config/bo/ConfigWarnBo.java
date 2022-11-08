package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * <li>文件名称: ConfigWarnBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月23日
 * @author   caoyong
 */
public class ConfigWarnBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 告警名称
	 */
	private String name;
	/**
	 * 创建人
	 */
	private Long entryId;
	/**
	 * 创建时间
	 */
	private Date entryDateTime;
	/**
	 * 创建人
	 */
	private Long updateId;
	/**
	 * 创建时间
	 */
	private Date updateDateTime;
	/**
	 * 关联的告警关联的设备
	 */
	private List<ConfigWarnResourceBo> configWarnResourceBos;
	/**
	 * 告警关联的规则（接收人和接收方式）
	 */
	private List<ConfigWarnRuleBo> configWarnRuleBos;
	/**
	 * 更新人姓名
	 */
	private String updateUserName;
	/**
	 * 创建人姓名
	 */
	private String entryUserName;
	
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
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	public Date getEntryDateTime() {
		return entryDateTime;
	}
	public void setEntryDateTime(Date entryDateTime) {
		this.entryDateTime = entryDateTime;
	}
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}
	public Date getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
	public List<ConfigWarnResourceBo> getConfigWarnResourceBos() {
		return configWarnResourceBos;
	}
	public void setConfigWarnResourceBos(
			List<ConfigWarnResourceBo> configWarnResourceBos) {
		this.configWarnResourceBos = configWarnResourceBos;
	}
	public List<ConfigWarnRuleBo> getConfigWarnRuleBos() {
		return configWarnRuleBos;
	}
	public void setConfigWarnRuleBos(List<ConfigWarnRuleBo> configWarnRuleBos) {
		this.configWarnRuleBos = configWarnRuleBos;
	}
	public String getUpdateUserName() {
		return updateUserName;
	}
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
	public String getEntryUserName() {
		return entryUserName;
	}
	public void setEntryUserName(String entryUserName) {
		this.entryUserName = entryUserName;
	}
}
