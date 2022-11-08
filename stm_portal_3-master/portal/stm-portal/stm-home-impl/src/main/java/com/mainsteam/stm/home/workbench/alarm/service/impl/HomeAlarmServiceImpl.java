package com.mainsteam.stm.home.workbench.alarm.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.home.workbench.alarm.api.IHomeAlarmApi;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: HomeAlarmServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月18日
 * @author   zhangjunfeng
 * 
 * modified
 * @since  2017年03月29日
 * @author tandl 
 */
public class HomeAlarmServiceImpl implements IHomeAlarmApi {

	@Resource
	private AlarmEventService resourceEventService;
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	@Qualifier("stm_home_workbench_resourceApi")
	private IResourceApi resourceApi;
	
	@Override
	public Map<String, Integer> getHomeAlarmData(List<Long> resourceIds) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.add(Calendar.DATE, -1);//设置获取时间为24小时内的，当前时间减去一天
		return getHomeAlarmData(resourceIds, startCalendar.getTime(), Calendar.getInstance().getTime());
	}
	@Override
	public Map<String, Integer> getHomeAlarmDataById(Long resourceIds) {
		System.out.println(resourceIds);
		// TODO Auto-generated method stub
		List<Long> Ids = new ArrayList<Long>();
		try {
			ResourceInstance source = resourceInstanceService.getResourceInstance(resourceIds);
			if(source!=null){
				if(source.getParentId()!=0){
					Ids.add(source.getParentId());
				}else{
			
					Ids.add(resourceIds);
					
				}
			}
			
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getHomeAlarmData(Ids,null,null);
	}
	@Override
	public Map<String, Integer> getHomeAlarmData(List<Long> resourceIds, Date start, Date end) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		int critical = 0;
		int serious = 0;
		int warn = 0;
		//int unkown = 0;
		//int normal = 0;
		//通过resource实例ID获取topn
		if(resourceIds!=null && resourceIds.size()>0){
			List<String> ids = new ArrayList<>();
			for (Long id : resourceIds) {
				ids.add(String.valueOf(id));
			}
			
			critical =	resourceEventService.countAlarmEvent(ids,SysModuleEnum.MONITOR,new InstanceStateEnum[]{InstanceStateEnum.CRITICAL},start,end,false);//致命
			serious =	resourceEventService.countAlarmEvent(ids,SysModuleEnum.MONITOR,new InstanceStateEnum[]{InstanceStateEnum.SERIOUS},start,end,false);//严重
			warn =	resourceEventService.countAlarmEvent(ids,SysModuleEnum.MONITOR, new InstanceStateEnum[]{InstanceStateEnum.WARN},start,end,false);//警告
			//unkown =	resourceEventService.countAlarmEvent(ids,SysModuleEnum.MONITOR,new InstanceStateEnum[]{InstanceStateEnum.UNKOWN},start,end,false);//未知
			//normal = resourceEventService.countAlarmEvent(ids, SysModuleEnum.MONITOR,new InstanceStateEnum[]{InstanceStateEnum.NORMAL}, start, end,false);//正常
		}
		result.put("critical", critical);
		result.put("serious", serious);
		result.put("warn",warn);
		//result.put("unkown",unkown);
		//result.put("normal", normal);
		//int tatal = critical+serious+warn+unkown+normal;
		int tatal = critical+serious+warn;
		result.put("total",tatal);
		return result;
	}
	@Override
	public List<AlarmEvent> getHomeOneAlarm(Long resourceIds) {
		System.out.println(resourceIds);
		// TODO Auto-generated method stub
		List<String> ids = new ArrayList<String>();
		try {
			ResourceInstance source = resourceInstanceService.getResourceInstance(resourceIds);
			if(source!=null){
				if(source.getParentId()!=0){
					ids.add(String.valueOf(source.getParentId()));
				}else{
				List<ResourceInstance> instances=	source.getChildren();
				if(instances.size()!=0){
					ids.add(String.valueOf(resourceIds));
					for (int i = 0; i <instances.size(); i++) {
						ids.add(String.valueOf(instances.get(i).getId()));
					}
				}
					
				}
			}
			
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlarmEventQuery2 query = new AlarmEventQuery2();
		
		List<AlarmEventQueryDetail> details = new ArrayList<AlarmEventQueryDetail>();
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR};
	for (int i = 0; i < monitorList.length; i++) {
	
		AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
		detail.setSourceIDes(ids);
		detail.setRecovered(false);

	//	detail.set
		detail.setSysID(monitorList[i]);
		details.add(detail);
	}
		

		query.setFilters(details);
		List<AlarmEvent> events=	resourceEventService.findAlarmEvent(query);
		System.out.println("events: "+events.size());
		return events;
	}


}
