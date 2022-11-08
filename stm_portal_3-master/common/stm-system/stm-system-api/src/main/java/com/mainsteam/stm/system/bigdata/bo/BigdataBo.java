package com.mainsteam.stm.system.bigdata.bo;

import java.io.Serializable;

/**
 * <li>文件名称: BigdataBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月26日
 * @author   ziwenwen
 */
public class BigdataBo implements Serializable{
	private static final long serialVersionUID = -4085951999286479076L;
	
	private boolean integrate;
	
	private String ip;
	
	private int port;
	
	private String datasources;
	
	private int udp;
	
	private int tcp;
	
	public int getUdp() {
		return udp;
	}
	public void setUdp(int udp) {
		this.udp = udp;
	}
	public int getTcp() {
		return tcp;
	}
	public void setTcp(int tcp) {
		this.tcp = tcp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getDatasources() {
		return datasources;
	}
	public void setDatasources(String datasources) {
		this.datasources = datasources;
	}
	public boolean isIntegrate() {
		return integrate;
	}
	public void setIntegrate(boolean integrate) {
		this.integrate = integrate;
	}
}


