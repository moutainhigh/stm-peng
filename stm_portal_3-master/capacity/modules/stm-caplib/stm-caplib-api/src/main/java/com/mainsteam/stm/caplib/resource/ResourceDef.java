package com.mainsteam.stm.caplib.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParaChanger;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;

public class ResourceDef implements java.io.Serializable{
	//malachi in
	private static final String MUST_INPUT = "mustInput";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8154324557052127715L;
	private static final String DEFAULT_VALUE = "DefaultValue";
	private static final String IS_DISPLAY = "isDisplay";
	private String id;
	private String name;
	private String description;
	private String icon;
	private String type;
	private boolean autodiscovery = true;
	private boolean isMain = false;

	private ResourceDef parentResourceDef;
	private ResourceDef[] childResourceDefs;
	private ResourceMetricDef[] metricDefs;
	private Map<String, PluginInitParaChanger> pluginInitParaChangers;
	private ResourcePropertyDef[] propertyDefs;
	private ResourceInstedDef instantiationDef;
	private CategoryDef category;

	private static final Log logger = LogFactory.getLog(ResourceDef.class);

	public ResourceDef() {
		this.pluginInitParaChangers = new HashMap<String, PluginInitParaChanger>();
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public boolean isAutodiscovery() {
		return autodiscovery;
	}

	public void setAutodiscovery(boolean autodiscovery) {
		this.autodiscovery = autodiscovery;
	}

	/**
	 * 获取可选插件id 一些指标有多种采集类型，并且这些指标的多种采集类型是一致的 比如：a指标有三种采集类型（WMI，SSH，TELNET），
	 * b指标如果有多种采集类型，那么必须和a指标一样 返回值：如果有指标存在多种采集类型，返回采集类型集合 如果没有指标存在多种采集类型，返回null
	 * 
	 * @return
	 */
	public Map<String, String> getOptionCollPluginIds() {
		Map<String, String> collTypes = new HashMap<String, String>();
		for (ResourceMetricDef metricDef : metricDefs) {
			MetricCollect[] metricCollects = metricDef.getMetricPlugins();
			if (metricCollects != null && metricCollects.length > 1) {
				for (int i = 0; i < metricCollects.length; i++) {
					String collectType = metricCollects[i].getCollectType();
					if (StringUtils.isNotEmpty(collectType)) {
						collTypes.put(metricCollects[i].getPlugin().getId(),
								collectType);
					}
				}
				return collTypes;
			}
		}
		return collTypes;
	}

	/**
	 * 获取模型对应的必选插件id 这些id和getOptionCollPluginIds加起来是所有的id
	 * 
	 * @return
	 */
	public Set<String> getRequiredCollPluginIds() {
		Set<String> sets = new HashSet<String>();
		for (ResourceMetricDef metricDef : metricDefs) {
			MetricCollect[] metricCollects = metricDef.getMetricPlugins();
			if (metricCollects != null) {
				for (MetricCollect metricCollect : metricCollects) {
					PluginDef plugin = metricCollect.getPlugin();
					if (null == plugin) {
						logger.error("NULL plugin:" + metricDef.getId());
					}else{
						sets.add(plugin.getId());
					}
				}
			}
		}
		Map<String, String> map = getOptionCollPluginIds();
		sets.removeAll(map.values());
		return sets;
	}

	/**
	 * 获取所有配置的plugid，用于portal判断是不是snmp的，portal调用这个方法后再看是否包含PluginIdEnum.SnmpPlugin.name()
	 * 
	 * @return
	 */
	public Set<String> getConfigPluginIds() {
		Set<String> allIds = new HashSet<String>();
		for (ResourceMetricDef metricDef : metricDefs) {
			MetricCollect[] metricCollects = metricDef.getMetricPlugins();
			if (null != metricCollects) {
				for (MetricCollect metricCollect : metricCollects) {
					PluginDef plugin = metricCollect.getPlugin();
					if (null != plugin) {
						allIds.add(plugin.getId());
					}
				}
			}
		}
		return allIds;
	}

	/**
	 * 获取插件初始化参数 当前resource资源下有一系列的指标，每个指标都有相应的插件， 这个插件里面配置有它的初始化参数
	 * 这里获取的插件参数其实和通过它的指标去获取的插件参数是一样的
	 * 
	 * @return Map<String, PluginInitParameter[]> (key: pluginid)
	 */
	public Map<String, PluginInitParameter[]> getPluginInitParameterMap() {
		List<PluginDef> plugins = new ArrayList<PluginDef>();
		for (ResourceMetricDef metricDef : metricDefs) {
			MetricCollect[] metricCollects = metricDef.getMetricPlugins();
			if (metricCollects != null) {
				for (MetricCollect metricCollect : metricCollects) {
					PluginDef plugin = metricCollect.getPlugin();
					if (!plugins.contains(plugin)) {
						plugins.add(plugin);
					}
				}
			}
		}

		Map<String, PluginInitParameter[]> pluginInitParameterMap = new HashMap<String, PluginInitParameter[]>();
		Set<String> plugIds = new HashSet<String>();
		for (PluginDef plugin : plugins) {
			String pluginId = plugin.getId();
			if (plugIds.contains(pluginId)) {
				continue;
			}
			plugIds.add(pluginId);
			PluginInitParameter[] parameters = plugin.getPluginInitParameters();
			for (PluginInitParameter parameter : parameters) {
				String parameterId = parameter.getId();
				String key = pluginId + "," + parameterId;
				Set<Entry<String, PluginInitParaChanger>> entrySet = pluginInitParaChangers
						.entrySet();
				for (Entry<String, PluginInitParaChanger> changer : entrySet) {
					String ckey = changer.getKey();
					if (ckey.equalsIgnoreCase(key)) {
						PluginInitParaChanger cvalue = changer.getValue();
						String propId = cvalue.getPropertyId();
						String propValue = cvalue.getPropertyValue();
						switch (propId) {
						case IS_DISPLAY:
							boolean b = Boolean.valueOf(propValue);
							parameter.setDisplay(b);
							break;
						case DEFAULT_VALUE:
							parameter.setDefaultValue(propValue);
							break;
						case MUST_INPUT:
							boolean isMustInput = Boolean.valueOf(propValue);
							parameter.setMustInput(isMustInput);
							break;
						default:
							break;
						}
						break;
					}
				}
			}
			pluginInitParameterMap.put(pluginId, parameters);
		}
		//

		return pluginInitParameterMap;
	}

	/**
	 * 获取对应的Category
	 * 
	 * @return
	 */
	public CategoryDef getCategory() {
		return category;
	}

	public void setCategory(CategoryDef category) {
		this.category = category;
	}

	/**
	 * 获取id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取描述信息
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取图片信息
	 * 
	 * @return
	 */
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 获取上一级资源
	 * 
	 * @return
	 */
	public ResourceDef getParentResourceDef() {
		return parentResourceDef;
	}

	public void setParentResourceDef(ResourceDef parentResourceDef) {
		this.parentResourceDef = parentResourceDef;
	}

	/**
	 * 获取下一级资源集合
	 * 
	 * @return
	 */
	public ResourceDef[] getChildResourceDefs() {
		return childResourceDefs;
	}

	/**
	 * 获取当前Resource下的子资源ID的集合
	 * 
	 * @return
	 */
	public String[] getChildResourceIds() {
		List<String> ids = new ArrayList<String>();
		if (null != childResourceDefs) {
			for (ResourceDef childResourceDef : childResourceDefs) {
				ids.add(childResourceDef.getId());
			}
		}
		String[] idsArray = new String[ids.size()];
		return ids.toArray(idsArray);
	}

	/**
	 * 根据type获取当前模型下的子模型的ID
	 * 
	 * @return
	 */
	public String getChildResourceIdByType(String type) {
		if (null == type) {
			return null;
		}
		if (null != childResourceDefs) {
			for (ResourceDef childResourceDef : childResourceDefs) {
				if (type.equalsIgnoreCase(childResourceDef.getType())) {
					return childResourceDef.getId();
				}
			}
		}
		return null;
	}

	public void setChildResourceDefs(ResourceDef[] childResourceDefs) {
		this.childResourceDefs = childResourceDefs;
	}

	/**
	 * 获取指标集合
	 * 
	 * @return
	 */
	public ResourceMetricDef[] getMetricDefs() {
		return metricDefs;
	}

	public void setMetricDefs(ResourceMetricDef[] metricDefs) {
		this.metricDefs = metricDefs;
	}

	/**
	 * 获取属性集合
	 * 
	 * @return
	 */
	public ResourcePropertyDef[] getPropertyDefs() {
		return propertyDefs;
	}

	public void setPropertyDefs(ResourcePropertyDef[] propertyDefs) {
		this.propertyDefs = propertyDefs;
	}

	/**
	 * 获取Instantiation集合
	 * 
	 * @return
	 */
	public ResourceInstedDef getInstantiationDef() {
		return instantiationDef;
	}

	public void setInstantiationDef(ResourceInstedDef instantiationDef) {
		this.instantiationDef = instantiationDef;
	}

	public void setPluginInitParaChangers(
			PluginInitParaChanger[] pluginInitParaChangers) {
		for (PluginInitParaChanger pluginInitParaChanger : pluginInitParaChangers) {
			String key = pluginInitParaChanger.getPluginId() + ","
					+ pluginInitParaChanger.getParameterId();
			this.pluginInitParaChangers.put(key, pluginInitParaChanger);
		}
	}
}
