package com.mainsteam.stm.topo.api;

import java.util.List;

import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


/**
 * <li>拓扑数据维护接口定义</li>
 * <li>文件名称: ITopoBackupRecoveryApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年4月14日
 * @author zwx
 */
public interface ITopoBackupRecoveryApi {
	
	/**
	 * 恢复文件数据
	 * @param fileId
	 */
	public void deleteAndRecovery(Long fileId) throws Exception;
	
	/**
	 * 删除已备份数据文件
	 * @param ids
	 * @return
	 */
	public void deleteFiles(Long[] ids)  throws Exception;
	
	/**
	 * 分页获取拓扑已备份的数据列表
	 * @return page
	 */
	public List<FileModel> getBackupFiles(Page<FileModel, FileModelQuery> page);
	
	/**
	 * 备份当前拓扑模块所有数据
	 */
	public void backup();
}
