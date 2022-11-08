package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.TopoClipboardService;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;

@Controller
@RequestMapping(value="topo/clipboard")
public class TopoClipboardAction {
	Logger logger = Logger.getLogger(TopoClipboardAction.class);
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Autowired
	private TopoClipboardService clipService;
	/**
	 * 复制
	 * @param topoId
	 * @param ids
	 */
	@ResponseBody
	@RequestMapping(value="copy")
	public JSONObject copy(Long topoId,Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){
				JSONArray duplicate = clipService.copy(topoId,ids);
				retn.put("status",200);
				retn.put("items",duplicate);
				retn.put("msg", "复制成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error("",e);
		}
		return retn;
	}
	/**
	 * 移动
	 * @param topoId
	 * @param ids
	 */
	@ResponseBody
	@RequestMapping(value="move")
	public JSONObject move(Long topoId,Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){
				JSONArray duplicate = clipService.move(topoId,ids);
				retn.put("items",duplicate);
				retn.put("status",200);
				retn.put("msg", "移动成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error("move exception",e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="replace")
	public JSONObject replace(String replaceStr,Long topoId,boolean isMove){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){
				JSONObject tmp = JSON.parseObject(replaceStr);
				clipService.replace(tmp,topoId,isMove);
				retn.put("status",200);
				retn.put("msg", "移动成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg", e.getMessage());
			logger.error("move exception",e);
		}
		return retn;
	}
}
