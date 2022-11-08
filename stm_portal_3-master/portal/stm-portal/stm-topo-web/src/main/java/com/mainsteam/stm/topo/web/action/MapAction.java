package com.mainsteam.stm.topo.web.action;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.MapGraphService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.enums.TopoType;
import com.mainsteam.stm.topo.util.TopoHelper;

@Controller
@RequestMapping(value="topo/map/graph")
public class MapAction {
	private Logger logger = Logger.getLogger(MapAction.class);
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Autowired
	private MapGraphService mapGraphService;
	@ResponseBody
	@RequestMapping("get")
	public JSONObject getMap(Long id){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){//必须包含编辑权限
			JSONObject data = mapGraphService.getMap(id);
			retn.put("data",data);
			retn.put("status",200);
		}else{
			retn.put("status",700);
			retn.put("msg","没查看权限");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("flowlist")
	public JSONObject getFlowListForMap(Long mapid){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){
				JSONObject info = mapGraphService.getFlowListForMap(mapid);
				retn.put("info",info);
				retn.put("status",200);
			}else{
				throw new RuntimeException("没查看权限");
			}
		} catch (Exception e) {
			logger.error(e);
			retn.put("status",700);
			retn.put("msg", e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("levelInfo")
	public JSONObject getMapLevelInfo(Long mapid,int level){
		JSONObject retn = new JSONObject();
		TopoHelper.beginLog("MapAction.getMapLevelInfo");
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){
				if(level==1){//全国的健康度
					final JSONObject tmpRetn = new JSONObject();
					tmpRetn.put("level",1);
					final CountDownLatch task = new CountDownLatch(4);
					for(int i=level;i<=4;i++){
						final int index = i;
						new Thread(new Runnable() {
							public void run() {
								tmpRetn.put("level"+index,mapGraphService.getWholeCountryLevelInfo(index));
								task.countDown();
							}
						}).start();
					}
					task.await();
					TopoHelper.mixin(retn, tmpRetn);
				}else if(level==2){//省份的健康度
					retn  = mapGraphService.getProvinceLevelInfo(mapid);
					retn.put("level",2);
				}else if(level==3){//市的网络健康度
					retn = mapGraphService.getCityLevelInfo(mapid);
					retn.put("level",3);
				}
				retn.put("status",200);
			}else{
				throw new RuntimeException("没查看权限");
			}
		} catch (Exception e) {
			logger.error(e);
			retn.put("status",700);
			retn.put("msg", e.getMessage());
		}
		logger.error("level="+level);
		TopoHelper.endLog("MapAction.getMapLevelInfo");
		return retn;
	}
	@ResponseBody
	@RequestMapping("refreshState")
	public JSONArray refreshState(Long[] nodeInstIds,Long[] linkInstIds,String linkMetricId,String nodeMetricId){
		JSONArray items = new JSONArray();
		items.addAll(mapGraphService.refreshNodeState(nodeInstIds,nodeMetricId));
		items.addAll(mapGraphService.refreshLinkState(linkInstIds,linkMetricId));
		return items;
	}
	
	@ResponseBody
	@RequestMapping("getTopoMapLineInfo")
	public JSONObject getTopoMapLineInfo(Long id){
		
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){//必须包含编辑权限
			retn.put("status",200);
			if(id == 0){
				id++;
			}
			retn.put("data",mapGraphService.getMapLine(id));
		}else{
			retn.put("status",700);
			retn.put("msg","没查看权限");
		}
		return retn;
	}
	
}
