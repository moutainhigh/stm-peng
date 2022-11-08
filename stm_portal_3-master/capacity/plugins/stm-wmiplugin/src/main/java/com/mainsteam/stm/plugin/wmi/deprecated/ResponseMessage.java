package com.mainsteam.stm.plugin.wmi.deprecated;

import java.util.ArrayList;

/**
 * 查询返回数据包
 */
public class ResponseMessage {

	/**
	 * 源端发送给本程序的查询命令，本程序原样返回
	 */
	private RequestMessage wmi_query_req;

	/**
	 * wmi_query_data中，结果的数量，即wmi_query_data数组变量个数
	 */
	private int wmi_query_count;

	/**
	 * 查询结果数据
	 */
	private java.util.ArrayList<ArrayList<ResponseCell>> wmi_query_data;

	/**
	 * 查询的错误码。0表示查询成功，其他表示错误
	 */
	private int wmi_query_err;

	/**
	 * 查询错误码对应的错误消息，可能为空
	 */
	private String wmi_query_msg;

	public RequestMessage getWmi_query_req() {
		return wmi_query_req;
	}

	public void setWmi_query_req(RequestMessage wmi_query_req) {
		this.wmi_query_req = wmi_query_req;
	}

	public int getWmi_query_count() {
		return wmi_query_count;
	}

	public void setWmi_query_count(int wmi_query_count) {
		this.wmi_query_count = wmi_query_count;
	}

	public java.util.ArrayList<ArrayList<ResponseCell>> getWmi_query_data() {
		return wmi_query_data;
	}

	public void setWmi_query_data(
			java.util.ArrayList<ArrayList<ResponseCell>> wmi_query_data) {
		this.wmi_query_data = wmi_query_data;
	}

	public int getWmi_query_err() {
		return wmi_query_err;
	}

	public void setWmi_query_err(int wmi_query_err) {
		this.wmi_query_err = wmi_query_err;
	}

	public String getWmi_query_msg() {
		return wmi_query_msg;
	}

	public void setWmi_query_msg(String wmi_query_msg) {
		this.wmi_query_msg = wmi_query_msg;
	}

}
