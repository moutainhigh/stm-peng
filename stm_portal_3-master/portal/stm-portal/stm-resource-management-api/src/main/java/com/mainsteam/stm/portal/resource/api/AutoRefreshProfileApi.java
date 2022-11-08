package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;


/**
 * <li>文件名称: ResourceCategoryApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年4月21日
 * @author   pengl
 */
public interface AutoRefreshProfileApi {
	
	/**
	 * 获取自动刷新策略列表
	 * @return
	 */
	List<ProfilelibAutoRediscover> getAutoProfileList(ILoginUser user);
	
	/**
	 * 根据资源模型类别获取资源实例
	 * @param categoryId
	 * @param state
	 * @param domainId
	 * @param searchContent
	 * @param user
	 * @return
	 */
	List<ResourceInstanceBo> getResourceInstancesByCategory(String categoryId, Long domainId,String searchContent, ILoginUser user);
	
	/**
	 * 添加一个刷新策略的基本信息
	 * @param rediscover
	 * @return
	 */
	long saveAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover);
	
	/**
	 * 更新一个刷新策略的基本信息
	 * @param rediscover
	 * @return
	 */
	long updateAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover);
	
	/**
	 * 更新策略的使用状态
	 * @param profileId
	 * @return
	 */
	boolean updateUseStatus(long userId,long profileId);
	

	/**
	 * 获取策略详情
	 * @param id
	 * @return
	 */
	ProfilelibAutoRediscover getProfilelibDetail(long id);
	
	/**
	 * 修改策略的关联资源
	 * @param id
	 * @param instanceIds
	 * @return
	 */
	boolean updateProfileResouces(long id,List<Long> instanceIds);
	
	/**
	 * 删除刷新策略
	 * @param ids
	 * @return
	 */
	boolean removeProfile(List<Long> ids);
	
}
