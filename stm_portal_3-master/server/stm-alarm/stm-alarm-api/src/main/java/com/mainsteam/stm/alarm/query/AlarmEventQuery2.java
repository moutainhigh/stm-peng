package com.mainsteam.stm.alarm.query;

import java.util.List;
import java.util.Map;

public class AlarmEventQuery2 {

	public enum OrderField{
		LEVEL("ALARM_LEVEL"),SOURCE_IP,COLLECTION_TIME,CONTENT,ITSM_DATA,
		SOURCE_NAME,
		EXT0,EXT1,EXT2,EXT3,EXT4,EXT5,EXT6,EXT7,EXT8,EXT9;

		private String val;

		OrderField(){
			val = this.name();
		}

		OrderField(String alarm_level) {
			val = alarm_level;
		}

		@Override
		public String toString() {
			return val;
		}
	}

	public enum OrderAscOrDesc{
		DESC, ASC;
	}

	private OrderField[] orderFieldes;
	private boolean orderASC;
	private List<AlarmEventQueryDetail> filters;
	private Map<OrderField, OrderAscOrDesc> orderCollections;
	
	public OrderField[] getOrderFieldes() {
		return orderFieldes;
	}
	public void setOrderFieldes(OrderField[] orderFieldes) {
		this.orderFieldes = orderFieldes;
	}
	public boolean isOrderASC() {
		return orderASC;
	}
	public void setOrderASC(boolean orderASC) {
		this.orderASC = orderASC;
	}
	public List<AlarmEventQueryDetail> getFilters() {
		return filters;
	}
	public void setFilters(List<AlarmEventQueryDetail> filters) {
		this.filters = filters;
	}

	public Map<OrderField, OrderAscOrDesc> getOrderCollections() {
		return orderCollections;
	}

	public void setOrderCollections(Map<OrderField, OrderAscOrDesc> orderCollections) {
		this.orderCollections = orderCollections;
	}
}
