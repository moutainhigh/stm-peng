package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.CustomGroupResourceBo;

public interface ICustomResourceGroupApi {
	
	/**
	 * 新增用户自定义资源组
	 * 
	 * @param accountBo
	 * @return
	 */
	int insert(CustomGroupBo customGroupBo);
	
	
	/**
	 * 删除用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	int del(long id);
	
	/**
	 * 删除用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	int batchDel(long[] id);

	/**
	 * 修改用户自定义资源组
	 * 
	 * @param accountBo
	 * @return
	 */
	int update(CustomGroupBo customGroupBo);

	/**
	 * 获取单个用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	CustomGroupResourceBo get(long id);
	
	/**
	 * 获取单个用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	List<String> getCustomGroupInstanceIds(long groupId);

	/**
	 * 获取单个用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	CustomGroupBo getCustomGroup(long id);
	
	/**
	 * 获取所有用户自定义资源组
	 * 
	 * @return
	 */
	List<CustomGroupBo> getList(long userId);
	
	/**
	 * 从自定义资源组中移出资源
	 * @param CustomGroupBo
	 * @return
	 */
	int deleteResourceFromCustomGroup(CustomGroupBo gourpBo);
	
	/**
	 * 从数据库删除资源和自定义资源组的关系
	 * @param ids
	 * @return
	 */
	int deleteGroupAndResourceRelation(long[] ids);
	
	/**
	 * 新增资源与自定义资源组关系
	 * @return
	 */
	int insertGroupAndResourceRelation(long groupId, String resourceID);
	
	/**
	 * 批量移入资源组
	 */
	boolean insertIntoGroupForInstances(String instanceIds,String groupIds);
	
	/** 获取当前自定义资源组的SORT的最大值 */
	int getMaxSortByEntryId(long entryId);
	
	/** 更新资源组的排序信息 */
	int updateGroupSort(int sort, long groupId);
	
	/** 查询资源组的子资源组信息 */
	List<CustomGroupBo> getChildGroupsById(Long entryId, Long parentGroupId);
}
