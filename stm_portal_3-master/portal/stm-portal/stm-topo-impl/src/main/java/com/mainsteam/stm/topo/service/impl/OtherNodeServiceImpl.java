package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.topo.api.ITopoGraphApi;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.OtherNodeService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.IOthersNodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.enums.IconState;
import com.mainsteam.stm.topo.enums.SettingKey;
import com.mainsteam.stm.topo.util.TopoHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class OtherNodeServiceImpl implements OtherNodeService {
	private Logger logger = Logger.getLogger(OtherNodeServiceImpl.class);
	@Autowired
	private IOthersNodeDao otherNodeDao;
	@Autowired
	private INodeDao nodeDao;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ISettingDao settingDao;
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService resourceService;
	@Autowired
	private ThirdService thirdSvc;
	@Autowired
	private ITopoGraphApi topoGraphApi;
	@Autowired
	private LinkService linkService;

	@Override
	public JSONArray getOtherState(Long subTopoId, String linkMetricId,String nodeMetricId) {
		JSONArray otherStateResults = new JSONArray();	//???????????????
		Map<Long, String> subtopoNodeStateMap = new HashMap<Long, String>();	//?????????????????????????????????????????????????????????
		JSONObject subTopHierarchy = new JSONObject();	//?????????????????????
		subTopHierarchy.put("startId", subTopoId);
		
		//1.????????????
		this.getOtherAlarmState(subTopoId, linkMetricId, nodeMetricId, subtopoNodeStateMap, otherStateResults,subTopHierarchy);
		//2.?????????????????????
		this.packageOtherState(otherStateResults,subtopoNodeStateMap, subTopHierarchy);
		return otherStateResults;
	}

	/**
	 * ?????????????????????
	 * @param otherSJtateResults
	 * @param subtopoNodeStateMap
	 * @param subTopHierarchy
	 */
	private void packageOtherState(JSONArray otherStateResults,Map<Long, String> subtopoNodeStateMap,JSONObject subTopHierarchy){
		for(Object o:otherStateResults){
			JSONObject tmp = (JSONObject) JSONObject.toJSON(o);
			tmp.put("state", subtopoNodeStateMap.get(tmp.getLong("subTopoId")));
			if(!tmp.getString("state").equals(IconState.DANGER.name().toLowerCase())){
				this.setChildState(subTopHierarchy,tmp);
			}
		}
	}
	
	private void setChildState(JSONObject parent,JSONObject other){
		JSONArray children = parent.getJSONArray("children");
		if(null != children && children.size() > 0){
			for(Object o:children){
				JSONObject tmp = (JSONObject) JSONObject.toJSON(o);
				if(tmp.getLongValue("id") == other.getLongValue("subTopoId") || (tmp.containsKey("startId") && tmp.getLongValue("startId") == other.getLongValue("subTopoId"))){
					String status = tmp.toJSONString();
					if(status.contains(IconState.DANGER.name().toLowerCase())){
						other.put("state", IconState.DANGER.name().toLowerCase());
					}else if(status.contains(IconState.SEVERITY.name().toLowerCase())){
						other.put("state", IconState.SEVERITY.name().toLowerCase());
					}else if(status.contains(IconState.WARNING.name().toLowerCase())){
						other.put("state", IconState.WARNING.name().toLowerCase());
					}
					break;
				}else{
					this.setChildState(tmp, other);
				}
			}
		}
	}
	
	/**
	 * ??????other???????????????
	 * @param subTopoId
	 * @param linkMetricId
	 * @param nodeMetricId
	 * @param subtopoNodeStateMap
	 * @param otherStateResults
	 * @param subTopHierarchy
	 */
	private void getOtherAlarmState(Long subTopoId, String linkMetricId,String nodeMetricId,Map<Long, String> subtopoNodeStateMap,JSONArray otherStateResults,JSONObject subTopHierarchy){
		//1.??????subTopoId???????????????other??????????????????????????????
		List<SettingBo> others  = settingDao.getOtherSetting(subTopoId);
		if(null == others || others.size() == 0) return;
		List<Map<String, Long>> otherSutopoMapList = this.getOtherSubtopo(others);	//[{otherNodeId:subTopoId}]
		if(otherSutopoMapList.size() == 0) return;
		//???????????????????????????,	?????????????????????others
		if(otherStateResults.size() == 0) this.initOtherStateResults(subTopoId, otherStateResults, otherSutopoMapList);
		
		JSONArray children = new JSONArray();
		subTopHierarchy.put("children", children);
		//2.?????????????????????????????????+??????+??????????????????
		for(Map<String, Long> map:otherSutopoMapList){
			JSONObject child = new JSONObject();
			child.put("id",map.get("subTopoId"));
			children.add(child);
			this.getSubTopoAlarmState(linkMetricId, nodeMetricId,subtopoNodeStateMap, otherStateResults, map,child);
		}
	}

	/**
	 * ?????????other?????????
	 * @param subTopoId
	 * @param otherStateResults
	 * @param otherSutopoMapList
	 */
	private void initOtherStateResults(Long subTopoId,JSONArray otherStateResults,List<Map<String, Long>> otherSutopoMapList) {
		for(Map<String, Long> map:otherSutopoMapList){
			JSONObject state = new JSONObject();	//{otherNodeId:state} ????????????????????????????????????????????????
			state.put("subTopoId", map.get("subTopoId"));
			state.put("otherNodeId",map.get("otherNodeId"));
			state.put("state",IconState.NORMAL.name().toLowerCase());	//??????normal
			otherStateResults.add(state);
		}
	}
	
	/**
	 * ???????????????????????????
	 * @param linkMetricId
	 * @param nodeMetricId
	 * @param subtopoNodeStateMap
	 * @param otherStateResults
	 * @param map
	 * @param subTopHierarchy
	 */
	private void getSubTopoAlarmState(String linkMetricId, String nodeMetricId,Map<Long, String> subtopoNodeStateMap,JSONArray otherStateResults, Map<String, Long> map,JSONObject subTopHierarchy) {
		Long subId = map.get("subTopoId");		//??????????????????????????????????????????
		if(!subtopoNodeStateMap.containsKey(subId)){	//?????????????????????????????????
			String subtopoInfoStr = topoGraphApi.getSubTopo(subId);
			JSONObject subtopoJson = JSONObject.parseObject(subtopoInfoStr);
			
			//3.?????????????????????????????????????????????????????????????????????
			JSONArray nodes = subtopoJson.getJSONArray("nodes");	//???????????????????????????
			String nodeState = IconState.NORMAL.name().toLowerCase();
			if(null != nodes && nodes.size() > 0){
				Long[] nodeIds = new Long[nodes.size()];
				for(int i=0;i<nodes.size();i++){
					JSONObject nodeJson = (JSONObject) JSONObject.toJSON(nodes.get(i));
					nodeIds[i] = nodeJson.getLong("id");
				}
				JSONArray nodesState = linkService.getNodeStates(nodeIds,nodeMetricId);
				nodeState = this.getMaxState(nodesState .toJSONString());
				subtopoNodeStateMap.put(subId, nodeState);	//??????????????????????????????????????????????????????
				subTopHierarchy.put("state", subtopoNodeStateMap.get(subId));
			}
			if(IconState.DANGER.name().toLowerCase().equals(nodeState)) return;
			
			//4.?????????????????????????????????????????????????????????????????????
			JSONArray links = subtopoJson.getJSONArray("links");	//???????????????????????????
			String linkState = IconState.NORMAL.name().toLowerCase();
			if(null != links && links.size() > 0){
				Long[] linkIds = new Long[links.size()];
				for(int i=0;i<links.size();i++){
					JSONObject nodeJson = (JSONObject) JSONObject.toJSON(links.get(i));
					linkIds[i] = nodeJson.getLong("id");
				}
				JSONArray linksState = linkService.convertLinkState(linkIds, linkMetricId);
				linkState = this.getMaxState(linksState .toJSONString());
			}
			if(linkState.equals(IconState.DANGER.name().toLowerCase())){
				subtopoNodeStateMap.put(subId, linkState);	//??????????????????????????????????????????????????????
				subTopHierarchy.put("state", subtopoNodeStateMap.get(subId));
				return;
			}else if(nodeState.equals(IconState.SEVERITY.name().toLowerCase()) || linkState.equals(IconState.SEVERITY.name().toLowerCase())){
				subtopoNodeStateMap.put(subId, IconState.SEVERITY.name().toLowerCase());
			}else if(nodeState.equals(IconState.WARNING.name().toLowerCase()) || linkState.equals(IconState.WARNING.name().toLowerCase())){
				subtopoNodeStateMap.put(subId, IconState.WARNING.name().toLowerCase());
			}else{
				subtopoNodeStateMap.put(subId, IconState.NORMAL.name().toLowerCase());
			}
			subTopHierarchy.put("state", subtopoNodeStateMap.get(subId));
			//5.???????????????????????????????????????????????????????????????
			this.getOtherAlarmState(subId, linkMetricId, nodeMetricId,subtopoNodeStateMap,otherStateResults,subTopHierarchy	);
		}
	}

	/**
	 * ??????????????????????????????????????????
	 * @param others
	 * @return [{otherNodeId ??? subTopoId}]
	 */
	private List<Map<String, Long>> getOtherSubtopo(List<SettingBo> others) {
		List<Map<String, Long>> otherSutopoMapList = new ArrayList<Map<String, Long>>();
		for(SettingBo setting:others){
			JSONObject valueJson = (JSONObject) JSONObject.parse(setting.getValue());
			if(StringUtils.isNotBlank(valueJson.getString("map"))){
				Map<String, Long> map = new HashMap<String,Long>();	//{otherNodeId ??? subTopoId}
				map.put("otherNodeId", Long.valueOf(setting.getKey().replace("other", "")));
				map.put("subTopoId", valueJson.getLong("map"));
				otherSutopoMapList.add(map);
			}
		}
		return otherSutopoMapList;
	}

	/**
	 * ??????????????????????????????
	 * @param stateStr
	 * @return
	 */
	private String getMaxState(String stateStr) {
		String state = IconState.NORMAL.name().toLowerCase();
		if(stateStr.contains(InstanceStateEnum.NORMAL_CRITICAL.name()) || stateStr.contains(InstanceStateEnum.CRITICAL.name()) || stateStr.contains(InstanceStateEnum.CRITICAL_NOTHING.name())){
			state = IconState.DANGER.name().toLowerCase();	//??????2
		}else if(stateStr.contains(InstanceStateEnum.SERIOUS.name())){
			state = IconState.SEVERITY.name().toLowerCase();//??????4
		}else if(stateStr.contains(InstanceStateEnum.WARN.name())){
			state = IconState.WARNING.name().toLowerCase();	//??????1
		}
		return state;
	}
	
	@Override
	public JSONObject updateAttr(OtherNodeBo otherNode) {
		JSONObject retn = new JSONObject();
		if(otherNode.getId()!=null){
			otherNodeDao.updateAttr(otherNode);
			retn.put("state", 200);
			retn.put("msg", "????????????");
		}else{
			retn.put("state", 700);
			retn.put("msg", "????????????");
		}
		return retn;
	}
	@Override
	public JSONObject getById(Long id) {
		JSONObject retn = new JSONObject();
		if(id!=null){
			OtherNodeBo nb = otherNodeDao.getById(id);
			if(nb!=null){
				return (JSONObject)JSON.toJSON(nb);
			}
		}
		return retn;
	}
	
	@Override
	public JSONObject cabinetDeviceList(Long id) throws InstancelibException {
		JSONObject retn = new JSONObject();
		//1.????????????????????????
		if(id!=null){
			OtherNodeBo nb = otherNodeDao.getById(id);
			if(nb!=null){
				retn.put("status",200);
				JSONObject attr = JSON.parseObject(nb.getAttr());
				//???????????????????????????ids
				if(attr.containsKey("rows")){	
					JSONArray ids = attr.getJSONArray("rows");
					List<Long> tmpIds = new ArrayList<Long>(ids.size());
                    for (int i = 0; i < ids.size(); i++) {
                        tmpIds.add(ids.getLong(i));
                    }
                    //2.????????????
					JSONArray rows = this.convertCabinetDevice(tmpIds);
					retn.put("rows", rows);
				}
			}
		}
		return retn;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * @throws InstancelibException 
	 */
	private JSONArray convertCabinetDevice(List<Long> instanceIds) throws InstancelibException{
		//??????cpu??????????????????
		JSONArray metrics = dataHelper.getResourceRealTimeVal(instanceIds.toArray(new Long[0]), new String[]{MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE});
		Map<Long,JSONObject> metricsMap = new HashMap<Long,JSONObject>();
		for(Object o:metrics){
			JSONObject metric = (JSONObject)o;
			JSONObject tmp = new JSONObject();
			tmp.put("cpuRate",metric.get(MetricIdConsts.METRIC_CPU_RATE));
			tmp.put("ramRate",metric.get(MetricIdConsts.METRIC_MEME_RATE));
			metricsMap.put(metric.getLong("instanceId"), tmp);
		}
		//????????????????????????
		List<ResourceInstance> instances = dataHelper.getResourceInstances(instanceIds);
		JSONArray rows = new JSONArray();
		//??????????????????????????????
		List<Map<String, String>> catergorys = thirdSvc.getAllCategory();
		for(ResourceInstance instance:instances){
			JSONObject tmp = new JSONObject();
			tmp.put("id", instance.getId());
			tmp.put("instanceId",instance.getId());
			tmp.put("ip", instance.getShowIP());
			String showName = instance.getShowName();
			if(StringUtils.isBlank(instance.getShowName())) showName = instance.getName();
			tmp.put("showName",StringUtils.isBlank(showName)?DataHelper.NODATA_FLAG:showName);
			//???????????????????????????????????????--bug#21331??????
			if(instance.getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED)){
				tmp.put("state",DataHelper.NODATA_FLAG);
			}else{
				tmp.put("state",dataHelper.getResourceInstanceAlarmInstanceStateEnum(instance.getId()));
			}
			if(metricsMap.containsKey(instance.getId())){
				JSONObject states = metricsMap.get(instance.getId());
				tmp.put("cpuRate",states.get("cpuRate"));
				tmp.put("ramRate",states.get("ramRate"));
			}
			tmp.put("typeName",instance.getCategoryId());
			this.convertCabinetDeviceOtherVal(catergorys, tmp);
			rows.add(tmp);
		}
		
		return rows;
	}
	
	/**
	 * ????????????????????????????????????
	 * @param cabinetDevice
	 */
	private void convertCabinetDeviceOtherVal(List<Map<String, String>> categorys,JSONObject cabinetDevice){
		//1.????????????
		Map<String,String> categoryMap = thirdSvc.getDeviceType(categorys,cabinetDevice.getString("typeName"));
		
		boolean isNet = false;
		//{"id": "Router","name": "?????????","pid": "NetworkDevice","type": "????????????"}
		String pid = DataHelper.NODATA_FLAG;
		if(categoryMap.containsKey("pid")){
			pid = categoryMap.get("pid");
		}
		String type = DataHelper.NODATA_FLAG;
		if(categoryMap.containsKey("type")){
			type = categoryMap.get("type");
		}else{
			type = cabinetDevice.getString("typeName");
		}
		if(pid.equals("NetworkDevice")){	//?????????????????????name
			cabinetDevice.put("typeName",categoryMap.get("name").toString());
		}else{
			cabinetDevice.put("typeName",type);
		}
		//2.??????????????????
		if(pid.equals("NetworkDevice")){
			isNet = true;
		}
		cabinetDevice.put("isNet",isNet);
	}
	
	@Override
	public JSONObject addCabinet(OtherNodeBo ob) {
		JSONObject retn = new JSONObject();
		//??????????????????cabinet
		JSONObject attr = ob.parseAttr(JSONObject.class);
		if(attr.containsKey("text")){
			if(otherNodeDao.isCabinetRepeatName(attr.getString("text"),ob.getSubTopoId())>0){
				retn.put("status", 700);
				retn.put("msg", "????????????????????????");
			}else{
				otherNodeDao.save(ob);
                JSONArray rows = attr.getJSONArray("rows");
                Long[] longs = new Long[rows.size()];
                for (int i = 0; i < rows.size(); i++) {
                    longs[i] = rows.getLong(i);
                }
                updateCabinetDeviceCount(new Long[]{}, longs);
                retn.put("status", 200);
                retn.put("msg","????????????!");
				retn.put("data",JSON.toJSON(ob));
			}
		}
		return retn;
	}
	public void updateCabinetDeviceCount(Long[] oldIds,Long[] nowIds){
		SettingBo sp = settingDao.getCfg(SettingKey.TOPO_ROOM_CABINET_IDS.getKey());
		if(sp==null){
			sp = new SettingBo();
			sp.setValue("{}");
			sp.setKey(SettingKey.TOPO_ROOM_CABINET_IDS.getKey());
		}else if(sp.getValue()==null){
			sp.setValue("{}");
		}
		JSONObject idJson = JSON.parseObject(sp.getValue());
		Map<String,Integer> nowMap = TopoHelper.idToMap(nowIds);
		//?????????????????????????????????
		Map<String,Integer> toRemoveIds = new HashMap<String,Integer>(10);
		for(Long id:oldIds){
			if(!nowMap.containsKey(id.toString()) && idJson.containsKey(id.toString())){
				toRemoveIds.put(id.toString(),1);
			}
		}
		for(Long id:nowIds){
			if(!idJson.containsKey(id.toString())){
				idJson.put(id.toString(),"1");
			}
		}
		JSONObject newJson=new JSONObject(10);
		//??????????????????
		for(String key:idJson.keySet()){
			if(!toRemoveIds.containsKey(key)){
				newJson.put(key, idJson.get(key));
			}
		}
		sp.setValue(newJson.toJSONString());
		if(sp.getId()==null){
			settingDao.save(sp);
		}else{
			settingDao.updateCfg(sp);
		}
	}
	@Override
	public JSONObject updateCabinet(OtherNodeBo ob) {
		JSONObject retn = new JSONObject();
		Assert.notNull(ob.getId());
		OtherNodeBo nodeBo = otherNodeDao.getById(ob.getId());
		Assert.notNull(nodeBo);
		JSONObject attr = ob.parseAttr(JSONObject.class);
		JSONObject dbAttr = nodeBo.parseAttr(JSONObject.class);
		if(attr.containsKey("text")){
			String name = attr.getString("text");
			String dbName=dbAttr.getString("text");
			int count = otherNodeDao.isCabinetRepeatName(attr.getString("text"),ob.getSubTopoId());
            if (dbName != null && !dbName.equals(name) && count > 0) {
                retn.put("status", 700);
				retn.put("msg", "????????????????????????");
			}else{
                Long[] dbIds = new Long[]{};
                if(dbAttr.containsKey("rows")){
                    JSONArray rows = dbAttr.getJSONArray("rows");
                    dbIds = new Long[rows.size()];
                    for (int i = 0; i < rows.size(); i++) {
                        dbIds[i] = rows.getLong(i);
                    }
                }
                Long[] nids = new Long[]{};
                if(attr.containsKey("rows")){
                    JSONArray rows = attr.getJSONArray("rows");
                    nids = new Long[rows.size()];
                    for (int i = 0; i < rows.size(); i++) {
                        nids[i] = rows.getLong(i);
                    }
                }
                updateCabinetDeviceCount(dbIds, nids);
                otherNodeDao.updateAttr(ob);
				retn.put("status", 200);
				retn.put("msg", "???????????????");
			}
		}
		return retn;
	}
	@Override
	public JSONObject removeCabinet(Long id) {
		JSONObject retn = new JSONObject();
		try {
			Assert.notNull(id);
			OtherNodeBo ob = otherNodeDao.getById(id);
			Assert.notNull(ob);
			JSONObject attrJson = ob.parseAttr(JSONObject.class);
			if(attrJson.containsKey("rows")){
				JSONArray rows = attrJson.getJSONArray("rows");
                Long[] ids = new Long[rows.size()];
                for (int i = 0; i < rows.size(); i++) {
                    ids[i] = rows.getLong(i);
                }
                updateCabinetDeviceCount(ids, new Long[]{});
                otherNodeDao.deleteByIds(Arrays.asList(id));
			}
			retn.put("status",200);
			retn.put("msg","????????????!");
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
	/**
	 * ?????????????????????
	 */
	@Override
	public JSONObject getCabinetsState(Long[] ids) {
		JSONObject retn = new JSONObject();
		List<OtherNodeBo> cabinets = otherNodeDao.getByIds(ids);
		for(OtherNodeBo cab : cabinets){
			JSONObject state = getCabinetState(cab);
			retn.put(cab.getId().toString(), state);
		}
		return retn;
	}
	private JSONObject getCabinetState(OtherNodeBo cab) {
		JSONObject attr = cab.parseAttr(JSONObject.class);
		InstanceStateEnum mostImportantState=InstanceStateEnum.NORMAL_NOTHING;
		Long mostImportantInstId=null;
		if(attr.containsKey("rows")){
			for(Object obj : attr.getJSONArray("rows")){
				Long instId = new Long((Integer)obj);
				InstanceStateEnum state = dataHelper.getResourceInstanceAlarmInstanceStateEnum(instId);
				if(!state.equals(InstanceStateEnum.NOT_MONITORED) && mostImportantState.getStateVal()<state.getStateVal()){
					mostImportantState=state;
					mostImportantInstId=instId;
				}
			}
		}
		ResourceInstance ri = dataHelper.getResourceInstance(mostImportantInstId);
		String msg = DataHelper.NODATA_FLAG;
		if(mostImportantInstId!=null){
			String tmp = dataHelper.getLatestMsgAlarmEventForInstance(mostImportantInstId);
			if(!tmp.equals(DataHelper.NODATA_FLAG)){
				String ipOrShowName = dataHelper.getResourceInstanceManageIp(ri);
				if(StringUtils.isBlank(ipOrShowName)){
					ipOrShowName=dataHelper.getResourceInstanceShowName(ri);
				}
				if(StringUtils.isBlank(ipOrShowName)){
					msg=String.format("%s",tmp);
				}else{
					msg=String.format("???%s???%s",ipOrShowName,tmp);
				}
				
			}
		}
		JSONObject retn = new JSONObject();
		retn.put("msg",msg);
		retn.put("state", mostImportantState.name());
		return retn;
	}
}
