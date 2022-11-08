package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.MapGraphService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.MapLineDao;
import com.mainsteam.stm.topo.dao.MapNodeDao;
import com.mainsteam.stm.topo.util.TopoHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MapGraphServiceImpl implements MapGraphService{
	private Logger logger = Logger.getLogger(MapGraphServiceImpl.class);
	@Autowired
	private MapLineDao lineDao;
	@Autowired
	private MapNodeDao nodeDao;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ThirdService thirdService;
	@Autowired
	private LinkService linkService;
	
	@Override
	public JSONObject getMap(Long id) {
		TopoHelper.beginLog("MapGraphServiceImpl.getMap");
		JSONObject retn = new JSONObject();
		List<MapLineBo> lines = lineDao.getLines(id);
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(id);
		//找出ip
		for(MapNodeBo node : nodes){
			if(null!=node.getInstanceId()){
				node.setIp(dataHelper.getResourceInstanceManageIp(dataHelper.getResourceInstance(node.getInstanceId())));
			}
		}
		retn.put("lines",JSON.toJSON(lines));
		retn.put("nodes",JSON.toJSON(nodes));
		TopoHelper.beginLog("MapGraphServiceImpl.getMap");
		return retn;
	}
	
	@Override
	public JSONObject getMapLine(Long id) {
		List<MapLineBo> lines = lineDao.getLines(id);
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(id);
		JSONObject result = new JSONObject();
		JSONArray items = new JSONArray();
		JSONArray nodeItems = new JSONArray();
		
		if(lines == null && nodes == null){
			return result;
		}
		
		Set<String> lineNodeIds = new HashSet<String>();
		for(MapLineBo link : lines){
			JSONObject state = new JSONObject();
			if(link.getInstanceId() == null || link.getInstanceId() <= 0){
				continue;
			}
			ResourceInstance linkRe=dataHelper.getResourceInstance(link.getInstanceId());
			if(linkRe==null) continue;
			if(!InstanceLifeStateEnum.NOT_MONITORED.equals(linkRe.getLifeState())){
				state = new JSONObject();
				InstanceStateEnum tstate = linkService.getLinkInstState(link.getInstanceId(),"device");
				state.put("state",tstate.name());
			}else{
				state=new JSONObject();
				state.put("state", InstanceLifeStateEnum.NOT_MONITORED.name());
			}
			state.put("linkid", link.getInstanceId());
			MapNodeBo node = new MapNodeBo();
			node.setNodeid(link.getFromId());
			node.setMapid(link.getMapid());
			MapNodeBo nodeResult = nodeDao.findAreaKeyByNodeIdAndMapId(node);
			JSONObject fromPoint = null;
			if(nodeResult == null || nodeResult.getInstanceId() == null){
				fromPoint = new JSONObject();
				fromPoint.put("state", "NORMAL");
				fromPoint.put("name", "");
				fromPoint.put("instanceid", null);
			}else{
				lineNodeIds.add(link.getFromId());
				fromPoint = thirdService.getInstanceState(nodeResult.getInstanceId());
				fromPoint.put("name", getAreaName(nodeResult.getNextMapId()));
				fromPoint.put("instanceid", nodeResult.getInstanceId());
			}
			fromPoint.put("lon", link.getFromId().split("_")[0]);
			fromPoint.put("lat", link.getFromId().split("_")[1]);
			MapNodeBo toNode = new MapNodeBo();
			toNode.setNodeid(link.getToId());
			toNode.setMapid(link.getMapid());
			nodeResult = nodeDao.findAreaKeyByNodeIdAndMapId(toNode);
			JSONObject toPoint = null;
			if(nodeResult == null || nodeResult.getInstanceId() == null){
				toPoint = new JSONObject();
				toPoint.put("state", "NORMAL");
				toPoint.put("name", "");
				toPoint.put("instanceid", null);
			}else{
				lineNodeIds.add(link.getToId());
				toPoint = thirdService.getInstanceState(nodeResult.getInstanceId());
				toPoint.put("name", getAreaName(nodeResult.getNextMapId()));
				toPoint.put("instanceid", nodeResult.getInstanceId());
			}
			toPoint.put("lon", link.getToId().split("_")[0]);
			toPoint.put("lat", link.getToId().split("_")[1]);
			state.put("from", fromPoint);
			state.put("to", toPoint);
			items.add(state);
		}
		
		for(MapNodeBo node : nodes){
			if(lineNodeIds.contains(node.getNodeid())){
				continue;
			}
			JSONObject point = null;
			point = thirdService.getInstanceState(node.getInstanceId());
			point.put("name", getAreaName(node.getNextMapId()));
			point.put("instanceid", node.getInstanceId());
			point.put("lon", node.getNodeid().split("_")[0]);
			point.put("lat", node.getNodeid().split("_")[1]);
			
			nodeItems.add(point);
		}
		
		result.put("line", items);
		result.put("node", nodeItems);
		
		return result;
	}
	
	//通过区域ID获取区域名称
	private String getAreaName(long mapId){
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String areaContent = null;
		JSONObject json = null;
		try {
			is = this.getClass().getResourceAsStream("areakey.json");
			isr = new InputStreamReader(is,"UTF-8");
			br = new BufferedReader(isr);
			areaContent = br.readLine();
			json = JSONObject.parseObject(areaContent);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally{
			try {
				if(br != null){
					br.close();
				}
				if(isr != null){
					isr.close();
				}
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return json.getString(mapId + "");
	}
	
	//是否可达
	private boolean isAvailable(String state){
		if(state.equals(InstanceStateEnum.CRITICAL.name()) || state.equals(InstanceStateEnum.CRITICAL_NOTHING.name())){
			return false;
		}else{
			return true;
		}
	}
	@SuppressWarnings("finally")
	public JSONObject countMap(Long mapid){
		JSONObject retn = new JSONObject();
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(mapid);
		final AtomicLong available=new AtomicLong(0),unavailable=new AtomicLong(0),monitorCount=new AtomicLong(0);
		//不可用节点信息
		final JSONArray unavailableNodes = new JSONArray();
		final CountDownLatch latch = new CountDownLatch(nodes.size());
		for(MapNodeBo node : nodes){
			if(node.getInstanceId()!=null){
				ResourceInstance inst = dataHelper.getResourceInstance(node.getInstanceId());
				if(inst!=null){
					monitorCount.getAndIncrement();
					JSONObject state = thirdService.getInstanceState(node.getInstanceId());
					if(state.containsKey("state")){
						if(!isAvailable(state.getString("state"))){
							JSONObject unavailableNode = new JSONObject();
							unavailableNode.put("name", dataHelper.getResourceInstanceShowName(inst));
							unavailableNode.put("ip", dataHelper.getResourceInstanceManageIp(inst));
							unavailableNode.put("nextMapId",node.getNextMapId());
							unavailableNode.put("level",node.getLevel());
							unavailableNodes.add(unavailableNode);
							unavailable.getAndIncrement();
						}else{
							available.getAndIncrement();
						}
					}
				}else{
					logger.error(String.format("MapGraphServiceImpl.countMap discard one instance(%s)",node.getInstanceId()));
				}
			}
			//县级节点
			JSONObject attr = node.getAttrJson();
			if(attr.containsKey("country")){
				List<Long> instIds = new ArrayList<Long>();
				JSONObject country = attr.getJSONObject("country");
				for(Object tmp : country.getJSONArray("ids")){
					instIds.add(new Long(tmp.toString()));
				}
				List<ResourceInstance> instances=dataHelper.getResourceInstances(instIds);
				for(ResourceInstance inst : instances){
					monitorCount.getAndIncrement();
					JSONObject state = thirdService.getInstanceState(inst.getId());
					if(state.containsKey("state")){
						if(!isAvailable(state.getString("state"))){
							JSONObject unavailableNode = new JSONObject();
							unavailableNode.put("name", dataHelper.getResourceInstanceShowName(inst));
							unavailableNode.put("ip", dataHelper.getResourceInstanceManageIp(inst));
							unavailableNode.put("nextMapId",node.getNextMapId());
							unavailableNodes.add(unavailableNode);
							unavailable.getAndIncrement();
						}else{
							available.getAndIncrement();
						}
					}else{
						logger.error(String.format("MapGraphServiceImpl.countMap discard one instance(%s) no state",inst.getId()));
					}
				}
				latch.countDown();
			}else{
				//统计下一个节点
				final Long nextMapId = node.getNextMapId();
				if(nextMapId!=null){
					new Thread(new Runnable() {
						public void run() {
							JSONObject tmpCount = countMap(nextMapId);
							monitorCount.addAndGet(tmpCount.getLongValue("monitorCount"));
							available.addAndGet(tmpCount.getLongValue("available"));
							unavailable.addAndGet(tmpCount.getLongValue("unavailable"));
							synchronized (unavailableNodes) {
								unavailableNodes.addAll(tmpCount.getJSONArray("unavailableNodes"));
							}
							latch.countDown();
						}
					}).start();
				}else{
					latch.countDown();
				}
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error(e);
		}finally{
			retn.put("unavailableNodes", unavailableNodes);
			retn.put("monitorCount", monitorCount);
			retn.put("available", available);
			retn.put("unavailable", unavailable);
			return retn;
		}
			
	}
	@Override
	public JSONObject getFlowListForMap(Long mapid) {
		TopoHelper.beginLog("MapGraphServiceImpl.getFlowListForMap");
		JSONObject retn = new JSONObject();
		List<MapLineBo> lines = lineDao.getLines(mapid);
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(mapid);
		//暂存nodeid-inst的关系
		Map<String,ResourceInstance> tmpMap = new HashMap<String,ResourceInstance>();
		//统计可用和不可用数量
		TopoHelper.beginLog("MapGraphServiceImpl.getFlowListForMap.countUnavailable");
		for(MapNodeBo node : nodes){
			if(node.getInstanceId()!=null){
				ResourceInstance inst = dataHelper.getResourceInstance(node.getInstanceId());
				tmpMap.put(node.getNodeid(),inst);
			}
		}
		TopoHelper.endLog("MapGraphServiceImpl.getFlowListForMap.countUnavailable");
		//统计链路流量
		//流量节点信息
		TopoHelper.beginLog("MapGraphServiceImpl.getFlowListForMap.flowInfo");
		Map<Double,List<JSONObject>> _flowNodes = new HashMap<Double,List<JSONObject>>(lines.size());
		JSONArray nodata = new JSONArray();
		for(MapLineBo line : lines){
			if(line.getInstanceId()!=null){
				String fromId = line.getFromId();
				String toId = line.getToId();
				JSONObject flowNode = new JSONObject();
				flowNode.put("instanceId",line.getInstanceId());
				ResourceInstance linkInst = dataHelper.getResourceInstance(line.getInstanceId());
				Long valueInstId = dataHelper.getLinkValueInstanceId(linkInst);
				ResourceInstance valueInst = dataHelper.getResourceInstance(valueInstId);
				if(tmpMap.containsKey(fromId)){
					ResourceInstance fromIst = tmpMap.get(fromId);
					flowNode.put("srcName",dataHelper.getResourceInstanceShowName(fromIst));
				}else{
					flowNode.put("srcName",dataHelper.getResourceInstanceShowName(valueInst));
				}
				if(tmpMap.containsKey(toId)){
					ResourceInstance toIst = tmpMap.get(toId);
					flowNode.put("desName",dataHelper.getResourceInstanceShowName(toIst));
				}else{
					flowNode.put("desName",dataHelper.getResourceInstanceShowName(valueInst));
				}
				if(null!=valueInstId){
					flowNode.put("flow",dataHelper.getPortTotalFlow(valueInstId,"Mbps"));
					flowNode.put("ratio",dataHelper.getPortBandWidthRatio(valueInstId));
				}
				if(flowNode.containsKey("flow")){
					String flow = flowNode.getString("flow");
					if(flow.equals(DataHelper.NODATA_FLAG)){
						nodata.add(flowNode);
					}else{
                        /*BUG #43366 离线demo:地图拓扑流量TOP10无数据显示 huangping 2017/8/9 start*/
//						String flowData = flow.replaceAll("[MKB]bps", "");
                        String flowData = flow.replaceAll("(,|[MKB]bps)", "");
                        /*BUG #43366 离线demo:地图拓扑流量TOP10无数据显示 huangping 2017/8/9 start*/
						Double flowRawData = Double.parseDouble(flowData);
                        if (flow.contains("M")) {
                            flowRawData*=(1024*1024);
						}
						if(flow.contains("K")){
							flowRawData*=(1024);
						}
						if(!_flowNodes.containsKey(flowRawData)){
							_flowNodes.put(flowRawData,new ArrayList<JSONObject>());
						}
						_flowNodes.get(flowRawData).add(flowNode);
					}
				}else{
					nodata.add(flowNode);
				}
			}
		}
		TopoHelper.endLog("MapGraphServiceImpl.getFlowListForMap.flowInfo");
		//排序，只要十个
		TopoHelper.beginLog("MapGraphServiceImpl.getFlowListForMap.sort");
		JSONArray flowNodes = new JSONArray();
		Double[] flows = _flowNodes.keySet().toArray(new Double[0]);
		Arrays.sort(flows);
		for(int i=flows.length-1,j=0;j<10 && i>=0;i--){
			for(JSONObject node : _flowNodes.get(flows[i])){
				flowNodes.add(node);
				j++;
			}
		}
		if(flowNodes.size()<10){
			int i = flowNodes.size();
			for(int j=0;i<=10 && j<nodata.size();i++,j++){
				flowNodes.add(nodata.get(j));
			}
		}
		JSONObject tmpCount = countMap(mapid);
		TopoHelper.endLog("MapGraphServiceImpl.getFlowListForMap.sort");
		retn.put("flowNodes", flowNodes);
		TopoHelper.mixin(retn, tmpCount);
		TopoHelper.endLog("MapGraphServiceImpl.getFlowListForMap");
		return retn;
	}
	@Override
	public JSONObject getWholeCountryLevelInfo(int level) {
		JSONObject retn = new JSONObject();
		List<Long> ids = new ArrayList<Long>();
		if(level<4){
			ids = nodeDao.instanceIdInLevel(level);
		}else if(level==4){
			ids = nodeDao.instanceIdInCountry();
		}
		long amount=0,available=0;
		List<ResourceInstance> instances=dataHelper.getResourceInstances(ids);
		amount=ids.size();
		for(ResourceInstance inst : instances){
			JSONObject state = thirdService.getInstanceState(inst.getId());
			if(state.containsKey("state")){
				if(isAvailable(state.getString("state"))){
					available++;
				}
			}
		}
		retn.put("amount",amount);
		retn.put("available", available);
		return retn;
	}
	private Map<String,Long> levelInfo(List<Long> instIds){
		Map<String,Long> retn = new HashMap<String,Long>();
		retn.put("amount", new Long(instIds.size()));
		long available=0;
		List<ResourceInstance> instances=dataHelper.getResourceInstances(instIds);
		for(ResourceInstance inst : instances){
			JSONObject state = thirdService.getInstanceState(inst.getId());
			if(state.containsKey("state")){
				if(isAvailable(state.getString("state"))){
					available++;
				}
			}
		}
		retn.put("available", available);
		return retn;
	}
	//获取县的level信息
	private Map<String,Long> getCountryLevelInfo(Long mapid){
		TopoHelper.beginLog("MapGraphServiceImpl.getCountryLevelInfo");
		TopoHelper.beginLog("MapGraphServiceImpl.getCountryByKey");
		MapNodeBo node = nodeDao.getCountryByKey(mapid,3);
		TopoHelper.endLog("MapGraphServiceImpl.getCountryByKey");
		List<Long> instIds = new ArrayList<Long>();
		JSONObject attr = node.getAttrJson();
		if(attr.containsKey("country")){
			JSONObject country = attr.getJSONObject("country");
			for(Object tmp : country.getJSONArray("ids")){
				instIds.add(new Long(tmp.toString()));
			}
		}
		TopoHelper.endLog("MapGraphServiceImpl.getCountryLevelInfo");
		return levelInfo(instIds);
	}
	@Override
	public JSONObject getCityLevelInfo(Long mapid) {
		TopoHelper.beginLog("MapGraphServiceImpl.getCityLevelInfo");
		JSONObject retn = new JSONObject();
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(mapid);
		retn.put("level3",statisticMapInfo(nodes));
		Long countryAmount=0l,countryAvailable=0l;
		for(MapNodeBo nb : nodes){
			//统计县级-乡镇
			if(nb.getNextMapId()!=null){
				Map<String,Long> tmp = getCountryLevelInfo(nb.getNextMapId());
				countryAmount+=tmp.get("amount");
				countryAvailable+=tmp.get("available");
			}
		}
		JSONObject level4=new JSONObject();
		level4.put("amount", countryAmount);
		level4.put("available", countryAvailable);
		retn.put("level4",level4);
		TopoHelper.endLog("MapGraphServiceImpl.getCityLevelInfo");
		return retn;
	}
	private JSONObject statisticMapInfo(List<MapNodeBo> nodes){
		JSONObject retn = new JSONObject();
		//统计本地图
		List<Long> instIds = new ArrayList<Long>();
		for(MapNodeBo nb : nodes){
			if(nb.getInstanceId()!=null){
				instIds.add(nb.getInstanceId());
			}
		}
		Map<String,Long> info = levelInfo(instIds);
		retn.put("amount",info.get("amount"));
		retn.put("available",info.get("available"));
		return retn;
	}
	@SuppressWarnings("finally")
	@Override
	public JSONObject getProvinceLevelInfo(final Long mapid) {
		TopoHelper.beginLog("MapGraphServiceImpl.getProvinceLevelInfo");
		JSONObject retn = new JSONObject();
		List<MapNodeBo> nodes = nodeDao.getNodesByMapId(mapid);
		//统计市
		final AtomicLong cityAmount=new AtomicLong(0l),cityAvailable=new AtomicLong(0l);
		final AtomicLong countryAmount=new AtomicLong(0l),countryAvailable=new AtomicLong(0l);
		final CountDownLatch task = new CountDownLatch(nodes.size());
		for(final MapNodeBo nb : nodes){
			if(nb.getAttrJson().containsKey("country")){
				JSONObject cityInfo = getCityLevelInfo(mapid);
				JSONObject level2 = cityInfo.getJSONObject("level3");
				JSONObject level3 = cityInfo.getJSONObject("level4");
				cityInfo.put("level2",level2);
				cityInfo.put("level3",level3);
				JSONObject level4 = new JSONObject();
				level4.put("amount",0);
				level4.put("available",0);
				cityInfo.put("level4",level4);
				return cityInfo;
			}else if(nb.getNextMapId()!=null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						//如果是直辖市-按市处理
						List<MapNodeBo> cityNodes = nodeDao.getNodesByMapId(nb.getNextMapId());
						JSONObject cityInfo = statisticMapInfo(cityNodes);
						cityAmount.addAndGet(cityInfo.getLong("amount"));
						cityAvailable.addAndGet(cityInfo.getLong("available"));
						for(MapNodeBo city : cityNodes){
							if(city.getNextMapId()!=null){
								Map<String,Long> countryInfo = getCountryLevelInfo(city.getNextMapId());
								countryAmount.addAndGet(countryInfo.get("amount"));
								countryAvailable.addAndGet(countryInfo.get("available"));
							}
						}
						task.countDown();
					}
				}).start();
			}
		}
		try {
			task.await();
		} catch (InterruptedException e) {
			logger.error(e);
		}finally{
			retn.put("level2",statisticMapInfo(nodes));
			JSONObject level3=new JSONObject();
			level3.put("amount", cityAmount);
			level3.put("available", cityAvailable);
			retn.put("level3",level3);
			JSONObject level4=new JSONObject();
			level4.put("amount", countryAmount);
			level4.put("available", countryAvailable);
			retn.put("level4",level4);
			TopoHelper.endLog("MapGraphServiceImpl.getProvinceLevelInfo");
			return retn;
		}
	}
	@Override
	public JSONArray refreshLinkState(Long[] linkInstIds, String metricId) {
		JSONArray items = new JSONArray();
		metricId=dataHelper.mapToCapacityConst(metricId);
		for(Long instId : linkInstIds){
			ResourceInstance linkRe=dataHelper.getResourceInstance(instId);
			if(linkRe==null) continue;
			JSONObject state = new JSONObject();
			if(!InstanceLifeStateEnum.NOT_MONITORED.equals(linkRe.getLifeState())){
				if(metricId.equals("device")){
					state = new JSONObject();
					InstanceStateEnum tstate = linkService.getLinkInstState(instId,metricId);
					state.put("state",tstate.name());
				}else{
					state = thirdService.getMetricState(instId, metricId,true);
				}
			}else{
				state=new JSONObject();
				state.put("state", InstanceLifeStateEnum.NOT_MONITORED.name());
			}
			state.put("instanceId", instId);
			items.add(state);
		}
		return items;
	}
	@Override
	public JSONArray refreshNodeState(Long[] nodeInstIds, String metricId) {
		metricId=dataHelper.mapToCapacityConst(metricId);
		JSONArray items = new JSONArray();
		for(Long instId : nodeInstIds){
			JSONObject state = null;
			if(metricId.equals("device")){//如果是设备状态
				state = thirdService.getInstanceState(instId);
			}else{
				state = thirdService.getMetricState(instId, metricId,false);
			}
			state.put("instanceId", instId);
			items.add(state);
		}
		return items;
	}
	
}
