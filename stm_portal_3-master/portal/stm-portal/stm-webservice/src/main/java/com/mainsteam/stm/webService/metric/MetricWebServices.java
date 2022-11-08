package com.mainsteam.stm.webService.metric;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
/**
 * @Desc:四川移动接口（只针对网络设备下(交换机、路由器、防火墙)资源信息指标(1天调用一次)、
 *       性能指标(1小时调用1次；返回当前时间到前1小时的指标历史数据)相关信息）
 * @Author: sunhailiang
 * @Date: 2017/12/15
 *
 */
@WebService(name = "metricServices", targetNamespace = "http://www.mainsteam.com/ms/metricServices/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface MetricWebServices {
	/*网络设备下(交换机、路由器、防火墙)资源信息指标*/
	@WebMethod
	@WebResult
	public String getMetricInfoList();

	/*网络设备下(交换机、路由器、防火墙)资源性能指标*/
	@WebMethod
	@WebResult
	public String getMetricRealSumaryList();
}
