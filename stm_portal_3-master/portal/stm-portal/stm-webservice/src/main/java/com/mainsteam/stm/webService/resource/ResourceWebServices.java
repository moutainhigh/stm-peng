package com.mainsteam.stm.webService.resource;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "resourceWebServices", targetNamespace = "http://www.mainsteam.com/ms/resourceService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ResourceWebServices {

	@WebMethod(action = "syncMetricThreshold")
	@WebResult
	public ResourceSyncResult syncMetricThreshold(
			@WebParam() ResourceSyncBean resourceBean);

	@WebMethod(action = "getResourceType")
	public String getResourceType();

	// 获取所有资源类型及分组信息(只包含两级类型)
	@WebMethod(action = "getAllCategory")
	public String getAllCategory();
	
	// 获取所有资源类型及分组信息(包含三级类型)
	@WebMethod(action = "getAllCategoryDetailInfo")
	public String getAllCategoryDetailInfo();
	@WebMethod(action = "getMetricDetailInfoByResourceId")
	public String getMetricDetailInfoByResourceId(@WebParam(name="resourceId")String resourceId);

	// 获取所有资源基本信息
	@WebMethod(action = "getAllResourceBaseInfo")
	public String getAllResourceBaseInfo();

	// 根据IP查询资源基本信息
	@WebMethod(action = "getResourceByResourceIp")
	public String getResourceByResourceIp(@WebParam(name="ip")String ip);

	// 根据名称模糊查询资源基本信息
	@WebMethod(action = "getResourceByResourceName")
	public String getResourceByResourceName(@WebParam(name="name")String name);

	// 根据资源ID查询资源基本信息
	@WebMethod(action = "getResourceByResourceId")
	public String getResourceByResourceId(@WebParam(name="reourceId")String resourceId);

	//
	@WebMethod(action = "getResourceByCategoryId")
	public String getResourceByCategoryId(@WebParam(name="categoryId")String categoryId);

	// 根据资源状态获取资源基本信息
	@WebMethod(action = "getResourceByResourceState")
	public String getResourceByResourceState(@WebParam(name="state")String state);

	// 获取所有已监控资源的基本信息
	@WebMethod(action = "getAllMonitoredResourceBaseInfo")
	public String getAllMonitoredResourceBaseInfo();
	
	// 根据资源实例Id获取资源的基本信息
	@WebMethod(action = "getResourceBaseInfoByInstanceId")
	public String getResourceBaseInfoByInstanceId(@WebParam(name="instanceId")long instanceId);

	// 获取所有未监控资源的基本信息
	@WebMethod(action = "getAllNotMonitoredResourceBaseInfo")
	public String getAllNotMonitoredResourceBaseInfo();

	// 根据IP获取资源详细信息
	@WebMethod(action = "getResourceDetailInfoByIp")
	public String getResourceDetailInfoByIp(@WebParam(name="ip")String ip);

	// 根据资源实例ID获取子资源信息
	@WebMethod(action = "getChildrenResourceById")
	public String getChildrenResourceById(@WebParam(name="instanceId")String instanceId);

	// 根据资源实例ID获取资源指标信息
	@WebMethod(action = "getResourceMetricByInstanceId")
	public String getResourceMetricByInstanceId(@WebParam(name="instanceId")String instanceId);

	// 根据资源实例ID获取资源自定义指标信息
	@WebMethod(action = "getResourceCustomMetricByInstanceId",operationName="getResourceCustomMetricByInstanceId")
	public String getResourceCustomMetricByInstanceId(@WebParam(name="instanceId")String instanceId);

	@WebMethod(action = "queryResources")
	@WebResult
	public List<ResourceBean> queryResources(@WebParam(name="types")String[] types);

	@WebMethod(action = "getIntercfaces")
	@WebResult
	public List<InterfaceBean> getIntercfaces(@WebParam(name="resourceId")long resourceId);
	
	// 根据资源实例ID获取指定交换机端口数量
	@WebMethod(action = "getSwitchPortNumberByInstanceId",operationName="getSwitchPortNumberByInstanceId")
	public String getSwitchPortNumberByInstanceId(@WebParam(name="instanceId")String instanceId);
	
	// 根据资源实例ID获取指定交换机状态
	@WebMethod(action = "getSwitchStatusByInstanceId",operationName="getSwitchStatusByInstanceId")
	public String getSwitchStatusByInstanceId(@WebParam(name="instanceId")String instanceId);
	
	// 根据接口资源实例ID获取状态
	@WebMethod(action = "getPortStatusByPortInstanceId",operationName="getPortStatusByPortInstanceId")
	public String getPortStatusByPortInstanceId(@WebParam(name="instanceId")String instanceId);
	
	// 根据接口资源实例ID获取当前设定速率
	@WebMethod(action = "getSpeedByPortInstanceId",operationName="getSpeedByPortInstanceId")
	public String getSpeedByPortInstanceId(@WebParam(name="instanceId")String instanceId);
	
	// 根据资源实例ID获取指定交换机信息
	@WebMethod(action = "getSwitchInfoByInstanceId",operationName="getSwitchInfoByInstanceId")
	public String getSwitchInfoByInstanceId(@WebParam(name="instanceId")String instanceId);
	
	/*根据模型主,子类型获取资源基本信息
	 * 主类型不能为空,子类型可以为空
	 * */
	@WebMethod(action = "getResourceBaseInfoByCategoryId")
	public String getResourceBaseInfoByCategoryId(
			@WebParam(name="parentCategrayId")String parentCategrayId,
			@WebParam(name="childrenCategrayId")String childrenCategrayId);
	
	//根据资源组ID,模型主,子类型获取资源基本信息
	@WebMethod(action = "getResourceBaseInfoByResourceGroupId")
	public String getResourceBaseInfoByResourceGroupId(
			@WebParam(name="resourceGroupId")Long resourceGroupId,
			@WebParam(name="parentCategrayId")String parentCategrayId,  
			@WebParam(name="childrenCategrayId")String  childrenCategrayId);
	
	//批量查询资源的指标数据
	@WebMethod(action = "getMetricDataByInstanceIds")
	public String getMetricDataByInstanceIds(
			@WebParam(name="instanceIds")Long[] instanceIds , 
			@WebParam(name="metricIds")String[] metricIds);
	
	

	//根据资源实例ID查询资源状态
	@WebMethod(action = "getResourceStateByInstanceIds")
	public String getResourceStateByInstanceIds(
			@WebParam(name="instanceids")Long[] instanceids);
	
	//根据获取资源组信息
	@WebMethod(action = "getResourceGroupByUserId")
	public String getResourceGroupByUserId(
			@WebParam(name="userId")Long[] userId);
	
	//根据业务系统名称查询业务系统资源清单
//	@WebMethod(action = "getBizResourceByBizName")
//	public String getBizResourceByBizName(
//			@WebParam(name="bizName")String bizName,
//			@WebParam(name="bool")boolean bool);
	
	//查询资源自定义指标的历史数据
	@WebMethod(action = "getInstanceCustomMetricDataByTime",operationName="getInstanceCustomMetricDataByTime")
	public String getInstanceCustomMetricDataByTime(
			@WebParam(name="instanceId")Long instanceId,
			@WebParam(name="metricId")String metricId,
			@WebParam(name="startTime")String startTime,
			@WebParam(name="endTime")String endTime,
			@WebParam(name="type")String type);
	
	//查询资源指标的历史数据
	@WebMethod(action = "getInstanceMetricDataByTime",operationName="getInstanceMetricDataByTime")
	public String getInstanceMetricDataByTime(
			@WebParam(name="instanceId")Long instanceId,
			@WebParam(name="metricId")String metricId,
			@WebParam(name="startTime")String startTime,
			@WebParam(name="endTime")String endTime,
			@WebParam(name="type")String type);
	
	//查询资源指标的最近一条数据
	@WebMethod(action = "getInstanceRTCustomMetricData")
	public String getInstanceRTCustomMetricData(
			@WebParam(name="instanceId")Long instanceId,
			@WebParam(name="metricId")String metricId);
}
