package com.mainsteam.stm.portal.threed.bo;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class BaseResult implements Serializable{

	private static final long serialVersionUID = -9027794420662646157L;
	
	private boolean success;
	
	private String data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
