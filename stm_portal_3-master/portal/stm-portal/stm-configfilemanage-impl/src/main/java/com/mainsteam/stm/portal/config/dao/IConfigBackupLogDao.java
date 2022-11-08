package com.mainsteam.stm.portal.config.dao;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
/**
 * <li>文件名称: IConfigBackupLogDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public interface IConfigBackupLogDao {
	/**
	 * 备份历史分页数据
	 * @param page
	 */
	void selectByPage(Page<ConfigBackupLogBo, ConfigBackupLogBo> page);
	/**
	 * 新增备份日志历史记录
	 * @param bo
	 * @return
	 */
	int insert(ConfigBackupLogBo bo);
	/**
	 * 根据资源id，文件名查找最新的startup/running-config关联的文件服务器文件id
	 * @param map
	 * @return
	 */
	Long getNewlyFileIdById(Map<String, Object> map);
	/**
	 * 根据设备资源ids数据批量删除关联的资源的历史记录数据
	 * @param ids
	 * @return
	 */
	int batchDelCBLByResourceIds(Long[] ids);
	/**
	 * 根据resourseId查找备份记录
	 * @param id
	 * @return
	 */
	List<ConfigBackupLogBo> getConfigHistoryByResourceId(long id);
}
