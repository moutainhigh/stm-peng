/**
 * 
 */
package com.mainsteam.stm.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;

/**
 * @author ziw
 * 
 */
public class ResourceInstanceGenerator {

	private static final Log logger = LogFactory
			.getLog(ResourceInstanceGenerator.class);

	/**
	 * 将指标数据转换为key和value的模式。利用了，同一个模型下，所有的指标id都不能相同的约束。
	 * 
	 * @param metricDatas
	 * @return
	 */
	private Map<String, List<DiscoveryMetricData>> convertMetricDataToMap(
			List<DiscoveryMetricData> metricDatas) {
		Map<String, List<DiscoveryMetricData>> dataMap = new HashMap<>(
				metricDatas.size());
		for (DiscoveryMetricData metricData : metricDatas) {
			if (dataMap.containsKey(metricData.getMetricId())) {
				dataMap.get(metricData.getMetricId()).add(metricData);
			} else {
				List<DiscoveryMetricData> datas = new ArrayList<>();
				datas.add(metricData);
				dataMap.put(metricData.getMetricId(), datas);
			}
		}
		return dataMap;
	}

	public ModelResourceInstance generateResourceInstance(
			List<DiscoveryMetricData> metricDatas, ResourceDef resourceDef,
			Map<String, List<ResourcePropertyDef>> resourceIdDefsMap,
			Map<String, List<ResourcePropertyDef>> resourcePropDependDefsMap)
			throws InstanceDiscoveryException {
		if (logger.isInfoEnabled()) {
			logger.info("generateResourceInstance start");
		}
		Map<String, List<DiscoveryMetricData>> metricDataMap = convertMetricDataToMap(metricDatas);
		Map<String, ResourcePropertyDef> pdefMap = getInstProps(resourceDef);
		List<ModelResourceInstance> mainResourceInsts = getInstWithPropsValue(
				metricDataMap, resourceIdDefsMap.get(resourceDef.getId()),
				resourcePropDependDefsMap.get(resourceDef.getId()),
				resourceDef, pdefMap);
		ModelResourceInstance topInstance = mainResourceInsts != null
				&& mainResourceInsts.size() > 0 ? mainResourceInsts.get(0)
				: null;
		if (topInstance == null) {
			StringBuilder b = new StringBuilder(
					"generateResourceInstance not found top resourceinstance.resourceDefId=")
					.append(resourceDef.getId());
			String msg = b.toString();
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new InstanceDiscoveryException(
					ServerErrorCodeConstant.ERROR_DISCOVERY_EMPTY_METRIC_VALUE_FOR_INSTANCE,
					msg);
		}

		ResourceDef[] children = resourceDef.getChildResourceDefs();
		if (children != null && children.length > 0) {
			topInstance.setChildren(new ArrayList<ModelResourceInstance>(
					children.length));
			/**
			 * 开始构建子资源实例和父子关系
			 */
			for (int i = 0; i < children.length; i++) {
				// 特殊处理，针对进程资源不做资源发现(模型已经处理)
				if(!children[i].isAutodiscovery()){
					continue;
				} 
				pdefMap = getInstProps(children[i]);
				List<ModelResourceInstance> childInsts = getInstWithPropsValue(
						metricDataMap,
						resourceIdDefsMap.get(children[i].getId()),
						resourcePropDependDefsMap.get(children[i].getId()),
						children[i], pdefMap);
				if (childInsts == null || childInsts.size() <= 0) {
					if (logger.isInfoEnabled()) {
						logger.info("generateResourceInstance no child instances discovery of resourceId="
								+ children[i].getId());
					}
					continue;
				}
				topInstance.getChildren().addAll(childInsts);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("generateResourceInstance end");
		}
		return topInstance;
	}

	private Map<String, Map<String, ModelResourceInstance>> getIndexPropertyModelResourceInstanceMap(
			String resourceId,
			Map<String, Map<String, Map<String, ModelResourceInstance>>> resourceIndexPropertyModelResourceInstanceMap) {
		if (resourceIndexPropertyModelResourceInstanceMap
				.containsKey(resourceId)) {
			return resourceIndexPropertyModelResourceInstanceMap
					.get(resourceId);
		} else {
			Map<String, Map<String, ModelResourceInstance>> map = new HashMap<>();
			resourceIndexPropertyModelResourceInstanceMap.put(resourceId, map);
			return map;
		}
	}

	private List<ModelResourceInstance> getInstWithPropsValue(
			Map<String, List<DiscoveryMetricData>> metricDataMap,
			List<ResourcePropertyDef> idPropertyDefs,
			List<ResourcePropertyDef> dependResourcePropertyDefs,
			ResourceDef resourceDef, Map<String, ResourcePropertyDef> pdefMap)
			throws InstanceDiscoveryException {
		/**
		 * 首先，用来生成资源实例对象
		 */
		List<ModelResourceInstance> moduleInstances = null;
		Map<String, List<ModelResourceInstance>> moduleInstancesMap = new HashMap<>();
		Map<String, Map<String, Map<String, ModelResourceInstance>>> resourceIndexPropertyModelResourceInstanceMap = new HashMap<>();
		for (ResourcePropertyDef resourcePropertyDef : idPropertyDefs) {
			ResourceMetricDef metricDef = resourcePropertyDef.getResourceMetric();
			List<DiscoveryMetricData> metricDatas = metricDataMap.get(metricDef
					.getId());
			List<ModelResourceInstance> resourceInstances = moduleInstancesMap
					.get(metricDef.getResourceDef().getId());
			if (metricDatas == null || metricDatas.size() < 0) {
				StringBuilder b = new StringBuilder();
				b.append("getInstWithPropsValue resourceId=")
						.append(resourceDef.getId())
						.append(" propValues instIdPropId=")
						.append(resourcePropertyDef.getId())
						.append(" metricId=").append(metricDef.getId())
						.append(" metricData is null,please check!");
				String errorMsg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(errorMsg);
				}
				// throw new InstanceDiscoveryException(
				// ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY,
				// errorMsg);
				/**
				 * 子资源实例可以没有被发现。资源实例的索引属性必须有值，否则发现失败。
				 */
				continue;
			}

			if (resourceInstances == null || resourceInstances.size() <= 0) {
				resourceInstances = new ArrayList<>(metricDatas.size());
				for (int i = 0; i < metricDatas.size(); i++) {
					DiscoveryMetricData discoveryMetricData = metricDatas
							.get(i);
					String[] metricValues = discoveryMetricData.getData();
					if (metricValues == null || metricValues.length <= 0) {
						StringBuilder b = new StringBuilder();
						b.append("getInstWithPropsValue resourceId=")
								.append(resourceDef.getId())
								.append(" instanceIndex=").append(i)
								.append(" propValues instIdPropId=")
								.append(resourcePropertyDef.getId())
								.append(" metricId=").append(metricDef.getId())
								.append(" metricValues is null,please check!");
						String errorMsg = b.toString();
						if (logger.isErrorEnabled()) {
							logger.error(errorMsg);
						}
						throw new InstanceDiscoveryException(
								ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY,
								errorMsg);
					}
					String v = null;
					if (metricValues.length == 1) {
						v = metricValues[0];
					} else {
						v = Arrays.toString(metricValues);
					}
					StringBuilder b = new StringBuilder(
							"getInstWithPropsValue resourceId=");
					b.append(discoveryMetricData.getResourceId());
					b.append(" instanceIndex=").append(i);
					b.append(" propId=").append(resourcePropertyDef.getId());
					b.append(" metricId=").append(
							discoveryMetricData.getMetricId());
					b.append(" metricValue=").append(v);
					if (StringUtils.isEmpty(v)) {
						b.append(".indexProperty value should not be emtpy.");
						String msg = b.toString();
						if (logger.isErrorEnabled()) {
							logger.error(msg);
						}
						throw new InstanceDiscoveryException(
								ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY,
								msg);
					}
					if (logger.isInfoEnabled()) {
						logger.info(b.toString());
					}
					ModelResourceInstance inst = new ModelResourceInstance();
					inst.setPropValues(new HashMap<String, String[]>());
					inst.getPropValues().put(resourcePropertyDef.getId(),
							metricValues);
					Map<String, Map<String, ModelResourceInstance>> indexPropertyModelResourceInstanceMap = getIndexPropertyModelResourceInstanceMap(
							metricDef.getResourceDef().getId(),
							resourceIndexPropertyModelResourceInstanceMap);
					if (indexPropertyModelResourceInstanceMap
							.containsKey(resourcePropertyDef.getId())) {
						indexPropertyModelResourceInstanceMap.get(
								resourcePropertyDef.getId()).put(v, inst);
					} else {
						Map<String, ModelResourceInstance> indexMap = new HashMap<>();
						indexMap.put(v, inst);
						indexPropertyModelResourceInstanceMap.put(
								resourcePropertyDef.getId(), indexMap);
					}
					resourceInstances.add(inst);
				}
				moduleInstancesMap.put(metricDef.getResourceDef().getId(),
						resourceInstances);
			} else if (metricDatas.size() != resourceInstances.size()) {
				StringBuilder b = new StringBuilder();
				b.append("getInstWithPropsValue metricValues.length=").append(
						metricDatas.size());
				b.append(" moduleInstances.length=").append(
						resourceInstances.size());
				b.append(" is not equals.resourceId=").append(
						resourceDef.getId());
				b.append(" metricId=").append(metricDef.getId());
				String msg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(msg);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY,
						msg);
			} else {
				for (int i = 0; i < metricDatas.size(); i++) {
					DiscoveryMetricData discoveryMetricData = metricDatas
							.get(i);
					String[] metricValues = discoveryMetricData.getData();
					String v = null;
					if (metricValues.length == 1) {
						v = metricValues[0];
					} else {
						v = Arrays.toString(metricValues);
					}
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder(
								"getInstWithPropsValue resourceId=");
						b.append(discoveryMetricData.getResourceId());
						b.append(" instanceIndex=").append(i);
						b.append(" propId=")
								.append(resourcePropertyDef.getId());
						b.append(" metricId=").append(
								discoveryMetricData.getMetricId());
						b.append(" metricValue=").append(v);
						b.append(" indexPropertyName=").append(
								discoveryMetricData.getIndexPropertyName());
						b.append(" indexPropertyValue=").append(
								discoveryMetricData.getIndexPropertyValue());
						logger.info(b.toString());
					}
					ModelResourceInstance inst = null;
					/**
					 * 首先从索引表里匹配资源实例
					 */
					Map<String, Map<String, ModelResourceInstance>> indexPropertyModelResourceInstanceMap = getIndexPropertyModelResourceInstanceMap(
							metricDef.getResourceDef().getId(),
							resourceIndexPropertyModelResourceInstanceMap);
					if (discoveryMetricData.getIndexPropertyName() != null
							&& indexPropertyModelResourceInstanceMap
									.containsKey(discoveryMetricData
											.getIndexPropertyName())) {
						inst = indexPropertyModelResourceInstanceMap.get(
								discoveryMetricData.getIndexPropertyName())
								.get(discoveryMetricData
										.getIndexPropertyValue());
					} else {
						/**
						 * 没有索引属性，则判断其是否是父资源实例，如果是
						 */
						/**
						 * 匹配不到，则以下标为索引匹配。
						 */
						inst = resourceInstances.get(i);
					}
					if (inst == null) {
						if (logger.isWarnEnabled()) {
							logger.warn("getInstWithPropsValue no instance was found.");
						}
						continue;
					}
					inst.getPropValues().put(resourcePropertyDef.getId(),
							metricValues);
					if (indexPropertyModelResourceInstanceMap
							.containsKey(resourcePropertyDef.getId())) {
						indexPropertyModelResourceInstanceMap.get(
								resourcePropertyDef.getId()).put(v, inst);
					} else {
						Map<String, ModelResourceInstance> indexMap = new HashMap<>();
						indexMap.put(v, inst);
						indexPropertyModelResourceInstanceMap.put(
								resourcePropertyDef.getId(), indexMap);
					}
				}
			}
		}

		if (moduleInstancesMap.size() <= 0) {
			return moduleInstances;
		}

		/**
		 * 其次，填充资源实例的其它属性
		 */
		for (ResourcePropertyDef resourcePropertyDef : dependResourcePropertyDefs) {
			ResourceMetricDef metricDef = resourcePropertyDef.getResourceMetric();
			List<DiscoveryMetricData> metricDatas = metricDataMap.get(metricDef
					.getId());
			if (metricDatas == null) {
				StringBuilder b = new StringBuilder();
				b.append("getInstWithPropsValue resourceId=")
						.append(resourceDef.getId())
						.append(" propValues instIdPropId=")
						.append(resourcePropertyDef.getId())
						.append(" metricId=").append(metricDef.getId())
						.append(" metricData is null.");
				String errorMsg = b.toString();
				if (logger.isWarnEnabled()) {
					logger.warn(errorMsg);
				}
				continue;
			}
			List<ModelResourceInstance> resourceInstances = moduleInstancesMap
					.get(metricDef.getResourceDef().getId());
			if (resourceInstances == null || resourceInstances.size() <= 0) {
				StringBuilder b = new StringBuilder();
				b.append("getInstWithPropsValue resourceId=")
						.append(resourceDef.getId())
						.append(" propValues instIdPropId=")
						.append(resourcePropertyDef.getId())
						.append(" metricId=").append(metricDef.getId())
						.append(" on one instance not found.");
				String errorMsg = b.toString();
				if (logger.isWarnEnabled()) {
					logger.warn(errorMsg);
				}
				continue;
			}
			for (int i = 0; i < metricDatas.size(); i++) {
				DiscoveryMetricData discoveryMetricData = metricDatas.get(i);
				String[] metricValues = discoveryMetricData.getData();
				StringBuilder b = new StringBuilder(
						"getInstWithPropsValue resourceId=");
				b.append(discoveryMetricData.getResourceId());
				b.append(" instanceIndex=").append(i);
				b.append(" propId=").append(resourcePropertyDef.getId());
				b.append(" metricId=")
						.append(discoveryMetricData.getMetricId());
				b.append(" metricValue=").append(Arrays.toString(metricValues));
				b.append(" metricIndexPropertyName=").append(
						discoveryMetricData.getIndexPropertyName());
				b.append(" metricIndexPropertyValue=").append(
						discoveryMetricData.getIndexPropertyValue());
				if (logger.isInfoEnabled()) {
					logger.info(b.toString());
				}
				ModelResourceInstance inst = null;
				/**
				 * 首先从索引表里匹配资源实例
				 */
				Map<String, Map<String, ModelResourceInstance>> indexPropertyModelResourceInstanceMap = getIndexPropertyModelResourceInstanceMap(
						metricDef.getResourceDef().getId(),
						resourceIndexPropertyModelResourceInstanceMap);
				if (discoveryMetricData.getIndexPropertyName() != null
						&& indexPropertyModelResourceInstanceMap
								.containsKey(discoveryMetricData
										.getIndexPropertyName())) {
					inst = indexPropertyModelResourceInstanceMap.get(
							discoveryMetricData.getIndexPropertyName()).get(
							discoveryMetricData.getIndexPropertyValue());
				} else if (resourceDef.getParentResourceDef() == null
						&& i < resourceInstances.size()) {
					inst = resourceInstances.get(i);
				}
				/**
				 * 匹配不到，则以下标为索引匹配。
				 */
				if (inst == null) {
					if (logger.isInfoEnabled()) {
						logger.info("getInstWithPropsValue no instance was found for this metric value.");
					}
					continue;
				}
				inst.getPropValues().put(resourcePropertyDef.getId(),
						metricValues);
			}
		}
		moduleInstances = new ArrayList<>();
		for (Iterator<Entry<String, List<ModelResourceInstance>>> iterator = moduleInstancesMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, List<ModelResourceInstance>> entry = iterator.next();
			List<ModelResourceInstance> instanceList = entry.getValue();
			for (ModelResourceInstance modelResourceInstance : instanceList) {
				fillInstBasicProp(modelResourceInstance, resourceDef);
				moduleInstances.add(modelResourceInstance);
			}
		}
		return moduleInstances;
	}

	private Map<String, ResourcePropertyDef> getInstProps(
			ResourceDef resourceDef) throws InstanceDiscoveryException {
		Map<String, ResourcePropertyDef> pdefMap = new HashMap<>();
		fillPropMap(pdefMap, resourceDef);
		return pdefMap;
	}

	private void fillPropMap(Map<String, ResourcePropertyDef> pdefMap,
			ResourceDef resourceDef) throws InstanceDiscoveryException {
		ResourcePropertyDef[] props = resourceDef.getPropertyDefs();
		if (props != null && props.length > 0) {
			for (ResourcePropertyDef resourcePropertyDef : props) {
				ResourceMetricDef resourceMetric = resourcePropertyDef
						.getResourceMetric();
				if (null != resourceMetric) {
					pdefMap.put(resourceMetric.getId(), resourcePropertyDef);
				}
			}
		} else {
			StringBuilder b = new StringBuilder(
					"can't find resouceDef: for discovery.resourceDefId=")
					.append(resourceDef.getId());
			String msg = b.toString();
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new InstanceDiscoveryException(
					ServerErrorCodeConstant.ERROR_DISCOVERY_RESOURCE_NOT_EXIST,
					msg);
		}
	}

	/**
	 * 填充实例的基础信息
	 * 
	 * @param instance
	 * 
	 * @param resourceDef
	 * @throws InstanceDiscoveryException
	 */
	private void fillInstBasicProp(ModelResourceInstance instance,
			ResourceDef resourceDef) throws InstanceDiscoveryException {
		/**
		 * 根据模型定义，将其实例化属性的值生成一个唯一
		 */
		String[] instIdPropIds = resourceDef.getInstantiationDef()
				.getInstanceId();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("fillInstBasicProp resourceId=");
			b.append(resourceDef.getId());
			b.append(" instIdPropIds=");
			b.append(Arrays.toString(instIdPropIds));
			logger.info(b.toString());
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < instIdPropIds.length; i++) {
			String[] propValues = instance.getPropValues()
					.get(instIdPropIds[i]);
			if (propValues != null) {
				for (int j = 0; j < propValues.length; j++) {
					builder.append(propValues[j]).append('-');
				}
			} else {
				StringBuilder b = new StringBuilder();
				b.append("resourceId=").append(resourceDef.getId())
						.append(" propValues instIdPropId=")
						.append(instIdPropIds[i])
						.append(" value is null,please check!");
				String errorMsg = b.toString();
				if (logger.isErrorEnabled()) {
					logger.error(errorMsg);
				}
				throw new InstanceDiscoveryException(
						ServerErrorCodeConstant.ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY,
						errorMsg);
			}
		}
		builder.deleteCharAt(builder.length() - 1);
		String instanceIdentifiedKey = DigestUtils.md5Hex(builder.toString());
		instance.getPropValues().put(InstanceCollectDiscover.INST_IDENTY_KEY,
				new String[] { instanceIdentifiedKey });
		builder.append(" encodedValue=").append(instanceIdentifiedKey);
		if (logger.isInfoEnabled()) {
			logger.info("fillInstBasicProp instIdPropIdsValues="
					+ builder.toString());
		}
		String instNamePropId = resourceDef.getInstantiationDef()
				.getInstanceName();
		if (logger.isInfoEnabled()) {
			logger.info("fillInstBasicProp instNamePropId=" + instNamePropId);
		}
		String[] instNames = instance.getPropValues().get(instNamePropId);
		if (instNames != null && instNames.length > 0) {
			instance.setName(instNames[0]);
			if (logger.isInfoEnabled()) {
				logger.info("fillInstBasicProp instNamePropIdValue="
						+ instNames[0]);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("can't find instanceName[" + instNamePropId + "]");
			}
		}
		if (resourceDef.getCategory() != null) {
			instance.setCategoryId(resourceDef.getCategory().getId());
		}
		instance.setChildType(resourceDef.getType());
		instance.setResourceId(resourceDef.getId());
	}
}
