package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.ProfileBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.StrategyPageBo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;

/**
 * <li>文件名称: StrategyAllApi.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
public interface ProfileApi {
	/**
	 * 根据策略类型查询系统默认分页
	 * @param startRecord
	 * @param pageSize
	 * @param profileInfo
	 * @return
	 */
	StrategyPageBo getDefaultStrategyAll(long startRecord,long pageSize,ProfileInfo profileInfo,String sort,String order) throws Exception;

	
	/**
	 * 根据策略类型查询自定义策略分页
	 * @param startRecord
	 * @param pageSize
	 * @param profileInfo
	 * @return
	 */
	StrategyPageBo getCustomStrategyAll(long startRecord,long pageSize,ProfileInfo profileInfo,ILoginUser user,String sort,String order) throws Exception;
	
	
	/**
	 * 根据策略类型查询个性化策略分页
	 * @param startRecord
	 * @param pageSize
	 * @param profileInfo
	 * @return
	 */
	StrategyPageBo getPersonalizeStrategyAll(long startRecord,long pageSize,ProfileInfo profileInfo,ILoginUser user,String sort,String order) throws Exception;
	
	/**
	 * 根据策略ID获取策略对象
	 * @param profileId
	 * @return
	 */
	Profile getDefaultStrategy(long profileId) throws Exception;
	
	/**
	 * 复制系统默认策略
	 * @param profileInfo 策略
	 * @return
	 */
	int copyDefaultStrategy(Profile profile) throws Exception;
	
	/**
	 * 新增用户自定义策略
	 * @param profileInfo
	 * @return
	 */
	int insertCustomStrategy(Profile profile, String childProfileIds) throws Exception;
	
	/**
	 * 
	 * 删除用户自定义策略
	 * @param profileId
	 * @return
	 */
	int delCustomStrategy(long profileId) throws Exception;
	
	/**
	 * 批量删除用户自定义策略
	 * @param profileIds
	 * @return
	 */
	int batchDelCustomStrategy(long[] profileIds) throws Exception;
	
	/**
	 * 删除个性化策略
	 * @param profileId
	 * @return
	 */
	int delPersonalizeStrategy(long profileId) throws Exception;
	
	/**
	 * 批量删除个性化策略
	 * @param profileIds
	 * @return
	 */
	int batchDelPersonalizeStrategy(long[] profileIds) throws Exception;
	
	/**
	 * 通过资源ID 和 指标ID  获取  指标信息
	 * @param resourceId
	 * @param metricId
	 * @return
	 */
	public ResourceMetricDef getMetricInfo(String resourceId,String metricId);
	
	/**
	 * 通过策略ID  查询策略
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	public ProfileBo getProfile(long profileId) throws ProfilelibException;
	
	public List<ProfileMetricBo> getProfileMetrics(long profileId) throws ProfilelibException;
	
	/**
	 * 获取个性化策略的profile对象
	 */
	public ProfileBo getPersonalizeProfile(String resourceId,Long instanceId);
	
	public boolean customStrategyExist(Profile profile);
}
