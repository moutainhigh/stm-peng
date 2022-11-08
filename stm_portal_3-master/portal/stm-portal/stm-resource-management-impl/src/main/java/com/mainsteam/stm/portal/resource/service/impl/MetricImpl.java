package com.mainsteam.stm.portal.resource.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.util.MetricDataUtil;
import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.dict.ArpTableColumnConsts;
import com.mainsteam.stm.caplib.dict.IpRouteTableColumnConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.NetStatColumnConsts;
import com.mainsteam.stm.caplib.dict.RollbackTableColumnConsts;
import com.mainsteam.stm.caplib.dict.SessionEventTableColumnConsts;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.portal.resource.api.IMetricApi;
import com.mainsteam.stm.portal.resource.bo.ArpTableBo;
import com.mainsteam.stm.portal.resource.bo.ArpTablePageBo;
import com.mainsteam.stm.portal.resource.bo.NetstatBo;
import com.mainsteam.stm.portal.resource.bo.NetstatPageBo;
import com.mainsteam.stm.portal.resource.bo.RollbackBo;
import com.mainsteam.stm.portal.resource.bo.RollbackPageBo;
import com.mainsteam.stm.portal.resource.bo.RouteTableBo;
import com.mainsteam.stm.portal.resource.bo.RouteTablePageBo;
import com.mainsteam.stm.portal.resource.bo.SessionBo;
import com.mainsteam.stm.portal.resource.bo.SessionPageBo;

public class MetricImpl implements IMetricApi{

	@Resource
	private MetricDataService metricDataService;

	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	private static Logger logger = Logger.getLogger(MetricImpl.class);
	
	private static final String  splitString = ",";
	
	@Override
	public MetricData getMetricDataByMetricId(long instanceId, String metricId) throws MetricExecutorException {
		MetricData metricData=metricDataService.catchRealtimeMetricData(instanceId, metricId);

		return metricData;

	}
	
	@Override
	public ArpTablePageBo getArpTableByInstanceId(long instanceId)  throws MetricExecutorException{
		ArpTablePageBo pageBo = new ArpTablePageBo();
		MetricData metricData=null;
		try {
			
			metricData = metricDataService.catchRealtimeMetricData(instanceId, MetricIdConsts.ARP_TABLE);

		} catch (MetricExecutorException e) {
			logger.error("Get ARP Table By InstanceId Error.",e);
		}
		
		if(metricData == null || metricData.getData() == null){
			logger.error("Scan ARP Table error: Table Data is null");
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
        List<ArpTableBo> dataList = new ArrayList<ArpTableBo>();
		
		if(dataString == null || dataString.length <= 0){
			pageBo.setArpTableData(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		
		//[arpTableIndex, IfIndex, MacAddress, IPAddress, ArpType]
        List<Map<String, String>> maps = null;
        if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
		//String[] dataTitles = dataString[0].split(splitString, -1);
		try{
            ArpTableBo arpTableBo = null;
			for(int i = 0 ;maps != null &&  i < maps.size() ; i ++){
                Map<String, String> stringStringMap = maps.get(i);
                Set<String> strings = stringStringMap.keySet();
                arpTableBo = new ArpTableBo();
                dataList.add(arpTableBo);
                for(String key : strings){
                    switch (key) {
                        case ArpTableColumnConsts.IFINDEX:
                                arpTableBo.setIfIndex(stringStringMap.get(key));
                            break;
                        //			case ArpTableColumnConsts.IFINDEX:
                        //				for(int j = 1 ; j < dataString.length ; j ++){
                        //					dataList.get(j - 1).setIfIndex(dataString[j].split(splitString)[i]);
                        //				}
                        //				break;
                        case ArpTableColumnConsts.MACADDRESS:
                            arpTableBo.setMacAddress(stringStringMap.get(key));
                            break;
                        case ArpTableColumnConsts.IPADDRESS:
                            arpTableBo.setiPAddress(stringStringMap.get(key));
                            break;
                        case ArpTableColumnConsts.ARPTYPE:
                            arpTableBo.setArpType(stringStringMap.get(key));
                            break;
                    }
                }
			}
		}catch(Exception e){
			logger.error("getArpTableByInstanceId:", e);
		}

		pageBo.setArpTableData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
	
		return pageBo;
	}

	@Override
	public void arptableExportExcel(long instanceId, HttpServletResponse response,HttpServletRequest request) throws Exception {
		ArpTablePageBo pageBo = getArpTableByInstanceId(instanceId);
		if(pageBo != null && pageBo.getArpTableData() != null){
			List<ArpTableBo> arpList = pageBo.getArpTableData();
			ExcelUtil<ArpTableBo> exportUtil = new ExcelUtil<ArpTableBo>();
			List<ExcelHeader> headers = new ArrayList<>();
			headers.add(new ExcelHeader("iPAddress","IP地址"));
			headers.add(new ExcelHeader("macAddress","MAC地址"));
			headers.add(new ExcelHeader("ifIndex","接口索引"));
			headers.add(new ExcelHeader("arpType","接口类型"));
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			String fileName = ri.getShowIP() + "_ARP_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".xlsx";
			WebUtil.initHttpServletResponse(fileName, response, request);
			exportUtil.exportExcel("ARP表", headers, arpList, response.getOutputStream());
		}
	}
	
	@Override
	public RouteTablePageBo getRouteTableByInstanceId(long instanceId)  throws MetricExecutorException{
		RouteTablePageBo pageBo = new RouteTablePageBo();
		
		MetricData metricData=null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(instanceId, MetricIdConsts.ROUTE_TABLE);
		} catch (MetricExecutorException e) {
			logger.error("Get Route Table By InstanceId Error.",e);
		}
		
		if(metricData == null || metricData.getData() == null){
			logger.error("Scan IP Route Table error: Data is null");
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
		List<RouteTableBo> dataList = new ArrayList<RouteTableBo>();

		if(dataString == null || dataString.length <= 0){
			pageBo.setRouteTableData(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		

		//String[] dataTitles = dataString[0].split(splitString, -1);
        List<Map<String, String>> maps = null;
        if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
		try{
            RouteTableBo dataBo = null;
			for(int i = 0 ;maps!= null && i < maps.size() ; i ++){
                Map<String, String> stringStringMap = maps.get(i);
                Set<String> strings = stringStringMap.keySet();
                dataBo = new RouteTableBo();
                dataList.add(dataBo);
                for(String key : strings){
                    switch (key) {
                        case IpRouteTableColumnConsts.DISTIPADDRESS:
                                dataBo.setDistIPAddress(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.IFINDEX:
                            dataBo.setIfIndex(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.MAINROUTING:
                            dataBo.setMainRoutingMetric(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.BACKUPINGROUTING1:
                            dataBo.setBackupingRoutingMetric1(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.BACKUPINGROUTING2:
                            dataBo.setBackupingRoutingMetric2(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.BACKUPINGROUTING3:
                            dataBo.setBackupingRoutingMetric3(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.BACKUPINGROUTING4:
                            dataBo.setBackupingRoutingMetric4(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.NEXTHOP:
                            dataBo.setNextHop(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.ROUTETYPE:
                            dataBo.setRouteType(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.ROUTEPROTOCOL:
                            dataBo.setRouteProtocol(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.ROUTEAGE:
                            dataBo.setRouteAge(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.SUBNETMASK:
                            dataBo.setSubnetMask(stringStringMap.get(key));
                            break;
                        case IpRouteTableColumnConsts.ROUTEINFO:
                            dataBo.setRouteInfo(stringStringMap.get(key));
                            break;
                    }
                }
			}
		}catch(Exception e){
			logger.error("getRouteTableByInstanceId:", e);
		}

		pageBo.setRouteTableData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		
		
		return pageBo;
		
	}
	

	@Override
	public void routetableExportExcel(long instanceId, HttpServletResponse response, HttpServletRequest request) throws Exception {
		RouteTablePageBo pageBo = getRouteTableByInstanceId(instanceId);
		if(pageBo != null && pageBo.getRouteTableData() != null){
			List<RouteTableBo> routeList = pageBo.getRouteTableData();
			ExcelUtil<RouteTableBo> exportUtil = new ExcelUtil<RouteTableBo>();
			List<ExcelHeader> headers = new ArrayList<>();
			headers.add(new ExcelHeader("distIPAddress","目的地"));
			headers.add(new ExcelHeader("subnetMask","掩码"));
			headers.add(new ExcelHeader("routeProtocol","路由协议"));
			headers.add(new ExcelHeader("nextHop","下一跳"));
			headers.add(new ExcelHeader("routeType","类型"));
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			String fileName = ri.getShowIP() + "_路由表_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".xlsx";
			WebUtil.initHttpServletResponse(fileName, response, request);
			exportUtil.exportExcel("路由表", headers, routeList, response.getOutputStream());
		}
	}
	
	/**
	 * 通过资源实例ID，查询 Netstat 返回结果
	 * @param instanceId
	 * @return
	 */
	@Override
	public NetstatPageBo getNetstatByInstanceId(long instanceId){
		NetstatPageBo pageBo = new NetstatPageBo();
		MetricData metricData=null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(instanceId, MetricIdConsts.NETSTAT);
		} catch (MetricExecutorException e) {
			logger.error("Get Netstat By InstanceId Error.",e);
		}
		
		if(metricData == null || metricData.getData() == null){
			logger.error("Scan Netstat error:Netstat is null");
			return pageBo;
		}
		String[] dataString = metricData.getData();
		List<NetstatBo> dataList = new ArrayList<NetstatBo>();
		
		if(dataString == null || dataString.length <= 0){
			pageBo.setNetstatData(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		//[proto, localAddress, foreignAddress, state]
		//String[] dataTitles = dataString[0].split(splitString);
        List<Map<String, String>> maps = null;
        if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
        NetstatBo dataBo = null;
        for(int i = 0 ; maps != null && i < maps.size() ; i ++) {
            Map<String, String> stringStringMap = maps.get(i);
            Set<String> strings = stringStringMap.keySet();
            dataBo = new NetstatBo();
            dataList.add(dataBo);
            for(String key : strings){
                switch (key) {
                    case NetStatColumnConsts.PROTOCOL:
                        dataBo.setProtocol(stringStringMap.get(key));
                        break;

                    case NetStatColumnConsts.LOCALADDRESS:
                        dataBo.setLocalAddress(stringStringMap.get(key));
                        break;
                    case NetStatColumnConsts.FOREIGNADDRESS:
                        dataBo.setForeignAddress(stringStringMap.get(key));
                        break;
                    case NetStatColumnConsts.STATE:
                        dataBo.setState(stringStringMap.get(key));
                        break;
                }
            }
        }


		pageBo.setNetstatData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		
		return pageBo;

	}

	@Override
	public SessionPageBo getSessionByInstanceId(long instanceId) throws MetricExecutorException {
		SessionPageBo pageBo = new SessionPageBo();
		MetricData metricData=null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(instanceId, "SessionEvent");
		} catch (MetricExecutorException e) {
			logger.error("Get Session By InstanceId Error.",e);
		}
		if(metricData == null || metricData.getData() == null){
			logger.error("Scan Session error: Data is null");
			return pageBo;
		}
		String[] dataString = metricData.getData();
		List<SessionBo> dataList = new ArrayList<SessionBo>();
		
		if(dataString == null || dataString.length <= 0){
			pageBo.setSessionBoList(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		
		//String[] dataTitles = dataString[0].split(splitString);
        List<Map<String, String>> maps = null;
        if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
        SessionBo sessionBo = null;
        for(int i = 0 ; maps != null && i < maps.size() ; i ++){
            Map<String, String> stringStringMap = maps.get(i);
            Set<String> strings = stringStringMap.keySet();
            sessionBo = new SessionBo();
            dataList.add(sessionBo);
            for(String key : strings){
                switch (key) {
                    case SessionEventTableColumnConsts.SID:
                            sessionBo.setSid(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.SEQ:
                            sessionBo.setSeq(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.EVENT:
                            sessionBo.setEvent(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.WAITTIME:
                            sessionBo.setWaitTime(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P1:
                            sessionBo.setP1(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P2:
                            sessionBo.setP2(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P3:
                            sessionBo.setP3(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P1TEXT:
                            sessionBo.setP1text(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P2TEXT:
                            sessionBo.setP2text(stringStringMap.get(key));
                        break;
                    case SessionEventTableColumnConsts.P3TEXT:
                            sessionBo.setP3text(stringStringMap.get(key));
                        break;
                }
            }
		}
		pageBo.setSessionBoList(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		return pageBo;
	}

	@Override
	public RollbackPageBo getRollbackByInstanceId(RollbackPageBo pageBo) throws MetricExecutorException {
		long instanceId = pageBo.getInstanceId();
		MetricData metricData=null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(instanceId, "Rollback");
		} catch (MetricExecutorException e) {
			logger.error("Get Rollback By InstanceId Error.",e);
		}
		if(metricData == null || metricData.getData() == null){
			logger.error("Scan Rollback error: Data is null");
			return pageBo;
		}
		String[] dataString = metricData.getData();
		List<RollbackBo> dataList = new ArrayList<RollbackBo>();
		
		if(dataString == null || dataString.length <= 0){
			pageBo.setRollbackBoList(dataList);;
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		
		//String[] dataTitles = dataString[0].split(splitString);
        List<Map<String, String>> maps = null;
        if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
        RollbackBo rollbackBo = null;
		for(int i = 0 ;maps != null &&  i < maps.size() ; i ++){
            Map<String, String> stringStringMap = maps.get(i);
            Set<String> strings = stringStringMap.keySet();
            rollbackBo = new RollbackBo();
            dataList.add(rollbackBo);
            for(String key : strings){
                switch (key) {
                    case RollbackTableColumnConsts.SEGMENT_NAME:
                            rollbackBo.setSegmentName(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.SEGMENT_ID:
                            rollbackBo.setSegmentId(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.OWNER:
                        rollbackBo.setOwner(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.TABLESPACE_NAME:
                        rollbackBo.setTablespaceName(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.STATUS:
                        rollbackBo.setStatus(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.EXTENTS:
                        rollbackBo.setExtents(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.XACTS:
                        rollbackBo.setXacts(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.CUREXT:
                        rollbackBo.setCurext(stringStringMap.get(key));
                        break;
                    case RollbackTableColumnConsts.CURBLK:
                        rollbackBo.setCurblk(stringStringMap.get(key));
                        break;
                }
            }
		}
		pageBo.setTotalRecord(dataList.size());

		// 分页索引
		int startIndex = (int) Math.min(pageBo.getStartRow(), dataList.size());
		int endIndex = (int) Math.min(pageBo.getStartRow() + pageBo.getRowCount(), dataList.size());
		// 分页
		dataList = dataList.subList(startIndex, endIndex);
		pageBo.setRollbackBoList(dataList);;
		return pageBo;
	}

}
