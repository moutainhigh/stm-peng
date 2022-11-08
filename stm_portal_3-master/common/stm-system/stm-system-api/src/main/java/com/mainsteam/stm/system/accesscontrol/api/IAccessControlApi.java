package com.mainsteam.stm.system.accesscontrol.api;

import com.mainsteam.stm.system.accesscontrol.bo.AccessControl;

/**
 * <li>文件名称: AccessControl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 访问控制接口</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月5日
 * @author   zjf
 */
public interface IAccessControlApi {

	/**
	 * 获取所有访问控制的IP
	 * */
	AccessControl getAccessControlIP();
	
	/**
	 * 修改黑白名单列表
	 * @param accessControl
	 * */
	boolean updateAccessControlIp(AccessControl accessControl);
	
	
	/**
	 * 检查IP是否允许通过
	 * @param ip 要验证的IP
	 * @return 是否通过 true 允许通过;false 不允许通过
	 * */
	boolean checkIpIsAllow(String ip);
	
	/**
	 * 检查是否开启IP过虑
	 * */
	boolean isOpenIPFilter();
}
