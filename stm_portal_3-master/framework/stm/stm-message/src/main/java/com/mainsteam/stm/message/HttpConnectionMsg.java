package com.mainsteam.stm.message;

public class HttpConnectionMsg {

	private String msg;
	private String msgCode;
	private boolean status;
	private int  statusCode;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
}
