package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryTreeBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotProfilePageBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceInstance;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceMetric;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;


public interface SnapshotProfileApi {
	/**
	 * 获取所有资源类别
	 */
	public List<ResourceCategoryTreeBo> getAllResourceCategory();
	/**
	 * 根据资源类别获取子资源
	 */
	public List<ResourceCategoryTreeBo> getChildResourceByResourceCategory(String categoryId);
	/**
	 * 根据主资源获取子资源
	 */
	public List<ResourceCategoryTreeBo> getChildResourceByMainResource(String resourceId);
	/**
	 * 根据资源类别ID获取主资源实例
	 */
	public List<SnapshotResourceInstance> getResourceInstanceByCategoryId(String categoryId,Long domainId,ILoginUser user);
	/**
	 * 根据资源获取资源实例
	 */
	public List<SnapshotResourceInstance> getInstanceByResource(String resourceId,Long domainId,ILoginUser user);	
	/**
	 * 根据资源ID查询指标列表
	 */
	public List<SnapshotResourceMetric> getMetricListByResource(Set<String> resourceIdList,Long[] instanceId);
	/**
	 * 获取快照脚本
	 */
	public List<ScriptManage> getSnapshotScript();
	/**
	 * 获取恢复脚本
	 */
	public List<ScriptManage> getResumScript();
	/**
	 * 新增快照脚本策略
	 */
	public boolean addSnapshotProfile(Profilefault pff, String instanceIds, String metricIds, ILoginUser user);
	/**
	 * 修改快照脚本策略
	 */
	public boolean editSnapshotProfile(Profilefault pff, String instanceIds, String metricIds, ILoginUser user);
	/**
	 * 查询所有策略
	 * @param spPBo
	 * @return
	 */
	public SnapshotProfilePageBo getAllSnapshotProfile(SnapshotProfilePageBo spPBo,ILoginUser user);
	/**
	 * 删除策略
	 * @param profileIds
	 * @return
	 */
	public boolean delSnapshotProfile(String profileIds);
	/**
	 * 更新策略是否启用
	 * @param profileId
	 * @return
	 */
	public boolean updateIsUse(String profileId);
	
	public Map<String, List<String>> getSnapshotProfileRelation(String profileId);
}
