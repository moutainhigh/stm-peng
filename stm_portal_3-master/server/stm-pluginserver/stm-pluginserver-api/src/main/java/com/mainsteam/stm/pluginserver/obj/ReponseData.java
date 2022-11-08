/**
 * 
 */
package com.mainsteam.stm.pluginserver.obj;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.exception.BaseException;

/**
 * 指标查询请求的返回数据
 * 
 * @author ziw
 *
 */
public class ReponseData {

	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 资源实例id
	 */
	private long resourceInstanceId;
	
	/**
	 * 资源ids
	 */
	private String resourceId;
	
	/**
	 * 采集时间
	 */
	private Date collectTime;
	
	/**
	 * 失败原因
	 */
	private BaseException cause;
	
	private List<ReponseIndexData> reponseIndexDatas;
	
	private long pluginRequestId;
	
	/**
	 * 
	 */
	public ReponseData() {
	}
	

	/**
	 * @param metricId
	 * @param resourceInstanceId
	 * @param resourceId
	 * @param collectTime
	 * @param cause
	 * @param reponseIndexDatas
	 * @param pluginRequestId
	 */
	public ReponseData(String metricId, long resourceInstanceId,
			String resourceId, Date collectTime, BaseException cause,
			List<ReponseIndexData> reponseIndexDatas, long pluginRequestId) {
		this.metricId = metricId;
		this.resourceInstanceId = resourceInstanceId;
		this.resourceId = resourceId;
		this.collectTime = collectTime;
		this.cause = cause;
		this.reponseIndexDatas = reponseIndexDatas;
		this.pluginRequestId = pluginRequestId;
	}



	/**
	 * @return the pluginRequestId
	 */
	public final long getPluginRequestId() {
		return pluginRequestId;
	}

	/**
	 * @param pluginRequestId the pluginRequestId to set
	 */
	public final void setPluginRequestId(long pluginRequestId) {
		this.pluginRequestId = pluginRequestId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	/**
	 * @return the cause
	 */
	public final BaseException getCause() {
		return cause;
	}

	/**
	 * @param cause the cause to set
	 */
	public final void setCause(BaseException cause) {
		this.cause = cause;
	}

	/**
	 * @return the reponseIndexDatas
	 */
	public final List<ReponseIndexData> getReponseIndexDatas() {
		return reponseIndexDatas;
	}

	/**
	 * @param reponseIndexDatas the reponseIndexDatas to set
	 */
	public final void setReponseIndexDatas(List<ReponseIndexData> reponseIndexDatas) {
		this.reponseIndexDatas = reponseIndexDatas;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReponseData [metricId=" + metricId + ", resourceInstanceId="
				+ resourceInstanceId + ", resourceId=" + resourceId
				+ ", collectTime=" + collectTime + ", cause=" + cause
				+ ", reponseIndexDatas=" + reponseIndexDatas + "]";
	}
}
