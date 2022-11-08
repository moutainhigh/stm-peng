package com.mainsteam.stm.webService.performance;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "performanceWebServices", targetNamespace = "http://www.mainsteam.com/ms/performanceService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PerformanceWebServices {

	@WebMethod(action = "getMetricList",operationName="getMetricList")
	public String getMetricList(@WebParam(name="resourceTypeId")String resourceTypeId);
	
	@WebMethod(action = "getAllMetricListByResourceId",operationName="getAllMetricListByResourceId")
	public String getAllMetricListByResourceId(@WebParam(name="resourceId")String resourceId);
	
	@WebMethod(action = "getCurrentPerformanceMetricData",operationName="getCurrentPerformanceMetricData")
	public String getCurrentPerformanceMetricData (@WebParam(name="instanceId")String instanceId,@WebParam(name="metricId")String metricId);
	
	@WebMethod(action = "getCurrentAvailabilityMetricData",operationName="getCurrentAvailabilityMetricData")
	public String getCurrentAvailabilityMetricData (@WebParam(name="instanceId")String instanceId,@WebParam(name="metricId")String metricId);
	
	@WebMethod(action = "getCurrentInformationMetricData",operationName="getCurrentInformationMetricData")
	public String getCurrentInformationMetricData (@WebParam(name="instanceId")String instanceId,@WebParam(name="metricId")String metricId);
	
	@WebMethod(action = "getHistoryPerformanceMetricData")
	public String getHistoryPerformanceMetricData (@WebParam(name="instanceId")String instanceId,@WebParam(name="metricIds")String[] metricIds
			,@WebParam(name="startTime")String startTime, @WebParam(name="endTime")String endTime);

	@WebMethod(action = "getTopNPerformance")
	public PerformanceDataBean getTopNPerformance(@WebParam(name="category")String category,@WebParam(name="metricId")String metricId,@WebParam(name="limits")String limits
			,@WebParam(name="startTime")String startTime, @WebParam(name="endTime")String endTime);
}
