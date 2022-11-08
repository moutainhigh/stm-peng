package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Set;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.resource.bo.ResourceViewBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;


/**
 * <li>文件名称: ResourceCategoryApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月28日
 * @author   pengl
 */
public interface ResourceCategoryApi {
	
	/**
	 * 获取所有一级资源类别
	 * @return
	 */
	List<ResourceCategoryBo> getFirstStageResourceCategoryList();
	
	/**
	 * 获取含有资源的一级资源类别
	 * @return
	 */
	List<ResourceCategoryBo> getResourceCategoryList(ILoginUser user) throws Exception ;
	/**
	 * 通过资源获取含有资源的一级资源类别
	 * @return
	 */
	List<ResourceCategoryBo> getResourceCategoryListByResources(List<ResourceInstanceBo> userResourceList, List<String> requiredCategory);
	
	/**
	 * 获取所有的资源
	 * @return
	 */
	List<ResourceInstanceBo> getAllResourceInstanceList(ILoginUser user);
	
	/**
	 * 获取所有的资源(系统管理)
	 * @return
	 */
	List<ResourceInstanceBo> getAllResourceInstanceList(long domainId,ILoginUser user);
	
	/**
	 * 获取指定id集合的资源列表
	 * @return
	 */
	List<ResourceInstanceBo> getResourceInstanceListByIds(String ids);
	
	/**
	 * 获取指定id集合外的的资源列表
	 * @return
	 */
	List<ResourceInstanceBo> getExceptResourceInstanceListByIds(String ids,ILoginUser user);
	
	/**
	 * 获取指定id集合外的的资源列表(带搜索关键字)
	 * @return
	 */
	List<ResourceInstanceBo> getExceptResourceInstanceListByIdsAndSearchContent(String ids,ILoginUser user,String searchContent);
	
	/**
	 * 获取指定id集合外的的资源列表(带搜索关键字)
	 * @return
	 */
	List<ResourceInstanceBo> getNewExceptResourceInstanceListByIdsAndSearchContent(String ids,ILoginUser user,String searchContent,int startNum,int pageSize);
	
	/**
	 * 获取指定id集合外的的资源列表(系统管理)
	 * @return
	 */
	List<ResourceInstanceBo> getExceptResourceInstanceListByIds(String ids,long domainId,ILoginUser user);
	
	/**
	 * 获取指定id集合外的的资源列表(带搜索关键字)(系统管理)
	 * @return
	 */
	List<ResourceInstanceBo> getExceptResourceInstanceListByIdsAndSearchContent(String ids,long domainId,ILoginUser user,String searchContent);
}
