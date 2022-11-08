package com.mainsteam.stm.profilelib.fault;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profile.fault.execute.obj.FaultScriptExecuteResult;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;

/**
 * <li>文件名称: ProfileFaultService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 故障策略服务</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月24日 上午10:04:23
 * @author   zhangjunfeng
 */
public interface ProfileFaultService {

	/**
	* @Title: queryProfilefaults
	* @Description: 分页查询所有故障策略
	* @param page
	* @return  List<Profilefault>
	* @throws
	*/
	void queryProfilefaults(Page<Profilefault, Profilefault> page);
	
	/**
	* @Title: getProfileFaultRelationById
	* @Description: 通过策略ID查询策略详细信息，包括策略与资源实例、指标的关系
	* @param profileId
	* @return  ProfileFaultRelation
	* @throws
	*/
	ProfileFaultRelation getProfileFaultRelationById(long profileId);
	
	/**
	* @Title: insertProfileFault
	* @Description: 添加故障策略基本信息
	* @param profilefault
	* @return  long
	* @throws
	*/
	Profilefault insertProfileFault(Profilefault profilefault);
	
	/**
	* @Title: insertProfileFault
	* @Description: 添加故障策略（包含同时添加故障策略关联的资源实例，指标）
	* @param profilefault
	* @return  long
	* @throws
	*/
	ProfileFaultRelation insertProfileFault(ProfileFaultRelation profilefault);
	
	/**
	* @Title: removeprofileFault
	* @Description: 通过策略ID删除策略
	* @param profileId
	* @return  int
	* @throws
	*/
	int removeProfileFault(Long[] profileId);
	
	/**
	* @Title: updateProfilefault
	* @Description: 更新故障策略
	* @param profilefault
	* @return  int
	* @throws
	*/
	int updateProfilefault(Profilefault profilefault);
	
	/**
	* @Title: updateProfilefault
	* @Description: 更新故障策略及策略与资源实例及指标的关系
	* @param profilefault
	* @return  int
	* @throws
	*/
	int updateProfilefault(ProfileFaultRelation profilefault);
	
	/**
	* @Title: updateProfilefaultState
	* @Description: 更新策略状态，用于启用、禁用策略
	* @param profileId
	* @param state
	* @return  int
	* @throws
	*/
	int updateProfilefaultState(long profileId);
	
	/**
	* @Title: checkProfileFaultIsAlarmByInstanceAndMetric
	* @Description: 通过InstanceId和指标ID检查该资源和指标是否有创建故障策略，如果有策略并启用了策略则执行快照脚本和恢复脚本文件
	* @param instanceId
	* @param metricId
	* @return  String 快照脚本执行后，返回的快照文件内容
	* @throws
	*/
	FaultScriptExecuteResult checkProfileFaultIsAlarmByInstanceAndMetric(String instanceId,String metricId,InstanceStateEnum alarmLevel);
}
