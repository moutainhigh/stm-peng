package com.mainsteam.stm.topo.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.topo.api.IIpMacAlarmTaskApi;
import com.mainsteam.stm.topo.api.ITopoFindHandler;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.bo.VlanBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.VlanDao;
import com.qwserv.itm.netprober.bean.DevType;
@Service
public class TopoFindHandlerImpl implements ITopoFindHandler{
	Logger logger = Logger.getLogger(TopoFindHandlerImpl.class);
	//消息
	private Map<String,Object> msg = new HashMap<String,Object>(); 
	//产生的日志消息
	private List<String> logs = new ArrayList<String>();
	//时间可视化
	private SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss,SSS  ");
	//ip_mac刷新
	@Autowired
	private IIpMacAlarmTaskApi alarmTaskApi;
	//节点dao
	@Autowired
	private INodeDao ndao;
	//链路dao
	@Autowired
	private ILinkDao ldao;
	//单链路发现时发现的节点
	private NodeBo singleFindNode=null;
	@Autowired
	private VlanDao vlanDao;
	//发现类型
	private String type;
	//开始时间
	private long beginTime=0;
	//拓扑发现设置dao
	@Autowired
	private ISettingDao settingDao;
	//硬件资源未授权计数器
	private int hardDeviceUnAuthCounter=0;
	//软件资源未授权计数器
	private int softDeviceUnAuthCounter=0;
	/**
	 * 当前运行状态，默认false
	 */
	private boolean isRunning = false;
	//第三方模块访问接口
	@Autowired
	private ThirdService thirdSvc;
	@Override
	public void addLinks(JSONArray links) {
		//链路图元入拓扑库
		buildMessage("正在处理链路...");
		logger.error(new StringBuilder("....................处理链路【").append(links.size()).append("】条-入拓扑链路表stm_topo_link..开始.................."));
		List<LinkBo> lbs = new ArrayList<LinkBo>();
		List<LinkBo> dbLinks = new ArrayList<LinkBo>();
		for(Object tmp:links){
			JSONObject link = (JSONObject)tmp;
			logger.error("添加链路：srcIfIdex="+link.getString("srcIf")+":desIfIdex="+link.getString("desIf")+",****处理链路信息："+link.toJSONString());
			try {
				//通过ip查是源端否存在
				NodeBo src = ndao.getFortopoFindByIp(link.getString("srcIp"));
				//再通过id找一遍
				if(src==null) src=ndao.getByDeviceId(link.getString("srcId"));
				
				//通过ip查是目的端否存在
				NodeBo des = ndao.getFortopoFindByIp(link.getString("desIp"));
				if(des==null) des=ndao.getByDeviceId(link.getString("desId"));
				logger.error("添加链路源端srcNode="+JSONObject.toJSONString(src));
				logger.error("添加链路目的端desNode="+JSONObject.toJSONString(des));
				
				LinkBo lb = new LinkBo();
				lb.setFrom(src.getId());
				lb.setTo(des.getId());
				lb.setFromType(LinkBo.NODE);
				lb.setToType(LinkBo.NODE);
				lb.setType("link");
				lb.setRawInfo(link);
				lb.setFromIfIndex(link.getLong("srcIf"));
				lb.setToIfIndex(link.getLong("desIf"));
				//连线判重
				List<LinkBo> tmpLinks = ldao.findLink(lb);
				if(tmpLinks.isEmpty()){
					lbs.add(lb);
				}else{
					//已经存在链路->更新端口索引等信息
					for(LinkBo tmpLink : tmpLinks){
						tmpLink.setFromIfIndex(lb.getFromIfIndex());
						tmpLink.setToIfIndex(lb.getToIfIndex());
						tmpLink.setRawInfo(lb.getRawInfo());
						dbLinks.add(tmpLink);
					}
					ldao.update(tmpLinks);
				}
			} catch (Exception e) {
				logger.error("拓扑添加链路其中一端设备不存在，过滤！srcIp="+link.getString("srcIp")+":desIp="+link.getString("desIp"),e);
			}
		}
		buildMessage(String.format("新发现链路%s条",lbs.size()));
		StringBuilder linkMsg = new StringBuilder("本次拓扑发现:链路【新添加").append(lbs.size()).append("条】,更新重复【").append(links.size()-lbs.size()).append("条】,【新添加】的链路信息=").append(JSON.toJSONString(lbs));
		logger.error(linkMsg.toString());
		
		//新链路入库
		ldao.save(lbs);
		//准备更新资源实例id（包括：新发现的链路和已存在的链路）
		lbs.addAll(dbLinks);
		
		try {
			//添加实例化链路到队列
			thirdSvc.addInstanceLinks(lbs);
		} catch (Exception e) {
			logger.error("添加链路信息到实例化队列中发生异常",e);
		}
		logger.error("....................处理链路,入拓扑链路表stm_topo_link..结束..................");
	}
	
	@Override
	public void addNode(JSONObject device) {
		//实例化图元
		logger.error("***find a device by DCS component，设备节点入图元库:"+device.toJSONString());
		NodeBo node = new NodeBo();
		node.setType(device.getString("devType"));
		node.setOid(device.getString("sysOid"));
		node.setIp(device.getString("ipDis"));
		node.setId(ndao.getSequence().next());
		node.setDeviceId(device.getString("id"));
		node.setMacAddress(device.getString("macs").split(","));
		if(device.containsKey("ips")){
			JSONArray ips = device.getJSONArray("ips");
			node.setIps(ips.toArray(new String[0]));
		}
		//入二层
		node.setSubTopoId(NodeBo.SECONDTOPO_ID);
		node.setShowName(device.getString("sysName"));
		node.setRawInfo(device);
		//匹配图标属性
		node.mapAttr();
		try {
			//添加节点实例化到资源库
			thirdSvc.addResourceInstance(node);
		} catch (Throwable e) {
			Throwable cause = e.getCause();
			if(cause!=null && cause instanceof InstancelibException){
				if(((InstancelibException)cause).getCode()==704){
					if(node.isHardDevice()){	//是否为硬件资源
						this.hardDeviceUnAuthCounter++;
					}else{
						this.softDeviceUnAuthCounter++;
					}
				}
			}
			logger.error("添加图元库调用thirdSvc.addResourceInstance方法异常",e);
		}
		Long instanceId = node.getInstanceId();
		if(instanceId!=null){
			//看该设备是否已存在
			NodeBo tmp = ndao.getByInstanceId(instanceId);
			if(tmp==null || tmp.getId()==null){
				logger.info("新增设备"+node.getIp());
				//后期处理-针对不同发现类型采取不同数据【入库stm_topo_node】处理方式
				this.addPostHanlder(node);
				node.setRepeat(false);
			}else{
				node.setRepeat(true);
				node.setId(tmp.getId());
				logger.info("设备"+tmp.getIp()+"已存在");
			}
			this.setSingleFindNode(node);
		}else{
			//后期处理-针对不同发现类型采取不同数据入库处理方式
			this.addPostHanlder(node);
		}
		//构建消息
		if(node.getTypeName()!=null){
			buildMessage("发现设备"+node.getIp()+"设备类型:"+node.getTypeName());
		}else{
			buildMessage("发现设备"+node.getIp()+"设备类型:"+node.getType());
		}
		//统计相应的类型数量
		String key = node.getType().toLowerCase();
		Object obj = msg.get(key);
		if(null != obj){
			Integer count = (Integer) obj;
			msg.put(key, ++count);
		}else{
			msg.put(key, 1);
		}
		Integer total = (Integer)msg.get("total");
		msg.put("total", total+1);
		//加入实例化队列stack
		if(node.getInstanceId()!=null){
			thirdSvc.addInstanceNode(node);
		}
		//添加vlan信息
		if(device.containsKey("vlans")){
			this.addVlan(device.getJSONArray("vlans"),node);
		}
		//添加子网
		JSONArray subnets = device.getJSONArray("subnets");
		if(subnets!=null && !subnets.isEmpty() && !node.getType().equals(DevType.HOST.name()) && !node.getType().equals(DevType.SERVER.name())){
			//添加子网信息
			if(!node.isRepeat()){
				this.addSubnets(subnets,node);
			}
		}
	}
	
	private void addVlan(JSONArray vlans,NodeBo node) {
		for(Object tmp : vlans){
			JSONObject vlan = (JSONObject)tmp;
			VlanBo vb = new VlanBo();
			vb.setVlanId(vlan.getLong("id"));
			vb.setVlanName(vlan.getString("name"));
			vb.setPortsIndex(vlan.getString("portsIndex"));
			vb.setPortsName(vlan.getString("portsName"));
			vb.setNodeId(node.getId());
			vlanDao.saveVlan(vb);
		}
	}
	@Override
	public void buildMessage(String info) {
		logs.add(sf.format(new Date()) + info);
	}
	@Override
	public void stop(boolean isError) {
		if(this.hardDeviceUnAuthCounter>0 ||this.softDeviceUnAuthCounter>0){
			msg.put("hardUnauth", this.hardDeviceUnAuthCounter);
			msg.put("softUnauth", this.softDeviceUnAuthCounter);
		}
		msg.put("isError", isError);
		msg.put("isRunning", false);
		
		//发现完成后立即刷新ip-mac-port数据
		if(!isError){
			alarmTaskApi.refreshIpMacPort(null);
		}
		//发现完成后将数据保存到数据库中
		if(!isError){
			JSONObject msgs = getMsg(0);
			SettingBo settingBo = settingDao.getCfg("topo_find_result");
			if(settingBo!=null){
				settingBo.setValue(msgs.toJSONString());
				settingDao.updateCfg(settingBo);
			}else{
				settingBo=new SettingBo();
				settingBo.setKey("topo_find_result");
				settingBo.setValue(msgs.toJSONString());
				settingDao.save(settingBo);
			}
		}
	}
	
	@Override
	public void setType(String type) {
		this.type=type;
	}
	@Override
	public String getType(){
		return this.type;
	}
	@Override
	public void resetMsg(){
		msg.clear();
		msg.put("total",0);
		msg.put("isRunning", true);
		msg.put("secondPassed",0);
		msg.put("type", this.type);
		this.beginTime=new Date().getTime();
		this.setSingleFindNode(null);
		this.hardDeviceUnAuthCounter=0;
		this.softDeviceUnAuthCounter=0;
		logs.clear();
	}
	@Override
	public JSONObject getMsg(int index) {
		JSONArray msgs = new JSONArray();
		while (index<logs.size()) {
			JSONObject msg = new JSONObject();
			msg.put("msg", logs.get(index));
			index++;
			msgs.add(msg);
		}
		this.msg.put("index", logs.size());
		this.msg.put("msgs", msgs);
		this.msg.put("type", this.type);
		long endTime = new Date().getTime();
		int secondPassed = (int)(endTime-this.beginTime)/1000;
		this.msg.put("secondPassed", secondPassed);
		//百分之10的概率去直接检查远端是否已经停止拓扑发现
		/*double random = Math.random()*100;
		if(random>=90){
			this.isRunning=thirdSvc.isTopoRunning();
			logger.info(String.format("topo status (%s)",this.isRunning));
			this.msg.put("isRunning", this.isRunning);
		}*/
		return (JSONObject) JSON.toJSON(this.msg);
	}
	/**
	 * 添加子网信息
	 * @param device
	 */
	private void addSubnets(JSONArray subnets,NodeBo device){
		//添加该网络设备
		List<NodeBo> nbs = ndao.getByIp(device.getIp(),NodeBo.THIRDTOPO_ID);
		if(nbs.isEmpty()){
			device.setSubTopoId(NodeBo.THIRDTOPO_ID);
			device.setId(ndao.getSequence().next());
			ndao.insert(device);
		}
		//添加和该子网的连接关系
		List<LinkBo> relations = new ArrayList<LinkBo>();
		for(Object tmp:subnets){
			NodeBo net = null;
			//数据库是否存在该子网
			List<NodeBo> tmpNbs = ndao.getByIp(tmp.toString(),NodeBo.THIRDTOPO_ID);
			if(!tmpNbs.isEmpty()){
				net=tmpNbs.get(0);
			}else{
				net=new NodeBo();
				net.setIp(tmp.toString());
				net.setSubTopoId(NodeBo.THIRDTOPO_ID);
				net.setX(0d);
				net.setY(0d);
				net.setIcon("themes/blue/images/topo/topoIcon/net/subnet.png");
				net.setIconHeight(33d);
				net.setIconWidth(33d);
				net.setId(ndao.getSequence().next());
				net.setType("NET");
				ndao.insert(net);
			}
			LinkBo lb = new LinkBo();
			lb.setFrom(device.getId());
			lb.setTo(net.getId());
			lb.setFromType(LinkBo.NODE);
			lb.setToType(LinkBo.NODE);
			lb.setType("line");
			relations.add(lb);
		}
		ldao.save(relations);
	}
	/**
	 * 后期业务处理
	 * @param node
	 */
	public void addPostHanlder(NodeBo node) {
		if(null!=type){
			switch(type){
				//扩展发现
				case "extend":
					//增量式入库
					addIncrementalInsert(node);
				break;
				//网段发现
				case "segment":
					//增量式入库
					addIncrementalInsert(node);
				break;
				default:
					//默认直接入库
					ndao.insert(node);
					/*List<NodeBo> tmps = ndao.getByIp(node.getIp());
					if(tmps.isEmpty()){
						ndao.insert(node);
					}else{
						BeanUtils.copyProperties(tmps.get(0),node);
					}*/
				break;
			}
		}
	}
	/**
	 * 增量式入库，以前存在的ip就不做任何改动，没有的话才做入库操作
	 * @param node
	 */
	public void addIncrementalInsert(NodeBo node){
		//检查是否该ip已经存在
		List<NodeBo> nodes = ndao.getByIp(node.getIp(),NodeBo.SECONDTOPO_ID);
		if(nodes.isEmpty()){//只有ip不存在的才做插入操作
			ndao.insert(node);
		}else{
			//更新资源实力id
//			NodeBo nb = nodes.get(0);
		}
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public NodeBo getSingleFindNode() {
		return singleFindNode;
	}
	public void setSingleFindNode(NodeBo singleFindNode) {
		this.singleFindNode = singleFindNode;
	}
	@Override
	public boolean isWholeNetFind() {
		String type = this.getType();
		if("wholeNet".equals(type)){
			return true;
		}
		return false;
	}
}
