package com.mainsteam.stm.topo.api;


/**
 * <li>网络设备IP-MAC-PORT告警任务业务</li>
 * @version  ms.stm
 * @since  2019年12月13日
 * @author zwx
 */
public interface IIpMacAlarmTaskApi {
	
	/**
	 * 删除IP-MAC-PORT告警job任务（供测试使用）
	 */
	public void delIpMacAlarmJobTask();
	
	/**
	 * 开启IP-MAC-PORT告警job任务
	 */
	public void startIpMacAlarmJobTask();
	
	/**
	 * 刷新ip-mac-port数据
	 * @param groupId
	 * @return
	 */
	public void refreshIpMacPort(Integer groupId);
}
