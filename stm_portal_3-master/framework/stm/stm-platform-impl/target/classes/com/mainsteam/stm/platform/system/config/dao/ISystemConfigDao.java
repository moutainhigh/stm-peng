package com.mainsteam.stm.platform.system.config.dao;

import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;

/**
 * <li>文件名称: ISystemConfigDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日 下午8:13:28
 * @author   俊峰
 */
public interface ISystemConfigDao {

	SystemConfigBo get(long id);
	
	int insert(SystemConfigBo config);
	
	int update(SystemConfigBo config);
}
