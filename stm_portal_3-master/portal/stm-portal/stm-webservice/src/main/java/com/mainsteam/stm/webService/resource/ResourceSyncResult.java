package com.mainsteam.stm.webService.resource;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceSyncResult")
public class ResourceSyncResult {
	
	@XmlElement(name = "result", required = true)
	private List<ResultInstanceBean> result;
	
	@XmlElement(name = "msg", required = false)
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<ResultInstanceBean> getResult() {
		return result;
	}

	public void setResult(List<ResultInstanceBean> result) {
		this.result = result;
	}
	
}
