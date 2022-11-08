package com.mainsteam.stm.portal.resource.dao;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.po.CustomGroupPo;
import com.mainsteam.stm.portal.resource.po.CustomGroupResourcePo;

/**
 * 
 * <li>文件名称: ICustomResourceGroupDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   wangxinghao
 */
public interface ICustomResourceGroupDao {
	
	int insert(CustomGroupPo customGroupPo);
	
	int batchInsert(List<CustomGroupResourcePo> customResourceGroupPoList);

	int del(long id);

	int updateGroup(CustomGroupPo customResourcePo);
	
	CustomGroupResourcePo get(long id);
	
	CustomGroupPo getCustomGroup(long id);
	/**
	 * 通过组ID查找对应资源ID集合
	 * @param id
	 * @return
	 */
	List<String> getGroupResourceIdsByGroup(long id);

	List<CustomGroupPo> getList(long userId);
	
	/**
	 * 
	 * 通过资源组ID删除资源和资源组的对应关系
	 * @param id
	 * @return
	 */
	int deleteResourceIDsByGroup(long id);
	
	/**
	 * 
	 * 通过资源组ID删除自定义资源组
	 * @param id
	 * @return
	 */
	int deleteGroupByGroup(long id);
	
	/**
	 * 
	 * 检查自定义资源组名称是否存在
	 * @param groupName
	 * @return
	 */
	int checkGroupNameIsExsit(CustomGroupPo customGroupPo);
	
	/**
	 * 从自定义资源组中移出资源
	 * @param CustomGroupBo
	 * @return
	 */
	int deleteResourceFromCustomGroupByIds(CustomGroupBo groupBo);
	
	/**
	 * 查找没有对应资源的自定义资源组
	 */
	List<Long> selectResourceNumberIsZeroGroup();
	
	int deleteGroupAndResourceRelationById(long[] ids);
	
	/**
	 * 查找对应资源关系是否存在
	 * 
	 */
	int checkGroupResourceRelationIsExsit(CustomGroupResourcePo customGroupResourcePo);
	
	/** 获取当前数据库SORT的最大值 */
	int getMaxSortByEntryId(long entryId);
	
	/** 更新资源组的排序信息 */
	int updateGroupSort(Map<String, String> map);
	
	/** 查询资源组的子资源组信息 */
	List<CustomGroupPo> getChildGroupsById(Map<String, String> map);
}
