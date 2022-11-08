package com.mainsteam.stm.portal.resource.po;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.portal.resource.bo.CustomGroupEnumType;

/**
 * 
 * <li>文件名称: CustomGroupPo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月25日
 * @author   wangxinghao
 */
public class CustomGroupPo implements Serializable{
	private static final long serialVersionUID = 4090064463016838587L;
	/**
	 * 自定义资源组ID
	 */
	private Long id;
	/**
	 * 自定义资源组名称
	 */
	private String name;
	
	/**
	 * 自定义资源组描述
	 */
	private String description;
	
	/**
	 * 自定义资源组类型
	 */
	private CustomGroupEnumType groupType;
	
	/**
	 * 操作时间
	 */
	private Long entryId;
	/**
	 * 操作日期
	 */
	private Date entryDatetime;
	
	private Long pid;
	
	private Integer sort;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	public Date getEntryDatetime() {
		return entryDatetime;
	}
	public void setEntryDatetime(Date entryDatetime) {
		this.entryDatetime = entryDatetime;
	}
	public CustomGroupEnumType getGroupType() {
		return groupType;
	}
	public void setGroupType(CustomGroupEnumType groupType) {
		this.groupType = groupType;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	

}
