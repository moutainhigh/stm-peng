package com.mainsteam.stm.alarm.soap.itsm;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.xml.ws.BindingProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mainsteam.stm.alarm.event.AlarmEventMonitor;
import com.mainsteam.stm.alarm.event.dao.AlarmEventDAO;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.ItsmAlarmData;
import com.mainsteam.stm.alarm.obj.ItsmOrderStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.util.SpringBeanUtil;

public class AlarmEventSyncItsm implements AlarmEventMonitor{
	private Logger logger=LoggerFactory.getLogger(AlarmEventSyncItsm.class);
	private static final long SYSTEM_CONFIG_ITSM_ALARM_WEBSERVICE=12;
	private ISystemConfigApi systemConfig;
	private AlarmEventDAO alarmEventDao;
	public void setAlarmEventDao(AlarmEventDAO resourceEventDao) {
		this.alarmEventDao = resourceEventDao;
	}
	
	private Meta getItssAlarmService(InstanceStateEnum level) throws IOException{
		SystemConfigBo bo=systemConfig.getSystemConfigById(SYSTEM_CONFIG_ITSM_ALARM_WEBSERVICE);
		if(bo==null){
			throw new RuntimeException("can't find itsm config.");
		}
		Map<String,String> config=JSON.parseObject(bo.getContent(), new TypeReference<Map<String, String>>(){});
		
		if(!"true".equals(config.get("AVAILABLE"))){
			logger.debug("don't allow itsm config:false");
			return null;
		}
		String levs=config.get("LEVEL");
		if(StringUtils.isEmpty(levs) ||!levs.contains(convertToAlarmLevel(level))){
			logger.debug("don't allow alarm level["+level+"] aync to itsm!");
			return null;
		}
		String url=config.get("URL");
		URL wsdlURL;
		try {
			if(logger.isDebugEnabled())
				logger.debug("sync alarm wsdlURL:"+url);
			wsdlURL = new URL(url);
			 URLConnection  conn =wsdlURL.openConnection();
			conn.setDoInput(true);  
            conn.setDoOutput(true);
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(3000);
            conn.connect();
            InputStream in=  conn.getInputStream();
            byte[] bts=new byte[100];
            in.read(bts);
            if(logger.isDebugEnabled())
            	logger.debug("read data:"+new String(bts,Charset.defaultCharset()));
            in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("can't find itsm soap's url:"+url+",exception:"+e.getMessage(),e);
		}
		
		ITSSAlarmServiceBusImplService ss = new ITSSAlarmServiceBusImplService(wsdlURL);
		ItssAlarmServiceBus bus=ss.getITSSAlarmServiceBusImplPort();
		//设置超时
        Map<String, Object> requestContext = ((BindingProvider)bus).getRequestContext();  
        requestContext.put("com.sun.xml.internal.ws.connect.timeout", 3000); // Timeout in millis  
        requestContext.put("com.sun.xml.internal.ws.request.timeout", 1000); // Timeout in millis  
        
		Meta mt=new Meta();
		mt.service= ss.getITSSAlarmServiceBusImplPort();  
		mt.code=config.get("CODE");
		
		return mt;
	}
	
	@Override
	public void handleEvent(AlarmEvent event) {
		
		try {
			Meta meta = getItssAlarmService(event.getLevel());
			if(meta==null){
				logger.debug("don't allow aync to itsm!");
				return ;
			}
		
			ItsmAlarm alarm=new ItsmAlarm();
			alarm.setSource(meta.code);
			alarm.setAlarmId(String.valueOf(event.getEventID()));
			alarm.setAlarmTrackId(alarm.getAlarmId());
			alarm.setOccurTime(formarTime(event.getCollectionTime()));
			alarm.setSeverity(convertToSeverity(event.getLevel()));
			alarm.setRecoveryAlarmID(event.getRecoverKey());
			StringBuffer stringBuffer = new StringBuffer();
			if(org.apache.commons.lang.StringUtils.isNotBlank(event.getSourceName()))
				stringBuffer.append("【").append(event.getSourceName()).append("】");
			if(org.apache.commons.lang.StringUtils.isNotBlank(event.getSourceIP())) {
				stringBuffer.append("【").append(event.getSourceIP()).append("】");
			}
			stringBuffer.append(event.getContent());
			alarm.setMessage(stringBuffer.toString());
			alarm.setAlarmType(event.isRecovered()?2:1);
			alarm.setMoId(event.getSourceID());
			alarm.setMoType(event.getExt2());
			
			ResourceInstanceService instanceService=SpringBeanUtil.getBean(ResourceInstanceService.class);
			try{
				ResourceInstance ins=instanceService.getResourceInstance(Long.valueOf(event.getSourceID()));
				IDomainApi stm_system_DomainApi =SpringBeanUtil.getBean(IDomainApi.class);
				if(ins.getDomainId()>0){
					Domain dm=stm_system_DomainApi.get(ins.getDomainId());
					if(dm!=null){
						alarm.setDomainName(dm.getName());
						alarm.setDomainId(dm.getId());
					}
				}
				
				
			}catch(Exception e){
				logger.error(e.getMessage());
			}
			alarm.setAdditionalInfo(event.getSourceIP());
			
			String jsonData=JSON.toJSONString(new ItsmAlarm[]{alarm});
			if(logger.isInfoEnabled())
				logger.info("sync alarm data:"+jsonData);
			
			String result=meta.service.sendAlarms(jsonData);
			
			if(logger.isInfoEnabled())
				logger.info("sync alarm["+event.getEventID()+":"+event.getContent()+","+alarm.getOccurTime()+"] to itsm result:"+result);
			
			ItsmAlarmData itsmData=new ItsmAlarmData();
			itsmData.setAlarmEventID(event.getEventID());
			itsmData.setState(ItsmOrderStateEnum.NOT_SEND);
			event.setItsmData(itsmData);
			
			alarmEventDao.updateItsmOrderState(itsmData);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public static String formarTime(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		StringBuilder sb=new StringBuilder();
		sb.append(cal.get(Calendar.YEAR)).append("-");
		sb.append(formartToStr(cal.get(Calendar.MONTH)+1)).append("-");
		sb.append(formartToStr(cal.get(Calendar.DAY_OF_MONTH))).append(" ");
		sb.append(formartToStr(cal.get(Calendar.HOUR_OF_DAY))).append(":");
		sb.append(formartToStr(cal.get(Calendar.MINUTE))).append(":");
		if(cal.get(Calendar.MILLISECOND)>=500){
			sb.append(formartToStr(cal.get(Calendar.SECOND)+1));
		}else{
			sb.append(formartToStr(cal.get(Calendar.SECOND)));
		}
		return sb.toString();
	}
	
	private  static String formartToStr(int num){
		return (num>9?"":"0")+num;
	}
	
	public static void main(String[] args) {
		String str=AlarmEventSyncItsm.formarTime(new Date());
		System.out.println(str);
	}
	
	private String convertToAlarmLevel(InstanceStateEnum level){
		switch(level){
			case CRITICAL:
				return "10";
			case SERIOUS:
				return "8";
			case WARN:
				return "6";
			case UNKOWN:
			case NORMAL_UNKNOWN:
				return "9";
			case NORMAL:
				return "0";
			default:
				return "null";
		}
		
	}
	
	private int convertToSeverity(InstanceStateEnum state){
		switch(state){
			case CRITICAL:
				return 1;
			case SERIOUS:
				return 2;
			case WARN:
				return 4;
			case UNKOWN:
			case NORMAL_UNKNOWN:
				return 4;
			case NORMAL:
				return 5;
			default:
				throw new RuntimeException("unknow level:"+state);
		}
		
	}

	public void setSystemConfig(ISystemConfigApi systemConfig) {
		this.systemConfig = systemConfig;
	}
	private class Meta{
		private ItssAlarmServiceBus service;
		private String code;
	}
}
