package com.mainsteam.stm.portal.resource.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
/**
 * 资源发现接口文件
 *
 */
public interface IDiscoverResourceApi {
	/**
	 * 单资源发现
	 * @param paramter
	 * @param user
	 * @return
	 */
	public Map<String, Object> discoverResource(Map paramter, ILoginUser user);
	/**
	 * 批量资源发现
	 * @param file
	 * @param domain
	 * @param dcs
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> resourceBatchDiscover(MultipartFile file, String domain, String dcs, ILoginUser user);
	/**
	 * 获取批量发现的结果
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public File getBatchResultFile(ILoginUser user) throws Exception;
	/**
	 * 通过资源实例获取自定义策略
	 * status 2:超过License监控数量 1:加入成功 0:加入失败
	 * @param instanceId
	 * @return
	 */
	public List<Map<String, String>> getProfileByInstanceId(Long instanceId);
	/**
	 * 资源加入监控
	 * @param instanceId
	 * @param profileId
	 * @return
	 */
	public Map<String, String> addMonitor(Long resourceGroupId, String newInstanceName, Long mainInstanceId, Long[] childInstanceIds);
	/**
	 * 处理重复资源
	 * @param method
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> handleRepeatInstance(String method, ILoginUser user);
	
	/**
	 * 0 失败 1 成功 2 重复
	 * @param instanceId
	 * @param name
	 * @return
	 * @throws InstancelibException
	 */
	public int updateInstanceName(Long instanceId, String name);
	/**
	 * 更新发现参数 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	public int updateDiscoverParamter(Map paramter, long instanceId);
	/**
	 * 重新发现资源 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	public int reDiscover(Map paramter, long instanceId);
	
	/**
	 * 测试发现连接
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public int testDiscover(Map paramter, long instanceId);
	
	/**
	 * 显示名称是否重复
	 * @param instanceId
	 * @param showName
	 * @return
	 */
	public boolean isShowNameRepeat(long instanceId, String showName);
	/**
	 * 重命名显示名称
	 * @param instanceId
	 * @param showName
	 * @return
	 */
	public String reCreateShowName(Map<String, String> showNameMap, int repeatNum, String showName);
	/**
	 * 处理重新发现
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	
	public Map<String, Object> refreshDiscover(Map paramter,long instanceId,ILoginUser user);
	/**
	 * 自动刷新，加入监控
	 * @param newInstanceName
	 * @param mainInstanceId
	 * @param childInstanceIds
	 * @param delchildInstanceIdLong
	 * @param cancleInstanceIds
	 * @return
	 */
	public Map<String, String> refAddMonitor(String newInstanceName,
			Long mainInstanceId, Long[] childInstanceIds,Long[] delchildInstanceIdLong,Long[] cancleInstanceIds);
	/**
	 * 删除子资源
	 * @param instanceId
	 * @return
	 */
	public int delResourceInstance(Long instanceId);
}
