package com.mainsteam.stm.state.chain.step;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午10:41:02
 * @version 1.0
 */
public class AvailableLinkValidateStep implements StateChainStep {

	private static final Log logger = LogFactory.getLog(AvailableLinkValidateStep.class);

	private final StateCaculatUtils stateCaculatUtils;

	private final ResourceInstanceService resourceInstanceService;

	public AvailableLinkValidateStep(StateCaculatUtils stateCaculatUtils, ResourceInstanceService resourceInstanceService) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
		this.resourceInstanceService = resourceInstanceService;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		ResourceInstance resourceInstance = (ResourceInstance) context.getContextData().get("resourceInstance");

		if (CapacityConst.LINK.equals(resourceInstance.getCategoryId())) {
			computeLinkInstanceState(resourceInstance, context.getMetricData(), context.getResourceMetricDef());
			return;
		}
		chain.doChain();

	}

	/**
	 *
	 * 链路可用性计算规则为，取连线两端接口的状态之间的最高级别状态，与之前的链路状态比较，
	 * 如果变化则入库和告警并且更新缓存当前链路状态，否则不处理。
	 *
	 * @param resourceInstance
	 * @param metricData
	 * @param resourceMetricDef
     */
	private void computeLinkInstanceState(ResourceInstance resourceInstance, MetricCalculateData metricData, ResourceMetricDef resourceMetricDef) {

		String[] src_ids = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_SRC_SUBINST_ID);
		String[] dest_ids = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_DEST_SUBINST_ID);

		long[] endpointIds = new long[2];
		try {
			if (src_ids != null && src_ids.length > 0) {
				endpointIds[0]  = Long.parseLong(src_ids[0]);
			}
			if (dest_ids != null && dest_ids.length > 0) {
				endpointIds[1] = Long.parseLong(dest_ids[0]);
			}
		}catch (Exception e){
			if(logger.isWarnEnabled()) {
				logger.warn("convert link instance id error,"+ e.getMessage(), e);
			}
			return ;
		}
		List<InstanceStateData> collectionState = new ArrayList<>(2);
		for(long instanceId : endpointIds) {
			try {
				ResourceInstance instance = null;
				if(resourceInstance.getId() != instanceId) {
					instance = resourceInstanceService.getResourceInstance(instanceId);
				}else
					instance = resourceInstance;
				if(null != instance && instance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
					synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
						InstanceStateData instanceStateData = stateCaculatUtils.getInstanceState(instanceId);
						if(instanceStateData !=null) {
							collectionState.add(instanceStateData);
						}
					}
				}
			} catch (InstancelibException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("get instance error." + e.getMessage(), e);
				}
			}
		}
		if(collectionState.isEmpty()) {//两端接口和两端设备状态都为空的情况下，默认为正常
			InstanceStateData instanceStateData = new InstanceStateData(resourceInstance.getId(), InstanceStateEnum.NORMAL, metricData.getCollectTime(),
					metricData.getMetricId(),resourceInstance.getId(),null);
			if(logger.isInfoEnabled()) {
				StringBuilder stringBuilder = new StringBuilder(100);
				stringBuilder.append("initialize link {");
				stringBuilder.append(resourceInstance.getId());
				stringBuilder.append("} state normal causing none interface & device state.");
				logger.info(stringBuilder.toString());
			}
			synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
				stateCaculatUtils.saveInstanceState(instanceStateData);
			}

		}else{
			InstanceStateData curInstanceStateData = Collections.max(collectionState);
			InstanceStateData preInstanceStateData = null;
			synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
				preInstanceStateData = stateCaculatUtils.getInstanceState(resourceInstance.getId());
				if (null == preInstanceStateData || curInstanceStateData.getState() !=preInstanceStateData.getState()) {
					/*
					由于链路状态是取两端接口的状态，但链路自身的告警只有三个指标：链路状态，带宽利用率，总流量。但引起接口变化的指标却有多个，
					为了保持一致，只有引起接口状态变化的指标状态为这三个指标时，才需要改变链路的状态。
					 */
					if(InstanceStateEnum.WARN == curInstanceStateData.getState() || InstanceStateEnum.SERIOUS == curInstanceStateData.getState()) {
						if(StringUtils.isNotBlank(curInstanceStateData.getCauseBymetricID()) &&
								!StringUtils.equals(MetricIdConsts.METRIC_IFOCTETSSPEED,curInstanceStateData.getCauseBymetricID()) &&
								!StringUtils.equals(MetricIdConsts.IF_IFBANDWIDTHUTIL,curInstanceStateData.getCauseBymetricID())) {
							curInstanceStateData.setState(InstanceStateEnum.NORMAL);
							if (logger.isInfoEnabled()) {
								StringBuffer stringBuffer = new StringBuffer();
								stringBuffer.append("Link ");
								stringBuffer.append(resourceInstance.getId());
								stringBuffer.append(" state should set to NORMAL, causing interface state was changed by ");
								stringBuffer.append(curInstanceStateData.getCauseBymetricID());
								logger.info(stringBuffer.toString());
							}
						}
					}
					if (logger.isInfoEnabled()) {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append("change link {");
						stringBuffer.append(resourceInstance.getId());
						stringBuffer.append("} state, causing instance {");
						stringBuffer.append(curInstanceStateData.getInstanceID());
						stringBuffer.append("},state is ");
						stringBuffer.append(curInstanceStateData.getState());
						logger.info(stringBuffer.toString());
					}
					curInstanceStateData.setInstanceID(resourceInstance.getId());
					stateCaculatUtils.saveInstanceState(curInstanceStateData);
					if(!(preInstanceStateData == null && curInstanceStateData.getState() == InstanceStateEnum.NORMAL)){
						InstanceStateChangeData instanceStateChangeData = stateCaculatUtils.
								generateNotifyInstanceState(curInstanceStateData,preInstanceStateData,true,1,metricData, metricData.getMetricId(),resourceMetricDef);
						stateCaculatUtils.notifyInstanceAlarmMsg(instanceStateChangeData);
					}
				}
			}
		}
	}

}
