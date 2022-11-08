package com.mainsteam.stm.topo.collector;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.util.Util;
import com.qwserv.itm.netprober.bean.DiscoveryStyle;
import com.qwserv.itm.netprober.bean.LinkBean;
import com.qwserv.itm.netprober.bean.MyException;
import com.qwserv.itm.netprober.bean.SNMPCollInfo;
import com.qwserv.itm.netprober.bean.SnmpCollType;
import com.qwserv.itm.netprober.bean.StatusProcess;
import com.qwserv.itm.netprober.bean.SubnetBean;
import com.qwserv.itm.netprober.bean.TopoDisBean;
import com.qwserv.itm.netprober.context.FrontgroundCfg;
import com.qwserv.itm.netprober.devdef.NetElementBean;
import com.qwserv.itm.netprober.devdef.entry.VlanItemPojo;
import com.qwserv.itm.netprober.devdef.tabbeans.IfTabBean;
import com.qwserv.itm.netprober.devdef.tabbeans.VlanTabBean;
import com.qwserv.itm.netprober.message.ProcessMsg;

/**
 * 拓扑采集信息收集器
 * @author 富强
 *
 */
public class TopoCollector implements TopoCollectorMBean{
	public static String cfgPath = null;
	private final static Logger logger = Logger.getLogger(TopoCollector.class);
	static {
		try {
			String tmpPath = TopoCollector.class.getResource("/").toURI().getPath();
			cfgPath=tmpPath.replace("config", "topo");
			logger.info("root path = "+cfgPath);
		} catch (URISyntaxException e) {
			logger.error("TopoCollector get path error ",e);
			cfgPath="./";
		}
	}
	//设备
	private Stack<NetElementBean> devices=new Stack<NetElementBean>();
	//链路
	private Stack<Set<LinkBean>> links = new Stack<Set<LinkBean>>();
	//消息
	private Stack<ProcessMsg[]> msgs = new Stack<ProcessMsg[]>();
	//发现组件管理
	private TopologyDiscoveryManager manager=null;
	//强制停止标记
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	public void addSnmpNode(NetElementBean device) {
		logger.info(sf.format(new Date())+"添加设备"+device.getIpDis());
		this.devices.push(device);
	}

	public void addLinks(Set<LinkBean> link) {
		logger.info(sf.format(new Date())+"添加链路，总数"+link.size());
		this.links.push(link);
	}

	public void buildMessage(ProcessMsg[] msg) {
		for(ProcessMsg m:msg){
			MyException e = m.getException();
			if(null!=e){
				logger.error("buildMessage 拓扑发现组件异常",e);
			}
		}
	}
	@Override
	public JSONObject next(){
		JSONObject retn = new JSONObject();
		JSONArray data = new JSONArray();
		retn.put("isRunning", this.isRunning());
		JSONArray link = nextLinkSet();
		JSONObject device = nextNetElementBean();
		while(link!=null || device!=null){
			JSONObject tmp = new JSONObject();
			try {
				tmp.put("link",link);
				tmp.put("device", device);
				data.add(tmp);
				link=nextLinkSet();
				device=nextNetElementBean();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		retn.put("data", data);
		logger.info("info="+retn.toJSONString());
		return retn;
	}
	public JSONArray nextLinkSet() {
		if(!this.links.isEmpty()){
			Set<LinkBean> lb = this.links.pop();
			if(null!=lb){
				JSONArray array = new JSONArray();
				for(LinkBean l:lb){
					JSONObject ll = new JSONObject();
					ll.put("srcId", l.getSrcId());
					ll.put("desId", l.getDestId());
					ll.put("srcIf", l.getSrcIf());
					ll.put("desIf", l.getDestIf());
					ll.put("srcIfName", l.getSrcIfName());
					ll.put("desIfName", l.getDestIfName());
					ll.put("linkSpeed", l.getLinkSpeed());
					ll.put("srcPort", l.getSrcPort());
					ll.put("desPort", l.getDestPort());
					ll.put("desIp", l.getDestIp());
					ll.put("srcIp", l.getSrcIp());
					array.add(ll);
				}
				return array;
			}
		}
		return null;
	}
	public JSONObject nextNetElementBean() {
		if(!this.devices.isEmpty()){
			NetElementBean nb = this.devices.pop();
			if(null!=nb){
				JSONObject retn = new JSONObject();
				retn.put("desc", nb.getDesc());
				retn.put("devType", nb.getDevType().name());
				retn.put("hop", nb.getHop());
				retn.put("id", nb.getId());
				SNMPCollInfo collInfo = nb.getSnmpConnInfo();
				retn.put("conninfo", JSON.toJSON(collInfo));
				retn.put("ipDis", nb.getIpDis());
				retn.put("ownMac", nb.getOwnMac());
				retn.put("sysName", nb.getSysName());
				Set<String> macs = nb.getOwnMacs();
				if(null!=macs){
					retn.put("macs", Util.join(macs, ","));
				}else{
					retn.put("macs", "");
				}
				retn.put("sysOid", nb.getSysOid());
				retn.put("subnets",JSON.toJSON(nb.getBaseFilter().getIpTable().getSubnets()));
				retn.put("ips",JSON.toJSON(nb.getBaseFilter().getIpTable().getAllIps()));
				//增加vlan
				if(nb.getSwitchFilter()!=null && nb.getSwitchFilter().getVlanTable() != null){
					JSONArray vlans = new JSONArray();
					VlanTabBean vlanTable = nb.getSwitchFilter().getVlanTable();
					VlanItemPojo[] vlanItems = vlanTable.getItems();
					IfTabBean ifTable = nb.getBaseFilter().getIfTable();
					Map<Integer,String> indexMap = new HashMap<Integer,String>(1);
					if(ifTable!=null){
						indexMap = ifTable.getIfNames();
					}
					for (VlanItemPojo vlanItem:vlanItems) {
						JSONObject tmp = new JSONObject();
						tmp.put("id", vlanItem.getVlanId());
						tmp.put("name", vlanItem.getVlanName());
						StringBuilder portsName = new StringBuilder();
						StringBuilder portsIndex = new StringBuilder();
						for(String port : vlanItem.getPorts()){
							portsName.append(indexMap.get(Integer.parseInt(port)))
								 .append(",");
							portsIndex.append(port)
							 .append(",");
						}
						if(portsName.length()>0){
							portsName.deleteCharAt(portsName.length()-1);
						}
						if(portsIndex.length()>0){
							portsIndex.deleteCharAt(portsIndex.length()-1);
						}
						tmp.put("portsName", portsName.toString());
						tmp.put("portsIndex",portsIndex.toString());
						vlans.add(tmp);
					}
					retn.put("vlans", vlans);
					logger.info("add vlans="+vlans);
				}
				return retn;
			}
		}
		return null;
	}
	public JSONArray nextProcessMsg() {
		if(!this.msgs.isEmpty()){
			ProcessMsg[] msg=this.msgs.pop();
			if(null!=msg){
				JSONArray array = new JSONArray();
				for(ProcessMsg ms:msg){
					JSONObject mm = new JSONObject();
					mm.put("disMsg", ms.getDisMsg());
					mm.put("flowNo", ms.getFlowNo());
					mm.put("disProcess", ms.getDisProcess());
					mm.put("resType", ms.getResType());
					mm.put("ipMacAliveSize", ms.getIpmacAliveSize());
					array.add(mm);
				}
				return array;
			}
		}
		return null;
	}
	@Override
	public String start(String cfg) {
		if(logger.isInfoEnabled()){
			logger.info("发现参数:"+cfg);
		}
		// 1.封装调用策略发现组件接口参数
		Object[] params = packFindParams(cfg);
		// 2.开始发现
		manager = TopologyDiscoveryManager.getInstance();
		manager.setTdsvc(this);
		String result = manager.discoveryL2Topology((TopoDisBean)params[0],(FrontgroundCfg)params[1],(String)params[2],(Boolean)params[3]);
		if (StatusProcess.Failed.name().equals(result) || StatusProcess.Busy.name().equals(result)) {
			logger.info("采集器状态="+result);
		}else{
			this.init();
		}
		return result;
	}
	@Override
	public boolean isRunning() {
		//加入强制停止
		boolean running = manager.isRunning() || !this.devices.isEmpty() || !this.links.isEmpty();
		logger.info(String.format("manger.isRunning=(%s) devices.isEmpty=(%s) link.isEmpty=(%s)",manager.isRunning(),this.devices.isEmpty(),this.links.isEmpty()));
		return running;
	}
	/**
	 * 初始化收集容器
	 */
	private void init() {
		this.devices.clear();
		this.links.clear();
		logger.info("拓扑管理采集器启动成功");
	}
	/**
	 * 封装调用策略发现组件接口参数
	 * @param settingCfg
	 * @return [TopoDisBean,FrontgroundCfg,"D:\\topo",false]
	 */
	private Object[] packFindParams(String settingCfg){
		FrontgroundCfg cfg = new FrontgroundCfg();
		Object[] params = new Object[4];
		JSONObject settingJson = JSONObject.parseObject(settingCfg);
		//端口
		int port=161;
		//重试次数
		int retryTimes=1;
		//超市时间
		int timeout=1600;
		//版本
		int snmpversion=0;	//0:v1,1:v2,3:v3
		//配置共同体
		JSONObject commonBody = settingJson.getJSONObject("commonBody");	//commonBody
		//获取commonbBody的配置
		if(commonBody.containsKey("snmpversion") && null !=commonBody.get("snmpversion")){
			try {
				snmpversion=commonBody.getIntValue("snmpversion");
			} catch (Exception e) {
				snmpversion=0;
			}
		}
		if(commonBody.containsKey("timeout")){
			try {
				timeout=commonBody.getIntValue("timeout");
			} catch (Exception e) {
				timeout=1600;
			}
		}
		if(commonBody.containsKey("retryTimes")){
			try {
				retryTimes=commonBody.getIntValue("retryTimes");
			} catch (Exception e) {
				retryTimes=1;
			}
		}
		if(commonBody.containsKey("port")){
			try {
				port=commonBody.getIntValue("port");
			} catch (Exception e) {
				port=161;
			}
		}
		
				
		List<SNMPCollInfo> collInfos = new ArrayList<SNMPCollInfo>();
		//拓扑的整体设置
		JSONObject topoSetting = settingJson.getJSONObject("topoSetting");
		
		//snmpv3设置，如果页面共同体配置的是V1或则V2,那么不传snmpV3的配置；如果选则的是V3则，则把共同体的配置变为v1和v2各一份
		if(snmpversion == 3){	//0:v1,1:v2,3:v3
			JSONArray snmpv3 = topoSetting.getJSONArray("snmpv3");			//snmpv3
			for(Object snmp:snmpv3){
				JSONObject snmpJson = (JSONObject) JSONObject.toJSON(snmp);
				SNMPCollInfo info = packSnmpColl(snmpJson);
				//设置参数
				info.setPort(port);
				info.setRetryTimes(retryTimes);
				info.setTimeout(timeout);
				info.setVersion(3);
				collInfos.add(info);
			}
		}
		if(commonBody.containsKey("rows")){
			//0:v1,1:v2,3:v3	
			if(snmpversion == 0 || snmpversion == 1){
				this.setSnmpVersionInfo(port, retryTimes, timeout, snmpversion,commonBody, collInfos);
			}else if(snmpversion == 3){	//如果是页面选择的是v3，则后台手动将共同体版本配置成v1和v2各一份
				this.setSnmpVersionInfo(port, retryTimes, timeout, 1,commonBody, collInfos);	//手动设置共同体为v2
				this.setSnmpVersionInfo(port, retryTimes, timeout, 0,commonBody, collInfos);	//手动设置共同体为v1
			}
		}
		
		//搜索算法
		if(topoSetting.containsKey("seachWay")){
			String searchWay = topoSetting.getString("seachWay");
			if("arp".equals(searchWay)){
				cfg.setDisProtocol(DiscoveryStyle.CDP.toString());
			}else{
				cfg.setDisProtocol(DiscoveryStyle.ROUTE.toString());
			}
		}
		//添加算法
		boolean useCdp = false;//常规算法
		String algorithm = topoSetting.getString("algorthm");
		if(algorithm.equals("cdp")){
			useCdp=true;
		}
		TopoDisBean disBean = new TopoDisBean();
		//设置包括的子网
		JSONArray includeSubnets = topoSetting.getJSONArray("include");
		for(Object seg:includeSubnets){
			JSONObject tmpSeg = (JSONObject) seg;
			SubnetBean sb = new SubnetBean(tmpSeg.getString("ip"),tmpSeg.getString("mask"));
			disBean.addSubnetsAdded(sb);
		}
		//设置排除的子网
		JSONArray excludeSubnets = topoSetting.getJSONArray("exclude");
		for(Object seg:excludeSubnets){
			JSONObject tmpSeg = (JSONObject) seg;
			SubnetBean sb = new SubnetBean(tmpSeg.getString("ip"),tmpSeg.getString("mask"));
			disBean.addExcludedSubnet(sb);
		}
		//设置跳数
		int hop = topoSetting.getIntValue("hop");
		//snmpv3的配置-重置默认值
		port=161;
		//重试次数
		retryTimes=1;
		//超市时间
		timeout=1600;
		if(topoSetting.containsKey("retryTimes")){
			try {
				retryTimes=topoSetting.getIntValue("retryTimes");
			} catch (Exception e) {
				retryTimes=1;
			}
		}
		if(topoSetting.containsKey("timeout")){
			try {
				timeout=topoSetting.getIntValue("timeout");
			} catch (Exception e) {
				timeout=1600;
			}
		}
		if(topoSetting.containsKey("port")){
			try {
				port=topoSetting.getIntValue("port");
			} catch (Exception e) {
				port=161;
			}
		}
		
		//具体发现方式配置
		String type = settingJson.getString("type");
		switch(type){
			//全网发现
			case "wholeNet":
				//添加核心ip
				JSONObject wholeNet = settingJson.getJSONObject(type);
				for(Object ip:wholeNet.getJSONArray("ips")){
					disBean.addSeedIp(ip.toString(), hop);
				}
			break;
			case "subnet":
				//添加子网
				JSONObject subnet = settingJson.getJSONObject(type);
				for(Object seg:subnet.getJSONArray("subnets")){
					JSONObject  sub = (JSONObject) seg;
					SubnetBean sb = new SubnetBean(sub.getString("ip"),sub.getString("mask"));
					disBean.addSubnetsAdded(sb);
				}
			break;
			case "segment":
				//添加网段
				JSONArray segments = settingJson.getJSONArray(type);
				for(Object o : segments){
					CoreIps coreIps = new CoreIps();
					JSONObject segment = (JSONObject)o;
					coreIps.setSegmentIps(segment.getString("startIp"), segment.getString("endIp"));
					for (String ip:coreIps.getAllIps()) {
						disBean.addIpAdded(ip);
					}
				}
			break;
			case "extend":
				//添加核心ip
				JSONArray extend = settingJson.getJSONArray(type);
				for(Object ip:extend){
					disBean.addSeedIp(ip.toString(), hop);
				}
			break;
			case "single":
				//添加核心ip
				JSONObject single = settingJson.getJSONObject("wholeNet");
				for(Object ip:single.getJSONArray("ips")){
					disBean.addSeedIp(ip.toString(), 0);
				}
			break;
		}
		//设置snmp信息
		cfg.setCollectorParams(collInfos);
		//组装参数
		params[0] = disBean;
		params[1] = cfg;
		
		params[2] = TopoCollector.cfgPath;		//发现数据生成目录地址，以后改成从配置文件读取
		params[3] = useCdp;
		
		return params;
	}

	//根据不同的snmp版本设置发现参数
	private void setSnmpVersionInfo(int port, int retryTimes, int timeout,int snmpversion, JSONObject commonBody, List<SNMPCollInfo> collInfos) {
		for(Object body:commonBody.getJSONArray("rows")){
			JSONObject bodyJson = (JSONObject) JSONObject.toJSON(body);
			SNMPCollInfo info = new SNMPCollInfo();
			String readOnly = bodyJson.getString("readOnly");
			String readWrite = bodyJson.getString("readWrite");
			if(null!=readOnly){
				info.setReadOnlyCommunity(readOnly);
			}
			if(null!=readWrite){
				info.setReadWriteCommunity(readWrite);
			}
			info.setPort(port);
			info.setRetryTimes(retryTimes);
			info.setTimeout(timeout);
			info.setVersion(snmpversion);
			collInfos.add(info);
		}
	}
	/**
	 * 设置SNMPColl的信息
	 * @param snmp
	 * @return SNMPCollInfo
	 * @param snmp
	 * @param commonBody
	 * @return
	 */
	private SNMPCollInfo packSnmpColl(JSONObject snmp){
		SNMPCollInfo collInfo = new SNMPCollInfo();
		//没值查询不出节点
		collInfo.setCollectType(SnmpCollType.GET);
		collInfo.setContextName(snmp.getString("contentName"));
		collInfo.setAuthProtocol(snmp.getString("protocal"));
		collInfo.setAuthPassword(snmp.getString("passWord"));
		collInfo.setAuthPrivatePassword(snmp.getString("epassword"));
		collInfo.setAuthPrivateProtocol(snmp.getString("eagreement"));
		collInfo.setSecurityLevel(snmp.getIntValue("safeLv"));
		collInfo.setSecurityName(snmp.getString("userName"));
		return collInfo;
	}
	@Override
	public int stopDiscover() {
		if(null!=manager){
			int r = manager.stopDiscovery();
			if(r==0){
				logger.info("停止拓扑采集成功");
			}else{
				logger.info("停止拓扑采集失败");
			}
			return r;
		}else{
			logger.info("未获取到拓扑发现组件");
			return -1;
		}
	}
}
