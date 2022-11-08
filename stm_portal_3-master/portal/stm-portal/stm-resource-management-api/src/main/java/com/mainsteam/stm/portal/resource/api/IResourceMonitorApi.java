package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorPageBo;


/**
 * <li>文件名称: IResourceMonitorApi.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月28日
 * @author xhf
 */
public interface IResourceMonitorApi {
	
	/**
	 * 开启监控 
	 * 2:超过License监控数量 1:加入成功 0:加入失败
	 * @param resourceMonitorId
	 * @return
	 */
	int openMonitor(long resourceMonitorId) throws Exception;
	
	/**
	 * 批量开启监控 
	 * @param resourceMonitorIds
	 * @return
	 */
	int batchOpenMonitor(String[] ids) throws Exception;
	
	/**
	 * 取消监控
	 * @return
	 */
	int closeMonitor(long resourceMonitorId) throws Exception;
	
	/**
	 * 批量取消监控
	 * @return
	 */
	int batchCloseMonitor(long[] resourceMonitorIds) throws Exception;
	
	
	/**
	 * 批量删除资源
	 * @param resourceMonitorId
	 * @return
	 */
	int batchDelResource(long[] resourceMonitorIds) throws Exception;
	/**
	 * 获取已监控列表数据
	 * @param user 当前登陆用户
	 * @param startRow 数据开始行
	 * @param pageSize 每页显示大小
	 * @param instanceStatus 资源实例状态
	 * @param ipOrShowName IP或显示名称
	 * @param domainId 域
	 * @param categoryId 类型
	 * @param parentCategoryId 父类型
	 * @param resourceIds 资源实例ID（多个用“,”分割）
	 * @param IsCustomResGroup 是否自定义资源组
	 * @return
	 */
	public ResourceMonitorPageBo getMonitored(ILoginUser user, long startRow,
			long pageSize, String instanceStatus, String ipOrShowName,
			Long domainId, String categoryId, String parentCategoryId,
			String resourceIds, String IsCustomResGroup,String sort,String order);
	/**
	 * 获取已监控列表数据
	 * @param user 当前登陆用户
	 * @param instanceStatus 资源实例状态
	 * @param ipOrShowName IP或显示名称
	 * @param domainId 域
	 * @param categoryId 类型
	 * @param parentCategoryId 父类型
	 * @param resourceIds 资源实例ID（多个用“,”分割）
	 * @param IsCustomResGroup 是否自定义资源组
	 * @return
	 */
	public ResourceMonitorPageBo getNewMonitored(ILoginUser user, String instanceStatus, 
			String ipOrShowName, Long domainId, String categoryId, String parentCategoryId,
			String resourceIds, String IsCustomResGroup,String sort,String order);
	
	/**
	 * 获取未监控列表数据
	 * @param user 当前登陆用户
	 * @param startRow 数据开始行
	 * @param pageSize 每页显示大小
	 * @param ipOrShowName IP或显示名称
	 * @param domainId 域
	 * @param categoryId 类型
	 * @param parentCategoryId 父类型
	 * @param resourceIds 资源实例ID（多个用“,”分割）
	 * @param IsCustomResGroup 是否自定义资源组
	 * @return
	 */
	public ResourceMonitorPageBo getUnMonitored(ILoginUser user, long startRow,
			long pageSize, String ipOrShowName, Long domainId,
			String categoryId, String parentCategoryId, String resourceIds,
			String IsCustomResGroup,String sort,String order);
	
	public List<Map<String, String>> getDiscoverParamter(long instanceId);
	
	/**
	 * 保存资源责任人
	 * @param instanceIds
	 * @param userIds
	 * @return
	 */
	public boolean saveLiablePerson(long[] instanceIds, String[] userIds);
	
	/**
	 * 清除资源责任人
	 * @param instanceIds
	 * @return
	 */
	public boolean clearLiablePerson(long[] instanceIds);
	
}
