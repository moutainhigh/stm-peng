package com.mainsteam.stm.state.sync.processer;

import java.util.List;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.state.obj.InstanceStateData;

public class InstanceStateCatch implements InstanceStateCatchMBean {

	private InstanceStateService instanceStateService;
	
	@Override
	public List<InstanceStateData> catchState() {
		return instanceStateService.getAllInstanceState();
	}

	public void setInstanceStateService(InstanceStateService instanceStateService) {
		this.instanceStateService = instanceStateService;
	}

}
