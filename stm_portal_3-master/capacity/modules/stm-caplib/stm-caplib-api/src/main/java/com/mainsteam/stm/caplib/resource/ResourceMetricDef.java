package com.mainsteam.stm.caplib.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.caplib.collect.DemoMetricCollect;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class ResourceMetricDef implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2182438512028881261L;
	
	private static boolean isDemo;
	static {
		try {
			Properties properties = PropertiesFileUtil.getProperties("demo.properties");
			if (properties == null) {
				properties = new Properties();
				InputStream in = ClassLoader.getSystemResourceAsStream("demo.properties");
				if (in != null) {
					properties.load(in);
				}
			}
			isDemo = Boolean.valueOf(properties.getProperty("stm.demo.isDemo"));
		} catch (Exception ignore) {
		}
	}

	private String id;
	private MetricTypeEnum style;
	private String name;
	private String description;
	private String unit;

	private boolean isEdit;
	private boolean isAlert;
	private boolean isMonitor;

	/**
	 * 指标是否显示
	 */
	private boolean isDisplay;
	/**
	 * 指标显示顺序
	 */
	private String displayOrder;
	private int defaultFlapping;

	private FrequentEnum defaultMonitorFreq;
	private FrequentEnum[] supportMonitorFreqs;

	private ResourceDef resourceDef;
	private ThresholdDef[] thresholdDefs;

	private MetricCollect[] metricPlugins;
	private boolean isTable = false;
	
	private MetricCollect demoMetricCollect;
	private MetricCollect[] demoMetricCollects;
	
	public ResourceMetricDef() {
		if (isDemo) { 
			demoMetricCollect = new DemoMetricCollect();
			demoMetricCollects = new DemoMetricCollect[1];
			demoMetricCollects[0] = demoMetricCollect;
		}
	}
	
	/**
	 * 获取资源
	 * 
	 * @return
	 */
	public ResourceDef getResourceDef() {
		return resourceDef;
	}

	public void setResourceDef(ResourceDef resourceDef) {
		this.resourceDef = resourceDef;
	}

	// public MetricCollect getMetricPluginByType(String collType) {
	// if (null == metricPlugins) {
	// return null;
	// }
	// if (1 == metricPlugins.length) {
	// return metricPlugins[0];
	// }
	// for (MetricCollect metricCollect : metricPlugins) {
	// String collType1 = metricCollect.getCollectType();
	// if (null != collType1 && collType1.equalsIgnoreCase(collType)) {
	// return metricCollect;
	// }
	// }
	// return null;
	// }

	public MetricCollect getMetricPluginByType(String collType, String sysoid) {
		// if (this.getId().equals("ifName")) {
		// System.out.println("");
		// }
		if (isDemo) { 
			return demoMetricCollect;
		}
		
		if (null == metricPlugins) {
			return null;
		}
		if (1 == metricPlugins.length) {
			return metricPlugins[0];
		}
		// collectype
		MetricCollect result = null;

		if (StringUtils.isNotEmpty(sysoid)) {
			for (MetricCollect metricCollect : metricPlugins) {
				if (metricCollect.containsSysoid(sysoid)) {
					result = metricCollect;
					break;
				}
			}
		} else if (StringUtils.isNotEmpty(collType)) {
			for (MetricCollect metricCollect : metricPlugins) {
				String collType1 = metricCollect.getCollectType();
				if (null != collType1 && collType1.equalsIgnoreCase(collType)) {
					result = metricCollect;
					break;
				}
			}
		}

		// default
		if (null == result) {
			for (MetricCollect metricCollect : metricPlugins) {
				if (metricCollect.isDefault()) {
					result = metricCollect;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 获取MetricCollect集合
	 * 
	 * @return
	 */
	public MetricCollect[] getMetricPlugins() {
		if (isDemo) { 
			return demoMetricCollects;
		}
		return metricPlugins;
	}

	public void setMetricPlugins(MetricCollect[] metricPlugins) {
		this.metricPlugins = metricPlugins;
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
	 * 获取类型
	 * 
	 * @return
	 */
	public MetricTypeEnum getMetricType() {
		return style;
	}

	public void setMetricType(MetricTypeEnum type) {
		this.style = type;
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
	 * 获取单位
	 * 
	 * @return
	 */
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 是否编辑
	 * 
	 * @return
	 */
	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	/**
	 * 是否alert
	 * 
	 * @return
	 */
	public boolean isAlert() {
		return isAlert;
	}

	public void setAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}

	/**
	 * 是否监控
	 * 
	 * @return
	 */
	public boolean isMonitor() {
		return isMonitor;
	}

	public void setMonitor(boolean isMonitor) {
		this.isMonitor = isMonitor;
	}

	/**
	 * 获取默认监控频率
	 * 
	 * @return
	 */
	public FrequentEnum getDefaultMonitorFreq() {
		return defaultMonitorFreq;
	}

	public void setDefaultMonitorFreq(FrequentEnum defaultMonitorFreq) {
		this.defaultMonitorFreq = defaultMonitorFreq;
	}

	/**
	 * 获取support监控频率
	 * 
	 * @return
	 */
	public FrequentEnum[] getSupportMonitorFreq() {
		return supportMonitorFreqs;
	}

	public void setSupportMonitorFreqs(FrequentEnum[] supportMonitorFreqs) {
		this.supportMonitorFreqs = supportMonitorFreqs;
	}

	/**
	 * 获取ThresholdDef集合
	 * 
	 * @return
	 */
	public ThresholdDef[] getThresholdDefs() {
		return thresholdDefs;
	}

	public void setThresholdDefs(ThresholdDef[] thresholdDefs) {
		this.thresholdDefs = thresholdDefs;
	}

	/**
	 * 是否显示
	 * 
	 * @return
	 */
	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	/**
	 * 获取排序值
	 * 
	 * @return
	 */
	public String getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(String displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void setDefaultFlapping(int defaultFlapping) {
		this.defaultFlapping = defaultFlapping;
	}

	public int getDefaultFlapping() {
		return this.defaultFlapping;
	}

	public void setIsTable(boolean b) {
		this.isTable = b;
	}

	/**
	 * 该指标返回的数据是不是表格方式，比如ip地址表，arp表，进程表
	 * 
	 * @return true
	 */
	public boolean isTable() {
		return this.isTable;
	}
}
