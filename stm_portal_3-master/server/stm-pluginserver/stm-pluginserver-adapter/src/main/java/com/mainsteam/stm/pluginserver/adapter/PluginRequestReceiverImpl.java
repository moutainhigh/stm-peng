/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.PluginContainer;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 *
 */
public class PluginRequestReceiverImpl implements PluginRequestReceiver {

	private PluginContainer container;
	
	
	
	public void setContainer(PluginContainer container) {
		this.container = container;
	}

	/**
	 * 
	 */
	public PluginRequestReceiverImpl() {
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.pluginserver.adapter.PluginRequestReceiver#receivePluginRequest(com.mainsteam.stm.pluginserver.message.PluginRequest)
	 */
	@Override
	public void receivePluginRequest(List<PluginRequest> requests) {
//		for (PluginRequest request : requests) {
//			container.handlePluginRequest(request);
//		}
		container.handlePluginRequest(requests);
	}
}
