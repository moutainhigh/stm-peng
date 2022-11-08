package com.mainsteam.stm.alarm.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.HandleType;
import com.mainsteam.stm.alarm.obj.ItsmAlarmData;
import com.mainsteam.stm.alarm.obj.ItsmOrderStateEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2.OrderField;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer*-beans.xml" })
public class AlarmEventServiceImplTest {
	@Autowired AlarmEventService alarmEventService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	
	@Test
	public void testRecoverAlarmEventBySourceID(){
		alarmEventService.recoverAlarmEventBySourceID("11231", HandleType.DELETE);
	}
	@Test
	public void testRecoverAlarmEventByRecoverKey(){
		alarmEventService.recoverAlarmEventByRecoverKey("11231", HandleType.DELETE);
	}
	
	@Test
	public void testUpdateAlarmEventHandleType(){
		alarmEventService.updateAlarmEventHandleType(1232l, HandleType.AUTO);
	}
	
	@Test
	public void testFindResourceEvent2(){
		AlarmEventQuery2 query=new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		
		AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
		List<MetricStateEnum> state=new ArrayList<MetricStateEnum>();
		state.add(MetricStateEnum.CRITICAL);
		state.add(MetricStateEnum.WARN);
		state.add(MetricStateEnum.SERIOUS);
		detail.setStates(state);
		
		List<String> sourceIDes=new ArrayList<String>();
		for(int i=0;i<3;i++){
			sourceIDes.add(String.valueOf(i));
		}
		detail.setSourceIDes(sourceIDes);
		detail.setRecovered(false);
		detail.setSysID(SysModuleEnum.MONITOR);
		filters.add(detail);
		
		detail=new AlarmEventQueryDetail();
		detail.setSysID(SysModuleEnum.CONFIG_MANAGER);
		 
		filters.add(detail);
		query.setFilters(filters);
		query.setOrderFieldes(new OrderField[]{OrderField.ITSM_DATA});
		
//		query.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.SOURCE_NAME,AlarmEventQuery2.OrderField.COLLECTION_TIME});
 
		System.out.println("testFindResourceEvent result:"+JSON.toJSONString(alarmEventService.findAlarmEvent(query, 0, 20)));
	}
	
	@Test
	public void testFindResourceEvent(){
		AlarmEventQuery query=new AlarmEventQuery();
		List<MetricStateEnum> state=new ArrayList<MetricStateEnum>();
//		state.add(MetricStateEnum.CRITICAL);
//		state.add(MetricStateEnum.WARN);
		state.add(MetricStateEnum.SERIOUS);
		query.setStates(state);
		
		query.setRecovered(false);
		
		List<String> sourceIDes=new ArrayList<String>();
		for(int i=0;i<10232;i++){
			sourceIDes.add(String.valueOf(i));
		}
		query.setSourceIDes(sourceIDes);
		
		query.setOrderFieldes(new AlarmEventQuery.OrderField[]{AlarmEventQuery.OrderField.SOURCE_NAME,AlarmEventQuery.OrderField.COLLECTION_TIME});
		query.setSysIDes(new SysModuleEnum[]{SysModuleEnum.MONITOR,SysModuleEnum.CONFIG_MANAGER});
//		query.setLikeSourceIP("200");
//		query.setExt1("Switch");
//		query.setLikeSourceIpOrName("12");
		System.out.println("testFindResourceEvent result:"+JSON.toJSONString(alarmEventService.findAlarmEvent(query, 0, 20)));
	}
	
	@Test
	public void testFindRecoveredResourceEvent(){
		AlarmEventQuery query=new AlarmEventQuery();
		
		Calendar cal=Calendar.getInstance();
		query.setStart(cal.getTime());
		cal.set(Calendar.MONTH, 8);
		query.setEnd(cal.getTime());
		query.setExt1("Switch");
		List<String> ides=new ArrayList<String>();
		ides.add("5832185");
		ides.add("5832181");
		query.setRecovered(true);
		query.setRecoveryEventID(3423l);
		query.setSourceIDes(ides);
		query.setOrderASC(true);
		System.out.println("testFindRecoveredResourceEvent result:"+JSON.toJSONString(alarmEventService.findAlarmEvent(query, 0, 20)));
	}
	
	@Test
	public void testCountResourceEvent(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, 8);
		
		List<String> sourceIDes=new ArrayList<String>();
		for(int i=0;i<5;i++){
			sourceIDes.add(String.valueOf(i));
		}
		
//		String json="[2064, 2050, 2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2060, 2061, 2062, 2063, 26122, 26123, 26120, 26121, 26126, 26127, 26124, 26125, 26114, 26115, 26112, 26113, 26118, 26119, 26116, 26117, 26139, 26138, 26137, 26136, 26140, 26131, 26130, 26129, 26128, 26135, 26134, 26133, 26132, 26004, 26005, 26006, 26007, 26002, 26003, 26012, 26013, 26014, 26015, 26008, 26009, 26010, 26011, 26038, 26039, 26036, 26037, 26034, 26035, 26032, 26033, 26046, 26047, 26044, 26045, 26042, 26043, 26040, 26041, 26023, 26022, 26021, 26020, 26019, 26018, 26017, 26016, 26031, 26030, 26029, 26028, 26027, 26026, 26025, 26024, 26064, 26065, 26066, 26067, 26068, 26069, 26070, 26071, 26072, 26073, 26074, 26075, 26076, 26077, 26078, 26079, 26049, 26048, 26051, 26050, 26053, 26052, 26055, 26054, 26057, 26056, 26059, 26058, 26061, 26060, 26063, 26062, 26098, 26099, 26096, 26097, 26102, 26103, 26100, 26101, 26106, 26107, 26104, 28001, 26105, 26110, 26111, 26108, 26109, 26083, 26082, 26081, 26080, 26087, 26086, 26085, 26084, 26091, 26090, 26089, 26088, 26095, 26094, 26093, 26092]";
//		List<String> sourceIDes=JSON.parseArray(json, String.class);
		
		int count=alarmEventService.countAlarmEvent(sourceIDes,SysModuleEnum.MONITOR, new InstanceStateEnum[]{InstanceStateEnum.SERIOUS},cal.getTime(), new Date(),false);
		System.out.println("testCountResourceEvent result:"+count);
	}
	
	@Test
	public void testGetResourceEvent(){
		AlarmEvent  alarm=alarmEventService.getAlarmEvent(101000,false);
		System.out.println("testCountResourceEvent result:"+alarm);
	}
	@Test
	public void testGetRecoveredResourceEvent(){
		AlarmEvent  alarm=alarmEventService.getAlarmEvent(126006,true);
		System.out.println("testCountResourceEvent result:"+alarm);
	}
	
	@Test 
	public void testUpdate(){
		ItsmAlarmData itsmData=new ItsmAlarmData();
		itsmData.setAlarmEventID(103040l);
		itsmData.setItsmOrderID("321");
		itsmData.setState(ItsmOrderStateEnum.FINISH);
		alarmEventService.updateItsmOrderState(itsmData);
	}
	
}
