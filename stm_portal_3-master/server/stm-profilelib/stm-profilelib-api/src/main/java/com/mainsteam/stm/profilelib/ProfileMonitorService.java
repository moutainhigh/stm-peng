package com.mainsteam.stm.profilelib;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;

/**
 * 策略服务接口 <br/>
 * 设计原则：<br/>
 * 1、对外不暴露出厂策略（无法查询修改和删除） <br/>
 * 2、不修改策略的全对象，对于监控频度、指标阈值是单独修改的<br/>
 * 3、对于想不清楚的业务场景，暂不提供接口
 * 
 * <p>
 * Create on : 2014-6-17<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author sunsht <br>
 * @version profilelib-api v4.1
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public interface ProfileMonitorService {

	/**
	 * <code>S_SERVICE_ID</code> - Service Bean ID.
	 */
	String S_SERVICE_ID = "ProfileService";

	/**
	 * =================================1、监控===============================
	 */

	/**
	 * 从模型文件(resourceId)生成一个策略<br/>
	 * 1：如果默认策略已经存在，只是加入绑定关系，否则创建默认策略，再加入绑定关系<br/>
	 * 2：用默认策略监控某个资源，其所有子资源也会被监控<br/>
	 * 3：如果加入该资源的实例在其他策略里边，会先删除之前的绑定关系。再加入默认策略。
	 * 4：如果当前资源已经在默认策略中监控，调用该方法会抛异常
	 * 
	 * @param  parentInstanceId 父资源实例id
	 * @throws ProfilelibException
	 */
	long addMonitorUseDefault(long parentInstanceId) throws ProfilelibException;

	/**
	 * 改方法是批量操作
	 * 从模型文件(resourceId)生成一个策略<br/>
	 * 1：如果默认策略已经存在，只是加入绑定关系，否则创建默认策略，再加入绑定关系<br/>
	 * 2：用默认策略监控某个资源，其所有子资源也会被监控<br/>
	 * 3：如果加入该资源的实例在其他策略里边，会先删除之前的绑定关系。再加入默认策略。
	 * 4：如果当前资源已经在默认策略中监控，调用该方法会抛异常
	 * 
	 * @param  parentInstanceId 父资源实例id
	 * @throws ProfilelibException
	 */
	Map<Long, Long> addMonitorUseDefault(List<Long> parentInstanceIds) throws ProfilelibException;

	/**
	 * 改方法用于发现资源完成后，用户指定部分资源需要添加到监控。
	 * 从模型文件(resourceId)生成一个策略<br/>
	 * 1：如果默认策略已经存在，只是加入绑定关系，否则创建默认策略，再加入绑定关系<br/>
	 * 2：用默认策略监控某个资源，父实例加入监控，其传入的childrenInstanceId参数里边的子资源也会被监控<br/>
	 * 3：如果加入该资源的实例在其他策略里边，会先删除之前的绑定关系。再加入默认策略。
	 * @param  parentInstanceId 父资源实例id
	 * @param  childrenInstanceId 子资源实例id
	 * @throws ProfilelibException
	 */
	void addMonitorUseDefault(long parentInstanceId,List<Long> childrenInstanceId) throws ProfilelibException;

	/**
	 * 自定义策略已经创建好情况下，把资源加入自定义策略监控
	 * 1:删除实例之前在策略中的关系 
	 * 2：其所有的子实例也会加入到监控
	 * @param profileId 自定义策略ID
	 * @param parentInstanceId 父实例Id
	 * @throws ProfilelibException
	 */
	void addMonitorUseSpecial(long profileId,long parentInstanceId) throws ProfilelibException;


	/**
	 * 创建个性化策略监控，传入参数为准
	 * @param   proflie 个性化策略信息
	 * @throws  ProfilelibException
	 */
	long createPersonalizeProfile(Profile proflie) throws ProfilelibException;
	
	/**
	 * 创建个性化策略监控
	 * @param  parentResourceId
	 * @param  parnetInstanceId
	 * @return
	 * @throws ProfilelibException
	 */
	Profile getEmptyPersonalizeProfile(String parentResourceId,long parnetInstanceId) throws ProfilelibException;
	
	/**
	 * 把资源加入个性化策略
	 * @param   parentProfileId  个性化父策略Id
	 * @param   parentInstanceId  父实例Id
	 * @throws  ProfilelibException
	 */
	void addMonitorUsePersonalize(long parentProfileId,long parentInstanceId) throws ProfilelibException;

	/**
	 * 针对一个策略添加一个子策略(createChildProfile由addChildProfile修改而来)
	 * @param parentProfileId  父策略id
	 * @param childProfileInfo 子策略信息
	 * @return 子策略Id
	 */
	long createChildProfile(long parentProfileId,ProfileInfo childProfileInfo) throws ProfilelibException;

	/**
	 * 该方法只能添加自定义父策略
	 * 
	 * @param profileInfo
	 *            父策略信息
	 * @param childProfileInfo
	 *            子策略信息
	 */
	long createSpecialProfile(ProfileInfo parentProfileInfo,
			List<ProfileInfo> childProfileInfo) throws ProfilelibException;

	/**
	 * 开启监控 
	 * 1：父与子实例一起监控
	 * 2：查找改设备上一次策略。如果没有，使用默认策略
	 * @param parentInstanceIds 父实例Id
	 */
	void enableMonitor(List<Long> parentInstanceIds) throws ProfilelibException;
	/**
	 * 链路开启监控 
	 * 2：查找改设备上一次策略。如果没有，使用默认策略
	 * @param parentInstanceIds 父实例Id
	 */
	void enableMonitorForLink(List<Long> parentInstanceIds) throws ProfilelibException;

	/**
	 * 开启监控 
	 * 1：父与子实例一起监控
	 * 2：查找改设备上一次策略。如果没有，使用默认策略
	 * @param parentInstanceIds 父实例Id
	 */
	void enableMonitor(long parentInstanceId) throws ProfilelibException;

	/**
	 * 取消监控<br/>
	 * 业务场景：选中一些主资源，取消监控<br/>
	 * 1：父与子一起取消监控
	 * @param parentInstanceIds 父实例Id
	 * @throws ProfilelibException
	 */
	void cancleMonitor(List<Long> parentInstanceIds) throws ProfilelibException;

	/**
	 * 取消监控<br/>
	 * 业务场景：选中一些主资源，取消监控<br/>
	 * 1：父与子一起取消监控
	 * @param parentInstanceIds 父实例Id
	 * @throws ProfilelibException
	 */
	void cancleMonitor(long parentInstanceId) throws ProfilelibException;

	/**
	 * 复制一个策略(指标复制，实例关系不复制)
	 * @param copyProfileId  复制的策略Id
	 * @param profileInfo    复制策略信息
	 * @return 复制后的策略信息
	 */
	Profile copyProfile(long copyProfileId, ProfileInfo profileInfo) throws ProfilelibException;

	/**
	 * 删除策略的监控设备 
	 * 1： 如果是父策略，对应的监控的父与子绑定关系一起删除
	 * 2： 如果是子策略，只删除子实例绑定关系
	 * @param profileId    策略id
	 * @param instanceIds  取消的实例Id
	 * @throws ProfilelibException
	 */
	void removeInstancesFromProfile(long profileId, List<Long> instanceIds)throws ProfilelibException;
	
	/**
	 * =================================2、查询=============================== *
	 */

	/**
	 * 获取所有默认策略 <br/>
	 * 业务场景：前台展示策略列表（默认，自定义，个性化策略）
	 * 
	 * @param ProfileTypeEnum
	 *            策略类型
	 * @return 策略基础信息
	 */
	List<ProfileInfo> getParentProfileBasicInfoByType(ProfileTypeEnum profileType)
			throws ProfilelibException;

	/**
	 * 取策所有的父策略信息
	 * 
	 * @return 策略信息
	 */
	List<ProfileInfo> getParentProfileBasicInfo() throws ProfilelibException;

	/**
	 * 取策所有策略信息
	 * 
	 * @param resourceIds
	 *            模型ID
	 * @param profileType
	 *            策略类型
	 * @return 策略信息
	 */
	List<ProfileInfo> getProfileBasicInfoByResourceId(
			List<String> resourceIds, ProfileTypeEnum profileType)
			throws ProfilelibException;
	
	
	/**
	 * 根据资源实例,指标Id 获取指标信息
	 * @param resourceInstanceId 资源实例ID
	 * @param metricId           指标ID
	 * @return                   策略指标
	 */
	ProfileMetric getMetricByInstanceIdAndMetricId(long resourceInstanceId,String metricId) throws ProfilelibException;
	
	/**
	 * 根据资源实例,获取指标信息
	 * @param   resourceInstanceId
	 * @return  策略指标
	 */
	List<ProfileMetric> getMetricByInstanceId(long resourceInstanceId) throws ProfilelibException;
	
	/**
	 * 根据策略类型返回所有的策略信息
	 * @param profileType  如果该值为null ,查询所有策略的信息
	 * @return 策略信息
	 */
	List<ProfileInfo> getAllProfileBasicInfo(ProfileTypeEnum profileType) throws ProfilelibException;
	/**
	 * 资源模型ID取所有策略信息
	 * @param resourceIds 资源模型ID
	 * @return 策略基本信息 
	 * @throws ProfilelibException
	 */
	List<ProfileInfo> getProfileBasicInfoByResourceIds(List<String> resourceIds) throws ProfilelibException;
	
	/**
	 * 取策所有策略信息
	 * @param resourceId 资源ID
	 * @return 策略基本信息
	 * @throws ProfilelibException
	 */
	ProfileInfo getProfileBasicInfoByResourceId(String resourceId) throws ProfilelibException;
	
	/**
	 * 取策所有策略信息
	 * @param proflieId 策略ID
	 * @return 策略基本信息
	 * @throws ProfilelibException
	 */
	ProfileInfo getProfileBasicInfoByProfileId(long proflieId) throws ProfilelibException;
	/**
	 * 取策资源实例所在的策略信息
	 * 
	 * @param resourceInstanceIds
	 *            资源实例id列表
	 * @return Map<Long,ProfileInfo> key:resourceInstanceId
	 */
	Map<Long, ProfileInfo> getBasicInfoByResourceInstanceIds(
			List<Long> resourceInstanceIds) throws ProfilelibException;
	
	/**
	 * 通过资源实例绑定的个性策略策略信息
	 * @param resourceInstanceId 资源实例Id
	 * @return
	 */
	ProfileInfo getPersonalizeProfileBasicInfoByResourceInstanceId(long resourceInstanceId) throws ProfilelibException;
	/**
	 * 取策略绑定实例信息
	 * @param profileId 策略Id
	 * @return List<Long> 资源实例Id
	 */
	List<Long> getResourceInstanceByProfileId(long profileId) throws ProfilelibException;
	
	/**
	 * 取策略实例信息
	 * @param profileIds 策略Id
	 * @return List<Long> 资源实例Id
	 */
	List<Long> getResourceInstanceByProfileIds(List<Long> profileIds) throws ProfilelibException;
	
	/**
	 * 取策资源实例所在的策略信息
	 * @param resourceInstanceId 资源实例id列表
	 * @return 策略信息
	 */
	ProfileInfo getBasicInfoByResourceInstanceId(long resourceInstanceId) throws ProfilelibException;

	/**
	 * 通过策略id，获取指标信息
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	List<ProfileMetric> getMetricByProfileId(long profileId) throws ProfilelibException;
	/**
	 * 通过策略id，指标Id 获取指标信息
	 * @param profileId 策略id
	 * @param metricId 指标id
	 * @return 指标
	 * @throws ProfilelibException
	 */
	ProfileMetric getMetricByProfileIdAndMetricId(long profileId,String metricId) throws ProfilelibException;
	/**
	 * 通过策略id,指标Id 获取阈值信息
	 * @param profileId 策略id
	 * @param metricId 指标id
	 * @return 指标阈值
	 * @throws ProfilelibException
	 */
	List<ProfileThreshold> getThresholdByProfileIdAndMetricId(long profileId,String metricId) throws ProfilelibException;
	
	/**
	 * 通过资源实例id,指标Id 获取阈值信息
	 * @param instanceId 资源实例id
	 * @param metricId 指标id
	 * @return 指标阈值
	 * @throws ProfilelibException
	 */
	List<ProfileThreshold> getThresholdByInstanceIdAndMetricId(long instanceId,String metricId) throws ProfilelibException;
	
	/**
	 * 根据策略ID获取当前策略<br/>
	 * 业务场景：根据该资源实例获取策略来展示和修改
	 * 
	 * @param profileId
	 * @return Profile 策略所有的信息
	 * @throws ProfilelibException
	 */
	Profile getProfilesById(long profileId) throws ProfilelibException;

	/**
	 * =================================3、删除===============================
	 */

	/**
	 * 根据策略ID删除策略，删除了策略的所有相关表的信息 删除自定义策略,以及个性化策略
	 * @param profileId     策略ID
	 * @throws ProfilelibException
	 */
	void removeProfileById(long profileId) throws ProfilelibException;

	/**
	 * 批量删除策略，删除了策略的所有相关表的信息 删除自定义或者个性化策略
	 * 
	 * @param profileId
	 *            策略ID
	 * @throws ProfilelibException
	 */
	void removeProfileByIds(List<Long> profileIds) throws ProfilelibException;
	
	/**
	 * =================================4、修改===============================
	 */

	/**
	 * 修改策略指标的监控频度 <br/>
	 * 业务场景：修改策略指标的监控频度
	 * 
	 * @param prlfileId
	 *            策略Id
	 * @param frequencyValue
	 *            key：metricId指标Id value:频度值
	 * @throws ProfilelibException
	 */
	void updateProfileMetricFrequency(long profileId,
			Map<String, String> frequencyValue) throws ProfilelibException;

	/**
	 * 修改策略的阈值 <br/>
	 * 业务场景：修改策略的阈值
	 * 
	 * @param prlfileId
	 *            策略Id
	 * @param threshold
	 *            key:metricId指标Id value:阈值
	 * @throws ProfilelibException
	 */
	void updateProfileMetricThreshold(long profileId,List<Threshold> thresholds) throws ProfilelibException;

	/**
	 * 修改策略的监控状态<br/>
	 * 
	 * @param prlfileId
	 *            策略Id
	 * @param monitor
	 *            key :metricId指标Id value:监控值
	 * @throws ProfilelibException
	 */
	void updateProfileMetricMonitor(long profileId, Map<String, Boolean> monitor)
			throws ProfilelibException;

	/**
	 * 修改策略的告警状态<br/>
	 * 
	 * @param prlfileId
	 *            策略Id
	 * @param alarms
	 *            key:metricId指标Id value:告警值
	 * @throws ProfilelibException
	 */
	void updateProfileMetricAlarm(long profileId, Map<String, Boolean> alarms)
			throws ProfilelibException;
	
	/**
	 * 修改策略的flapping<br/>
	 * 
	 * @param prlfileId
	 *            策略Id
	 * @param alarms
	 *            key:metricId指标Id value:flapping值
	 * @throws ProfilelibException
	 */
	void updateProfileMetricflapping(long profileId, Map<String, Integer> flappings)
			throws ProfilelibException;

	/**
	 * 添加策略绑定关系
	 * 1：参数策略是父策略。添加监控的是父实例。子实例不做绑定操作
	 * 2：参数策略是子策略。父实例必须是已经加入到监控。该操作只是子实例加入到监控。
	 * 
	 * @param parentProfileId
	 *            策略id
	 * @param instances
	 *            实例
	 */
	void addProfileInstance(long profileId, List<Long> instances)
			throws ProfilelibException;

	/**
	 * 修改策略绑定关系
	 * 1：参数策略是父策略。addInstances 参数添加监控的是父实例。子实例不做绑定操作。
	 *   deleteInstances 参数会把父与子的关系全部移除
	 * 2：参数策略是子策略。addInstances 参数父实例必须是已经加入到监控。该操作只是子实例加入到监控。
	 *   deleteInstances 参数 只是移除子实例关系
	 * @param parentProfileId
	 *            策略id
	 * @param addInstances
	 *           添加实例
	 * @param deleteInstances 
	 *           删除实例
	 */
	void operateProfileInstance(long profileId, List<Long> addInstances,List<Long> deleteInstances)
			throws ProfilelibException;
	
	/**
	 * 修改策略基础信息
	 * 
	 * @param profileInfo
	 */
	void updateProfileBaiscInfo(ProfileInfo profileInfo)
			throws ProfilelibException;
	
	/**
	 * 通过模型修改默认策略状态不可用
	 * @param resourceId
	 */
	void updateDefaultProfileStateByResourceId(List<String> resourceId,boolean isUse);

}
