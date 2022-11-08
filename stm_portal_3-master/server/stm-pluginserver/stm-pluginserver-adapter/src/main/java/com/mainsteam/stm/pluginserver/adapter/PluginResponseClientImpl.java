/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.List;

import com.mainsteam.stm.pluginserver.PluginContainer;
import com.mainsteam.stm.pluginserver.message.PluginResult;

/**
 * @author ziw
 * 
 */
public class PluginResponseClientImpl implements PluginResponseClient {

	private PluginResponseReceiver responseReceiver;

	public void setResponseReceiver(PluginResponseReceiver responseReceiver) {
		this.responseReceiver = responseReceiver;
	}

	/**
	 * 
	 */
	public PluginResponseClientImpl() {
	}
	

	/**
	 * @param container the container to set
	 */
	public final void setContainer(PluginContainer container) {
		container.setPluginResponseClient(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginserver.PluginResponseClient#sendPluginReponse
	 * (com.mainsteam.stm.pluginserver.message.PluginResult)
	 */
	@Override
	public void sendPluginReponse(List<PluginResult> response) {
		responseReceiver.receivePluginResponse(response);
	}
}
