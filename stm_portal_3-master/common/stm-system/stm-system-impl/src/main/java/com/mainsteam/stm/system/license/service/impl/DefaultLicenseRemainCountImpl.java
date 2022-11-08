package com.mainsteam.stm.system.license.service.impl;

import com.mainsteam.stm.system.license.api.ILicenseRemainCountApi;

/**
 * <li>文件名称: DefaultLicenseRemainCountImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月8日
 * @author   ziwenwen
 */
public class DefaultLicenseRemainCountImpl extends ILicenseRemainCountApi {

	private String type;
	
	public DefaultLicenseRemainCountImpl(String type){
		this.type=type;
	}
	
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public int getUsed() {
		return 0;
	}

}


