package com.mainsteam.stm.portal.alarm.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.alarm.api.IRemoteDataQueryRecordApi;
import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;
import com.mainsteam.stm.portal.alarm.web.vo.SpecialAlarmResult;
import com.mainsteam.stm.portal.alarm.web.vo.SpecialAlarmVo;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.util.PropertiesFileUtil;

/**
 * <li>文件名称: SpecialAlarmInfoQueryAction.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:
 * 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li> 
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author tongpl
 */
@Controller
@RequestMapping("/alarm/specialAlarm")//specialAlarm
public class SpecialAlarmInfoQueryAction extends BaseAction {

	private static final Log logger = LogFactory.getLog(SpecialAlarmInfoQueryAction.class);
	
	@Resource
	private AlarmEventService alarmEventService;
	
	@Resource
	private AlarmRuleService alarmRuleService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IRemoteDataQueryRecordApi remoteDataQueryRecordService;
	
	
	private List<AlarmEvent> findAlarmEvent(SysModuleEnum[] sysModules){
		List<ResourceInstance> riList = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();
			
			if(null==riList){
				return null;
			}
		} catch (InstancelibException e) {
			String errorMsg = "load ParentInstance error!!!";
			logger.error(errorMsg);
			logger.error(e.getMessage());
			
			return null;
		}
		List<String> strList = new ArrayList<String>();
		for(ResourceInstance ri:riList){
			if(ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				strList.add(String.valueOf(ri.getId()));
			}
		}
		List<MetricStateEnum>  mseList = getMetricStateEnum("down");
		
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		for(SysModuleEnum sme:sysModules){
			AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
			detail.setSysID(sme);
			detail.setSourceIDes(strList);
			detail.setStates(mseList);
			detail.setRecovered(false);
//			detail.setExt1(vo.getChildCategory());
//			detail.setExt2(vo.getPrentCategory());
			
			filters.add(detail);
		}
		eq.setFilters(filters);
		
		return alarmEventService.findAlarmEvent(eq);
		//配置文件读写
//		String filePath = "properties/resource_alarm_query.properties";
//		Properties prop=PropertiesFileUtil.getProperties(filePath);
//		String defaultDateStr = null,key = "default";
//		Date date = new Date();
//		SimpleDateFormat myFmt1=new SimpleDateFormat("yyyy/MM/dd HH/mm/ss"); 
//		
//		if(null!=prop){
//			defaultDateStr = prop.getProperty(key);
//			
//			try {
//				writeProperties(key,myFmt1.format(date),prop,filePath);
//				date = myFmt1.parse(defaultDateStr);
//			} catch (ParseException e) {
//				logger.error("get resource_alarm_query.properties value error");
//				logger.error(e.getMessage());
//			}
//		}
//		aeq.setStart(date);
	}
	
	/**
	 * iwatch 告警信息定制接口
	 * 
	 * @param JSONObject
	 * @return
	 */
	@RequestMapping("/getParentResourceCriticalAlarm")
	public String getParentResourceCriticalAlarm(HttpServletRequest request){
//		return getParentResourceCriticalAlarmForTest(request);
		SpecialAlarmResult result = new SpecialAlarmResult();
		List<SpecialAlarmVo> mapList = new ArrayList<SpecialAlarmVo>();
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR};
		String queryState = "false";
		
		List<AlarmEvent> aeList = findAlarmEvent(monitorList);
		if (null != aeList && aeList.size() > 0) {
			RemoteDataQueryRecord rdqr = getLastAlarmIdByRequest(request);
			Long sendAlarmId = rdqr.getLastAlarmId();
			Long largestAlarmId = sendAlarmId;
			
			for (AlarmEvent ae : aeList) {
				if(sendAlarmId.longValue() < ae.getEventID()){
					if(largestAlarmId.longValue() < ae.getEventID()){
						largestAlarmId = ae.getEventID();
					}
					
					SpecialAlarmVo alarm = new SpecialAlarmVo();
					alarm.setAlarmId(String.valueOf(ae.getEventID()));
					alarm.setSourceIP(null == ae.getSourceIP() ? "" : ae.getSourceIP());
					alarm.setSourceName(ae.getSourceName());
					
					Date dateCollect = ae.getCollectionTime();
					if(null!=dateCollect){
						alarm.setCollectionTime(String.valueOf(ae.getCollectionTime().getTime()));
					}
					alarm.setAlarmContent(ae.getContent());
					mapList.add(alarm);
				}
			}
			rdqr.setLastAlarmId(largestAlarmId);
			
			if(mapList.size()>0){
				String msg = "alarmEvent query success!!!";
				queryState = "true";
				
				if(!updateRemoteDataQueryRecord(request,rdqr)){
					logger.error("alarmEvent query success , but updateRemoteDataQueryRecord false !!!");
				}
				
				result.setInfo(mapList, queryState, msg);
				return JSONObject.toJSONString(result);
			}
		}
		String msg = "alarmEvent is null!!!";
		result.setInfo(mapList, queryState, msg);
		return JSONObject.toJSONString(result);
	}
	
	private static List<MetricStateEnum> getMetricStateEnum(String metricStateEnumString) {
		List<MetricStateEnum> ise = new ArrayList<MetricStateEnum>();
		if (null == metricStateEnumString) {
			return ise;
		} else {
			switch (metricStateEnumString) {
			case "all":
				break;
			case "down":
				ise.add(MetricStateEnum.CRITICAL);
				
				break;
			case "metric_error":
				ise.add(MetricStateEnum.SERIOUS);
				
				break;
			case "metric_warn":
				ise.add(MetricStateEnum.WARN);
				
				break;
			case "metric_recover":
				ise.add(MetricStateEnum.NORMAL);
				
				break;
			case "metric_unkwon":
				ise.add(MetricStateEnum.UNKOWN);
				
				break;
			}
		}
		return ise;
	}
	
	/**
	 * iwatch 告警信息定制接口
	 * 
	 * @param JSONObject
	 * @return
	 */
	@RequestMapping("/getParentResourceCriticalAlarmForTest")
	public String getParentResourceCriticalAlarmForTest(HttpServletRequest request){
//		ServletContext sc = session.getServletContext();
		
		String addr = request.getRemoteAddr();
		String host = request.getRemoteHost();
		int port = request.getRemotePort();
		
		RemoteDataQueryRecord rdqr = new RemoteDataQueryRecord();
		rdqr.setRemoteIp(host);
		rdqr.setRemotePort(port);
		rdqr.setQueryTime(new Date());
		
		remoteDataQueryRecordService.add(rdqr);
		
		SpecialAlarmResult result = new SpecialAlarmResult();
		List<SpecialAlarmVo> mapList = new ArrayList<SpecialAlarmVo>();
//		SimpleDateFormat myFmt1=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"); 
		
		List<AlarmEvent> aeList = new ArrayList<AlarmEvent>();
		AlarmEvent ae1 = new AlarmEvent();
		ae1.setEventID(111597);
		ae1.setSourceIP("192.168.1.159");
		ae1.setSourceName("192.168.1.159");
		ae1.setCollectionTime(new Date(Long.parseLong("1437378560000")));
		ae1.setContent("资源192.168.1.159不可用");
		
		AlarmEvent ae2 = new AlarmEvent();
		ae2.setEventID(111361);
		ae2.setSourceIP("192.168.9.93");
		ae2.setSourceName("192.168.9.93");
		ae2.setCollectionTime(new Date(Long.parseLong("1437374925000")));
		ae2.setContent("资源192.168.9.93不可用");
		
		AlarmEvent ae3 = new AlarmEvent();
		ae3.setEventID(111201);
		ae3.setSourceIP("172.16.8.103");
		ae3.setSourceName("sheldon-PC");
		ae3.setCollectionTime(new Date(Long.parseLong("1437370544000")));
		ae3.setContent("资源sheldon-PC不可用");
		
		AlarmEvent ae4 = new AlarmEvent();
		ae4.setEventID(111598);
		ae4.setSourceIP("");
		ae4.setSourceName("jianghd-cluster");
		ae4.setCollectionTime(new Date(Long.parseLong("1437378560000")));
		ae4.setContent("资源jianghd-cluster不可用");
		
		AlarmEvent ae5 = new AlarmEvent();
		ae5.setEventID(111066);
		ae5.setSourceIP("172.16.8.129");
		ae5.setSourceName("WIN-DPGVKSPQ17A");
		ae5.setCollectionTime(new Date(Long.parseLong("1437365144000")));
		ae5.setContent("资源【WIN-DPGVKSPQ17A】不可用");
//		ae5.setContent("资源WIN-DPGVKSPQ17A不可用");
		
		aeList.add(ae1);
		aeList.add(ae2);
//		aeList.add(ae3);
//		aeList.add(ae4);
//		aeList.add(ae5);
		
		for (AlarmEvent ae : aeList) {
			SpecialAlarmVo alarm = new SpecialAlarmVo();
			alarm.setAlarmId(String.valueOf(ae.getEventID()));
			alarm.setSourceIP(null == ae.getSourceIP() ? "" : ae.getSourceIP());
			alarm.setSourceName(ae.getSourceName());
			
			Date dateCollect = ae.getCollectionTime();
			if(null!=dateCollect){
				alarm.setCollectionTime(String.valueOf(ae.getCollectionTime().getTime()));
//					alarm.setCollectionTimeStr(myFmt1.format(ae.getCollectionTime()));
			}
			alarm.setAlarmContent(ae.getContent());
			mapList.add(alarm);
		}
		
		String msg = "alarmEvent query success!!!";
		result.setInfo(mapList, "true", msg);
		return JSONObject.toJSONString(result);
		
	}
	
	private RemoteDataQueryRecord getLastAlarmIdByRequest(HttpServletRequest request){
		String host = request.getRemoteHost();
		if(null==host){
			RemoteDataQueryRecord rdqr = new RemoteDataQueryRecord();
			rdqr.setLastAlarmId(new Long(0));
			return rdqr;
		}
		List<RemoteDataQueryRecord>  rdqrList = remoteDataQueryRecordService.loadByIp(host);
		if(null==rdqrList || rdqrList.size()==0){
			RemoteDataQueryRecord rdqr = new RemoteDataQueryRecord();
			rdqr.setLastAlarmId(new Long(0));
			return rdqr;
		}
		return rdqrList.get(0);
	}
	
	private boolean updateRemoteDataQueryRecord(HttpServletRequest request,RemoteDataQueryRecord rdqr){
		String host = request.getRemoteHost();
		int port = request.getRemotePort();
		
		rdqr.setRemoteIp(host);
		rdqr.setRemotePort(port);
		rdqr.setQueryTime(new Date());
		
		if(null==rdqr.getRecordId()){
			return 1==remoteDataQueryRecordService.add(rdqr);
		}else{
			return 1==remoteDataQueryRecordService.update(rdqr);
		}
	}
	
	
	
    public static void writeProperties(String keyname,String keyvalue ,Properties props,String filePath) {
        try {
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        	
        	ClassLoader loader = Thread.currentThread().getContextClassLoader();
        	URL url = loader.getResource(filePath);
        	File file = new File(url.getFile());
            OutputStream fos = new FileOutputStream(file);
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
        	e.printStackTrace();
            System.err.println("属性文件更新错误");
        }
     }
}
