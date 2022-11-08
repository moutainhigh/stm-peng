package com.mainsteam.stm.portal.config.api;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupBo;

/**
 * <li>文件名称: IConfigCustomGroupApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public interface IConfigCustomGroupApi {
	/**
	 * 移入配置组分页
	 * @param page
	 */
	void selectByPage(Page<ConfigCustomGroupBo, ConfigCustomGroupBo> page) throws Exception;
	/**
	 * 资源移入配置组
	 * @param groupIds 组ids
	 * @param resourceInstanceIds 资源ids
	 * @return
	 */
	int moveIntoGroup(long[] groupIds,long[] resourceInstanceIds) throws Exception;
	/**
	 * 新增用户自定义配置组
	 * 
	 * @param accountBo
	 * @return
	 */
	int insert(ConfigCustomGroupBo configCustomGroupBo) throws Exception;
	
	/**
	 * 删除用户自定义配置组
	 * 
	 * @param id
	 * @return
	 */
	int del(long id) throws Exception;
	
	/**
	 * 修改用户自定义配置组
	 * 
	 * @param accountBo
	 * @return
	 */
	int update(ConfigCustomGroupBo configCustomGroupBo) throws Exception;

	/**
	 * 获取单个用户自定义配置组
	 * 
	 * @param id
	 * @return
	 */
	ConfigCustomGroupBo getCustomGroup(long id) throws Exception;
	
	/**
	 * 获取所有用户自定义配置组
	 * @param userId 根据当前用户查询用户创建的组
	 * @return
	 */
	List<ConfigCustomGroupBo> getList(long userId) throws Exception;
	
	/**
	 * 从自定义配置组中移出资源
	 * @param CustomGroupBo
	 * @return
	 */
	int deleteResourceFromCustomGroup(ConfigCustomGroupBo configCustomGroupBo) throws Exception;
	
	/**
	 * 从数据库删除资源和自定义配置组的关系
	 * @param ids
	 * @return
	 */
	int deleteGroupAndResourceRelation(long[] ids) throws Exception;
	
	/**
	 * 获取所有的资源
	 * @return
	 */
	List<ResourceInstance> getAllResourceInstanceList();
	
	/**
	 * 获取指定id集合的资源列表
	 * @return
	 */
	List<ResourceInstance> getResourceInstanceListByIds(String ids) throws Exception;
	/**
	 * 获取指定id集合外的的资源列表
	 * @return
	 */
	List<ResourceInstance> getExceptResourceInstanceListByIds(String ids) throws Exception;
}
