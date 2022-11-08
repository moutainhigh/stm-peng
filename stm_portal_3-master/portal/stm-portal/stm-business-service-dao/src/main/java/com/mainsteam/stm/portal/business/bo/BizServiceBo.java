package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * <li>文件名称: BizServiceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月7日
 * @author   caoyong
 */
public class BizServiceBo implements Serializable {
	private static final long serialVersionUID = 482569427032836788L;
	/** 主键ID **/
	private Long id;
	/** 业务名称 **/
	private String name;
	/** 旧的业务名称 **/
	private String oldName;
	/** 业务备注 **/
	private String remark;
	/** 业务状态ID外键（0：默认；1：自定义）**/
	private String status_type;
	/** 责任人ID **/
	private String entry_id;
	/** 创建人ID **/
	private String createrId;
	/** 责任人名字 **/
	private String entryName;
	/** 创建时间 **/
	private Date entry_datetime;
	/** 更新时间 **/
	private Date update_datetime;
	/** 删除时间 **/
	private Date delete_datetime;
	/** 上传图片文件ID **/
	private Long fileId;
	/** 拓扑图json数据 **/
	private String topology;
	/** 业务状态 **/
	private String status;
	/** 致命逻辑关系 **/
	private String death_relation;
	/** 严重逻辑关系 **/
	private String serious_relation;
	/** 告警逻辑关系 **/
	private String warn_relation;
	/**关联的资源**/
	private List<ResourceInstance> resourceInstances;
	/** 域id **/
	private String domain_id;
	/** svg **/
	private String svg;
	
	public String getTopology() {
		return topology;
	}
	public void setTopology(String topology) {
		this.topology = topology;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getEntry_datetime() {
		return entry_datetime;
	}
	public void setEntry_datetime(Date entry_datetime) {
		this.entry_datetime = entry_datetime;
	}
	public Date getUpdate_datetime() {
		return update_datetime;
	}
	public void setUpdate_datetime(Date update_datetime) {
		this.update_datetime = update_datetime;
	}
	public Date getDelete_datetime() {
		return delete_datetime;
	}
	public void setDelete_datetime(Date delete_datetime) {
		this.delete_datetime = delete_datetime;
	}
	public String getStatus_type() {
		return status_type;
	}
	public void setStatus_type(String status_type) {
		this.status_type = status_type;
	}
	public String getEntry_id() {
		return entry_id;
	}
	public void setEntry_id(String entry_id) {
		this.entry_id = entry_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeath_relation() {
		return death_relation;
	}
	public void setDeath_relation(String death_relation) {
		this.death_relation = death_relation;
	}
	public String getSerious_relation() {
		return serious_relation;
	}
	public void setSerious_relation(String serious_relation) {
		this.serious_relation = serious_relation;
	}
	public String getWarn_relation() {
		return warn_relation;
	}
	public void setWarn_relation(String warn_relation) {
		this.warn_relation = warn_relation;
	}
	public List<ResourceInstance> getResourceInstances() {
		return resourceInstances;
	}
	public void setResourceInstances(List<ResourceInstance> resourceInstances) {
		this.resourceInstances = resourceInstances;
	}
	public String getEntryName() {
		return entryName;
	}
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	public String getDomain_id() {
		return domain_id;
	}
	public void setDomain_id(String domain_id) {
		this.domain_id = domain_id;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getSvg() {
		return svg;
	}
	public void setSvg(String svg) {
		this.svg = svg;
	}
	public String getCreaterId() {
		return createrId;
	}
	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}
}
