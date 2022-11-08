package com.mainsteam.stm.portal.config.api;

import java.util.List;
import java.util.Set;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.system.um.login.bo.LoginUser;


/**
 * <li>文件名称: IConfigDeviceApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public interface IConfigDeviceApi {
	ConfigDeviceBo get(Long id);
	/**
	 * 设备一览分页
	 * @param page
	 */
	void selectByPage(Page<ConfigDeviceBo, ConfigDeviceBo> page,String groupId,String ipOrName,LoginUser user) throws Exception;
	/**
	 * 新增网络设备资源
	 * 
	 * @param configDeviceBo
	 * @return
	 */
	int insert(ConfigDeviceBo configDeviceBo);


	/**
	 * 删除网络设备资源
	 *      
	 * @param id
	 * @return
	 */
	int del(long id);

	/**
	 * 修改网络设备资源
	 * 
	 * @param configDeviceBo
	 * @return
	 * @throws Exception 
	 */
	int update(ConfigDeviceBo configDeviceBo) throws Exception;

	/**
	 * 批量删除网络设备资源
	 * 
	 * @param ids
	 * @return
	 */
	int batchDel(Long[] ids);

	/**
	 * 批量插入网络设备资源
	 * 
	 * @param ids
	 * @return
	 */
	int batchInsert(Long[] ids,String groupId);
	/**
	 * 得到不是该备份计划的设备资源
	 * @return
	 */
	List<ConfigDeviceBo> getDeviceExcept(Long id);
	/**
	 * 获取备份计划对应的设备集合
	 * @param id
	 * @return
	 */
	List<ConfigDeviceBo> getDeviceByPlanId(Long id);
	/**
	 * 查询除去已经添加进配置文件管理资源表中资源的资源
	 * @param searchKey 查询关键字(设备名称或ip地址)
	 */
	List<ResourceInstance> getAllExceptionResourceInstanceList(String searchKey) throws Exception;

	/**
	 * 读取文件返回行list<String>集合
	 * @param filePath
	 * @return
	 */
	List<String> readfilebylist(String filePath);

	/**
	 * 比较文件
	 * @param filePath1
	 * @param filePath2
	 * @return
	 */
	List<List<String>> comparecfgfile(String filePath1,String filePath2);

	/**
	 * 设备资源
	 * @param ids
	 * @param baupType 手动备份(true)；自动备份(false)
	 * @return 返回成功失败 成功返回"",失败原因
	 */
	String backupResourcesByIds(Long[] ids,boolean baupType);

	/**
	 * 获取全部的设备资源Id，用于全部手动备份时用
	 * @return
	 */
	List<Long> getAllResourceIds();

	/**
	 * 恢复配置文件
	 * @param filePath
	 * @param id
	 * @return
	 */
	String recoveryResources(String filePath,Long id);

}
