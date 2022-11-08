package com.mainsteam.stm.dataprocess.bigData;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.AlarmSyncService;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.dataprocess.bigData.bo.AlarmDataLine;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AlarmEventSyncService implements AlarmSyncService {

	private static final Log logger=LogFactory.getLog(MetricDataProcessorSyncService.class);
	private static final int SEND_BY_SOCKET = 2;
	private static final int SEND_BY_KAFKA = 1;

	private ResourceInstanceService instanceService;
	private UdpSenderForBigData udpSender;

	public void setInstanceService(ResourceInstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public void setUdpSender(UdpSenderForBigData udpSender) {
		this.udpSender = udpSender;
	}

	@Override
	public void sync(AlarmSenderParamter paramter) {
		if(!udpSender.allowSync()){
			return;
		}

		switch (UdpSenderForBigData.getSendMethod()) {
			case SEND_BY_KAFKA:
				sendByKafka(paramter);
				break;
			case SEND_BY_SOCKET:
				sendBySocket(paramter);
				break;

		}

	}

	private void sendByKafka(AlarmSenderParamter paramter) {
		Map<String,Map<String,String>> messageMap = new HashMap<>(1);
		Map<String, String> properties = new HashMap<String, String>(11);
		messageMap.put("content", properties);
		properties.put("sourceType", "ITM");
		properties.put("sourceId", udpSender.getItbaSourceId());// 告警唯一标志
		properties.put("dataType", "KPIAlarm");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		properties.put("dateTime", format.format(paramter.getGenerateTime()));
		//告警等级：由1~N数字表示，1表示最高等级，随数字增大依次等级降低
		//properties.put("alarmLevel", String.valueOf(Math.abs(paramter.getLevel().getStateVal()-13)));
		properties.put("alarmLevel", paramter.getLevel() == InstanceStateEnum.CRITICAL ? "1" : (paramter.getLevel() == InstanceStateEnum.SERIOUS ?
			"2" : (paramter.getLevel() == InstanceStateEnum.WARN ? "3" : "4")));
		properties.put("isRecover", paramter.getLevel() == InstanceStateEnum.NORMAL ? "1" : "0");
		properties.put("alarmContent", paramter.getDefaultMsg());
		if(StringUtils.isNotBlank(paramter.getExt8()) && !StringUtils.equals(paramter.getExt8(), paramter.getSourceID())) {
			properties.put("ciId", paramter.getExt8());
			properties.put("subId", paramter.getSourceID());
		}else{
			properties.put("ciId", paramter.getSourceID());
			properties.put("subId", "");
		}
		properties.put("metricId", paramter.getExt3());
		properties.put("metricValue", "null");

		udpSender.sendMsg(JSON.toJSONString(messageMap));
	}

	private void sendBySocket(AlarmSenderParamter paramter) {
		try{
			AlarmDataLine adl=new AlarmDataLine();

			adl.setAlarmLevel(paramter.getLevel().name());
			adl.setAlarmContent(paramter.getDefaultMsg());
			adl.setMetricId(paramter.getExt3());
			adl.setResourceId(paramter.getExt0());
			adl.setDateTime(paramter.getGenerateTime());

			ResourceInstance ins=instanceService.getResourceInstance(Long.valueOf(paramter.getSourceID()));
			if(ins==null || ins.getParentId()<1){
				adl.setInstanceId(paramter.getSourceID());
				adl.setInstanceIp(paramter.getSourceIP());
				adl.setInstanceName(paramter.getSourceName());
			}
			else{
				ResourceInstance pins=ins.getParentInstance();

				adl.setInstanceId(String.valueOf(pins.getId()));
				adl.setInstanceIp(paramter.getSourceIP());
				adl.setInstanceName(paramter.getSourceName());

				adl.setSubInstanceId(String.valueOf(ins.getId()));
				adl.setSubInstanceName(ins.getShowName()!=null?ins.getShowName():ins.getName());
			}


			String msg=JSON.toJSONString(adl);
			if(logger.isTraceEnabled())
				logger.trace(msg);
			udpSender.sendMsg(msg.getBytes("UTF-8"),UdpSenderForBigData.SYNC_DATA_ALARM);
		}catch(Exception e){
			if(logger.isErrorEnabled())
				logger.error(e.getMessage(),e);
		}
	}

}
