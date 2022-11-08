package com.mainsteam.stm.system.topo.api;

import java.util.List;


/**
 * IP-MAC-PORT告警API
 * @author zwx
 */
public interface IIpMacPortApi {
	
	/**
	 * 获取ip-mac-port基准表id列表
	 * @return
	 */
	List<Long> getMacBaseIds();
}


