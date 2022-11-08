package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
/**
 * <li>文件名称: BizDepBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月23日
 * @author   caoyong
 */
public class BizDepBo implements Serializable {
	private static final long serialVersionUID = 482569427032836788L;
	/** 主键ID **/
	private Long id;
	/** 业务单位或者业务服务名称 **/
	private String name;
	/** 业务单位或者业务服务旧名称 **/
	private String oldName;
	/** 图片路径 **/
	private long fileId;
	/** 0:业务单位;1:业务服务 **/
	private Integer type;
	/** 查询业务单位时存在多个业务服务（每个业务服务与业务应用之间的关系） **/
	private Set<Long> bizMainIds;
	/** 创建人ID **/
	private Long entryId;
	/** 创建时间 **/
	private Date entryDateTime;
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
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Set<Long> getBizMainIds() {
		return bizMainIds;
	}
	public void setBizMainIds(Set<Long> bizMainIds) {
		this.bizMainIds = bizMainIds;
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
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
}
