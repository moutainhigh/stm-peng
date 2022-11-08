package com.mainsteam.stm.plugin.wps;

import com.mainsteam.stm.plugin.parameter.IBMWebSphereParameter;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * WPS采集插件
 * @author xiaop_000
 *
 */
public class WPSPluginSession extends BaseSession {
	
	public static final String WPS_PLUGIN_IP = "IP";

	public static final String WPS_PLUGIN_PORT = "WPSPort";

	public static final String WPS_PLUGIN_USERNAME = "WPSUsername";

	public static final String WPS_PLUGIN_PASSWORD = "WPSPassword";

	public static final String WPS_PLUGIN_DEPlOYTYPE = "WPSDeployType";

	public static final String WPS_PLUGIN_ISSECURITY = "WPSIsSecurity";

	public static final String WPS_PLUGIN_APPDMGRIP = "WPSAppDmgrIp";

	public static final String WPS_PLUGIN_APPDMGRPORT = "WPSAppDmgrPort";
	
	public static final String WPS_PLUGIN_KEYSTOREPATH = "WPSKeyStorePath";
	
	public static final String WPS_PLUGIN_TRUSTSTOREPATH = "WPSTrustStorePath";
	
	public static final String WPS_PLUGIN_KEYSTOREPASS = "WPSKeyStorePassword";
	
	public static final String WPS_PLUGIN_TRUSTSTOREPASS = "WPSTrustStorePassword";

	@Override
	public void init(PluginInitParameter init) {
		super.getParameter().setIBMWebSphereParameter(new IBMWebSphereParameter());
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case WPS_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case WPS_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_PASSWORD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_DEPlOYTYPE:
				super.getParameter().getIBMWebSphereParameter().setDeployType(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_ISSECURITY:
				super.getParameter().getIBMWebSphereParameter().setIsSecurity(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_APPDMGRIP:
				super.getParameter().getIBMWebSphereParameter().setAppDmgrIp(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_APPDMGRPORT:
				super.getParameter().getIBMWebSphereParameter().setAppDmgrPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case WPS_PLUGIN_KEYSTOREPATH:
				super.getParameter().getIBMWebSphereParameter().setKeyStorePath(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_KEYSTOREPASS:
				super.getParameter().getIBMWebSphereParameter().setKeyStorePassword(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_TRUSTSTOREPATH:
				super.getParameter().getIBMWebSphereParameter().setTrustStorePath(initParameters[i].getValue());
				break;
			case WPS_PLUGIN_TRUSTSTOREPASS:
				super.getParameter().getIBMWebSphereParameter().setTrustStorePassword(initParameters[i].getValue());
				break;
			}
		}
	}

	@Override
	public void destory() {
	}

	@Override
	public void reload() {

	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
