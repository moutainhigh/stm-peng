package com.mainsteam.stm.portal.alarm.web.action;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmEventDetail;
import com.mainsteam.stm.alarm.obj.AlarmNotify;
import com.mainsteam.stm.alarm.obj.ItsmOrderStateEnum;
import com.mainsteam.stm.alarm.obj.NotifyState;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.alarm.api.AlarmApi;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgePageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmNotifyPageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmPageBo;
import com.mainsteam.stm.portal.alarm.web.vo.AlarmKnowledgePageVo;
import com.mainsteam.stm.portal.alarm.web.vo.AlarmNotifyPageVo;
import com.mainsteam.stm.portal.alarm.web.vo.AlarmNotifyVo;
import com.mainsteam.stm.portal.alarm.web.vo.AlarmPageVo;
import com.mainsteam.stm.portal.alarm.web.vo.AlarmVo;
import com.mainsteam.stm.portal.alarm.web.vo.AlertClientVo;
import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.topo.api.IIpMacPortApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: AlarmAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月
 * @author xhf
 */
@Controller
@RequestMapping("/alarm/alarmManagement")
public class AlarmManagemenAction extends BaseAction {

	@Resource
	private AlarmApi alarmApi;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private IUserApi userApi;

	@Resource
	private AlarmNotifyService alarmNotifyService;
	
	@Resource
	private AlarmEventService alarmEventService;
	
	@Resource
	private IDomainApi domainApi;

	@Resource(name="stm_system_resourceApi")
	private IResourceApi resourceApi;
	@Resource(name="stm_system_ipmacportApi")
	private IIpMacPortApi ipMacPortApi;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	
	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource(name="customResourceGroupApi")
	private ICustomResourceGroupApi resourceGroupApi ;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	@Resource
	private ProfileService profileService;
	@Resource
	private BizUserRelApi bizUserRelApi;
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(AlarmManagemenAction.class);

	/**
	 * 获取未恢复告警
	 * 
	 * @param alarmVo
	 * @return
	 */
	@RequestMapping("/getNotRestoreAlarm")
	public JSONObject getNotRestoreAlarm(AlarmPageVo alarmPageVo,HttpSession session) {
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR,SysModuleEnum.BUSSINESS,SysModuleEnum.LINK};
		AlarmEventQuery2 condition = toEventQuery(alarmPageVo.getCondition(), user, alarmPageVo.getSort(), alarmPageVo.getOrder(), monitorList,"query");
	
		for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
			condition.getFilters().get(i).setRecovered(false);
			if(condition.getFilters().get(i).getSourceIDes()==null || condition.getFilters().get(i).getSourceIDes().size()==0 ){
			
				condition.getFilters().get(i).setSourceIDes(null);
			}
		
		}
		long currentTime = System.currentTimeMillis();
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), condition);
		logger.info("Query alarm information elapsed time: " + (System.currentTimeMillis() - currentTime) + "ms");
		
		List<AlarmVo> alarmVoList = new ArrayList<AlarmVo>();
		alarmPageVo.setTotalRecord(alarmPageBo.getTotalRecord());
		if (null != alarmPageBo.getRows() && alarmPageBo.getRows().size() > 0) {
			for (AlarmEvent re : alarmPageBo.getRows()) {
			//	System.out.println(re.getCollectionTime());
				AlarmVo alarmVo = toVo(re,"query",0);
				alarmVo.setDataClass("1");
			
				alarmVoList.add(alarmVo);
			}
			alarmPageVo.setAlarms(alarmVoList);
		}

		return toSuccess(alarmPageVo);
	}
	
	/**
	 * 导出告警
	 * @param Vo
	 * @param session
	 * @param response
	 * @param request
	 */
	@RequestMapping("/exportNotRestoreAlarm")
	public void exportNotRestoreAlarm(String Vo,int index,HttpSession session,HttpServletResponse response,HttpServletRequest request) {
		//获取当前登录用户
		
		ILoginUser user = getLoginUser(session);
	//	alarmPageVo=	getNotRestoreAlarmData(alarmPageVo, user,"export");
		ExcelUtil<AlarmVo> exportUtil = new ExcelUtil<AlarmVo>();
		List<ExcelHeader> headers = new ArrayList<>();
	//	headers.add(new ExcelHeader("oper_date","序号"));
		headers.add(new ExcelHeader("instanceStatus","告警级别"));
		headers.add(new ExcelHeader("alarmContent","告警详细内容"));
		headers.add(new ExcelHeader("resourceName","告警来源"));
		headers.add(new ExcelHeader("ipAddress","IP地址"));
	/*	if(index==0 || index==1){//已恢复/未恢复警
			headers.add(new ExcelHeader("repeatNum","告警重复发生次数"));	
		}*/
	
		headers.add(new ExcelHeader("alarmType","告警分类"));
		if(index!=1){//已恢复告警去掉发生日期和时间
			headers.add(new ExcelHeader("collectionTimeShow","告警发生日期"));
			headers.add(new ExcelHeader("collection_Time","告警发生时间"));	
		}
		if(index!=2 && index != 0){
			headers.add(new ExcelHeader("recoveryTimeShow","告警恢复日期"));
			headers.add(new ExcelHeader("recover_Time","告警恢复时间"));	
		}

		List<AlarmVo> vos=	getNotRestoreData(Vo, user,index);//alarmPageVo.getAlarms();
		try {
			WebUtil.initHttpServletResponse("告警信息.xlsx", response, request);
			exportUtil.exportExcel("告警信息", headers, vos, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	//	return toSuccess(alarmPageVo);
	}
	public List<AlarmVo> getNotRestoreData(String Vo,ILoginUser user,int index){
		 List<AlarmVo> vos= new ArrayList<AlarmVo>();
		 AlarmVo alarmVo = new AlarmVo();
		 String alarmChecked=null;
		 String endTime=null;
		 String instanceStatus=null;
		 String isCheckedRadioOne=null;
		 String startTime=null;
		 String queryTime=null;
		 String iPorName=null;
		 String prentCategory=null;
			if(Vo != null && !"".equals(Vo)){
			//	String[] voDatas= new String [1];
				//voDatas[0]=Vo;
				
				JSONObject obj=JSONObject.parseObject(Vo);
				if (StringUtils.isNotEmpty(obj.getString("alarmChecked"))) {
					alarmChecked = obj.getString("alarmChecked");
				}
				if (StringUtils.isNotEmpty(obj.getString("endTime"))) {
					endTime = obj.getString("endTime");
				}
				if (StringUtils.isNotEmpty(obj.getString("instanceStatus"))) {
					instanceStatus = obj.getString("instanceStatus");
				}
				if (StringUtils.isNotEmpty(obj.getString("isCheckedRadioOne"))) {
					isCheckedRadioOne = obj.getString("isCheckedRadioOne");
				}
				if (StringUtils.isNotEmpty(obj.getString("queryTime"))) {
					queryTime = obj.getString("queryTime");
				}
				if (StringUtils.isNotEmpty(obj.getString("startTime"))) {
					startTime = obj.getString("startTime");
				}
				if (StringUtils.isNotEmpty(obj.getString("iPorName"))) {
					iPorName = obj.getString("iPorName");
				}
				if (StringUtils.isNotEmpty(obj.getString("prentCategory"))) {
					prentCategory = obj.getString("prentCategory");
				}
				
				
	}
			alarmVo.setAlarmChecked(alarmChecked);
			alarmVo.setQueryTime(queryTime);
			alarmVo.setEndTime(endTime);
			alarmVo.setStartTime(startTime);
			alarmVo.setInstanceStatus(instanceStatus);
			alarmVo.setIsCheckedRadioOne(isCheckedRadioOne);
			alarmVo.setiPorName(iPorName);
			alarmVo.setPrentCategory(prentCategory);
			vos=	 getexportData( alarmVo,index,user);
		
		return vos;
		 
	}
	public List<AlarmVo> getexportData( AlarmVo alarmVo,int index,ILoginUser user){
		List<AlarmVo> vos= new ArrayList<AlarmVo>();
		if(index==0){//未恢复
			SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR,SysModuleEnum.BUSSINESS,SysModuleEnum.LINK};
			AlarmEventQuery2 condition = toEventQuery(alarmVo, user, null, null, monitorList,"export");
		
			for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
				condition.getFilters().get(i).setRecovered(false);
				if(condition.getFilters().get(i).getSourceIDes()==null || condition.getFilters().get(i).getSourceIDes().size()==0 ){
					
					condition.getFilters().get(i).setSourceIDes(null);
				}
			}
			condition.setOrderFieldes(null);
		List<AlarmEvent> events=	alarmEventService.findAlarmEvent(condition);
		if(events!=null && events.size()!=0){
			for (AlarmEvent re : events) {
				AlarmVo alarmvo = toVo(re,"export",index);
				alarmVo.setDataClass("1");
			
				vos.add(alarmvo);
			}	
		}
		}else if(index==1){//已恢复
			 SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR,SysModuleEnum.BUSSINESS,SysModuleEnum.LINK,SysModuleEnum.OTHER};
				AlarmEventQuery2 condition = toEventQuery(alarmVo, user, null, null, monitorList,"export");
				for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
					condition.getFilters().get(i).setRecovered(true);
					if(condition.getFilters().get(i).getSourceIDes()==null || condition.getFilters().get(i).getSourceIDes().size()==0 ){
						
						condition.getFilters().get(i).setSourceIDes(null);
					}
					//告警关联列表显示相关告警信息，否则就显示正常恢复告警
					if(null != alarmVo && Util.isEmpty(alarmVo.getResourceId()) ){
						List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
						list.add(MetricStateEnum.NORMAL);
						condition.getFilters().get(i).setStates(list);
					}else if(null != alarmVo && !Util.isEmpty(alarmVo.getResourceId()) && (!Util.isEmpty(alarmVo.getOnclickAlarm()))){
						List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
						list.add(MetricStateEnum.NORMAL);
						condition.getFilters().get(i).setStates(list);
					}
				}
				List<AlarmEvent> events=	alarmEventService.findAlarmEvent(condition);
				if(events!=null && events.size()!=0){
					for (AlarmEvent re : events) {
						AlarmVo alarmvo = toVo(re,"export",index);
						alarmVo.setDataClass("2");
						vos.add(alarmvo);
					}
				}	
			
			
			
		}else if(index==2){//日志
			
			SysModuleEnum[] monitorList = {SysModuleEnum.SYSLOG,SysModuleEnum.CONFIG_MANAGER,SysModuleEnum.TRAP,SysModuleEnum.IP_MAC_PORT,SysModuleEnum.NETFLOW};
			AlarmEventQuery2 condition = toEventQuery(alarmVo,user,null,null, monitorList,"export");

			List<AlarmEvent> events=	alarmEventService.findAlarmEvent(condition);
			if(events!=null && events.size()!=0){
				for (AlarmEvent re : events) {
					AlarmVo alarmvo = toVo(re,"export",index);
					alarmVo.setDataClass("3");
					vos.add(alarmvo);
				}
			}	
			
		}else if(index==3){//第三方
			SysModuleEnum[] monitorList = {SysModuleEnum.OTHER};
			AlarmEventQuery2 condition = toEventQuery(alarmVo,user,null,null, monitorList,"export");
			for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
				condition.getFilters().get(i).setRecovered(false);
				if(condition.getFilters().get(i).getSourceIDes()==null || condition.getFilters().get(i).getSourceIDes().size()==0 ){
					
					condition.getFilters().get(i).setSourceIDes(null);
				}
			
			}
			List<AlarmEvent> events=	alarmEventService.findAlarmEvent(condition);
			if(events!=null && events.size()!=0){
				for (AlarmEvent re : events) {
					AlarmVo alarmvo = toVo(re,"export",index);
					alarmVo.setDataClass("4");
					vos.add(alarmvo);
				}
			}	
		}
		
		return vos;
		
	}
	@RequestMapping("/getNotRestoreAlarmForResourceInfoPage")
	public JSONObject getNotRestoreAlarm(AlarmPageVo alarmPageVo,String instanceid,String alarmType) {
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		eq.setOrderASC(false);
		eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
		List<String> idList = new ArrayList<String>(); 
		idList.add(instanceid);
		
		//资源告警列表显示主资源、子资源告警数据
		ResourceInstance ri;
		try {
			ri = resourceInstanceService.getResourceInstance(new Long(instanceid));
			if(ri != null && null != ri.getLifeState() && ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				List<ResourceInstance> resourcechildList = resourceInstanceService.getChildInstanceByParentId(ri.getId());
				if (resourcechildList!=null && resourcechildList.size()>0) {
					for(ResourceInstance riChild:resourcechildList){
						if(riChild.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							idList.add(String.valueOf(riChild.getId()));
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			logger.info("getNotRestoreAlarmForResourceInfoPage : " + e.getMessage());
			e.printStackTrace();
		} catch (InstancelibException e) {
			logger.info("getNotRestoreAlarmForResourceInfoPage : " + e.getMessage());
			e.printStackTrace();
		}
		
//		detail.setSysID(SysModuleEnum.TRAP);
		detail.setSourceIDes(idList);
		filters.add(detail);
		
		
		if("recovered".equals(alarmType)){
			detail.setRecovered(true);
			List<MetricStateEnum> mseList = new ArrayList<MetricStateEnum>();
			mseList.add(MetricStateEnum.NORMAL);
			detail.setStates(mseList);
		}else{
			detail.setRecovered(false);
		}
		
		eq.setFilters(filters);
		
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), eq);
		
		return toSuccess(alarmPageBo);
	}
	
	/**
	 * 首页获取未恢复告警
	 * 
	 * @param alarmVo
	 * @return
	 */
	@RequestMapping("/getNotRestoreMonitorAlarm")
	public JSONObject getMonitorAlarm(AlarmPageVo alarmPageVo,HttpSession session,String type,long groupId,long domainId) {
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR};
		if(type!=null){
			alarmPageVo.getCondition().setInstanceStatus(type);
		}
		if(alarmPageVo.getCondition().getPrentCategory()==null){
			alarmPageVo.getCondition().setInstanceStatus("all");
		}
		
		List<String> sources= new ArrayList<String>();
		AlarmEventQuery2 condition = toEventQuery2(alarmPageVo.getCondition(),user,alarmPageVo.getSort(),alarmPageVo.getOrder(), monitorList, groupId, domainId);
		List<MetricStateEnum> state=new ArrayList<MetricStateEnum>();
		for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
			condition.getFilters().get(i).setRecovered(false);
			if(condition.getFilters().get(i).getExt2()!=null &&condition.getFilters().get(i).getExt2().equals("App")){
				condition.getFilters().get(i).setExt2(null);
			}
			List<String> list=	condition.getFilters().get(i).getSourceIDes();
			for (int j = 0; j < list.size(); j++) {
				if(list.get(j)!=null){
					sources.add(list.get(j));
				}
			}
			if(sources.size()!=0){
				condition.getFilters().get(i).setSourceIDes(sources);
			}else{
				condition.getFilters().get(i).setSourceIDes(null);
			}
		
			if(type.equals("down")){
				state.add(MetricStateEnum.CRITICAL);
			}else if(type.equals("metric_error")){
				state.add(MetricStateEnum.SERIOUS);
			}else{
				state.add(MetricStateEnum.WARN);
			}
			condition.getFilters().get(i).setStates(state);
		}
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), condition);
		List<AlarmVo> alarmVoList = new ArrayList<AlarmVo>();
		alarmPageVo.setTotalRecord(alarmPageBo.getTotalRecord());
		if (null != alarmPageBo.getRows() && alarmPageBo.getRows().size() > 0) {
			for (AlarmEvent re : alarmPageBo.getRows()) {
				AlarmVo alarmVo = toVo(re,"query",0);
				alarmVo.setDataClass("1");
				alarmVoList.add(alarmVo);
			}
			alarmPageVo.setAlarms(alarmVoList);
		}

		return toSuccess(alarmPageVo);
	}
	
	/**
	 * 通过告警ID查询知识库关联信息
	 * @param alarmId
	 * @return
	 */
	@RequestMapping("/queryAlarmKnowledgeList")
	public JSONObject queryAlarmKnowledgeList(AlarmKnowledgePageVo pageVo ,long alarmId){
		AlarmKnowledgePageBo bo = alarmApi.getAlarmKnowledge(pageVo.getStartRow(), 
				pageVo.getRowCount(), alarmId);
		pageVo.setTotalRecord(bo.getTotalRecord());
		pageVo.setAlarmKnowledge(bo.getRows());
		
		return toSuccess(pageVo);
	}
	/**
	 * 通过告警ID查询已恢复告警对象
	 * 
	 * @param alarmId
	 * @param alarmType 0、所有 1、未恢复 2、已恢复
	 * @return
	 */
	@RequestMapping("/getAlarmById")
	public JSONObject getAlarmById(long alarmId, String alarmType) {

		AlarmEvent resourceEvent = alarmApi.getAlarmById(alarmId, alarmType);
		AlarmVo alarmVo = null;
		if(resourceEvent != null){
			  alarmVo = toVo(resourceEvent,"query",1);
			if(InstanceStateEnum.CRITICAL.equals(resourceEvent.getLevel())){
				Map<String, String> relation = alarmApi.getAlarmRelationship(alarmId);
				alarmVo.setRelation(relation);
			}
		}
		
		return toSuccess(alarmVo);
	}
    
	private AlarmEventQuery2 toEventQuery(AlarmVo vo, ILoginUser user, String sort, String order, SysModuleEnum[] sysModules,String type) {
		vo = vo == null ? new AlarmVo() : vo;
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		//属性排序
		if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
			if("ASC".equals(order.toUpperCase())){
				eq.setOrderASC(true);
			}else{
				eq.setOrderASC(false);
			}
			if("alarmContent".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.CONTENT});
			} else if("resourceName".endsWith(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.SOURCE_NAME});
			} else if("alarmType".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.EXT0});
			} else if("collectionTime".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
			} else if("recoveryTime".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
			} else if("alarmStatus".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.LEVEL});
			} else if("itmsData".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.ITSM_DATA});
			}
		}
		
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		for(int i = 0; i < sysModules.length; i++){
			AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			detail.setExt1(vo.getChildCategory());
			detail.setExt2(vo.getPrentCategory());
			//资源名称或者IP查询
			if(null != vo.getiPorName()){
				detail.setLikeSourceIpOrName(vo.getiPorName());
			}
			try {
				if (null != vo.getResourceId()) {
					List<String> sList = new  ArrayList<String>();
				
					//告警管理--告警关联列表
					if(Util.isEmpty(vo.getOnclickAlarm())){
						ResourceInstance source = resourceInstanceService.getResourceInstance(vo.getResourceId());
						if(source!=null){
							if(source.getParentId()!=0){
								sList.add(String.valueOf(source.getParentId()));
							}else{
								sList.add(vo.getResourceId().toString());
							}
						}
						
						detail.setSourceIDes(sList);
						detail.setExt3(vo.getMetricId());
						detail.setRecoveryEventID(vo.getRecoveryAlarmId());
						SysModuleEnum moduleEnum= SysModuleEnum.valueOf(vo.getSysID());
						detail.setSysID(moduleEnum);
						logger.info("SysModuleEnum is "+moduleEnum.name());
						logger.error("SysModuleEnum is "+moduleEnum.name());
					}else{
						//资源告警列表显示主资源、子资源告警数据
						ResourceInstance ri = resourceInstanceService.getResourceInstance(vo.getResourceId());
						if(ri != null && null != ri.getLifeState() && ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							List<ResourceInstance> resourcechildList = resourceInstanceService.getChildInstanceByParentId(ri.getId());
							for(int j = 0; resourcechildList != null && j < resourcechildList.size(); j++){
								ResourceInstance  riList = resourcechildList.get(j);
								if(riList.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
									sList.add(String.valueOf(riList.getId()));
								}
							}
							sList.add(vo.getResourceId().toString());
						}
						detail.setSourceIDes(sList);
					}
				} else {
					if(sysModules[i].equals(SysModuleEnum.MONITOR) || sysModules[i].equals(SysModuleEnum.CONFIG_MANAGER) ){
						List<ResourceInstanceBo> rbList = resourceApi.getResources(user);
						List<String> resourceIdList = new  ArrayList<String>();
						Set<Long> parentInstanceIdSet = new HashSet<Long>();
						List<Long> parentInstanceIdList = new ArrayList<Long>();
						for(ResourceInstanceBo rb : rbList){
							//删除状态,未监控状态时不显示
							if(null != rb.getLifeState() && rb.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
								parentInstanceIdSet.add(rb.getId());
								parentInstanceIdList.add(rb.getId());
							}
						}
						if(!parentInstanceIdSet.isEmpty()){
//							for (long instanceId:parentInstanceIdSet) {
//								List<ResourceInstance> instanceslist=resourceInstanceService.getChildInstanceByParentId(instanceId);
//								for(int j = 0; instanceslist != null && j < instanceslist.size(); j ++){
//									resourceIdList.add(String.valueOf(instanceslist.get(j).getId()));
//								}
//							}
							if(type.equals("export")){
								List<Long> childInstanceIds = resourceInstanceService.getAllChildrenInstanceIdbyParentId(parentInstanceIdSet,InstanceLifeStateEnum.MONITORED);
								for(int j = 0; childInstanceIds != null && j < childInstanceIds.size(); j ++){
									resourceIdList.add(childInstanceIds.get(j).toString());
								}
							}
						/*	List<Long> childInstanceIds = resourceInstanceService.getAllChildrenInstanceIdbyParentId(parentInstanceIdSet,InstanceLifeStateEnum.MONITORED);
							for(int j = 0; childInstanceIds != null && j < childInstanceIds.size(); j ++){
								resourceIdList.add(childInstanceIds.get(j).toString());
							}*/
						}
						for(int j = 0; j < parentInstanceIdList.size(); j++){
							resourceIdList.add(parentInstanceIdList.get(j).toString());
						}
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
							logger.error(Arrays.asList(resourceIdList));
							detail.setSourceIDes(resourceIdList);
						}else{
							resourceIdList.add("-1");
							detail.setSourceIDes(resourceIdList);
						}
					}else if(sysModules[i].equals(SysModuleEnum.BUSSINESS)){
						List<String> resourceIdList = new  ArrayList<String>();
						List<BizMainBo> bos=	bizUserRelApi.getBizlistByUserId(user.getId());
						if(bos==null || bos.size()==0){
							resourceIdList.add("0");
						}else{
							for (int j = 0; j < bos.size(); j++) {
								resourceIdList.add(String.valueOf(bos.get(j).getId()));
							}
							
						}
						detail.setSourceIDes(resourceIdList);	
						
					}
				}
			} catch (InstancelibException e) {
				logger.error("getChildrenResource ", e); 
			}
			
			if (null != vo.getIsCheckedRadioOne() || null != vo.getIsCheckedRadioTwo()) {
				if ("isChecked".equals(vo.getIsCheckedRadioOne())) {
					// 如果是自定义时间
					if("6".equals(vo.getQueryTime())){
						detail.setStart(vo.getStartTime());
						detail.setEnd(vo.getEndTime());
					}else{
						detail.setStart(getBeginTime(vo.getQueryTime()));
						detail.setEnd(getCurrentTime());
					}
				}
				if ("isChecked".equals(vo.getIsCheckedRadioTwo())) {
					detail.setStart(vo.getStartTime());
					detail.setEnd(vo.getEndTime());
				}
			} else {
				detail.setStart(null);
				detail.setEnd(null);
			}
			if ("all".equals(vo.getInstanceStatus())
					|| vo.getInstanceStatus() == null) {
				detail.setStates(null);
			} else {
				List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
				list.add(getMetricStateEnum(vo.getInstanceStatus()));
				detail.setStates(list);
			}
			if(vo.getIpAddress() != null && !(vo.getIpAddress().equals(""))){
				detail.setLikeSourceIP(vo.getIpAddress());
			}

			filters.add(detail);
		}
		eq.setFilters(filters);
		return eq;
	}
	private AlarmEventQuery2 toEventQuery2(AlarmVo vo, ILoginUser user, String sort, String order, SysModuleEnum[] sysModules,long groupId,long domainId) {
		vo = vo == null ? new AlarmVo() : vo;
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		//属性排序
		if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
			if("ASC".equals(order.toUpperCase())){
				eq.setOrderASC(true);
			}else{
				eq.setOrderASC(false);
			}
			if("alarmContent".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.CONTENT});
			} else if("resourceName".endsWith(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.SOURCE_NAME});
			} else if("alarmType".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.EXT0});
			} else if("collectionTime".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
			} else if("recoveryTime".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
			} else if("alarmStatus".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.LEVEL});
			} else if("itmsData".equals(sort)){
				eq.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.ITSM_DATA});
			}
		}
		
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		for(int i = 0; i < sysModules.length; i++){
			AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
			detail.setSysID(sysModules[i]);
			detail.setExt1(vo.getChildCategory());
			detail.setExt2(vo.getPrentCategory());
			//资源名称或者IP查询
			if(null != vo.getiPorName()){
				detail.setLikeSourceIpOrName(vo.getiPorName());
			}
			try {
				if(sysModules[i].equals(SysModuleEnum.MONITOR)){
					long currentTime = System.currentTimeMillis();
					//
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					if(domainId!=0){
						List<Long> domainIds = new ArrayList<Long>();
						domainIds.add(domainId);
						queryBo.setDomainIds(domainIds);
					}
					//获取用户、域、资源类别下的资源实例
					Set<Long> parentInstaceIdSet = new HashSet<Long>();
					String type = vo.getPrentCategory();
					if(type!=null && type!=""){
						//通过“主机、网络、应用”获取子资源类别
						List<String> categories = this.getCategorIds(type);
						queryBo.setCategoryIds(categories);
					}
					List<ResourceInstanceBo> instanceBos = resourceApi.getResources(queryBo);
					for(int j = 0; instanceBos != null && j < instanceBos.size(); j++){
						ResourceInstanceBo instance = instanceBos.get(j);
						if(instance!=null && instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							parentInstaceIdSet.add(instance.getId());
						}
					}
					
					List<String> resultInstanceIds = new ArrayList<String>();
					if(groupId != 0){
						Set<Long> groupInstanceIdSet = new HashSet<Long>();
						List<CustomGroupBo> boList = resourceGroupApi.getList(user.getId());
						List<Long> resourceIds = new ArrayList<Long>();
						CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(groupId);
						boList2Tree(groupBo, boList, resourceIds);
						List<String> resourceDatas= new ArrayList<String>();
						for (Long id : resourceIds) {
							resourceDatas.add(String.valueOf(id));
						}
						List<String> groupInstanceIds = resourceDatas != null ? resourceDatas : null;//资源组里的所有资源
					//	List<String> groupInstanceIds = groupBo != null ? groupBo.getResourceInstanceIds() : null;//资源组里的所有资源
						if(!parentInstaceIdSet.isEmpty() && groupInstanceIds != null){
							for(Long parentInstanceId : parentInstaceIdSet){
								if(groupInstanceIds.contains(String.valueOf(parentInstanceId))){
									groupInstanceIdSet.add(parentInstanceId);
									resultInstanceIds.add(String.valueOf(parentInstanceId));
								}
							}
						/*	if(!groupInstanceIdSet.isEmpty()){
								List<Long> childInstanceIdList = resourceInstanceService.getAllChildrenInstanceIdbyParentId(groupInstanceIdSet,InstanceLifeStateEnum.MONITORED);
								for(int j = 0; childInstanceIdList != null && j < childInstanceIdList.size(); j++){
									resultInstanceIds.add(String.valueOf(childInstanceIdList.get(j)));
								}
							}*/
						}
					} else {
						if(!parentInstaceIdSet.isEmpty()){
							for(Long parentInstanceId : parentInstaceIdSet){
								resultInstanceIds.add(parentInstanceId.toString());
							}
						/*	List<Long> childInstanceIdList = resourceInstanceService.getAllChildrenInstanceIdbyParentId(parentInstaceIdSet,InstanceLifeStateEnum.MONITORED);
							for(int j = 0; childInstanceIdList != null && j < childInstanceIdList.size(); j++){
								resultInstanceIds.add(childInstanceIdList.get(j).toString());
							}*/
						}
					}
					
					detail.setSourceIDes(resultInstanceIds);
					logger.info("Query id of resource elapsed time: " + (System.currentTimeMillis() - currentTime) + "ms");
				}
			} catch (Exception e) {
				logger.error("getChildrenResource ", e); 
			}
			
			if (null != vo.getIsCheckedRadioOne() || null != vo.getIsCheckedRadioTwo()) {
				if ("isChecked".equals(vo.getIsCheckedRadioOne())) {
					detail.setStart(getBeginTime(vo.getQueryTime()));
					detail.setEnd(getCurrentTime());
				}
				if ("isChecked".equals(vo.getIsCheckedRadioTwo())) {
					detail.setStart(vo.getStartTime());
					detail.setEnd(vo.getEndTime());
				}
			} else {
				detail.setStart(null);
				detail.setEnd(null);	
			}
			if ("all".equals(vo.getInstanceStatus())
					|| vo.getInstanceStatus() == null) {
				detail.setStates(null);
			} else {
				List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
				list.add(getMetricStateEnum(vo.getInstanceStatus()));
				detail.setStates(list);
			}
			if(vo.getIpAddress() != null && !(vo.getIpAddress().equals(""))){
				detail.setLikeSourceIP(vo.getIpAddress());
			}
			filters.add(detail);
		}
		eq.setFilters(filters);
		return eq;
	}
	private void boList2Tree(CustomGroupBo parentBo, List<CustomGroupBo> boList, List<Long> resourceIds){
		for (int i = 0; parentBo != null && parentBo.getResourceInstanceIds() != null && i < parentBo.getResourceInstanceIds().size(); i++) {
			resourceIds.add(Long.valueOf(parentBo.getResourceInstanceIds().get(i)));
		}
		for (int i = 0; i < boList.size(); i++) {
			CustomGroupBo bo = boList.get(i);
			if(bo.getPid() != null && parentBo != null && parentBo.getId().longValue() == bo.getPid().longValue()){
				boList2Tree(bo, boList, resourceIds);
			}
		}
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

	/**
	 * 获取已恢复告警
	 * 
	 * @param alarmVo
	 * @return
	 */
	@RequestMapping("/getRestoreAlarm")
	public JSONObject getRestoreAlarm(AlarmPageVo alarmPageVo,HttpSession session) {
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR,SysModuleEnum.BUSSINESS,SysModuleEnum.LINK,SysModuleEnum.OTHER};//,SysModuleEnum.SYSLOG,SysModuleEnum.CONFIG_MANAGER,SysModuleEnum.TRAP,SysModuleEnum.IP_MAC_PORT,SysModuleEnum.NETFLOW
		AlarmEventQuery2 condition = toEventQuery(alarmPageVo.getCondition(),user,alarmPageVo.getSort(),alarmPageVo.getOrder(), monitorList,"query");
		for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
			condition.getFilters().get(i).setRecovered(true);
			if(condition.getFilters().get(i).getSourceIDes()==null || condition.getFilters().get(i).getSourceIDes().size()==0 ){
				condition.getFilters().get(i).setSourceIDes(null);
			}
			//告警关联列表显示相关告警信息，否则就显示正常恢复告警
			if(null != alarmPageVo.getCondition() && Util.isEmpty(alarmPageVo.getCondition().getResourceId()) ){
				List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
				list.add(MetricStateEnum.NORMAL);
				condition.getFilters().get(i).setStates(list);
			}else if(null != alarmPageVo.getCondition() && !Util.isEmpty(alarmPageVo.getCondition().getResourceId()) && (!Util.isEmpty(alarmPageVo.getCondition().getOnclickAlarm()))){
				List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
				list.add(MetricStateEnum.NORMAL);
				condition.getFilters().get(i).setStates(list);
			}
		}
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), condition);
		List<AlarmVo> alarmVoList = new ArrayList<AlarmVo>();
		alarmPageVo.setTotalRecord(alarmPageBo.getTotalRecord());
		if (null != alarmPageBo.getRows() && alarmPageBo.getRows().size() > 0) {
			for (AlarmEvent re : alarmPageBo.getRows()) {
				
				AlarmVo alarmVo = toVo(re,"query",1);
				alarmVo.setDataClass("2");
				alarmVoList.add(alarmVo);
			}
			alarmPageVo.setAlarms(alarmVoList);
		}
		return toSuccess(alarmPageVo);
	}

	/**
	 * 获取系统日志告警数据
	 * 
	 * @param AlarmPageVo
	 * @return
	 */
	@RequestMapping("/getSyslogAlarm")
	public JSONObject getSyslogAlarm(AlarmPageVo alarmPageVo,HttpSession session){
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		SysModuleEnum[] monitorList = {SysModuleEnum.SYSLOG,SysModuleEnum.CONFIG_MANAGER,SysModuleEnum.TRAP,SysModuleEnum.IP_MAC_PORT,SysModuleEnum.NETFLOW};
		AlarmEventQuery2 condition = toEventQuery(alarmPageVo.getCondition(),user,alarmPageVo.getSort(),alarmPageVo.getOrder(), monitorList,"query");
		/*
		for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
			List<String> sourceList = condition.getFilters().get(i).getSourceIDes();
			// 新增ip_mac_port ID
			sourceList = sourceList == null ? new ArrayList<String>() : sourceList;
			List<Long> ip_mac_portList = ipMacPortApi.getMacBaseIds();
			for(int j = 0; ip_mac_portList != null && j < ip_mac_portList.size(); j++){
				sourceList.add(String.valueOf(ip_mac_portList.get(j)));
			}
			condition.getFilters().get(i).setSourceIDes(sourceList);
		}
		*/
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), condition);
		List<AlarmVo> alarmVoList = new ArrayList<AlarmVo>();
		alarmPageVo.setTotalRecord(alarmPageBo.getTotalRecord());
		if (null != alarmPageBo.getRows() && alarmPageBo.getRows().size() > 0) {
			for (AlarmEvent re : alarmPageBo.getRows()) {
				AlarmVo alarmVo = toVo(re,"query",2);
				alarmVo.setDataClass("3");
				alarmVoList.add(alarmVo);
			}
			alarmPageVo.setAlarms(alarmVoList);
		}

		return toSuccess(alarmPageVo);
	}
	
	/**
	 * 获取系统第三方告警数据
	 * 
	 * @param AlarmPageVo
	 * @return
	 */
	@RequestMapping("/getThirdPartyAlarm")
	public JSONObject getThirdPartyAlarm(AlarmPageVo alarmPageVo,HttpSession session){
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		SysModuleEnum[] monitorList = {SysModuleEnum.OTHER};
		AlarmEventQuery2 condition = toEventQuery(alarmPageVo.getCondition(),user,alarmPageVo.getSort(),alarmPageVo.getOrder(), monitorList,"query");
		for(int i = 0; condition.getFilters() != null && i < condition.getFilters().size(); i++){
			condition.getFilters().get(i).setRecovered(false);
		
		}
		AlarmPageBo alarmPageBo = alarmApi.getAlarmList(alarmPageVo.getStartRow(), alarmPageVo.getRowCount(), condition);
		List<AlarmVo> alarmVoList = new ArrayList<AlarmVo>();
		alarmPageVo.setTotalRecord(alarmPageBo.getTotalRecord());
		if (null != alarmPageBo.getRows() && alarmPageBo.getRows().size() > 0) {
			for (AlarmEvent re : alarmPageBo.getRows()) {
				AlarmVo alarmVo = toVo(re,"query",3);
				alarmVo.setDataClass("4");
				alarmVoList.add(alarmVo);
			}
			alarmPageVo.setAlarms(alarmVoList);
		}
		return toSuccess(alarmPageVo);
	}
	
	
	/**
	 * 通过告警ID查询发送信息
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/getAlarmNotify")
	public JSONObject getAlarmNotify(AlarmNotifyPageVo pageVo, long alarmId) {

		AlarmNotifyPageBo pageBo = alarmApi.getAlarmNotify(
				pageVo.getStartRow(), pageVo.getRowCount(), alarmId);
		pageVo.setTotalRecord(pageBo.getTotalRecord());
		List<AlarmNotifyVo> alarmNotifyVoList = new ArrayList<AlarmNotifyVo>();
		if (pageBo.getRows() != null && pageBo.getRows().size() > 0) {
			for (AlarmNotify an : pageBo.getRows()) {
				alarmNotifyVoList.add(toVo(an));
			}
			pageVo.setAlarmNotify(alarmNotifyVoList);
		}
		return toSuccess(pageVo);
	}

	
	private AlarmNotifyVo toVo(AlarmNotify alarmNotify) {
		AlarmNotifyVo vo = new AlarmNotifyVo();
		if (NotifyState.NOT_SEND == alarmNotify.getState()) {
			vo.setNotifyStatus("禁止发送");
		} else if(NotifyState.SUCCESS == alarmNotify.getState()) {
			vo.setNotifyStatus("发送成功");
		}else if(NotifyState.NOT_SUCCESS ==alarmNotify.getState()){
			vo.setNotifyStatus("发送失败");
		}else if(NotifyState.WAITE_SEND ==alarmNotify.getState()){
			vo.setNotifyStatus("等待发送");
		}else if(NotifyState.SENDING ==alarmNotify.getState()){
			vo.setNotifyStatus("发送中");
		}else{
			vo.setNotifyStatus(null);
		}
		vo.setUserName(null == userApi.get(alarmNotify.getNotifyUserID()) ? ""
				: userApi.get(alarmNotify.getNotifyUserID()).getName());
		vo.setNotifytType(alarmNotify.getNotifyType().toString());
		vo.setNotifyAdrr(alarmNotify.getNotifyAddr());
		vo.setSendTime(alarmNotify.getNotifyTime());
		
		return vo;
	}

	/**
	 * 告警对象转换为VO
	 * 
	 * @param resourceEvent
	 * @return
	 */
	private AlarmVo toVo(AlarmEvent alarmEvent,String way,int index) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		AlarmVo vo = new AlarmVo();
		//alarmEvent.getRecoverKey()
		vo.setRecoverKey(alarmEvent.getRecoverKey());
		vo.setAlarmId(alarmEvent.getEventID());
		vo.setAlarmContent(alarmEvent.getContent());
		vo.setResourceName(alarmEvent.getSourceName());
	    ResourceDef resourceDef = this.capacityService.getResourceDefById(alarmEvent.getExt0());
	    if ((alarmEvent.getSysID() != null) && (alarmEvent.getSysID() == SysModuleEnum.MONITOR) && (resourceDef != null))
	      vo.setAlarmType(resourceDef.getName());
	    else if ((alarmEvent.getSysID() != null) && (alarmEvent.getSysID() == SysModuleEnum.LINK) && (resourceDef != null))
	      vo.setAlarmType(resourceDef.getName());
	    else {
	      vo.setAlarmType(alarmEvent.getExt0());
	    }
//		vo.setAlarmType(null == capacityService.getResourceDefById(alarmEvent.getExt0())?"":capacityService.getResourceDefById(alarmEvent.getExt0()).getName());
		if(way.equals("export")){//导出，需要设置部分参数
			 try {
				 if(alarmEvent.getCollectionTime()==null){
						vo.setCollectionTimeShow("");
				 }else{
					 String colltime=sdf2.format(alarmEvent.getCollectionTime());
						vo.setCollectionTimeShow(colltime);
				 }
				 if(alarmEvent.getRecoveryTime()==null){
						vo.setRecoveryTimeShow("");
				 }else{
					 String recovertime=sdf2.format(alarmEvent.getCollectionTime());
						vo.setRecoveryTimeShow(recovertime);
				 }
			
				
				 vo.setCollectionTime(alarmEvent.getCollectionTime());
					vo.setRecoveryTime(alarmEvent.getCollectionTime());	
					if(getInstStateEnumString(alarmEvent.getLevel()).equals("yellow")){
						vo.setInstanceStatus(InstanceStateEnum.getValue(InstanceStateEnum.WARN));
					}else if(getInstStateEnumString(alarmEvent.getLevel()).equals("orange")){
						vo.setInstanceStatus(InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS));
					}else if(getInstStateEnumString(alarmEvent.getLevel()).equals("red")){
						vo.setInstanceStatus(InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL));
					}else if(getInstStateEnumString(alarmEvent.getLevel()).equals("green")){
						vo.setInstanceStatus(InstanceStateEnum.getValue(InstanceStateEnum.NORMAL));
					}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			vo.setInstanceStatus(getInstStateEnumString(alarmEvent.getLevel()));
			 vo.setCollectionTime(alarmEvent.getCollectionTime());
				vo.setRecoveryTime(alarmEvent.getCollectionTime());	
		}
	   
		vo.setAcquisitionTime(getAcquisitionDate(alarmEvent.getSourceID(),alarmEvent.getExt3(),alarmEvent.getExt6()));
		vo.setIpAddress(alarmEvent.getSourceIP());
		vo.setResourceId(Long.parseLong(alarmEvent.getSourceID()));
		
		vo.setMetricId(alarmEvent.getExt3());
		vo.setRecoveryAlarmId(alarmEvent.getRecoveryEventID());
		vo.setSysID(alarmEvent.getSysID().name());
//		vo.setAlertID(alarmEvent.getExt9());//金圆临时字段
		if(alarmEvent.getHandleType()!=null){
			vo.setHandType(alarmEvent.getHandleType().name());
		}else{
			vo.setHandType(null);
		}
		if(alarmEvent.getItsmData() != null){
			vo.setItsmOrderId(alarmEvent.getItsmData().getItsmOrderID());
			if(ItsmOrderStateEnum.FINISH == alarmEvent.getItsmData().getState()){
				vo.setItmsData("已处理");
			}else if(ItsmOrderStateEnum.HANDLE == alarmEvent.getItsmData().getState()){
				vo.setItmsData("处理中");
			}else if(ItsmOrderStateEnum.NOT_SEND == alarmEvent.getItsmData().getState()){
				vo.setItmsData("未下发");
			}else{
				vo.setItmsData(null);
			}
		}else{
			vo.setItmsData(null);
		}
		vo.setSnapShotJSON(alarmEvent.getExt7());
		if(way.equals("export")){//导出，需要设置部分参数
			vo.setCollection_Time(sdf.format(alarmEvent.getCollectionTime()));
			if(alarmEvent.getCollectionTime()==null){
				vo.setRecover_Time("");
			}else{
				vo.setRecover_Time(sdf.format(alarmEvent.getCollectionTime()));
				vo.setRecoveryTimeShow(sdf2.format(alarmEvent.getCollectionTime()));
			}
			/*if(index==2 || index==3){
				vo.setRepeatNum(1);
			}else{
				vo.setRepeatNum(getRepeatNum(alarmEvent.getSourceID(),alarmEvent.getExt3(),alarmEvent.getExt6(),vo.getCollectionTime().getTime()));
			}*/
			
		}
		return vo;
	}
   /**
    * 获取采集时间
    * @param resourceId
    * @param metricId
    * @return
    */
	private Date getAcquisitionDate(String resourceId,String metricId,String isMetric){
	 if(!Util.isEmpty(metricId)){
		long sourceId = 0;
		if(resourceId != null){
			sourceId = Long.parseLong(resourceId);
		} 
		MetricData md  = null;
			if("0".equals(isMetric)){
				//性能指标0
				md = metricDataService.getMetricPerformanceData(sourceId, metricId);
			}else if("1".equals(isMetric)){
				//可用性指标1
				md = metricDataService.getMetricAvailableData(sourceId, metricId);
			}else if("2".equals(isMetric)){
				//信息性指标2
//				md = metricDataService.getMetricInfoData(sourceId, metricId);
				md=infoMetricQueryAdaptService.getMetricInfoData(sourceId, metricId);
			}else if("3".equals(isMetric) || "4".equals(isMetric)){
				//自定义可用性指标3 自定义性能指标4
				md = metricDataService.getCustomerMetricData(sourceId, metricId);
			}
			if(md != null){
				return	md.getCollectTime();
			}
		}
		return null;
	}
	private long getRepeatNum(String resourceId,String metricId,String isMetric,long current){
		long repeatNum=0;
		String monitorFeq=null;
		long monitorFeqTime=0L;
		String monitorFeqUnit=null;
		 try {
			if(!Util.isEmpty(metricId)){
					long sourceId = 0;
					if(resourceId != null){
						sourceId = Long.parseLong(resourceId);
					} 
					ProfileMetric metric  = null;
					metric=profileService.getMetricByInstanceIdAndMetricId(sourceId, metricId);
					MetricData md  = null;
					
					if("0".equals(isMetric)){
						//性能指标0
						md = metricDataService.getMetricPerformanceData(sourceId, metricId);
					}else if("1".equals(isMetric)){
						//可用性指标1
						md = metricDataService.getMetricAvailableData(sourceId, metricId);
					}else if("2".equals(isMetric)){
						//信息性指标2
//						md = metricDataService.getMetricInfoData(sourceId, metricId);
						md=infoMetricQueryAdaptService.getMetricInfoData(sourceId, metricId);
					}else if("3".equals(isMetric) || "4".equals(isMetric)){
						//自定义可用性指标3 自定义性能指标4
						md = metricDataService.getCustomerMetricData(sourceId, metricId);
					}
				
			
				//	metric=	profileService.getMetricByProfileIdAndMetricId(md.getProfileId(), md.getMetricId());
					if(metric!=null){
				int flapping=	metric.getAlarmFlapping();
				 monitorFeq=	metric.getDictFrequencyId();
				 monitorFeqTime=Long.parseLong(monitorFeq.replaceAll("[a-zA-Z]", ""));
				 monitorFeqUnit=monitorFeq.replaceAll("[0-9]", "");
				long collectTime=0;
				if(md==null){
					
				}else{
					//重复次数=(当前时间-最新采集时间)/采集周期*flapping次数
				collectTime=md.getCollectTime().getTime();
				if(monitorFeqUnit.equals("min")){//分钟
					long time=(collectTime-current)/1000/60;
						repeatNum=Math.abs(time/(monitorFeqTime*flapping));
					}else if(monitorFeqUnit.equals("hour")){//小时
						long time=(current-collectTime)/1000/60/60;
						repeatNum=Math.abs(time/(monitorFeqTime*flapping));
					}else{//天s
						long time=(current-collectTime)/1000/60/60/24;
						repeatNum=Math.abs(time/(monitorFeqTime*flapping));
						
					}
				}
				
		
				
			}

					
					}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return repeatNum;
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
			case "metric_unkwon":
				ise = MetricStateEnum.UNKOWN;
				break;
			}
		}
		return ise;
	}

	private static String getInstStateEnumString(InstanceStateEnum isEnum) {
		String ise = "gray";
		isEnum = isEnum == null ? InstanceStateEnum.UNKNOWN_NOTHING : isEnum; 
		switch (isEnum) {
		case CRITICAL:
			ise = "red";
			break;
		case SERIOUS:
			ise = "orange";
			break;
		case WARN:
			ise = "yellow";
			break;
		case NORMAL:
		case NORMAL_NOTHING:
			ise = "green";
			break;
		default:
//			ise = "gray";
			ise = "green";
			break;
		}
		return ise;
	}

	/**
	 * 通过告警id查询alert告警数据
	 * 
	 * rdcnt: reload count ,alert网络问题错误，只能重新读取3次；
	 * @return
	 */
	@RequestMapping("/getAlertMsg")
	public void getAlertMsg(long id, String name, Integer rdcnt,HttpServletRequest req, HttpServletResponse resp) {
		
		String host = req.getRemoteHost();
		
		try {
			if (!Util.isEmpty(name)) {
				User user = userApi.getByAccount(name);
				if (user != null) {
					long userId=user.getId();
					boolean isRefresh = getRefreshStatus(host, userId);
					//当前用户没有告警更新 ，Alert 数据解析失败3次后，不再提供数据
					if(!isRefresh && rdcnt >= 3){
						return;
					}
					
					byte[] alarmNumber = null;
					AlarmNotify alarm = alarmNotifyService.getNotifyByID(id);
					if ((alarm != null && alarm.getNotifyTime() != null) || id == 0) {
						List<AlarmNotify> alarmList = null;
						long currenTime = getCurrentTime().getTime();
						if (id != 0) {
							long alarmTime = alarm.getNotifyTime().getTime();
							if (currenTime - 30 * 60 * 1000 <= alarmTime) {
								alarmList = alarmNotifyService.findByTime(NotifyTypeEnum.alert, user.getId(), alarm.getNotifyTime(), null);
							} else {
								alarmList = alarmNotifyService.findByTime(NotifyTypeEnum.alert, user.getId(), new Date(currenTime - 30 * 60 * 1000), getCurrentTime());
							}
						} else {
							alarmList = alarmNotifyService.findByTime(NotifyTypeEnum.alert, user.getId(), new Date(currenTime - 30 * 60 * 1000), getCurrentTime());
						}
						if (alarmList != null && alarmList.size() > 0) {
							resp.setContentType("application/octet-stream");
							OutputStream out = null;
							try {
								out = resp.getOutputStream();
								List<Long> alertIdList = new ArrayList<Long>();
								for (AlarmNotify anf : alarmList) {
									JSONObject obj = new JSONObject();
									alarmNumber = toByte(obj.toJSONString(toAlertClientVo(anf)));
									out.write(alarmNumber);
									alertIdList.add(anf.getNotifyID());
								}
								alarmNotifyService.updateNotifyState(alertIdList, NotifyState.SUCCESS);
							} catch (IOException e) {
								logger.error("alertClient type conversion error", e);
							} finally {
								try {
									if (out != null) {
										out.flush();
										out.close();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("alertClient send failed", e);
		}
	}
	
	  public byte[] toByte(String data){
	       
	      int length = 0;
	      String version ="0001";
	        byte[] dataByte = new byte[0];
	        try {
	            dataByte = data.getBytes("GBK");
	            length = dataByte.length;
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        byte []dataLen= intToBytes(length); //数据长度
	        byte [] versionByte = version.getBytes();
	        ByteBuffer returnBuffer=ByteBuffer.allocate(8+dataByte.length);

	        returnBuffer.put(versionByte);
	        returnBuffer.put(dataLen);
	        returnBuffer.put(dataByte);
	        
	        return returnBuffer.array();
	    }
	  
	public static byte[] intToBytes(int num) {
	    byte[] b = new byte[4];
	    for (int i = 0; i < 4; i++) {
	     b[i] = (byte) (num >>> (24 - i * 8));
	    }
	    return b;
	 }
	
	/**
	 * alarmNotify 转换 AlertClientVo
	 * @param alarmNotify
	 * @return
	 */
	private AlertClientVo toAlertClientVo(AlarmNotify alarmNotify){
		AlertClientVo acv = new AlertClientVo();
		acv.setSeq(alarmNotify.getNotifyID());
		acv.setServerity(getAlarmEnumInt(alarmNotify.getAlarmID()));
		acv.setHostname(null == alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null)?"":alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null).getSourceName());
		acv.setDevicetype(null == alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null)?"":alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null).getExt0());
	    acv.setIpaddr(null == alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null)?"":alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null).getSourceIP());
	    acv.setFirstoccur(null == alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null)?0
	    		:alarmEventService.getAlarmEvent(alarmNotify.getAlarmID(), null).getCollectionTime().getTime()/1000);
	    
	    acv.setDomain(getAlarmDomain(alarmNotify.getAlarmID()));
	    acv.setMessage(alarmNotify.getNotifyContent());
	    acv.setUrl("resource/module/alarm-management/alarm-management-main.html");
	    
		return acv;
	}
	/**
	 * 根据告警ID查询域名称
	 * @param alarmId
	 * @return
	 */
	
	private  String getAlarmDomain(long alarmId){
		AlarmEvent ae =  alarmEventService.getAlarmEvent(alarmId, null);
		if(ae != null && ae.getSourceID() != null ){
			try {
				ResourceInstance resource =  resourceInstanceService.getResourceInstance(Long.parseLong(ae.getSourceID()));
				if(resource != null && resource.getDomainId() != 0){
					Domain	domain =  domainApi.get(resource.getDomainId());
					if(domain != null){
						return domain.getName();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 根据告警ID查询告警状态
	 * @param AlarmId
	 * @return
	 */
	private  int getAlarmEnumInt(long alarmId){
		AlarmEvent ae =  alarmEventService.getAlarmEvent(alarmId, null);
		int alarmType = 5;
		if(ae != null && ae.getLevel() != null){
			switch(ae.getLevel().toString()){
			case "CRITICAL":
				alarmType = 1;
				break;
			case "SERIOUS":
				alarmType = 2;
				break;	
			case "WARN":
				alarmType = 3;
				break;
			case "NORMAL":
				alarmType = 4;
				break;
			case "UNKOWN":
				alarmType = 5;
				break;
			}
		}
		
		return alarmType;
	}
	
	@RequestMapping("/getSnapShotFile")
	public JSONObject getSnapShotFile(long snapshotFileId, long recoverFileId) {
		return toSuccess(alarmApi.getSnapShotFile(snapshotFileId, recoverFileId));
	}

	/**
	 * 获取资源策略模型父节点
	 * @return
	 */
	@RequestMapping("/getPrentStrategyType")
	public JSONObject getPrentStrategyType(){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			//一级菜单
			CategoryDef[] baseCategoryDef = capacityService.getRootCategory().getChildCategorys();
			for(int i = 0; baseCategoryDef != null && i < baseCategoryDef.length; i++){
				CategoryDef c = baseCategoryDef[i];
				if("VM".equals(c.getId())){
					for(CategoryDef vmC : c.getChildCategorys()){
						Map<String,String> result  =  new HashMap<String,String>();
						result.put("id",vmC.getId() );
						result.put("name", vmC.getName());
						baseList.add(result);
					}
				}else{
					Map<String,String> result  =  new HashMap<String,String>();
					result.put("id",c.getId());
					result.put("name", c.getName());
					baseList.add(result);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		
		}
		List<Map<String, String>> categoryList = new ArrayList<Map<String, String>>();
		getAllCategory4Alarm(capacityService.getRootCategory(), categoryList);
		return toSuccess(categoryList);
	}
	
	/**
	 * 告警确认
	 * @param alarmInstanceIds 告警ids
	 * @return
	 */
	@RequestMapping("/SureAlarmByIds")
	public JSONObject SureAlarmByIds(String alarmInstanceIds,String type){
		if(alarmInstanceIds != null && !"".equals(alarmInstanceIds)){
			return toSuccess(alarmApi.confirmAlarmEvent(alarmInstanceIds, type));
			
}
		return toJsonObject(204, "确认失败");
		
		
	}
	
	private void getAllCategory4Alarm(CategoryDef categoryDef, List<Map<String, String>> categoryList){
		if("VM".equals(categoryDef.getId())){
			for(int i = 0; categoryDef.getChildCategorys() != null && i < categoryDef.getChildCategorys().length; i++){
				getAllCategory4Alarm(categoryDef.getChildCategorys()[i], categoryList);
			}
		}else{
			if(!licenseCapacityCategory.isAllowCategory(categoryDef.getId())){
				return;
			}
			Map<String, String> categoryMap = new HashMap<String, String>();
			categoryMap.put("id", categoryDef.getId());
			categoryMap.put("name", categoryDef.getName());
			if(categoryDef.getParentCategory() != null){
				categoryMap.put("pid", categoryDef.getParentCategory().getId());
				categoryList.add(categoryMap);
			}
			if(categoryDef.getChildCategorys() != null){
				for(int i = 0; i < categoryDef.getChildCategorys().length; i++){
					getAllCategory4Alarm(categoryDef.getChildCategorys()[i], categoryList);
				}
				categoryMap.put("state", "closed");
				categoryMap.put("type", "1");
			}else{
				categoryMap.put("type", "2");
			}
		}
	}
	
	public List<String> getCategorIds(String type){
		List<String> categories = null;
		if(null!=type){
			if(type.equals("All")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
				categories.add(CapacityConst.NETWORK_DEVICE);
//				categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);
				categories.add(CapacityConst.MIDDLEWARE);
				categories.add(CapacityConst.J2EEAPPSERVER);
				categories.add(CapacityConst.WEBSERVER);
				categories.add(CapacityConst.STANDARDSERVICE);
				categories.add(CapacityConst.MAILSERVER);
				categories.add(CapacityConst.DIRECTORY);
				categories.add(CapacityConst.LOTUSDOMINO);
				categories.add(CapacityConst.VM);
				categories.add(CapacityConst.SNMPOTHERS);
				categories.add("UniversalModel");
				categories.add(CapacityConst.LINK);
			}
			if(type.equals("Host")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
			}
			if(type.equals("NetworkDevice")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.NETWORK_DEVICE);
				categories.add(CapacityConst.SNMPOTHERS);
				categories.add(CapacityConst.LINK);
			}
			if(type.equals("App")){
				categories = new ArrayList<String>();
//				categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);//
				categories.add(CapacityConst.MIDDLEWARE);//
				categories.add(CapacityConst.J2EEAPPSERVER);//
				categories.add(CapacityConst.WEBSERVER);//
				categories.add(CapacityConst.STANDARDSERVICE);//
				categories.add(CapacityConst.MAILSERVER);//
				categories.add(CapacityConst.DIRECTORY);//
				categories.add(CapacityConst.LOTUSDOMINO);//
				categories.add(CapacityConst.VM);
				categories.add("UniversalModel");
			}
		}
		return categories;
	}
	
	/**
	 * 验证Alert告警缓存对象是否更新
	 * @param host
	 * @param userId
	 * @return
	 */
	public boolean getRefreshStatus(String host,long userId){
		String cacheKey=NotifyTypeEnum.alert.toString()+userId;
		
		IMemcache<List> alertUsersCache=MemCacheFactory.getRemoteMemCache(List.class);
		
		//Alert 用户IP 集合
		List<HashMap<String,String>> userIPs=alertUsersCache.get(cacheKey);
		
		HashMap<String,String> currentUserReqMap=new HashMap<String,String>();
		currentUserReqMap.put("host", host);
		currentUserReqMap.put("refresh", "false");
		
		
		//当前用户不存在
		if(userIPs==null || userIPs.isEmpty()){
			userIPs=new ArrayList<HashMap<String,String>>();
			userIPs.add(currentUserReqMap);
			alertUsersCache.set(cacheKey, userIPs);
			return true;
		}
		
		
		for(HashMap<String,String> userReqMap:userIPs){
			if(userReqMap.containsValue(host) && userReqMap.containsValue("true")){
				userReqMap.put("refresh", "false");
				alertUsersCache.set(cacheKey, userIPs);
				return true;
			}
			if(userReqMap.equals(currentUserReqMap)){
				return false;
			}
		}
		
		//用户存在，IP不存在
		userIPs.add(currentUserReqMap);
		alertUsersCache.set(cacheKey, userIPs);
		
		return true;
	}
	@RequestMapping("/getHistoryAlarm")
	public JSONObject getHistoryAlarm(AlarmPageVo page,
			HttpSession session) {
		AlarmPageVo pageVo = new AlarmPageVo();
		List<AlarmVo> listnew = new ArrayList<AlarmVo>();
		AlarmVo historyVo=	page.getCondition();
		String recoverKey=historyVo.getRecoverKey();
		String sys_type=historyVo.getSys_type();
		
		//System.out.println("recoverKey:"+recoverKey);
		//System.out.println("sys_type:"+sys_type);
		List<AlarmEventDetail> list= new ArrayList<AlarmEventDetail>();
		//,SysModuleEnum.valueOf(sys_type)
		SysModuleEnum mnum=	SysModuleEnum.valueOf(sys_type);
		 list = alarmEventService.queryAlarmHistory(recoverKey,mnum);
	
		List<AlarmVo> alarmVos = toHisVo(list);
		int startRow = new Long(page.getStartRow()).intValue();
		int endRow = new Long(page.getStartRow() + page.getRowCount())
				.intValue();
		int pageSize = new Long(page.getRowCount()).intValue();
		if (list == null) {
			pageVo.setAlarms(null);
			pageVo.setTotalRecord(0);
		} else {

			if ((startRow + pageSize) > alarmVos.size()) {
				if (startRow <= alarmVos.size()) {
					endRow = alarmVos.size();
				}
			} else {
				endRow = startRow + pageSize;

			}
			for (int i = startRow; i < endRow; i++) {
				listnew.add(alarmVos.get(i));
			}

			pageVo.setAlarms(listnew);
			pageVo.setTotalRecord(list.size());
		}
		return toSuccess(pageVo);

	}
	public List<AlarmVo> toHisVo(List<AlarmEventDetail> details) {

		List<AlarmVo> alarmVos = new ArrayList<AlarmVo>();
		for (AlarmEventDetail detail : details) {
			AlarmVo alarmVo = new AlarmVo();
			alarmVo.setAlarmId(detail.getEventId());
			alarmVo.setCollectionTime(detail.getCollectionTime());
			alarmVo.setAlarmContent(detail.getContent());
			alarmVo.setInstanceStatus(getInstStateEnumString(detail.getLevel()));
			alarmVo.setSnapShotJSON(detail.getSnapshotResult());
			
			// alarmVo.setSnapShotJSON(detail.getExt7());
			alarmVos.add(alarmVo);
		}
		return alarmVos;
	}
}
