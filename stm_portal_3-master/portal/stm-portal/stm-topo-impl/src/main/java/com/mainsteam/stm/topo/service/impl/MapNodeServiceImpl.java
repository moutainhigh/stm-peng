package com.mainsteam.stm.topo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.MapNodeService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.MapNodeDao;
import com.mainsteam.stm.topo.util.TopoHelper;
@Service
public class MapNodeServiceImpl implements MapNodeService{
	@Autowired
	private MapNodeDao mapNodeDao;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ThirdService thirdService;
	@Override
	public MapNodeBo relateResourceInstance(MapNodeBo node,Integer level) {
		MapNodeBo dbnode = mapNodeDao.findByNodeIdAndLevel(node.getNodeid(),level);
		Long instId = node.getInstanceId();
		ResourceInstance inst = dataHelper.getResourceInstance(instId);
		if(null!=dbnode){
			dbnode.setInstanceId(node.getInstanceId());
			String ip = dataHelper.getResourceInstanceManageIp(inst);
			dbnode.setIp(ip);
			mapNodeDao.update(dbnode);
			return dbnode;
		}else{
			mapNodeDao.add(node);
			String ip = dataHelper.getResourceInstanceManageIp(inst);
			node.setIp(ip);
			return node;
		}
	}
	@Override
	public void cancelRelateResourceInstance(String nodeid, Long mapid,Integer level) {
		MapNodeBo dbnode = mapNodeDao.findByNodeIdAndLevel(nodeid,level);
		if(null!=dbnode && dbnode.getMapid().equals(mapid)){
			mapNodeDao.removeByNodeId(nodeid);
		}else{
			throw new RuntimeException(String.format("不存在相关图元%s",nodeid));
		}
	}
	@Override
	public JSONObject updateAttr(String attr, Long id) {
		MapNodeBo node = mapNodeDao.findById(id);
		if(null!=node){
			JSONObject attrJson = JSON.parseObject(attr);
			JSONObject dbAttrJson = node.getAttrJson();
			TopoHelper.mixin(dbAttrJson, attrJson);
			node.setAttr(dbAttrJson.toJSONString());
			mapNodeDao.update(node);
		}
		return (JSONObject) JSON.toJSON(node);
	}
	@Override
	public void updateNextMapIdAndLevel(String nodestr) {
		JSONArray nodes = JSON.parseArray(nodestr);
		for(Object tmp:nodes){
			JSONObject node  = (JSONObject)tmp;
			mapNodeDao.updateNextMapIdAndLevel(node.getLong("id"),node.getLong("nextMapId"),node.getInteger("level"));
		}
	}
	@Override
	public JSONArray getCountryList(Long key,Integer level) {
		TopoHelper.beginLog("MapNodeService.getCountryList");
		TopoHelper.beginLog("MapNodeService.getCountryByKey");
		MapNodeBo node = mapNodeDao.getCountryByKey(key,level);
		TopoHelper.endLog("MapNodeService.getCountryByKey");
		JSONArray retn = new JSONArray();
		if(node!=null){
			JSONObject json = node.getAttrJson();
			JSONObject country = json.getJSONObject("country");
			Long coreInstanceId = country.getLong("coreInstanceId");
			ResourceInstance coreInst = dataHelper.getResourceInstance(coreInstanceId);
			String srcName = dataHelper.getResourceInstanceShowName(coreInst);
			JSONObject state = thirdService.getInstanceState(coreInstanceId);
			String srcState = state.getString("state");
			for(Object tmp:country.getJSONArray("ids")){
				Long instId = new Long((Integer) tmp);
				if(!coreInstanceId.equals(instId)){
					JSONObject item = new JSONObject();
					item.put("srcName", srcName);
					item.put("srcState", srcState);
					ResourceInstance inst = dataHelper.getResourceInstance(instId);
					item.put("desName", dataHelper.getResourceInstanceShowName(inst));
					JSONObject tstate = thirdService.getInstanceState(instId);
					item.put("desState", tstate.getString("state"));
					retn.add(item);
				}
			}
		}
		TopoHelper.endLog("MapNodeService.getCountryList");
		return retn;
	}
}
