package com.mainsteam.stm.home.workbench.resource.api;

import java.util.List;

import com.mainsteam.stm.home.workbench.resource.bo.PageResource;
import com.mainsteam.stm.home.workbench.resource.bo.WorkbenchResourceInstance;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.resource.bo.CategoryBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

/**
 * <li>文件名称: IResourceCatogaryApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
public interface IResourceApi {
	
	/**
	 * 获取所有资源种类
	 * @return
	 */
	CategoryBo getAllCategory();
	
	/**
	 * 根据资源种类id获取资源列表
	 * @param catogaryId
	 * @return
	 */
	List<ResourceInstanceBo> getResources(String catogaryId);
	
	PageResource getResourceCount(String catogaryId,ILoginUser user, Long domainId);
	
	/**
	 * 
	 * @param catogaryId
	 * @param user
	 * @param domainId
	 * @return
	 */
	PageResource getResourceCount(String catogaryId,ILoginUser user, Long... domainId);
	
	/**
	* @Title: queryGroupResourceByDomain
	* @Description: 通过用户ID，资源组ID，域 ID获取资源;查询用户在该域的资源组下能使用的资源
	* @param type 资源类别“主机(host)、网络(network)、应用(app)”
	* @param userId 用户ID
	* @param groupId 资源组ID
	* @param domainId 域 ID
	* @return  List<Long>
	* @throws
	*/
	public List<Long> queryGroupResourceByDomain(String type,ILoginUser user,long groupId,Long... domainId);
	
	/**
	* @Title: queryGroupResourceByDomain
	* @Description: 通过用户ID，资源组ID，域 ID获取资源;查询用户在该域的资源组下能使用的主资源
	* @param type 资源类别“主机(host)、网络(network)、应用(app)”
	* @param userId 用户ID
	* @param groupId 资源组ID
	* @param domainId 域 ID
	* @return  List<Long>
	* @throws
	*/
	public List<Long> queryGroupParentResourceByDomain(String type,ILoginUser user,long groupId,Long... domainId);
	
	/**
	* @Title: getCategoriesInstanceIds
	* @Description: 通过资源类别获取资源实例ID集合
	* @param resource
	* @return  List<Long>
	* @throws
	*/
	public List<Long> getCategoriesInstanceIds(String resource);
	
	/**
	* @Title: getCategories
	* @Description: 通过资源类别获取所有子资源类别
	* @param type
	* @return  List<String>
	* @throws
	*/
	public List<String> getCategorIds(String type);
	
	/**
	 * 根据资源类别ID获取主资源实例
	 */
	public List<WorkbenchResourceInstance> getResourceInstanceByCategoryId(String categoryId,Long[] domainId,int startNum,int pageSize,String content,ILoginUser user);
	
	/**
	 * 根据资源获取资源实例
	 */
	public List<WorkbenchResourceInstance> getInstanceByResource(String resourceId,Long[] domainId,int startNum,int pageSize,String content,ILoginUser user);
	
}


