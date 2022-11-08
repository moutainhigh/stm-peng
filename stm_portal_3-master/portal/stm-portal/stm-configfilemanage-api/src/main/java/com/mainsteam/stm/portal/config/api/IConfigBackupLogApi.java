package com.mainsteam.stm.portal.config.api;

import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
/**
 * <li>文件名称: IConfigBackupLogApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public interface IConfigBackupLogApi {
	/**
	 * 备份历史分页数据
	 * @param page 分页对象
	 * @param resourceId 设备id
	 * @param all 是否查询全部的记录(true失败与成功的;false成功的)
	 * @param searchKey(搜索关键字,根据文件名称模糊查询)
	 * @return
	 */
	void selectByPage(Page<ConfigBackupLogBo, ConfigBackupLogBo> page,
			Long resourceId,boolean all,String searchKey) throws Exception;
	/**
	 * 新增
	 * @param bo
	 * @return
	 */
	int insert(ConfigBackupLogBo bo) throws Exception;
	/**
	 * 根据资源id，文件名查找最新的startup/running-config关联的文件服务器文件id
	 * @param map
	 * @return
	 */
	Long getNewlyFileIdById(Map<String, Object> map) throws Exception;
}
