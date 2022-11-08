package com.mainsteam.stm.system.um.domain.api;

public abstract interface IDomainReferencerRelationshipApi {

	/**
	* @Title: checkDomainIsRel
	* @Description: 检查域是否与其他发方存在引用关系
	* @param domainId 域ID
	* @return  boolean （true 存在引用关系；false 不存在引用关系）
	* @throws
	*/
	public abstract boolean checkDomainIsRel(long domainId);
}
