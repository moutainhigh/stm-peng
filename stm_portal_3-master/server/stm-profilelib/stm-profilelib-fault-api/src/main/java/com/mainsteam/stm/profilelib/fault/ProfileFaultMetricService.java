package com.mainsteam.stm.profilelib.fault;

import java.util.List;

import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;

/**
 * <li>文件名称: ProfileFaultMetricService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 策略与指标关系服务</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月24日 下午3:57:15
 * @author   zhangjunfeng
 */
public interface ProfileFaultMetricService {

	/**
	* @Title: removeMetricByProfile
	* @Description: 通过策略ID删除所有策略与指标关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int removeMetricByProfile(long profileId);
	
	/**
	* @Title: insertProfileFaultMetric
	* @Description: 批量添加策略与指标关系
	* @param profileFaultMetrics
	* @return  int
	* @throws
	*/
	int insertProfileFaultMetric(List<ProfileFaultMetric> profileFaultMetrics);
	
	/**
	* @Title: queryProfileMetricByProfileId
	* @Description: 通过策略ID查询策略与指标关系
	* @param profileId
	* @return  int
	* @throws
	*/
	List<ProfileFaultMetric> queryProfileMetricByProfileId(long profileId);
	
	/**
	* @Title: updateProfileFaultMetrics
	* @Description: 更新策略与指标关系
	* @param profileId
	* @param profileFaultMetrics
	* @return  int
	* @throws
	*/
	int updateProfileFaultMetrics(long profileId,List<ProfileFaultMetric> profileFaultMetrics);
}
