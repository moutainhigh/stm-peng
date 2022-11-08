package com.mainsteam.stm.plugin.wmi;

import com.mainsteam.stm.plugin.wmi.bean.Request;
import com.mainsteam.stm.plugin.wmi.bean.Response;

public interface WMIAgent {
	
	public static final String TYPE_BIO = "bio";
	public static final String TYPE_NIO = "nio";
	public static final String TYPE_SERIAL = "serial";
	
	public Response query(Request request);
	
	public void start();
	
	public void stop();
	
	public boolean isAvailable();
}
