/**
 * 
 */
package com.mainsteam.stm.dataprocess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.util.InstanceProcessLogController;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;

/**
 * @author ziw
 */
public class MetricDataProcessEngineImpl implements MetricDataProcessEngine,
		BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private static final Log logger = LogFactory
			.getLog(MetricDataProcessEngineImpl.class);
	private Map<String, MetricCalculateData> repairDataes = new LinkedHashMap<>();
	private CustomMetricService customMetricService;
	private ResourceInstanceService resourceInstanceService;
	private CapacityService capacityService;
	private List<MetricDataProcessor> metricDataProcessors = new ArrayList<>();
	/** 标注是否忆完成对处理队列的排序 */
	private boolean isSort = false;

	private MetricDataService metricDataService;

	private Map<String, MetricDataProcessor> filteredMetricDataProcessor = new HashMap<>();

	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setCustomMetricService(CustomMetricService customMetricService) {
		this.customMetricService = customMetricService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

//	public static void main(String[] args) {
//		System.out.println(NumberUtils.isNumber("1.10"));
//		System.out.println(NumberUtils.isNumber("100"));
//		System.out.println(NumberUtils.isNumber("1.10f"));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngine#handle(
	 * com.mainsteam.stm.dataprocess.MetricCalculateData)
	 */
	@Override
	public void handle(MetricCalculateData data) {
		ResourceMetricDef rmd = null;
		CustomMetric cm = null;
		if (logger.isDebugEnabled())
			logger.debug("handle data:"
					+ JSON.toJSONString(data,
							SerializerFeature.WriteDateUseDateFormat));
		if(InstanceProcessLogController.getInstance().isLog(data.getResourceInstanceId()) && logger.isInfoEnabled()){
			logger.info("handle:" + data);
		}
		if (data.isCustomMetric()) {
			if (logger.isInfoEnabled())
				logger.info("find customer metricID:" + data.getMetricId());
			try {
				cm = customMetricService.getCustomMetric(data.getMetricId());
			} catch (CustomMetricException e) {
				if (logger.isErrorEnabled())
					logger.error(e.getMessage(), e);
			}
		} else {
			String metricID = data.getMetricId();

			rmd = capacityService.getResourceMetricDef(data.getResourceId(),
					metricID);
			if (rmd == null) {
				if (logger.isWarnEnabled())
					logger.warn("metric[" + data.getResourceInstanceId() + ","
							+ data.getMetricId()
							+ "] can't not find ResourceMetricDef,exit!");
				return;
			} else {
				if (StringUtils.isEmpty(metricID)) {
					if (logger.isWarnEnabled())
						logger.warn("metric[" + data.getResourceInstanceId()
								+ "," + data.getResourceId() + ","
								+ data.getMetricId()
								+ "] can't not find ,exit!");
					return;
				} else if (metricID.length() > 23
						&& rmd.getMetricType() == MetricTypeEnum.PerformanceMetric) {
					if (logger.isWarnEnabled())
						logger.warn("metricID[" + data.getResourceInstanceId()
								+ "," + data.getResourceId() + "," + metricID
								+ "]'s length over 23, exit!");
					return;
				}
			}
		}

		try {
			ResourceInstance ins = resourceInstanceService
					.getResourceInstance(data.getResourceInstanceId());
			if (ins == null
					|| ins.getLifeState() != InstanceLifeStateEnum.MONITORED) {
				logger.warn("instance[" + data.getResourceInstanceId()
						+ "] hasn't monitor or deleted.");
				return;
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
		}

		if (rmd != null
				&& MetricTypeEnum.PerformanceMetric == rmd.getMetricType()) {

			/******** 数字验证:非法数据置将为NULL ********/
			if (data.getMetricData() != null) {
				if (data.getMetricData().length < 1) {
					logger.error("not find[" + data.getResourceInstanceId()
							+ "," + data.getResourceId() + ","
							+ data.getMetricId()
							+ "] metric data. please check!");
				} else if (data.getMetricData()[0] != null) {
					if (!NumberUtils.isNumber(data.getMetricData()[0])) {
						logger.error("illega metric["
								+ data.getResourceInstanceId() + ","
								+ data.getResourceId() + ","
								+ data.getMetricId() + "] data:"
								+ data.getMetricData()[0]);
						data.setMetricData(new String[] { null });
					}
					// try {
					// Float.valueOf(data.getMetricData()[0]);
					// } catch (Exception e) {
					// logger.error("illega metric["
					// + data.getResourceInstanceId() + ","
					// + data.getResourceId() + ","
					// + data.getMetricId() + "] data:"
					// + data.getMetricData()[0]);
					// data.setMetricData(new String[] { null });
					// }
				}
			}
		}
		if (rmd != null) {
			String repairKey = data.getResourceInstanceId()
					+ data.getMetricId();
			String[] ndata = data.getMetricData();
			if ((ndata == null || ndata.length < 1 || StringUtils
					.isEmpty(ndata[0]))) {
				/**
				 * 针对信息指标，如果信息指标无值，则不对它进行处理。
				 */
				if (MetricTypeEnum.InformationMetric == rmd.getMetricType()) {
//					if (logger.isWarnEnabled()) {
//						StringBuilder b = new StringBuilder(
//								"information metric's value is empty.ignore it.");
//						b.append("instanceId=").append(
//								data.getResourceInstanceId());
//						b.append(" metricId=").append(data.getMetricId());
//						logger.warn(b);
//					}
					return;
				}

				/***** 补点功能 *****/
				if (!repairDataes.containsKey(repairKey)) {
					MetricData oldData;
					if (MetricTypeEnum.PerformanceMetric == rmd.getMetricType()) {
						oldData = metricDataService.getMetricPerformanceData(
								data.getResourceInstanceId(),
								data.getMetricId());
					} else if (MetricTypeEnum.AvailabilityMetric == rmd
							.getMetricType()) {
						oldData = metricDataService.getMetricAvailableData(
								data.getResourceInstanceId(),
								data.getMetricId());
					} else {
						oldData = metricDataService.getMetricInfoData(
								data.getResourceInstanceId(),
								data.getMetricId());
					}

					if (oldData != null) {
						data.setMetricData(oldData.getData());
					}
					repairDataes.put(repairKey, data);
				} else {
					MetricCalculateData oldData = repairDataes.get(repairKey);
					/**
					 * 补点时，不要忘了把旧数据的采集时间更新为新数据的采集时间，否则，插入历史指标数据时，会出现主键冲突。
					 * 
					 * modify by ziw at 2016年8月12日 上午10:47:57
					 */
					oldData.setCollectTime(data.getCollectTime());
					repairDataes.put(repairKey, data);
					data = oldData;
				}
			} else {
				// repairDataes.remove(data.getResourceInstanceId()
				// + data.getMetricId());
				repairDataes.put(repairKey, data);
			}
		}
		Map<String,Object> contextData = new HashMap<>();
		for (MetricDataProcessor dataProcessor : metricDataProcessors) {
			if (filteredMetricDataProcessor.containsKey(dataProcessor
					.getClass().getSimpleName())) {
				continue;
			}
			try {
				dataProcessor.process(data, rmd, cm, contextData);
			} catch (Exception e) {
				logger.error("dataProcessor[" + JSON.toJSONString(data)
						+ "] exception:" + e.getMessage(), e);
			}
		}
	}

	public void filteredMetricDataProcessor(String filterName) {
		if (filteredMetricDataProcessor.containsKey(filterName)) {
			return;
		}
		for (MetricDataProcessor dataProcessor : metricDataProcessors) {
			if (dataProcessor.getClass().getSimpleName()
					.equalsIgnoreCase(filterName)) {
				filteredMetricDataProcessor.put(filterName, dataProcessor);
			}
		}
	}

	public void unFilteredMetricDataProcessor(String filterName) {
		filteredMetricDataProcessor.remove(filterName);
	}

	public void clearMetricDataProcessors() {
		filteredMetricDataProcessor.clear();
	}

	public List<MetricDataProcessor> getMetricDataProcessors() {
		return new ArrayList<>(metricDataProcessors);
	}

	public Map<String, MetricDataProcessor> getFilteredMetricDataProcessor() {
		return new HashMap<>(filteredMetricDataProcessor);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof MetricDataProcessor) {
			if (logger.isInfoEnabled()) {
				logger.info("add metricDataProcessors[" + beanName + "]:"
						+ bean.getClass().getName());
			}

			metricDataProcessors.add((MetricDataProcessor) bean);
		}
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!isSort) {
			synchronized (this) {
				if (!isSort) {
					Collections.sort(metricDataProcessors,
							new Comparator<MetricDataProcessor>() {
								@Override
								public int compare(MetricDataProcessor o1,
										MetricDataProcessor o2) {
									return o1.getOrder() == o2.getOrder() ? 0
											: (o1.getOrder() > o2.getOrder() ? 1
													: -1);
								}
							});
					if (logger.isInfoEnabled()) {
						StringBuilder sb = new StringBuilder(
								"sorted metricDataProcessors:");
						for (MetricDataProcessor p : metricDataProcessors) {
							sb.append(p.getClass().getName()).append(",");
						}
						logger.info(sb);
					}
				}
				isSort = true;
			}
		}
	}
}
