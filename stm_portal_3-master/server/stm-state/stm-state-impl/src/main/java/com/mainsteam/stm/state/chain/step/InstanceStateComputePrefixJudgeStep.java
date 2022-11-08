package com.mainsteam.stm.state.chain.step;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.util.StateCaculatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 资源实例状态计算前置判断。
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月17日 上午9:32:42
 * @version 1.0
 */
public class InstanceStateComputePrefixJudgeStep implements StateChainStep {
	//
	private static final Log logger = LogFactory.getLog(InstanceStateComputePrefixJudgeStep.class);

	private final StateCaculatUtils stateCaculatUtils;

	private Map<Long, String> instanceFirstComputeFlag;

	public InstanceStateComputePrefixJudgeStep(
			StateCaculatUtils stateCaculatUtils) {
		super();
		this.stateCaculatUtils = stateCaculatUtils;
		instanceFirstComputeFlag = new HashMap<>(100000);
	}

	/**
	 * DHS启动成功后，针对资源实例 ，无论如何，对其状态进行一次计算。<br>
	 * 
	 * 这样，后续计算只需要判断指标是否改变了，来决定是否需要对资源实例再进行计算。<br>
	 * 
	 * @param instanceId
	 *            资源实例id
	 * @return true:需要进行计算,false:不需要进行计算
	 */
	private boolean isComputeFirst(Long instanceId) {
		if (instanceFirstComputeFlag.containsKey(instanceId)) {
			return false;
		} else {
			synchronized (this) {
				if (instanceFirstComputeFlag.containsKey(instanceId)) {
					return false;
				} else {
					instanceFirstComputeFlag.put(instanceId, null);
				}
			}
			return true;
		}
	}

	@Override
	public void doStepChain(StateChainStepContext context, StateComputeChain chain) {

		MetricCalculateData metricData = context.getMetricData();

		if (isComputeFirst(metricData.getResourceInstanceId())) {
			context.getContextData().put("computeInstanceState", Boolean.TRUE);
			if(Boolean.TRUE.equals(context.getContextData().get(MetricTypeEnum.AvailabilityMetric.name()))){

				if(null == context.getContextData().get("flapping")) {
					Object object = context.getContextData().get("preMetricStateData");
					MetricStateEnum metricStateEnum = (MetricStateEnum) context.getContextData().get("metricState");
					if(null != object ) {
						MetricStateData metricStateData = (MetricStateData)object;
						if(metricStateEnum != metricStateData.getState()) {
							metricStateData.setCollectTime(metricData.getCollectTime());
							metricStateData.setState(metricStateEnum);
							this.stateCaculatUtils.saveAvailMetricData(metricData.getResourceInstanceId(),metricData.getMetricId(),
									(String) context.getContextData().get("availMetricValue"));
							this.stateCaculatUtils.saveMetricState(metricStateData);
						}
					}else {
						MetricStateData metricStateData = new MetricStateData();
						metricStateData.setMetricID(metricData.getMetricId());
						metricStateData.setInstanceID(metricData.getResourceInstanceId());
						metricStateData.setState(metricStateEnum);
						metricStateData.setCollectTime(metricData.getCollectTime());
						metricStateData.setType(MetricTypeEnum.AvailabilityMetric);
						this.stateCaculatUtils.saveAvailMetricData(metricData.getResourceInstanceId(),metricData.getMetricId(),
								(String) context.getContextData().get("availMetricValue"));
						this.stateCaculatUtils.saveMetricState(metricStateData);
						if(logger.isInfoEnabled()) {
							logger.info("save metric state after startup. " + metricStateData);
						}
					}

				}
			}
		} else if (Boolean.TRUE.equals(context.getContextData().get("flapping"))) {

			if(isComputeFirst(metricData.getResourceInstanceId())) {
				context.getContextData().put("computeInstanceState", Boolean.TRUE);
			}else {
				// 判断当前实例的状态
				InstanceStateData preInstanceStateData = stateCaculatUtils.getInstanceState(metricData.getResourceInstanceId());
				if(null == preInstanceStateData){
					context.getContextData().put("computeInstanceState", Boolean.TRUE);
				}else {
					MetricStateEnum metricStateEnum = (MetricStateEnum) context.getContextData().get("metricState");
					InstanceStateEnum curInstanceState = StateCaculatUtils.convertMetricStateToInstanceState(metricStateEnum);
					if(preInstanceStateData.getState() != curInstanceState)
						context.getContextData().put("computeInstanceState", Boolean.TRUE);
					else {
						//如果可采集状态有变化的话，依然需要计算
						if(Boolean.TRUE.equals(context.getContextData().get(MetricTypeEnum.AvailabilityMetric.name()))) {
							Object resourceInstObj = context.getContextData().get("resourceInstance");
							if(null != resourceInstObj && ((ResourceInstance)resourceInstObj).getParentId() <=0) {
								CollectStateEnum currentCollectState = MetricStateEnum.CRITICAL == metricStateEnum ? CollectStateEnum.UNCOLLECTIBLE :
										CollectStateEnum.COLLECTIBLE;
								if(null !=preInstanceStateData.getCollectStateEnum() && currentCollectState != preInstanceStateData.getCollectStateEnum()) {

									context.getContextData().put("computeCollectionState", Boolean.TRUE);
									context.getContextData().put("collectionStateEnum", currentCollectState);
								}

							}
						}
					}
				}
			}

		}

		chain.doChain();
	}
}
