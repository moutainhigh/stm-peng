/**
 * 
 */
package com.mainsteam.stm.route;

import com.mainsteam.stm.route.logic.LogicAppEnum;

/**
 * 远程连接管理
 * 
 * @author ziw
 * 
 */
public interface RouteEntrySupporter {
	/**
	 * 根据目的ip地址和端口，拿到下一跳
	 * 
	 * @param distIp
	 * @param distPort
	 * @return null:本地,RemoteEntry:下一跳
	 * @throws RouteConnectionException
	 *             无法找到下一跳
	 */
	public RouteEntry getNextIp(String distIp, int distPort,LogicAppEnum app);

	/**
	 * 获取当前节点ip设置信息
	 * 
	 * @return RouteEntry
	 */
	public RouteEntry getCurrentIp();
}
