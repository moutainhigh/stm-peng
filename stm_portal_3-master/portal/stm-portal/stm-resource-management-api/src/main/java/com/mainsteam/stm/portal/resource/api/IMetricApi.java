package com.mainsteam.stm.portal.resource.api;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.portal.resource.bo.ArpTablePageBo;
import com.mainsteam.stm.portal.resource.bo.NetstatPageBo;
import com.mainsteam.stm.portal.resource.bo.RollbackPageBo;
import com.mainsteam.stm.portal.resource.bo.RouteTablePageBo;
import com.mainsteam.stm.portal.resource.bo.SessionPageBo;

public interface IMetricApi {
	
	/**
	 * 通过指标ID，查询当前资源的指标数据
	 * @param instanceId
	 * @param metricId
	 * @return
	 */
	public MetricData getMetricDataByMetricId(long instanceId,String metricId)  throws MetricExecutorException;
	
	/**
	 * 通过资源实例ID，查询 ARP表
	 * @param instanceId
	 * @return
	 */
	public ArpTablePageBo getArpTableByInstanceId(long instanceId)  throws MetricExecutorException;
	
	/**
	 * 导出arp表的excel文档
	 * @param instanceId
	 * @param outStream
	 */
	public void arptableExportExcel(long instanceId, HttpServletResponse response,HttpServletRequest request) throws Exception;
	
	/**
	 * 通过资源实例ID，查询 路由表
	 * @param instanceId
	 * @return
	 */
	public RouteTablePageBo getRouteTableByInstanceId(long instanceId)  throws MetricExecutorException;

	/**
	 * 导出路由表的excel文档
	 * @param instanceId
	 * @param response
	 * @param request
	 */
	public void routetableExportExcel(long instanceId, HttpServletResponse response, HttpServletRequest request) throws Exception;
	
	/**
	 * 通过资源实例ID，查询 Netstat 返回结果
	 * @param instanceId
	 * @return
	 */
	public NetstatPageBo getNetstatByInstanceId(long instanceId);
	/**
	 * 通过资源实例ID，查询 Session
	 * @param instanceId
	 * @return
	 */
	public SessionPageBo getSessionByInstanceId(long instanceId)  throws MetricExecutorException;
	/**
	 * 通过资源实例ID，查询 rollback
	 * @param instanceId
	 * @return
	 */
	public RollbackPageBo getRollbackByInstanceId(RollbackPageBo pageBo)  throws MetricExecutorException;

}
