/**
 * 
 */
package com.mainsteam.stm.home.workbench.metric.web.vo;

/**
 * 
 * <li>文件名称:MetricDataQueryVo.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2017年4月1日 下午5:54:50
 * @author tandl
 */
public class MetricDataQueryVo {
	private long instanceId;
	private String[] metricId;
	private long dateStart;
	private long dateEnd;
	private String summaryType;

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String[] getMetricId() {
		return metricId;
	}

	public void setMetricId(String[] metricId) {
		this.metricId = metricId;
	}

	public long getDateStart() {
		return dateStart;
	}

	public void setDateStart(long dateStart) {
		this.dateStart = dateStart;
	}

	public long getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(long dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

}
