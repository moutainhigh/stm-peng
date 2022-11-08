package com.mainsteam.stm.topo.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.HLJService;
import com.mainsteam.stm.topo.bo.HLJNode;
import com.mainsteam.stm.topo.dao.HLJDao;
@Service
public class HLJServiceImpl implements HLJService {
	@Autowired
	private HLJDao hljDao;
	@Autowired
	private DataHelper dataHelper;
	@Override
	public BufferedImage getBadge(String type,Integer size,Integer badge) {
		BufferedImage image = null;
		try {
			String badgeStr = badge.toString();
			float ratio = (size/12);
			int fontWidth=(int)(ratio*7*(badgeStr.length()));
			int margin=(int)(4*ratio);
			int height=size+margin*2,width=fontWidth+height-(int)(ratio*7);
			int cx=width/2,cy=height/2;
			int fontBeginX=cx-fontWidth/2;
			int fontBeginY=cy+(int)(ratio*4);
			image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics g= image.getGraphics();
			if(type==null||"green".equals(type)){
				g.setColor(new Color(0,211,68));
			}else{
				g.setColor(new Color(253,0,2));
			}
			
			//绘制背景
			//绘制两个半圆
			g.fillOval(-1, -1, height, height);
			g.fillRect(cy-1,-1,fontWidth-(int)(ratio*7),height);
			g.fillOval(cy+fontWidth-(int)(ratio*7)-height/2, -1, height, height);
			//绘制数字
			Font font = new Font("",Font.BOLD,size);
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString(badgeStr,fontBeginX,fontBeginY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	@Override
	public JSONArray refreshState(Integer key) {
		JSONArray items = new JSONArray();
		List<HLJNode> nodes = hljDao.getNodesByMapId("*",key);
		for(HLJNode node : nodes){
			int unavailable=0,available=0;
			String idsStr=node.getInstanceIds();
			if(idsStr!=null && !"".equals(idsStr)){
				for(String idStr:idsStr.split(",")){
					InstanceStateEnum state = dataHelper.getResourceInstanceAlarmInstanceStateEnum(Long.valueOf(idStr));
					if(isAvailable(state)){
						available++;
					}else{
						unavailable++;
					}
				}
				JSONObject item = new JSONObject();
				item.put("id",node.getId());
				item.put("nextMapId",node.getNextMapId());
				item.put("mapId", node.getMapId());
				item.put("nodeId",node.getNodeId());
				//显示绿色的可用性
				if(unavailable==0){
					item.put("state", "green");
					item.put("count",available);
				}else{
					item.put("state", "red");
					item.put("count",unavailable);
				}
				items.add(item);
			}
		}
		return items;
	}
	@Override
	public void relateInstance(HLJNode node) {
		if(node.getId()==null){//新增加
			hljDao.addNode(node);
			if(node.getParentMapId()!=null && node.getParentMapId()!=node.getMapId()){
				//如果不存在父到子的关系就建立一条空的关系节点，否则删除为空的关系节点
				if(!hljDao.existRelation(node.getParentMapId(),node.getMapId())){
					if(!hljDao.existNullRelation(node.getParentMapId(),node.getMapId())){
						HLJNode tmpNode = new HLJNode();
						tmpNode.setMapId(node.getParentMapId());
						tmpNode.setNextMapId(node.getMapId());
						hljDao.addNode(tmpNode);
					}
				}else{
					hljDao.delNullRelation(node.getParentMapId(),node.getMapId());
				}
			}
		}else{//更新
			HLJNode dbNode = hljDao.getById(node.getId());
			dbNode.setInstanceIds(node.getInstanceIds());
			if(node.getArea()!=null){
				dbNode.setArea(node.getArea());
			}
			if(node.getMapId()!=null){
				dbNode.setMapId(node.getMapId());
			}
			if(node.getNextMapId()!=null){
				dbNode.setNextMapId(node.getNextMapId());
			}
			if(node.getParentMapId()!=null){
				dbNode.setParentMapId(node.getParentMapId());
			}
			hljDao.updateInstanceIds(dbNode);
		}
	}
	private boolean isAvailable(InstanceStateEnum state){
		switch(state){
		case CRITICAL:
			return false;
		case CRITICAL_NOTHING:
			return false;
		case DELETED:
			return false;
		case MONITORED:
			return true;
		case NORMAL:
			return true;
		case NORMAL_CRITICAL:
			return true;
		case NORMAL_NOTHING:
			return true;
		case NORMAL_UNKNOWN:
			return true;
		case NOT_MONITORED:
			return false;
		case SERIOUS:
			return true;
		case UNKNOWN_NOTHING:
			return false;
		case UNKOWN:
			return false;
		case WARN:
			return true;
			default:
				return true;
		}
	}
	private void getAllNodesInMap(Integer mapId,List<HLJNode> nodes){
		List<HLJNode> tmpNodes = hljDao.getNodesByMapId("instanceIds,nextMapId",mapId);
		for(HLJNode node : tmpNodes){
			if(node.getNextMapId()!=null){
				getAllNodesInMap(node.getNextMapId(),nodes);
			}
			nodes.add(node);
		}
	}
	@Override
	public JSONObject mapInfo(Integer mapId,Integer level) {
		JSONObject retn = new JSONObject();
		List<HLJNode> nodes = new ArrayList<HLJNode>();
		if(level.equals(3)){
			getAllNodesByNextMapId(mapId,nodes);
		}else{
			getAllNodesInMap(mapId,nodes);
		}
		int amount=0,available=0,unavailable=0;
		//去重集合
		Set<Long> instIds = new HashSet<Long>();
		for(HLJNode node : nodes){
			String instanceIdsStr=node.getInstanceIds();
			if(instanceIdsStr!=null && !"".equals(instanceIdsStr)){
				for(String idStr : instanceIdsStr.split(",")){
					instIds.add(Long.valueOf(idStr));
				}
			}
		}
		amount=instIds.size();
		JSONArray items = new JSONArray();
		for(Long id : instIds){
			InstanceStateEnum state = dataHelper.getResourceInstanceAlarmInstanceStateEnum(id);
			if(isAvailable(state)){
				available++;
			}else{
				unavailable++;
				JSONObject item = new JSONObject();
				ResourceInstance res = dataHelper.getResourceInstance(id);
				item.put("ip", dataHelper.getResourceInstanceManageIp(res));
				item.put("showName",dataHelper.getResourceInstanceShowName(res));
				item.put("instanceId",id);
				item.put("state", state.name());
				items.add(item);
			}
		}
		retn.put("amount", amount);
		retn.put("available", available);
		retn.put("unavailable", unavailable);
		retn.put("items", items);
		return retn;
	}
	private void getAllNodesByNextMapId(Integer mapId, List<HLJNode> nodes) {
		List<HLJNode> tnodes = hljDao.getNodesByNextMapId(mapId);
		nodes.addAll(tnodes);
	}
	@Override
	public JSONObject getRelateInfo(Long id) {
		HLJNode node = hljDao.getById(id);
		return (JSONObject)JSON.toJSON(node);
	}
}
