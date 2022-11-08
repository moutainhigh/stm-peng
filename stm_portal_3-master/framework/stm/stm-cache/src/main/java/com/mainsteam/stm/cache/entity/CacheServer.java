package com.mainsteam.stm.cache.entity;

public class CacheServer {
	/**
	 * 缓存服务器的ID
	 */
	private String ip;
	/**
	 * 缓存服务器的端口
	 */
	private String port;
	
	/**
	 * 构造缓存服务器
	 * @param ip 缓存服务器的ID
	 * @param port 缓存服务器的端口
	 */
	public CacheServer(String ip,String port){
		this.ip=ip;
		this.port=port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	@Override
	public String toString(){
		return ip+":"+port;
	}
	
}
