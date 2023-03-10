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
	//??????????????????
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	//????????????
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
			//1.??????????????????id??????????????????from-to??????
			LinkBo link = ldao.getById(id);
			Long linkIstId=link.getInstanceId();
			ResourceInstance linkRe = dataHelper.getResourceInstance(linkIstId);
			if(linkRe==null) continue;
			
			//2.?????????????????????
			List<LinkBo> linkList = null;
			if(link!=null){
				linkList = ldao.getLinksByFromToId(link.getFrom(), link.getTo());
			}
			
			//3.?????????????????????
			JSONObject state = null;
			if(null != linkList && linkList.size() > 2){	//?????????
				state = this.convertMultiLinkState(linkList,metricId);
			}else{	//?????????
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
				//??????????????????????????????,???????????????????????????????????????
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
	 * ?????????????????????
	 * @param list
	 * @param metricId
	 * @return
	 */
	private JSONObject convertMultiLinkState(List<LinkBo> list,String metricId){
		JSONObject state = new JSONObject();
		//???????????????????????????????????????????????????????????????????????????
		List<MetricStateEnum> metricStates = new ArrayList<MetricStateEnum>();
		List<InstanceStateEnum> deviceStates = new ArrayList<InstanceStateEnum>();
		List<Long> instancesIdsTmp = new ArrayList<Long>();
		//??????????????????lifeState
		for(LinkBo link : list){
			Long instanceId = link.getInstanceId();
			if("link".equals(link.getType()) && null != instanceId){
				instancesIdsTmp.add(instanceId);
			}
		}
		if(instancesIdsTmp.size() > 0){
			//???????????????????????????(lifeState)
			InstanceLifeStateEnum lifeState = this.converMulitLinkLifeState(instancesIdsTmp);
			//??????????????????????????????????????????????????????????????????????????????
			if(InstanceLifeStateEnum.MONITORED.equals(lifeState)){
				for(Long instanceId:instancesIdsTmp){
					ResourceInstance linkRe = dataHelper.getResourceInstance(instanceId);
					if(linkRe==null) continue;
					if(metricId.equals("device")){
						InstanceStateEnum tstate = getLinkInstState(instanceId,metricId);
						logger.error("?????????"+tstate.name());
						deviceStates.add(tstate);
					}else{
						String tmpstate=tsvc.getMetricState(instanceId, metricId,true).getString("state");
						metricStates.add(MetricStateEnum.valueOf(tmpstate));
					}
				}
				//????????????????????????
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
					logger.error("??????????????????"+stateEnum.name());
					state.put("state",stateEnum.name());
				}
			}else{	//?????????????????????
				state.put("state",InstanceStateEnum.NOT_MONITORED.name());
			}
		}
		return state;
	}
	
	/**
	 * ???????????????????????????
	 * @param instanceIds
	 * @return
	 */
	private InstanceLifeStateEnum converMulitLinkLifeState(List<Long> instanceIds){
		InstanceLifeStateEnum result = InstanceLifeStateEnum.MONITORED;
		try {
			List<ResourceInstance> instances = rsvc.getResourceInstances(instanceIds);
			for(ResourceInstance instance:instances){
				InstanceLifeStateEnum state = instance.getLifeState();
				//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				if(InstanceLifeStateEnum.NOT_MONITORED.equals(state)){
					return InstanceLifeStateEnum.NOT_MONITORED;
				}
			}
		} catch (InstancelibException e) {
			logger.error("??????????????????????????????",e);
		}
		return result;
	}

	/**
	 * 1.??????????????????????????????????????????
	 * 2.???????????????
	 */
	@Override
	public void removeMultiLink(Long id) {
		//1.??????????????????????????????????????????
		LinkBo link = ldao.getById(id);
		List<LinkBo> list = ldao.getLinksByFromToId(link.getFrom(), link.getTo());
		
		//2.???????????????
		List<Long> ids = new ArrayList<Long>();
		for(LinkBo bo:list){
			ids.add(bo.getId());
		}
		if(ids.size() > 0) ldao.deleteByIds(ids);
	}
	
	/**
	 * 1.??????????????????
	 * 2.?????????????????????
	 */
	@Override
	public void selectMultiLinkByPage(Page<LinkBo, LinkBo> page)throws InstancelibException {
		//1.??????????????????(?????????????????????)
		ldao.selectMutilLinkByPage(page);
		List<LinkBo>  linkList = page.getDatas();
		
		//2.1 ??????node??????
		List<Long>  nodeIds = new ArrayList<Long>();
		List<Long>  linkResourceIds = new ArrayList<Long>();
		for(LinkBo link:linkList){
			nodeIds.add(link.getFrom());
			nodeIds.add(link.getTo());
			if(null != link.getInstanceId()) linkResourceIds.add(link.getInstanceId());	//????????????????????????
		}
		//????????????
		Set<Long> setTmp = new HashSet<Long>();
		setTmp.addAll(nodeIds);
		nodeIds.clear();
		nodeIds.addAll(setTmp);
		//??????node??????
		List<NodeBo> nodeList = ndao.getByIds(nodeIds);
		List<Long>  nodeInstaneIds = new ArrayList<Long>();
		for(NodeBo node:nodeList){
			if(null != node.getInstanceId()) nodeInstaneIds.add(node.getInstanceId());
		}
		//??????node??????????????????
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
		
		//2.2 ??????????????????
		List<ResourceInstance> linkInstances = rsvc.getResourceInstances(linkResourceIds);
		Map<Long, ResourceInstance> linkInstanceMap = new HashMap<Long, ResourceInstance>();
		for(ResourceInstance linkInstance:linkInstances){
			linkInstanceMap.put(linkInstance.getId(), linkInstance);
		}
		
		//2.3 ????????????(node?????????)
		for(LinkBo link:linkList){
			//node??????
			String defaultVal = "- -";
			ResourceInstance fromNodeInstance = nodeInstanceMap.get(link.getFrom());
			ResourceInstance toNodeInstance = nodeInstanceMap.get(link.getTo());
			boolean fromNodeCheck = (null!=fromNodeInstance);
			boolean toNodeCheck = (null!=toNodeInstance);
			link.setSrcMainInstName(fromNodeCheck?fromNodeInstance.getShowName():defaultVal);	//????????????
			link.setSrcMainInstIP(fromNodeCheck?fromNodeInstance.getShowIP():defaultVal);	//??????IP
			link.setSrcInstanceId(fromNodeCheck?fromNodeInstance.getId():0);					//?????????ID
			link.setDestMainInsName(toNodeCheck?toNodeInstance.getShowName():defaultVal);		//???????????????
			link.setDestMainInstIP(toNodeCheck?toNodeInstance.getShowIP():defaultVal);		//?????????IP
			link.setDestInstanceId(toNodeCheck?toNodeInstance.getId():0);						//?????????ID
			//????????????
			ResourceInstance linkInstance = linkInstanceMap.get(link.getInstanceId());
			if("link".equals(link.getType())){
				String defaultConstant = "- -";
				if(null != linkInstance){
					String srcSubInstId = resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_SRC_SUBINST_ID);		//?????????id
					logger.error("?????? linkInstanceId="+linkInstance.getId()+"  ,srcSubInstId="+srcSubInstId);
					ResourceInstance srcIfInstance = null;
					if(!defaultConstant.equals(srcSubInstId)){
						long srcIfIdT = Long.valueOf(srcSubInstId);	
						srcIfInstance = rsvc.getResourceInstance(srcIfIdT);
					}
					String destSubInstId = resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_DEST_SUBINST_ID);		//????????????id
					logger.error("?????? instanceId="+linkInstance.getId()+"  ,destSubInstId="+destSubInstId);
					ResourceInstance destIfInstance = null;
					if(!defaultConstant.equals(destSubInstId)){
						long destIfIdT = Long.valueOf(destSubInstId);
						destIfInstance = rsvc.getResourceInstance(destIfIdT);
					}
					String linkState = resourceExApi.convertLinkStateColor(linkInstance,srcIfInstance,destIfInstance);
					link.setInsStatus(linkState);//????????????
					link.setMonitorStatus(linkInstance.getLifeState().toString().toLowerCase());	//????????????
					link.setSrcIfName(resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_SRC_IFNAME));		//??????????????????
					link.setDestIfName(resourceExApi.getPropVal(linkInstance, LinkResourceConsts.PROP_DEST_IFNAME));	//?????????????????????
				}else{	//???????????????????????????????????????
					link.setSrcIfName(link.getFromIfIndex() !=null?link.getFromIfIndex().toString():"");
					link.setDestIfName(link.getToIfIndex()!=null?link.getToIfIndex().toString():"");
				}
			}else if("line".equals(link.getType())){
				link.setMonitorStatus("not_monitored");	//????????????
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
		//?????????????????????
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
		//??????
		List<LinkBo> lbs= ldao.getLinksByInstanceIds(new long[]{instanceId});
		if(!lbs.isEmpty()){
			LinkBo lb = lbs.get(0);
			retn.put("direction", lb.isDirection());
		}
		//?????????
		JSONObject ifAttr = tsvc.getLinkModuleProps(instanceId);
		//???????????????
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
		
		//?????????-??????
		long collResourceId = 0l;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_COLL_SUBINST_ID))){
			collResourceId = ifAttr.getLongValue(LinkResourceConsts.PROP_COLL_SUBINST_ID);	//????????????
		}
		long srcSubInstId = 0;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_SRC_SUBINST_ID))){
			srcSubInstId = ifAttr.getLongValue(LinkResourceConsts.PROP_SRC_SUBINST_ID);	//?????????
		}
		long destSubInstId=0l;
		if(StringUtils.isNotBlank(ifAttr.getString(LinkResourceConsts.PROP_DEST_SUBINST_ID))){
			destSubInstId = ifAttr.getLongValue(LinkResourceConsts.PROP_DEST_SUBINST_ID);	//????????????
		}
		retn.put("bandWidth",getBandWidth(collResourceId,"Mbps"));	//????????????????????????
//		retn.put("bandWidth",getBandWidth(instanceId,"Mbps"));	//??????????????????
		//?????????????????????????????????
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
		//????????????
		//?????????????????????
		JSONObject bandThreshold= tsvc.getThreshold(instanceId, MetricIdConsts.IF_IFBANDWIDTHUTIL);
		retn.put("instanceId", instanceId);
		retn.put("bandThreshold", bandThreshold);
		
		JSONObject profile = tsvc.getProfileIdByInstanceId(instanceId);	//??????id
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
			logger.error("??????????????????????????????????????????",e);
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
			//???????????????????????????
			JSONObject band = info.getJSONObject("band");
			JSONObject bwthresholdRetn = tsvc.refreshThreshold(instanceId, MetricIdConsts.IF_IFBANDWIDTHUTIL, band.getFloat("min"),band.getFloat("max"));
			//?????????????????????
			JSONObject flow = info.getJSONObject("flow");
			JSONObject tsthresholdRetn = tsvc.refreshThreshold(instanceId, MetricIdConsts.METRIC_IFOCTETSSPEED, flow.getFloat("min"),flow.getFloat("max"));
			//????????????
			Map<String, Boolean> alarmsMap = new HashMap<String, Boolean>();
			alarmsMap.put(MetricIdConsts.METRIC_AVAILABLE, info.getBoolean("availabilityAlarm"));
			alarmsMap.put(MetricIdConsts.IF_IFBANDWIDTHUTIL, band.getBoolean("alarm"));
			alarmsMap.put(MetricIdConsts.METRIC_IFOCTETSSPEED, flow.getBoolean("alarm"));
			JSONObject profile = tsvc.getProfileIdByInstanceId(instanceId);	//??????id
			Long profileId = profile.getLongValue("profileId");
			try {
				profileService.updateProfileMetricAlarm(profileId, alarmsMap);
			} catch (ProfilelibException e) {
				logger.error("??????????????????????????????????????????",e);
			}
			
			if(bwthresholdRetn.getInteger("state").equals(700)||tsthresholdRetn.getInteger("state").equals(700)){
				return false;
			}
			//?????????
			if(info.containsKey("srcSubInstId") && StringUtils.isNotBlank(info.getString("srcSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_SUBINST_ID, new String[]{info.getString("srcSubInstId")});
			}
			//????????????
			if(info.containsKey("destSubInstId")  && StringUtils.isNotBlank(info.getString("destSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_SUBINST_ID, new String[]{info.getString("destSubInstId")});
			}
			//????????????
			if(info.containsKey("collSubInstId") && StringUtils.isNotBlank(info.getString("collSubInstId"))){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_COLL_SUBINST_ID, new String[]{info.getString("collSubInstId")});
			}
			//???????????????
			if(info.containsKey("srcIfIndex")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_IFINDEX, new String[]{info.getString("srcIfIndex")});
			}
			//???????????????
			if(info.containsKey("srcIfName")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_SRC_IFNAME, new String[]{info.getString("srcIfName")});
			}
			//??????????????????
			if(info.containsKey("desIfIndex")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_IFINDEX, new String[]{info.getString("desIfIndex")});
			}
			//??????????????????
			if(info.containsKey("desIfName")){
				tsvc.refreshModuleProp(instanceId, LinkResourceConsts.PROP_DEST_IFNAME, new String[]{info.getString("desIfName")});
			}
			//?????????????????????????????????
			if(info.containsKey("srcIfIndex") || info.containsKey("desIfIndex") || info.containsKey("direction")){
				List<LinkBo> lbs = ldao.getLinksByInstanceIds(new long[]{instanceId});
				for(LinkBo lb : lbs){
					if(info.containsKey("direction")) lb.setDirection(info.getBooleanValue("direction"));
					if(info.containsKey("srcIfIndex")) lb.setFromIfIndex(info.getLong("srcIfIndex"));
					if(info.containsKey("desIfIndex")) lb.setToIfIndex(info.getLong("desIfIndex"));
				}
				ldao.update(lbs);
			}
			//??????
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
		//????????????->????????????????????????
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
		
		//??????????????????
		ldao.deleteByIds(ids);
	}
	@Override
	public JSONObject addMonitor(List<Long> ids) {
		JSONObject retn = new JSONObject();
		for(Long id : ids){
			LinkBo link = ldao.getById(id);
			if(null!=link){
//				logger.error("addMonitor()??????????????????linkBo="+JSONObject.toJSONString(link));
                JSONObject linkInfo = this.packageLinkInfo(link);
//                logger.error("-----??????????????????????????????"+JSONObject.toJSONString(linkInfo));
//				logger.error("?????????????????????????????????instance??????????????????addMonitor()???packageLinkInfo()????????????????????????linkInfo="+linkInfo.toJSONString());
                Long instanceId = tsvc.newLink(linkInfo);
//                    logger.error("------??????????????????????????????id???"+instanceId);
                if (null != instanceId) {
                    //??????????????????????????????id
                    link.setInstanceId(instanceId);
                    ldao.updateInstanceId(link);
                    retn.put(Long.toString(link.getId()), Long.toString(instanceId));
                }
            } else {
                logger.error("??????????????????????????????["+id+"]?????????");
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
//		logger.error("??????["+instanceId+"]???????????????????????????(NetInterface)?????????\n"+ifs.toJSONString());
        logger.error("????????????????????????????????????ID[" + instanceId + "]???????????????[" + ifIndex + "]?????????");
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
//			logger.error("------??????????????????:"+srcMainInstanceId+"_"+fromIfIndex);
            JSONObject srcInfo = this.getInstanceIf(srcMainInstanceId,fromIfIndex);
			if(null!=srcInfo){
				if(srcInfo.containsKey("instanceId")){
					srcIfInstanceId=srcInfo.getLong("instanceId");
				}else{
					logger.error("??????????????????????????????????????????ID["+srcMainInstanceId+"]?????????(??????)["+fromIfIndex+"]??????--->?????????????????????id(srcIfInstanceId)??????");
				}
			}else{
				logger.error("??????????????????????????????????????????ID["+srcMainInstanceId+"]??????-->??????????????????srcInfo??????");
			}
		}else{
			logger.error("????????????????????????fromNode??????ID["+fromNodeId+"]??????fromNode.getInstanceId()??????");
		}
        if (toNode != null && null != toNode.getInstanceId()) {
            desMainInstanceId = toNode.getInstanceId();
//            logger.error("------?????????????????????:"+desMainInstanceId+"_"+toIfIndex);
            JSONObject desInfo = getInstanceIf(desMainInstanceId,toIfIndex);
			if(null!=desInfo){
				if (desInfo.containsKey("instanceId")){
					desIfInstanceId=desInfo.getLong("instanceId");
				}else{
					logger.error("?????????????????????????????????????????????ID["+desMainInstanceId+"]?????????(??????)["+toIfIndex+"]??????--->????????????????????????id(desIfInstanceId)??????");
				}
			}else{
				logger.error("?????????????????????????????????????????????ID["+desMainInstanceId+"]??????-->?????????????????????desInfo??????");
			}
		}else{
			logger.error("????????????????????????toNode??????ID["+toNodeId+"]??????toNode.getInstanceId()??????");
		}
		if(srcMainInstanceId!=null){
			linkInfo.put("valueMainInstanceId",srcMainInstanceId);
		}else if(desMainInstanceId!=null){
//			logger.error("????????????????????????srcMainInstanceId?????????desMainInstanceId?????????");
            linkInfo.put("valueMainInstanceId",desMainInstanceId);
		}else{
			logger.error("????????????????????????srcMainInstanceId???desMainInstanceId??????");
		}
		if(srcIfInstanceId!=null){
			linkInfo.put("valueInstanceId",srcIfInstanceId);
		}else if(desIfInstanceId!=null){
//			logger.error("????????????????????????srcIfInstanceId?????????desIfInstanceId?????????");
            linkInfo.put("valueInstanceId",desIfInstanceId);
		}else{
			logger.error("????????????????????????srcIfInstanceId???desIfInstanceId??????");
		}
		//???????????????????????????????????????????????????????????????
		if(linkInfo.containsKey("valueMainInstanceId") && linkInfo.containsKey("valueInstanceId")){
			linkInfo.put("srcMainInstanceId", srcMainInstanceId);
			linkInfo.put("desMainInstanceId", desMainInstanceId);
			linkInfo.put("srcIfInstanceId", srcIfInstanceId);
			linkInfo.put("desIfInstanceId", desIfInstanceId);
			linkInfo.put("srcIfIndex",fromIfIndex );
			linkInfo.put("desIfIndex",toIfIndex);
			return linkInfo;
		}else{
			logger.error("packageLinkInfo?????????valueMainInstanceId???valueInstanceId?????????-->-->????????????????????????,return null-->??????newLink??????");
			return null;
		}
	}
	@Override
	public JSONObject updateAttr(LinkBo link) {
		JSONObject retn = new JSONObject();
		if (link.getId()==null || link.getId()<0l) {
			retn.put("state", 700);
			retn.put("msg", "????????????");
		}else{
			//????????????????????????????????????
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
					//?????????????????????????????????
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
				retn.put("msg", "????????????");
			}else{
				retn.put("state", 200);
				retn.put("msg", "????????????");
			}
		}
		return retn;
	}
	
	/**
	 * ?????????????????????????????????--
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
		this.setLinkTipDefault(collector);	//?????????????????????
		
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
			String subMainInstState = dataHelper.getInstanceState(subMainInstance.getId());	//??????????????????
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
		//?????????????????????????????????????????????????????????????????????
		JSONObject state = tsvc.getInstanceState(linkResInst.getId());
		String stateStr = state.getString("state");
		if(stateStr.equals(IconState.NODATA.getName()) || stateStr.equals(IconState.DISABLED.getName())){
			collector.put("bandRatio", DataHelper.NODATA_FLAG);
			collector.put("receiveSpeed", DataHelper.NODATA_FLAG);
			collector.put("sendSpeed", DataHelper.NODATA_FLAG);
			collector.put("receiveBandWidthRatio", DataHelper.NODATA_FLAG);
			collector.put("sendBandWidthRatio", DataHelper.NODATA_FLAG);
		}else{//?????????????????????????????????
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
		//??????
		JSONObject src = new JSONObject();
		//???????????????tip??????
		linkSubinstTip(src,linkInstance,true);
		retn.put("src", src);
		//?????????
		JSONObject des = new JSONObject();
		//??????????????????tip??????
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
				if(metricId.equals("device")){//?????????????????????
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
