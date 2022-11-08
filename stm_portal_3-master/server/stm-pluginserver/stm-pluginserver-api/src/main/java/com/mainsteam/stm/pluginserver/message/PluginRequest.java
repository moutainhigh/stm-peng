/**
 * 
 */
package com.mainsteam.stm.pluginserver.message;

import java.util.Date;
import java.util.Map;

import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginMapExecutorParameter;

/**
 * plugin请求
 * 
 * @author ziw
 * 
 */
public class PluginRequest {

	/**
	 * 属性的注释见方法
	 */
	private long requestId;
	private long resourceInstId;
	private String metricId;
	private boolean customMetric = false;
	private String resourceId;
	private String pluginId;
	private String pluginSessionKey;
	private PluginInitParameter pluginInitParameter;
	private PluginExecutorParameter<?> pluginExecutorParameter;
	private long batch;
	private long cacheExpireTime;
	private Date collectTime;
	private PluginRequestEnum pluginRequestType;
	private PluginProcessParameter[] pluginProcessParameters;
	private PluginConvertParameter pluginConvertParameter;
	private ResultSetMetaInfo resultSetMetaInfo;
	/**
	 * pluginsession执行时，用来判断执行命令是否相同的key
	 */
	private String pluginSessionExecuteIdentyfiedKey;

	/**
	 * 使用的策略id
	 */
	private long profileId = -1;

	/**
	 * 使用的基线id
	 */
	private long timelineId = -1;

	/**
	 * 转换器依赖的索引属性名称
	 */
	private String convertParameterIndexProperty;

	private String requestInstanceMetricKey;

	/**
	 * 获取请求的id
	 * 
	 * @return
	 */
	public long getRequestId() {
		return this.requestId;
	}

	/**
	 * @return the requestInstanceMetricKey
	 */
	public final String getRequestInstanceMetricKey() {
		if (requestInstanceMetricKey == null) {
			StringBuilder b = new StringBuilder();
			b.append(resourceInstId).append('-').append(metricId);
			requestInstanceMetricKey = b.toString();
		}
		return requestInstanceMetricKey;
	}

	/**
	 * 获取资源实例id
	 * 
	 * @return
	 */
	public long getResourceInstId() {
		return this.resourceInstId;
	}

	/**
	 * 获取指标id
	 * 
	 * @return
	 */
	public String getMetricId() {
		return this.metricId;
	}

	/**
	 * 获取resourceId
	 * 
	 * @return
	 */
	public String getResourceId() {
		return this.resourceId;
	}

	/**
	 * 获取插件的id
	 * 
	 * @return
	 */
	public String getPluginId() {
		return this.pluginId;
	}

	/**
	 * 获取plugin session的key。表示是否采用同一session.
	 * 
	 * @return
	 */
	public String getPluginSessionKey() {
		return this.pluginSessionKey;
	}

	/**
	 * 获取为插件运行准备的初始化参数
	 * 
	 * @return
	 */
	public PluginInitParameter getPluginInitParameter() {
		return this.pluginInitParameter;
	}

	/**
	 * 获取为插件执行准备的执行参数
	 * 
	 * @return
	 */
	public PluginExecutorParameter<?> getPluginExecutorParameter() {
		return this.pluginExecutorParameter;
	}

	/**
	 * 请求的批次。对于同一批次的数据，如果使用的是同一个plugin session，则使用该批次的前一个请求得到的数据。
	 * 
	 * @return
	 */
	public long getBatch() {
		return this.batch;
	}

	/**
	 * 获取该批次的数据cache的失效时间
	 * 
	 * @return million seconds
	 */
	public long getCacheExpireTime() {
		return this.cacheExpireTime;
	}

	/**
	 * 获取该次请求的发起时间
	 * 
	 * @return
	 */
	public Date getCollectTime() {
		return this.collectTime;
	}

	/**
	 * 获取该次请求的类型
	 * 
	 * @return PluginRequestEnum
	 */
	public PluginRequestEnum getPluginRequestType() {
		return this.pluginRequestType;
	}

	/**
	 * 获取该次请求对数据的处理类和参数
	 * 
	 * @return
	 */
	public PluginProcessParameter[] getPluginProcessParameters() {
		return this.pluginProcessParameters;
	}

	/**
	 * 获取该次请求对数据转换为指标的处理类和参数
	 * 
	 * @return
	 */
	public PluginConvertParameter getPluginConvertParameter() {
		return this.pluginConvertParameter;
	}

	/**
	 * 获取本次查询得到的数据的metainfo设置信息
	 * 
	 * @return
	 */
	public ResultSetMetaInfo getResultSetMetaInfo() {
		return this.resultSetMetaInfo;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public void setResourceInstId(long resourceInstId) {
		this.resourceInstId = resourceInstId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public void setPluginSessionKey(String pluginSessionKey) {
		this.pluginSessionKey = pluginSessionKey;
	}

	public void setPluginInitParameter(PluginInitParameter pluginInitParameter) {
		this.pluginInitParameter = pluginInitParameter;
	}

	public void setPluginExecutorParameter(
			PluginExecutorParameter<?> pluginExecutorParameter) {
		this.pluginExecutorParameter = pluginExecutorParameter;
	}

	public void setBatch(long batch) {
		this.batch = batch;
	}

	public void setCacheExpireTime(long cacheExpireTime) {
		this.cacheExpireTime = cacheExpireTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public void setPluginRequestType(PluginRequestEnum pluginRequestType) {
		this.pluginRequestType = pluginRequestType;
	}

	public void setPluginProcessParameters(
			PluginProcessParameter[] pluginProcessParameters) {
		this.pluginProcessParameters = pluginProcessParameters;
	}

	public void setPluginConvertParameter(
			PluginConvertParameter pluginConvertParameter) {
		this.pluginConvertParameter = pluginConvertParameter;
	}

	public void setResultSetMetaInfo(ResultSetMetaInfo resultSetMetaInfo) {
		this.resultSetMetaInfo = resultSetMetaInfo;
	}

	/**
	 * @return the profileId
	 */
	public final long getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId
	 *            the profileId to set
	 */
	public final void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the timelineId
	 */
	public final long getTimelineId() {
		return timelineId;
	}

	/**
	 * @param timelineId
	 *            the timelineId to set
	 */
	public final void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}

	/**
	 * @return the convertParameterIndexProperty
	 */
	public final String getConvertParameterIndexProperty() {
		return convertParameterIndexProperty;
	}

	/**
	 * @param convertParameterIndexProperty
	 *            the convertParameterIndexProperty to set
	 */
	public final void setConvertParameterIndexProperty(
			String convertParameterIndexProperty) {
		this.convertParameterIndexProperty = convertParameterIndexProperty;
	}
	

	/**
	 * @return the customMetric
	 */
	public final boolean isCustomMetric() {
		return customMetric;
	}

	/**
	 * @param customMetric the customMetric to set
	 */
	public final void setCustomMetric(boolean customMetric) {
		this.customMetric = customMetric;
	}

	/**
	 * @return the pluginSessionExecuteIdentyfiedKey
	 */
	public final String getPluginSessionExecuteIdentyfiedKey() {
		PluginExecutorParameter<?> pluginExecutorParameter = getPluginExecutorParameter();
		if (this.pluginSessionExecuteIdentyfiedKey == null
				&& this.pluginSessionKey != null) {
			if (pluginExecutorParameter != null) {
				StringBuilder b = new StringBuilder(this.pluginSessionKey);
				if (pluginExecutorParameter instanceof PluginArrayExecutorParameter) {
					String[] values = ((PluginArrayExecutorParameter) pluginExecutorParameter)
							.getParametersValue();
					if (values != null && values.length > 0) {
						for (String v : values) {
							b.append(';').append(v);
						}
					}
				} else if (pluginExecutorParameter instanceof PluginMapExecutorParameter) {
					Map<String, Parameter> ps = ((PluginMapExecutorParameter) pluginExecutorParameter)
							.getParameters();
					if (ps != null && ps.size() > 0) {
						for (String key : ps.keySet()) {
							b.append('$');
							Parameter p = ps.get(key);
							if (p == null) {
								b.append("null");
							} else {
								b.append(p.getValue());
							}
						}
					}
				}
				this.pluginSessionExecuteIdentyfiedKey = b.toString();
			}
		}
		return this.pluginSessionExecuteIdentyfiedKey;
	}
}
