package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.DeviceTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.IBackbordApi;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.dao.IBackbordBaseDao;
import com.mainsteam.stm.topo.dao.IBackbordRealDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 背板管理业务实现
 * @author zwx
 */
@Service
public class BackbordImpl implements IBackbordApi{
	private final Logger logger = LoggerFactory.getLogger(BackbordImpl.class);
	private IBackbordRealDao backbordRealDao;
	private IBackbordBaseDao backbordBaseDao;
	@Autowired
	private ThirdService thirdSvc;
	@Autowired
	private INodeDao ndao;
	@Autowired
	private CapacityService capacityService;	//能力库服务
	@Autowired
	private ResourceInstanceService resourceInstanceService;	//资源实例
	@Autowired
	private MetricDataService metricDataService;	//指标
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private IResourceInstanceExApi resourceExApi;
	

	@Override
	public JSONArray getInterfaceBySearchVal(Long instanceId, String searchVal) {
		JSONArray result = new JSONArray();
		JSONArray interfaces = this.getAllIfs(instanceId);
		//根据条件过滤数据
		if(null != interfaces && StringUtils.isNotBlank(searchVal)){
			String ifIndex = null,name = null,showName=null;
			for(int i=interfaces.size()-1;i>=0;i--){
				JSONObject interJson = (JSONObject)interfaces.get(i);
				ifIndex = interJson.getString("ifIndex");
				name = interJson.getString("name");
				showName = interJson.getString("showName");
				if(StringUtils.isNotBlank(ifIndex) && ifIndex.contains(searchVal)
					|| StringUtils.isNotBlank(name) && name.toLowerCase().contains(searchVal)
					|| StringUtils.isNotBlank(showName) && showName.toLowerCase().contains(searchVal)){
					result.add(interJson);	//索引号过滤
				}
			}
		}else{
			result = interfaces;
		}
		return result;
	}
	
	/**
	 * 导入背板xml数据
	 * 1. 解析xml
	 * 2. 更新背板数据
	 * @param file
	 * @return int 印象行数
	 * @throws IOException
	 * @throws DocumentException 
	 */
	@Override
	public int importBackbordXml(MultipartFile file) throws IOException, DocumentException {
		// 1. 解析xml
		Map<String, Object> backXmlMap = readXml(file);
		
		// 2. 更新背板数据
		Long instanceId = Long.valueOf((String)backXmlMap.get("instanceId"));
		String info = JSONObject.toJSONString(backXmlMap.get("info"));
		
		return backbordRealDao.addOrUpdateBackbordRealInfo(null,instanceId, info);
	}
	
	/**
	 * 读取背板xml数据
	 * @param file
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Object> readXml(MultipartFile file) throws DocumentException, IOException{
		List<Map<String, Object>> backgrounds = new ArrayList<Map<String,Object>>();	//背景
		List<Map<String, Object>> powers = new ArrayList<Map<String,Object>>();			//电源
		List<Map<String, Object>> cards = new ArrayList<Map<String,Object>>();			//板卡列表
		List<Map<String, Object>> ports = new ArrayList<Map<String,Object>>();			//接口列表
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> info = new HashMap<String, Object>();
		
		InputStream is = file.getInputStream();
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);

		Element root = document.getRootElement();
		for (Iterator i = root.elementIterator(); i.hasNext();) {
    		Element element = (Element) i.next();
    		if ("instanceId".equalsIgnoreCase(element.getName())){	//背板id
    			if(StringUtils.isBlank(element.getTextTrim())){
    				throw new RuntimeException("背板xml格式错误，没有instanceId");
    			}
    			map.put("instanceId", element.getTextTrim());
    		}
    		
    		if ("backgrounds".equalsIgnoreCase(element.getName())){	//背景图信息
    			for (Iterator c = element.elementIterator(); c.hasNext();) {
    				Element e = (Element)c.next();
    				if ("background".equals(e.getName())){
    					Map<String, Object> background = new HashMap<String, Object>();
    					background.put("imgUrl", e.attributeValue("imgUrl"));
    					background.put("x", e.attributeValue("x"));
    					background.put("y", e.attributeValue("y"));
    					background.put("width", e.attributeValue("width"));
    					background.put("height", e.attributeValue("height"));
    					backgrounds.add(background);
    				}
    			}
    			info.put("backgrounds", backgrounds);
    		}
    		
    		if("powers".equalsIgnoreCase(element.getName())){	//电源
    			for (Iterator c = element.elementIterator(); c.hasNext();) {
    				Element e = (Element)c.next();
    				if ("power".equals(e.getName())){
    					Map<String, Object> power = new HashMap<String, Object>();
    					power.put("x", parse2Double(e.attributeValue("x")));
    	    			power.put("y", parse2Double(e.attributeValue("y")));
    	    			power.put("imgUrl", e.attributeValue("imgUrl"));
    	    			power.put("width", e.attributeValue("width"));
    	    			power.put("height", e.attributeValue("height"));
    	    			powers.add(power);
    				}
    			}
    			info.put("powers", powers);
    		}
    		
    		if("cards".equalsIgnoreCase(element.getName())){	//板卡信息
    			for (Iterator c = element.elementIterator(); c.hasNext();) {
    				Element e = (Element)c.next();
    				if ("card".equals(e.getName())){
    					Map<String, Object> card = new HashMap<String, Object>();
    					card.put("index", parse2Double(e.attributeValue("index")));
    					card.put("x", parse2Double(e.attributeValue("x")));
    					card.put("y", parse2Double(e.attributeValue("y")));
    					card.put("imgUrl", e.attributeValue("imgUrl"));
    					card.put("width", e.attributeValue("width"));
    					card.put("height", e.attributeValue("height"));
    					cards.add(card);
    				}
    			}
    			info.put("cards", cards);
    		}
    		
    		if("ports".equalsIgnoreCase(element.getName())){	//接口信息
    			for (Iterator c = element.elementIterator(); c.hasNext();) {
    				Element e = (Element)c.next();
    				if ("port".equals(e.getName())){
    					Map<String, Object> port = new HashMap<String, Object>();
    					port.put("index", parse2Double(e.attributeValue("index")));
    					port.put("x", parse2Double(e.attributeValue("x")));
						port.put("y", parse2Double(e.attributeValue("y")));
						port.put("imgUrl", e.attributeValue("imgUrl"));
						port.put("width", e.attributeValue("width"));
						port.put("height", e.attributeValue("height"));
    					ports.add(port);
    				}
    			}
    			info.put("ports", ports);
    		}
    	}
		map.put("info", info);
		
		return map;
	}
	
	/**
	 * 字符串转换为doubel
	 * @param str
	 * @return
	 */
	private double parse2Double(String param){
		return Double.parseDouble(StringUtils.isBlank(param)?"0":param);
	}
	
	@Override
	public Object[] createBackborcXmlDom(Long instanceId,String ip) {
		Object[] objs = new Object[2];
		//1.查询背板数据
		BackbordBo bo = this.getBackboardBoByInstanceId(instanceId);
		
		//2.创建dom
		 Document document = DocumentHelper.createDocument();
		 objs[0] = document;
		 try {
			 if(null != bo && StringUtils.isNotBlank(bo.getInfo())){
				 JSONObject info = (JSONObject) JSONObject.parse(bo.getInfo());
				 Element root = document.addElement("backboard");	//根节点
				 
				 StringBuffer fName = new StringBuffer();			//导出文件名称
				 if(StringUtils.isNotBlank(bo.getVendor()) && StringUtils.isNotBlank(bo.getType()) && !"default".equals(bo.getVendor())){
					 fName.append(bo.getVendor()).append("_").append(bo.getType());
				 }
				 fName.append("[").append(ip).append("]").append(".xml");
				 objs[1] = fName.toString();
				 
				 Element ide = root.addElement("instanceId");		//背板资源实例id（唯一Id）
				 ide.addText(instanceId == null?"":instanceId.toString());
				 
				 Element backgrounds = root.addElement("backgrounds");	//背景
				 JSONArray backgroundArray = info.getJSONArray("backgrounds");
				 for(Object bg:backgroundArray){
					 JSONObject bgJson = (JSONObject) JSONObject.toJSON(bg);
					 Element background = backgrounds.addElement("background");
					 background.addAttribute("imgUrl", bgJson.getString("imgUrl"));
					 background.addAttribute("x", bgJson.getString("x"));
					 background.addAttribute("y", bgJson.getString("y"));
					 background.addAttribute("width", bgJson.getString("width"));
					 background.addAttribute("height", bgJson.getString("height"));
				 }
				 
				 Element powers = root.addElement("powers");	//电源
				 JSONArray powerArray = info.getJSONArray("powers");
				 for(Object pw:powerArray){
					 JSONObject pwJson = (JSONObject) JSONObject.toJSON(pw);
					 Element power = powers.addElement("power");
					 power.addAttribute("imgUrl",pwJson.getString("imgUrl"));
					 power.addAttribute("x",pwJson.getString("x"));
					 power.addAttribute("y",pwJson.getString("y"));
					 power.addAttribute("width", pwJson.getString("width"));
					 power.addAttribute("height", pwJson.getString("height"));
				 }
				 
				 Element cards = root.addElement("cards");	//板卡列表
				 JSONArray cardsArray = info.getJSONArray("cards");
				 for(Object card:cardsArray){
					 JSONObject cardJson = (JSONObject) JSONObject.toJSON(card);
					 Element ce = cards.addElement("card");
					 ce.addAttribute("index", cardJson.getString("index"));
					 ce.addAttribute("imgUrl", StringUtils.isNotBlank(cardJson.getString("imgUrl"))?
							 cardJson.getString("imgUrl"):cardJson.getString("onImg"));
					 ce.addAttribute("x",cardJson.getString("x"));
					 ce.addAttribute("y",cardJson.getString("y"));
					 ce.addAttribute("width",cardJson.getString("width"));
					 ce.addAttribute("height",cardJson.getString("height"));
				 }
				 
				 Element ports = root.addElement("ports");	//接口列表
				 JSONArray portArray = info.getJSONArray("ports");
				 for(Object port:portArray){
					 JSONObject portJson = (JSONObject) JSONObject.toJSON(port);
					 Element pe = ports.addElement("port");
					 pe.addAttribute("index", portJson.getString("index"));
					 pe.addAttribute("imgUrl", StringUtils.isNotBlank(portJson.getString("imgUrl"))?
							 portJson.getString("imgUrl"):portJson.getString("onImg"));
					 pe.addAttribute("x",portJson.getString("x"));
					 pe.addAttribute("y",portJson.getString("y"));
					 pe.addAttribute("width",portJson.getString("width"));
					 pe.addAttribute("height",portJson.getString("height"));
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
         
         return objs;
	}
	
	@Override
	public void saveBackbord(Long instanceId, String infoParam, Boolean isBatch) {
		//1.获取默认背板的id
		BackbordBo defaultBo = this.getVenderType(instanceId);
		Long baseId = defaultBo.getId();
		String vendor = defaultBo.getVendor();	//厂商
		
		//2.新增或修改该背板的信息
		String info = infoParam;
		int limit = 2000;	//处理oracle太长字符串无法插入问题
		if(info.length() <= limit){
			backbordRealDao.addOrUpdateBackbordRealInfo(baseId,instanceId, info);
		}else{
			//先清空，再赋值
			backbordRealDao.addOrUpdateBackbordRealInfo(baseId,instanceId," ");	//TODO:oracle表中建立了不能为空约束，故插入一个空格串，""空字符也插入不了
			//分多次插入更新过长数据
			String infoTmp = info.substring(0,limit);
			backbordRealDao.addOrUpdateBackbordRealInfo(baseId,instanceId, infoTmp);
			info = info.substring(infoTmp.length());
			while(info.length() >= limit){
				infoTmp = info.substring(0,limit);
				backbordRealDao.addUpdateInfo(instanceId, infoTmp);
				info = info.substring(infoTmp.length());
			}
			if(info.length() > 0 && info.length() < limit){
				backbordRealDao.addUpdateInfo(instanceId, info);
			}
		}
		
		//3.isBatch=true，同时修改相同厂商+型号的设备实时数据(不修改默认背板数据)
		if(isBatch && !"default".equals(vendor)){
			info = infoParam;
			if(info.length() <= limit){
				backbordRealDao.batchUpdateBackbordRealInfo(baseId, info);
			}else{
				//先清空，在赋值
				backbordRealDao.batchUpdateBackbordRealInfo(baseId, " ");
				//分多次插入更新过长数据
				String infoTmp = info.substring(0,limit);
				backbordRealDao.batchUpdateBackbordRealInfo(baseId, infoTmp);
				info = info.substring(infoTmp.length());
				while(info.length() >= limit){
					infoTmp = info.substring(0,limit);
					backbordRealDao.batchAddUpdateInfo(baseId, infoTmp);
					info = info.substring(infoTmp.length());
				}
				if(info.length() > 0 && info.length() < limit){
					backbordRealDao.batchAddUpdateInfo(baseId, info);
				}
			}
		}
	}

	private BackbordBo getVenderType(Long instanceId) {
		NodeBo nb = ndao.getByInstanceId(instanceId);
		if (nb==null) {
			try {
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId.longValue());
				String showName = ri.getShowName();
				String showIP = ri.getShowIP();
				String[] oid = ri.getModulePropBykey("sysObjectID");
				DeviceType deviceType = capacityService.getDeviceType(oid[0]);
				DeviceTypeEnum type = deviceType.getType();
				BackbordBo bo = this.getVenderType(nb);
				bo.setIp(showIP);
				bo.setName(showName);
				bo.setVenderName(deviceType.getVendorName());
				switch (type) {
				case L2Switch:
					bo.setTypeName("二层交换机");
					break;
				case Router:
					bo.setTypeName("路由器");
					break;
				case RouterSwitch:
					bo.setTypeName("三层交换机");
					break;
				case Wireless:
					bo.setTypeName("无线AC");
					break;
				case WirelessAC:
					bo.setTypeName("无线AC");
					break;
				case Firewall:
					bo.setTypeName("防火墙");
					break;
				case LoadBalancer:
					bo.setTypeName("负载均衡");
					break;
				case WirelessAP:
					bo.setTypeName("无线AP");
					break;
				case Server:
					bo.setTypeName("服务器");
					break;
				case Host:
					bo.setTypeName("主机");
					break;
				case Printer:
					bo.setTypeName("打印机");
					break;
				case Copier:
					bo.setTypeName("复印机");
					break;
				case VideoDevice:
					bo.setTypeName("视频设备");
					break;
				case TrafficManage:
					bo.setTypeName("流量控制");
					break;
				case UPS:
					bo.setTypeName("UPS");
					break;
				case AirConditioner:
					bo.setTypeName("空调");
					break;
				
				default:
					bo.setTypeName("未知");
					break;
				}
				return bo;
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		return this.getVenderType(nb);
	}

	
	public BackbordBo getBackboardBoByInstanceId(Long instanceId){
		//1.根据instanceId查询实时背板信息
		BackbordBo info = backbordRealDao.getBackbordRealInfo(instanceId);
//		if(null == info){
			//2.无实时数据，先根据厂商+型号查询默认数据
			info = this.getVenderType(instanceId);
			//3.根据instanceId查询[拓扑发现]的背板接口信息,并替换默认-发现数据（替换默认数据格式，匹配前端）
			this.getRemoteBackbordData(instanceId,info);
//		}else{
//			BackbordBo boTmp = this.getVenderType(instanceId);
//			info = boTmp;
//			this.getRemoteBackbordData(instanceId,info);
//			info.setIp(boTmp.getIp());
//			info.setTypeName(boTmp.getTypeName());
//			info.setVenderName(boTmp.getVenderName());
//			info.setOsType(boTmp.getOsType());
//			info.setSeries(boTmp.getSeries());
//			info.setName(boTmp.getName());
//		}
		return info;
	}
	
	/**
	 * 查询设备背板信息
	 * 拥有背板的设备：现在支持路由器，交换机，无线控制器AC，防火墙，负载均衡设备
	 * 1.根据instanceId查询实时背板信息
	 * 2.无实时数据，先根据厂商+型号查询默认数据
	 * 3.根据instanceId查询[拓扑发现]的背板接口信息,并替换默认-发现数据
	 * @param instanceId
	 * @return
	 */
	@Override
	public String getBackbordBo(Long instanceId) {
		BackbordBo bo = this.getBackboardBoByInstanceId(instanceId);
		JSONObject retn = (JSONObject)JSON.toJSON(bo);
		return retn.toJSONString();
	}
	
	/**
	 * 根据instanceId查询[拓扑发现]的背板接口信息,并替换默认-发现数据
	 * @param instanceId
	 * @param backbordBo
	 * @return
	 * TODO:这种魔鬼代码也不是我想写的，实属无奈啊~~o(>_<)o ~~
	 */
	private BackbordBo getRemoteBackbordData(Long instanceId,BackbordBo backbordBo){
		//TODO:如果没有查到就会是默认的default，但是接口数量还得取实际的个数，这个需要修改
		backbordBo.setVendor("default");
		backbordBo.setType("default");
//		backbordBo.setInfo(backbordBo.getInfo().replace("devicebg1.gif", "devicebg1-1.gif"));	//默认没有电源
//		if("default".equals(backbordBo.getVendor())){
//			backbordBo.setInfo(backbordBo.getInfo().replace("devicebg1.gif", "devicebg1-1.gif"));	//默认没有电源
//			return backbordBo;
//		}
		
		try {
			//获取资源实例信息
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			
			//子资源(接口),如果没有子资源可能有问题（bug，因为链路也是一个实例，但是是它没有子资源的）
			int portpPositionX = 5;	//第一个接口初始化位置
			if(null != instance.getChildren()){
				int portCount = 0;
				int powerCount = 0;
				JSONArray powers = new JSONArray();	//新电源列表
				JSONArray ports = new JSONArray();	//新接口列表
				JSONObject info = (JSONObject) JSONObject.parse(backbordBo.getInfo());
				JSONObject portJson = (JSONObject) JSONObject.toJSON(info.getJSONArray("ports").get(0));
				String onImg = portJson.getString("onImg");
				
				for(ResourceInstance ri:instance.getChildren()){
					String childType = ri.getChildType();
					//转换电源：Power
					if("Power".equals(childType)){
						if(!"default".equals(backbordBo.getVendor())){
							String[] powerIndexVals = ri.getModulePropBykey("powerIndex");
							if(null != powerIndexVals){
								for(int i=0;i<powerIndexVals.length;i++){
									powerCount ++;
									JSONObject power = this.parsePower(Integer.parseInt(powerIndexVals[i]));
									powers.add(power);
								}
							}
						}
					}
					
					//转换接口：NetInterface
					if("NetInterface".equals(childType)){
						String[] ifIndexVals = ri.getModulePropBykey("ifIndex");
						String[] ifTypeVals = ri.getModulePropBykey("ifType");
						if(ifTypeVals[0].equals("ethernetCsmacd") || ifTypeVals[0].equals("gigabitEthernet") || ifTypeVals[0].equals("fibreChannel")){
							portCount ++;
							if(null != ifIndexVals){
								for(String index:ifIndexVals){
									if(1 == portCount%24) portpPositionX = 5;	//接口换行
									portpPositionX += 23;
									JSONObject port = this.parsePort(portCount,Integer.valueOf(index), portpPositionX, onImg);
									ports.add(port);
								}
							}
						}
					}
				}
				
				//转换背景：background
				JSONObject background = (JSONObject) JSONObject.toJSON(info.getJSONArray("backgrounds").get(0));
				background = this.parseBackground(powerCount,portCount,background);
				
				//默认面板背景上已有电源，故不再添加
				if(background.getString("imgUrl").contains("devicebg")) powers = new JSONArray();
				
				info.put("powers", powers);
				info.put("ports", ports);
				backbordBo.setInfo(info.toJSONString());
				
				if("default".equals(backbordBo.getVendor())){
					backbordBo.setInfo(backbordBo.getInfo().replace("devicebg1.gif", "devicebg1-1.gif"));	//默认没有电源
				}
			}
		} catch (InstancelibException e) {
			logger.error("背板-获取资源实例信息出错!");
		}
		
		return backbordBo;
	}
	
	/**
	 * 转换接口数据
	 * @param portCount
	 * @param index
	 * @param portpPositionX
	 * @param imgUrl
	 * @return
	 */
	private JSONObject parsePort(int portCount,int index,int portpPositionX,String imgUrl){
		JSONObject port = new JSONObject();
		port.put("index", index);
		port.put("x", portpPositionX);
		port.put("cx", 0);
		port.put("y", 51);
		port.put("cy", 0);
		port.put("angle", 0);
		port.put("type", "image");
		port.put("width", 25);
		port.put("height", 20);
		port.put("imgUrl", imgUrl);
		 if(24 < portCount && portCount <= 48){
			 port.put("x", portpPositionX);
			 port.put("y", 51+110);
		}else if(48 < portCount && portCount <= 72){
			port.put("x", portpPositionX);
			port.put("y", 51+110+110);
		}else if(72 < portCount && portCount <= 96){
			port.put("x", portpPositionX);
			port.put("y", 51+110+110+110);
		}else if(96 < portCount && portCount <= 120){
			port.put("x", portpPositionX);
			port.put("y", 51+110+110+110+110);
		}
		return port;
	}
	
	/**
	 * 转换电源数据
	 * @param index
	 * @return
	 */
	private JSONObject parsePower(int index){
		JSONObject power = new JSONObject();
		power.put("index", index);
		power.put("imgUrl", "themes/blue/images/topo/dianyuan.jpg");
		power.put("x", 610);
		power.put("cx", 0);
		power.put("y", 51);
		power.put("cy", 0);
		power.put("angle", 0);
		power.put("type", "image");
		power.put("width", 110);
		power.put("height", 80);
		return power;
	}
	
	/**
	 * 转换背景数据
	 * @paramportCountt
	 * @param portCount
	 * @param background
	 * @return
	 */
	private JSONObject parseBackground(int powerCount,int portCount,JSONObject background){
		background.put("x", 3);
		background.put("cx", 353);
		background.put("y", -1);
		background.put("cy", 49);
		background.put("width", 700);
		background.put("height", 100);
		background.put("angle", 0);
		background.put("type", "image");
		
		String defaultImg = "devicebg1.gif";
		String imgUrl = background.getString("imgUrl");
		if(portCount <= 24 && powerCount == 0){	//无电源
			if(imgUrl.contains(defaultImg)){
				imgUrl = imgUrl.replace(defaultImg, "devicebg1-1.gif");
			}
		}else if(24 < portCount && portCount <= 48){
			if(imgUrl.contains(defaultImg)){
				imgUrl = imgUrl.replace(defaultImg, "devicebg2.gif");
			}
			background.put("height", 100+110);
		}else if(48 < portCount && portCount <= 72){
			if(imgUrl.contains(defaultImg)){
				imgUrl = imgUrl.replace(defaultImg, "devicebg3.gif");
			}
			background.put("height", 100+110+110);
		}else if(72 < portCount && portCount <= 96){
			if(imgUrl.contains(defaultImg)){
				imgUrl = imgUrl.replace(defaultImg, "devicebg4.gif");
			}
			background.put("height", 100+110+110+110);
		}else if(96 < portCount && portCount <= 120){
			if(imgUrl.contains(defaultImg)){
				imgUrl = imgUrl.replace(defaultImg, "devicebg5.gif");
			}
			background.put("height", 100+110+110+110+110);
		}
		background.put("imgUrl", imgUrl);
		return background;
	}
	
	/**
	 * 说明：根据insatanceId查询出背板默认数据
	 * @param instanceId
	 * @return
	 */
	private BackbordBo getVenderType(NodeBo nodeBo){
		BackbordBo bo = null;
		DeviceType deviceType = null;
		if(null != nodeBo) {
			//1. 根据oid调用接口查询厂商+型号
			deviceType = capacityService.getDeviceType(nodeBo.getOid());
			
			//2. 根据厂商+型号查询默认数据
			if(null != deviceType && StringUtils.isNotBlank(deviceType.getVendorNameEn()) && StringUtils.isNotBlank(deviceType.getModelNumber())){
				String vendorName = deviceType.getVendorNameEn().replaceAll(" ", "").toLowerCase();
				String moduleNumber = deviceType.getModelNumber().replaceAll(" ", "").toLowerCase();
				//根据厂商查询列表
				List<BackbordBo> backbords = backbordBaseDao.getBackbordByVendor(vendorName);
				if(null == backbords) logger.error("系统暂时没有该厂商["+vendorName+"]的数据！");
				//在厂商列表搜索匹配型号
				for(BackbordBo backbord:backbords){
					String type = backbord.getType().toLowerCase();
					if(type.contains(moduleNumber) || moduleNumber.contains(type)){
						/*
						 * TODO:该判断可能导致数据不匹配问题，如果完全匹配查询，则导致很多面板匹配不上，都是因为3.5和4.0面板厂商和设备名称不对应，没有唯一的标识,
						 * 3.5面板配置xml的sysoid没有值，故无法记录唯一性，很难匹配上
						*/
						System.out.println("厂商："+vendorName+" ,型号： "+moduleNumber+", 系统型号"+type);
						bo = backbord;
						break;
					}
				}
			}
		}
		
		//3. 如果没有匹配的模型，查询系统默认背板
		if(null == bo){
			bo = backbordBaseDao.getBackbordBaseInfo("default", "default");
		}
		
		//封装页面显示用的数据
		if(null != nodeBo){
			bo.setIp(nodeBo.getIp());
			bo.setTypeName(nodeBo.getTypeName());
		}
		
		if(null!=deviceType){
			bo.setVenderName(deviceType.getVendorName());
			bo.setOsType(deviceType.getOsType());
			bo.setSeries(deviceType.getSeries());
			try {
				ResourceInstance res = resourceInstanceService.getResourceInstance(nodeBo.getInstanceId());
				bo.setName(res!=null?res.getName():"未知");
			} catch (InstancelibException e) {
				logger.error("获取背板实例异常",e);
			}
		}
		
		return bo;
	}

	@Override
	public String categoryDeviceInfo(Set<Long> domainSet) {
		Map<String,List<NodeBo>> retn = new HashMap<String,List<NodeBo>>();
		List<NodeBo> nodes = ndao.getBySubTopoId(null);
		
		//查询出登录用户所属域的资源，并过滤
		List<Long> domainInstanceIds = resourceExApi.getResourceIdsByDomainId(domainSet);
		
		//手动分组
		for(NodeBo nb:nodes){
			String key = nb.getType().toLowerCase();
			//主机设备没有背板信息，需过滤掉
			if("server".equals(key) || "host".equals(key) || null == nb.getInstanceId())continue;
			
			//按照用户域过滤设备
			Long instanceId = nb.getInstanceId();
			if(null != instanceId && !domainInstanceIds.contains(instanceId)) continue;
			
			List<NodeBo> tmp = retn.get(key);
			if(null == tmp){
				tmp = new ArrayList<NodeBo>();
				retn.put(key, tmp);
			}
			tmp.add(nb);
		}
		return JSON.toJSONString(retn);
	}
	
	public void setBackbordRealDao(IBackbordRealDao backbordRealDao) {
		this.backbordRealDao = backbordRealDao;
	}

	public void setBackbordBaseDao(IBackbordBaseDao backbordBaseDao) {
		this.backbordBaseDao = backbordBaseDao;
	}

	@Override
	public JSONArray getAllIfs(Long instanceId) {
		if(null!=instanceId){
			return thirdSvc.getInstancesIfs(instanceId,false);
		}else{
			return new JSONArray();
		}
	}

    @Override
    public JSONArray getAllIfs2(Long instanceId) {
        if (null != instanceId) {
            return thirdSvc.getInstancesIfs2(instanceId, false);
        } else {
            return new JSONArray();
        }
    }

    @Override
	public String getDetailInfo(Long instanceId) {
		JSONObject retn = new JSONObject();
		try {
			ResourceInstance res = resourceInstanceService.getResourceInstance(instanceId);
			if(null!=res){
				retn.put("ifIndex", res.getModulePropBykey("ifIndex")[0]);
				retn.put("name", res.getName());
//				retn.put("port", res.getModulePropBykey("port")[0]);
//				retn.put("ip", res.getModulePropBykey("ip")[0]);
				retn.put("ifSpeed", res.getModulePropBykey("ifSpeed")[0]);
				retn.put("wholeStatus", thirdSvc.getInstanceState(instanceId));
//				retn.put("bandUseRatio", thirdSvc.getp);
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		return retn.toJSONString();
	}
	@Override
	public JSONObject getPortTipInfo(Long instanceId) {
		JSONObject retn = new JSONObject();
		ResourceInstance portInstance = dataHelper.getResourceInstance(instanceId);
		//retn.put("index", dataHelper.getPortIfIndex(instanceId));
		retn.put("name", dataHelper.getResourceInstanceIfName(portInstance));
		retn.put("type", dataHelper.getPortType(instanceId));
		retn.put("bandWidth",dataHelper.getPortBandWidth(instanceId,"Mbps"));
		retn.put("mac", dataHelper.getResourceInstanceIfMac(instanceId));
		retn.put("manageState",dataHelper.getResourceInstanceManageState(instanceId));
		retn.put("operatorState", dataHelper.getResourceInstanceOperateState(instanceId));
		retn.put("bandWidthRatio", dataHelper.getPortBandWidthRatio(instanceId));
		retn.put("recevieSpeed", dataHelper.getPortReceiveSpeed(instanceId, "Mbps"));
		retn.put("sendSpeed", dataHelper.getPortSendSpeed(instanceId, "Mbps"));
		retn.put("recevieBandWidthRatio", dataHelper.getPortReceiveRatio(instanceId));
		retn.put("sendBandWidthRatio", dataHelper.getPortSendRatio(instanceId));
		retn.put("portState", dataHelper.getResourceInstanceState(portInstance));
		return retn;
	}
	@Override
	public JSONObject downDeviceInfo(Long mainInstanceId, Long subInstanceId) {
		JSONObject retn = new JSONObject();
		ResourceInstance resourceInstance = dataHelper.getResourceInstance(mainInstanceId);
		retn.put("ip", dataHelper.getResourceInstanceManageIp(resourceInstance));
		retn.put("IfName",dataHelper.getPortName(subInstanceId));
		retn.put("deviceShowName", dataHelper.getResourceInstanceShowName(resourceInstance));
		//retn.put("ifIndex", dataHelper.getPortIfIndex(subInstanceId));
		return retn;
	}
}
