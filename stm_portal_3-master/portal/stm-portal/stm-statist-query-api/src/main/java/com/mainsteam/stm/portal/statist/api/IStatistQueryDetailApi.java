package com.mainsteam.stm.portal.statist.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceCategoryBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceInstanceBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceMetricBo;

public interface IStatistQueryDetailApi {
	
	/**
	 * 查询所有资源类别
	 * @return
	 */
	public List<StatistResourceCategoryBo> getAllResourceCategory();
	/**
	 * 根据资源类别获取子资源
	 */
	public List<StatistResourceCategoryBo> getChildResourceByResourceCategory(String categoryId);
	/**
	 * 根据主资源获取子资源
	 */
	public List<StatistResourceCategoryBo> getChildResourceByMainResource(String resourceId);
	/**
	 * 根据资源类别ID获取主资源实例
	 */
	public List<StatistResourceInstanceBo> getResourceInstanceByCategoryId(String categoryId, Long domainId, ILoginUser user);
	/**
	 * 根据资源获取资源实例
	 */
	public List<StatistResourceInstanceBo> getInstanceByResource(String resourceId, Long domainId, ILoginUser user);
	/**
	 * 根据资源ID查询指标列表
	 */
	public List<StatistResourceMetricBo> getMetricListByResource(List<String> resourceIdList,long instanceId, String statQType);
	
	/**
	 * statQMain名称是否重复
	 * @param statQMainBo
	 * @return
	 */
	public boolean isExistsStatQMainName(StatistQueryMainBo statQMainBo);
	
	public int insertStatQMain(StatistQueryMainBo statQMainBo, Long userId);
	
	public List<StatistQueryMainBo> getAllSQMain(ILoginUser user);
	
	public StatistQueryMainBo getSQMainByStatQId(Long statQId);
	
	public int delSQMainByStatQId(Long statQMainId);
	
	public int updateStatQMain(StatistQueryMainBo statQMainBo, Long userId);
}
