package com.mainsteam.stm.topo.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.topo.api.FTMSMapService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.FTMSDao;
import com.mainsteam.stm.topo.dao.MapNodeDao;
@Service
public class FTMSMapServiceImpl implements FTMSMapService {
	@Autowired
	private FTMSDao dao;
	@Autowired
	private MapNodeDao mapNodeDao;
	@Autowired
	private DataHelper dhelper;
	private Map<Long,JSONObject> flowCache = new HashMap<Long,JSONObject>();
	private Map<Long,JSONObject> countCache = new HashMap<Long,JSONObject>();
	private Lock cacheLock = new ReentrantLock();
	public JSONObject tip(Long lineId,int level){
		JSONObject info = new JSONObject();
		if(lineId!=null){
			MapLineBo line = dao.getLineById(lineId);
			if(line!=null){
				info=packageLine(line,level);
				//格式化
				info.put("flow",dhelper.autoAdjustBandWidthUnit(info.getDoubleValue("flow")));
			}
		}
		return info;
	}
	public JSONObject flowList(final Long mapId,final int level){
		final JSONObject info = new JSONObject();
		dhelper.enableCache(5*60000);
		final CountDownLatch latch = new CountDownLatch(2);
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					info.put("flowNodes",calcLinesInMap(mapId,level));
				}catch(Exception e){
				}finally{
					latch.countDown();
				}
			}
		}).start();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					JSONObject tmp = calcNodeInMap(mapId);
					info.put("monitorCount",tmp.getInteger("monitorCount"));
					info.put("unavailable",tmp.getInteger("unavailable"));
					info.put("available",tmp.getInteger("available"));
					info.put("unavailableNodes",tmp.getJSONArray("unavailableNodes"));
				}catch(Exception e){
				}finally{
					latch.countDown();
				}
			}
		}).start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			info.put("monitorCount",0);
			info.put("unavailable",0);
			info.put("available",0);
			info.put("unavailableNodes",new JSONArray(0));
			info.put("flowNodes",new JSONArray(0));
		}
		dhelper.disableCache();
		return info;
	}
	private JSONObject packageLine(MapLineBo line,int level){
		JSONObject item = new JSONObject();
		Double flow = handleLine(line,level);
		item.put("flow", flow);
		item.put("id",line.getId());
		item.put("instanceId", line.getInstanceId());
		MapNodeBo fromNode=mapNodeDao.findByNodeIdAndLevel(line.getFromId(),level);
		MapNodeBo toNode=mapNodeDao.findByNodeIdAndLevel(line.getToId(),level);
		if(fromNode!=null && fromNode.getInstanceId()!=null){
			ResourceInstance ri = dhelper.getResourceInstance(fromNode.getInstanceId());
			item.put("srcName",dhelper.getResourceInstanceShowName(ri));
			item.put("srcIp",dhelper.getResourceInstanceManageIp(ri));
		}
		if(toNode!=null && toNode.getInstanceId()!=null){
			ResourceInstance ri = dhelper.getResourceInstance(toNode.getInstanceId());
			item.put("desName",dhelper.getResourceInstanceShowName(ri));
			item.put("desIp",dhelper.getResourceInstanceManageIp(ri));
		}
		item.put("ratio"," ");
		return item;
	}
	private JSONArray calcLinesInMap(Long mapId,int level){
		List<MapLineBo> lines = dao.getLinesInMap(mapId);
		JSONObject[] items = new JSONObject[lines.size()];
		int index=0;
		for(MapLineBo line : lines){
			items[index++]=packageLine(line,level);
		}
		sort(items);
		//取最前边的10个
		int end=items.length;
		if(end>10){
			end=10;
		}
		JSONObject[] nitems = (JSONObject[]) ArrayUtils.subarray(items, 0, end);
		//做一次自动单位调整
		for(JSONObject item : nitems){
			item.put("flow", dhelper.autoAdjustBandWidthUnit(item.getDouble("flow")));
		}
		return (JSONArray)JSON.toJSON(nitems);
	}
	//统计节点的可用性
	private JSONObject calcNodeInMap(Long mapId){
		if(countCache.containsKey(mapId)){
			refreshCache();
			return countCache.get(mapId);
		}else{
			return _calcNodeInMap(mapId);
		}
	}
	private JSONObject _calcNodeInMap(Long mapId) {
		JSONObject tmp = countCache.get(mapId);
		if(tmp!=null && tmp.getBooleanValue("status")){
			return tmp;
		}else{
			JSONObject retn = new JSONObject();
			int monitorCount=0,unavailable=0,available=0;
			JSONArray unavailableNodes = new JSONArray();
			List<MapNodeBo> nodes = mapNodeDao.getNodesByMapId(mapId);
			for(MapNodeBo node : nodes){
				Long nextMapId = node.getNextMapId();
				if(node.getInstanceId()!=null){
					ResourceInstance ri = dhelper.getResourceInstance(node.getInstanceId());
					if(ri!=null){
						//必须是监控状态
						if(ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							monitorCount++;
							InstanceStateEnum state = dhelper.getResourceInstanceAlarmInstanceStateEnum(node.getInstanceId());
							if(state.equals(InstanceStateEnum.CRITICAL) || state.equals(InstanceStateEnum.CRITICAL_NOTHING)){
								unavailable++;
								JSONObject unavailableNode = new JSONObject();
								unavailableNode.put("name", dhelper.getResourceInstanceShowName(ri));
								unavailableNode.put("ip", dhelper.getResourceInstanceManageIp(ri));
								unavailableNode.put("nextMapId",nextMapId);
								unavailableNodes.add(unavailableNode);
							}else{
								available++;
							}
						}
					}
				}
				if(nextMapId!=null && !nextMapId.equals(node.getMapid())){
					JSONObject nextInfo = _calcNodeInMap(nextMapId);
					available+=nextInfo.getIntValue("available");
					unavailable+=nextInfo.getIntValue("unavailable");
					monitorCount+=nextInfo.getIntValue("monitorCount");
					unavailableNodes.addAll(nextInfo.getJSONArray("unavailableNodes"));
				}
			}
			retn.put("monitorCount", monitorCount);
			retn.put("unavailable", unavailable);
			retn.put("unavailableNodes", unavailableNodes);
			retn.put("available", available);
			retn.put("status",true);
			countCache.put(mapId, retn);
			return retn;
		}
	}
	private void sort(JSONObject[] items){
		Arrays.sort(items, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject e1, JSONObject e2) {
				return e2.getDouble("flow").compareTo(e1.getDouble("flow"));
			}
		});
	}
	//计算Map总流量
	private Double calcFlowInMap(Long mapid,int level){
		Double flow=0d;
		if(flowCache.containsKey(mapid)){
			refreshCache();
			flow = flowCache.get(mapid).getDouble("flow");
		}else{
			flow = _calcFlowInMap(mapid,level);
		}
		return flow;
	}
	private double handleLine(MapLineBo line,int level){
		int nextLevel=level+1;
		double flow=0d;
		if(line.getInstanceId()!=null){
			ResourceInstance re = dhelper.getResourceInstance(line.getInstanceId());
			if(re!=null){
				Long valueInstId = dhelper.getLinkValueInstanceId(re);
				if(valueInstId!=null){
					flow = dhelper.getPortTotalFlow(valueInstId);
				}
			}
		}else{
			//默认先处理to节点
			MapNodeBo tnode = mapNodeDao.findByNodeIdAndLevel(line.getToId(), level);
			
			if(tnode!=null && tnode.getNextMapId()!=null){
				Long mapid=tnode.getNextMapId();
				flow += calcFlowInMap(mapid, nextLevel);
			}
			MapNodeBo fnode = mapNodeDao.findByNodeIdAndLevel(line.getFromId(), level);
			if(fnode!=null && fnode.getNextMapId()!=null){
				Long mapid=fnode.getNextMapId();
				flow += calcFlowInMap(mapid, nextLevel);
			}
		}
		return flow;
	}
	private Double _calcFlowInMap(Long mapId,int level){
		if(level>4) return 0d;
		JSONObject tmp = flowCache.get(mapId);
		//如果本次已经计算过了，就不需要再计算
		if(tmp!=null && tmp.getBooleanValue("status")){
			return tmp.getDouble("flow");
		}else{
			Double flow=0d;
			List<MapLineBo> lines = dao.getLinesInMap(mapId);
			for(MapLineBo line : lines){
				flow += handleLine(line,level);
			}
			JSONObject flowObject = new JSONObject();
			flowObject.put("flow", flow);
			flowObject.put("level",level);
			//保存到缓存中
			flowCache.put(mapId, flowObject);
			return flow;
		}
	}
	private void resetCache(){
		for(Map.Entry<Long, JSONObject> entry : countCache.entrySet()){
			entry.getValue().put("status", false);
		}
		for(Map.Entry<Long, JSONObject> entry : flowCache.entrySet()){
			entry.getValue().put("status", false);
		}
	}
	private void refreshCache(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				if(cacheLock.tryLock()){
					try {
						resetCache();
						for(Map.Entry<Long,JSONObject> entry : flowCache.entrySet()){
							JSONObject flowObject = entry.getValue();
							_calcFlowInMap(entry.getKey(),flowObject.getInteger("level"));
						}
						for(Map.Entry<Long,JSONObject> entry : countCache.entrySet()){
							_calcNodeInMap(entry.getKey());
						}
						//最大频率10分钟刷新一次
						TimeUnit.SECONDS.sleep(10);
					} catch (Exception e) {
					} finally{
						cacheLock.unlock();
					}
				}
			}
			
		}).start();
	}
}
