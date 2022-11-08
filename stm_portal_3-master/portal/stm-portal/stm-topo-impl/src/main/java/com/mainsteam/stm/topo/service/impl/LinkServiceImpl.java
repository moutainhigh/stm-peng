package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.enums.IconState;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service
public class LinkServiceImpl implements LinkService{
	Logger logger = Logger.getLogger(LinkServiceImpl.class);
	@Autowired
	private ILinkDao ldao;
	@Autowired
	private INodeDao ndao;
	@Autowired
	private ThirdService tsvc;
	//资源实例服务
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	//策略服务
	@Autowired
	private ProfileService profileService;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	IResourceInstanceExApi resourceExApi;
	
	@Override
	public JSONArray convertLinkState(Long[] linkIds,String linkMetricId) {
		String metricId = dataHelper.mapToCapacityConst(linkMetricId);
		JSONArray linksState = new JSONArray();
		for(Long id : linkIds){
			//1.根据链路实例id查询出链路的from-to节点
			LinkBo link = ldao.getById(id);
			Long linkIstId=link.getInstanceId();
			ResourceInstance linkRe = dataHelper.getResourceInstance(linkIstId);
			if(linkRe==null) continue;
			
			//2.查询多链路数据
			List<LinkBo> linkList = null;
			if(link!=null){
				linkList = ldao.getLinksByFromToId(link.getFrom(), link.getTo());
			}
			
			//3.过滤多链路状态
			JSONObject state = null;
			if(null != linkList && linkList.size() > 2){	//多链路
				state = this.convertMultiLinkState(linkList,metricId);
			}else{	//单链路
				if(!InstanceLifeStateEnum.NOT_MONITORED.equals(linkRe.getLifeState())){
					if(metricId.equals("device")){
						state = new JSONObject();
						InstanceStateEnum tstate = getLinkInstState(linkIstId,metricId);	//TODO:danlianlu&&&&&&&&&&&&&&&&&&&
						state.put("state",tstate.name());
					}else{
						state = tsvc.getMetricState(linkIstId, metricId,true);
					}
				}else{
					state=new JSONObject();
					state.put("state", InstanceLifeStateEnum.NOT_MONITORED.name());
				}
			}
			if(null!=state){
				state.put("id", id);
				linksState.add(state);
			}
		}
		return linksState;
	}
	public InstanceStateEnum getLinkInstState(Long linkInstId,String metricId){
		try{
			logger.error("getLinkInstState("+linkInstId+")");
			ResourceInstance linkRe = dataHelper.getResourceInstance(linkInstId);
			if(null !=linkRe && !InstanceLifeStateEnum.NOT_MONITORED.equals(linkRe.getLifeState())){
				//先判断主资源是否可用,如果主资源不可用，直接返回
				Long srcInstId = dataHelper.getLinkSrcMainInstanceId(linkRe);
				Long desInstId = dataHelper.getLinkDesMainInstanceId(linkRe);
				if(srcInstId!=null){
					InstanceStateEnum srcMainState=dataHelper.getInstAlarmInstanceStateEnum(srcInstId,metricId);
					if(InstanceStateEnum.CRITICAL.equals(srcMainState)||InstanceStateEnum.CRITICAL_NOTHING.equals(srcMainState)){
						return srcMainState;
					}
				}
				if(desInstId!=null){
					InstanceStateEnum desMainState=dataHelper.getInstAlarmInstanceStateEnum(desInstId,metricId);
					if(InstanceStateEnum.CRITICAL.equals(desMainState)||InstanceStateEnum.CRITICAL_NOTHING.equals(desMainState)){
						return desMainState;
					}
				}
				Long srcSubInstId=dataHelper.getLinkSrcSubInstanceId(linkRe);
				Long desSubInstId=dataHelper.getLinkDesSubInstanceId(linkRe);
				ResourceInstance srcSubRi = dataHelper.getResourceInstance(srcSubInstId);
				ResourceInstance desSubRi = dataHelper.getResourceInstance(desSubInstId);
				InstanceLifeStateEnum srcLiftState=InstanceLifeStateEnum.NOT_MONITORED;
				if(srcSubRi!=null){
					srcLiftState=srcSubRi.getLifeState();
				}
				InstanceLifeStateEnum desLiftState=InstanceLifeStateEnum.NOT_MONITORED;
				if(desSubRi!=null){
					desLiftState=desSubRi.getLifeState();
				}
				if(InstanceLifeStateEnum.NOT_MONITORED.equals(srcLiftState) && InstanceLifeStateEnum.NOT_MONITORED.equals(desLiftState)){
					return InstanceStateEnum.NOT_MONITORED;
				}else{
					InstanceStateEnum srcState = InstanceStateEnum.NOT_MONITORED;
					InstanceStateEnum desState = InstanceStateEnum.NOT_MONITORED;
					if(null != srcSubInstId){
						srcState = dataHelper.getInstAlarmInstanceStateEnum(srcSubInstId,metricId);
					}
					if(null != desSubInstId){
						desState = dataHelper.getInstAlarmInstanceStateEnum(desSubInstId,metricId);
					}
					if(srcState.getStateVal()>desState.getStateVal()){
						return srcState;
					}else{
						return desState;
					}
				}
			}else{
				return InstanceStateEnum.NOT_MONITORED;
			}
		}catch(Throwable e){
			logger.error("LinkServiceImpl.getLinkInstState",e);
			return InstanceStateEnum.NOT_MONITORED;
		}
	}
	/**
	 * 转换多链路状态
	 * @param list
	 * @param metricId
	 * @return
	 */
	private JSONObject convertMultiLinkState(List<LinkBo> list,String metricId){
		JSONObject state = new JSONObject();
		//用来统计多链路的状态，最终计算一个最严重的状态返回
		List<MetricStateEnum> metricStates = new ArrayList<MetricStateEnum>();
		List<InstanceStateEnum> deviceStates = new ArrayList<InstanceStateEnum>();
		List<Long> instancesIdsTmp = new ArrayList<Long>();
		//多链路先获取lifeState
		for(LinkBo link : list){
			Long instanceId = link.getInstanceId();
			if("link".equals(link.getType()) && null != instanceId){
				instancesIdsTmp.add(instanceId);
			}
		}
		if(instancesIdsTmp.size() > 0){
			//转换多链路监控状体(lifeState)
			InstanceLifeStateEnum lifeState = this.converMulitLinkLifeState(instancesIdsTmp);
			//先判断监控状态（非蓝色），如果监控了则再判断实时状态
			if(InstanceLifeStateEnum.MONITORED.equals(lifeState)){
				for(Long instanceId:instancesIdsTmp){
					ResourceInstance linkRe = dataHelper.getResourceInstance(instanceId);
					if(linkRe==null) continue;
					if(metricId.equals("device")){
						InstanceStateEnum tstate = getLinkInstState(instanceId,metricId);
						logger.error("状态为"+tstate.name());
						deviceStates.add(tstate);
					}else{
						String tmpstate=tsvc.getMetricState(instanceId, metricId,true).getString("state");
						metricStates.add(MetricStateEnum.valueOf(tmpstate));
					}
				}
				//寻找最严重的状态
				if(!metricId.equals("device")){
					MetricStateEnum stateEnum = MetricStateEnum.NORMAL_NOTHING;
					for(MetricStateEnum tstate : metricStates){
						if(stateEnum.getStateVal()<tstate.getStateVal()){
							stateEnum=tstate;
						}
					}
					state.put("state",stateEnum.name());
				}else{
					InstanceStateEnum stateEnum = InstanceStateEnum.NOT_MONITORED;
					for(InstanceStateEnum tstate : deviceStates){
						if(stateEnum.getStateVal()<tstate.getStateVal()){
							stateEnum=tstate;
						}
					}
					logger.error("最严重状态为"+stateEnum.name());
					state.put("state",stateEnum.name());
				}
			}else{	//多链路都未监控
				state.put("state",InstanceStateEnum.NOT_MONITORED.name());
			}
		}
		return state;
	}
	
	/**
	 * 转换多链路监控状态
	 * @param instanceIds
	 * @return
	 */
	private InstanceLifeStateEnum converMulitLinkLifeState(List<Long> instanceIds){
		InstanceLifeStateEnum result = InstanceLifeStateEnum.MONITORED;
		try {
			List<ResourceInstance> instances = rsvc.getResourceInstances(instanceIds);
			for(ResourceInstance instance:instances){
				InstanceLifeStateEnum state = instance.getLifeState();
				//默认为监控状态，如果发现其中一条链路为非监控，则整个多链路是非监控的，所以可以直接返回
				if(InstanceLifeStateEnum.NOT_MONITORED.equals(state)){
					return InstanceLifeStateEnum.NOT_MONITORED;
				}
			}
		} catch (InstancelibException e) {
			logger.error("查询链路资源实例错误",e);
		}
		return result;
	}

	/**
	 * 1.查询出两端相同的多链路和连线
	 * 2.删除多链路
	 */
	@Override
	public void removeMultiLink(Long id) {
		//1.查询出两端相同的多链路和连线
		LinkBo link = ldao.getById(id);
		List<LinkBo> list = ldao.getLinksByFromToId(link.getFrom(), link.getTo());
		
		//2.删除多链路
		List<Long> ids = new ArrayList<Long>();
		for(LinkBo bo:list){
			ids.add(bo.getId());
		}
		if(ids.size() > 0) ldao.deleteByIds(ids);
	}
	
	/**
	 * 1.查询链路数据
	 * 2.转换成前台数据
	 */
	@Override
	public void selectMultiLinkByPage(Page<LinkBo, LinkBo> page)throws InstancelibException {
		//1.查询链路数据(包括链路和连线)
		ldao.selectMutilLinkByPage(page);
		List<LinkBo>  linkList = page.getDatas();
		
		//2.1 查询node实例
		List<Long>  nodeIds = new ArrayList<Long>();
		List<Long>  linkResourceIds = new ArrayList<Long>();
		for(LinkBo link:linkList){
			nodeIds.add(link.getFrom());
			nodeIds.add(link.getTo());
			if(null != link.getInstanceId()) linkResourceIds.add(link.getInstanceId());	//已经实例化的链路
		}
		//去掉重复
		Set<Long> setTmp = new HashSet<Long>();
		setTmp.addAll(nodeIds);
		nodeIds.clear();
		nodeIds.addAll(setTmp);
		//获取node节点
		List<NodeBo> nodeList = ndao.getByIds(nodeIds);
		List<Long>  nodeInstaneIds = new ArrayList<Long>();
		for(NodeBo node:nodeList){
			if(null != node.getInstanceId()) nodeInstaneIds.add(node.getInstanceId());
		}
		//获取node节点资源实例
		List<ResourceInstance> nodeInstances = rsvc.getResourceInstances(nodeInstaneIds);
		Map<Long, ResourceInstance> nodeInstanceMap = new HashMap<Long, ResourceInstance>();
		for(NodeBo node:nodeList){
			for(ResourceInstance nodeInstance:nodeInstances){
				if(null != node.getInstanceId() && node.getInstanceId().longValue() == nodeInstance.getId()){
					nodeInstanceMap.put(node.getId(), nodeInstance);
					break;
				}
			}
		}
		
		//2.2 查询链路实例
		List<ResourceInstance> linkInstances = rsvc.getResourceInstances(linkResourceIds);
		Map<Long, ResourceInstance> linkInstanceMap = new HashMap<Long, ResourceInstance>();
		for(ResourceInstance linkInstance:linkInstances){
			linkInstanceMap.put(linkInstance.getId(), linkInstance);
		}
		
		//2.3 转换数据(node和链路)
		for(LinkBo link:linkList){
			//node数据
			String defaultVal = "- -";
			ResourceInstance fromNodeInstance = nodeInstanceMap.get(link.getFrom());
			ResourceInstance toNodeInstance = nodeInstanceMap.get(link.getTo());
			boolean fromNodeCheck = (null!=fromNodeInstance);
			boolean toNodeCheck = (null!=toNodeInstance);
			link.setSrcMainInstName(fromNodeCheck?fromNodeInstance.getShowName():defaultVal);	//源端名称
			link.setSrcMainInstIP(fromNodeCheck?fromNodeInstance.getShowIP():defaultVal);	//源端IP
			link.setSrcInstanceId(fromNodeCheck?fromNodeInstance.getId():0);					//目的端ID
			link.setDestMainInsName(toNodeCheck?toNodeInstance.getShowName():defaultVal);		//目的端名称
			link.setDestMainInstIP(toNodeCheck?toNodeInstance.getShowIP():defaultVal);		//目的端IP
			link.setDestInstanceId(toNodeCheck?toNodeInstance.getId():0);						//目的端ID
			//链路数据
			ResourceInstance linkInstance = linkInstanceMap.get(link.getInstanceId());
			if("link".equals(link.getType())){
				String defaultConstant = "- -";
				if(null != linkInstance){
					String srcSubInstId = resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_SRC_SUBINST_ID);		//源接口id
					logger.error("链路 linkInstanceId="+linkInstance.getId()+"  ,srcSubInstId="+srcSubInstId);
					ResourceInstance srcIfInstance = null;
					if(!defaultConstant.equals(srcSubInstId)){
						long srcIfIdT = Long.valueOf(srcSubInstId);	
						srcIfInstance = rsvc.getResourceInstance(srcIfIdT);
					}
					String destSubInstId = resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_DEST_SUBINST_ID);		//目的接口id
					logger.error("链路 instanceId="+linkInstance.getId()+"  ,destSubInstId="+destSubInstId);
					ResourceInstance destIfInstance = null;
					if(!defaultConstant.equals(destSubInstId)){
						long destIfIdT = Long.valueOf(destSubInstId);
						destIfInstance = rsvc.getResourceInstance(destIfIdT);
					}
					String linkState = resourceExApi.convertLinkStateColor(linkInstance,srcIfInstance,destIfInstance);
					link.setInsStatus(linkState);//链路状态
					link.setMonitorStatus(linkInstance.getLifeState().toString().toLowerCase());	//监控状态
					link.setSrcIfName(resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_SRC_IFNAME));		//源端端口名称
					link.setDestIfName(resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_DEST_IFNAME));	//目的端端口名称
				}else{	//未实例化链路展示出接口索引
					link.setSrcIfName(link.getFromIfIndex() !=null?link.getFromIfIndex().toString():"");
					link.setDestIfName(link.getToIfIndex()!=null?link.getToIfIndex().toString():"");
				}
			}else if("line".equals(link.getType())){
				link.setMonitorStatus("not_monitored");	//监控状态
				link.setSrcIfName(defaultVal);
				link.setDestIfName(defaultVal);
			}
		}
	}
	
	@Override
	public boolean updateBandWidthUtil(String bandInfo) {
		boolean rst = true;
		JSONObject info = (JSONObject) JSONObject.parse(bandInfo);
		String instanceIds = info.getString("instanceIds");
		String[] ids =  instanceIds.replace("[", "").replace("]", "").split(",");
		//带宽利用率阈值
		JSONObject band = info.getJSONObject("band");
		for(String id:ids){
			JSONObject thresholdRetn = tsvc.refreshThreshold(Long.valueOf(id), MetricIdConsts.IF_IFBANDWIDTHUTIL, band.getFloat("min"),band.getFloat("max"));
			if(rst && !thresholdRetn.getInteger("state").equals(200)){
				rst = false;
			}
		}
		return rst;
	}
	@Override
	public double getBandWidth(Long instanceId,String unit){
		double bwidth = 0;
		DecimalFormat df = new DecimalFormat("0.00");
		String[] bandWidth = tsvc.getMetrics(instanceId,"ifSpeed",MetricTypeEnum.InformationMetric);
		if(null!=bandWidth && bandWidth.length>0){
			for(String str : bandWidth){
				try {
					bwidth=Float.parseFloat(str);
				} catch (NumberFormatException e) {
					bwidth=0;
				}
			}
		}
		if(unit==null){
			return Double.valueOf(df.format(Double.valueOf(bwidth)));
		}else{
			BigDecimal divide=null;
			switch(unit){
			case "kbps":
				divide = new BigDecimal(1000);
				break;
			case "Mbps":
				divide = new BigDecimal(1000000);
				break;
			default:
				divide = new BigDecimal(1);
			}
			return Double.valueOf(df.format(new BigDecimal(bwidth).divide(divide)));
		}
	}
	@Override
	public JSONObject getDetailInfo(Long instanceId) {
		JSONObject retn = new JSONObject();
		//图元
		List<LinkBo> lbs= ldao.getLinksByInstanceIds(new long[]{instanceId});
		if(!lbs.isEmpty()){
			LinkBo lb = lbs.get(0);
			retn.put("direction", lb.isDirection());
		}
		//源接口
		JSONObject ifAttr = tsvc.getLinkModuleProps(instanceId);
		//源设备接口
		if(ifAttr.containsKey(LinkResourceConsts.PROP_SRC_MAININST_ID)){
			JSONArray srcIfs = tsvc.getInstancesIfs(ifAttr.getLong(LinkResourceConsts.PROP_SRC_MAININST_ID),false);
			retn.put("srcIfs", srcIfs);
		}
		if(ifAttr.containsKey(LinkResourceConsts.PROP_DEST_MAININST_ID)){
			JSONArray desIfs = tsvc.getInstancesIfs(ifAttr.getLong(LinkResourceConsts.PROP_DEST_MAININST_ID),false);
			retn.put("desIfs", desIfs);
		}
		retn.put("resIp", ifAttr.getString(LinkResourceConsts.PROP_SRC_MAININST_IP));
		retn.put("desIp", ifAttr.getString(LinkResourceConsts.PROP_DEST_MAININST_IP));
		retn.put("ifAttr", ifAttr);
		
		//吞吐量-带宽
		long collResourceId = 0l;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_COLL_SUBINST_ID))){
			collResourceId = ifAttr.getLongValue(LinkResourceConsts.PROP_COLL_SUBINST_ID);	//取值接口
		}
		long srcSubInstId = 0;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_SRC_SUBINST_ID))){
			srcSubInstId = ifAttr.getLongValue(LinkResourceConsts.PROP_SRC_SUBINST_ID);	//源接口
		}
		long destSubInstId=0l;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_DEST_SUBINST_ID))){
			destSubInstId = ifAttr.getLongValue(LinkResourceConsts.PROP_DEST_SUBINST_ID);	//目的接口
		}
		retn.put("bandWidth",getBandWidth(collResourceId,"Mbps"));	//取值来至取值接口
//		retn.put("bandWidth",getBandWidth(instanceId,"Mbps"));	//取值来至链路
		//计算两种带宽，方便联动
		if(collResourceId==srcSubInstId){
			retn.put("srcBandWidth",retn.getLong("bandWidth"));
		}else{
			retn.put("srcBandWidth",getBandWidth(srcSubInstId,"Mbps"));
		}
		if(collResourceId==destSubInstId){
			retn.put("desBandWidth",retn.getLong("bandWidth"));
		}else{
			retn.put("desBandWidth",getBandWidth(destSubInstId,"Mbps"));
		}
		//获取阈值
		//带宽利用率阈值
		JSONObject bandThreshold= tsvc.getThreshold(instanceId, MetricIdConsts.IF_IFBANDWIDTHUTIL);
		retn.put("instanceId", instanceId);
		retn.put("bandThreshold", bandThreshold);
		
		JSONObject profile = tsvc.getProfileIdByInstanceId(instanceId);	//策略id
		Long profileId = profile.getLongValue("profileId");
		try {
			Profile pro = profileService.getProfilesById(profileId);
			if(null != pro){
				MetricSetting metricSet = pro.getMetricSetting();
				List<ProfileMetric> proMetricList = metricSet.getMetrics();
				for(ProfileMetric metric:proMetricList){
					switch(metric.getMetricId()){
						case MetricIdConsts.METRIC_AVAILABLE:
							retn.put("availabilityAlarm", metric.isAlarm());
							break;
						case MetricIdConsts.METRIC_IFOCTETSSPEED:
							retn.put("flowAlarm", metric.isAlarm());
							break;
						case MetricIdConsts.IF_IFBANDWIDTHUTIL:
							retn.put("bandAlarm", metric.isAlarm());
							break;
					}
				}
			}
		} catch (ProfilelibException e) {
			logger.error("编辑链路，更新告警信息异常！",e);
		}
		return retn;
	}
	@Override
	@Transactional
	public boolean refreshLink(String linkInfo) {
		boolean flag = false;
		if(StringUtils.isNotBlank(linkInfo)){
			JSONObject info = (JSONObject) JSONObject.parse(linkInfo);
			Long instanceId = info.getLong("instanceId");
			//更新带宽利用率阈值
			JSONObject band = info.getJSONObject("band");
			JSONObject bwthresholdRetn = tsvc.refreshThreshold(instanceId, MetricIdConsts.IF_IFBANDWIDTHUTIL, band.getFloat("min"),band.getFloat("max"));
			//更新总流量阈值
			JSONObject flow = info.getJSONObject("flow");
			JSONObject tsthresholdRetn = tsvc.refreshThreshold(instanceId, MetricIdConsts.METRIC_IFOCTETSSPEED, flow.getFloat("min"),flow.getFloat("max"));
			//更新告警
			Map<String, Boolean> alarmsMap = new HashMap<String, Boolean>();
			alarmsMap.put(MetricIdConsts.METRIC_AVAILABLE, info.getBoolean("availabilityAlarm"));
			alarmsMap.put(MetricIdConsts.IF_IFBANDWIDTHUTIL, band.getBoolean("alarm"));
			alarmsMap.put(MetricIdConsts.METRIC_IFOCTETSSPEED, flow.getBoolean("alarm"));
			JSONObject profile = tsvc.getProfileIdByInstanceId(instanceId);	//策略id
			Long profileId = profile.getLongValue("profileId");
			try {
				profileService.updateProfileMetricAlarm(profileId, alarmsMap);
			} catch (ProfilelibException e) {
				logger.error("编辑链路，更新告警信息异常！",e);
			}
			
			if(bwthresholdRetn.getInteger("state").equals(700)||tsthresholdRetn.getInteger("state").equals(700)){
				return false;
			}
			//源接口
			if(info.containsKey("srcSubInstId") && StringUtils.isNotBlank(info.getString("srcSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_SUBINST_ID, new String[]{info.getString("srcSubInstId")});
			}
			//目的接口
			if(info.containsKey("destSubInstId")  && StringUtils.isNotBlank(info.getString("destSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_SUBINST_ID, new String[]{info.getString("destSubInstId")});
			}
			//取值接口
			if(info.containsKey("collSubInstId") && StringUtils.isNotBlank(info.getString("collSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_COLL_SUBINST_ID, new String[]{info.getString("collSubInstId")});
			}
			//原接口索引
			if(info.containsKey("srcIfIndex")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_IFINDEX, new String[]{info.getString("srcIfIndex")});
			}
			//原接口名称
			if(info.containsKey("srcIfName")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_IFNAME, new String[]{info.getString("srcIfName")});
			}
			//目的接口索引
			if(info.containsKey("desIfIndex")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_IFINDEX, new String[]{info.getString("desIfIndex")});
			}
			//目的接口名称
			if(info.containsKey("desIfName")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_IFNAME, new String[]{info.getString("desIfName")});
			}
			//更新链路图元的下行方向
			if(info.containsKey("srcIfIndex") || info.containsKey("desIfIndex") || info.containsKey("direction")){
				List<LinkBo> lbs = ldao.getLinksByInstanceIds(new long[]{instanceId});
				for(LinkBo lb : lbs){
					if(info.containsKey("direction")) lb.setDirection(info.getBooleanValue("direction"));
					if(info.containsKey("srcIfIndex")) lb.setFromIfIndex(info.getLong("srcIfIndex"));
					if(info.containsKey("desIfIndex")) lb.setToIfIndex(info.getLong("desIfIndex"));
				}
				ldao.update(lbs);
			}
			//备注
			String note = info.getString("note");
			if(null!=note && !"".equals(note)){
				tsvc.refreshModuleProp(instanceId, "note", new String[]{note});
			}
			flag = true;
		}
		return flag;
	}
	@Override
	public boolean addLink(LinkBo lb) {
		List<LinkBo> lks = ldao.findLink(lb.getFrom(),lb.getTo());
		for(LinkBo lk : lks){
			if("line".equals(lk.getType())){
				return false;
			}
		}
		List<LinkBo> lbs = new ArrayList<LinkBo>();
		lb.setType("line");
        lb.setDirection(true);
        lbs.add(lb);
		ldao.save(lbs);
		return true;
	}
	@Override
	public void removeLink(List<Long> ids) throws Exception{
		//查询链路->删除资源实例链路
		List<LinkBo> links = ldao.getLinksByIds(ids);
		List<Long> instanceIds = new ArrayList<Long>();
		if(null != links && links.size() > 0){
			for(LinkBo link:links){
				if(null != link.getInstanceId()){
					instanceIds.add(link.getInstanceId());
				}
			}
		}
		
		if(instanceIds.size() > 0){
			rsvc.removeResourceInstanceByLinks(instanceIds);
		}
		
		//删除拓扑链路
		ldao.deleteByIds(ids);
	}
	@Override
	public JSONObject addMonitor(List<Long> ids) {
		JSONObject retn = new JSONObject();
		for(Long id : ids){
			LinkBo link = ldao.getById(id);
			if(null!=link){
//				logger.error("addMonitor()中，查询出的linkBo="+JSONObject.toJSONString(link));
                JSONObject linkInfo = this.packageLinkInfo(link);
//                logger.error("-----加入监控的链路信息："+JSONObject.toJSONString(linkInfo));
//				logger.error("实例化链路中，封装链路instance异常后，调用addMonitor()中packageLinkInfo()封装后的链路数据linkInfo="+linkInfo.toJSONString());
                Long instanceId = tsvc.newLink(linkInfo);
//                    logger.error("------链路加入监控返回实例id："+instanceId);
                if (null != instanceId) {
                    //更新该链路的资源实例id
                    link.setInstanceId(instanceId);
                    ldao.updateInstanceId(link);
                    retn.put(Long.toString(link.getId()), Long.toString(instanceId));
                }
            } else {
                logger.error("链路加入监控时：链路["+id+"]不存在");
			}
		}
		return retn;
	}
	private JSONObject getInstanceIf(Long instanceId,Long ifIndex){
		JSONArray ifs = tsvc.getInstancesIfs(instanceId,false);
		for(Object obj : ifs){
			JSONObject ifInfo = (JSONObject)obj;
			Long _ifIndex = ifInfo.getLong("ifIndex");
			if(_ifIndex!=null && null!=ifIndex && ifIndex.equals(_ifIndex)){
				return ifInfo;
			}
		}
//		logger.error("资源["+instanceId+"]的所有被监控的接口(NetInterface)数据：\n"+ifs.toJSONString());
        logger.error("链路加入监控时：资源实例ID[" + instanceId + "]未找到索引[" + ifIndex + "]的接口");
        return null;
	}
	private JSONObject packageLinkInfo(LinkBo link){
		JSONObject linkInfo = new JSONObject();
		Long fromNodeId = link.getFrom();
		NodeBo fromNode = ndao.getById(fromNodeId);
		Long toNodeId = link.getTo();
		NodeBo toNode = ndao.getById(toNodeId);
		Long srcMainInstanceId = null;
		Long desMainInstanceId = null;
		Long srcIfInstanceId = null;
		Long desIfInstanceId = null;
		Long fromIfIndex = link.getFromIfIndex();
		Long toIfIndex = link.getToIfIndex();
        if (fromNode != null && null != fromNode.getInstanceId()) {
            srcMainInstanceId = fromNode.getInstanceId();
//			logger.error("------源端接口索引:"+srcMainInstanceId+"_"+fromIfIndex);
            JSONObject srcInfo = this.getInstanceIf(srcMainInstanceId,fromIfIndex);
			if(null!=srcInfo){
				if(srcInfo.containsKey("instanceId")){
					srcIfInstanceId=srcInfo.getLong("instanceId");
				}else{
					logger.error("链路加入监控时：源端资源实例ID["+srcMainInstanceId+"]的接口(索引)["+fromIfIndex+"]为空--->导致源接口实例id(srcIfInstanceId)为空");
				}
			}else{
				logger.error("链路加入监控时：源端资源实例ID["+srcMainInstanceId+"]为空-->导致源端信息srcInfo为空");
			}
		}else{
			logger.error("链路加入监控时：fromNode图元ID["+fromNodeId+"]或则fromNode.getInstanceId()为空");
		}
        if (toNode != null && null != toNode.getInstanceId()) {
            desMainInstanceId = toNode.getInstanceId();
//            logger.error("------目的端接口索引:"+desMainInstanceId+"_"+toIfIndex);
            JSONObject desInfo = getInstanceIf(desMainInstanceId,toIfIndex);
			if(null!=desInfo){
				if (desInfo.containsKey("instanceId")){
					desIfInstanceId=desInfo.getLong("instanceId");
				}else{
					logger.error("链路加入监控时：目的端资源实例ID["+desMainInstanceId+"]的接口(索引)["+toIfIndex+"]为空--->导致目的接口实例id(desIfInstanceId)为空");
				}
			}else{
				logger.error("链路加入监控时：目的端资源实例ID["+desMainInstanceId+"]为空-->导致目的端信息desInfo为空");
			}
		}else{
			logger.error("链路加入监控时：toNode图元ID["+toNodeId+"]或则toNode.getInstanceId()为空");
		}
		if(srcMainInstanceId!=null){
			linkInfo.put("valueMainInstanceId",srcMainInstanceId);
		}else if(desMainInstanceId!=null){
//			logger.error("链路加入监控时：srcMainInstanceId为空，desMainInstanceId不为空");
            linkInfo.put("valueMainInstanceId",desMainInstanceId);
		}else{
			logger.error("链路加入监控时：srcMainInstanceId和desMainInstanceId为空");
		}
		if(srcIfInstanceId!=null){
			linkInfo.put("valueInstanceId",srcIfInstanceId);
		}else if(desIfInstanceId!=null){
//			logger.error("链路加入监控时：srcIfInstanceId为空，desIfInstanceId不为空");
            linkInfo.put("valueInstanceId",desIfInstanceId);
		}else{
			logger.error("链路加入监控时：srcIfInstanceId和desIfInstanceId为空");
		}
		//一条链路必须有取值接口，否则的话这是失败的
		if(linkInfo.containsKey("valueMainInstanceId") && linkInfo.containsKey("valueInstanceId")){
			linkInfo.put("srcMainInstanceId", srcMainInstanceId);
			linkInfo.put("desMainInstanceId", desMainInstanceId);
			linkInfo.put("srcIfInstanceId", srcIfInstanceId);
			linkInfo.put("desIfInstanceId", desIfInstanceId);
			linkInfo.put("srcIfIndex",fromIfIndex );
			linkInfo.put("desIfIndex",toIfIndex);
			return linkInfo;
		}else{
			logger.error("packageLinkInfo（）中valueMainInstanceId和valueInstanceId都为空-->-->导致链路信息为空,return null-->不会newLink（）");
			return null;
		}
	}
	@Override
	public JSONObject updateAttr(LinkBo link) {
		JSONObject retn = new JSONObject();
		if (link.getId()==null || link.getId()<0l) {
			retn.put("state", 700);
			retn.put("msg", "无效操作");
		}else{
			//获取数据库存在的所有属性
			LinkBo lb = ldao.getById(link.getId());
			if(lb!=null){
				String attr = lb.getAttr();
				String nattr = link.getAttr();
				if(nattr!=null){
					JSONObject da = new JSONObject();
					if(attr!=null){
						da = (JSONObject)JSON.parse(attr);
					}
					JSONObject na = (JSONObject)JSON.parse(nattr);
					//用更新的值去覆盖老的值
					for(Map.Entry<String, Object> entry: na.entrySet()){
						da.put(entry.getKey(), entry.getValue());
					}
					link.setAttr(da.toJSONString());
				}else{
					link.setAttr(attr);
				}
			}
			int count = ldao.updateAttr(link);
			if(count>0){
				retn.put("state", 200);
				retn.put("msg", "更新成功");
			}else{
				retn.put("state", 200);
				retn.put("msg", "更新失败");
			}
		}
		return retn;
	}
	
	/**
	 * 设置链路提示默认信息为--
	 * @param collector
	 */
	private void setLinkTipDefault(JSONObject collector){
		collector.put("index", DataHelper.NODATA_FLAG);
		collector.put("shot", DataHelper.NODATA_FLAG);
		collector.put("ip", DataHelper.NODATA_FLAG);
		collector.put("name", DataHelper.NODATA_FLAG);
		collector.put("portName", DataHelper.NODATA_FLAG);
		collector.put("portType", DataHelper.NODATA_FLAG);
		collector.put("portBandWidth", DataHelper.NODATA_FLAG);
		collector.put("mac",DataHelper.NODATA_FLAG);
		collector.put("manageState", DataHelper.NODATA_FLAG);
		collector.put("operateState",DataHelper.NODATA_FLAG);
		collector.put("bandRatio", DataHelper.NODATA_FLAG);
		collector.put("receiveSpeed", DataHelper.NODATA_FLAG);
		collector.put("sendSpeed", DataHelper.NODATA_FLAG);
		collector.put("receiveBandWidthRatio", DataHelper.NODATA_FLAG);
		collector.put("sendBandWidthRatio", DataHelper.NODATA_FLAG);
		collector.put("portState", DataHelper.NODATA_FLAG);
	}
	
	@Override
	public void linkSubinstTip(JSONObject collector,ResourceInstance linkResInst,boolean isSrc){
		this.setLinkTipDefault(collector);	//设置一遍默认值
		
		if(linkResInst==null) return;
		Long subMainInstId = null;
		Long subInstId = null;
		if(isSrc){
			subMainInstId=dataHelper.getLinkSrcMainInstanceId(linkResInst);
			subInstId=dataHelper.getLinkSrcSubInstanceId(linkResInst);
			collector.put("index",dataHelper.getLinkSrcIfIndex(linkResInst));
		}else{
			subMainInstId=dataHelper.getLinkDesMainInstanceId(linkResInst);
			subInstId=dataHelper.getLinkDesSubInstanceId(linkResInst);
			collector.put("index",dataHelper.getLinkDesIfIndex(linkResInst));
		}
		
		ResourceInstance subMainInstance=dataHelper.getResourceInstance(subMainInstId);
		if(null != subMainInstance){
			String subMainInstState = dataHelper.getInstanceState(subMainInstance.getId());	//获取快照标记
			collector.put("shot", subMainInstState);
			collector.put("ip", dataHelper.getResourceInstanceManageIp(subMainInstance));
			collector.put("name", dataHelper.getResourceInstanceShowName(subMainInstance));
		}
		String portName = dataHelper.getPortName(subInstId);
		if(portName.equals(DataHelper.NODATA_FLAG) && !DataHelper.NODATA_FLAG.equals(collector.getString("index"))){
			portName=dataHelper.getPortName(subMainInstId, collector.getLong("index"));
		}
		collector.put("portName", portName);
		collector.put("portType", dataHelper.getPortType(subInstId));
		collector.put("portBandWidth", dataHelper.getPortBandWidth(subInstId, "Mbps"));
		String mac = dataHelper.getResourceInstanceIfMac(subInstId);
		if(DataHelper.NODATA_FLAG.equals(mac)){
			mac=dataHelper.getResourceInstanceIfMAC(subInstId);
		}
		if(DataHelper.NODATA_FLAG.equals(mac)){
			mac=dataHelper.getResourceInstanceMac(subInstId);
		}
		collector.put("mac",mac);
		collector.put("manageState", dataHelper.getResourceInstanceManageState(subInstId));
		collector.put("operateState", dataHelper.getResourceInstanceOperateState(subInstId));
		//获取链路状态，如果是灰色的状态，不显示历史数据
		JSONObject state = tsvc.getInstanceState(linkResInst.getId());
		String stateStr = state.getString("state");
		if(stateStr.equals(IconState.NODATA.getName()) || stateStr.equals(IconState.DISABLED.getName())){
			collector.put("bandRatio", DataHelper.NODATA_FLAG);
			collector.put("receiveSpeed", DataHelper.NODATA_FLAG);
			collector.put("sendSpeed", DataHelper.NODATA_FLAG);
			collector.put("receiveBandWidthRatio", DataHelper.NODATA_FLAG);
			collector.put("sendBandWidthRatio", DataHelper.NODATA_FLAG);
		}else{//有数据状态显示当前数据
			collector.put("bandRatio", dataHelper.getPortBandWidthRatio(subInstId));
			collector.put("receiveSpeed", dataHelper.getPortReceiveSpeed(subInstId, "Mbps"));
			collector.put("sendSpeed", dataHelper.getPortSendSpeed(subInstId, "Mbps"));
			collector.put("receiveBandWidthRatio", dataHelper.getPortReceiveRatio(subInstId));
			collector.put("sendBandWidthRatio", dataHelper.getPortSendRatio(subInstId));
		}
		ResourceInstance subInstance = dataHelper.getResourceInstance(subInstId);
		collector.put("portState", subInstance==null?DataHelper.NODATA_FLAG:dataHelper.getResourceInstanceState(subInstance));
	}
	
	@Override
	public JSONObject getLinkInfoForTip(Long linkInstanceId) {
		JSONObject retn = new JSONObject();
		ResourceInstance linkInstance = dataHelper.getResourceInstance(linkInstanceId);
		//源端
		JSONObject src = new JSONObject();
		//组装源端口tip信息
		linkSubinstTip(src,linkInstance,true);
		retn.put("src", src);
		//目的端
		JSONObject des = new JSONObject();
		//组装目的端口tip信息
		linkSubinstTip(des,linkInstance,false);
		retn.put("des", des);
		retn.put("alarmInfo", dataHelper.getLatestMsgAlarmEventForLink(linkInstanceId));
		return retn;
	}
	@Override
	public Long getValueInstId(Long instId) {
		ResourceInstance linkInst = dataHelper.getResourceInstance(instId);
		if(null!=linkInst){
			return dataHelper.getLinkValueInstanceId(linkInst);
		}
		return null;
	}
	@Override
	public JSONArray getNodeStates(Long[] nodeIds, String nodeMetricId) {
		JSONArray nodes = new JSONArray();
		String metricId = dataHelper.mapToCapacityConst(nodeMetricId);
		for(Long nodeId : nodeIds){
			JSONObject state = null;
			NodeBo nb = ndao.getById(nodeId);
			if(nb!=null && nb.getInstanceId()!=null){
				Long instId = nb.getInstanceId();
				if(metricId.equals("device")){//如果是设备状态
					state = tsvc.getInstanceState(instId);
				}else{
					state = tsvc.getMetricState(instId,metricId,false);
				}
			}
			if(null!=state){
				state.put("id", nodeId);
				nodes.add(state);
			}
		}
		return nodes;
	}
}
