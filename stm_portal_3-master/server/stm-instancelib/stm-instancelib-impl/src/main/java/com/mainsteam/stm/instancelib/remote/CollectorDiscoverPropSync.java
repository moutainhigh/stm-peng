package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;

public class CollectorDiscoverPropSync implements
		CollectorDiscoverPropSyncMBean {

	private static final Log logger = LogFactory.getLog(CollectorDiscoverPropSync.class);

	private DiscoverPropService discoverPropService;
	
	public void start() {
		System.out.println("collector discoverporp Mben has start!");
	}
	
	@Override
	public void updateDiscoverPropSyncToCollector(
			List<DiscoverProp> discoverProps) throws Exception {
		if(logger.isTraceEnabled()){
			logger.trace("updateDiscoverPropSyncToCollector start");
		}
		discoverPropService.updateProps(discoverProps);
		if(logger.isTraceEnabled()){
			logger.trace("updateDiscoverPropSyncToCollector end");
		}
	}

	public void setDiscoverPropService(
			DiscoverPropService discoverPropService) {
		this.discoverPropService = discoverPropService;
	}
}
