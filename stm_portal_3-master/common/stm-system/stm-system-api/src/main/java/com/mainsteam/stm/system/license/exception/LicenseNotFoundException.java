package com.mainsteam.stm.system.license.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * <li>文件名称: LicenseNotFoundException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月10日
 * @author   ziwenwen
 */
public class LicenseNotFoundException extends BaseException {
	
	public LicenseNotFoundException(Throwable cause) {
		super("授权文件不存在或已过期！", cause);
		this.code=606;
	}

	private static final long serialVersionUID = -8594976673776745776L;

}


