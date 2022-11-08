package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.MaplineService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.enums.TopoType;

@Controller
@RequestMapping(value="topo/map/line")
public class MapLineAction {
	Logger logger = Logger.getLogger(MapLineAction.class);
	@Autowired
	private MaplineService mapLineService;
	@Autowired
	private ITopoAuthSettingApi authSvc;
	/**
	 * 添加地图连线
	 * @param line
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="addLine",method=RequestMethod.POST)
	public JSONObject addLine(MapLineBo line){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				mapLineService.addLine(line);
				retn.put("status",200);
				retn.put("msg", "添加成功");
				retn.put("line", JSON.toJSON(line));
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error(e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="remove",method=RequestMethod.POST)
	public JSONObject remove(Long id){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				mapLineService.remove(id);
				retn.put("status",200);
				retn.put("msg", "删除成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error(e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="convertToLink",method=RequestMethod.POST)
	public JSONObject convertToLink(String linkInfo){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				JSONObject linkInfoJson = JSON.parseObject(linkInfo);
				Long instanceId = mapLineService.convertToLink(linkInfoJson);
				if(null!=instanceId){
					retn.put("status",200);
					retn.put("msg", "转化成功");
					retn.put("instanceId",instanceId);
				}else{
					throw new RuntimeException("转化链路失败");
				}
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error(e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="unbindLinks",method=RequestMethod.POST)
	public JSONObject unbindLinks(Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				JSONArray tmpIds = mapLineService.unbindLinks(ids);
				retn.put("status",200);
				retn.put("msg", "链路取消绑定成功");
				retn.put("ids",tmpIds);
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error(e);
		}
		return retn;
	}
}
