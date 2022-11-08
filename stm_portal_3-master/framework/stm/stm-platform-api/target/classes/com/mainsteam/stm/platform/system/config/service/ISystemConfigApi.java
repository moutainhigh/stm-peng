package com.mainsteam.stm.platform.system.config.service;

import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;

/**
 * <li>文件名称: ISystemConfigApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日 下午8:13:25
 * @author   俊峰
 */
public interface ISystemConfigApi {

	int insertSystemConfig(SystemConfigBo config);
	
	SystemConfigBo getSystemConfigById(long id);
	
	int updateSystemConfig(SystemConfigBo config);
	
	void insertTransaction(long id);
	
	String getCurrentSkin();
}
