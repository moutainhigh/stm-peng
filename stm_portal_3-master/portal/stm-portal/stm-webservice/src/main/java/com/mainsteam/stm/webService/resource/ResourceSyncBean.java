package com.mainsteam.stm.webService.resource;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceSyncBean")
public class ResourceSyncBean {

	@XmlElement(name = "thresholdData", required = true)
	@XmlJavaTypeAdapter(value = BeanMapAdapter.class)
	private Map<Long,List<ThresholdSyncBean>> thresholdData;

	public Map<Long, List<ThresholdSyncBean>> getThresholdData() {
		return thresholdData;
	}

	public void setThresholdData(Map<Long, List<ThresholdSyncBean>> thresholdData) {
		this.thresholdData = thresholdData;
	}


}
