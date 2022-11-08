package com.mainsteam.stm.system.um.login.api;

import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.login.bo.LoginUser;

/**
 * <li>文件名称: ILoginApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月25日
 * @author   ziwenwen
 */
public interface ILoginApi {
	
	/**
	 * 根据账号获取用户及权限域信息<br/>
	 * 支持普通、域管理员、超级管理员
	 * @param account
	 * @return
	 * @throws LicenseNotFoundException 
	 */
	void setLoginUserRight(LoginUser loginUser) throws LicenseNotFoundException, LicenseCheckException;

	LoginUser login(String account);
	
	/**
	 * 登出操作，目前使用的是tomcat自身的session机制，所以该方法始终返回true
	 * @param account
	 * @return
	 */
	boolean loginOut(String account);
}
