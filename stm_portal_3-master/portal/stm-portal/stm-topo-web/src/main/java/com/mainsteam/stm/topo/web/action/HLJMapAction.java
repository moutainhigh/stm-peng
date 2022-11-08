package com.mainsteam.stm.topo.web.action;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.HLJService;
import com.mainsteam.stm.topo.bo.HLJNode;

@Controller
@RequestMapping("topo/hlj")
public class HLJMapAction {
	@Autowired
	private HLJService hljService;
	@ResponseBody
	@RequestMapping("badge")
	public void getFlowListForMap(Integer size,Integer badge,String type,HttpServletResponse resp,HttpServletRequest req){
		BufferedImage image = hljService.getBadge(type,size,badge);
		try {
			ImageIO.write(image, "png",resp.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@ResponseBody
	@RequestMapping("relateInst")
	public JSONObject relateInstance(HLJNode node){
		JSONObject retn = new JSONObject();
		try {
			hljService.relateInstance(node);
			retn.put("status",200);
			retn.put("msg","关联成功");
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg",e.getMessage());
			e.printStackTrace();
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("refreshState")
	public JSONObject refreshState(Integer key){
		JSONObject retn = new JSONObject();
		try {
			retn.put("items",hljService.refreshState(key));
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			e.printStackTrace();
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("mapInfo")
	public JSONObject mapInfo(Integer mapId,Integer level){
		JSONObject retn = new JSONObject();
		try {
			retn.put("info",hljService.mapInfo(mapId,level));
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			e.printStackTrace();
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("getRelateInfo")
	public JSONObject getRelateInfo(Long id){
		JSONObject retn = new JSONObject();
		try {
			retn.put("info",hljService.getRelateInfo(id));
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			e.printStackTrace();
		}
		return retn;
	}
}
