package com.mainsteam.stm.caplib.collect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.caplib.plugin.PluginConnectSetting;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginParameter;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;

/**
 * 
 * @author Administrator
 * 
 */
public class MetricCollect implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 690831668570227933L;

	/**
	 * 采集类型，比如WMI，SSH/TELNET
	 */
	private String collectType;

	/**
	 * 采集类型，比如WMI，SSH/TELNET
	 */
	private Set<String> sysoids;
	/**
	 * 指标定义
	 */
	private ResourceMetricDef resourceMetric;
	/**
	 * 插件定义
	 */
	private PluginDef plugin;
	/**
	 * 插件参数
	 */
	private PluginParameter pluginParameter;

	private boolean isDefault = false;
	/**
	 * 插件连接参数设置
	 */
	private PluginConnectSetting[] pluginConnectSettings;

	/**
	 * 列标题
	 */
	private PluginResultMetaInfo pluginResultMetaInfo;
	/**
	 * 处理器
	 */
	private PluginDataHandler[] pluginDataHandlers;
	/**
	 * 转换器
	 */
	private PluginDataConverter converter;

	public MetricCollect() {
		sysoids = new HashSet<String>();
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * 获取采集类型
	 * 
	 * @return
	 */
	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

	/**
	 * 获取指标
	 * 
	 * @return
	 */
	public ResourceMetricDef getResourceMetric() {
		return resourceMetric;
	}

	public void setResourceMetric(ResourceMetricDef resourceMetric) {
		this.resourceMetric = resourceMetric;
	}

	/**
	 * 获取插件
	 * 
	 * @return
	 */
	public PluginDef getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginDef plugin) {
		this.plugin = plugin;
	}

	/**
	 * 获取插件执行参数
	 * 
	 * @return
	 */
	public PluginParameter getPluginParameter() {
		return pluginParameter;
	}

	public void setPluginParameter(PluginParameter pluginParameter) {
		this.pluginParameter = pluginParameter;
	}

	/**
	 * 获取插件采集结果集的表头信息
	 * 
	 * @return
	 */
	public PluginResultMetaInfo getPluginResultMetaInfo() {
		return pluginResultMetaInfo;
	}

	public void setPluginResultMetaInfo(
			PluginResultMetaInfo pluginResultMetaInfo) {
		this.pluginResultMetaInfo = pluginResultMetaInfo;
	}

	/**
	 * 获取插件数据处理参数
	 * 
	 * @return
	 */
	public PluginDataHandler[] getPluginDataHandlers() {
		return pluginDataHandlers;
	}

	public void setPluginDataHandlers(PluginDataHandler[] pluginDataHandlers) {
		this.pluginDataHandlers = pluginDataHandlers;
	}

	public void setPluginDataConverter(PluginDataConverter converter) {
		this.converter = converter;
	}

	/**
	 * 获取插件数据转换参数
	 * 
	 * @return
	 */
	public PluginDataConverter getPluginDataConverter() {
		return this.converter;
	}

	/**
	 * 插件连接参数设置
	 * 
	 * @return
	 */
	public PluginConnectSetting[] getPluginConnectSettings() {
		return pluginConnectSettings;
	}

	public void setPluginConnectSettings(
			PluginConnectSetting[] pluginConnectSettings) {
		this.pluginConnectSettings = pluginConnectSettings;
	}

	public boolean containsSysoid(String sysoid) {
		return this.sysoids.contains(sysoid);
	}

	public boolean sysoidIsEmpty() {
		return this.sysoids.isEmpty();
	}

	public void addSysoids(Collection<String> sysoids) {
		this.sysoids.addAll(sysoids);
	}

}
