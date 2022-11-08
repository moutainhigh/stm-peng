package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
/**
 * <li>文件名称: IConfigDeviceDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public interface IConfigDeviceDao {
	
	ConfigDeviceBo get(Long id);
	
	List<ConfigDeviceBo> selectByPage(ConfigDeviceBo cdBo);
	
	int insert(ConfigDeviceBo configDeviceBo);
	
	int batchInsert(List<ConfigDeviceBo> list);

	int del(long id);
	
	int batchDelConfigDevice(Long[] ids);

	int update(ConfigDeviceBo configDeviceBo);
	/**
	 * 通过计划ID查询计划下面所有的资源
	 * @param planId
	 * @return
	 */
	List<ConfigDeviceBo> getByPlanId(Long planId);
	
	List<Long> getAllResourceIds();
	
	int deleteResourceByResourceIds(Long[] ids);
	/**
	 * 查询不是该计划的所有资源
	 * @param planId
	 * @return
	 */
	List<ConfigDeviceBo> getExcept(ConfigDeviceBo bo);
	/**
	 * 查询所有的已经导入的资源
	 * @return
	 */
	List<ConfigDeviceBo> getAllResources();
}
