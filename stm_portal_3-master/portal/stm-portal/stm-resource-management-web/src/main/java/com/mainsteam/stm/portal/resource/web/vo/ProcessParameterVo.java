package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;

public class ProcessParameterVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911393371479856954L;
	
	private String processList;
	
	private long mainInstanceId;
	
	private int type;

	public String getProcessList() {
		return processList;
	}

	public void setProcessList(String processList) {
		this.processList = processList;
	}

	public long getMainInstanceId() {
		return mainInstanceId;
	}

	public void setMainInstanceId(long mainInstanceId) {
		this.mainInstanceId = mainInstanceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
