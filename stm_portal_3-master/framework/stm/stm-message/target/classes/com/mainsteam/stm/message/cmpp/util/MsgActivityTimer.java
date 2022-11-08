package com.mainsteam.stm.message.cmpp.util;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <li>文件名称: AlarmInfo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口调用</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月18日
 * @author zhangjunfeng
 */
public class MsgActivityTimer {
	/**
	 * 短信接口长链接，定时进行链路检查
	 */
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		int count = 0;
		boolean result = MsgContainer.activityTestISMG();
		while (!result) {
			count++;
			result = MsgContainer.activityTestISMG();
			if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次链路检查次数超过两次则终止连接
				break;
			}
		}
	}
}
