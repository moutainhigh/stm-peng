package com.mainsteam.stm.portal.business.service.impl;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.BizAlarmInfoApi;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;
import com.mainsteam.stm.portal.business.dao.IBizAlarmInfoDao;
import com.mainsteam.stm.util.DateUtil;
public class BizAlarmInfoImpl implements BizAlarmInfoApi {
private IBizAlarmInfoDao bizAlarmInfoDao;

private ISequence seq;
@Resource
private AlarmEventService alarmEventService;
@Resource
private BizCanvasApi bizCanvasApi;
@Resource
private ResourceInstanceService resourceInstanceService;
private static final Log logger = LogFactory.getLog(BizAlarmInfoImpl.class);
	@Override
	public BizAlarmInfoBo getAlarmInfo(BizAlarmInfoBo infoBo) {
		// TODO Auto-generated method stub
		return bizAlarmInfoDao.getAlarmInfo(infoBo);
	}

	@Override
	public int insertBizAlarmInfo(BizAlarmInfoBo infoBo) {
		infoBo.setId(seq.next());
		return bizAlarmInfoDao.insertBizAlarmInfo(infoBo);
	}

	@Override
	public int updateBizAlarmInfo(BizAlarmInfoBo infoBo) {
		// TODO Auto-generated method stub
		return bizAlarmInfoDao.updateBizAlarmInfo(infoBo);
	}

	public IBizAlarmInfoDao getBizAlarmInfoDao() {
		return bizAlarmInfoDao;
	}

	public void setBizAlarmInfoDao(IBizAlarmInfoDao bizAlarmInfoDao) {
		this.bizAlarmInfoDao = bizAlarmInfoDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public BizAlarmInfoBo getAlarmInfoById(long bizid) {
		// TODO Auto-generated method stub
		return bizAlarmInfoDao.getAlarmInfoById(bizid);
	}

	@Override
	public int deleteInfo(long bizid) {
		// TODO Auto-generated method stub
		return bizAlarmInfoDao.deleteInfo(bizid);
	}

	@Override
	public void selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bizServiceBo, String status,String nodeId,String restore,Date starTime,Date endTime,String queryTime,String isCheckOne,String isCheckTwo) throws Exception {
	int start=(int)page.getStartRow();
	int rowsize=(int)page.getStartRow()+(int)page.getRowCount();
//	System.out.println("start: "+start+"======="+"rowsize: "+rowsize);
		SysModuleEnum[] enums={SysModuleEnum.BUSSINESS,SysModuleEnum.MONITOR};
		BizInstanceNodeBo node =null;
		if(nodeId!=null && nodeId!=""){
			node = bizCanvasApi.getInstanceNode(Long.parseLong(nodeId));
		}
		AlarmEventQuery2 eventQuery2 = toEventQuery(bizServiceBo, status, enums,node,restore,starTime,endTime,queryTime,isCheckOne,isCheckTwo);

		List<AlarmEvent> list=	alarmEventService.findAlarmEvent(eventQuery2);
//		System.out.println(list);
	//	Page<AlarmEvent, AlarmEventQuery2> pageResult = alarmEventService.findAlarmEvent(eventQuery2, (int)page.getStartRow(), (int)page.getRowCount());
		List<BizWarnViewBo> datas = new ArrayList<BizWarnViewBo>();
//		List<AlarmEvent> eventstempl = pageResult.getDatas();
		List<AlarmEvent> eventstempl = new ArrayList<AlarmEvent>();
		if(rowsize>list.size()){
			rowsize=list.size();
		}
		for (int i = start; i < rowsize; i++) {
			eventstempl.add(list.get(i));
		}
		List<AlarmEvent> events = new ArrayList<AlarmEvent>();
		List<AlarmEvent> eventtemps = new ArrayList<AlarmEvent>();
	
		if(node!=null){
			if(node.getType()==3){
				eventstempl=list;
				//过滤指标
				if(eventstempl != null && eventstempl.size() > 0){
				
					for(AlarmEvent event : eventstempl){
						List<BizNodeMetricRelBo> metricbos=	bizCanvasApi.getMetricByNodeId(node.getId());
						for(BizNodeMetricRelBo metric : metricbos){
							if(metric.getChildInstanceId() <= 0){
								//主资源的指标
								if(event.getSourceID().equals(String.valueOf(node.getInstanceId())) && event.getExt3().equals(metric.getMetricId())){
									events.add(event);
								}else{
								//	events.remove(event);
								}
							}else{
								//子资源的指标
								if(event.getSourceID().equals(String.valueOf(metric.getChildInstanceId())) && event.getExt3().equals(metric.getMetricId())){
									events.add(event);
								}else{
									//events.remove(event);	
								}
							}
						}
					}
					int totle=events.size();
					if(rowsize>events.size()){
						rowsize=events.size();
					}
					for (int i = start; i < rowsize; i++) {
						eventtemps.add(events.get(i));
					}
					events=eventtemps;
					page.setTotalRecord(totle);
//					pageResult.setTotalRecord(events.size());
				}
			}else{
				events=eventstempl;
				page.setTotalRecord(list.size());
			}
		}else{
			events=eventstempl;
			page.setTotalRecord(list.size());
		}
		
		for(AlarmEvent event: events){
			BizWarnViewBo bo = new BizWarnViewBo();
			bo.setId(event.getEventID());
			bo.setSourceId(Long.parseLong(event.getSourceID()));
			if(null==restore||"false".equals(restore)){
				bo.setDataClass("1");
			}else{
				bo.setDataClass("2");;
			}
			bo.setContent(event.getContent());
			bo.setName(event.getSourceName());
			bo.setWarnTime(event.getCollectionTime());
			bo.setLevel(event.getLevel().name());
			datas.add(bo);
		}
		page.setDatas(datas);
		
		
	}
	
	private AlarmEventQuery2 toEventQuery(BizServiceBo bizServiceBo, String status, SysModuleEnum[] sysModules,BizInstanceNodeBo node,String restore,Date starTime,Date endTime,String queryTime,String isCheckOne,String isCheckTwo ) {
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		//属性排序
		
		
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		for(int i = 0; i < sysModules.length; i++){
			AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			//detail.setRecovered(false);
			if(null==restore||"false".equals(restore)){
				detail.setRecovered(false);
			}else{
				detail.setRecovered(true);
			}
			if(!StringUtils.isEmpty(status)){
				List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
				list.add(getMetricStateEnum(status));
				detail.setStates(list);
			}
			
			if (null != isCheckOne || null != isCheckTwo) {
				if ("isChecked".equals(isCheckOne)) {
					detail.setStart(getBeginTime(queryTime));
					detail.setEnd(getCurrentTime());
				}
				if ("isChecked".equals(isCheckTwo)) {
					detail.setStart(starTime);
					detail.setEnd(endTime);
				}
			} else {
				detail.setStart(null);
				detail.setEnd(null);	
			}
			
			//资源名称或者IP查询
			try {
				if (null != bizServiceBo.getId()) {
					List<String> sList = new  ArrayList<String>();
					//资源告警列表显示主资源、子资源告警数据
					ResourceInstance ri = resourceInstanceService.getResourceInstance(bizServiceBo.getId());
					if(ri != null && null != ri.getLifeState() && ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED) && sysModules[i].equals(SysModuleEnum.MONITOR)){
						
							if(node!=null){
								if(node.getType()==1){//主资源
									List<ResourceInstance> resourcechildList = resourceInstanceService.getChildInstanceByParentId(ri.getId());
									for(int j = 0; resourcechildList != null && j < resourcechildList.size(); j++){
										ResourceInstance  riList = resourcechildList.get(j);
										if(riList.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
											sList.add(String.valueOf(riList.getId()));
										}
									}
									sList.add(bizServiceBo.getId().toString());
								}else if(node.getType()==2){//子资源
								List<BizNodeMetricRelBo> bos=bizCanvasApi.getChildInstanceByNodeId(node.getId());
								if(bos!=null){
									for (BizNodeMetricRelBo bizNodeMetricRelBo : bos) {
										sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
									}
								}
								}else if(node.getType()==3){//指标
									List<BizNodeMetricRelBo> metricbos=	bizCanvasApi.getMetricByNodeId(node.getId());
									if(metricbos!=null){
										for (BizNodeMetricRelBo bizNodeMetricRelBo : metricbos) {
											if(bizNodeMetricRelBo.getChildInstanceId()<=0){//主资源指标
												ResourceInstance instance=	resourceInstanceService.getResourceInstance(node.getInstanceId());
												sList.add(String.valueOf(instance.getId()));
											//	detail.setExt3(bizNodeMetricRelBo.getMetricId());
												
											}else{
											//	detail.setExt3(bizNodeMetricRelBo.getMetricId());
												sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));	
											}
//											
										}
									}
								
								}
							}
						
						
						
						
					}else{
						if(detail.getSysID().equals(SysModuleEnum.BUSSINESS)){
							sList.add(String.valueOf(bizServiceBo.getId()));
						}
					}
					if(sList.size()==0){
						detail.setSourceIDes(null);
					}else{
						detail.setSourceIDes(sList);
						filters.add(detail);
					}
					
				
					
				} 
			} catch (InstancelibException e) {
			
			}
			
		
		
			
		}
		eq.setFilters(filters);
		return eq;
	}
	
	private Date getBeginTime(String queryTime) {
		Date date = null;
		if (null == queryTime || "0".equals(queryTime)) {
			return date;
		} else {
			switch (queryTime) {
			case "1":
				date = DateUtil.subHour(getCurrentTime(), 1);
				break;
			case "2":
				date = DateUtil.subHour(getCurrentTime(), 2);
				break;
			case "3":
				date = DateUtil.subHour(getCurrentTime(), 4);
				break;
			case "4":
				date = DateUtil.subDay(getCurrentTime(), 1);
				break;
			case "5":
				date = DateUtil.subDay(getCurrentTime(), 7);
				break;
			}

		}
		return date;
	}

	/**
	 * 获取系统当前日期
	 * 
	 * @return
	 */
	private static Date getCurrentTime() {

		Date date = Calendar.getInstance().getTime();
		return date;
	}
	
	private static MetricStateEnum getMetricStateEnum(String metricStateEnumString) {
		MetricStateEnum ise = null;
		if (null == metricStateEnumString) {
			return ise;
		} else {
			switch (metricStateEnumString) {
			case "all":
				break;
			case "down":
				ise = MetricStateEnum.CRITICAL;
				break;
			case "metric_error":
				ise = MetricStateEnum.SERIOUS;
				break;
			case "metric_warn":
				ise = MetricStateEnum.WARN;
				break;
			case "metric_recover":
				ise = MetricStateEnum.NORMAL;
				break;
			}
		}
		return ise;
	}


	
	public List<String> getListIds(long nodeId) throws InstancelibException{
		List<String> sList = new  ArrayList<String>();
		BizInstanceNodeBo node = bizCanvasApi.getInstanceNode(nodeId);
		if(node!=null){
			if(node.getType()==1){//主资源
				List<ResourceInstance> resourcechildList = resourceInstanceService.getChildInstanceByParentId(node.getInstanceId());
				for(int j = 0; resourcechildList != null && j < resourcechildList.size(); j++){
					ResourceInstance  riList = resourcechildList.get(j);
					if(riList.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						sList.add(String.valueOf(riList.getId()));
					}
				}
				sList.add(String.valueOf(node.getInstanceId()));
			}else if(node.getType()==2){//子资源
			List<BizNodeMetricRelBo> bos=bizCanvasApi.getChildInstanceByNodeId(node.getId());
			if(bos!=null){
				for (BizNodeMetricRelBo bizNodeMetricRelBo : bos) {
					sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
				}
			}
			}else if(node.getType()==3){//指标
				List<BizNodeMetricRelBo> metricbos=	bizCanvasApi.getMetricByNodeId(node.getId());
				if(metricbos!=null){
					for (BizNodeMetricRelBo bizNodeMetricRelBo : metricbos) {
						sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
					}
				}
			
			}
		}
		
		return sList;
		
	}

}
