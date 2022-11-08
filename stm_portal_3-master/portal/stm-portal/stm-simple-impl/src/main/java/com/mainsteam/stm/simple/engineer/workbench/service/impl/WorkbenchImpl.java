package com.mainsteam.stm.simple.engineer.workbench.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeAttachmentApi;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.service.api.IKnowledgeServiceApi;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.network.ssh.SshTerminal;
import com.mainsteam.stm.network.util.NetWorkBean;
import com.mainsteam.stm.network.util.NetWorkUtil;
import com.mainsteam.stm.network.util.ProtocolEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.simple.engineer.workbench.api.IWorkbenchApi;
import com.mainsteam.stm.simple.engineer.workbench.bo.FaultProcessFlowBo;
import com.mainsteam.stm.simple.engineer.workbench.bo.RemoteAcessBo;
import com.mainsteam.stm.simple.engineer.workbench.bo.StoreBo;
import com.mainsteam.stm.simple.engineer.workbench.dao.IFaultProcessFlowDao;
import com.mainsteam.stm.simple.engineer.workbench.job.WorkbenchJob;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: WorkbenchImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
@Service("com.mainsteam.stm.simple.engineer.workbench.api.IWorkbenchApi")
public class WorkbenchImpl implements IWorkbenchApi{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WorkbenchImpl.class);
	
	@Value("${stm.TargetAcquisitionCycle.Minute}")
	private int targetAcquisitionCycle;
	
	@Resource
	private ScheduleManager scheduleManager;
	
	@Resource
	private IFileClientApi fileClient;
	
	@Autowired
	private AlarmEventService alarmEventService;

	@Autowired
	@Qualifier("knowledgeServiceApi")
	private IKnowledgeServiceApi knowledgeServiceApi;
	
	@Autowired
	@Qualifier("knowledgeAttachmentApi")
	private IKnowledgeAttachmentApi knowledgeAttachmentApi;
	
	@Autowired
	private IFaultProcessFlowDao faultProcessFlowDao;
	
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Autowired
	@Qualifier("stm_system_resourceApi")
	private IResourceApi resourceApi;

	private List<MetricStateEnum> ms=new ArrayList<MetricStateEnum>();
	{
		ms.add(MetricStateEnum.CRITICAL);
		ms.add(MetricStateEnum.SERIOUS);
		ms.add(MetricStateEnum.WARN);
	}
	
	@Override
	public Page<AlarmEvent, AlarmEventQuery> setResolveFault(Page<AlarmEvent, AlarmEventQuery> page,ILoginUser user) {
		AlarmEventQuery req=page.getCondition();
		if(req==null)req = new AlarmEventQuery();
		if(!user.isSystemUser() && user.getUserType()!=UserConstants.USER_TYPE_SUPER_ADMIN){
			List<ResourceInstanceBo> instances = resourceApi.getResources(user);
			List<String> instanceIds = null;
			if(instances!=null && instances.size()>0){
				instanceIds = new ArrayList<String>();
				for (ResourceInstanceBo instance : instances) {
					if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						instanceIds.add(String.valueOf(instance.getId()));
					}
				}
			}
			if(instanceIds==null||instanceIds.isEmpty()){	//如果此用户没有资源权限，那将无法查看告警信息
				page.setDatas(Collections.<AlarmEvent> emptyList());
				return page;
			}
			req.setSourceIDes(instanceIds);
		}else{
			
			//过滤掉已删除或者未监控的资源实例
			try {
				List<String> instanceIds = new ArrayList<String>();
				List<ResourceInstance> instances = resourceInstanceService.getAllParentInstance();
				for(ResourceInstance instance : instances){
					if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						instanceIds.add(String.valueOf(instance.getId()));
					}
				}
				req.setSourceIDes(instanceIds);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
		}
		req.setStates(ms);
		SysModuleEnum[] enums={SysModuleEnum.MONITOR};
		req.setSysIDes(enums);
		req.setRecovered(false);
		if(req.getSourceIDes().size()==0){
		req.setSourceIDes(null);
	}
		if(req.getSourceIDes()!=null && req.getSourceIDes().size()!=0){
			page = alarmEventService.findAlarmEvent(req,(int)page.getStartRow(),(int)page.getRowCount());
		}
		return page;
	}

	@Override
	public Page<AlarmEvent, AlarmEventQuery> setResolvedFault(Page<AlarmEvent, AlarmEventQuery> page,ILoginUser user) {
		AlarmEventQuery req=page.getCondition();
		if(req==null)req=new AlarmEventQuery();
		if(!user.isSystemUser() && user.getUserType()!=UserConstants.USER_TYPE_SUPER_ADMIN){
			List<ResourceInstanceBo> instances = resourceApi.getResources(user);
			if(instances!=null && instances.size()>0){
				List<String> instanceIds = new ArrayList<String>();
				for (ResourceInstanceBo instance : instances) {
					if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						instanceIds.add(String.valueOf(instance.getId()));
					}
				}
				req.setSourceIDes(instanceIds);
			}
		}else{
			
			//过滤掉已删除或者未监控的资源实例
			try {
				List<String> instanceIds = new ArrayList<String>();
				List<ResourceInstance> instances = resourceInstanceService.getAllParentInstance();
				for(ResourceInstance instance : instances){
					if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						instanceIds.add(String.valueOf(instance.getId()));
					}
				}
				req.setSourceIDes(instanceIds);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
		}
		//req.setStates(ms);
		SysModuleEnum[] enums={SysModuleEnum.MONITOR};
		req.setSysIDes(enums);
		req.setRecovered(true);
	//	req.setExt4("1");
		if(req.getSourceIDes().size()==0){
			req.setSourceIDes(null);
		}
		if(req.getSourceIDes()!=null && req.getSourceIDes().size()!=0){
			
			page= alarmEventService.findAlarmEvent(req,(int)page.getStartRow(),(int)page.getRowCount());
		}
		return page;
	}

	@Override
	public List<KnowledgeBo> analyzeFault(AlarmEvent re) {
		FaultBo fb=new FaultBo();
		if(re!=null){
			//通过模型ID+资源类型ID+指标ID组合成故障类型
			fb.setColudyType(re.getExt2()+"-"+re.getExt1()+"-"+re.getExt3());
			fb.setLocalType(re.getExt1()+"-"+re.getExt3());
			List<KnowledgeBo> result = knowledgeServiceApi.analyzeFault(fb);
			return result;
		}
		return null;
		
	}

	@Override
	public List<KnowledgeResolveBo> resolveFault(long knowlgeId) {
		return knowledgeServiceApi.resolveFault(knowlgeId);
	}

	@Override
	public KnowledgeBo getKnowledge(long knowledgeId) {
		return knowledgeServiceApi.getKnowledge(knowledgeId);
	}

	@Override
	public int saveFaultProcessFlow(FaultProcessFlowBo faultProcessFlowBo) {
		if(faultProcessFlowBo.getFaultId()!=null)
		{
			faultProcessFlowDao.del(faultProcessFlowBo.getFaultId());
		
		}return faultProcessFlowDao.insert(faultProcessFlowBo);
	}

	@Override
	public FaultProcessFlowBo getFaultProcessFlow(Long faultId) {
		return faultProcessFlowDao.get(faultId);
	}

	@Override
	public int updateRepairFaultByResole(RemoteAcessBo repair) {
		try {	
			NetWorkBean bean = new NetWorkBean();
			bean.setIp(repair.getIp());
			bean.setUserName(repair.getAccount());
			bean.setPassword(URLDecoder.decode(repair.getPassword(),"utf-8"));
			bean.setPort(StringUtils.isEmpty(repair.getPort())?SshTerminal.DEFAULT_SSH_PORT:Integer.valueOf(repair.getPort()));
			if(repair.getRepairType()==RemoteAcessBo.REPAIR_TYPE_TELENT){//通过telent方式连接
				bean.setProtocol(ProtocolEnum.TELNET);
			}else if(repair.getRepairType()==RemoteAcessBo.REPAIR_TYPE_SSH){//通过SSH方式连接
				bean.setProtocol(ProtocolEnum.SSH);
			}else if(repair.getRepairType()==RemoteAcessBo.REPAIR_TYPE_WMI){//通过WMI方式连接
				bean.setProtocol(ProtocolEnum.TELNET);
			}
			List<String> scripts = new ArrayList<String>();
			List<KnowledgeAttachmentBo> attachments = this.getResolveAttachments(repair.getResolveId());
			for (KnowledgeAttachmentBo attachment : attachments) {
				if(KnowledgeResolveBo.SCRIPT_FILE_EXT.contains(attachment.getFileName().substring(attachment.getFileName().lastIndexOf(".")+1))){
					scripts.add(readScriptFileToString(attachment.getFileId()));
				}
			}
			bean.setScripts(scripts);
			NetWorkUtil.execute(bean);
			AlarmEvent event = alarmEventService.getAlarmEvent(repair.getEventId(),false);
			event.setExt4("1");
			event.setExt5(DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss"));
			alarmEventService.updateAlarmEventExt(event);
			return 1;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<StoreBo> getStores() {
		
		return null;
	}

	@Override
	public KnowledgeResolveBo getKnowledgeResolve(long resolveId) {
		return knowledgeServiceApi.getKnowledgeResolve(resolveId);
	}

	@Override
	public boolean saveResolveEvaluation(ResolveEvaluationBo evaluation) {
		
		return knowledgeServiceApi.saveResolveEvaluation(evaluation);
	}
	
	@Override
	public AlarmEvent getAlarmEventById(long eventId){
		return alarmEventService.getAlarmEvent(eventId,null);
	}

	@Override
	public ResourceInstance getResourceInstance(long resourceId) {
		try {
			return resourceInstanceService.getResourceInstance(resourceId);
		} catch (InstancelibException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<KnowledgeAttachmentBo> getResolveAttachments(long resolveId) {
		return knowledgeAttachmentApi.selectResolveAttachment(resolveId);
	}
	/**
	 * @throws Exception 
	* @Title: readScriptFileToString
	* @Description: 读取脚本文件转换成String
	* @return  String
	* @throws
	*/
	private String readScriptFileToString(long fileId) throws Exception{
		File file = fileClient.getFileByID(fileId);
		if (file != null) {
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String str;
			while ((str = reader.readLine()) != null) {
				buffer.append(str).append("\r\n");
			}
			reader.close();
			return buffer.toString();
		} else {
			return "";
		}
	}

	@Override
	public void updateCheckedAlarmEvemtIsRecovered() {
		Calendar calendar = Calendar.getInstance();
		Date currentTime = calendar.getTime();//job执行时间
		if (logger.isDebugEnabled()) {
			logger.debug("updateCheckedAlarmEvemtIsRecovered() - start"+DateUtil.format(currentTime, "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		AlarmEventQuery query = new AlarmEventQuery();
		query.setRecovered(false);
		query.setExt4("1");
		List<AlarmEvent> alarmEvents = alarmEventService.findAlarmEvent(query, 0, 1000000000).getDatas();
		int cycle = Integer.valueOf(new BigDecimal(targetAcquisitionCycle*2.5).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		for (AlarmEvent alarm : alarmEvents) {
			calendar.setTime(DateUtil.parseDate(alarm.getExt5(), "yyyy-MM-dd HH:mm:ss"));
			calendar.add(Calendar.MINUTE, cycle);
			if(calendar.getTime().getTime()<=currentTime.getTime()){//修复时间+采集周期<=job执行时间的
				logger.info("告警执行脚本自动修复失败：EventID"+alarm.getEventID()+"/sourceName:"+alarm.getSourceName()+"/content:"+alarm.getContent());
				alarm.setExt4("0");
				alarm.setExt5(DateUtil.format(currentTime, "yyyy-MM-dd HH:mm:ss"));
				alarmEventService.updateAlarmEventExt(alarm);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCheckedAlarmEvemtIsRecovered() - end"); //$NON-NLS-1$
		}
	}
	
	@PostConstruct
	public void initJob(){
		if (logger.isDebugEnabled()) {
			logger.debug("initJob() - start"); //$NON-NLS-1$
		}
		try {
			WorkbenchJob workbenchJob = new WorkbenchJob();
			IJob iWorkbenchJob = new IJob("Wowkbenck-CheckedAlarmEvent-job", workbenchJob,  "0 0/11 * * * ?");
			scheduleManager.scheduleJob(iWorkbenchJob);
		} catch (ClassNotFoundException e) {
			logger.error("initJob()", e); //$NON-NLS-1$
			e.printStackTrace();
		} catch (SchedulerException e) {
			logger.error("initJob()", e); //$NON-NLS-1$
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("initJob() - end"); //$NON-NLS-1$
		}
	}
}


