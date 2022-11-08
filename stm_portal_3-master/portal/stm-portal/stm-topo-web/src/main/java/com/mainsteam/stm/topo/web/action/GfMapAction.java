package com.mainsteam.stm.topo.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.GfMapService;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.enums.TopoType;
import com.mainsteam.stm.topo.util.TopoHelper;

/**
 * 地图拓扑，专为高法而生
 * gf==高法 特此说明
 */
@Controller
@RequestMapping("topo/gf")
public class GfMapAction {
	@Autowired
	private GfMapService mapService;
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@ResponseBody
	@RequestMapping("flowlist")
	public JSONObject getFlowListForMap(Long mapid){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){
				JSONObject info = mapService.getFlowListForMap(mapid);
				retn.put("info",info);
				retn.put("status",200);
			}else{
				throw new RuntimeException("没查看权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("levelInfo")
	public JSONObject getMapLevelInfo(Long mapid,int level){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){
				JSONObject info=null;
				switch(level){
				case 1://全国地图统计
					info = mapService.whole();
					break;
				case 2://省地图统计
					info = mapService.province(mapid);
					break; 
				case 3://市地图统计
					info = mapService.city(mapid);
					break;
				}
				retn.put("status", 200);
				retn.put("level", level);
				TopoHelper.mixin(retn, info);
				return retn;
			}else{
				throw new RuntimeException("没查看权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
		}
		return retn;
	}
}
