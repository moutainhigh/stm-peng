package com.mainsteam.stm.profile.fault.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;

/**
 * <li>文件名称: ProfileFaultMetricDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月24日 下午3:30:49
 * @author   zhangjunfeng
 */
public interface ProfileFaultMetricDao {

	/**
	* @Title: deleteProfileFaultMetric
	* @Description: 删除所有策略与指标的关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int deleteProfileFaultMetric(long profileId);
	
	/**
	* @Title: batchInsertProfileFaultMetric
	* @Description: 批量添加策略与指标的关系
	* @param profileFaultMetrics
	* @return  int
	* @throws
	*/
	int batchInsertProfileFaultMetric(List<ProfileFaultMetric> profileFaultMetrics);
	
	/**
	* @Title: selectProfileFaultMetrics
	* @Description: 通过策略ID查询策略关联的所有指标
	* @param profileId
	* @return  List<ProfileFaultMetric>
	* @throws
	*/
	List<ProfileFaultMetric> selectProfileFaultMetrics(long profileId);
}
