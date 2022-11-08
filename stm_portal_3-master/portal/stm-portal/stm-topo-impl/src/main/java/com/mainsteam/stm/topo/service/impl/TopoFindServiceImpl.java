package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ITopoFindHandler;
import com.mainsteam.stm.topo.api.ITopoGraphApi;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.api.TopoFindService;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.ITopoFindDao;
import com.mainsteam.stm.topo.enums.SettingKey;
import com.mainsteam.stm.topo.util.CoreIps;
import com.qwserv.itm.netprober.bean.StatusProcess;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TopoFindServiceImpl implements TopoFindService{
	Logger logger = Logger.getLogger(TopoFindServiceImpl.class);
	//拓扑发现处理服务类
	@Autowired
	private ITopoFindHandler handler;
	//调用第三方服务
	@Autowired
	private ThirdService thirdSvc;
	@Autowired
	private ITopoGraphApi tgsvc;
	//系统设置dao
	@Autowired
	private ISettingDao sdao;
	@Autowired
	private ITopoFindDao tdao;
	@Autowired
	private INodeDao ndao;
	//是否已经取消
	private boolean isCanceled=false;
	@Override
	public String topoFind(String type){
		logger.error("*****************拓扑发现***begin**********************"+type);
		this.isCanceled=false;
		//重置上次发现结果
		if(!handler.isRunning()){
			handler.resetMsg();
		}else{
			return "拓扑发现正在进行,请稍后重试";
		}
		//获取本次发现类型的配置信息
		JSONObject params = this.getCurrentFindParams(type);
		logger.error("*****start topofind params :" + params.toJSONString());
		
		//检查发现参数
		JSONObject checkRst = this.checkFindParams(params, type);
		if(checkRst.getBooleanValue("checked")){
			return checkRst.getString("msg");
		}
		
		try {	//开始采集
			handler.setType(type);	//设置发现类型
			StatusProcess state = thirdSvc.topoFind(params,handler);
			
			//根据不同发现类型处理以前的拓扑库中的数据
			switch(state){
				case Busy:
					handler.setRunning(true);
					return "正在进行拓扑发现";
				case Failed:
					handler.stop(true);
					return "拓扑发现启动失败";
				case Running:
					switch(type){
						case "wholeNet":
							wholeNetFind(params);
							break;
						case "subnet":
							subnetFind(params);
							break;
					}
					//设置发现类型
					params.put("type", type);
					//设置处理器的发现类型
					handler.setType(type);
					return "拓扑发现已经启动，正在进行扫描...";
				default:
					return "拓扑发现启动异常";
			}
		} catch (Throwable e) {
			handler.stop(true);
			logger.error("拓扑发现发生异常",e);
		}
		logger.error("*****************拓扑发现***end**********************"+type);
		return "拓扑发现启动异常";
	}

	/**
	 * 获取本次拓扑发现类型的配置信息
	 * @param type
	 * @return
	 */
	private JSONObject getCurrentFindParams(String type) {
		//获取本次发现类型的配置信息
		SettingBo current = sdao.getCfg(type);
		//获取总体拓扑发现配置信息
		SettingBo topoSetting = sdao.getCfg("topoSetting");
		//获取CommonBody配置信息
		SettingBo commonBody = sdao.getCfg("commonBody");
		String commonBodyStr = (null!=commonBody)?commonBody.getValue():"{}";
		//将他们组合起来
		JSONObject params = new JSONObject();
		params.put("topoSetting", null != topoSetting?JSON.parse(topoSetting.getValue()):topoSetting);	//有可能为空，检查参数是要判断
		params.put("commonBody", JSON.parse(commonBodyStr));
		params.put(type, null !=current?JSON.parse(current.getValue()):current);	//current有可能为空，检查参数是要判断
		params.put("type",type);
		return params;
	}
	
	/**
	 * 检查拓扑发现参数是否正确
	 * @param params
	 * @param type
	 * @return
	 */
	private JSONObject checkFindParams(JSONObject params,String type){
		JSONObject rst = new JSONObject();
		boolean checked = false;
		String msg = null;
		//当前的状态时不能为空的
		if(null == params.get(type) || !params.containsKey("topoSetting") || !params.containsKey("commonBody")){
			checked = true;
			msg = "缺少发现配置信息,请检查后重试!";
		}else{
			JSONObject commonBody = params.getJSONObject("commonBody");	//commonBody
			if(null==commonBody){
				checked = true;
			}else{
				int snmpversion = 0;
				try {
					snmpversion=commonBody.getIntValue("snmpversion");
				} catch (Exception e) {
					snmpversion=0;
				}
				if(snmpversion == 0 || snmpversion == 1){	//0:v1,1:v2,3:v3；v3发现可以没有commbody.rows
					JSONArray commonBodyRows = commonBody.getJSONArray("rows");
                    checked = (commonBodyRows == null || commonBodyRows.size() == 0);
                }
			}
			if(checked){	//共体配置
				msg = "请配置共同体！";
			}else if("wholeNet".equals(type)){	//全网发现
				JSONArray wholeNet = params.getJSONObject("wholeNet").getJSONArray("ips");
				checked = (null==wholeNet || wholeNet.size()==0);
				msg = checked?"请配置全网发现-核心IP":null;
			}else if("extend".equals(type)){	//扩展发现
				JSONArray extend = params.getJSONArray("extend");
				checked = (null==extend || extend.size()==0);
				msg = checked?"请配置扩展发现-核心IP":null;
			}else if("subnet".equals(type)){	//子网发现
				JSONArray subnet = params.getJSONObject("subnet").getJSONArray("subnets");
				checked = (null==subnet || subnet.size()==0);
				msg = checked?"请配置子网发现参数":null;
			}else if("segment".equals(type)){	//网段发现
				checked = (null==params.get("segment"));
				msg = checked?"请配置网段发现-IP地址":null;
			}
		}
		if(checked) logger.error(msg);
		final String errorConst = "error-";
		rst.put("checked", checked);
		rst.put("msg",errorConst+msg);
		return rst;
	}
	@Override
	public JSONObject resultInfo(int index) {
		JSONObject msg = handler.getMsg(index);
		msg.put("isCanceled", this.isCanceled);
		return msg;
	}
	//子网发现
	@Override
	public void subnetFind(JSONObject params) {
		//子网配置
		JSONObject cfg = params.getJSONObject("subnet");
		boolean research = cfg.getBoolean("research");
		JSONArray subnets = cfg.getJSONArray("subnets");
		if(research){//重新发现子网
			//查找所有那些子网相关的节点
			Map<Long, String> idIp = tdao.findIp();
			CoreIps ci = new CoreIps();
			List<Long> prepareDelete = new ArrayList<Long>();
			for(Map.Entry<Long,String> entry :idIp.entrySet()){
				String checkIp = entry.getValue();
				//检查是否在重新发现的子网内,如果在，准备删除
				for(Object tmp : subnets){
					JSONObject subnet = (JSONObject) tmp;
					ci.setSubnetIps(subnet.getString("ip"), subnet.getString("mask"));
					if(ci.contains(checkIp)){
						prepareDelete.add(entry.getKey());
					}
				}
			}
			tdao.deleteNodes(prepareDelete);
		}
	}
	//全网发现
	@Override
	public void wholeNetFind(JSONObject params) {
		//删除链路
		tdao.trunkLinkAll();
		//删除节点表
		tdao.trunkNodeAll();
		tdao.deleteSubTopo(NodeBo.SECONDTOPO_ID);
		sdao.deleteCfg(SettingKey.TOPO_ROOM_CABINET_IDS.getKey());
	}
	@Override
	@Transactional
	public JSONObject singleTopoFind(JSONObject node) {
		JSONObject retn = new JSONObject();
		if(handler.isRunning()){
			retn.put("msg", "拓扑发现正在进行,请稍后重试");
			retn.put("state", 700);
		}else{
			//检查是否已经发现过
			NodeBo nb = ndao.getFortopoFindByIp(node.getString("ip"));
			if(null==nb){
				//先获取总体拓扑发现配置信息
				SettingBo topoSetting = sdao.getCfg("topoSetting");
				
				JSONObject topoSettingJson = (JSONObject)JSON.parse(topoSetting.getValue());
				JSONObject commonBodyJson = new JSONObject();
				JSONArray commonBodys = new JSONArray();
				JSONObject wholeNetJson = new JSONObject();
				//修改参数
				//拓扑发现配置
				topoSettingJson.put("hop", "0");//跳数设为0
				topoSettingJson.put("include", new JSONArray());
				topoSettingJson.put("exclude", new JSONArray());
				topoSettingJson.put("snmpv3", new JSONArray());
				
				JSONArray ips = new JSONArray();
				//同体字
				JSONObject commonBody = new JSONObject();
				commonBody.put("readOnly", node.getString("commonBodyName"));
				commonBody.put("readWrite", "");
				commonBodys.add(commonBody);
				commonBodyJson.put("rows", commonBodys);
				ips.add(node.getString("ip"));
				
				wholeNetJson.put("ips", ips);
				
				//将他们组合起来
				JSONObject params = new JSONObject();
				params.put("topoSetting", topoSettingJson);
				params.put("commonBody", commonBodyJson);
				params.put("wholeNet", wholeNetJson);
				params.put("type","single");
				handler.setType("single");
				//开始发现
				handler.resetMsg();
				thirdSvc.topoFind(params,handler);
				while(handler.isRunning()){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger.error("singlefind error",e);
					}
				}
				//将该ip返回回去
				nb = handler.getSingleFindNode();
				if(null!=nb){
					nb.setIcon(node.getString("src"));
					nb.setX(node.getDouble("x"));
					nb.setY(node.getDouble("y"));
					JSONObject attr = new JSONObject();
					attr.put("name",node.getString("name"));
					nb.setAttr(attr.toJSONString());
					attr.put("manageIp", nb.getIp());
					attr.put("instanceId",nb.getInstanceId());
					try {
						tgsvc.updateResourceBaseInfo(attr);
					} catch (Throwable e) {
						logger.error("updateResourceBaseInfo",e);
					}
					ndao.update(Arrays.asList(nb));
					ndao.replaceIcon(nb.getId(), nb.getIcon());
				}
				//更新图标
			}else{
				retn.put("msg", nb.getIp()+"已经发现");
				retn.put("isRepeat", true);
			}
			if(nb!=null){
				retn.put("node", JSON.toJSON(nb));
				retn.put("msg", "添加成功");
			}else{
				retn.put("msg", "无相关设备");
			}
		}
		return retn;
	}
	@Override
	public int cancelDiscover() {
		this.isCanceled=true;
		return thirdSvc.cancelDiscover();
	}
}
