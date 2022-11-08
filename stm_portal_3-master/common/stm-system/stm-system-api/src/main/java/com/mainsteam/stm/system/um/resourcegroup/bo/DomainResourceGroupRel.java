package com.mainsteam.stm.system.um.resourcegroup.bo;

import java.io.Serializable;

import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;

public class DomainResourceGroupRel extends CustomGroupBo implements Serializable {
	private static final long serialVersionUID = 1999540527943377614L;
	
	/**域ID*/
	private long domainId;
	/**域名称*/
	private String domainName;
	/**创建人姓名*/
	private String createUserName;
	
	private Long[] domainIds;
	public long getDomainId() {
		return domainId;
	}
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public DomainResourceGroupRel() {
		super();
	}
	public Long[] getDomainIds() {
		return domainIds;
	}
	public void setDomainIds(Long[] domainIds) {
		this.domainIds = domainIds;
	}
	
}
