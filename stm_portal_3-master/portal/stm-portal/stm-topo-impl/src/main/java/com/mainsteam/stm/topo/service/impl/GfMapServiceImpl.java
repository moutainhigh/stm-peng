package com.mainsteam.stm.topo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.GfMapService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.GfMapDao;
import com.mainsteam.stm.topo.dao.MapLineDao;
import com.mainsteam.stm.topo.dao.MapNodeDao;
import com.mainsteam.stm.topo.enums.IconState;
import com.mainsteam.stm.topo.util.TopoHelper;
@Service
public class GfMapServiceImpl implements GfMapService {
	private Logger logger = Logger.getLogger(GfMapServiceImpl.class);
	@Autowired
	private MapNodeDao mapNodeDao;
	@Autowired
	private GfMapDao gfmapDao;
	@Autowired
	private MapLineDao lineDao;
	@Autowired
	private MapNodeDao nodeDao;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ResourceInstanceService resourceService;
	//缓存
	private ConcurrentMap<Long,JSONObject> cache = new ConcurrentHashMap<Long,JSONObject>();
	//缓存锁
	private Lock cacheLock = new ReentrantLock();
	@Autowired
	private ThirdService thirdService;
	private JSONObject packageUnavailableNodes(ResourceInstance ri,MapNodeBo node){
		JSONObject unavailableNode = new JSONObject();
		unavailableNode.put("name", dataHelper.getResourceInstanceShowName(ri));
		unavailableNode.put("ip", dataHelper.getResourceInstanceManageIp(ri));
		unavailableNode.put("nextMapId",node.getNextMapId());
		unavailableNode.put("level",node.getLevel());
		return unavailableNode;
	}
	@Override
	public JSONObject whole() {
		//刷新缓存
		refresh();
		//不可用节点信息
		final ConcurrentSkipListSet<Long> ids = new ConcurrentSkipListSet<Long>();
		final JSONArray unavailableNodes = new JSONArray();
		JSONObject retn  = new JSONObject();
		final AtomicInteger[][] levelInfo = new AtomicInteger[4][2];
		for(int i=0;i<4;i++){
			for(int j=0;j<2;j++){
				levelInfo[i][j]=new AtomicInteger(0);
			}
		}
		int windowSize=50;
		List<MapNodeBo> nodes = gfmapDao.getAllInstanceNodes();
		ExecutorService executor = Executors.newFixedThreadPool(20);
		int totalSize = nodes.size();
		int size = totalSize/windowSize;
		if(totalSize%windowSize>0){
			size+=1;
		}
		for(int i=0;i<size;i++){
			int begin = i*windowSize;
			int end = begin+windowSize;
			if(end>totalSize){
				end = totalSize;
			}
			
			final List<MapNodeBo> tmpNodes = nodes.subList(begin, end);
			executor.execute(new Runnable(){
				@Override
				public void run() {
					for(MapNodeBo node : tmpNodes){
						//去重第四级县级节点
						if(ids.contains(node.getInstanceId())){
							continue;
						}
						ids.add(node.getInstanceId());
						Integer fyjb = node.getFyjb();
						//总数增加
						levelInfo[fyjb-1][0].incrementAndGet();
						JSONObject state = null;
						if(cache.containsKey(node.getInstanceId())){
							state = cache.get(node.getInstanceId());
						}else{
							state = thirdService.getInstanceState(node.getInstanceId());
							cache.put(node.getInstanceId(), state);
						}
						if(state!=null && state.containsKey("state")){
							if(!isCritical(state)){
								//可用增加
								levelInfo[fyjb-1][1].incrementAndGet();
							}else{
								unavailableNodes.add(packageUnavailableNodes(dataHelper.getResourceInstance(node.getInstanceId()),node));
							}
						}						
						//判断是否为县级节点
						JSONObject nodeAttr=node.getAttrJson();
						if(nodeAttr.containsKey("country")){
							JSONObject country = nodeAttr.getJSONObject("country");
							JSONArray idsJson = country.getJSONArray("ids");
							for(Object idt :idsJson){
								Long id = new Long((int) idt);
								if(ids.contains(id)){
									continue;
								}else{
									ids.add(id);
									JSONObject tmpState = null;
									if(cache.containsKey(id)){
										tmpState=cache.get(id);
									}else{
										tmpState = thirdService.getInstanceState(id);
										cache.put(id, tmpState);
									}
									levelInfo[3][0].incrementAndGet();
									if(tmpState.containsKey("state")){
										if(!isCritical(tmpState)){
											//可用增加
											levelInfo[3][1].incrementAndGet();
										}else{
											MapNodeBo tmpNode = new MapNodeBo();
											tmpNode.setLevel(4);
											tmpNode.setNextMapId(null);
											unavailableNodes.add(packageUnavailableNodes(dataHelper.getResourceInstance(id),tmpNode));
										}
									}
								}
							}
						}
					}
				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			logger.error("GfMapServiceImpl.whole",e);
		}
		int totalAmount=0,totalAvailable=0;
		for(int i=0;i<4;i++){
			JSONObject tmp = new JSONObject();
			totalAmount+=levelInfo[i][0].get();
			totalAvailable+=levelInfo[i][1].get();
			tmp.put("amount", levelInfo[i][0].get());
			tmp.put("available", levelInfo[i][1].get());
			retn.put("level"+(i+1), tmp);
		}
		retn.put("totalAmount", totalAmount);
		retn.put("totalAvailable", totalAvailable);
		retn.put("unavailableNodes", unavailableNodes);
		return retn;
	}
	//刷新缓存
	private void refresh() {
		//一次只能刷新一次
		new Thread(new Runnable(){
			@Override
			public void run() {
				if(cacheLock.tryLock()){
					for(Map.Entry<Long, JSONObject> entry : cache.entrySet()){
						Long instId = entry.getKey();
						JSONObject state=thirdService.getInstanceState(instId);
						cache.put(instId, state);
					}
					cacheLock.unlock();
				}
			}
		},"cache_refresh").start();
		
	}
	private boolean isCritical(JSONObject state){
		if(state.containsKey("state")){
			String sta = state.getString("state");
			if(sta.equalsIgnoreCase(InstanceStateEnum.CRITICAL.name()) || sta.equalsIgnoreCase(InstanceStateEnum.CRITICAL_NOTHING.name())){
				return true;
			}
		}
		return false;
	}
	@Override
	public JSONObject province(Long mapid) {
		JSONObject province = new JSONObject();
		JSONArray unavailableNodes = new JSONArray();
		int[][] levelInfo = new int[3][2];
		for(int i=0;i<3;i++){
			for(int j=0;j<2;j++){
				levelInfo[i][j]=0;
			}
		}
		//去重
		Set<Long> ids = new HashSet<Long>();
		//获取省下边的全部节点
		List<MapNodeBo> nodes = new ArrayList<MapNodeBo>();
		List<MapNodeBo> provinceNodes = mapNodeDao.getNodesByMapId(mapid);
		for(MapNodeBo pnode : provinceNodes){
			//检查是否是县节点,是的话统计县的数量，否则当省节点处理
			if(!ids.contains(pnode.getInstanceId())){
				ids.add(pnode.getInstanceId());
				nodes.add(pnode);
				if(!checkCountry(pnode,levelInfo,ids,2,unavailableNodes)){
					Long nextMapId = pnode.getNextMapId();
					//进入市节点
					List<MapNodeBo> cityNodes = mapNodeDao.getNodesByMapId(nextMapId);
					for(MapNodeBo cnode : cityNodes){
						ids.add(cnode.getInstanceId());
						nodes.add(cnode);
						checkCountry(cnode,levelInfo,ids,2,unavailableNodes);
					}
				}
			}
		}
		for(MapNodeBo node : nodes){
			Long instId = node.getInstanceId();
			int fyjb = node.getFyjb();
			if(instId!=null && fyjb>1){
				ids.add(instId);
				levelInfo[fyjb-2][0]++;
				JSONObject state=null;
				if(cache.containsKey(instId)){
					state = cache.get(instId);
				}else{
					state = thirdService.getInstanceState(instId);
					cache.put(instId, state);
				}
				if(state.containsKey("state")){
					if(!isCritical(state)){
						//可用增加
						levelInfo[fyjb-2][1]++;
					}else{
						unavailableNodes.add(packageUnavailableNodes(dataHelper.getResourceInstance(instId), node));
					}
				}
			}
		}
		int totalAmount=0,totalAvailable=0;
		for(int i=0;i<3;i++){
			JSONObject le=new JSONObject();
			totalAmount+=levelInfo[i][0];
			totalAvailable+=levelInfo[i][1];
			le.put("amount",levelInfo[i][0]);
			le.put("available",levelInfo[i][1]);
			province.put("level"+(i+2), le);
		}
		province.put("level",2);
		province.put("totalAmount", totalAmount);
		province.put("totalAvailable", totalAvailable);
		province.put("unavailableNodes",unavailableNodes);
		return province;
	}
	/**
	 * 统计县级的信息
	 * @param node 地图节点
	 * @param levelInfo 统计矩阵变量
	 * @param ids InstanceId去重标记
	 * @param index 县级的脚标位置
	 * @return 是否为县级节点
	 */
	private boolean checkCountry(MapNodeBo node, int[][] levelInfo,Set<Long> ids,int index,JSONArray collector) {
		JSONObject attr = node.getAttrJson();
		if(attr.containsKey("country")){
			JSONObject country = attr.getJSONObject("country");
			JSONArray idsJson = country.getJSONArray("ids");
			for(Object idt :idsJson){
				Long id = new Long((int) idt);
				if(ids.contains(id)){
					continue;
				}else{
					ids.add(id);
					JSONObject tmpState = null;
					if(cache.containsKey(id)){
						tmpState=cache.get(id);
					}else{
						tmpState = thirdService.getInstanceState(id);
						cache.put(id, tmpState);
					}
					levelInfo[index][0]++;
					if(tmpState.containsKey("state")){
						if(!isCritical(tmpState)){
							//可用增加
							levelInfo[index][1]++;
						}
					}else{
						MapNodeBo tmpNode = new MapNodeBo();
						tmpNode.setNextMapId(null);
						tmpNode.setLevel(4);
						collector.add(packageUnavailableNodes(dataHelper.getResourceInstance(id), tmpNode));
					}
				}
			}
			return true;
		}
		return false;
	}
	@Override
	public JSONObject city(Long mapid) {
		JSONArray unavailableNodes = new JSONArray();
		int[][] levelInfo = new int[2][2];
		//初始化
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				levelInfo[i][j]=0;
			}
		}
		JSONObject city = new JSONObject();
		//去重集合
		Set<Long> ids = new HashSet<Long>();
		for(MapNodeBo node : mapNodeDao.getNodesByMapId(mapid)){
			Long instId = node.getInstanceId();
			if(!ids.contains(instId)){
				ids.add(instId);
				int fyjb=node.getFyjb();
				if(fyjb>2){
					ids.add(instId);
					levelInfo[fyjb-3][0]++;
					JSONObject state=null;
					if(cache.containsKey(instId)){
						state = cache.get(instId);
					}else{
						state = thirdService.getInstanceState(instId);
						cache.put(instId, state);
					}
					if(state.containsKey("state")){
						if(!isCritical(state)){
							//可用增加
							levelInfo[fyjb-3][1]++;
						}
					}else{
						unavailableNodes.add(packageUnavailableNodes(dataHelper.getResourceInstance(instId), node));
					}
				}
				checkCountry(node, levelInfo, ids, 1,unavailableNodes);
			}
		}
		
		city.put("level",3);
		int totalAmount=0,totalAvailable=0;
		for(int i=0;i<2;i++){
			JSONObject le=new JSONObject();
			totalAmount+=levelInfo[i][0];
			totalAvailable+=levelInfo[i][1];
			le.put("amount",levelInfo[i][0]);
			le.put("available",levelInfo[i][1]);
			city.put("level"+(i+3), le);
		}
		city.put("totalAmount", totalAmount);
		city.put("totalAvailable", totalAvailable);
		city.put("unavailableNodes", unavailableNodes);
		return city;
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
						String flowData = flow.replaceAll("[MKB]bps", "");
						Double flowRawData = Double.parseDouble(flowData);
						if(flow.contains("M")){
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
		retn.put("flowNodes", flowNodes);
		return retn;
	}
}
