package com.mainsteam.stm.platform.system.config.service;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * <li>文件名称: ISystemInitializeConfigApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since 2019年4月15日
 * @author pengl
 */
public interface ISystemInitializeConfigApi {
	
	/**
	 * 检查服务器的标示数据库文件是否存在，不存在则创建
	 */
	void checkLicenseIdentityFileIsExsit();
	
	
	void CopyFiel(File sourceFile, File targetFile) throws Exception;
	
}
