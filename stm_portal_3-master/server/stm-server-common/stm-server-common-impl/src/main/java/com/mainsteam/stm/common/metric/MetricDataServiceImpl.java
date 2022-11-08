package com.mainsteam.stm.common.metric;

import java.io.IOException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.dao.MetricAvailableDAO;
import com.mainsteam.stm.common.metric.dao.MetricInfoDAO;
import com.mainsteam.stm.common.metric.dao.MetricDataDAO;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.MetricRpcExecutorMBean;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.rpc.client.OCRPCClient;

public class MetricDataServiceImpl implements MetricDataService {
	private static final Log logger = LogFactory.getLog(MetricDataServiceImpl.class);
	
	private MetricAvailableDAO metricAvailableDAO;
	private MetricDataDAO metricDataDAO;
	private MetricInfoDAO metricInfoDAO;
	private LocaleNodeService localNodeService;
	private ResourceInstanceService resourceInstanceService;
	
	private CapacityService capacityService;
	
	public void setResourceInstanceService( ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	private OCRPCClient client;
	
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	public void setMetricDataDAO(MetricDataDAO metricDataDAO) {
		this.metricDataDAO = metricDataDAO;
	}
	public void setMetricAvailableDAO(MetricAvailableDAO metricAvailableDAO) {
		this.metricAvailableDAO = metricAvailableDAO;
	}

	public void setMetricInfoDAO(MetricInfoDAO metricInfoDAO) {
		this.metricInfoDAO = metricInfoDAO;
	}


	@Override
	public Page<Map<String,?>,MetricRealtimeDataQuery>  queryRealTimeMetricDatas(MetricRealtimeDataQuery query,int pageNo,int pageSize){
		query.valid();

		Page<Map<String,?>,MetricRealtimeDataQuery> page=new Page<Map<String,?>,MetricRealtimeDataQuery>(pageNo*pageSize, pageSize,query);
		List<Map<String,?>> mdl=new ArrayList<Map<String,?>>();
		
		if(mdl.size()<1){
			mdl=metricDataDAO.queryRealtimeMetricDataForPage(page);
		}
		return page;
	}
	
	@Override
	public List<Map<String,?>> queryRealTimeMetricData(MetricRealtimeDataQuery query){
		query.valid();
		return metricDataDAO.queryRealtimeMetricData(query);
	}
	
	@Override
	public List<MetricData> findTop(String metricID, long[] instanceIDes,int topn) {
		return metricDataDAO.findTop(metricID,instanceIDes,topn,"DESC");
	}
	
	@Override
	public List<MetricData> findTop(String metricID, long[] instanceIDes,int topn,int order) {
		String orderbyStr = null;
		if(order == 0){
			orderbyStr = "DESC";
		}else{
			orderbyStr = "ASC";
		}
		return metricDataDAO.findTop(metricID,instanceIDes,topn,orderbyStr);
	}
	
	public void updateMetricDatas(List<MetricData> metricDatas){
		
		for(MetricData md :metricDatas){
			metricDataDAO.updateRealTimeMetricData(md);
		}
//		metricDataDAO.addMetricDataBatch(metricDatas);
	}
	
	public void start(){
		for(ResourceDef rdef:capacityService.getResourceDefList()){
			for(ResourceMetricDef mdef:rdef.getMetricDefs()){
				if(MetricTypeEnum.PerformanceMetric ==mdef.getMetricType()){
					if(mdef.getId().length()>23){
						continue;
					}
					metricDataDAO.createRealtimeMetricTable(mdef.getId());
					metricDataDAO.createHistoryMetricTable(mdef.getId());
				}
			}
		}
		
	}


	@Override
	public  Page<List<MetricData>,MetricHistoryDataQuery> queryHistoryMetricDatas(MetricHistoryDataQuery query ,int pageNo, int pageSize) {
		
		Page<List<MetricData>,MetricHistoryDataQuery> page=new Page<List<MetricData>,MetricHistoryDataQuery>(pageNo*pageSize, pageSize,query);
		return metricDataDAO.queryHistoryMetricDatas(page);
	}

	@Override
	public List<MetricData> queryHistoryMetricData(String metricID, long instanceID, Date startTime, Date endTime) {
		MetricHistoryDataQuery query=new MetricHistoryDataQuery();
		query.setEndTime(endTime);
		query.setStartTime(startTime);
		query.setMetricID(metricID);
		query.setInstanceID(instanceID);
		
		return metricDataDAO.queryHistoryMetricDatas(query);
	}

	@Override
	public void updatePerformanceMetricData(MetricData data) {
		metricDataDAO.updateRealTimeMetricData(data);
	}
	
	@Override
	public void addMetricInfoData(MetricData data) {
		metricInfoDAO.addMetricInfoData(data);
	}
	@Override
	public MetricData getMetricInfoData(long instanceID, String metricID) {
		return metricInfoDAO.getMetricInfoData(instanceID,metricID);
	}
	@Override
	public MetricData getMetricAvailableData(long instanceID, String metricID) {
		return metricDataDAO.getMetricAvailableData(instanceID,metricID);
	}

	@Override
	public List<MetricData> getMetricAvailableData(long instanceID, Set<String> metricID) {
		return metricDataDAO.getMetricAvailableData(instanceID, metricID);
	}

	@Override
	public MetricData getMetricPerformanceData(long instanceID, String metricID) {
		return metricDataDAO.getMetricPerformanceData(instanceID,metricID);
	}
	@Override
	public List<MetricData> getMetricInfoDatas(long instanceID, String[] metricID) {
		return getMetricInfoDatas(new long[]{instanceID},metricID);
	}
	@Override
	public List<MetricData> getMetricInfoDatas(long[] instanceIDes, String[] metricID) {
		return metricInfoDAO.getMetricInfoDatas(instanceIDes,metricID);
	}
	
	@Override
	public void updateAvailableMetricData(MetricData data) {
		metricAvailableDAO.updateMetricAvailableData(data);
	}
	
	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID,Map<String,String> params)throws MetricExecutorException {
		Node node = getNodeFromIns(instanceID);
		if(logger.isDebugEnabled()){
			logger.debug("start call jmx...");
		}
		try {
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
			return rpcExecutor.catchRealtimeMetricDataWithParameter(instanceID, metricID,params);
		} catch (IOException e) {
			throw new MetricExecutorException("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
		}
	}
	private Node getNodeFromIns(long instanceID) throws MetricExecutorException {
		Node node = null;
		try {
			ResourceInstance instance= resourceInstanceService.getResourceInstance(instanceID);
			int nodeGroupId=Integer.valueOf(instance.getDiscoverNode());
		    node  = localNodeService.getLocalNodeTable().getNodeInGroup(nodeGroupId);
		    if(node == null){
		    	throw new MetricExecutorException(ServerErrorCodeConstant.ERR_NODE_NOT_FIND,"can't find the node["+nodeGroupId+"]");
		    }
		} catch (Exception e) {
			throw new MetricExecutorException("can't find the instance["+instanceID+"]'s node,the Exception:"+e.getMessage(),e);
		}
		return node;
	}
	
	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID)throws MetricExecutorException {
		Node node = getNodeFromIns(instanceID);
		if(logger.isDebugEnabled()){
			logger.debug("start call jmx...");
		}
		try {
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
			return rpcExecutor.catchRealtimeMetricData(instanceID, metricID);
		} catch (IOException e) {
			throw new MetricExecutorException("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
		}
	}
	
	/**
	 * 查询资源是否在采集
	 */
	@Override
	public boolean isMetricGather(long parentInstanceID) throws MetricExecutorException {
		boolean result = false;
		Node node = getNodeFromIns(parentInstanceID);
		try {
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
			result = rpcExecutor.isMetricGather(parentInstanceID);
		} catch (IOException e) {
			throw new MetricExecutorException("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 触发资源开始采集指标
	 * @param parentInstanceID
	 * @param containChild
	 * @return
	 * @throws MetricExecutorException
	 */
	@Override
	public void triggerMetricGather(long parentInstanceID, boolean containChild)
			throws MetricExecutorException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("triggerMetricGather  parentInstanceID=").append(parentInstanceID);
			b.append(" containChild").append(containChild);
			b.append(" start");
			logger.info(b);
		}
		Node node = null;
		try {
			node = getNodeFromIns(parentInstanceID);
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
			rpcExecutor.triggerMetricGather(parentInstanceID, containChild);
		} catch (IOException e) {
			StringBuilder b = new StringBuilder(100);
			b.append("triggerMetricGather  parentInstanceID=").append(parentInstanceID);
			b.append(" containChild").append(containChild);
			b.append(" can't connect the node[");
			if(node != null){
				b.append(node.getIp()).append(":").append(+node.getPort());
				b.append("],the Exception:");
			}
			b.append(e.getMessage());
			throw new MetricExecutorException(b.toString(),e);
		}catch(Exception e){
			StringBuilder b = new StringBuilder(100);
			b.append("triggerMetricGather  parentInstanceID=").append(parentInstanceID);
			b.append(" containChild").append(containChild);
			b.append(" is failed!");
			b.append(e.getMessage());
			throw new MetricExecutorException(b.toString(),e);
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("triggerMetricGather  parentInstanceID=").append(parentInstanceID);
			b.append(" containChild").append(containChild);
			b.append(" end");
			logger.info(b);
		}
	}
	
	/**触发资源开始采集信息指标
	 * @return
	 * @throws MetricExecutorException
	 */
	@Override
	public void triggerInfoMetricGather(long parentInstanceID, boolean containChild)throws MetricExecutorException {
		Node node = getNodeFromIns(parentInstanceID);
		if(logger.isDebugEnabled()){
			logger.debug("start call jmx...");
		}
		try {
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
			rpcExecutor.triggerInfoMetricGather(parentInstanceID, containChild);
		} catch (IOException e) {
			throw new MetricExecutorException("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
		}
	}
	
	@Override
	public List<MetricData> catchRealtimeMetricData( int nodeGroupId, List<Long> instanceIDes, String metricID)throws MetricExecutorException {
		
		if(logger.isDebugEnabled()){
			logger.debug("start call jmx...");
		}
		try {
			MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(nodeGroupId, MetricRpcExecutorMBean.class);
			List<MetricData> result = rpcExecutor.catchRealtimeMetricDatas(instanceIDes, metricID);
			StringBuilder b = new StringBuilder(1000);
			b.append("catchRealtimeMetricData result:");
			if(result != null){
				for (MetricData metricData : result) {
					b.append(metricData);
					b.append("\n");
				}
			}else{
				b.append("instanceId:").append(instanceIDes);
				b.append(" metricId:").append(metricID);
				b.append(" not collect data");
			}
			logger.info(b.toString());
			return result;
		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(), e);
			}
			throw new MetricExecutorException("can't connect the node["+ nodeGroupId +"],the Exception:"+e.getMessage(),e);
		} catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(), e);
			}
			throw new MetricExecutorException("can't connect the node["+ nodeGroupId +"],the Exception:"+e.getMessage(),e);
		}
	}
	
	@Override
	public void addCustomerMetricData(MetricData data) {
		metricDataDAO.addCustomerData(data);
	}
	
	@Override
	public MetricData getCustomerMetricData(long instanceID, String metricID) {
		return metricDataDAO.getCustomerData(instanceID,metricID);
	}
	
	@Override
	public List<MetricData> queryHistoryCustomerMetricData(String metricID,long instanceID, Date startTime, Date endTime) {
		MetricHistoryDataQuery query=new MetricHistoryDataQuery();
		query.setEndTime(endTime);
		query.setStartTime(startTime);
		query.setMetricID(metricID);
		query.setInstanceID(instanceID);
		
		return metricDataDAO.queryHistoryCustomerMetricDatas(query);
	}
	
	
	
}
