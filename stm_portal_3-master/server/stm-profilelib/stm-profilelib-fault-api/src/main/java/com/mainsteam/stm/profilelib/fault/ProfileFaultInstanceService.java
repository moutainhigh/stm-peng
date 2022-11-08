package com.mainsteam.stm.profilelib.fault;

import java.util.List;

import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;

/**
 * <li>文件名称: ProfileFaultInstanceService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 策略与资源实例关系服务</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月24日 下午3:57:15
 * @author   zhangjunfeng
 */
public interface ProfileFaultInstanceService {

	/**
	* @Title: removeInstanceByProfile
	* @Description: 通过策略ID删除所有策略与资源实例关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int removeInstanceByProfile(long profileId);
	
	/**
	* @Title: insertProfileFaultInstance
	* @Description: 批量添加策略与资源实例关系
	* @param profileFaultInstances
	* @return  int
	* @throws
	*/
	int insertProfileFaultInstance(List<ProfileFaultInstance> profileFaultInstances);
	
	/**
	* @Title: queryProfileInstanceByProfileId
	* @Description: 通过策略ID查询策略与资源实例关系
	* @param profileId
	* @return  int
	* @throws
	*/
	List<ProfileFaultInstance> queryProfileInstanceByProfileId(long profileId);
	
	/**
	* @Title: updateProfilefaultInstances
	* @Description: 更新策略与资源实例的关系
	* @param profileId
	* @param profileFaultInstances
	* @return  int
	* @throws
	*/
	int updateProfilefaultInstances(long profileId,List<ProfileFaultInstance> profileFaultInstances);
}
