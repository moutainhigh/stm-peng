package com.mainsteam.stm.system.um.relation.bo;

import java.io.Serializable;

/**
 * <li>文件名称: UmRelation.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月20日
 * @author   ziwenwen
 */
public class UmRelation implements Serializable{
	private static final long serialVersionUID = -3825952623334921547L;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 角色ID
	 */
	private Long roleId;
	
	/**
	 * 域ID
	 */
	private Long domainId;
	
	public UmRelation(){}
	public UmRelation(Long userId){
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	
	
}


