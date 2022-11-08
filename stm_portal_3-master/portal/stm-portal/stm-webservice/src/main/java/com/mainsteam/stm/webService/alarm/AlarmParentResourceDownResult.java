package com.mainsteam.stm.webService.alarm;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AlarmParentResourceDownResult")
public class AlarmParentResourceDownResult {
	@XmlElement(name = "result", required = true)
	private List<AlarmParentResourceDownResultBean> result;
	@XmlElement(name = "queryState", required = true)
	private String queryState;
	@XmlElement(name = "msg", required = true)
	private String msg;
	
	public void setInfo(List<AlarmParentResourceDownResultBean> result,String queryState,String msg){
		this.result = result;
		this.queryState = queryState;
		this.msg = msg;
	}
	
	public List<AlarmParentResourceDownResultBean> getResult() {
		return result;
	}
	public void setResult(List<AlarmParentResourceDownResultBean> result) {
		this.result = result;
	}
	public String isQueryState() {
		return queryState;
	}
	public void setQueryState(String queryState) {
		this.queryState = queryState;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getQueryState() {
		return queryState;
	}

	
}
