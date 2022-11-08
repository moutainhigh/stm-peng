package com.mainsteam.stm.common.metric.query;


public class MetricRealtimeDataQuery {
	
	private String[] metricID;
	private long[] instanceID;
	private String orderMetricID;
	private boolean orderForDesc;
	public String[] getMetricID() {
		return metricID;
	}
	public void setMetricID(String[] metricID) {
		this.metricID = metricID;
	}
	public long[] getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long[] instanceID) {
		this.instanceID = instanceID;
	}
	public String getOrderMetricID() {
		return orderMetricID;
	}
	public void setOrderMetricID(String orderMetricID) {
		this.orderMetricID = orderMetricID;
	}
	public boolean isOrderForDesc() {
		return orderForDesc;
	}
	public void setOrderForDesc(boolean orderForDesc) {
		this.orderForDesc = orderForDesc;
	}
	
	public void valid(){
		if(orderMetricID!=null && !"".equals(orderMetricID.trim())){
			boolean order=false;
			for(String m:metricID){
				if(m.equals(orderMetricID)){
					order=true;
					break;
				}
			}
			if(!order){
				throw new RuntimeException("can't find the order["+orderMetricID+"] in the metricIDs,please check.");
			}
		}
	}
}
