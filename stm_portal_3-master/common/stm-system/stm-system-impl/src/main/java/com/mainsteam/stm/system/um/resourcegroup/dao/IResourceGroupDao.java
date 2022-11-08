package com.mainsteam.stm.system.um.resourcegroup.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;

public interface IResourceGroupDao {

	/**
	* @Title: insert
	* @Description:添加资源组与域 的关系
	* @param rel
	* @return  int
	* @throws
	*/
	int insert(DomainResourceGroupRel rel);
	
	/**
	* @Title: batchDelRel
	* @Description: 批量删除资源组与域的关系
	* @param rel
	* @return  int
	* @throws
	*/
	int batchDel(long[] groupIds);
	
	/**
	* @Title: getRelByGroup
	* @Description: 通过资源组ID获取域信息
	* @param groupId
	* @return  DomainResourceGroupRel
	* @throws
	*/
	DomainResourceGroupRel get(long groupId);
	
	/**
	* @Title: queryRelByDomain
	* @Description: 通过域 ＩＤ获取资源组与域 的关系
	* @param domainId
	* @return  List<DomainResourceGroupRel>
	* @throws
	*/
	List<DomainResourceGroupRel> queryRelByDomain(long domainId);
	
	/**
	* @Title: pageSelect
	* @Description: 分页查询所有资源组
	* @param page
	* @return  List<DomainResourceGroupRel>
	* @throws
	*/
	List<DomainResourceGroupRel> pageSelect(Page<DomainResourceGroupRel, DomainResourceGroupRel> page);
}
