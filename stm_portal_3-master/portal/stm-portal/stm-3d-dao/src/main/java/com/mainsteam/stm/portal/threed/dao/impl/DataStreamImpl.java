package com.mainsteam.stm.portal.threed.dao.impl;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.IUrlDao;
import com.mainsteam.stm.portal.threed.util.jaxb.Metric;
import com.mainsteam.stm.portal.threed.xfire.client.DataStreamPortType;
import com.mainsteam.stm.portal.threed.xfire.client.DataStreamService;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;
import com.mainsteam.stm.util.DateUtil;
/**
 * 
 * <li>文件名称: DataStreamImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 3D机房webservice调用处理类</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   liupeng
 */
public class DataStreamImpl{
	private static final Log log = LogFactory.getLog(DataStreamImpl.class);
	
	public static final String METHOD_INVOKE_FAIL = "webservice方法调用失败!";
	public static final String ESCAPE_START = "\"";
	public static final String ESCAPE_END = "\",";
	
	private IUrlDao urlDao;
	
	public void setUrlDao(IUrlDao urlDao) {
		this.urlDao = urlDao;
	}
	/**
	 * 从机柜中移除设备
	 * @param ids
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByArray(long[] ids) throws ThreeDException{
		DataStreamPortType impl = getDataStream();
		StringBuffer buffer = new StringBuffer("[");
		for(long id :ids){
			buffer.append(ESCAPE_START).append(id).append(ESCAPE_END);
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		try{
			String resultStr = impl.deleteRackEquipment(str);
			BaseResult result = JSON.parseObject(resultStr, BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 从机柜中移除设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByList(List<CabinetBo> boList) throws ThreeDException{
		DataStreamPortType impl = getDataStream();
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo :boList){
			buffer.append(ESCAPE_START).append(bo.getId()).append(ESCAPE_END);
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		try{
			String resultStr = impl.deleteRackEquipment(str);
			BaseResult result = JSON.parseObject(resultStr, BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 得到机柜结构
	 * @return
	 * @throws ThreeDException
	 */
	public String getNodeTree() throws ThreeDException {
		DataStreamPortType impl = getDataStream();
		try{
			String nodeTree = impl.getNodeAppendTypeForTree();
			return nodeTree;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 得到产品库信息
	 * @return
	 * @throws ThreeDException
	 */
	public String getProductInfo() throws ThreeDException {
		DataStreamPortType impl = getDataStream();
		try{
			String productInfo = impl.getProductInfo();
			return productInfo;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 添加设备到机柜
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult addRackEquipment(List<CabinetBo> boList) throws ThreeDException {
		DataStreamPortType impl = getDataStream();
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo : boList){
			StringBuffer json = toJson(bo);
			buffer.append(json).append(",");
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		try{
			String resultStr = impl.addRackEquipment(str);
			BaseResult result = JSON.parseObject(resultStr,BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 更新单个架势设备
	 * @param bo
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(CabinetBo bo) throws ThreeDException {
		DataStreamPortType impl = getDataStream();
		String json = toJson(bo).toString();
		try{
			String str = impl.updateRackEquipment(json);
			BaseResult result = JSON.parseObject(str, BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 推送监控数据
	 * @param instanceMetrics
	 * @param metricList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushMonitor(List<Map<String, ?>> instanceMetrics,List<Metric> metricList) throws ThreeDException{
		DataStreamPortType impl = getDataStream();
		StringBuffer instanceBuff = new StringBuffer();
		for(Map<String, ?> map : instanceMetrics){
			//监控设备ID
			instanceBuff.append(ESCAPE_START).append(map.get("instanceid")).append("\":{");
			StringBuffer metricBuff = new StringBuffer();
			for(Metric metric : metricList){
				//监控关键字
				metricBuff.append(ESCAPE_START).append(metric.getName()).append("\":{")
					  .append("\"group\":\"监控组\",\"type\":\"数值\",")
					  .append("\"unit\":").append(ESCAPE_START).append(metric.getUnit()).append(ESCAPE_END)
					  .append("\"value\":").append(ESCAPE_START).append(map.get(metric.getId()))
					  .append("\"},");
			}
			instanceBuff.append(metricBuff.substring(0, metricBuff.length()-1)).append("},");
		}
		String pushData = "{\"_KEY_\":\"BMC\",\"_DATA_\":{"+instanceBuff.substring(0, instanceBuff.length()-1)+"}}";
		try{
			String str = impl.pushMonitor1(pushData,true);
			BaseResult result = JSON.parseObject(str,BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 推送告警数据
	 * @param event
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushAlarm(AlarmEvent event) throws ThreeDException{
		DataStreamPortType impl = getDataStream();
		StringBuffer alarmBuff = new StringBuffer();
		alarmBuff.append("{\"_KEY_\":\"BMC\",\"_DATA_\":{")
					.append(ESCAPE_START).append(event.getSourceID()).append("\":{")
					.append(ESCAPE_START).append("报警").append("\":{")
					.append("\"报警级别\":").append(ESCAPE_START).append(getLevelCN(event.getLevel())).append(ESCAPE_END)
					.append("\"报警时间\":").append(ESCAPE_START)
					.append(DateUtil.format(event.getCollectionTime(), DateUtil.DEFAULT_DATETIME_FORMAT)).append(ESCAPE_END)
					.append("\"报警信息\":").append(ESCAPE_START).append(event.getContent()).append("\"")
					.append("}}}}");
		try{
			String str = impl.pushAlarm1(alarmBuff.toString(), true);
			BaseResult result = JSON.parseObject(str,BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	private String getLevelCN(InstanceStateEnum state){
		return InstanceStateEnum.getValue(state);
//		if(state.equals(InstanceStateEnum.CRITICAL)){
//			return "致命";
//		}else if(state.equals(InstanceStateEnum.SERIOUS)){
//			return "严重";
//		}else if(state.equals(InstanceStateEnum.NORMAL)){
//			return "正常";
//		}else if(state.equals(InstanceStateEnum.WARN)){
//			return "警告";
//		}else if(state.equals(InstanceStateEnum.UNKOWN)){
//			return "正常";
//		}else{
//			return "正常";
//		}
	}
	/**
	 * 关闭告警
	 * @param event
	 * @return
	 * @throws ThreeDException 
	 */
	public BaseResult closeAlarm(AlarmEvent event) throws ThreeDException{
		DataStreamPortType impl = getDataStream();
		StringBuffer alarmBuff = new StringBuffer();
		alarmBuff.append("{\"_KEY_\":\"BMC\",\"_DATA_\":{\"").append(event.getSourceID()).append("\":{}}}");
		try{
			String str = impl.closeAlarm(alarmBuff.toString());
			BaseResult result = JSON.parseObject(str,BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 批量更新架势设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(List<CabinetBo> boList) throws ThreeDException {
		DataStreamPortType impl = getDataStream();
		StringBuffer buffer = new StringBuffer("[");
		for(CabinetBo bo : boList){
			StringBuffer json = toJson(bo);
			buffer.append(json).append(",");
		}
		String str = buffer.substring(0, buffer.length()-1)+"]";
		try{
			String resultStr = impl.updateRackEquipment(str);
			BaseResult result = JSON.parseObject(resultStr, BaseResult.class);
			return result;
		}catch(Exception e){
			throw new ThreeDException(METHOD_INVOKE_FAIL, e);
		}
	}
	/**
	 * 每次获取，保证实时性
	 * @return
	 * @throws ThreeDException
	 */
	private DataStreamPortType getDataStream() throws ThreeDException{
		UrlBo bo = urlDao.get3DInfo();
		if(bo==null) throw new ThreeDException("系统还未集成3D");
		if(!"1".equals(bo.getStatus())) throw new ThreeDException("3D机房集成已关闭");
		if(StringUtils.isEmpty(bo.getIp())) throw new ThreeDException("3D机房IP不合法");
		String endpoint = "http://"+bo.getIp()+":"+bo.getPort()+bo.getWebservicePath();
		try{
			URL wsdlDocumentLocation = new URL(endpoint);
			DataStreamService service = new DataStreamService(wsdlDocumentLocation);
			DataStreamPortType impl = service.getDataStreamHttpPort();
			return impl;
		}catch(Exception e){
			log.error("3D客户端调用失败", e);
			throw new ThreeDException("3D客户端调用失败",e);
		}
	}
	/**
	 * 将javaBean转化为3D需要的json格式
	 * @param bo
	 * @return
	 */
	private StringBuffer toJson(CabinetBo bo){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{")
			  .append("\"推送类型\":").append(ESCAPE_START).append("架式设备").append(ESCAPE_END)
			  .append("\"编号\":").append(ESCAPE_START).append(bo.getId()).append(ESCAPE_END)
			  .append("\"名称\":").append(ESCAPE_START).append(bo.getName()).append(ESCAPE_END)
			  .append("\"所属\":").append(ESCAPE_START).append(bo.getBelong()).append(ESCAPE_END)
			  .append("\"U位\":").append(ESCAPE_START).append(bo.getUplace()).append(ESCAPE_END)
			  .append("\"统计分类2\":").append(ESCAPE_START).append(bo.getUplace()).append(ESCAPE_END)
			  .append("\"布局\":").append(ESCAPE_START).append(bo.getLayout()).append(ESCAPE_END)
			  .append("\"设备型号\":").append(ESCAPE_START).append(bo.getModel()).append(ESCAPE_END)
			  .append("\"型号系列/版本号\":").append(ESCAPE_START).append(bo.getModel())
			  .append("\"}");
		return buffer;
	}
}
