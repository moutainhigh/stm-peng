package com.mainsteam.stm.plugin.was;

import com.mainsteam.stm.plugin.parameter.IBMWebSphereParameter;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * WAS采集插件
 * 
 * @author xiaop_000
 *
 */
public class WASPluginSession extends BaseSession {
	
	public static final String WAS_PLUGIN_IP = "IP";

	public static final String WAS_PLUGIN_PORT = "WASPort";

	public static final String WAS_PLUGIN_USERNAME = "WASUsername";

	public static final String WAS_PLUGIN_PASSWORD = "WASPassword";

	public static final String WAS_PLUGIN_DEPlOYTYPE = "WASDeployType";

	public static final String WAS_PLUGIN_ISSECURITY = "WASIsSecurity";

	public static final String WAS_PLUGIN_APPDMGRIP = "WASAppDmgrIp";

	public static final String WAS_PLUGIN_APPDMGRPORT = "WASAppDmgrPort";
	
	public static final String WAS_PLUGIN_KEYSTOREPATH = "WASKeyStorePath";
	
	public static final String WAS_PLUGIN_TRUSTSTOREPATH = "WASTrustStorePath";
	
	public static final String WAS_PLUGIN_KEYSTOREPASS = "WASKeyStorePassword";
	
	public static final String WAS_PLUGIN_TRUSTSTOREPASS = "WASTrustStorePassword";
	
	private boolean sessionAlive;
	
	@Override
	public void init(PluginInitParameter init) {
		super.getParameter().setIBMWebSphereParameter(new IBMWebSphereParameter());
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case WAS_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case WAS_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_PASSWORD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_DEPlOYTYPE:
				super.getParameter().getIBMWebSphereParameter().setDeployType(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_ISSECURITY:
				super.getParameter().getIBMWebSphereParameter().setIsSecurity(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_APPDMGRIP:
				super.getParameter().getIBMWebSphereParameter().setAppDmgrIp(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_APPDMGRPORT:
				super.getParameter().getIBMWebSphereParameter().setAppDmgrPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case WAS_PLUGIN_KEYSTOREPATH:
				super.getParameter().getIBMWebSphereParameter().setKeyStorePath(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_KEYSTOREPASS:
				super.getParameter().getIBMWebSphereParameter().setKeyStorePassword(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_TRUSTSTOREPATH:
				super.getParameter().getIBMWebSphereParameter().setTrustStorePath(initParameters[i].getValue());
				break;
			case WAS_PLUGIN_TRUSTSTOREPASS:
				super.getParameter().getIBMWebSphereParameter().setTrustStorePassword(initParameters[i].getValue());
				break;
			}
		}
		sessionAlive = true;
	}

	@Override
	public void destory() {
		sessionAlive = false;
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return sessionAlive;
	}

}
