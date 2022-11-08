package com.mainsteam.stm.system.resource.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.resource.bo.CategoryBo;
import com.mainsteam.stm.system.resource.bo.ResourceModuleBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

/**
 * <li>文件名称: IResourceApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月19日
 * @author   ziwenwen
 */
public interface IResourceApi {
	
	/**
	 * <pre>
	 * 获取所有树形结构的资源种类
	 * </pre>
	 */
	CategoryBo getTreeCategory();
	
	/**
	 * <pre>
	 * 给在一个资源种类id，获取树形资源种类列表数据
	 * </pre>
	 * @param categoryId
	 */
	CategoryBo getTreeCategory(String categoryId);
	
	/**
	 * <pre>
	 * 键为资源类型id，置为资源类型名称
	 * </pre>
	 * @param categoryId
	 */
	Map<String,String> getCategoryMapper();
	
	/**
	 * <pre>
	 * 根据用户id获取用户拥有的所有资源集合
	 * </pre>
	 * @param userId
	 * @return
	 */
	List<ResourceInstanceBo> getResources(ILoginUser user);
	
	
	/**
	 * <pre>
	 * 查询所有的资源集合
	 * </pre>
	 * @return
	 */
	List<ResourceInstanceBo> getAllParentInstance();
	
	/**
	 * <pre>
	 * 根据用户id和域id获取用户在特定域下的所有资源集合
	 * </pre>
	 * @param domainId
	 * @param userId
	 * @return
	 */
	List<ResourceInstanceBo> getResources(ILoginUser user,Long domainId);
	
	/**
	 * <pre>
	 * 根据用户id获取该用户指定几个域
	 * </pre>
	 * @param userId
	 * @param domainIds
	 * @return
	 */
	List<ResourceInstanceBo> getResources(ILoginUser user,List<Long> domainIds);
	
	/**
	 * <pre>
	 * 根据查询对象获取资源实例
	 * </pre>
	 * @param queryBo
	 * @return
	 */
	List<ResourceInstanceBo> getResources(ResourceQueryBo queryBo);
	
	/**
	 * <pre>
	 * 过滤允许用户访问的主资源id集合
	 * </pre>
	 * @param queryBo
	 * @return
	 */
	List<Long> accessFilter(ResourceQueryBo queryBo,Collection<Long> parentResourceIds);
	
	/**
	 * <pre>
	 * 根据资源id获取资源
	 * </pre>
	 * @param resourceId
	 * @return
	 */
	ResourceInstanceBo getResource(Long resourceId);
	
	/**
	 * <pre>
	 * 根据资源id获取资源
	 * </pre>
	 * @param resourceId
	 * @return
	 */
	List<ResourceInstanceBo> getResource(List<Long> resourceIds);
	
	/**
	 * <pre>
	 * 根据资源id获取其下的所有模型类型
	 * </pre>
	 * @param categoryId
	 * @return
	 */
	List<ResourceModuleBo> getModules(String categoryId);
	
	/**
	 * <pre>
	 * 根据资源类型id获取所有父级资源类型id并组成数组，越往顶级越排到数组后面
	 * 包含资源id本身（放在最前面）
	 * 不包含isDisplay为false的
	 * </pre>
	 * @param categoryId
	 * @return 不存在资源类型或者传入null返回null
	 */
	String[] getCategoryParents(String categoryId);
	
	/**
	 * <pre>
	 * 根据资源类型id获取所有父级资源类型id并组成数组，越往顶级越排到数组后面
	 * 包含资源id本身（放在最前面）
	 * </pre>
	 * @param categoryId
	 * @return  不存在资源类型或者传入null返回null
	 */
	String[] getAllCategoryParents(String categoryId);
}


