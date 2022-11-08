package com.mainsteam.stm.system.um.user.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: com.mainsteam.stm.system.um.user.bo.UserResourceRel.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月20日
 */
public class UserResourceRel implements Serializable{
	private static final long serialVersionUID = -5991485020883932467L;
	public static final int TYPE_RESOURCE_GROUP=2;
	public static final int TYPE_RESOURCE=1;
	private Long userId;
	private Long resourceId;
	private Integer type;
	private Long domainId;
	private List<Long> domainIds;
	public UserResourceRel(){}
	public UserResourceRel(Long userId, Long resourceId){
		this.userId = userId;
		this.resourceId = resourceId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public List<Long> getDomainIds() {
		return domainIds;
	}
	public void setDomainIds(List<Long> domainIds) {
		this.domainIds = domainIds;
	}
}
