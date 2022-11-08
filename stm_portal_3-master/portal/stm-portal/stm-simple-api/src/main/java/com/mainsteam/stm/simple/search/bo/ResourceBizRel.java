package com.mainsteam.stm.simple.search.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.bo.ResourceBizRel.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月28日
 */
public class ResourceBizRel implements Serializable, Cloneable{

	private static final long serialVersionUID = -1790387550892509299L;
	private Long resourceId;	//资源ID
	private Long type;
	private Long bizId;	//业务主键
	private String nav;	//导航描述
	private List<Long> resourceIds;	//一组资源ID
	public ResourceBizRel(){}
	public ResourceBizRel(Long resourceId, Long bizId, String nav){
		this.resourceId = resourceId;
		this.bizId = bizId;
		this.nav = nav;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public Long getBizId() {
		return bizId;
	}
	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	public String getNav() {
		return nav;
	}
	public void setNav(String nav) {
		this.nav = nav;
	}
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bizId == null) ? 0 : bizId.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceBizRel other = (ResourceBizRel) obj;
		if (bizId == null) {
			if (other.bizId != null)
				return false;
		} else if (!bizId.equals(other.bizId))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
