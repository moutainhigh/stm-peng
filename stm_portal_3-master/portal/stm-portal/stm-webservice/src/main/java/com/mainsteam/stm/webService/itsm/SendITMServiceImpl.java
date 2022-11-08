package com.mainsteam.stm.webService.itsm;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.ItsmAlarmData;
import com.mainsteam.stm.alarm.obj.ItsmOrderStateEnum;

@WebService
public class SendITMServiceImpl implements SendITMService{
	private static final Logger logger=LoggerFactory.getLogger(SendITMServiceImpl.class);
	
	@Resource
	private AlarmEventService alarmEventService;
	@Override
	public void sendITM(SendITM sendITM) {
		
		logger.info("sendITM:alarmID="+sendITM.getAlarmID()+";status="+sendITM.getStatus());
		
		ItsmAlarmData itsmAlarmData=new ItsmAlarmData();
		itsmAlarmData.setAlarmEventID(Long.parseLong(sendITM.getAlarmID()));
		itsmAlarmData.setItsmOrderID(sendITM.getCaseID());
		switch(sendITM.getStatus()){
		case "0":
			itsmAlarmData.setState(ItsmOrderStateEnum.HANDLE);
			break;
		case "1":
			itsmAlarmData.setState(ItsmOrderStateEnum.FINISH);
			break;
			
		}
		
		ItsmResult irlt = new ItsmResult();
		try{
			alarmEventService.updateItsmOrderState(itsmAlarmData);
			
//			irlt.setMsg("success");
//			irlt.setResult(true);
//			return JSONObject.toJSONString(irlt);
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("itsm alarmEventService update error!---------"+e.getMessage(),e);
			}

//			irlt.setMsg("alarmEventService update error!");
//			irlt.setResult(false);
//			return JSONObject.toJSONString(irlt);
		}
		
	}

}
