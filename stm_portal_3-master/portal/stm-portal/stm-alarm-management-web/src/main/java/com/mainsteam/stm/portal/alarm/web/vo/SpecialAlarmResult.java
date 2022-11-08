package com.mainsteam.stm.portal.alarm.web.vo;

import java.util.List;




public class SpecialAlarmResult {
	private List<SpecialAlarmVo> resultList;
	private String queryState;
	private String queryMsg;
	
	public void setInfo(List<SpecialAlarmVo> resultList,String queryState,String queryMsg){
		this.queryState = queryState;
		this.queryMsg = queryMsg;
		this.resultList = resultList;
	}
	
	public List<SpecialAlarmVo> getResult() {
		return resultList;
	}
	public void setResult(List<SpecialAlarmVo> resultList) {
		this.resultList = resultList;
	}
	public String isQueryState() {
		return queryState;
	}
	public void setQueryState(String queryState) {
		this.queryState = queryState;
	}
	public String getQueryMsg() {
		return queryMsg;
	}
	public void setQueryMsg(String queryMsg) {
		this.queryMsg = queryMsg;
	}

	
}
