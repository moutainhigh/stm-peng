/**
 * 
 */
package com.mainsteam.stm.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginConnectSetting;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginParameter;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.CustomMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.executor.obj.PluginInitParameterData;
import com.mainsteam.stm.executor.sequence.PluginRequestBatchNumberGenerator;
import com.mainsteam.stm.executor.sequence.PluginRequestIdGenerator;
import com.mainsteam.stm.executor.util.EncryptUtil;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginMapExecutorParameter;

/**
 * @author ziw
 * 
 */
public class PluginRequestGenerator {

	private static final Log logger = LogFactory
			.getLog(PluginRequestGenerator.class);

	private static final String PLUGIN_SNMP_ID = "SnmpPlugin";

	private CapacityService capacityService;
	private PluginRequestIdGenerator idGenerator;
	private ResourceInstanceService resourceInstanceService;
	private ModulePropService modulePropService;
	private PluginRequestBatchNumberGenerator batchNumberGenerator;
	private MetricExecuteFilter[] executeFilters;
	private EncryptUtil encryptUtil;
	private InstanceCollectorLogController collectorLogController = InstanceCollectorLogController
			.getInstance();

	public PluginRequestGenerator(CapacityService capacityService,
			PluginRequestIdGenerator idGenerator,
			ResourceInstanceService resourceInstanceService,
			ModulePropService modulePropService,
			PluginRequestBatchNumberGenerator batchNumberGenerator)
			throws Exception {
		super();
		this.capacityService = capacityService;
		this.idGenerator = idGenerator;
		this.batchNumberGenerator = batchNumberGenerator;
		this.resourceInstanceService = resourceInstanceService;
		this.modulePropService = modulePropService;
		this.encryptUtil = new EncryptUtil();
		this.encryptUtil.setKey(PluginRequestGenerator.PLUGIN_SNMP_ID);
	}

	/**
	 * @param executeFilters
	 *            the executeFilters to set
	 */
	protected final void setExecuteFilters(MetricExecuteFilter[] executeFilters) {
		this.executeFilters = executeFilters;
	}

	protected List<PluginRequest>[] buildPluginRequestsByDiscovery(
			List<MetricDiscoveryParameter> bindParameters, String discoveryWay)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("buildPluginRequests start");
		}
		@SuppressWarnings("unchecked")
		List<PluginRequest>[] reslult = new List[2];
		List<PluginRequest> pluginRequests = new ArrayList<>(
				bindParameters.size());
		long batch = batchNumberGenerator
				.generateBatch(PluginRequestEnum.discovery);
		Date currentDate = new Date();
		Map<String, PluginRequest> sesionClosePluginRequest = new HashMap<>();
		for (MetricDiscoveryParameter parameter : bindParameters) {
			PluginRequest req = buildPluginRequestByDiscovery(parameter, batch,
					discoveryWay);
			req.setCollectTime(currentDate);
			if (!sesionClosePluginRequest
					.containsKey(req.getPluginSessionKey())) {
				PluginRequest request = new PluginRequest();
				request.setPluginSessionKey(req.getPluginSessionKey());
				request.setPluginRequestType(PluginRequestEnum.discovery_end);
				sesionClosePluginRequest
						.put(req.getPluginSessionKey(), request);
			}
			pluginRequests.add(req);
		}
		reslult[0] = pluginRequests;
		reslult[1] = new ArrayList<>(sesionClosePluginRequest.values());
		if (logger.isTraceEnabled()) {
			logger.trace("buildPluginRequests end");
		}
		return reslult;
	}

	/**
	 * 构建pluginReqequest对象，绑定执行request的参数
	 * 
	 * @param p
	 * @param batch
	 * @param discoveryWay
	 * @return
	 * @throws MetricExecutorException
	 */
	private PluginRequest buildPluginRequestByDiscovery(
			MetricDiscoveryParameter p, long batch, String discoveryWay)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("buildPluginRequestByDiscovery start when discovery.");
		}
		ResourceMetricDef metricDef = capacityService.getResourceMetricDef(
				p.getResourceId(), p.getMetricId());
		if (metricDef == null) {
			StringBuilder b = new StringBuilder();
			b.append("ResourceMetricDef is null(resourceId=")
					.append(p.getResourceId()).append(" metricId=")
					.append(p.getMetricId());
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_METRIC_NOT_EXIST,
					b.toString());
		}
		MetricCollect collect = null;
		Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap = null;
		PluginRequest request = bindPluginRequest(p.getDiscoveryInfo(),
				p.getResourceId(), -1, p.getMetricId(), discoveryWay,
				pluginInitDataMap, false, collect);
		request.setPluginRequestType(PluginRequestEnum.discovery);
		request.setBatch(batch);
		if (logger.isTraceEnabled()) {
			logger.trace("buildPluginRequestByDiscovery end when discovery");
		}
		return request;
	}

	private String buildPluginSessionKey(MetricCollect collect,
			Map<String, String> discoveryInfo) throws MetricExecutorException {
		com.mainsteam.stm.caplib.plugin.PluginInitParameter[] initParameters = collect
				.getPlugin().getPluginInitParameters();
		StringBuilder builder = new StringBuilder();
		builder.append(collect.getPlugin().getId()).append('|');
		for (int i = 0; i < initParameters.length; i++) {
			com.mainsteam.stm.caplib.plugin.PluginInitParameter p = initParameters[i];
			if (p.isSessionKey()) {
				String pValue = discoveryInfo.get(p.getId());
				// if (logger.isDebugEnabled()) {
				// StringBuilder debugBuilder = new StringBuilder(
				// "buildPluginSessionKey ");
				// debugBuilder.append(p.getId()).append('=').append(pValue)
				// .append(" defaultValue=")
				// .append(p.getDefaultValue());
				// logger.debug(debugBuilder);
				// }
				if (pValue == null) {
					pValue = p.getDefaultValue();
					if (pValue == null) {
						pValue = "";
					}
				}
				// else {
				// if (p.isPassword()) {
				// pValue = encryptUtil.encrypt(pValue);
				// }
				// }
				builder.append(pValue).append('|');
			}
		}
		// if (logger.isDebugEnabled()) {
		// logger.debug("buildPluginSessionKey sesionKey=" + builder);
		// }
		return builder.toString();
	}

	private void bindPluginInitParameter(PluginRequest request,
			Map<String, String> parameterMap, MetricCollect collect) {
		PluginInitParameterData parameter = new PluginInitParameterData();
		if (collect != null) {
			PluginConnectSetting[] connectSettings = collect
					.getPluginConnectSettings();
			if (connectSettings != null && connectSettings.length > 0) {
				for (int i = 0; i < connectSettings.length; i++) {
					if (parameterMap.containsKey(connectSettings[i]
							.getParameterId())) {
						continue;
					}
					parameterMap.put(connectSettings[i].getParameterId(),
							connectSettings[i].getParameterValue());
				}
			}
		}
		parameter.setParameter(parameterMap);
		request.setPluginInitParameter(parameter);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends PluginResultSetProcessor> getProcessorClass(
			String className) throws MetricExecutorException {
		try {
			return (Class<? extends PluginResultSetProcessor>) this.getClass()
					.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProcessorClass className=" + className, e);
			}
			throw new MetricExecutorException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Class<? extends PluginResultSetConverter> getConverterClass(
			String className) throws MetricExecutorException {
		try {
			return (Class<? extends PluginResultSetConverter>) this.getClass()
					.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProcessorClass className=" + className, e);
			}
			throw new MetricExecutorException(e);
		}
	}

	private ParameterValue bindParameterValue(long resourceInstId,
			ParameterDef def, Map<String, String> map) throws Exception {
		ParameterValue value = new ParameterValue();
		value.setKey(def.getKey());
		value.setType(def.getType() == null ? null : def.getType().name());
		if (ValueTypeEnum.DiscoveryInfo == def.getType()) {
			/* 从发现信息里拿值 */
			value.setValue(map.get(def.getValue()));
		} else if (ValueTypeEnum.ResourceProperty == def.getType()) {
			// 从资源实例的模型属性里边拿值
			if (resourceInstId >= 0) {
				ModuleProp prop = modulePropService.getPropByInstanceAndKey(
						resourceInstId, def.getValue());
				if (prop != null) {
					String[] propValues = prop.getValues();
					if (propValues == null || propValues.length <= 0) {
						if (logger.isWarnEnabled()) {
							StringBuilder b = new StringBuilder();
							b.append("bindParameterValue prop no value.");
							b.append(" resourceInstId=").append(resourceInstId);
							b.append(" def.key=").append(def.getKey());
							b.append(" propKey=def.value=")
									.append(def.getKey());
							logger.warn(b.toString());
						}
					} else if (propValues.length == 1) {
						value.setValue(propValues[0]);
					} else {
						StringBuilder b = new StringBuilder();
						b.append(propValues[0]);
						int length = propValues.length;
						for (int i = 1; i < length; i++) {
							b.append(',').append(propValues[i]);
						}
						value.setValue(b.toString());
					}
				} else {
					if (logger.isErrorEnabled()) {
						StringBuilder b = new StringBuilder();
						b.append("bindParameterValue prop no exist.");
						b.append(" resourceInstId=").append(resourceInstId);
						b.append(" def.key=").append(def.getKey());
						b.append(" propKey=def.value=").append(def.getKey());
						logger.error(b.toString());
					}
				}
			}
		} else {
			value.setValue(def.getValue());
		}
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("bindParameterValue ");
			b.append(" resourceInstId=").append(resourceInstId);
			b.append(" def.key=").append(def.getKey());
			b.append(" def.value=").append(def.getKey());
			b.append(" resultKey=").append(value.getKey());
			/**
			 * 禁用日志，防止出现密码
			 */
			// b.append(" resultValue=").append(value.getValue());
			logger.debug(b.toString());
		}
		return value;
	}

	private void bindPluginExecutorParameter(PluginRequest request,
			long resourceInstanceId, Map<String, String> param,
			MetricCollect collect) throws Exception {
		if (collect == null) {
			return;
		}
		PluginParameter parameter = collect.getPluginParameter();
		if (parameter == null) {
			return;
		}
		ParameterDef[] parameterDefs = parameter.getParameters();
		/* map类型参数 */
		if (ParameterTypeEnum.MapType == parameter.getType()) {
			PluginMapExecutorParameter executorParameter = new PluginMapExecutorParameter();
			if (parameterDefs != null && parameterDefs.length > 0) {
				for (ParameterDef def : parameterDefs) {
					ParameterValue value = bindParameterValue(
							resourceInstanceId, def, param);
					executorParameter.addParameter(value.getKey(), value);
				}
			}
			request.setPluginExecutorParameter(executorParameter);
		} else {
			/* 数组类型参数 */
			PluginArrayExecutorParameter pluginArrayExecutorParameter = new PluginArrayExecutorParameter();
			if (parameterDefs != null && parameterDefs.length > 0) {
				Parameter[] parameterArray = new Parameter[parameterDefs.length];
				for (int i = 0; i < parameterDefs.length; i++) {
					ParameterValue value = bindParameterValue(
							resourceInstanceId, parameterDefs[i], param);
					parameterArray[i] = value;
				}
				pluginArrayExecutorParameter.setParameters(parameterArray);
			}
			request.setPluginExecutorParameter(pluginArrayExecutorParameter);
		}
	}

	private void bindPluginHandlersParameter(PluginRequest request,
			long resourceInstanceId, Map<String, String> param,
			MetricCollect collect) throws Exception {
		if (collect == null) {
			return;
		}
		PluginDataHandler[] dataHandlers = collect.getPluginDataHandlers();
		if (dataHandlers != null && dataHandlers.length > 0) {
			PluginProcessParameter[] pluginProcessParameters = new PluginProcessParameter[dataHandlers.length];
			for (int j = 0; j < dataHandlers.length; j++) {
				PluginDataHandler pluginDataHandler = dataHandlers[j];
				PluginProcessParameter pluginProcessParameter = new PluginProcessParameter();
				pluginProcessParameters[j] = pluginProcessParameter;
				String className = pluginDataHandler.getClassFullName();
				if (className == null || className.equals("")) {
					throw new MetricExecutorException(
							ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_EMPTY_PROCESSER_CLASS_NAME,
							"pluginDataHandler.getClassFullName not exist.");
				}
				pluginProcessParameter
						.setProcessorClass(getProcessorClass(className));
				ParameterDef[] parameters = pluginDataHandler
						.getParameterDefs();
				ProcessParameter parameter = new ProcessParameter();
				pluginProcessParameter.setParameter(parameter);

				if (parameters != null && parameters.length > 0) {
					for (ParameterDef def : parameters) {
						ParameterValue value = bindParameterValue(
								resourceInstanceId, def, param);
						parameter.addParameter(value);
					}
				}
			}
			request.setPluginProcessParameters(pluginProcessParameters);
		}
	}

	private void bindPluginConvertParameter(PluginRequest request,
			long resourceInstanceId, Map<String, String> param,
			MetricCollect collect) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("bindPluginConvertParameter start");
		}
		if (collect == null) {
			return;
		}
		PluginDataConverter converter = collect.getPluginDataConverter();
		if (converter == null) {
			return;
		}
		PluginConvertParameter convertParameter = new PluginConvertParameter();
		convertParameter.setConverterClass(getConverterClass(converter
				.getClassFullName()));
		ParameterDef[] parameters = converter.getParameterDefs();
		ProcessParameter parameter = new ProcessParameter();
		if (parameters != null && parameters.length > 0) {
			for (ParameterDef def : parameters) {
				if (logger.isDebugEnabled()) {
					logger.debug("bindPluginConvertParameter bindPluginConvertParameter key="
							+ def.getKey()
							+ " value="
							+ def.getValue()
							+ " type=" + def.getType());
				}
				ParameterValue value = bindParameterValue(resourceInstanceId,
						def, param);
				parameter.addParameter(value);
				if (ValueTypeEnum.ResourceProperty == def.getType()
						&& "InstPropertyKey".equals(def.getKey())) {
					request.setConvertParameterIndexProperty(def.getValue());
				}
			}
		}
		convertParameter.setParameter(parameter);
		request.setPluginConvertParameter(convertParameter);
		if (logger.isTraceEnabled()) {
			logger.trace("bindPluginConvertParameter end");
		}
	}

	public List<PluginRequest> generatePluginRequest(
			List<MetricExecuteParameter> parameters)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("generatePluginRequest start");
		}
		List<PluginRequest> pluginRequests = new ArrayList<>(parameters.size());
		int size = parameters.size() / 20;
		if (size < 20) {
			size = 20;
		}
		Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap = new HashMap<>(
				size);
		Map<Long, Map<String, String>> discoverysDataMap = new HashMap<>(size);
		long batch = batchNumberGenerator
				.generateBatch(PluginRequestEnum.monitor);
		MetricCollect collect = null;

		for (MetricExecuteParameter metricExecuteParameter : parameters) {

			/**
			 * 过滤执行的参数
			 */
			if (executeFilters != null && executeFilters.length > 0) {
				MetricExecuteFilter f = null;
				for (MetricExecuteFilter filter : executeFilters) {
					if (filter.filter(metricExecuteParameter)) {
						f = filter;
						break;
					}
				}
				if (f != null) {
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder();
						b.append("metricExecuteParameter resourceInstId=")
								.append(metricExecuteParameter
										.getResourceInstanceId());
						b.append(" metricId=").append(
								metricExecuteParameter.getMetricId());
						b.append(" filtered by ").append(f);
						logger.info(b);
					}
					continue;
				}
			}
			PluginRequest request = null;
			if (metricExecuteParameter instanceof CustomMetricExecuteParameter) {
				try {
					request = generatePluginRequest(
							((CustomMetricExecuteParameter) metricExecuteParameter)
									.getPluginParameter(), batch,
							discoverysDataMap, pluginInitDataMap);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(e.getMessage(), e);
					}
				}
			} else {
				Long instanceId = metricExecuteParameter
						.getResourceInstanceId();
				String metricId = metricExecuteParameter.getMetricId();
				try {
					request = generatePluginRequest(instanceId, metricId,
							batch, discoverysDataMap, pluginInitDataMap, false,
							collect);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			if (request == null) {
				if (logger.isErrorEnabled()) {
					StringBuilder b = new StringBuilder(
							"generatePluginRequest request generate failure.");
					b.append("instanceId=").append(
							metricExecuteParameter.getResourceInstanceId());
					b.append("metricId=").append(
							metricExecuteParameter.getMetricId());
					logger.error(b.toString());
				}
			} else {
				request.setCollectTime(metricExecuteParameter.getExecuteTime());
				request.setProfileId(metricExecuteParameter.getProfileId());
				request.setTimelineId(metricExecuteParameter.getTimelineId());
				if (collectorLogController.isLog(request.getResourceInstId())
						&& logger.isInfoEnabled()) {
					String msg = buildPrintPluginRequestResult(request);
					logger.info("generatePluginRequest" + msg);
				}
				pluginRequests.add(request);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("generatePluginRequest end");
		}
		return pluginRequests;
	}

	public static String buildPrintPluginRequestResult(PluginRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(" request.getRequestId()=")
				.append(request.getRequestId()).append(" request.getBatch()=")
				.append(request.getBatch())
				.append(" request.getPluginRequestType()=")
				.append(request.getPluginRequestType())
				.append(" request.getResourceId()=")
				.append(request.getResourceId())
				.append(" request.getResourceInstId()=")
				.append(request.getResourceInstId())
				.append(" request.getMetricId()=")
				.append(request.getMetricId());
		return builder.toString();
	}

	private PluginRequest generatePluginRequest(Long instanceId,
			String metricId, long batch,
			Map<Long, Map<String, String>> discoverysDataMap,
			Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap,
			boolean customMetric, MetricCollect collect)
			throws MetricExecutorException {
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("execute construct plugin request instanceId=");
			b.append(instanceId);
			b.append(" metricId=");
			b.append(metricId);
			logger.debug(b.toString());
		}
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(instanceId);
		} catch (Exception e) {
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_INSTANCE_SELECT_EXCEPTION,
					"getResourceInstance[" + instanceId + ']', e);
		}
		if (instance == null) {
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_INSTANCE_NOT_EXIST,
					"can not find resourceInstance[" + instanceId + ']');
		}
		if (instance.getDiscoverProps() == null) {
			// 如果是子资源的发现属性没有值。从父资源的里边取值。父资源没有发现属性，才抛出异常
			if (instance.getParentId() != 0) {
				try {
					ResourceInstance parentInstance = resourceInstanceService
							.getResourceInstance(instance.getParentId());
					if (parentInstance != null) {
						instance.setParentInstance(parentInstance);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("getResourceInstance ", e);
					}
				}

			}
			// 从父资源中获取
			if (instance.getDiscoverProps() == null) {
				throw new MetricExecutorException(
						ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_INSTANCE_NOT_EXIST,
						"can not find resourceInstance[" + instanceId + ']'
								+ " discoverProp");
			}
		}
		String resourceId = instance.getResourceId();
		DiscoverWayEnum discoverWay = instance.getDiscoverWay();
		Map<String, String> discoveryParameterMap = null;
		if (discoverysDataMap != null) {
			if (discoverysDataMap.containsKey(instanceId)) {
				discoveryParameterMap = discoverysDataMap.get(instanceId);
				if (customMetric) {
					/**
					 * 自定义指标在方法外已经为discoveryParameterMap赋值，并且只是部分值。需要在这里对这些值补全。
					 */
					for (DiscoverProp pp : instance.getDiscoverProps()) {
						if (discoveryParameterMap.containsKey(pp.getKey())) {
							continue;
						}
						discoveryParameterMap.put(pp.getKey(),
								StringUtils.join(pp.getValues(), ","));
					}
				}
			} else {
				discoveryParameterMap = new HashMap<String, String>();
				for (DiscoverProp pp : instance.getDiscoverProps()) {
					discoveryParameterMap.put(pp.getKey(),
							StringUtils.join(pp.getValues(), ","));
				}
				discoverysDataMap.put(instanceId, discoveryParameterMap);
			}
		} else {
			discoveryParameterMap = new HashMap<String, String>();
			for (DiscoverProp pp : instance.getDiscoverProps()) {
				discoveryParameterMap.put(pp.getKey(),
						StringUtils.join(pp.getValues(), ","));
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("generatePluginRequest find discoverWay:"
					+ discoverWay);
		}
		PluginRequest request = bindPluginRequest(discoveryParameterMap,
				resourceId, instanceId, metricId, discoverWay == null ? null
						: discoverWay.name(), pluginInitDataMap, customMetric,
				collect);
		request.setBatch(batch);
		return request;
	}

	public List<PluginRequest> generateInstanceMetricPluginRequest(
			List<InstanceMetricExecuteParameter> parameters)
			throws MetricExecutorException {
		Map<Long, Map<String, String>> discoverysDataMap = null;
		Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap = null;
		long batch = batchNumberGenerator
				.generateBatch(PluginRequestEnum.immediate);
		List<PluginRequest> requests = new ArrayList<>(parameters.size());
		Date currentDate = new Date();
		MetricCollect collect = null;
		for (InstanceMetricExecuteParameter parameter : parameters) {
			PluginRequest request = generatePluginRequest(
					parameter.getResourceInstanceId(), parameter.getMetricId(),
					batch, discoverysDataMap, pluginInitDataMap, false, collect);
			request.setCollectTime(currentDate);
			requests.add(request);
		}
		return requests;
	}

	public PluginRequest generatePluginRequest(Long instanceId, String metricId)
			throws MetricExecutorException {
		Map<Long, Map<String, String>> discoverysDataMap = null;
		Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap = null;
		MetricCollect collect = null;
		long batch = batchNumberGenerator
				.generateBatch(PluginRequestEnum.immediate);
		PluginRequest request = generatePluginRequest(instanceId, metricId,
				batch, discoverysDataMap, pluginInitDataMap, false, collect);
		return request;
	}

	public PluginRequest generatePluginRequest(
			CustomMetricPluginParameter parameter)
			throws MetricExecutorException {
		long batch = batchNumberGenerator
				.generateBatch(PluginRequestEnum.immediate);
		Map<Long, Map<String, String>> discoverysDataMap = null;
		Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap = null;
		PluginRequest request = generatePluginRequest(parameter, batch,
				discoverysDataMap, pluginInitDataMap);
		if (request == null) {
			return null;
		}
		request.setPluginRequestType(PluginRequestEnum.immediate);
		return request;
	}

	private PluginRequest generatePluginRequest(
			CustomMetricPluginParameter parameter, long batch,
			Map<Long, Map<String, String>> discoverysDataMap,
			Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap)
			throws MetricExecutorException {
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("execute construct CustomMetricPluginParameter plugin request instanceId=");
			b.append(parameter.getInstanceId());
			b.append(" metricId=");
			b.append(parameter.getCustomMetricId());
			logger.debug(b.toString());
		}

		PluginDef pluginDef = null;
		String realPluginId = parameter.getPluginId();
		if (PluginIdEnum.SshPlugin.name().equals(parameter.getPluginId())
				|| PluginIdEnum.TelnetPlugin.name().equals(
						parameter.getPluginId())) {
			try {
				ResourceInstance instance = resourceInstanceService
						.getResourceInstance(parameter.getInstanceId());
				if (instance == null) {
					if (logger.isErrorEnabled()) {
						logger.error("generatePluginRequest instance not exist.instanceId="
								+ parameter.getInstanceId());
					}
					return null;
				}
				DiscoverWayEnum wayEnum = instance.getDiscoverWay();
				if (wayEnum != null) {
					if (DiscoverWayEnum.TELNET == wayEnum) {
						realPluginId = PluginIdEnum.TelnetPlugin.name();
					} else if (DiscoverWayEnum.SSH == wayEnum) {
						realPluginId = PluginIdEnum.SshPlugin.name();
					}
				}
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("generatePluginRequest", e);
				}
				return null;
			}
		} else if (PluginIdEnum.UrlPlugin.name()
				.equals(parameter.getPluginId())) {
			Map<String, String> initParaemter = new HashMap<>();
			ParameterValue[] pluginParameters = parameter.getParameters();
			if (pluginParameters != null && pluginParameters.length > 0) {
				for (int i = 0; i < pluginParameters.length; i++) {
					if ("urlSite".equals(pluginParameters[i].getKey())) {
						initParaemter.put("urlSite",
								pluginParameters[i].getValue());
					}
				}
				ParameterValue[] newValues = new ParameterValue[pluginParameters.length + 1];
				System.arraycopy(pluginParameters, 0, newValues, 0,
						pluginParameters.length);
				ParameterValue v = new ParameterValue();
				v.setKey("COMMAND");
				v.setValue("com.mainsteam.stm.plugin.url.UrlCollectorUtil.availability");
				newValues[pluginParameters.length] = v;
				parameter.setParameters(newValues);
			}
			initParaemter.put("urlName",
					"customURLMetric-" + parameter.getCustomMetricId());
			discoverysDataMap.put(parameter.getInstanceId(), initParaemter);
		}
		pluginDef = capacityService.getPluginDef(realPluginId);
		if (pluginDef == null) {
			StringBuilder b = new StringBuilder();
			b.append(
					"generatePluginRequest CustomMetricPluginParameter pluginDef not exist.")
					.append(" pluginDefId=").append(realPluginId);
			b.append(" parameterPluginId=").append(parameter.getPluginId());
			b.append(" metricId=").append(parameter.getCustomMetricId());
			b.append(" instanceId=").append(parameter.getInstanceId());
			String msg = b.toString();
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST,
					msg);
		}
		MetricCollect collect = new MetricCollect();
		collect.setPlugin(pluginDef);

		PluginRequest request = generatePluginRequest(
				parameter.getInstanceId(), parameter.getCustomMetricId(),
				batch, discoverysDataMap, pluginInitDataMap, true, collect);
		request.setPluginId(parameter.getPluginId());
		request.setBatch(batch);
		request.setCustomMetric(true);

		if (parameter.getDataProcessClass() != null) {
			PluginProcessParameter[] pluginProcessParameters = new PluginProcessParameter[1];
			pluginProcessParameters[0] = new PluginProcessParameter();
			pluginProcessParameters[0]
					.setProcessorClass(getProcessorClass(parameter
							.getDataProcessClass()));
			request.setPluginProcessParameters(pluginProcessParameters);
		}

		PluginConvertParameter convertParameter = new PluginConvertParameter();
		convertParameter
				.setConverterClass(getConverterClass(CapacityConst.CLASS_DEFAULTCONVERTER));
		convertParameter.setParameter(new ProcessParameter());
		request.setPluginConvertParameter(convertParameter);

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pluginParameters = parameter.getParameters();
		if (pluginParameters != null && pluginParameters.length > 0) {
			Parameter[] parameters = new Parameter[pluginParameters.length];
			for (int i = 0; i < pluginParameters.length; i++) {
				parameters[i] = pluginParameters[i];
				if (logger.isDebugEnabled()) {
					StringBuilder b = new StringBuilder(
							"generatePluginRequest executorParameter");
					b.append(" key=").append(parameters[i].getKey());
					b.append(" value=").append(parameters[i].getValue());
					b.append(" type=").append(parameters[i].getType());
					logger.debug(b.toString());
				}
			}
			executorParameter.setParameters(parameters);
		}
		request.setPluginExecutorParameter(executorParameter);
		return request;
	}

	private PluginRequest bindPluginRequest(
			Map<String, String> discoveryParameterMap, String resourceId,
			long instanceId, String metricId, String discoverWay,
			Map<Long, Map<String, PluginInitParameter>> pluginInitDataMap,
			boolean customMetric, MetricCollect collect)
			throws MetricExecutorException {
		PluginRequest request = new PluginRequest();
		request.setMetricId(metricId);
		request.setResourceInstId(instanceId);
		request.setResourceId(resourceId);

		if (customMetric == false) {
			ResourceMetricDef metricDef = capacityService.getResourceMetricDef(
					resourceId, metricId);
			if (metricDef == null) {
				StringBuilder b = new StringBuilder();
				b.append("ResourceMetricDef is null(resourceId=")
						.append(resourceId).append(" metricId=")
						.append(metricId);
				throw new MetricExecutorException(
						ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_METRIC_NOT_EXIST,
						b.toString());
			}
			String sysObjectId = null;
			if (collect == null) {
				// 从资源实例的模型属性里边拿值
				if (instanceId >= 0) {
					ResourceInstance instance = null;
					ModuleProp prop = null;
					try {
						instance = resourceInstanceService
								.getResourceInstance(instanceId);
						if (instance != null) {
							if (instance.getParentInstance() != null) {
								prop = modulePropService
										.getPropByInstanceAndKey(
												instance.getParentInstance()
														.getId(),
												MetricDiscoveryParameter.SYSTEM_OID_NAME);
							} else {
								prop = modulePropService
										.getPropByInstanceAndKey(
												instanceId,
												MetricDiscoveryParameter.SYSTEM_OID_NAME);
							}
						}
					} catch (InstancelibException e) {
						if (logger.isErrorEnabled()) {
							logger.error("bindPluginRequest", e);
						}
					}
					if (prop != null) {
						String[] propValues = prop.getValues();
						if (propValues != null && propValues.length > 0) {
							sysObjectId = propValues[0];
						}
					}
				} else {
					sysObjectId = discoveryParameterMap
							.get(MetricDiscoveryParameter.SYSTEM_OID_NAME);
				}
				collect = metricDef.getMetricPluginByType(discoverWay,
						sysObjectId);
			}
			if (collect == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("MetricCollect is null.");
				builder.append("metricId=").append(metricId);
				builder.append(" resourceId=").append(resourceId);
				builder.append(" discoveryWay=").append(discoverWay);
				builder.append(" systemOid=").append(
						discoveryParameterMap
								.get(MetricDiscoveryParameter.SYSTEM_OID_NAME));
				throw new MetricExecutorException(
						ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_COLLECTOR_NOT_EXIST,
						builder.toString());
			}
			request.setPluginId(collect.getPlugin().getId());

			PluginResultMetaInfo pluginResultMetaInfo = collect
					.getPluginResultMetaInfo();
			if (pluginResultMetaInfo != null) {
				ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
				String[] columnNames = pluginResultMetaInfo.getColumns();
				if (columnNames != null && columnNames.length > 0) {
					for (int i = 0; i < columnNames.length; i++) {
						metaInfo.addColumnName(columnNames[i]);
					}
				}
				request.setResultSetMetaInfo(metaInfo);
			}
		}
		request.setPluginId(collect.getPlugin().getId());
		if (instanceId > 0 && pluginInitDataMap != null
				&& customMetric == false) {
			Long key = instanceId;
			if (pluginInitDataMap.containsKey(key)) {
				Map<String, PluginInitParameter> pluginMap = pluginInitDataMap
						.get(key);
				if (pluginMap.containsKey(request.getPluginId())) {
					request.setPluginInitParameter(pluginMap.get(request
							.getPluginId()));
				} else {
					bindPluginInitParameter(request, discoveryParameterMap,
							collect);
					pluginMap.put(request.getPluginId(),
							request.getPluginInitParameter());
				}
			} else {
				Map<String, PluginInitParameter> pluginMap = new HashMap<>();
				bindPluginInitParameter(request, discoveryParameterMap, collect);
				pluginMap.put(request.getPluginId(),
						request.getPluginInitParameter());
				pluginInitDataMap.put(key, pluginMap);
			}
		} else {
			bindPluginInitParameter(request, discoveryParameterMap, collect);
		}
		try {
			bindPluginExecutorParameter(request, instanceId,
					discoveryParameterMap, collect);
			bindPluginHandlersParameter(request, instanceId,
					discoveryParameterMap, collect);
			bindPluginConvertParameter(request, instanceId,
					discoveryParameterMap, collect);
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
			b.append(" resourceId=").append(resourceId).append(" instanceId=")
					.append(instanceId).append(" discoveryParameterMap=")
					.append(discoveryParameterMap);
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST,
					b.toString(), e);
		}
		request.setPluginSessionKey(buildPluginSessionKey(collect,
				discoveryParameterMap));
		request.setRequestId(idGenerator.generate());
		return request;
	}

	public PluginRequest[] generateSystemOidRequest(
			MetricDiscoveryParameter bindParameter)
			throws MetricExecutorException {
		ResourceDef resourceDef = null;
		if (bindParameter.getResourceId() != null) {
			resourceDef = capacityService.getResourceDefById(bindParameter
					.getResourceId());
			if (resourceDef == null) {
				StringBuilder b = new StringBuilder();
				b.append(
						"bindPluginRequest ResourceDef not exist in the collector.resourceId=")
						.append(bindParameter.getResourceId())
						.append(" discoveryParameterMap=")
						.append(bindParameter.getDiscoveryInfo());
				String msg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(msg);
				}
				throw new MetricExecutorException(
						ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST,
						msg);
			}

			CategoryDef categoryDef = resourceDef.getCategory();
			while (categoryDef != null) {
				/**
				 * 如果是网络设备，则构建获取systemoid的request
				 */
				if (categoryDef.getId().equals(CapacityConst.NETWORK_DEVICE)) {
					return innerGenerateSystemOidRequest(bindParameter);
				}
			}
			return null;
		} else {
			return innerGenerateSystemOidRequest(bindParameter);
		}
	}

	private PluginRequest[] innerGenerateSystemOidRequest(
			MetricDiscoveryParameter bindParameter)
			throws MetricExecutorException {
		PluginDef snmpPluginDef = capacityService.getPluginDef(PLUGIN_SNMP_ID);
		if (snmpPluginDef == null) {
			StringBuilder b = new StringBuilder();
			b.append("bindPluginRequest snmpPluginDef not exist.")
					.append(" discoveryParameterMap=")
					.append(bindParameter.getDiscoveryInfo());
			String msg = b.toString();
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST,
					msg);
		}
		MetricCollect collect = new MetricCollect();
		collect.setPlugin(snmpPluginDef);

		PluginRequest[] requests = new PluginRequest[2];
		PluginRequest request = new PluginRequest();
		bindPluginInitParameter(request, bindParameter.getDiscoveryInfo(),
				collect);
		request.setBatch(batchNumberGenerator
				.generateBatch(PluginRequestEnum.discovery));
		request.setPluginId(PLUGIN_SNMP_ID);
		request.setCollectTime(new Date());
		request.setPluginRequestType(PluginRequestEnum.discovery);
		request.setPluginSessionKey(buildPluginSessionKey(collect,
				bindParameter.getDiscoveryInfo()));
		request.setMetricId(MetricDiscoveryParameter.SYSTEM_OID_NAME);
		request.setRequestId(idGenerator.generate());
		PluginConvertParameter convertParameter = new PluginConvertParameter();
		convertParameter
				.setConverterClass(getConverterClass(CapacityConst.CLASS_DEFAULTCONVERTER));
		convertParameter.setParameter(new ProcessParameter());
		request.setPluginConvertParameter(convertParameter);

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		Parameter[] parameters = new Parameter[2];
		ParameterValue value = new ParameterValue();
		value.setKey("method");
		value.setValue("get");
		parameters[0] = value;
		value = new ParameterValue();
		value.setKey("");
		value.setValue("1.3.6.1.2.1.1.2.0");
		parameters[1] = value;
		executorParameter.setParameters(parameters);
		request.setPluginExecutorParameter(executorParameter);
		requests[0] = request;

		request = new PluginRequest();
		request.setPluginSessionKey(requests[0].getPluginSessionKey());
		request.setPluginRequestType(PluginRequestEnum.discovery_end);
		requests[1] = request;
		return requests;
	}
}
