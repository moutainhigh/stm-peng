package com.mainsteam.stm.plugin.wps;

@SuppressWarnings("serial")
public class PluginException extends RuntimeException {
	
	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable ex) {
		super(ex);
	}
	
	public PluginException(Throwable ex, String reson) {
		super(reson, ex);
	}

}
