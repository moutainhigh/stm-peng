package com.mainsteam.stm.executor.generate.callback;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.generate.detail.PluginConvertParameterGenerator;
import com.mainsteam.stm.executor.generate.detail.PluginExecutorParameterGenerator;
import com.mainsteam.stm.executor.generate.detail.PluginInitParameterGenerator;
import com.mainsteam.stm.executor.generate.detail.PluginProcessParameterGenerator;
import com.mainsteam.stm.executor.generate.detail.PluginResultMetaInfoGenerator;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class PluginRequestLazyGeneratorCallbackFactory {

	private static final Log logger = LogFactory
			.getLog(PluginRequestLazyGeneratorCallbackFactory.class);

	private PluginInitParameterGenerator pluginInitParameterGenerator;
	private PluginProcessParameterGenerator pluginProcessParameterGenerator;
	private PluginConvertParameterGenerator pluginConvertParameterGenerator;
	private PluginExecutorParameterGenerator pluginExecutorParameterGenerator;
	private PluginResultMetaInfoGenerator pluginResultMetaInfoGenerator;

	public PluginRequestLazyGeneratorCallbackFactory() {
	}

	public void setPluginInitParameterGenerator(
			PluginInitParameterGenerator pluginInitParameterGenerator) {
		this.pluginInitParameterGenerator = pluginInitParameterGenerator;
	}

	public void setPluginProcessParameterGenerator(
			PluginProcessParameterGenerator pluginProcessParameterGenerator) {
		this.pluginProcessParameterGenerator = pluginProcessParameterGenerator;
	}

	public void setPluginConvertParameterGenerator(
			PluginConvertParameterGenerator pluginConvertParameterGenerator) {
		this.pluginConvertParameterGenerator = pluginConvertParameterGenerator;
	}

	public void setPluginExecutorParameterGenerator(
			PluginExecutorParameterGenerator pluginExecutorParameterGenerator) {
		this.pluginExecutorParameterGenerator = pluginExecutorParameterGenerator;
	}

	public void setPluginResultMetaInfoGenerator(
			PluginResultMetaInfoGenerator pluginResultMetaInfoGenerator) {
		this.pluginResultMetaInfoGenerator = pluginResultMetaInfoGenerator;
	}

	public PluginRequestLazyGeneratorCallback newCallback(
			final Map<String, String> discoveryParameterMap,
			final String resourceId,
			final long instanceId,
			final Long cacheKey,
			final String metricId,
			final String discoverWay,
			final Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap,
			final boolean customMetric, final MetricCollect input_collect,
			final PluginRequest request) {
		PluginRequestLazyGeneratorCallback callback = new PluginRequestLazyGeneratorCallback() {
			@Override
			public void generate() throws Exception {
				MetricCollect collect = input_collect;
				if (customMetric == false) {
					pluginResultMetaInfoGenerator.generatePluginResultMetaInfo(
							request, collect);
				}
				
				if (instanceId > 0 && pluginInitDataMap != null
						&& customMetric == false) {
					Long key = cacheKey;
					synchronized (pluginInitDataMap) {
						if (pluginInitDataMap.containsKey(key)) {
							Map<String, PluginInitParameter> pluginMap = pluginInitDataMap
									.get(key);
							if (pluginMap.containsKey(request.getPluginId())) {
								request.setPluginInitParameter(pluginMap
										.get(request.getPluginId()));
							} else {
								pluginInitParameterGenerator
										.generatePluginInitParameter(request,
												discoveryParameterMap, collect);
								pluginMap.put(request.getPluginId(),
										request.getPluginInitParameter());
							}
						} else {
							Map<String, PluginInitParameter> pluginMap = new HashMap<>();
							pluginInitParameterGenerator
									.generatePluginInitParameter(request,
											discoveryParameterMap, collect);
							pluginMap.put(request.getPluginId(),
									request.getPluginInitParameter());
							pluginInitDataMap.put(key, pluginMap);
						}
					}
				} else {
					pluginInitParameterGenerator.generatePluginInitParameter(
							request, discoveryParameterMap, collect);
				}
				try {
					pluginExecutorParameterGenerator
							.generatePluginExecutorParameter(request,
									instanceId, collect, discoveryParameterMap);
					pluginProcessParameterGenerator
							.generatePluginProcessParameter(request,
									instanceId, discoveryParameterMap, collect);
					pluginConvertParameterGenerator
							.generatePluginConvertParameter(request,
									instanceId, discoveryParameterMap, collect);
				} catch (BaseException e) {
					if (logger.isErrorEnabled()) {
						logger.error("bindPluginRequest", e);
					}
					throw new MetricExecutorException(e);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("bindPluginRequest", e);
					}
					StringBuilder b = new StringBuilder();
					b.append(" resourceId=").append(resourceId)
							.append(" instanceId=").append(instanceId)
							.append(" discoveryParameterMap=")
							.append(discoveryParameterMap);
					throw new MetricExecutorException(
							ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST,
							b.toString(), e);
				}
			}
		};
		return callback;
	}
}
