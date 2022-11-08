package com.mainsteam.stm.webService.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BeanMapConvertor")
public class BeanMapConvertor {
	
	private List<MapEntry> entries = new ArrayList<BeanMapConvertor.MapEntry>();

	public void addEntry(MapEntry entry) {
		entries.add(entry);
	}

	public List<MapEntry> getEntries() {
		return entries;
	}

	
	public static class MapEntry {
		private Long key;
		private List<ThresholdSyncBean> value;

		public MapEntry() {

		}

		public MapEntry(Long key, List<ThresholdSyncBean> metricList) {
			this.key = key;
			this.value = metricList;
		}

		public MapEntry(Map.Entry<Long, List<ThresholdSyncBean>> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public Long getKey() {
			return key;
		}

		public void setKey(Long key) {
			this.key = key;
		}

		public List<ThresholdSyncBean> getValue() {
			return value;
		}

		public void setValue(List<ThresholdSyncBean> value) {
			this.value = value;
		}

	}
}
