package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;


/**
 * 监控策略指标阈值和频度DAO <br>
 * <p>
 * Create on : 2011-9-8<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author <br>
 * @version oc-profilelib-impl v1.0
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public interface ProfileThresholdDAO{

	/**
	 * 插入指标阈值.
	 * 
	 * @param thresholdPojo
	 *            阈值
	 * @throws Exception
	 *             DAO层异常
	 */

	public void insertThreshold(final ProfileThresholdPO thresholdPojo) throws Exception ;
	
	/**
	 * 批量插入指标阈值.
	 * 
	 * @param thresholdPojos
	 *            阈值
	 * @throws Exception
	 *             DAO层异常
	 */

	public void insertThresholds(final List<ProfileThresholdPO> thresholdPojos) throws Exception ;

	
	/**
	 * 根据策略ID删除指标阈值和频度.
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeThresholdByProfileId(final long profileId) throws Exception ;
	
	/**
	 * 根据策略ID删除指标阈值
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeThresholdByProfileIds(List<Long> profileIds) throws Exception;
	
	/**
	 * 根据基线ID删除指标阈值和频度.
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeThresholdByTimelineId(final long timelineId) throws Exception ;
	
	/**
	 * 根据基线ID删除指标阈值和频度.
	 * 
	 * @param timelineId
	 *            基线ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeThresholdByTimelineIds(final List<Long> timelineIds) throws Exception;

	/**
	 * 根据策略ID批量删除阈值.
	 * 
	 * @param profileIds
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeThresholdByProfileIdAndMetricId(List<Long> profileIds,String metricId) throws Exception;
	/**
	 * 更新某策略下的指标阈值和频度.
	 * 
	 * @param profileId
	 *            策略ID
	 * @param profileThresholdPojo
	 *            指标阈值和频度
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateThreshold(final ProfileThresholdPO profileThresholdPojo) throws Exception;
	
	/**
	 * 更新某策略下的指标阈值和频度.
	 * 
	 * @param profileId
	 *            策略ID
	 * @param profileThresholdPojos
	 *            指标阈值和频度
	 * @throws Exception
	 *             DAO层异常
	 */
	public void updateThresholds(final List<ProfileThresholdPO> profileThresholdPojos) throws Exception;
	
	/**
	 * 根据策略ID获取指标阈值.
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 指标阈值和频度
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileThresholdPO> getThresholdByProfileId(final long profileId) throws Exception;
	
	/**
	 * 根据策略ID获取指标阈值.
	 * 
	 * @param profileId
	 *            策略ID
	 * @return 指标阈值和频度
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileThresholdPO> getThresholdByProfileIdAndMetricId(final long profileId,final String metricId) throws Exception;

	/**
	 * 根据策略ID获取指标阈值.
	 * 
	 * @param timelineId
	 *            基线Id
	 * @return 指标阈值和频度
	 * @throws Exception
	 *             DAO层异常
	 */
	public List<ProfileThresholdPO> getThresholdByTimelineId(final long timelineId) throws Exception;
	
	/**
	 * 获取所有的阈值
	 * @return
	 * @throws Exception
	 */
	public List<ProfileThresholdPO> getAllThreshold() throws Exception;

}
