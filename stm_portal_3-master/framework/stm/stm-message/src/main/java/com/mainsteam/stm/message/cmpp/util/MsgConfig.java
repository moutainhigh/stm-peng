package com.mainsteam.stm.message.cmpp.util;

import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;

/**
 * <li>文件名称: AlarmInfo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 用于获取短信接口配置参数</li> <li>
 * 其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月18日
 * @author zhangjunfeng
 */
public class MsgConfig {
	private static MsgSettingInfo setting = new MsgSettingManager().getMsgSetting();

	// public static String get(String key){
	// return resourceBundle.getString(key);
	// }

	/**
	 * 获取互联网短信网关IP
	 * 
	 * @return
	 */
	public static String getIsmgIp() {
		return setting.getMessageGatewayIp();
	}

	/**
	 * 获取互联网短信网关端口号
	 * 
	 * @return
	 */
	public static int getIsmgPort() {
		return Integer.valueOf(setting.getMessagePort());
	}

	/**
	 * 获取sp企业代码
	 * 
	 * @return
	 */
	public static String getSpId() {
		return setting.getMessageLoginAccount();
	}

	/**
	 * 获取sp下发短信号码
	 * 
	 * @return
	 */
	public static String getSpCode() {
		return setting.getSpCode();
	}

	/**
	 * 获取sp sharedSecret
	 * 
	 * @return
	 */
	public static String getSpSharedSecret() {
		return setting.getMessagePassword();
	}

	/**
	 * 获取链接的次数
	 * 
	 * @return
	 */
	public static int getConnectCount() {
		return Integer.parseInt(setting.getConnectCount());
	}

	/**
	 * 获取链接的超时时间
	 * 
	 * @return
	 */
	public static int getTimeOut() {
		return Integer.parseInt(setting.getTimeOut());
	}
}
