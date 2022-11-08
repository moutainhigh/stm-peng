package com.mainsteam.stm.portal.threed.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.util.jaxb.Metric;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;

public class Adapter3DInterfaceImpl {

	private static final Log log = LogFactory.getLog(Adapter3DInterfaceImpl.class);
	
	@Resource
	private DataStreamImpl dataStreamImpl;
	
	@Resource
	private Uninova3DInterfaceImpl uninova3DInterfaceImpl;
	
	private final static String versionOne = "2.24";
	private final static String versionTwo = "3.0";
	private static String version = versionTwo;
	/**
	 * 从机柜中移除设备
	 * @param ids
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByArray(long[] ids) throws Exception{
		switch(version){
			/*case versionTwo:
				return uninova3DInterfaceImpl.deleteRackEquipmentByArray(ids);
			case versionOne:*/
			default :
				return dataStreamImpl.deleteRackEquipmentByArray(ids);		
		}
	}
	
	/**
	 * 从机柜中移除设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult deleteRackEquipmentByList(List<CabinetBo> boList) throws Exception{
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.deleteRackEquipmentByList(boList);
		case versionOne:*/
		default :
			return dataStreamImpl.deleteRackEquipmentByList(boList);
		}
	}
	
	/**
	 * 得到机柜结构
	 * @return
	 * @throws ThreeDException
	 */
	public String getNodeTree() throws Exception {
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.getNodeTree();
//			uninova3DInterfaceImpl.test();
//			 return null;
		case versionOne:*/
		default :
			return dataStreamImpl.getNodeTree();
		}
	}
	
	/**
	 * 得到产品库信息
	 * @return
	 * @throws ThreeDException
	 */
	public String getProductInfo() throws Exception {
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.getProductInfo();
		case versionOne:*/
		default :
			return dataStreamImpl.getProductInfo();
		}
	}
	
	/**
	 * 添加设备到机柜
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult addRackEquipment(List<CabinetBo> boList) throws Exception {
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.addRackEquipment(boList);
		case versionOne:*/
		default :
			return dataStreamImpl.addRackEquipment(boList);
		}
	}
	
	/**
	 * 更新单个架势设备
	 * @param bo
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(CabinetBo bo) throws Exception {
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.updateRackEquipment(bo);
		case versionOne:*/
		default :
			return dataStreamImpl.updateRackEquipment(bo);
		}
	}
	
	/**
	 * 推送监控数据
	 * @param instanceMetrics
	 * @param metricList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushMonitor(List<Map<String, ?>> instanceMetrics,List<Metric> metricList) throws Exception{
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.pushMonitor(instanceMetrics, metricList);
		case versionOne:*/
		default :
			return dataStreamImpl.pushMonitor(instanceMetrics, metricList);
		}
	}
	
	/**
	 * 推送告警数据
	 * @param event
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult pushAlarm(AlarmEvent event) throws Exception{
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.pushAlarm(event);
		case versionOne:*/
		default :
			return dataStreamImpl.pushAlarm(event);
		}
	}
	
	/**
	 * 关闭告警
	 * @param event
	 * @return
	 * @throws ThreeDException 
	 */
	public BaseResult closeAlarm(AlarmEvent event) throws Exception{
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.closeAlarm(event);
		case versionOne:*/
		default :
			return dataStreamImpl.closeAlarm(event);
		}
	}
	
	/**
	 * 批量更新架势设备
	 * @param boList
	 * @return
	 * @throws ThreeDException
	 */
	public BaseResult updateRackEquipment(List<CabinetBo> boList) throws Exception {
		switch(version){
		/*case versionTwo:
			return uninova3DInterfaceImpl.updateRackEquipment(boList);
		case versionOne:*/
		default :
			return dataStreamImpl.updateRackEquipment(boList);
		}
	}
	
}
