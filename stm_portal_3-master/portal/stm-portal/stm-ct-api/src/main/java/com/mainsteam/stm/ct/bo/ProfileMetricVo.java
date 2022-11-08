package com.mainsteam.stm.ct.bo;

import java.util.List;

public class ProfileMetricVo {
    private String profilelibId;

    private List<MsMetricTemplates> metricTemplates;

	public String getProfilelibId() {
		return profilelibId;
	}

	public void setProfilelibId(String profilelibId) {
		this.profilelibId = profilelibId;
	}

	public List<MsMetricTemplates> getMetricTemplates() {
		return metricTemplates;
	}

	public void setMetricTemplates(List<MsMetricTemplates> metricTemplates) {
		this.metricTemplates = metricTemplates;
	}

	
    
}
