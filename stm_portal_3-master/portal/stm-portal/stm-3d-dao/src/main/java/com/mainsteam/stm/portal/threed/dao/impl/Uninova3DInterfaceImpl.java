package com.mainsteam.stm.portal.threed.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.IUrlDao;
import com.mainsteam.stm.portal.threed.util.jaxb.Metric;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class Uninova3DInterfaceImpl {

	private static final Log log = LogFactory.getLog(Uninova3DInterfaceImpl.class);
	
	private IUrlDao urlDao;
	
	
	private final static String ESCAPE_START = "\"";
	private final static String ESCAPE_END = "\",";
	private final static String deleteRackEquipment = "deleteRackEquipment";
	private final static String addRackEquipment = "addRackEquipment";
	private final static String updateRackEquipment  = "updateRackEquipment";
	private final static String pushMonitor  = "pushMonitor";
	private final static String pushAlarm  = "pushAlarm";
	private final static String getNodeAppendTypeForTree = "getNodeAppendTypeForTree";
	private final static String getProductInfo = "getProductInfo";
	
	private static String scene = null;
	private static String scid = null;
	
	private static void loadPropertiesValue(){
		try{Properties pp=PropertiesFileUtil.getProperties("config/threeD.properties");
		
		String scidP=pp.getProperty("3d.scid");
		String sceneP=pp.getProperty("3d.scene");
		if(!StringUtils.isEmpty(scidP)){
			scid = scidP;
		}
		if(!StringUtils.isEmpty(sceneP)){
			scene = sceneP;
		}}
		catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	/**
	 * 从机柜中移除设备
	 * @param ids
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByArray(long[] ids) throws Exception{
		String methodName= deleteRackEquipment;
		Object[] objects = new Object[1];
		
		StringBuffer buffer = new StringBuffer("[");
		for(long id :ids){
			buffer.append(ESCAPE_START).append(id).append(ESCAPE_END);
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		log.debug("deleteRackEquipmentByArray---"+str);
		objects[0] = str;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		return result;
	}
	
	
	
	/**
	 * 从机柜中移除设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByList(List<CabinetBo> boList) throws Exception{
		String methodName= deleteRackEquipment;
		Object[] objects = new Object[1];
		
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo :boList){
			buffer.append(ESCAPE_START).append(bo.getId()).append(ESCAPE_END);
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		log.debug("deleteRackEquipmentByList---"+str);
		objects[0] = str;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		return result;
	}
	
	/**
	 * 得到机柜结构
	 * @return
	 * @throws ThreeDException
	 */
	public String getNodeTree() throws Exception {
		if(StringUtils.isEmpty(scid)){
			loadPropertiesValue();
		}
		String methodName= getNodeAppendTypeForTree;
		Object[] objects = new Object[1];
		objects[0] = scid;
		log.debug("getNodeTree---"+scid);
		return processWebserviceMethod(methodName,objects);
	}
	
	/**
	 * 得到产品库信息
	 * @return
	 * @throws ThreeDException
	 */
	public String getProductInfo() throws Exception {
		/*if(StringUtils.isEmpty(scid)){
			loadPropertiesValue();
		}*/
		String methodName= getProductInfo;
		Object[] objects = new Object[1];
		objects[0] = scid;
		log.debug("getNodeTree---"+scid);
		
		return processWebserviceMethod(methodName,objects);
	}
	
	/**
	 * 添加设备到机柜
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult addRackEquipment(List<CabinetBo> boList) throws Exception {
		String methodName= addRackEquipment;
		Object[] objects = new Object[1];
		
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo : boList){
			StringBuffer json = toJson(bo);
			buffer.append(json).append(",");
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		log.debug("addRackEquipment---"+str);
		objects[0] = str;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	
	
	/**
	 * 更新单个架势设备
	 * @param bo
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(CabinetBo bo) throws Exception {
		String methodName= updateRackEquipment;
		Object[] objects = new Object[1];
		
		String json = toJson(bo).toString();
		log.debug("updateRackEquipment---"+json);
		objects[0] = json;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	
	/**
	 * 推送监控数据
	 * @param instanceMetrics
	 * @param metricList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushMonitor(List<Map<String, ?>> instanceMetrics,List<Metric> metricList) throws Exception{
		String methodName= pushMonitor;
		Object[] objects = new Object[1];
		if(StringUtils.isEmpty(scene)){
			loadPropertiesValue();
		}
		
		StringBuffer instanceBuff = new StringBuffer();
		for(Map<String, ?> map : instanceMetrics){
			//监控设备ID
			instanceBuff.append("{").append("\"key\":\"_\",\"type\":\"数值\",")
			.append("\"id\":").append(ESCAPE_START).append(map.get("id")).append(ESCAPE_END)
			.append("\"scene\":").append(ESCAPE_START).append(scene).append(ESCAPE_END);
			for(Metric metric : metricList){
				String metricId = metric.getId();
				String timeKey = metricId+ "CollectTime";
				
				String valueStr = "_";
				String timeStr = "_";
				if(map.containsKey(metricId)){
					valueStr = map.get(metricId).toString();
					
					if(map.containsKey(timeKey)){
						Date date = (Date)map.get(timeKey);
						timeStr = String.valueOf(date.getTime());
					}
				}
				
				instanceBuff.append("\"unit\":").append(ESCAPE_START).append(metric.getUnit()).append(ESCAPE_END)
				.append("\"param\":").append(ESCAPE_START).append(map.get(metric.getName())).append(ESCAPE_END)
				.append("\"app\":").append(ESCAPE_START).append("_").append(ESCAPE_END)
				.append("\"inst\":").append(ESCAPE_START).append("_").append(ESCAPE_END)
				.append("\"time\":").append(ESCAPE_START).append(timeStr).append(ESCAPE_END)
				.append("\"val\":").append(ESCAPE_START).append(valueStr).append("\"");
			}
			instanceBuff.append("},");
		}
		String pushData = "["+instanceBuff.substring(0, instanceBuff.length()-1)+"]";
		log.debug("pushMonitor---"+pushData);
		objects[0] = pushData;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	
	/**
	 * 推送告警数据
	 * @param event
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushAlarm(AlarmEvent event) throws Exception{
		String methodName= pushAlarm;
		Object[] objects = new Object[1];
		String type = "OPEN";
		if(StringUtils.isEmpty(scene)){
			loadPropertiesValue();
		}
		
		StringBuffer instanceBuff = new StringBuffer();
		instanceBuff.append("[{").append("\"key\":\"_\",")
		.append("\"id\":").append(ESCAPE_START).append(event.getSourceID()).append(ESCAPE_END)
		.append("\"scene\":").append(ESCAPE_START).append(scene).append(ESCAPE_END)
		.append("\"title\":").append(ESCAPE_START).append("告警").append(ESCAPE_END)
		.append("\"status\":").append(ESCAPE_START).append(type).append(ESCAPE_END)
		.append("\"severity\":").append(ESCAPE_START).append("_").append(ESCAPE_END)//level
		.append("\"msg\":").append(ESCAPE_START).append(event.getContent()).append(ESCAPE_END)
		.append("\"modifyTime\":").append(ESCAPE_START).append("_").append(ESCAPE_END)
		.append("\"time\":").append(ESCAPE_START)
		.append(event.getCollectionTime().getTime()).append("\"");
		
		instanceBuff.append("}]");
		String pushData = instanceBuff.toString();
		log.debug("pushAlarm---"+pushData);
		objects[0] = pushData;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	
	/**
	 * 关闭告警
	 * @param event
	 * @return
	 * @throws ThreeDException 
	 */
	public BaseResult closeAlarm(AlarmEvent event) throws Exception{
		String methodName= pushAlarm;
		Object[] objects = new Object[1];
		String type = "CLOSED";
		if(StringUtils.isEmpty(scene)){
			loadPropertiesValue();
		}
		
		StringBuffer instanceBuff = new StringBuffer();
		instanceBuff.append("[{").append("\"key\":\"_\",")
		.append("\"id\":").append(ESCAPE_START).append(event.getSourceID()).append(ESCAPE_END)
		.append("\"scene\":").append(ESCAPE_START).append(scene).append(ESCAPE_END)
		.append("\"title\":").append(ESCAPE_START).append("关闭告警").append(ESCAPE_END)
		.append("\"status\":").append(ESCAPE_START).append(type).append(ESCAPE_END)
		.append("\"severity\":").append(ESCAPE_START).append("_").append(ESCAPE_END)//level
		.append("\"msg\":").append(ESCAPE_START).append(event.getContent()).append(ESCAPE_END)
		.append("\"modifyTime\":").append(ESCAPE_START).append("_").append(ESCAPE_END)
		.append("\"time\":").append(ESCAPE_START)
		.append(event.getCollectionTime().getTime()).append("\"");
		
		instanceBuff.append("}]");
		String pushData = instanceBuff.toString();
		log.debug("pushAlarm---"+pushData);
		objects[0] = pushData;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	
	/**
	 * 批量更新架势设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(List<CabinetBo> boList) throws Exception {
		String methodName = updateRackEquipment;
		
		Object[] objects = new Object[1];
		
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo : boList){
			StringBuffer json = toJson(bo);
			buffer.append(json).append(",");
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		log.debug("updateRackEquipment---"+str);
		objects[0] = str;
		
		BaseResult result = JSON.parseObject(processWebserviceMethod(methodName,objects), BaseResult.class);
		
		return result;
	}
	public void test(){
//		if(StringUtils.isEmpty(scene)){
//			loadPropertiesValue();
//		}
//		
//		long[] ids = new long[5];
//		ids[0] = 1;
//		ids[1] = 11;
//		ids[2] = 1111;
//		ids[3] = 11111;
//		ids[4] = 2;
//		
//		CabinetBo cb = new CabinetBo();
//		cb.setId(new Long(1));
//		cb.setBelong("belong");
//		cb.setBrand("brand");
//		cb.setLayout("layout");
//		cb.setModel("model");
//		cb.setName("name");
//		cb.setUplace("uplace");
//		CabinetBo cb1 = new CabinetBo();
//		cb1.setId(new Long(11));
//		cb1.setBelong("belong1");
//		cb1.setBrand("brand1");
//		cb1.setLayout("layout1");
//		cb1.setModel("model1");
//		cb1.setName("name1");
//		cb1.setUplace("uplace1");
//		List<CabinetBo> boList = new ArrayList<CabinetBo>();
//		boList.add(cb);
//		boList.add(cb1);
//		
//		AlarmEvent event = new AlarmEvent();
//		event.setCollectionTime(new Date());
//		event.setContent("test");
//		event.setSourceID("sourceid");
//		
//		try {
//			log.debug("deleteRackEquipmentByArray---"+deleteRackEquipmentByArray(ids));
			
//			log.debug("deleteRackEquipmentByList---"+deleteRackEquipmentByList(boList));
			
//			log.debug("getNodeTree---"+getNodeTree());
			
//			log.debug("getProductInfo---"+getProductInfo());
			
//			log.debug("addRackEquipment---"+addRackEquipment(boList));
			
//			log.debug("updateRackEquipment---"+updateRackEquipment(cb));
			
//			log.debug("pushAlarm---"+pushAlarm(event));
//			
//			log.debug("closeAlarm---"+closeAlarm(event));
//			
//			log.debug("updateRackEquipment---"+updateRackEquipment(boList));
			
//			pushMonitor
			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void main(String args[]){
		
		
		
//		String methodName = "getMetricDetailInfoByResourceId";
////		String wsdl = "http://localhost:8080/mainsteam-stm-webapp/soap/resourceService?wsdl";//getUrl();
//		String wsdl = "http://192.168.10.14/soap/resourceService?wsdl";
//		JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
//		Client client = dcf.createClient(wsdl);
//		
//		Object[] result;
//		String resultStr="";
////			result = client.invoke(opName,objects);
//		try {
//			result = client.invoke(methodName,"test");
//			if(null!=result && result.length>0){
//				 resultStr = result[0].toString();
//			 }
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	/**
	 * 每次获取，保证实时性
	 * @return
	 * @throws ThreeDException
	 */
	private String getUrl() throws ThreeDException{
		UrlBo bo = urlDao.get3DInfo();
		if(bo==null) throw new ThreeDException("系统还未集成3D");
		if(!"1".equals(bo.getStatus())) throw new ThreeDException("3D机房集成已关闭");
		if(StringUtils.isEmpty(bo.getIp())) throw new ThreeDException("3D机房IP不合法");
		String url = "http://"+bo.getIp()+":"+bo.getPort()+bo.getWebservicePath();
		return url;
	}
	
	private String processWebserviceMethod(String methodName,Object[] objects) throws Exception{
//		methodName = "getMetricDetailInfoByResourceId";
//		String wsdl = "http://localhost:8080/mainsteam-stm-webapp/soap/resourceService?wsdl";
//		String wsdl = "http://192.168.10.14/soap/resourceService?wsdl";
		String wsdl = getUrl();
		JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdl);
		
		// 下面一段处理 WebService接口和实现类namespace不同的情况  
		// CXF动态客户端在处理此问题时，会报No operation was found with the name的异常  
//		Endpoint endpoint = client.getEndpoint();  
//		QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), methodName);  
//		BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();  
//		if (bindingInfo.getOperation(opName) == null) {
//		    for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {  
//		        if (methodName.equals(operationInfo.getName().getLocalPart())) {  
//		            opName = operationInfo.getName();  
//		            break;  
//		        }
//		    }
//		}
		
		Object[] result;
		String resultStr="";
//			result = client.invoke(opName,objects);
		result = client.invoke(methodName,objects);
		if(null!=result && result.length>0){
			 resultStr = result[0].toString();
			 return resultStr;
		 }
		return resultStr;
	}
	
	/**
	 * 将javaBean转化为3D需要的json格式
	 * @param bo
	 * @return
	 */
	private static StringBuffer toJson(CabinetBo bo){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{")
			  .append("\"推送类型\":").append(ESCAPE_START).append("架式设备").append(ESCAPE_END)
			  .append("\"业务ID\":").append(ESCAPE_START).append(bo.getId()).append(ESCAPE_END)
			  .append("\"名称\":").append(ESCAPE_START).append(bo.getName()).append(ESCAPE_END)
			  .append("\"设备名称\":").append(ESCAPE_START).append(bo.getName()).append(ESCAPE_END)
			  .append("\"所属\":").append(ESCAPE_START).append(bo.getBelong()).append(ESCAPE_END)
			  .append("\"柜内位置\":").append(ESCAPE_START).append(bo.getUplace()).append(ESCAPE_END)
			  .append("\"布局\":").append(ESCAPE_START).append(bo.getLayout()).append(ESCAPE_END)
			  .append("\"设备型号\":").append(ESCAPE_START).append(bo.getModel()).append(ESCAPE_END)
			  .append("\"机柜号\":").append(ESCAPE_START).append("").append(ESCAPE_END)
			  .append("\"设备编号\":").append(ESCAPE_START).append(bo.getModel())
			  .append("\"}");
		return buffer;
	}
	
	
	
}
