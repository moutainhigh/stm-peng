package com.mainsteam.stm.system.um.resourcegroup.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;

/**
 * <li>文件名称: IResourceGroupApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月14日 下午1:47:27
 * @author   zhangjufneng
 */
public interface IResourceGroupApi {

	
	/**
	* @Title: addDomainResourceGroupRel
	* @Description: 添加域与资源组的关联关系
	* @param domainId 域Id
	* @param groupId 资源组ID
	* @return  int
	* @throws
	*/
	boolean addDomainResourceGroupRel(long domainId,long groupId);
	
	
	/**
	* @Title: addResourceGroup
	* @Description: 添加资源组，同时添加资源组与域的关联关系
	* @param rel 资源组信息与域 信息
	* @return  int
	* @throws
	*/
	boolean addResourceGroup(DomainResourceGroupRel rel);
	
	
	/**
	* @Title: delResourceGroup
	* @Description: 删除资源组，同时删除资源组与域的关系
	* @param groupId
	* @return  int
	* @throws
	*/
	String delResourceGroup(long[] groupIds);
	
	
	/**
	* @Title: updateResourceGroup
	* @Description: 更新资源组信息
	* @param rel 资源组信息
	* @return  int
	* @throws
	*/
	boolean updateResourceGroup(DomainResourceGroupRel rel);
	
	/**
	* @Title: queryAllGroupByDomain
	* @Description: 通过域ID获取域下所有资源组
	* @param domainId
	* @return  List<DomainResourceGroupRel>
	* @throws
	*/
	List<DomainResourceGroupRel> queryAllGroupByDomain(long domainId);
	
	/**
	* @Title: queryResourceGroupPage
	* @Description: 分页查询所有资源组信息
	* @param page
	* @return  List<DomainResourceGroupRel>
	* @throws
	*/
	List<DomainResourceGroupRel> queryResourceGroupPage(Page<DomainResourceGroupRel, DomainResourceGroupRel> page);
	
	/**
	* @Title: getResourceGroupRel
	* @Description: 通过资源组ID查询资源组信息
	* @param groupId
	* @return  DomainResourceGroupRel
	* @throws
	*/
	DomainResourceGroupRel getResourceGroupRel(long groupId);
}
