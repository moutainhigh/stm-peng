package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupBo;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupResourceBo;
/**
 * <li>文件名称: ICustomResourceGroupDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public interface IConfigCustomGroupDao {
	/**
	 * 移入配置组的分页数据
	 * @param page
	 */
	void selectByPage(Page<ConfigCustomGroupBo, ConfigCustomGroupBo> page);
	
	int insert(ConfigCustomGroupBo configCustomGroupBo);
	
	int batchInsert(List<ConfigCustomGroupResourceBo> cList);

	int del(long id);

	int updateGroup(ConfigCustomGroupBo configCustomGroupBo);
	
//	ConfigCustomGroupResourceBo get(long id);
	
	ConfigCustomGroupBo getCustomGroup(long id);
	/**
	 * 通过组ID查找对应资源ID集合
	 * @param id
	 * @return
	 */
	List<String> getGroupResourceIdsByGroup(long id);
	/**
	 * 获取所有用户自定义配置组
	 * @param entryId 根据当前用户查询用户创建的组
	 * @return
	 */
	List<ConfigCustomGroupBo> getList(long userId);
	
	/**
	 * 
	 * 通过配置组ID删除资源和配置组的对应关系
	 * @param id
	 * @return
	 */
	int deleteResourceIDsByGroup(long id);
	
	/**
	 * 
	 * 通过配置组ID删除自定义配置组
	 * @param id
	 * @return
	 */
	int deleteGroupByGroup(long id);
	
	/**
	 * 
	 * 检查自定义配置组名称是否存在
	 * @param groupName
	 * @return
	 */
	List<ConfigCustomGroupBo> checkGroupNameIsExsit(String groupName);
	
	/**
	 * 从自定义配置组中移出资源
	 * @param 
	 * @return
	 */
	int deleteResourceFromCustomGroupByIds(ConfigCustomGroupBo bo);
	
	/**
	 * 查找没有对应资源的自定义配置组
	 */
	List<Long> selectResourceNumberIsZeroGroup();
	
	int deleteGroupAndResourceRelationById(long[] ids);
}
