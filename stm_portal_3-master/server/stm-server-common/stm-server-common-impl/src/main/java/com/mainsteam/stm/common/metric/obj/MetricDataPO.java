package com.mainsteam.stm.common.metric.obj;

import java.util.Calendar;


/**
 * @author cx
 * 
 */
public class MetricDataPO extends MetricData {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 仅用于数据存储；
	 */
	private String tableName;

	private String metricData;
	//用于区分分区标识. 范围 1-31 
	private int partitionId;

	public int getPartitionId() {
		return partitionId;
	}

	public String getMetricData() {
		String data[] = super.getData();
		return (data != null && data.length > 0) ? data[0] : null;
	}

	public void setMetricData(String metricData) {
		this.metricData = metricData;
		super.setData(new String[] { metricData });
	}

	public java.sql.Timestamp getCollectTimeStamp() {
		return super.getCollectTime() == null ? null : new java.sql.Timestamp(
				super.getCollectTime().getTime());
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public void setCollectTime(java.util.Date collectTime) {
		super.setCollectTime(collectTime);
		Calendar c = Calendar.getInstance();
		c.setTime(collectTime);
		this.partitionId = c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static MetricDataPO convert(MetricData md) {
		if (md == null)
			return null;
		MetricDataPO po = new MetricDataPO();
		po.setCollectTime(md.getCollectTime());
		po.setMetricId(md.getMetricId());
		po.setResourceId(md.getResourceId());
		po.setProfileId(md.getProfileId());
		po.setTimelineId(md.getTimelineId());
		po.setMetricType(md.getMetricType());
		po.setResourceInstanceId(md.getResourceInstanceId());
		po.setData(md.getData());
		return po;
	}
}
