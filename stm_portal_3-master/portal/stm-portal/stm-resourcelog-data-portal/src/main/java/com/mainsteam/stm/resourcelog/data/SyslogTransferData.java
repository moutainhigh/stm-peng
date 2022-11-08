package com.mainsteam.stm.resourcelog.data;

import java.io.Serializable;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年11月30日 上午11:39:00
 * @version 1.0
 */
public class SyslogTransferData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4916917206897334412L;
	private String ip;
	private byte[] msg;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public byte[] getMsg() {
		return msg;
	}
	public void setMsg(byte[] msg) {
		this.msg = msg;
	}
}
