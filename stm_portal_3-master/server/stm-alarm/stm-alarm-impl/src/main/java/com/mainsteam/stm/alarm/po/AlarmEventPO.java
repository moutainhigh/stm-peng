package com.mainsteam.stm.alarm.po;

import com.mainsteam.stm.alarm.obj.AlarmEventDetail;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.ItsmAlarmData;

public class AlarmEventPO extends AlarmEvent {
	private String itsmJsonData;
	private AlarmEventDetail eventDetail;

	public String getItsmJsonData() {
		if(!StringUtils.isEmpty(this.getItsmData())){
			itsmJsonData=JSON.toJSONString(this.getItsmData());
		}
		return itsmJsonData;
	}
	public void setItsmJsonData(String itsmJsonData) {
		try{
			this.setItsmData(JSON.parseObject(itsmJsonData, ItsmAlarmData.class));
		}catch(Exception e){}
		this.itsmJsonData = itsmJsonData;
	}

	public AlarmEventDetail getEventDetail() {
		return eventDetail;
	}

	public void setEventDetail(AlarmEventDetail eventDetail) {
		this.eventDetail = eventDetail;
	}
}
