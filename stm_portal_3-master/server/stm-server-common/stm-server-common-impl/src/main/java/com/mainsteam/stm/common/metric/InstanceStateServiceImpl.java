package com.mainsteam.stm.common.metric;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.state.dao.MetricStateDAO;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.MetricRpcExecutorMBean;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.state.obj.InstanceStateData;

public class InstanceStateServiceImpl implements InstanceStateService{
	private static final Log logger = LogFactory.getLog(InstanceStateServiceImpl.class);
	
	private MetricStateDAO metricStateDAO;	
	private CapacityService capacityService;
	private LocaleNodeService localNodeService;
	private ResourceInstanceService resourceInstanceService;
	private OCRPCClient client;
	
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	public void setResourceInstanceService( ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
	public void setMetricStateDAO(MetricStateDAO metricStateDAO) {
		this.metricStateDAO = metricStateDAO;
	}
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	@Override
	public InstanceStateData getState(long instanceID) {
		return metricStateDAO.getInstanceState(instanceID);
	}
	
	@Override
	public InstanceStateData getStateAdapter(long instanceID) {
		InstanceStateData data=this.getState(instanceID);
		if(data==null)
			return null;
		
		data.setState(convertState(data.getState()));
		return data;
	}
	
	private InstanceStateEnum convertState(InstanceStateEnum state){
		if(InstanceStateEnum.isUnknownForIns(state)){
			return InstanceStateEnum.UNKOWN;
		}else if(InstanceStateEnum.isCriticalForIns(state)){
			return InstanceStateEnum.CRITICAL;
		}else if(InstanceStateEnum.NORMAL_CRITICAL==state){
			return InstanceStateEnum.SERIOUS;
		}else if(InstanceStateEnum.NORMAL_UNKNOWN==state || InstanceStateEnum.NORMAL_NOTHING==state || InstanceStateEnum.UNKNOWN_NOTHING == state){
			return InstanceStateEnum.NORMAL;
		}else{
			return state;
		}
	}
	
	@Override
	public List<InstanceStateData> findStates(List<Long> instanceIDes) {
		return metricStateDAO.findInstanceStates(instanceIDes);
	}
	@Override
	public List<InstanceStateData> findStatesAdapter(List<Long> instanceIDes) {
		 List<InstanceStateData> list=this.findStates(instanceIDes);
		 for(InstanceStateData data:list){
			 data.setState(convertState(data.getState()));
		 }
		return list;
	}

	@Override
	public boolean addState(InstanceStateData data) {
		data.setUpdateTime(new Date());
		int flag = metricStateDAO.addInstanceState(data);
		return flag > 0 ? true : false;
	}

	@Override
	public boolean addState(List<InstanceStateData> data) {
		//有可能批量插入数据太多发生错误
		metricStateDAO.addInstanceState(data);
		return true;
	}

	/* (non-Javadoc)
         * @see com.mainsteam.stm.common.metric.InstanceStateService#catchRealtimeMetricData(long)
         */
	@Override
	public InstanceStateData catchRealtimeMetricData(long instanceID)throws MetricExecutorException, InstancelibException {
		InstanceStateData stateData=new InstanceStateData();
		stateData.setInstanceID(instanceID);
		stateData.setCollectTime(new Date());
		
		ResourceInstance instance= resourceInstanceService.getResourceInstance(instanceID);
		ResourceDef def=capacityService.getResourceDefById(instance.getResourceId());
		
		Node node = null;
		try {
			int nodeGroupId=Integer.valueOf(instance.getDiscoverNode());
		    node  = localNodeService.getLocalNodeTable().getNodeInGroup(nodeGroupId);
		    if(node == null){
		    	throw new MetricExecutorException(ServerErrorCodeConstant.ERR_NODE_NOT_FIND,"can't find the node["+nodeGroupId+"]");
		    }
		} catch (NodeException e) {
			throw new MetricExecutorException("can't find node for instance["+instanceID+"],the Exception:"+e.getMessage(),e);
		}
		
		ResourceMetricDef[] mdefList=def.getMetricDefs();
		for(ResourceMetricDef mdef:mdefList){
			if(MetricTypeEnum.AvailabilityMetric==mdef.getMetricType()){
				try {
					MetricRpcExecutorMBean rpcExecutor = client.getRemoteSerivce(node, MetricRpcExecutorMBean.class);
					MetricData mdata=rpcExecutor.catchRealtimeMetricData(instanceID, mdef.getId());
					stateData.setCauseBymetricID(mdef.getId());
					if(mdata==null ||mdata.getData().length<1){
						stateData.setState(InstanceStateEnum.UNKOWN);
						return stateData;
					}else if("0".equals(mdata.getData()[0])){
						stateData.setState(InstanceStateEnum.CRITICAL);
						return stateData;
					}else if("1".equals(mdata.getData()[0])){
						stateData.setState(InstanceStateEnum.NORMAL);
						return stateData;
					}
						
				} catch (IOException e) {
					throw new MetricExecutorException("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
				}
			}
		}
		
		stateData.setState(InstanceStateEnum.UNKOWN);
		return stateData;
	}
	@Override
	public List<InstanceStateData> getAllInstanceState() {
		return metricStateDAO.getAllInstanceState();
	}

	@Override
	public InstanceStateData getLatestInstanceState(Long instanceId) {
		return metricStateDAO.findLatestInstanceState(instanceId);
	}
}
