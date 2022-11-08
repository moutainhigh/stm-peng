package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.NodeBo;

public interface ITopoFindHandler {
	/**
	 * 添加链路
	 * 1.已存在相同链路则更新接口信息，不存在则直接入库
	 * 2.入库后所有链路放入待实例化队列，等待实例化
	 * @param links
	 */
	void addLinks(JSONArray links);
	/**
	 * 添加节点设备
	 * 1.添加节点到资源库，返回instanceId
	 * 2.添加到拓扑图元表
	 * 3.添加到拓扑图元表
	 * 4.根据条件插入vlan、子网等数据
	 * @param node 包含节点信息的json对象
	 */
	void addNode(JSONObject node);
	/**
	 * 构建生成的消息
	 * @param msgs 包含消息信息的json对象
	 */
	void buildMessage(String msgs);
	/**
	 * 停止采集
	 * 本次发现结束-但是不意味着资源实例化结束
	 * @param isError 是否是异常中断
	 */
	void stop(boolean isError);
	/**
	 * 设置本次发现类型
	 * @param type
	 */
	void setType(String type);
	/**
	 * 重置消息收集器
	 */
	void resetMsg();
	/**
	 * 获取当前的拓扑发现收集的消息内容
	 * @return
	 */
	JSONObject getMsg(int index);
	/**
	 * 获取当前运行状态
	 * @param b
	 * @return
	 */
	boolean isRunning();
	/**
	 * 设置当前运行状态
	 * @param isRunning
	 */
	void setRunning(boolean isRunning);
	NodeBo getSingleFindNode();
	/**
	 * 获取类型
	 * @return
	 */
	String getType();
	/**
	 * 是否为全网发现
	 * @return
	 */
	boolean isWholeNetFind();
	
}
