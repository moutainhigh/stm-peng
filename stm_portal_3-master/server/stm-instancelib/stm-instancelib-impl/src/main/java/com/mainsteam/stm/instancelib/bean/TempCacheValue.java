package com.mainsteam.stm.instancelib.bean;

import java.io.Serializable;

public class TempCacheValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2431999506221173743L;

	private long instanceId;
	
	private String[] ip;
	
	private String[] mac;

	public String[] getIp() {
		return ip;
	}

	public String[] getMac() {
		return mac;
	}

	public void setIp(String[] ip) {
		this.ip = ip;
	}

	public void setMac(String[] mac) {
		this.mac = mac;
	}
	
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("cacheUnique:[instancelib=").append(instanceId);
		b.append("mac=[");
		if(mac != null){
			for (String macString : mac) {
				b.append(macString).append(" ");
			}
		}
		b.append("]");
		b.append("ip=[");
		if(ip != null){
			for (String ipString : ip) {
				b.append(ipString).append(" ");
			}
		}
		b.append("]");
		return b.toString();
	}
}
