package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
/**
 * 
 * <li>文件名称: IBackupPlanDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   liupeng
 */
public interface IBackupPlanDao {
	/**
	 * 通过ID查询计划实体
	 * @param id
	 * @return
	 */
	BackupPlanBo get(Long id);
	/**
	 * 通过计划名称查询备份计划
	 * @param name
	 * @return
	 */
	List<BackupPlanBo> getByName(String name);
	/**
	 * 通过主键ID获取实体
	 * @param bo
	 * @return
	 */
	int insert(BackupPlanBo bo);
	/**
	 * 通过id删除备份计划
	 * @param id
	 * @return
	 */
	int del(Long id);
	/**
	 * 修改备份计划
	 * @param bo
	 * @return
	 */
	int update(BackupPlanBo bo);
	/**
	 * 批量删除备份计划
	 * @param ids
	 * @return
	 */
	int batchDelPlan(long[] ids);
	/**
	 * 分页查询备份计划
	 * @param page
	 */
	void pageSelect(Page<BackupPlanBo, Object> page); 
	/**
	 * 更新资源的备份计划
	 * @param bo
	 * @return
	 */
	int upateDevicePlan(BackupPlanBo bo);
}
