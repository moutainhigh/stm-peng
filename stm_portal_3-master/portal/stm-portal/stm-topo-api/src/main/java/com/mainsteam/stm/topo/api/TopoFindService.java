package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONObject;

/**
 * 拓扑发现服务类
 * @author 富强
 *
 */
public interface TopoFindService {
	/**
	 * 通过拓扑发现类型去发现拓扑信息
	 * @param type 拓扑发现类型
	 * @return 采集器的启动状态
	 */
	String topoFind(String type);
	/**
	 * 发现结果信息
	 * @return
	 */
	JSONObject resultInfo(int index);
	/**
	 * 单资源发现
	 * @param param
	 * @return
	 */
	JSONObject singleTopoFind(JSONObject param);
	
	int cancelDiscover();
	
	void wholeNetFind(JSONObject params);
	void subnetFind(JSONObject params);

}
