package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

/**
 * 资源策略指标DAO <br>
 * Create on : 2014-06-17<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author <br>
 * @version oc-profilelib-impl v4.1
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public interface ProfileMetricDAO {

	/**
	 * 单独插入一条指标.
	 * 
	 * @param profileMetric
	 *            策略指标
	 * @throws Exception
	 *             DAO层异常
	 */
	public void insertMetric(final ProfileMetricPO profileMetric) throws Exception;

	/**
	 * 批量插入指标.
	 * 
	 * @param profileId
	 *            策略ID
	 * @param metrics
	 *            指标
	 * @throws Exception
	 *             DAO层异常
	 */
	public void insertMetrics(final List<ProfileMetricPO> metrics) throws Exception;// final String
																// profileId,

	/**
	 * 根据策略ID删除指标.
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByProfileId(final long profileId) throws Exception;
	
	/**
	 * 根据基线ID删除指标.
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByTimelineId(final long timelineId) throws Exception;
	/**
	 * 根据基线ID删除指标.
	 * 
	 * @param timelineIds
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByTimelineIds(final List<Long> timelineIds) throws Exception;

	/**
	 * 根据策略ID批量删除指标.
	 * 
	 * @param profileIds
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByProfileIds(final List<Long> profileIds) throws Exception;

	/**
	 * 根据策略ID批量删除指标.
	 * 
	 * @param profileIds
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByProfileIdAndMetricId(List<Long> profileIds,String metricId) throws Exception;
	
	/**
	 * 单独更新一条指标.
	 * 
	 * @param profileMetric
	 *            指标对象
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateProfileMetric(final ProfileMetricPO profileMetric) throws Exception;
	
	/**
	 * 单独更新一条指标.
	 * 
	 * @param profileMetric
	 *            指标对象
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateTimelineMetric(final ProfileMetricPO profileMetric) throws Exception;

	/**
	 * 批量更新更新某策略下的指标
	 * 
	 * @param profileId
	 *            策略ID
	 * @param profileMetrics
	 *            指标列表
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateProfileMetrics(final List<ProfileMetricPO> profileMetrics) throws Exception;
																				
	/**
	 * 批量更新基线某策略下的指标
	 * 
	 * @param profileId
	 *            策略ID
	 * @param profileMetrics
	 *            指标列表
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateTimelineMetrics(final List<ProfileMetricPO> profileMetrics) throws Exception;
	/**
	 * 根据策略ID获取指标
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 指标
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileMetricPO> getMetricsByProfileId(final long profileId) throws Exception;
	
	/**
	 * 根据策略ID,基线Id,获取指标
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 指标
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileMetricPO> getMetricsByTimelineId(final long timelineId) throws Exception;
	
	/**
	 * 获取所有的策略
	 * @return
	 * @throws Exception
	 */
	public List<ProfileMetricPO> getAllMetric() throws Exception;

}
