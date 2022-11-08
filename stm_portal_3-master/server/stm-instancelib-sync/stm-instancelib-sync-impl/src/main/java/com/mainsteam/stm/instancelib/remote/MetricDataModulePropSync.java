package com.mainsteam.stm.instancelib.remote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.metric.obj.CustomMetric;

/**
 * 
 * 指标值有改变（模型属性有改变需要同步修改实例库的属性值）
 * 
 * @author xiaoruqiang
 * 
 */
public class MetricDataModulePropSync implements MetricDataProcessor {

	private static final Log logger = LogFactory
			.getLog(MetricDataModulePropSync.class);
	private static final String IFSPEED = "ifSpeed";
	private ModulePropService modulePropService;
	private CustomModulePropService customModulePropService;
	
	public void setCustomModulePropService(
			CustomModulePropService customModulePropService) {
		this.customModulePropService = customModulePropService;
	}

	/**
	 * 使用hash来减少循环
	 * 
	 * 
	 * Map<resourceId,Map<metricId,propId>>
	 */
	private Map<String, Map<String, String>> resourceModulePropMap = new HashMap<String, Map<String, String>>(
			2000);

	private LinkedBlockingQueue<ModuleProp> toUpdateModuleProps;

	private Runnable worker;

	private static boolean started = false;

	public MetricDataModulePropSync() {
	}

	public synchronized void start() {
		if (started) {
			return;
		}
		toUpdateModuleProps = new LinkedBlockingQueue<>();
		worker = new Runnable() {
			@Override
			public void run() {
				while (started) {
					ModuleProp m = null;
					try {
						m = toUpdateModuleProps.take();
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("run", e);
						}
					}
					if (m != null) {
						try {
							dealModuleProp(m);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("run", e);
							}
						}
					}
				}
			}
		};
		started = true;
		new Thread(worker, "MetricDataModulePropSyncWorker").start();
	}

	public synchronized void stop() {
		started = false;
		try {
			toUpdateModuleProps.put(null);
		} catch (InterruptedException e) {
		}
	}

	public void setModulePropService(ModulePropService modulePropService) {
		this.modulePropService = modulePropService;
	}

	/**
	 * order 被执行的顺序。值越大，优先级越低。
	 */
	@Override
	public int getOrder() {
		return 100;
	}

	private static boolean equalsArray(String[] a, String[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * @return the toUpdateModuleProps
	 */
	public final LinkedBlockingQueue<ModuleProp> getToUpdateModuleProps() {
		return toUpdateModuleProps;
	}

	private void dealModuleProp(ModuleProp prop) throws InstancelibException {
		ModuleProp oldProp = modulePropService.getPropByInstanceAndKey(
				prop.getInstanceId(), prop.getKey());
		
		if (oldProp == null
				|| equalsArray(oldProp.getValues(), prop.getValues())) {
			// 没有模型属性，或者模型属性跟之前值一样，不做更新操作
			return;
		}
		
		CustomModuleProp rim = customModulePropService.getCustomModulePropByInstanceIdAndKey(prop.getInstanceId(), prop.getKey());
		/*
		 *  判断 接口带宽 是否用户自定义
		 *  如果有设置自定义的值，采集回来值不修改模型属性值。直接用用户定义值
		 */
		
		if(rim != null && IFSPEED.equals(prop.getKey()) && prop.getInstanceId() == rim.getInstanceId()) {
			return;
		}
		
		/*
		 * 如果是模型属性，需要同步到采集器。
		 */
		try {
			modulePropService.updateProp(prop);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("MetricDataPersistence error!", e);
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder str = new StringBuilder(100);
			str.append("Update ModuleProp ");
			str.append(" InstanceId=");
			str.append(oldProp.getInstanceId());
			str.append(" ModulePropId=");
			str.append(oldProp.getKey());
			str.append(" MetricData=[");
			str.append(Arrays.toString(prop.getValues()));
			str.append("]");
			logger.info(str);
		}
	}

	/**
	 * Map 表示几个计算机的之间的关系，不需要关注。
	 */
	@Override
	public MetricDataPersistence process(
			MetricCalculateData metricCalculateData, ResourceMetricDef rdf,
			CustomMetric cm, Map<String, Object> arg1) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("monitor and sync instance's module prop.");
		}
		if (rdf == null) {
			return null;
		}

		/**
		 * 只有信息指标才会配置模型属性。但是信息指标不会一定有模型属性。
		 */
		if (rdf.getMetricType() != MetricTypeEnum.InformationMetric) {
			return null;
		}

		/**
		 * 因为性能指标可能会取不到值，这里过滤一下，如果值为空，则不更新。防止出现属性值不存在的情况。
		 */
		String[] metricData = metricCalculateData.getMetricData();
		if (metricData == null) {
			return null;
		}
		String modulePropId = getModulePropId(rdf.getResourceDef(),
				metricCalculateData.getMetricId());
		if (modulePropId != null) {
//			if (logger.isInfoEnabled()) {
//				logger.info("Update ModuleProp metricId="
//						+ metricCalculateData.getMetricId());
//			}
			ModuleProp prop = new ModuleProp();
			prop.setInstanceId(metricCalculateData.getResourceInstanceId());
			// prop.setKey(metricCalculateData.getMetricId());
			prop.setKey(modulePropId);
			prop.setValues(metricCalculateData.getMetricData());
			toUpdateModuleProps.put(prop);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("collect metric data to collector end");
		}
		return null;
	}

	/**
	 * TODO:to be called
	 * 
	 * @param resourceId
	 */
	public synchronized void clearResourceProp(String resourceId) {
		resourceModulePropMap.remove(resourceId);
	}

	/**
	 * 查询指标对应的模型属性，并缓存起来，减少cpu消耗。但是得考虑模型部署造成的影响。
	 * 
	 * TODO:如果模型部署发生，模型文件被动态重载，而且系统没有重启，需要将resourceModulePropMap对应的模型重新load。解决方案
	 * ：监听模型重载，调用clearResourceProp方法。
	 * 
	 * 
	 * @param def
	 * @param metricId
	 * @return
	 */
	private String getModulePropId(ResourceDef def, String metricId) {
		String resourceId = def.getId();
		Map<String, String> propMap = resourceModulePropMap.get(resourceId);
		if (propMap == null) {
			synchronized (this) {
				propMap = resourceModulePropMap.get(resourceId);
				if (propMap == null) {
					ResourcePropertyDef[] propdefs = def.getPropertyDefs();
					if (propdefs != null && propdefs.length > 0) {
						propMap = new HashMap<>(propdefs.length);
						for (ResourcePropertyDef resourcePropertyDef : propdefs) {
							if (resourcePropertyDef.getResourceMetric() != null) {
								propMap.put(resourcePropertyDef
										.getResourceMetric().getId(),
										resourcePropertyDef.getId());
							}
						}
					} else {
						propMap = new HashMap<>(0);
					}
					resourceModulePropMap.put(resourceId, propMap);
					if (logger.isInfoEnabled()) {
						logger.info("getModulePropId propMap=" + propMap
								+ " resourceId=" + resourceId);
					}
				}
			}
		}
		return propMap.get(metricId);
	}
}
