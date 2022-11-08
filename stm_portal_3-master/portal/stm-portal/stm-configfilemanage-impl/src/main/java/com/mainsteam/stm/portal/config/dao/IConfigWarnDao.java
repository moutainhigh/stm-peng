package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigWarnBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnResourceBo;

/**
 * <li>文件名称: ICustomResourceGroupDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public interface IConfigWarnDao {
	/**
	 * 告警分页数据
	 * @param page
	 */
	void selectByPage(Page<ConfigWarnBo, ConfigWarnBo> page);
	
	/**
	 * 根据名字判断告警名字是否存在
	 * @param name
	 * @return
	 */
	int checkNameIsExsit(String name);
	
	/**
	 * 插入新的告警
	 * @param bo
	 * @return
	 */
	int insert(ConfigWarnBo bo);
	
	/**
	 * 批量插入告警关联的资源
	 * @param resources
	 * @return
	 */
	int batchInsertWarnResource(List<ConfigWarnResourceBo> resources);
	
	/**
	 * 根据id删除告警
	 * @param id
	 * @return
	 */
	int del(Long id);
	
	/**
	 * 根据告警id删除关联的设备资源
	 * @param id
	 * @return
	 */
	int delWarnResourcesById(Long id);
	
	/**
	 * 更新告警
	 * @param bo
	 * @return
	 */
	int update(ConfigWarnBo bo);
	
	/**
	 * 根据告警id获取告警关联的设备资源
	 * @param id
	 * @return
	 */
	List<ConfigWarnResourceBo> getWarnResourcesById(Long id);
	
	/**
	 * 根据告警ids数据批量删除告警
	 * @param ids
	 * @return
	 */
	int batchDelConfigWarn(Long[] ids);
	
	/**
	 * 根据告警ids数据批量删除关联的设备资源
	 * @param ids
	 * @return
	 */
	int batchDelConfigWarnResource(Long[] ids);
	
	/**
	 * 根据设备资源ids数据批量删除关联的设备资源
	 * @param ids
	 * @return
	 */
	int batchDelCWRByResourceIds(Long[] ids);
	
	/**
	 * 根据id获取告警
	 * @param id
	 * @return
	 */
	ConfigWarnBo get(Long id);
	/**
	 * 通过资源设备ID获取告警ID，设备对告警一对一，告警对资源一对多
	 * @param id
	 * @return
	 */
	Long getWarnIdByResourceId(Long id);
}
