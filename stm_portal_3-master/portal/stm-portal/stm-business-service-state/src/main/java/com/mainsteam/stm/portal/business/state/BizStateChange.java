package com.mainsteam.stm.portal.business.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2.OrderField;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizHealthHisBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.dao.IBizAlarmInfoDao;
import com.mainsteam.stm.portal.business.dao.IBizCanvasDao;
import com.mainsteam.stm.portal.business.dao.IBizHealthHisDao;
import com.mainsteam.stm.portal.business.dao.IBizMainDao;
import com.mainsteam.stm.portal.business.state.api.BizNodeTypeDefine;
import com.mainsteam.stm.portal.business.state.api.BizStatusDefine;
import com.mainsteam.stm.portal.business.uitl.BizAlarmParameterDefine;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.engine.StateHandle;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateData;

/**
 * <li>文件名称: BizSerState.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 计算业务状态</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年8月27日
 * @author   pengl
 */
public class BizStateChange implements StateHandle{
	/**
	 * 日志
	 */
	private Logger logger = Logger.getLogger(BizStateChange.class);
	
	@Autowired
	private LockService lockService;
	
	@Resource
	private IBizHealthHisDao bizHealthHisDao;
	
	@Resource
	private IBizMainDao bizMainDao;
	
	@Resource
	private IBizCanvasDao bizCanvasDao;
	
	private IBizAlarmInfoDao bizAlarmInfoDao;

	@Resource
	private AlarmService alarmService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private CustomMetricService customMetricService;
	
	@Resource
	private ResourceInstanceService instanceService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private AlarmEventService alarmEventService;

	public IBizAlarmInfoDao getBizAlarmInfoDao() {
		return bizAlarmInfoDao;
	}

	public void setBizAlarmInfoDao(IBizAlarmInfoDao bizAlarmInfoDao) {
		this.bizAlarmInfoDao = bizAlarmInfoDao;
	}

	@Override
	public void onInstanceStateChange(final InstanceStateChangeData instanceState) {
		lockService.sync("bizserstate",new LockCallback<Object>() {
			@Override
			public Object doAction() {
				
				logger.error("*********begin***********");
				logger.error("Businesss onInstanceStateChange , instanceId : " + instanceState.getNewState().getInstanceID() + ",newState : " +instanceState.getNewState().getState() );
				
				List<Long> chageNodeIds = new ArrayList<Long>();
				try {
					ResourceInstance instance = instanceService.getResourceInstance(instanceState.getNewState().getInstanceID());
					
					long queryInstanceId = instanceState.getNewState().getInstanceID();
					
					Set<Long> checkNodeId = new HashSet<Long>();
					
					if(instance.getParentId() > 0){
						//子资源
						logger.error("Change instance name : " + instance.getName() + ",query parent instance");

						queryInstanceId = instance.getParentId();
					}else{
						logger.error("Query main instance name : " + instance.getShowName());
					}
					
					//根据资源实例ID从节点表找出节点受该资源状态影响的节点ID
					List<BizInstanceNodeBo> allInstanceNode = bizCanvasDao.getAllInstanceNode();
					if(allInstanceNode == null || allInstanceNode.size() >= 0){
						for(BizInstanceNodeBo instanceNode : allInstanceNode){
							if(instanceNode.getInstanceId() == queryInstanceId){
								
								checkNodeId.add(instanceNode.getId());
								
							}
						}
					}
					
					if(checkNodeId != null && checkNodeId.size() > 0){
						
						changeBizNodeStatus(checkNodeId, chageNodeIds);
						
						logger.error("Begin changeBizStateFromNodes");
						
						if(chageNodeIds != null && chageNodeIds.size() > 0){
							changeBizStateFromNodes(chageNodeIds);
						}
						
					}
					
					
					logger.error("*********end***********");
				} catch (InstancelibException e1) {
					logger.error(e1.getMessage(),e1);
				}
				
				return null;
			}
		},20);
		
	}
	
	@Override
	public void onMetricStateChange(final MetricStateChangeData metricState) {
		lockService.sync("bizserstate", new LockCallback<Object>() {
			@Override
			public Object doAction() {
				
				logger.error("*********begin***********");
				logger.error("Businesss MetricStateChangeData , instanceId : " + metricState.getNewState().getInstanceID() + ",newState : " +metricState.getNewState().getState() );
				
				List<Long> chageNodeIds = new ArrayList<Long>();
				
				ResourceInstance instance = null;
				try {
					instance = instanceService.getResourceInstance(metricState.getNewState().getInstanceID());
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				
				long queryInstanceId = metricState.getNewState().getInstanceID();
				
				Set<Long> nodeIds = new HashSet<Long>();
				//获取此指标影响的节点
				if(instance.getParentId() > 0){
					//子资源
					logger.error("Change child instance name : " + instance.getName() + ",query parent instance");

					queryInstanceId = instance.getParentId();
					
				}else{
					logger.error("Change main instance name : " + instance.getName());
				}
				
				//主资源
				//根据资源实例ID从节点表找出节点受该资源状态影响的节点ID
				List<BizInstanceNodeBo> allInstanceNode = bizCanvasDao.getAllInstanceNode();
				if(allInstanceNode == null || allInstanceNode.size() >= 0){
					for(BizInstanceNodeBo instanceNode : allInstanceNode){
						if(instanceNode.getInstanceId() == queryInstanceId){
							
							nodeIds.add(instanceNode.getId());
							
						}
					}
				}

				if(nodeIds != null && nodeIds.size() > 0){
					
					changeBizNodeStatus(nodeIds, chageNodeIds);
					
					if(chageNodeIds != null && chageNodeIds.size() > 0){
						
						logger.error("Begin changeBizStateFromNodes");
						
						changeBizStateFromNodes(chageNodeIds);
					}
				}
				
				logger.error("*********end***********");
				return null;
			}
		}, 20);
	}
	
	private void changeBizNodeStatus(Set<Long> nodeIds,List<Long> chageNodeIds){
		
		//判断此节点是否只绑定了该子资源
		for(long nodeId : nodeIds){
			logger.error("Check node id : " + nodeId);
			BizInstanceNodeBo canvasNode = bizCanvasDao.getInstanceNodeAndRelation(nodeId);
			List<BizNodeMetricRelBo> relationList = canvasNode.getBind();
			
			//该节点绑定了多个
			int curHighestState = BizStatusDefine.NONE_STATUS;
			
			if(canvasNode.getType() == 3){
				//判断全部指标状态
				logger.error("Check node nodeType : " + 3);
				for(BizNodeMetricRelBo rel : relationList){
					long queryInstanceId = rel.getChildInstanceId() > 0 ? rel.getChildInstanceId() : canvasNode.getInstanceId();
					try {
						ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(queryInstanceId, rel.getMetricId());
						CustomMetric customMetric = null;
						if(profileMetric == null && rel.getChildInstanceId() <= 0){
							//判断是否为自定义指标
							try {
								customMetric = customMetricService.getCustomMetric(rel.getMetricId());
							} catch (CustomMetricException e) {
								logger.error(e.getMessage(),e);
							}
						}
						
						if(rel.getChildInstanceId() > 0){
							//子资源的指标
							if(profileMetric == null || !profileMetric.isMonitor()){
								continue;
							}
						}else{
							//主资源的指标
							if(profileMetric == null){
								if(customMetric == null || customMetric.getCustomMetricInfo() == null || !customMetric.getCustomMetricInfo().isMonitor()){
									continue;
								}
							}else if(!profileMetric.isMonitor()){
								continue;
							}
						}
						
					} catch (ProfilelibException e) {
						logger.error(e.getMessage(),e);
					}
					MetricStateData metricStateData = metricStateService.getMetricState(queryInstanceId, rel.getMetricId());
					int curState = BizStatusDefine.NORMAL_STATUS;
					if(metricStateData != null){
						curState = getInstanceState(metricStateData.getState());
					}
					if(curState > curHighestState){
						curHighestState = curState;
						if(curHighestState == BizStatusDefine.DEATH_STATUS){
							break;
						}
					}
					
				}
			}else if(canvasNode.getType() == 2){
				logger.error("Check node nodeType : " + 2);
				for(BizNodeMetricRelBo rel : relationList){
					try {
						ResourceInstance childInstance = instanceService.getResourceInstance(rel.getChildInstanceId());
						if(childInstance == null || childInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
							continue;
						}
					} catch (InstancelibException e) {
						logger.error(e.getMessage(),e);
					}
					InstanceStateData stateData = instanceStateService.getStateAdapter(rel.getChildInstanceId());
					int curState = -1;
					if(stateData == null){
						curState = BizStatusDefine.NORMAL_STATUS;
					}else{
						curState = getInstanceState(stateData.getState());
					}
					if(curState > curHighestState){
						curHighestState = curState;
						if(curHighestState == BizStatusDefine.DEATH_STATUS){
							break;
						}
					}
				}
			}else if(canvasNode.getType() == 1){
				logger.error("Check node nodeType : " + 1);
				InstanceStateData stateData = instanceStateService.getStateAdapter(canvasNode.getInstanceId());
				if(stateData != null){
					curHighestState = getInstanceState(stateData.getState());
				}else{
					try {
						ResourceInstance instance = instanceService.getResourceInstance(canvasNode.getInstanceId());
						if(instance != null && instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							curHighestState = BizStatusDefine.NORMAL_STATUS;
						}
					} catch (InstancelibException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			
			logger.error("New status : " + curHighestState + ",old status : " + canvasNode.getNodeStatus());
			
			if(curHighestState != canvasNode.getNodeStatus()){
				//需要改变节点状态
				BizCanvasNodeBo bizNode = new BizCanvasNodeBo();
				bizNode.setId(canvasNode.getId());
				bizNode.setNodeStatus(curHighestState);
				bizNode.setStatusTime(new Date());
				bizCanvasDao.updateCanvasNodeStatusInfoByNodeId(bizNode);
				
				chageNodeIds.add(nodeId);
			}
		}
		
	}
	
	private void changeBizStateFromNodes(List<Long> chageNodeIds){
		
		List<Long> checkBizIdList = new ArrayList<Long>();
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//遍历所有业务获取状态定义规则，找出受该节点影响的所有业务
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		for(BizMainBo status : statusDefineList){
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//默认状态定义规则
				if(bizCanvasDao.checkNodeByBizId(status.getId(), chageNodeIds) > 0){
					checkBizIdList.add(status.getId());
				}
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				//自定义状态规则
				for(long nodeId : chageNodeIds){
					if(status.getStatusDefine().contains("{" + nodeId  + "}")){
						checkBizIdList.add(status.getId());
					    break;
						
					}
				}
				//使用的自定义状态定义
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
					}
				}
			}
		}
		
		//将所有业务关系分为多个图，且获取每个图的修改顺序
		List<List<Long>> mapsList = getBizChangeOrder(bizNodeRelationList);
		List<Long> orderList = new ArrayList<Long>();
		
		//判断该资源所影响的业务影响哪些业务关系图，不影响的不进行健康度运算
		for(List<Long> map : mapsList){
			List<Long> retainList = new ArrayList<Long>(map);
			retainList.retainAll(checkBizIdList);
			if(retainList != null && retainList.size() > 0){
				//有交集，该业务关系图需要计算
				orderList.addAll(map);
			}
		}
		
		Map<Long, String> healthChangeMap = new HashMap<Long, String>();
		
		for(long checkId : checkBizIdList){
			if(!orderList.contains(checkId)){
				orderList.add(checkId);
			}
		}
		
		//根据默认规则或者自定义规则修改业务状态
		for(long bizId : orderList){
			
			//获取该业务的告警阈值
			BizAlarmInfoBo alarmInfo = new BizAlarmInfoBo();
			alarmInfo.setBizId(bizId);
			alarmInfo = bizAlarmInfoDao.getAlarmInfo(alarmInfo);
			
			for(BizMainBo status : statusDefineList){
				if(bizId == status.getId()){
					
					BizHealthHisBo oldHealthHis = bizHealthHisDao.getBizHealthHis(bizId);
					if(oldHealthHis == null){
						oldHealthHis = new BizHealthHisBo();
						oldHealthHis.setBizHealth(100);
						oldHealthHis.setBizStatus(0);
					}
					
					int newAlarmLevel = 0;
					int newHealthScore = 100;
					String alarmNodeName = "";
					String alarmNodeType = "";
					int alarmNodeTypeInt = 0;
					long alarmNodeId = 0;
					String alarmNodeContent = "";
					long alarmNodeInstanceId = 0;
					
					if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
						//默认状态定义规则
						List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
						if(nodes != null && nodes.size() > 0){
							for(BizCanvasNodeBo node : nodes){
								if((node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE || node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE) 
										&& node.getInstanceId() != status.getId()){
									if(node.getNodeStatus() > newAlarmLevel){
										//取最高的告警级别
										newAlarmLevel = node.getNodeStatus();
										alarmNodeName = node.getShowName();
										alarmNodeTypeInt = node.getNodeType();
										alarmNodeInstanceId = node.getInstanceId();
										alarmNodeId = node.getId();
									}
								}
							}
							
							newHealthScore = getScoreByAlarmLevel(newAlarmLevel, alarmInfo);
							
						}
					}else{
						//自定义状态规则
						String statusHealth = status.getStatusDefine();
					    String patternParameter = "\\$\\{.*?\\}";
					    Pattern pattern = Pattern.compile(patternParameter);
			
					    Matcher matcher = pattern.matcher(statusHealth);
					    int minimum = 100;
					    while (matcher.find()) {
					    	long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					    	if(!statusHealth.contains("" + bizNodeId)){
					    		continue;
					    	}
					    	BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					    	if(nodeBo.getNodeStatus() == BizStatusDefine.NONE_STATUS || nodeBo.getNodeType() == BizNodeTypeDefine.DELETE_INSTANCE_TYPE){
					    		//该节点未监控
					    		statusHealth = statusHealth.replaceAll("\\$\\{" + bizNodeId + "\\}", "-1");
					    	}else{
					    		int curScore = getScoreByAlarmLevel(nodeBo.getNodeStatus(),alarmInfo);
					    		if(curScore < minimum){
					    			alarmNodeTypeInt = nodeBo.getNodeType();
					    			alarmNodeInstanceId = nodeBo.getInstanceId();
					    			alarmNodeName = nodeBo.getShowName();
					    			alarmNodeId = nodeBo.getId();
					    		}
					    		statusHealth = statusHealth.replaceAll("\\$\\{" + bizNodeId + "\\}", curScore + "");
					    	}
					    }
					    
					    //计算健康度
					    newHealthScore = Integer.parseInt(getScore(statusHealth,Integer.parseInt(alarmInfo.getDeathThreshold())));
					    newAlarmLevel = getAlarmLevelByScore(newHealthScore, alarmInfo);
					}
					
					//存入健康度历史表并修改节点业务状态
					BizCanvasNodeBo bizNode = new BizCanvasNodeBo();
					bizNode.setInstanceId(bizId);
					bizNode.setNodeType(BizNodeTypeDefine.BUSINESS_TYPE);
					bizNode.setNodeStatus(newAlarmLevel);
					bizNode.setStatusTime(new Date());
					bizCanvasDao.updateCanvasNodeStatusInfo(bizNode);
					
					BizHealthHisBo healthHis = new BizHealthHisBo();
					healthHis.setBizChangeTime(new Date());
					healthHis.setBizHealth(newHealthScore);
					healthHis.setBizStatus(newAlarmLevel);
					healthHis.setBizId(bizId);
					bizHealthHisDao.insertHealthHis(healthHis);
					
					healthChangeMap.put(bizId, oldHealthHis.getBizHealth() + "," + newHealthScore);
					
					logger.error("check bizId : " + bizId + ",old status : " + oldHealthHis.getBizStatus() + ", new status : " + newAlarmLevel);
					
					if(oldHealthHis.getBizStatus() != newAlarmLevel){
						//状态改变，产生告警
						if(newAlarmLevel > 0){
							if(alarmNodeTypeInt == BizNodeTypeDefine.BUSINESS_TYPE){
								alarmNodeType =  "业务系统";
								alarmNodeContent = "业务系统【" + alarmNodeName + "】的健康度由【" + 
										healthChangeMap.get(alarmNodeInstanceId).split(",")[0] + "】变为【" + healthChangeMap.get(alarmNodeInstanceId).split(",")[1] + "】";
							}else{
								ResourceInstance instance = null;
								try {
									instance = instanceService.getResourceInstance(alarmNodeInstanceId);
									alarmNodeType = capacityService.getResourceDefById(instance.getResourceId()).getName();
									alarmNodeContent = queryNodeLastAlarm(alarmNodeId,alarmNodeInstanceId);
									
								} catch (InstancelibException e) {
									logger.error(e.getMessage(),e);
								}
							}
						}
						
						BizAlarmParameterDefine alarmParameterDefine = new BizAlarmParameterDefine();
						
						BizMainBo mainInfo = bizMainDao.getBasicInfo(bizId);
						
						if(newAlarmLevel > 0){
							alarmParameterDefine.setAlarmNodeContent(alarmNodeContent);
							alarmParameterDefine.setAlarmNodeName(alarmNodeName);
							alarmParameterDefine.setAlarmNodeType(alarmNodeType);
							alarmParameterDefine.setBizAlarmLevel(getAlarmInfoByLevel(newAlarmLevel));
						}else{
							alarmParameterDefine.setAlarmNodeContent("");
							alarmParameterDefine.setAlarmNodeName("");
							alarmParameterDefine.setAlarmNodeType("");
							alarmParameterDefine.setBizAlarmLevel("");
						}
						alarmParameterDefine.setBizHealth(newHealthScore + "");
						alarmParameterDefine.setBizManager(mainInfo.getManagerName());
						alarmParameterDefine.setBizName(mainInfo.getName());
						
						String alarmTitle = generateAlarmTitle(newAlarmLevel, mainInfo.getName());
						
						String alarmContent = generateBizAlarmContent(alarmInfo,newAlarmLevel,alarmParameterDefine);
						addAlarm(alarmContent,alarmTitle, mainInfo, newAlarmLevel);
					}
					
					
					break;
				}
			}
		}
	}
	
	public String getScore(String statusHealth,int deathScore){
		
		statusHealth = statusHealth.replaceAll(" ", "");
	    
	    statusHealth = statusHealth.toUpperCase();
		
		String content = "";
		
		String curExpression = "";
		
		String newS = new String(statusHealth);
		
		boolean isAdapt = false;
		
		for(int i = 0 ; i < statusHealth.length() ; i++){
			char a = statusHealth.charAt(i);
			if(a == '('){
				content = "";
				isAdapt = true;
				String temp = statusHealth.substring(0, i);
				int substringIndex_1 = temp.lastIndexOf(",") < 0 ? 0 : temp.lastIndexOf(",") + 1;
				int substringIndex_2 = temp.lastIndexOf("(") + 1;
				curExpression = temp.substring(substringIndex_1 > substringIndex_2 ? substringIndex_1 : substringIndex_2, temp.length());
			}else if(a == ')'){
				if(isAdapt){
					if(!content.equals("")){
						if(curExpression.equals("OR") || curExpression.equals("AVG")){
							int weight = 0;
							int sum = 0;
							for(String node : content.split(",")){
								if(node.contains("-1")){
									continue;
								}
								if(node.contains("*")){
									weight += Integer.parseInt(node.split("\\*")[0]);
									sum += Integer.parseInt(node.split("\\*")[0]) * Integer.parseInt(node.split("\\*")[1]);
								}else{
									weight++;
									sum += Integer.parseInt(node);
								}
							}
							int value = 100;
							if(weight > 0){
								value = sum / weight;
							}
							newS = newS.replace(curExpression + "(" + content + ")", value + "");
							System.out.println("newS : " + newS);
						}else if(curExpression.equals("AND")){
							boolean isUseWeight = true;
							List<Integer> list = new ArrayList<Integer>();
							for(String node : content.split(",")){
								if(node.contains("-1")){
									continue;
								}
								if(node.contains("*")){
									if(Integer.parseInt(node.split("\\*")[1]) <= deathScore){
										//有不可用节点使用AND
										isUseWeight = false;
									}
									list.add(Integer.parseInt(node.split("\\*")[1]));
								}else{
									if(Integer.parseInt(node) <= deathScore){
										//有不可用节点使用AND
										isUseWeight = false;
									}
									list.add(Integer.parseInt(node));
								}
								
							}
							if(isUseWeight){
								//全部节点可用使用加权平均
								int weight = 0;
								int sum = 0;
								for(String node : content.split(",")){
									if(node.contains("-1")){
										continue;
									}
									if(node.contains("*")){
										weight += Integer.parseInt(node.split("\\*")[0]);
										sum += Integer.parseInt(node.split("\\*")[0]) * Integer.parseInt(node.split("\\*")[1]);
									}else{
										weight++;
										sum += Integer.parseInt(node);
									}
								}
								int value = 100;
								if(weight > 0){
									value = sum / weight;
								}
								newS = newS.replace(curExpression + "(" + content + ")",value + "");
								System.out.println("newS : " + newS);
							}else{
								int value = 100;
								if(list != null && list.size() > 0){
									Collections.sort(list);
									value = list.get(0);
								}
								newS = newS.replace(curExpression + "(" + content + ")",value  + "");
								System.out.println("newS : " + newS);
							}
						}
					}
					content = "";
					isAdapt = false;
				}
			}else{
				content += a;
			}
			
		}
		
		if(newS.contains("(")){
			return getScore(newS,deathScore);
		}else{
			return newS;
		}
		
		
	}
	
	//将告警级别转换为对应分数
	public int getScoreByAlarmLevel(int alarmLevel,BizAlarmInfoBo alarmInfo){
		
		if(alarmLevel == BizStatusDefine.NORMAL_STATUS){
			return 100;
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			return Integer.parseInt(alarmInfo.getWarnThreshold());
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			return Integer.parseInt(alarmInfo.getSeriousThreshold());
		}else if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			return Integer.parseInt(alarmInfo.getDeathThreshold());
		}else{
			return 100;
		}
		
	}
	
	//将分数转换为对应的告警级别
	public int getAlarmLevelByScore(int score,BizAlarmInfoBo alarmInfo){
		
		if(score <= Integer.parseInt(alarmInfo.getDeathThreshold())){
			return BizStatusDefine.DEATH_STATUS;
		}else if(score <= Integer.parseInt(alarmInfo.getSeriousThreshold())){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(score <= Integer.parseInt(alarmInfo.getWarnThreshold())){
			return BizStatusDefine.WARN_STATUS;
		}else{
			return BizStatusDefine.NORMAL_STATUS;
		}
		
	}
	
	public String getAlarmInfoByLevel(int alarmLevel){
		if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL);
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS);
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.WARN);
		}else{
			return InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
		}
	}
	
	//深度优先遍历，并排序出度为零的点
	private List<List<Long[]>> loopZeroStartPoint(List<Long[]> topoList,List<List<Long[]>> resultBizId,Set<Long> queryNode,List<Long[]> curGroup){
		
		if(topoList == null || topoList.size() <= 0){
			if(curGroup != null && curGroup.size() > 0){
				resultBizId.add(curGroup);
			}
			return resultBizId;
		}
		
		//分组
		List<Long[]> group = new ArrayList<Long[]>();
		if(curGroup != null && curGroup.size() > 0){
			group = curGroup;
		}
		if(topoList != null && topoList.size() > 0){
			if(queryNode == null){
				queryNode = new HashSet<Long>();
				queryNode.add(topoList.get(0)[0]);
				queryNode.add(topoList.get(0)[1]);
				group.add(topoList.get(0));
				topoList.remove(0);
			}
			boolean isNextArray = true;
			for(int i = 0 ; i < topoList.size() ; i++){
				Long[] nodeArray = topoList.get(i);
				if(queryNode.contains(nodeArray[0]) || queryNode.contains(nodeArray[1])){
					isNextArray = false;
					queryNode.add(nodeArray[0]);
					queryNode.add(nodeArray[1]);
					group.add(nodeArray);
					topoList.remove(i);
					break;
				}
			}
			if(isNextArray){
				resultBizId.add(group);
				return loopZeroStartPoint(topoList, resultBizId, null ,null);
			}else{
				return loopZeroStartPoint(topoList, resultBizId, queryNode,group);
			}
		}

		return null;
	}
	
	private List<List<Long>> getBizChangeOrder(List<Long[]> topoList){
		
		List<List<Long>> result = new ArrayList<List<Long>>();
		
		List<List<Long[]>> resultBizId = new ArrayList<List<Long[]>>();
		
		resultBizId = loopZeroStartPoint(topoList, resultBizId, null, null);
		
		if(resultBizId != null && resultBizId.size() > 0){
			for(List<Long[]> list : resultBizId){
				List<Long> singleResult = new ArrayList<Long>();
				singleResult = getGroupOrder(list, singleResult);
				result.add(singleResult);
			}
		}
		
		return result;
		
	}
	
	private List<Long> getGroupOrder(List<Long[]> list,List<Long> result){
		
		if(list == null || list.size() <= 0){
			return result;
		}
		
		long orderId = -1;
		out : for(Long[] node : list){
			long id = node[1];
			for(Long[] node_2 : list){
				if(node_2[0] == id){
					continue out;
				}
			}
			orderId = id;
			result.add(id);
			break out;
		}
	
		List<Long[]> newList = new ArrayList<Long[]>();
		out : for(Long[] node : list){
			if(node[1] == orderId){
				for(Long[] node_2 : list){
					if((node[0].equals(node_2[0]) || node[0].equals(node_2[1])) && node_2[1] != orderId){
						continue out;
					}
				}
				result.add(node[0]);
			}else{
				newList.add(node);
			}
		}
		
		return getGroupOrder(newList, result);
	
	}
	
	private String generateAlarmTitle(int newAlarmLevel,String name){
		if(newAlarmLevel == BizStatusDefine.NORMAL_STATUS){
			return "业务【" + name + "】恢复正常";
		}else{
			return "业务【" + name + "】发生告警";
		}
	}
	
	/**
	 * 业务状态变化产生告警
	 */
	public void addAlarm(String content,String alarmTitle,BizMainBo bizMain,int newState){
		try {
			
			AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
			alarmSenderParamter.setDefaultMsg(content);
			alarmSenderParamter.setDefaultMsgTitle(alarmTitle);
			alarmSenderParamter.setGenerateTime(new Date());
			alarmSenderParamter.setLevel(getInstanceState(newState));
			alarmSenderParamter.setProvider(AlarmProviderEnum.OC4);
			alarmSenderParamter.setSysID(SysModuleEnum.BUSSINESS);
			alarmSenderParamter.setExt0("业务系统");
			alarmSenderParamter.setExt2(SysModuleEnum.BUSSINESS.name());
			alarmSenderParamter.setProfileID(bizMain.getId());
			alarmSenderParamter.setSourceID(String.valueOf(bizMain.getId()));
			alarmSenderParamter.setSourceName(bizMain.getName());
			alarmSenderParamter.setRuleType(AlarmRuleProfileEnum.biz_profile);
			alarmSenderParamter.setRecoverKeyValue(new String[]{String.valueOf(bizMain.getId())});
			alarmService.notify(alarmSenderParamter);
			logger.error("portal.business.state.addAlarm(business service id="+bizMain.getId()+") successful");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	public InstanceStateEnum getInstanceState(int state){
		if(state == BizStatusDefine.DEATH_STATUS){
			return InstanceStateEnum.CRITICAL;
		}else if(state == BizStatusDefine.SERIOUS_STATUS){
			return InstanceStateEnum.SERIOUS;
		}else if(state == BizStatusDefine.WARN_STATUS){
			return InstanceStateEnum.WARN;
		}else if(state == BizStatusDefine.NORMAL_STATUS){
			return InstanceStateEnum.NORMAL;
		}
		return InstanceStateEnum.NORMAL;
	}
	
	public int getInstanceState(InstanceStateEnum state){
		if(state == InstanceStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == InstanceStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == InstanceStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == InstanceStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	public int getInstanceState(MetricStateEnum state){
		if(state == MetricStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == MetricStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == MetricStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == MetricStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 10;
	}
	
	public String generateBizAlarmContent(BizAlarmInfoBo alarmInfo,int alarmLevel,BizAlarmParameterDefine alarmParameterDefine){
		
		String alarmParameter = "";
		
		
		if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			alarmParameter = alarmInfo.getDeathAlarmContent();
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			alarmParameter = alarmInfo.getSeriousAlarmContent();
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			alarmParameter = alarmInfo.getWarnAlarmContent();
		}else{
			alarmParameter = alarmInfo.getNormalContent();
		}
		String alarmContent = new String(alarmParameter);
		
	    String patternParameter = "\\$\\{.*?\\}";
	    Pattern pattern = Pattern.compile(patternParameter);

	    Matcher matcher = pattern.matcher(alarmParameter);
	    while (matcher.find()) {
	    	String parameterName = matcher.group(0).replace("${", "").replace("}", "");
	    	if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_CONTENT)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeContent());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_NAME)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeName());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_TYPE)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeType());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_ALARM_LEVEL)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizAlarmLevel());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_HEALTH)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizHealth());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_MANAGER)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizManager());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_NAME)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizName());
	    	}
	    }
		
		return alarmContent;
	}
	
	private String queryNodeLastAlarm(long nodeId,long instanceId){
		
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters=new ArrayList<AlarmEventQueryDetail>();
		AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
		detail.setSysID(SysModuleEnum.MONITOR);
		detail.setRecovered(false);
		
		//根据节点ID查询节点绑定信息
		List<String> sList = new  ArrayList<String>();
		BizInstanceNodeBo node = bizCanvasDao.getInstanceNode(nodeId);
		
		List<BizNodeMetricRelBo> metricbos = null;
		
		if(node!=null){
			if(node.getType()==1){//主资源
				
				List<ResourceInstance> resourcechildList = null;
				try {
					resourcechildList = instanceService.getChildInstanceByParentId(node.getInstanceId());
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				for(int j = 0; resourcechildList != null && j < resourcechildList.size(); j++){
					ResourceInstance  riList = resourcechildList.get(j);
					if(riList.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						sList.add(String.valueOf(riList.getId()));
					}
				}
				sList.add(String.valueOf(node.getInstanceId()));
			}else if(node.getType() == 2){//子资源
				
				List<BizNodeMetricRelBo> bos = bizCanvasDao.getChildInstanceBindRelation(node.getId());
				if(bos!=null){
					for (BizNodeMetricRelBo bizNodeMetricRelBo : bos) {
						sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
					}
				}
			}else if(node.getType()==3){//指标
				
				metricbos = bizCanvasDao.getChildInstanceMetricBindRelation(node.getId());
				if(metricbos!=null){
					for (BizNodeMetricRelBo bizNodeMetricRelBo : metricbos) {
						if(bizNodeMetricRelBo.getChildInstanceId() <= 0){
							sList.add(String.valueOf(node.getInstanceId()));
						}else{
							sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
						}
					}
				}
			
			}
		}
		
		detail.setSourceIDes(sList);
		
		filters.add(detail);
		eq.setFilters(filters);
		eq.setOrderASC(true);
		eq.setOrderFieldes(new OrderField[]{OrderField.LEVEL});
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			logger.error(e1.getMessage(),e1);
		}
		
		List<AlarmEvent> events= alarmEventService.findAlarmEvent(eq);
		
		String alarmContent = "";
		
		if(node.getType() == 3){
			//过滤指标
			if(events != null && events.size() > 0){
				for(AlarmEvent event : events){
					for(BizNodeMetricRelBo metric : metricbos){
						if(metric.getChildInstanceId() <= 0){
							//主资源的指标
							if(event.getSourceID().equals(String.valueOf(node.getInstanceId())) && event.getExt3().equals(metric.getMetricId())){
								alarmContent = event.getContent();
								break;
							}
						}else{
							//子资源的指标
							if(event.getSourceID().equals(String.valueOf(metric.getChildInstanceId())) && event.getExt3().equals(metric.getMetricId())){
								alarmContent = event.getContent();
								break;
							}
						}
					}
				}
			}
		}else if(node.getType() == 1){
			//主资源
			if(events != null && events.size() > 0){
				 InstanceStateData stateData = instanceStateService.getStateAdapter(instanceId);
			
				 if(stateData == null){
					 alarmContent = events.get(0).getContent();
				 }else{
					 for(AlarmEvent event : events){
						 if(Long.parseLong(event.getSourceID()) == stateData.getCauseByInstance()){
							 alarmContent = event.getContent();
							 break;
						 }
					 }
					 
					 if(alarmContent.equals("")){
						 logger.error("Get cause alarm error , cause instance : " + stateData.getCauseByInstance());
						 alarmContent = events.get(0).getContent();
					 }
				 }
				 
			}
		}else{
			//子资源
			if(events != null && events.size() > 0){
				alarmContent = events.get(0).getContent();
			}
		}
		
//		if(alarmContent == null || alarmContent.equals("")){
//			
//			for(int i = 0 ; i < 3 ; i++){
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					logger.error(e.getMessage(),e);
//				}
//				
//				events= alarmEventService.findAlarmEvent(eq);
//				
//				if(node.getType()==3){
//					//过滤指标
//					if(events != null && events.size() > 0){
//						for(AlarmEvent event : events){
//							for(BizNodeMetricRelBo metric : metricbos){
//								if(metric.getChildInstanceId() <= 0){
//									//主资源的指标
//									if(event.getSourceID().equals(String.valueOf(node.getInstanceId())) && event.getExt3().equals(metric.getMetricId()) 
//											&& status == getInstanceState(event.getLevel())){
//										alarmContent = event.getContent();
//										break;
//									}
//								}else{
//									//子资源的指标
//									if(event.getSourceID().equals(String.valueOf(metric.getChildInstanceId())) && event.getExt3().equals(metric.getMetricId())
//											&& status == getInstanceState(event.getLevel())){
//										alarmContent = event.getContent();
//										break;
//									}
//								}
//							}
//						}
//					}
//				}else{
//					if(events != null && events.size() > 0 && getInstanceState(events.get(0).getLevel()) == status){
//						alarmContent = events.get(0).getContent();
//					}
//				}
//				
//				if(alarmContent != null && !alarmContent.equals("")){
//					break;
//				}
//				
//			}
//			
//		}
		
		if(alarmContent == null || alarmContent.equals("")){
			alarmContent = "未查询到告警内容";
		}
		
		return alarmContent;
		
	}
	
	public static void main(String[] args) {
		BizStateChange change = new BizStateChange();
		change.getScore("OR(20,50,AND(3*100,4*50),AVG(50,100),50)",20);
//		String newName = "asdf()38&@^@*******%&!{]234}34";
//		newName = newName.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\*", "").replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\$", "");
//		System.out.println(newName);
	}
	
}
