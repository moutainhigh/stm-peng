package com.mainsteam.stm.home.workbench.warning.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.home.workbench.warning.api.IWarningApi;
import com.mainsteam.stm.home.workbench.warning.bo.Warning;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.util.DateUtil;

@Service("stm_home_workbench_warningApi")
public class WarningImpl implements IWarningApi {
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private AlarmEventService resourceEventService;
	@Resource
	private BizUserRelApi bizUserRelApi;
	private static final Log logger = LogFactory.getLog(WarningImpl.class);
	@Override
	public void getAllWarning(Page<Warning, AlarmEventQuery> page) {
		//Date now = new Date();
		//Date yesterday = DateUtil.subDay(now, 1);
		
		AlarmEventQuery condition = page.getCondition();
		condition = condition == null ? new AlarmEventQuery() : condition;
		//condition.setStart(yesterday);
		//condition.setEnd(now);
		condition.setRecovered(false);
		// 接口已经修改
		SysModuleEnum[] sysModules = SysModuleEnum.values();
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters = new ArrayList<>();
		for (int i = 0; i < sysModules.length; i++) {
			AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			detail.setRecovered(false);
			detail.setStart(condition.getStart());
			detail.setEnd(condition.getEnd());
			if (sysModules[i].equals(SysModuleEnum.MONITOR)) {
				List<String> parentIds = condition.getSourceIDes() == null ? new ArrayList<String>()
						: condition.getSourceIDes();
				/*Set<Long> parentInstaceIdSet = new HashSet<Long>();
				for (int j = 0; j < parentIds.size(); j++) {
					parentInstaceIdSet.add(Long.valueOf(parentIds.get(j)));
				}*/
				/*
				 * if(!parentInstaceIdSet.isEmpty()){ List<Long> childIds =
				 * resourceInstanceService.getAllChildrenInstanceIdbyParentId(
				 * parentInstaceIdSet,InstanceLifeStateEnum.MONITORED); for (int
				 * j = 0; childIds != null && j < childIds.size(); j++) {
				 * parentIds.add(childIds.get(j).toString()); } }
				 */
				detail.setSourceIDes(parentIds.isEmpty() ? null : parentIds);
			}
			filters.add(detail);
		}
		eq.setFilters(filters);
	//	logger.error("condition.getSourceIDes(): "+condition.getSourceIDes());
		if (condition.getSourceIDes() != null && condition.getSourceIDes().size() > 0) {
			int start = (int)page.getStartRow();
			int rowcout = (int)page.getRowCount();
			logger.error("queryAlarmEvent start");
			Page<AlarmEvent, AlarmEventQuery2> pageEvent = resourceEventService.queryAlarmEvent(eq, start, rowcout);
			logger.error("queryAlarmEvent end");
			List<Warning> warnings = new ArrayList<Warning>();
			for (AlarmEvent resourceEvent : pageEvent.getDatas()) {
				Warning warning = toWarning(resourceEvent);
				warnings.add(warning);
			}
			page.setDatas(warnings);
		} else {
			page.setDatas(null);
		}
	}

	@Override
	public void getWarning(Page<Warning, AlarmEventQuery> page) {
		
		AlarmEventQuery condition = page.getCondition();
		//condition = condition == null ? new AlarmEventQuery() : condition;
		if(condition == null){
			condition = new AlarmEventQuery();
			Date now = new Date();
			Date yesterday = DateUtil.subDay(now, 1);
			condition.setStart(yesterday);
			condition.setEnd(now);
			condition.setRecovered(false);
		}
		
		// 接口已经修改
		SysModuleEnum[] sysModules = SysModuleEnum.values();
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters = new ArrayList<>();
		for (int i = 0; i < sysModules.length; i++) {
			AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			detail.setRecovered(condition.getRecovered());
			detail.setStates(condition.getStates());
			detail.setStart(condition.getStart());
			detail.setEnd(condition.getEnd());
			
			if (sysModules[i].equals(SysModuleEnum.MONITOR)) {
				List<String> parentIds = condition.getSourceIDes() == null ? new ArrayList<String>()
						: condition.getSourceIDes();
				Set<Long> parentInstaceIdSet = new HashSet<Long>();
				for (int j = 0; j < parentIds.size(); j++) {
					parentInstaceIdSet.add(Long.valueOf(parentIds.get(j)));
				}
				
				detail.setSourceIDes(parentIds.isEmpty() ? null : parentIds);
			}
			filters.add(detail);
		}
		eq.setFilters(filters);
//		OrderField[] orderFieldes = new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.LEVEL};
//		eq.setOrderFieldes(orderFieldes);
		Map<AlarmEventQuery2.OrderField, AlarmEventQuery2.OrderAscOrDesc> orders = new java.util.LinkedHashMap(2);
		orders.put(AlarmEventQuery2.OrderField.LEVEL, AlarmEventQuery2.OrderAscOrDesc.ASC);
		orders.put(AlarmEventQuery2.OrderField.COLLECTION_TIME, AlarmEventQuery2.OrderAscOrDesc.DESC);
		eq.setOrderCollections(orders);
		if (condition.getSourceIDes() != null && condition.getSourceIDes().size() > 0) {
			int start = (int)page.getStartRow();
			int rowcout = (int)page.getRowCount();
			
			Page<AlarmEvent, AlarmEventQuery2> pageEvent = resourceEventService.queryAlarmEvent(eq, start, rowcout);
			List<Warning> warnings = new ArrayList<Warning>();
			for (AlarmEvent resourceEvent : pageEvent.getDatas()) {
				Warning warning = toWarning(resourceEvent);
				warnings.add(warning);
			}
			page.setDatas(warnings);
		} else {
			page.setDatas(null);
		}
	}
	
	public Warning toWarning(AlarmEvent resourceEvent) {
		Warning warning = new Warning();
		warning.setInstanceStatus(getInstStateEnumString(resourceEvent.getLevel()));
		warning.setResourceName(resourceEvent.getSourceName());
		warning.setIpAddress(resourceEvent.getSourceIP());
		warning.setAlarmContent(resourceEvent.getContent());
		warning.setEventTime(resourceEvent.getCollectionTime());
		warning.setEventID(resourceEvent.getEventID());
		warning.setSourceID(resourceEvent.getSourceID());
		warning.setSysID(resourceEvent.getSysID());
		return warning;
	}

	private String getInstStateEnumString(InstanceStateEnum isEnum) {
		String ise = "gray";
		if (null == isEnum) {
			return ise;
		} else {
			if (InstanceStateEnum.CRITICAL == isEnum) {
				ise = "red";
			} else if (InstanceStateEnum.SERIOUS == isEnum) {
				ise = "orange";
			} else if (InstanceStateEnum.WARN == isEnum) {
				ise = "yellow";
			} else if (InstanceStateEnum.NORMAL == isEnum) {
				ise = "green";
			}

		}
		return ise;
	}

	@Override
	public void getWarning(Page<Warning, AlarmEventQuery> page, ILoginUser user) {
		AlarmEventQuery condition = page.getCondition();
		if(condition == null){
			condition = new AlarmEventQuery();
			Date now = new Date();
			Date yesterday = DateUtil.subDay(now, 1);
			condition.setStart(yesterday);
			condition.setEnd(now);
			condition.setRecovered(false);
		}
		
		// 接口已经修改
		SysModuleEnum[] sysModules = SysModuleEnum.values();
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters = new ArrayList<>();
		for (int i = 0; i < sysModules.length; i++) {
			AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			detail.setRecovered(condition.getRecovered());
			detail.setStates(condition.getStates());
			detail.setStart(condition.getStart());
			detail.setEnd(condition.getEnd());
			
			if (sysModules[i].equals(SysModuleEnum.MONITOR) || sysModules[i].equals(SysModuleEnum.CONFIG_MANAGER)) {
				List<String> parentIds = condition.getSourceIDes() == null ? new ArrayList<String>()
						: condition.getSourceIDes();
				Set<Long> parentInstaceIdSet = new HashSet<Long>();
				for (int j = 0; j < parentIds.size(); j++) {
					parentInstaceIdSet.add(Long.valueOf(parentIds.get(j)));
				}
				detail.setSourceIDes(parentIds.isEmpty() ? null : parentIds);
			}else if(sysModules[i].equals(SysModuleEnum.BUSSINESS)){
				List<String> resourceIdList = new  ArrayList<String>();
				List<BizMainBo> bos = bizUserRelApi.getBizlistByUserId(user.getId());
				if(bos !=null && bos.size() >= 0){
					
					String domainIds = condition.getExt9();
					out : for (BizMainBo bizMain : bos) {
						if(domainIds.equals("-1")){
							//全部域
							resourceIdList.add(bizMain.getId() + "");
						}else{
							for(String domainId : domainIds.split(",")){
								if(bizMain.getDomainId() == Long.parseLong(domainId)){
									resourceIdList.add(bizMain.getId() + "");
									continue out;
								}
							}
						}
					}
				}
				resourceIdList.add("0");
				detail.setSourceIDes(resourceIdList);	
			}else if(sysModules[i].equals(SysModuleEnum.LINK)){
				List<String> resourceIdList = new  ArrayList<String>();
				List<ResourceInstance> linkResourceInstList = resourceInstanceService.getAllResourceInstancesForLink();
				Set<IDomain> domains = user.getDomains();
				Set<Long> domainSet = new HashSet<Long>();
				for(IDomain domain:domains){
					domainSet.add(domain.getId());
				}
			//	List<ResourceInstance> linkResourceInstList= resourceInstanceService.getParentResourceInstanceByDomainIds(domainSet);
				for (int j = 0; linkResourceInstList != null && j < linkResourceInstList.size(); j++) {
					ResourceInstance ri = linkResourceInstList.get(j);
					if(ri.getLifeState() == InstanceLifeStateEnum.MONITORED){
						for (int k = 0; k < domainSet.size(); k++) {
							for(IDomain domain:domains){
							if(ri.getDomainId()==domain.getId()){
								resourceIdList.add(String.valueOf(ri.getId()));
							}
							}
						}
					
					}
				}
				if(!resourceIdList.isEmpty()){
					detail.setSourceIDes(resourceIdList);
				}else{
					resourceIdList.add("-1");
					detail.setSourceIDes(resourceIdList);
				}
			}
			filters.add(detail);
		}
		eq.setFilters(filters);
		Map<AlarmEventQuery2.OrderField, AlarmEventQuery2.OrderAscOrDesc> orders = new java.util.LinkedHashMap(2);
		orders.put(AlarmEventQuery2.OrderField.LEVEL, AlarmEventQuery2.OrderAscOrDesc.ASC);
		orders.put(AlarmEventQuery2.OrderField.COLLECTION_TIME, AlarmEventQuery2.OrderAscOrDesc.DESC);
		eq.setOrderCollections(orders);
		if (condition.getSourceIDes() != null && condition.getSourceIDes().size() > 0) {
			int start = (int)page.getStartRow();
			int rowcout = (int)page.getRowCount();
			
			Page<AlarmEvent, AlarmEventQuery2> pageEvent = resourceEventService.queryAlarmEvent(eq, start, rowcout);
			List<Warning> warnings = new ArrayList<Warning>();
			for (AlarmEvent resourceEvent : pageEvent.getDatas()) {
				Warning warning = toWarning(resourceEvent);
				warnings.add(warning);
			}
			page.setDatas(warnings);
		} else {
			page.setDatas(null);
		}
	}
}
