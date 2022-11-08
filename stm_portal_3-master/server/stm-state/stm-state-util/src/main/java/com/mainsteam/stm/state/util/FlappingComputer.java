/**
 * 
 */
package com.mainsteam.stm.state.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 上午9:57:08
 * @version 1.0
 */
/** 
 */
public class FlappingComputer {

	private static final Log logger = LogFactory.getLog(FlappingComputer.class);

	/*
	 * 保存flapping次数，key为实例ID+指标Id，value表示flapping计算方向相同的值，flapping的初始化为0
	 * 即状态从正常--
	 * >致命为+1【正向】，致命-->正常为-1【反向】，相同方向的不同状态flapping累计+1或者-1，方向发生变化flapping为0，
	 * 然后去flapping的绝对值与策略设置的flapping比较即可
	 */
	private final Map<String, Integer> flappingMap = new ConcurrentHashMap<String, Integer>();

	private final static Integer INIT_FLAPPING_VALUE = Integer.valueOf(0);

	/**
	 * 
	 */
	public FlappingComputer() {
	}

	/**
	 * Flapping处理,处理逻辑如下： 1.当前flapping ==
	 * 0，则表示是flapping计入初始状态，可能是由于首次状态计算或flapping满足告警条件后归零所致； 2.当前flapping >
	 * 0，则表示flapping进入正向计算，即指标状态从“正常”过渡到“致命”期间的所有状态；当flapping < 0表示进入反向计算，
	 * 指标状态从“致命”过渡到“正常”期间的所有状态。在正向计算中，flapping > 0,反之在反向计算中,flapping < 0;
	 * 3.首次进行状态计算，由当前状态与初始化状态（基准点）比较，其他情况为当前状态与上次状态（基准点）比较，当前状态>上次状态， 且flapping
	 * >=0,则flapping++,否则flapping置为0;当前状态<上次状态，且flapping<=0时，flapping--，
	 * 否则flapping置为0；当前状态==上次状态时，
	 * flapping置为0；需要注意的是，在比较状态时，同时需要判断相应的flapping是否大于或者小于0的情况，例如在同向计算中，
	 * flapping一直是自增或自减的
	 * ，如果flapping大于0则表示上次计算是正向计算，如果flapping小于0则表示上次计算是反向计算，那么就能知道在当前计算中
	 * 是按照正向计算++1还是按照反向计算重置为0 4.当flapping满足告警条件时，flapping置为0；
	 * 5.首次计算状态或flapping满足告警条件时都需要保存指标状态，其他情况则不需要。当指标状态变化时，最新的指标状态则为新的基准点；
	 * 
	 * @param instanceId
	 * @param metricId
	 * @param curMetricStateEnum
	 * @param preMetricStateEnum
	 * @param flapping
	 * @return
	 * @throws ProfilelibException
	 */
	public boolean flapping(long instanceId, String metricId,
			MetricStateEnum curMetricStateEnum,
			MetricStateEnum preMetricStateEnum, final int flapping)
			throws ProfilelibException {

		boolean isFlapping = false; //true表示在flapping期间内，false表示表示超过flapping阈值
		final String flapKey = instanceId + metricId;
		int thresholdFlapping = flapping < 1 ? 1 : flapping;
		Integer flappingTimes = flappingMap.get(flapKey);
		flappingTimes = (flappingTimes == null) ? INIT_FLAPPING_VALUE : flappingTimes;
		if (logger.isDebugEnabled()) {
			StringBuffer bf = new StringBuffer(100);
			bf.append("flapping start metricID is ");
			bf.append(metricId);
			bf.append(",resourceInstanceId is ");
			bf.append(instanceId);
			bf.append(",Current flapping times is ");
			bf.append(flappingTimes);
			bf.append(",Metric flapping times is ");
			bf.append(thresholdFlapping);
			logger.debug(bf.toString());
		}
		if(null == preMetricStateEnum) {
			preMetricStateEnum = MetricStateEnum.NORMAL;
		}
		if(null == curMetricStateEnum) {
			curMetricStateEnum = MetricStateEnum.NORMAL;
		}
		//转换状态
		if(MetricStateEnum.isUnknown(preMetricStateEnum) || preMetricStateEnum == MetricStateEnum.NORMAL_NOTHING)
			preMetricStateEnum = MetricStateEnum.NORMAL;
		else if(MetricStateEnum.CRITICAL_NOTHING == preMetricStateEnum)
			preMetricStateEnum = MetricStateEnum.CRITICAL;

		if(curMetricStateEnum.getStateVal() > preMetricStateEnum.getStateVal()){
			if(flappingTimes < 0)
				flappingTimes = INIT_FLAPPING_VALUE;
			else
				flappingTimes = Integer.valueOf(flappingTimes.intValue() + 1);
		}else if(curMetricStateEnum.getStateVal() < preMetricStateEnum.getStateVal()){
			if(flappingTimes > 0)
				flappingTimes = INIT_FLAPPING_VALUE;
			else
				flappingTimes = Integer.valueOf(flappingTimes.intValue() - 1);
		}else {
			flappingTimes = INIT_FLAPPING_VALUE;
		}

		if (Math.abs(flappingTimes) >= thresholdFlapping) {//指标状态改变事件
			isFlapping = false;
			flappingMap.put(flapKey, INIT_FLAPPING_VALUE);
			if (logger.isInfoEnabled()){
				StringBuffer bf = new StringBuffer(100);
				bf.append("matched flapping, should reset flapping to zero {").append(instanceId);
				bf.append(":").append(metricId).append("}, flapping threshold is " + thresholdFlapping);
				logger.info(bf.toString());
			}
		} else {// 未满足Flapping次数，退出;
			if (logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer(100);
				bf.append("[");
				bf.append(instanceId);
				bf.append(",");
				bf.append(metricId);
				bf.append(",");
				bf.append(curMetricStateEnum);
				bf.append("]");
				bf.append(" wait to flapping[");
				bf.append(thresholdFlapping);
				bf.append(",current:");
				bf.append(flappingTimes);
				bf.append("]");
				logger.debug(bf.toString());
			}
			flappingMap.put(flapKey, flappingTimes);
			isFlapping = true;
		}

		return isFlapping;
	}

	public void removeFlapping(String key) {
		this.flappingMap.remove(key);
	}
}
