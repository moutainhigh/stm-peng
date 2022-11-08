package com.mainsteam.stm.cache.entity;

/**
 * <li>文件名称: MemcachedServer.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: Memcached Server 配置信息</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月19日
 * @author wangxinghao
 */
public class MemcachedServer {
	//定义server IP
	private String address;
	//定义server port
	private int port;
	//定义server weight,defult :1
	private int weight=1;
	
	/*
	 * 构造Bate
	 */
	public MemcachedServer(String address, int port, int weight) {
		this.address = address;
		this.port = port;
		this.weight = weight;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public String toString() {
		return address + ":" + port + "," + weight;
	}
	
}
