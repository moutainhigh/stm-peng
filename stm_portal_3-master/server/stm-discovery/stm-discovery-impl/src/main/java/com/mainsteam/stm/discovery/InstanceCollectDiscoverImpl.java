/**
 * 
 */
package com.mainsteam.stm.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.PluginParameter;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.MetricExecutor;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.mainsteam.stm.util.ResourceOrMetricConst;

/**
 * @author ziw
 * 
 */
public class InstanceCollectDiscoverImpl implements InstanceCollectDiscover {

	private static final Log logger = LogFactory
			.getLog(InstanceCollectDiscoverImpl.class);

	private CapacityService capacityService;

	private MetricExecutor executor;

	private ResourceInstanceGenerator instanceGenerator;

	public void setExecutor(MetricExecutor executor) {
		this.executor = executor;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setInstanceGenerator(ResourceInstanceGenerator instanceGenerator) {
		this.instanceGenerator = instanceGenerator;
	}

	/**
	 * 
	 */
	public InstanceCollectDiscoverImpl() {
	}

	public void start() {

	}

	private ResourceDef collectAndMakeSureResourceId(
			DiscoveryParameter discoveryParameter)
			throws InstanceDiscoveryException {
		ResourceDef resourceDef = null;
		String systemOid = null;
		if (discoveryParameter.isAnonymousNetworkDevice()) {
			if (!discoveryParameter.getDiscoveryInfos().containsKey(
					MetricDiscoveryParameter.SYSTEM_OID_NAME)) {
				MetricDiscoveryParameter p = new MetricDiscoveryParameter();
				p.setDiscoveryInfo(discoveryParameter.getDiscoveryInfos());
				try {
					systemOid = executor.getNetworkSystemOid(p);
				} catch (MetricExecutorException e) {
					StringBuilder b = new StringBuilder(
							"caplib discovery  systemOid is empty.discoveryParameter=");
					b.append(discoveryParameter.getDiscoveryInfos());
					String msg = b.toString();
					if (logger.isErrorEnabled()) {
						logger.error(msg, e);
					}
					throw new InstanceDiscoveryException(e);
				} catch (Exception e) {
					StringBuilder b = new StringBuilder(
							"caplib  systemOid is empty.discoveryParameter=");
					b.append(discoveryParameter.getDiscoveryInfos());
					String msg = b.toString();
					if (logger.isErrorEnabled()) {
						logger.error(msg, e);
					}
					throw new InstanceDiscoveryException(
							ServerErrorCodeConstant.ERROR_DISCOVERY_EMPTY_SYSTEMOID,
							msg, e);
				}
				discoveryParameter.getDiscoveryInfos().put(
						MetricDiscoveryParameter.SYSTEM_OID_NAME, systemOid);
			} else {
				systemOid = discoveryParameter.getDiscoveryInfos().get(
						MetricDiscoveryParameter.SYSTEM_OID_NAME);
			}
			if (StringUtils.isEmpty(systemOid)) {
				StringBuilder b = new StringBuilder(
						"AnonymousNetworkDevice discovery  systemOid is empty.discoveryParameter=");
				b.append(discoveryParameter.getDiscoveryInfos());
				String msg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(msg);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_EMPTY_SYSTEMOID,
						msg);
			}
			String resourceDefId = capacityService.getResourceId(systemOid);
			if (StringUtils.isEmpty(resourceDefId)) {
				StringBuilder b = new StringBuilder(
						"discovery resourceId is not found for systemOid=");
				b.append(systemOid);
				String msg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(msg);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_NO_RESOURCEID_BY_SYSTEMOID,
						msg);
			}
			discoveryParameter.setResourceId(resourceDefId);
		}
		resourceDef = capacityService.getResourceDefById(discoveryParameter
				.getResourceId());
		return resourceDef;
	}

	private CategoryDef collectNetworkSystemoid(
			DiscoveryParameter discoveryParameter, ResourceDef resourceDef) {
		String systemOid = null;
		/**
		 * 判断是否是网络设备
		 */
		CategoryDef categoryDef = resourceDef.getCategory();
		while (categoryDef != null) {
			if (categoryDef.getId().equals(CapacityConst.NETWORK_DEVICE)) {
				MetricDiscoveryParameter p = new MetricDiscoveryParameter();
				p.setDiscoveryInfo(discoveryParameter.getDiscoveryInfos());
				try {
					systemOid = executor.getNetworkSystemOid(p);
				} catch (MetricExecutorException e) {
					/**
					 * 针对网络设备模型，已经找到了模型id，则不强制要求必须取到systemOid
					 */
					StringBuilder b = new StringBuilder(
							"discovery systemOid is empty.discoveryParameter=");
					b.append(discoveryParameter.getDiscoveryInfos());
					String msg = b.toString();
					if (logger.isWarnEnabled()) {
						logger.warn(msg, e);
					}
				}
				discoveryParameter.getDiscoveryInfos().put(
						MetricDiscoveryParameter.SYSTEM_OID_NAME, systemOid);
				break;
			}
			if (categoryDef == categoryDef.getParentCategory()) {
				break;
			}
			categoryDef = categoryDef.getParentCategory();
		}
		return categoryDef;
	}

	private void loggerDiscoverPrepareInfo(
			DiscoveryParameter discoveryParameter, String systemOid,
			ResourceDef resourceDef) {
		if (logger.isInfoEnabled()) {
			/**
			 * 屏蔽密码日志输出
			 */
			Map<String, Boolean> passwordParameters = new HashMap<>();
			ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
			for (ResourceMetricDef rmd : metricDefs) {
				MetricCollect collect = rmd.getMetricPluginByType(
						discoveryParameter.getDiscoveryWay(), systemOid);
				if (collect != null) {
					PluginDef p = collect.getPlugin();
					if (p != null) {
						PluginInitParameter[] parameterDefs = collect
								.getPlugin().getPluginInitParameters();
						if (parameterDefs != null) {
							for (PluginInitParameter parameterDef : parameterDefs) {
								if (parameterDef.isPassword()) {
									passwordParameters.put(
											parameterDef.getId(), Boolean.TRUE);
								}
							}
						}
					} else {
						StringBuilder b = new StringBuilder();
						b.append(" resourceId=").append(resourceDef.getId());
						b.append(" metricId=").append(rmd.getId());
						b.append(" discoveryWay=").append(
								discoveryParameter.getDiscoveryWay());
						logger.error(b.toString());
					}
				}
			}

			StringBuilder b = new StringBuilder();
			b.append("discovery start resourceId=");
			b.append(discoveryParameter.getResourceId());
			b.append(" way={");
			b.append(discoveryParameter.getDiscoveryWay());
			b.append(" map={");
			Map<String, String> mapParameters = discoveryParameter
					.getDiscoveryInfos();
			if (mapParameters != null) {
				for (Iterator<Entry<String, String>> iterator = mapParameters
						.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, String> en = iterator.next();
					String pk = en.getKey();
					b.append(' ').append(pk).append('=');
					//2018-1-26 modify by Xiao Ruqiang 
					//放开密码打印输出，但是打印的是加密后的密码。
//					if (passwordParameters.containsKey(pk)) {
//						b.append(en.getValue());
//						continue;
//					}
					b.append(en.getValue());
				}
			}
			b.append('}');
			logger.info(b.toString());
		}
	}

	private Map<String, ResourcePropertyDef> loadResourcePropertyDefMap(
			ResourceDef resourceDef) {
		ResourcePropertyDef[] props = resourceDef.getPropertyDefs();
		Map<String, ResourcePropertyDef> pdefMap = new HashMap<>(props.length);
		for (ResourcePropertyDef resourcePropertyDef : props) {
			pdefMap.put(resourcePropertyDef.getId(), resourcePropertyDef);
		}
		return pdefMap;
	}

	private void fillPropertiesAndMetricsForDiscovery(
			List<ResourcePropertyDef> toFilledAllIdPropertyDefs,
			List<ResourceMetricDef> toFilledAllIdPropetiesMetrics,
			Map<String, List<ResourcePropertyDef>> resourceIdPropetiesMap,
			List<ResourcePropertyDef> toFilledAllOtherPropertyDefs,
			List<ResourceMetricDef> toFilledAllOtherPropertiesMetrics,
			Map<String, List<ResourcePropertyDef>> resourceOtherPropertiesMap,
			ResourceDef resourceDef, String discoveryWay, String systemOid,
			CategoryDef categoryDef) throws InstanceDiscoveryException {

		fillPropertiesAndMetricsForDiscovery0(toFilledAllIdPropertyDefs,
				toFilledAllIdPropetiesMetrics, resourceIdPropetiesMap,
				toFilledAllOtherPropertyDefs,
				toFilledAllOtherPropertiesMetrics, resourceOtherPropertiesMap,
				resourceDef, discoveryWay, systemOid, categoryDef);
		ResourceDef[] children = resourceDef.getChildResourceDefs();
		if (children != null) {
			for (ResourceDef child : children) {
				fillPropertiesAndMetricsForDiscovery0(
						toFilledAllIdPropertyDefs,
						toFilledAllIdPropetiesMetrics, resourceIdPropetiesMap,
						toFilledAllOtherPropertyDefs,
						toFilledAllOtherPropertiesMetrics,
						resourceOtherPropertiesMap, child, discoveryWay,
						systemOid, categoryDef);
			}
		}
		Map<String, String> mapper = new HashMap<>(
				toFilledAllIdPropetiesMetrics.size()
						+ toFilledAllOtherPropertiesMetrics.size());
		for (int i = 0; i < toFilledAllIdPropetiesMetrics.size(); i++) {
			if (mapper
					.containsKey(toFilledAllIdPropetiesMetrics.get(i).getId())) {
				toFilledAllIdPropetiesMetrics.remove(i);
				i--;
			} else {
				mapper.put(toFilledAllIdPropetiesMetrics.get(i).getId(), null);
			}
		}
		for (int i = 0; i < toFilledAllOtherPropertiesMetrics.size(); i++) {
			if (mapper.containsKey(toFilledAllOtherPropertiesMetrics.get(i)
					.getId())) {
				toFilledAllOtherPropertiesMetrics.remove(i);
				i--;
			} else {
				mapper.put(toFilledAllOtherPropertiesMetrics.get(i).getId(),
						null);
			}
		}
		mapper.clear();
		mapper = null;
	}

	private void fillPropertiesAndMetricsForDiscovery0(
			List<ResourcePropertyDef> toFilledAllIdPropertyDefs,
			List<ResourceMetricDef> toFilledAllIdPropetiesMetrics,
			Map<String, List<ResourcePropertyDef>> resourceIdPropetiesMap,
			List<ResourcePropertyDef> toFilledAllOtherPropertyDefs,
			List<ResourceMetricDef> toFilledAllOtherPropertiesMetrics,
			Map<String, List<ResourcePropertyDef>> resourceOtherPropertiesMap,
			ResourceDef resourceDef, String discoveryWay, String systemOid,
			CategoryDef categoryDef) throws InstanceDiscoveryException {
		// malachi 实体类转map
		Map<String, ResourcePropertyDef> pdefMap = loadResourcePropertyDefMap(resourceDef);
		Map<String, ResourcePropertyDef> propertyFindedMap = new HashMap<>();

		List<ResourcePropertyDef> toFilledIdPropertyDefs = new ArrayList<>();
		// malachi 拿到发现资源实例依赖的指标
		fillInstIdPropertiesAndMetrics(toFilledIdPropertyDefs, resourceDef,
				pdefMap, propertyFindedMap);

		List<ResourcePropertyDef> toFilledOtherPropertyDefs = new ArrayList<>();
		// malachi
		fillMetricsDependPropertiesAndMetrics(toFilledOtherPropertyDefs,
				resourceDef, propertyFindedMap, discoveryWay, systemOid,
				categoryDef);

		convertPropertiesToMetricsAndFill(toFilledIdPropertyDefs,
				toFilledAllIdPropetiesMetrics);

		convertPropertiesToMetricsAndFill(toFilledOtherPropertyDefs,
				toFilledAllOtherPropertiesMetrics);

		resourceIdPropetiesMap.put(resourceDef.getId(), toFilledIdPropertyDefs);
		resourceOtherPropertiesMap.put(resourceDef.getId(),
				toFilledOtherPropertyDefs);
	}

	private void convertPropertiesToMetricsAndFill(
			List<ResourcePropertyDef> toConvertProperties,
			List<ResourceMetricDef> toFillMetricDefs) {
		for (ResourcePropertyDef prop : toConvertProperties) {
			ResourceMetricDef m = prop.getResourceMetric();
			if (m == null) {
				StringBuilder b = new StringBuilder();
				b.append(
						"convertPropertiesToMetricsAndFill property's metric is null.propName=")
						.append(prop.getName());
				b.append(" propId=").append(prop.getId());
				if (logger.isWarnEnabled()) {
					logger.warn(b.toString());
				}
				continue;
			}
			toFillMetricDefs.add(m);
		}
	}

	private void fillInstIdPropertiesAndMetrics(
			List<ResourcePropertyDef> toFilledAllIdPropertyDefs,
			ResourceDef resourceDef, Map<String, ResourcePropertyDef> pdefMap,
			Map<String, ResourcePropertyDef> propertyFindedMap)
			throws InstanceDiscoveryException {
		/**
		 * 拿到发现资源实例依赖的指标
		 */
		String[] instIdPropIds = resourceDef.getInstantiationDef()
				.getInstanceId();

		if (instIdPropIds == null || instIdPropIds.length <= 0) {
			throw new InstanceDiscoveryException(
					ServerErrorCodeConstant.ERROR_DISCOVERY_ISNTANCE_ID_NOT_EXIST,
					"getInstantiationDef.instanceid is empty.resourceId="
							+ resourceDef.getId());
		}
		for (String instIdPropId : instIdPropIds) {
			ResourcePropertyDef def = pdefMap.get(instIdPropId);
			if (def == null) {
				StringBuilder b = new StringBuilder(100);
				b.append("getInstantiationDef.instanceid.ResourcePropertyDef not exist.PropId=");
				b.append(instIdPropId);
				if (logger.isWarnEnabled()) {
					logger.warn(b);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_ID_PROPERTY_NOT_EXIST,
						b.toString());
			}
			if (propertyFindedMap.containsKey(def.getId())) {
				continue;
			}
			propertyFindedMap.put(def.getId(), def);
			if (def.getResourceMetric() == null) {
				StringBuilder b = new StringBuilder();
				b.append(
						"ResourcePropertyDef's getResourceMetric() is null,id=")
						.append(def.getId());
				b.append(" name=").append(def.getName());
				if (logger.isWarnEnabled()) {
					logger.warn(b.toString());
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_ID_PROPERTY_METRIC_NOT_EXIST,
						"getInstantiationDef.instanceid.ResourcePropertyDef’s ResourceMetric not exist.resourcePropertyDefId="
								+ def.getId());
			} else {
				toFilledAllIdPropertyDefs.add(def);
			}
		}
	}

	private void fillMetricsDependPropertiesAndMetrics(
			List<ResourcePropertyDef> toFilledAllOtherPropertyDefs,
			ResourceDef resourceDef,
			Map<String, ResourcePropertyDef> propertyFindedMap,
			String discoveryWay, String systemOid, CategoryDef categoryDef)
			throws InstanceDiscoveryException {
		List<ResourcePropertyDef> findedPropsList = toFilledAllOtherPropertyDefs;
		// malachi 获取属性集合
		ResourcePropertyDef[] props = resourceDef.getPropertyDefs();
		Map<String, ResourcePropertyDef> pdefMap = new HashMap<>(props.length);
		for (ResourcePropertyDef resourcePropertyDef : props) {
			pdefMap.put(resourcePropertyDef.getId(), resourcePropertyDef);
			/**
			 * 特殊处理，针对网络接口的属性ifType，在发现时进行取值
			 * 
			 * modify by ziw at 2019/6/30
			 */
			if ("ifType".equalsIgnoreCase(resourcePropertyDef.getId())) {
				findedPropsList.add(resourcePropertyDef);
				propertyFindedMap.put(resourcePropertyDef.getId(),
						resourcePropertyDef);
			}
		}
		String instNamePropId = resourceDef.getInstantiationDef()
				.getInstanceName();
		ResourcePropertyDef def = pdefMap.get(instNamePropId);
		if (def == null) {
			StringBuilder b = new StringBuilder();
			b.append(
					"getMetricsDependByInst instNamePropDef not exist.resourceId=")
					.append(resourceDef.getId());
			b.append(" instNamePropId=").append(instNamePropId);
			if (logger.isWarnEnabled()) {
				logger.warn(b.toString());
			}
		} else {
			if (!propertyFindedMap.containsKey(def.getId())) {
				propertyFindedMap.put(def.getId(), def);
				findedPropsList.add(def);
			}
		}
		if (resourceDef.getParentResourceDef() == null
				|| resourceDef.getParentResourceDef().getId()
						.equals(resourceDef.getId())) {
			/**
			 * 主资源，拿取所有的属性。
			 */
			for (ResourcePropertyDef resourcePropertyDef : props) {
				if (!propertyFindedMap.containsKey(resourcePropertyDef.getId())) {
					propertyFindedMap.put(resourcePropertyDef.getId(),
							resourcePropertyDef);
					findedPropsList.add(resourcePropertyDef);
				}
			}
		} else {
			/**
			 * 子资源，拿到指标取值需要的输入参数所依赖的属性对应的指标
			 */
			addMetricsDependByPropertyForParameter(resourceDef, discoveryWay,
					findedPropsList, pdefMap, propertyFindedMap, systemOid);
			/**
			 * 获取网络设备接口状态指标，wmi主机服务指标
			 */
			addInterfaceOrServiceProperty(resourceDef, categoryDef,
					findedPropsList, pdefMap);
		}
		/**
		 * 清空属性临时map
		 */
		pdefMap.clear();
		pdefMap = null;
		propertyFindedMap.clear();
		propertyFindedMap = null;
	}

	private List<DiscoveryMetricData> collectMetricDatasForDiscovery(
			List<ResourceMetricDef> toFilledAllIdPropetiesMetrics,
			List<ResourceMetricDef> toFilledAllOtherPropertiesMetrics,
			DiscoveryParameter discoveryParameter)
			throws InstanceDiscoveryException {
		List<MetricDiscoveryParameter> bindParameters = bindParameters(
				toFilledAllIdPropetiesMetrics,
				toFilledAllOtherPropertiesMetrics, discoveryParameter);
		List<DiscoveryMetricData> metricDatas = null;
		try {
			// malachi in discovery 执行
			metricDatas = executor.catchDiscoveryMetricDatas(bindParameters,
					discoveryParameter.getDiscoveryWay());
		} catch (MetricExecutorException e) {
			if (logger.isErrorEnabled()) {
				logger.error("discovery", e);
			}
			throw new InstanceDiscoveryException(e);
		}
		return metricDatas;
	}

	@Override
	public ModelResourceInstance discovery(DiscoveryParameter discoveryParameter)
			throws InstanceDiscoveryException {
		ResourceDef resourceDef = collectAndMakeSureResourceId(discoveryParameter);
		String systemOid = discoveryParameter.getDiscoveryInfos().get(
				MetricDiscoveryParameter.SYSTEM_OID_NAME);
		CategoryDef categoryDef = null;
		if (resourceDef == null) {
			throw new InstanceDiscoveryException(
					ServerErrorCodeConstant.ERROR_DISCOVERY_RESOURCE_NOT_EXIST,
					"resourceId=" + discoveryParameter.getResourceId());
		} else if (systemOid == null) {
			categoryDef = collectNetworkSystemoid(discoveryParameter,
					resourceDef);
		}
		if (categoryDef == null && resourceDef.getCategory() != null) {
			categoryDef = resourceDef.getCategory().getParentCategory();
		}
		loggerDiscoverPrepareInfo(discoveryParameter, systemOid, resourceDef);

		List<ResourcePropertyDef> toFilledAllIdPropertyDefs = new ArrayList<>();
		List<ResourceMetricDef> toFilledAllIdPropetiesMetrics = new ArrayList<>();
		List<ResourcePropertyDef> toFilledAllOtherPropertyDefs = new ArrayList<>();
		List<ResourceMetricDef> toFilledAllOtherPropertiesMetrics = new ArrayList<>();

		Map<String, List<ResourcePropertyDef>> resourceIdPropetiesMap = new HashMap<>();
		Map<String, List<ResourcePropertyDef>> resourceOtherPropertiesMap = new HashMap<>();
		//malachi in discovery3 装载 指标属性等
		fillPropertiesAndMetricsForDiscovery(toFilledAllIdPropertyDefs,
				toFilledAllIdPropetiesMetrics, resourceIdPropetiesMap,
				toFilledAllOtherPropertyDefs,
				toFilledAllOtherPropertiesMetrics, resourceOtherPropertiesMap,
				resourceDef, discoveryParameter.getDiscoveryWay(), systemOid,
				categoryDef);
		// malachi 开始采集
		List<DiscoveryMetricData> metricDatas = collectMetricDatasForDiscovery(
				toFilledAllIdPropetiesMetrics,
				toFilledAllOtherPropertiesMetrics, discoveryParameter);
		ModelResourceInstance instance = instanceGenerator
				.generateResourceInstance(metricDatas, resourceDef,
						resourceIdPropetiesMap, resourceOtherPropertiesMap);
		if (logger.isInfoEnabled()) {
			logger.info("discovery end");
		}
		return instance;
	}

	private List<MetricDiscoveryParameter> bindParameters(
			List<ResourceMetricDef> resourceIdDefs,
			List<ResourceMetricDef> initMetrics,
			DiscoveryParameter discoveryParameter) {
		List<MetricDiscoveryParameter> bindParameters = new ArrayList<>(
				resourceIdDefs.size() + initMetrics.size());
		for (ResourceMetricDef resourceMetricDef : resourceIdDefs) {
			MetricDiscoveryParameter p = new MetricDiscoveryParameter();
			p.setDiscoveryInfo(discoveryParameter.getDiscoveryInfos());
			p.setMetricId(resourceMetricDef.getId());
			p.setResourceId(resourceMetricDef.getResourceDef().getId());
			bindParameters.add(p);
		}

		for (ResourceMetricDef resourceMetricDef : initMetrics) {
			MetricDiscoveryParameter p = new MetricDiscoveryParameter();
			p.setDiscoveryInfo(discoveryParameter.getDiscoveryInfos());
			p.setMetricId(resourceMetricDef.getId());
			p.setResourceId(resourceMetricDef.getResourceDef().getId());
			bindParameters.add(p);
		}
		return bindParameters;
	}

	private void addInterfaceOrServiceProperty(ResourceDef resourceDef,
			CategoryDef categoryDef, List<ResourcePropertyDef> findedPropsList,
			Map<String, ResourcePropertyDef> pdefMap) {
		/**
		 * 拿到网络设备接口、wmi方式发现的主机服务
		 */
		String metricId = null;
		if (ResourceTypeConsts.TYPE_NETINTERFACE.equals(resourceDef.getType())) {
			if (CapacityConst.NETWORK_DEVICE.equals(categoryDef.getId())
					|| CapacityConst.SNMPOTHERS.equals(categoryDef.getId())) {
				metricId = ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID;
			} else if (CapacityConst.HOST.equals(categoryDef.getId())) {
				metricId = ResourceOrMetricConst.HOST_INTERFACE_AVAILABILITY_METRICID;
			} else if(CapacityConst.STORAGE.equals(categoryDef.getId())){
				metricId = "ifStatus";
			}
		} else if (ResourceTypeConsts.TYPE_SERVICE
				.equals(resourceDef.getType())
				&& ResourceOrMetricConst.WMI_HOST_SERVICE_RESOURCEID
						.equals(resourceDef.getId())) {
			metricId = ResourceOrMetricConst.WMI_HOST_SERVICE_AVAILABILITY_METRICID;
		}
		if (metricId != null) {
			// 通过metricId查找ResourcePropertyDef对象
			for (ResourcePropertyDef item : pdefMap.values()) {
				if (metricId.equals(item.getResourceMetric().getId())) {
					findedPropsList.add(item);
				}
			}
		}
	}

	private void addMetricsDependByPropertyForParameter(
			ResourceDef resourceDef, String discoveryWay,
			List<ResourcePropertyDef> findedPropsList,
			Map<String, ResourcePropertyDef> pdefMap,
			Map<String, ResourcePropertyDef> propertyFindedMap, String systemOid)
			throws InstanceDiscoveryException {
		ResourceMetricDef[] resourceMetricDefs = resourceDef.getMetricDefs();

		for (ResourceMetricDef resourceMetricDef : resourceMetricDefs) {
			MetricCollect collect = resourceMetricDef.getMetricPluginByType(
					discoveryWay, systemOid);
			if (null == collect) {
				StringBuilder b = new StringBuilder();
				b.append("addMetricsDependByPropertyForParameter MetricCollect is null");
				b.append(" resourceId=").append(resourceDef.getId());
				b.append(" metricId=").append(resourceMetricDef.getId());
				String errorMsg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(errorMsg);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_COLLECTOR_NOT_EXIST,
						errorMsg);
			}
			PluginParameter executeParameter = collect.getPluginParameter();
			if (executeParameter != null) {
				ParameterDef[] parameterDefs = executeParameter.getParameters();
				findPropsForParameter(parameterDefs, resourceDef,
						resourceMetricDef, propertyFindedMap, pdefMap, collect,
						findedPropsList);
			}
			PluginDataHandler[] handlers = collect.getPluginDataHandlers();
			if (handlers != null && handlers.length > 0) {
				for (PluginDataHandler pluginDataHandler : handlers) {
					ParameterDef[] parameterDefs = pluginDataHandler
							.getParameterDefs();
					findPropsForParameter(parameterDefs, resourceDef,
							resourceMetricDef, propertyFindedMap, pdefMap,
							collect, findedPropsList);
				}
			}
			PluginDataConverter converter = collect.getPluginDataConverter();
			if (converter != null) {
				ParameterDef[] parameterDefs = converter.getParameterDefs();
				findPropsForParameter(parameterDefs, resourceDef,
						resourceMetricDef, propertyFindedMap, pdefMap, collect,
						findedPropsList);
			}
		}
//		propertyFindedMap.clear();
//		propertyFindedMap = null;
	}

	private void findPropsForParameter(ParameterDef[] parameterDefs,
			ResourceDef resourceDef, ResourceMetricDef resourceMetricDef,
			Map<String, ResourcePropertyDef> propertyFindedMap,
			Map<String, ResourcePropertyDef> pdefMap, MetricCollect collect,
			List<ResourcePropertyDef> findedPropsList)
			throws InstanceDiscoveryException {
		if (parameterDefs != null) {
			for (ParameterDef parameterDef : parameterDefs) {
				if (parameterDef.getType() == ValueTypeEnum.ResourceProperty) {
					ResourcePropertyDef def = pdefMap.get(parameterDef
							.getValue());
					if (def == null) {
						StringBuilder b = new StringBuilder();
						b.append("findPropsForParameter resourceId=").append(
								resourceDef.getId());
						b.append(" metricId=")
								.append(resourceMetricDef.getId());
						b.append(" MetricCollect=").append(
								collect.getPlugin().getId());
						b.append(" parameterDef.key").append(
								parameterDef.getKey());
						b.append(" parameterDef.value of ResourceProperty.Id=")
								.append(parameterDef.getValue());
						b.append(" the ResourceProperty is not exist in ResourceModel.");
						// throw new InstanceDiscoveryException(
						// ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_FOR_PARAMETER_NOT_EXIST,
						// b.toString());
						if (logger.isWarnEnabled()) {
							logger.warn(b);
						}
						continue;
					} else if (propertyFindedMap.containsKey(def.getId())) {// 如果已经加入了该属性，则不再处理
						continue;
					}
					propertyFindedMap.put(def.getId(), def);
					findedPropsList.add(def);
				}
			}
		}
	}
}
