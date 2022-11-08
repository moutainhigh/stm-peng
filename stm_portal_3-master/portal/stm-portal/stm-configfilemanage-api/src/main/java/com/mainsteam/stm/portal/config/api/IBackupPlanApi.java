package com.mainsteam.stm.portal.config.api;

import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;

/**
 * 
 * <li>文件名称: IBackupPlanApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   liupeng
 */
public interface IBackupPlanApi {
	/**
	 * 添加或者更新备份计划
	 * @param backupPlanBo
	 * @return
	 */
	BackupPlanBo addOrUpdatePlan(BackupPlanBo backupPlanBo);
	/**
	 * 更新资源的计划
	 * @param map
	 * @return
	 */
	int upateDevicePlan(BackupPlanBo bo);
	/**
	 * 批量删除计划
	 * @param ids
	 * @return 删除的行数
	 */
	int batchRemovePlan(long[] planIds);
	/**
	 * 根据ID查询计划
	 * @param id
	 * @return 
	 */
	BackupPlanBo queryPlanById(long id);
	/**
	 * 分页查询计划
	 * @param page
	 * @return
	 */
	void queryPlanByPager(Page<BackupPlanBo, Object> page);
	
}
