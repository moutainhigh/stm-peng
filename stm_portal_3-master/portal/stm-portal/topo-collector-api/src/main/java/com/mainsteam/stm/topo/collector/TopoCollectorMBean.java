package com.mainsteam.stm.topo.collector;

import com.alibaba.fastjson.JSONObject;

public interface TopoCollectorMBean {
	/**
	 * 启动采集器
	 * @param 拓扑配置参数
	 * @return 启动状态
	 */
	String start(String cfg);
	/**
	 * 获取下一个发现信息
	 * @return null或者JSON
	 */
	JSONObject next();
	/**
	 * 采集器是否启动成
	 * @return
	 */
	boolean isRunning();
	/**
	 * 取消发现
	 * @return
	 */
	int stopDiscover();
}
