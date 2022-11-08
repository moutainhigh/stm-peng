package com.mainsteam.stm.plugin.was;

import java.util.Properties;

import com.ibm.websphere.management.AdminClient;

/**
 * This is the plugin to the basic connection information <br>
 * 
 * <p>
 * Create on : 2012-4-19<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author xujie@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps v1.0
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public class WasConnInfo implements java.io.Serializable {

	/**
	 * <code>serialVersionUID</code> - {description}.
	 */
	private static final long serialVersionUID = -5551819457389044650L;

	/**
	 * <code>enable security</code> - Whether opening security value: true|false
	 */
	private boolean m_enableSecurity;

	/**
	 * <code>WAS Server Type</code> - About tye type of WebSphere value: ND|Base
	 */
	private String m_wasServerType;

	/**
	 * <code>WAS ND IP</code> - the IP address of network deployment service.
	 */
	private String m_appNdIp;

	/**
	 * <code>WAS ND Soap Port</code> - the port of network deployment service.
	 */
	private int m_appNdPort;

	/**
	 * Digital certificate library e.g. DummyClientTrustFile.jks
	 */
	private String m_trustStore;// =
								// ServiceAccess.getSystemSupportService().getHomeDir()
								// +
	// "/deploy/pal/itm-pal-appmdl.par/DummyClientTrustFile.jks";
	/**
	 * Digital certificate library e.g. DummyClientKeyFile.jks
	 */
	private String m_keyStore;// =
								// ServiceAccess.getSystemSupportService().getHomeDir()
								// +
	// "/deploy/pal/itm-pal-appmdl.par/DummyClientKeyFile.jks";;
	/**
	 * Digital certificate library e.g. "WebAS"
	 */
	private String m_trustStorePW = "WebAS";
	/**
	 * Digital certificate library e.g. "WebAS"
	 */
	private String m_keyStorePW = "WebAS";

	private String ip;
	private int appPort;
	private String appUserName;
	private String appPassword;

	// /**
	// * <code>Client Key File</code> -
	// */
	// private String m_dummyClientKeyFile;
	//
	// /**
	// * <code>Client Trust File</code> -
	// */
	// private String m_dummyClientTrustFile;

	// /**
	// *
	// * {method description}.
	// * @return m_dummyClientKeyFile
	// */
	// public String getDummyClientKeyFile() {
	// return m_dummyClientKeyFile;
	// }
	//
	// /**
	// *
	// * {method description}.
	// * @param dummyClientKeyFile Client Key File
	// */
	// public void setDummyClientKeyFile(final String dummyClientKeyFile) {
	// this.m_dummyClientKeyFile = dummyClientKeyFile;
	// }
	//
	// /**
	// *
	// * {method description}.
	// * @return m_dummyClientTrustFile
	// */
	// public String getDummyClientTrustFile() {
	// return m_dummyClientTrustFile;
	// }
	//
	// /**
	// *
	// * {method description}.
	// * @param dummyClientTrustFile Client Trust File
	// */
	// public void setDummyClientTrustFile(final String dummyClientTrustFile) {
	// this.m_dummyClientTrustFile = dummyClientTrustFile;
	// }

	public WasConnInfo(String ip, int appPort, String appUserName,
			String appPassword) {
		super();
		this.ip = ip;
		this.appPort = appPort;
		this.appUserName = appUserName;
		this.appPassword = appPassword;
	}

	public String getIp() {
		return ip;
	}

	public int getAppPort() {
		return appPort;
	}

	public String getAppUserName() {
		return appUserName;
	}

	public String getAppPassword() {
		return appPassword;
	}

	/**
	 * the getter of security enabled.
	 * 
	 * @return value
	 */
	public boolean isEnableSecurity() {
		return m_enableSecurity;
	}

	/**
	 * the setter of security enabled.
	 * 
	 * @param enableSecurity
	 *            - enable or not.
	 */
	public void setEnableSecurity(final boolean enableSecurity) {
		this.m_enableSecurity = enableSecurity;
	}

	/**
	 * @return wasServerType - {return content description}
	 */
	public String getWasServerType() {
		return m_wasServerType;
	}

	/**
	 * @param wasServerType
	 *            - {parameter description}.
	 */
	public void setWasServerType(String wasServerType) {
		if ("NetworkDeployment".equalsIgnoreCase(wasServerType)) {
			wasServerType = "ND";
		}
		m_wasServerType = wasServerType;
	}

	/**
	 * @return appNdIp - {return content description}
	 */
	public String getAppNdIp() {
		return m_appNdIp;
	}

	/**
	 * @param appNdIp
	 *            - {parameter description}.
	 */
	public void setAppNdIp(final String appNdIp) {
		this.m_appNdIp = appNdIp;
	}

	/**
	 * @return appNdPort - {return content description}
	 */
	public int getAppNdPort() {
		return m_appNdPort;
	}

	/**
	 * @param appNdPort
	 *            - {parameter description}.
	 */
	public void setAppNdPort(final int appNdPort) {
		this.m_appNdPort = appNdPort;
	}

	/**
	 * the getter of Digital certificate library.
	 * 
	 * @return value
	 */
	public String getTrustStore() {
		return m_trustStore;
	}

	/**
	 * the setter of Digital certificate library
	 * 
	 * @param trustStore
	 *            - value
	 */
	public void setTrustStore(final String trustStore) {
		this.m_trustStore = trustStore;
	}

	/**
	 * the getter of Digital certificate library.
	 * 
	 * @return value
	 */
	public String getKeyStore() {
		return m_keyStore;
	}

	/**
	 * the setter of Digital certificate library
	 * 
	 * @param keyStore
	 *            - value
	 */
	public void setKeyStore(final String keyStore) {
		this.m_keyStore = keyStore;
	}

	/**
	 * the getter of Digital certificate library.
	 * 
	 * @return value
	 */
	public String getTrustStorePW() {
		return m_trustStorePW;
	}

	/**
	 * the setter of Digital certificate library
	 * 
	 * @param trustStorePW
	 *            - value
	 */
	public void setTrustStorePW(final String trustStorePW) {
		this.m_trustStorePW = trustStorePW;
	}

	/**
	 * the getter of Digital certificate library.
	 * 
	 * @return value
	 */
	public String getKeyStorePW() {
		return m_keyStorePW;
	}

	/**
	 * the setter of Digital certificate library
	 * 
	 * @param keyStorePW
	 *            - value
	 */
	public void setKeyStorePW(final String keyStorePW) {
		this.m_keyStorePW = keyStorePW;
	}

	// public String getDummyKeyStorePath() {
	// return ServiceAccess.getSystemSupportService().getHomeDir() +
	// "/deploy/pal/itm-pal-appmdl.par/";
	// }

	/**
	 * convert connection information to properties.
	 * 
	 * @return result
	 */
	public Properties toBaseProperties() {

		Properties t_prop = new Properties();

		t_prop.setProperty(AdminClient.CONNECTOR_TYPE,
				AdminClient.CONNECTOR_TYPE_SOAP);
		t_prop.setProperty(AdminClient.CONNECTOR_HOST, getIp());
		t_prop.setProperty(AdminClient.CONNECTOR_PORT,
				Integer.toString(getAppPort()));
		if (getAppUserName() != null && getAppPassword().length() != 0) {
			t_prop.setProperty(AdminClient.USERNAME, getAppUserName());
			t_prop.setProperty(AdminClient.PASSWORD, getAppPassword());
		}
		if (isEnableSecurity()) {
			t_prop.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");

			t_prop.setProperty("javax.net.ssl.trustStore", m_trustStore);
			t_prop.setProperty("javax.net.ssl.keyStore", m_keyStore);
			t_prop.setProperty("javax.net.ssl.trustStorePassword",
					m_trustStorePW);
			t_prop.setProperty("javax.net.ssl.keyStorePassword", m_keyStorePW);

			// t_prop.put("javax.net.ssl.keyStore",
			// "D:\\DummyClientKeyFile.jks");
			// t_prop.put("javax.net.ssl.trustStore",
			// "D:\\DummyClientTrustFile.jks");

			// t_prop.put("javax.net.ssl.keyStore",getDummyKeyStorePath() +
			// "DummyClientKeyFile.jks");
			// t_prop.put("javax.net.ssl.trustStore",getDummyKeyStorePath() +
			// "DummyClientTrustFile.jks");

			// t_prop.put("javax.net.ssl.keyStorePassword", "WebAS");
			// t_prop.put("javax.net.ssl.trustStorePassword", "WebAS");
		}

		return t_prop;
	}

	/**
	 * convert connection information to properties.
	 * 
	 * @return result
	 */
	public Properties toNDProperties() {

		Properties t_prop = new Properties();

		t_prop.setProperty(AdminClient.CONNECTOR_TYPE,
				AdminClient.CONNECTOR_TYPE_SOAP);
		String t_ip = getAppNdIp();
		if (t_ip == null) {
			t_ip = getIp();
		}
		t_prop.setProperty(AdminClient.CONNECTOR_HOST, t_ip);

		int t_port = getAppNdPort();
		if (t_port <= 0) {
			t_port = getAppPort();
		}
		t_prop.setProperty(AdminClient.CONNECTOR_PORT, Integer.toString(t_port));

		t_prop.setProperty(AdminClient.USERNAME, getAppUserName());
		t_prop.setProperty(AdminClient.PASSWORD, getAppPassword());
		if (isEnableSecurity()) {
			t_prop.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");

			t_prop.setProperty("javax.net.ssl.trustStore", m_trustStore);
			t_prop.setProperty("javax.net.ssl.keyStore", m_keyStore);
			t_prop.setProperty("javax.net.ssl.trustStorePassword",
					m_trustStorePW);
			t_prop.setProperty("javax.net.ssl.keyStorePassword", m_keyStorePW);

			// t_prop.put("javax.net.ssl.keyStore",
			// "E:\\DummyFile\\was6.0\\172.17.160.17\\DummyClientKeyFile.jks");
			// t_prop.put("javax.net.ssl.trustStore",
			// "E:\\DummyFile\\was6.0\\172.17.160.17\\DummyClientTrustFile.jks");

			// t_prop.put("javax.net.ssl.keyStore",getDummyKeyStorePath() +
			// "DummyClientKeyFile.jks");
			// t_prop.put("javax.net.ssl.trustStore",getDummyKeyStorePath() +
			// "DummyClientTrustFile.jks");
			// t_prop.put("javax.net.ssl.keyStorePassword", "WebAS");
			// t_prop.put("javax.net.ssl.trustStorePassword", "WebAS");
		}

		return t_prop;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appPassword == null) ? 0 : appPassword.hashCode());
		result = prime * result + appPort;
		result = prime * result
				+ ((appUserName == null) ? 0 : appUserName.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result
				+ ((m_appNdIp == null) ? 0 : m_appNdIp.hashCode());
		result = prime * result + m_appNdPort;
		result = prime * result + (m_enableSecurity ? 1231 : 1237);
		result = prime * result
				+ ((m_keyStore == null) ? 0 : m_keyStore.hashCode());
		result = prime * result
				+ ((m_keyStorePW == null) ? 0 : m_keyStorePW.hashCode());
		result = prime * result
				+ ((m_trustStore == null) ? 0 : m_trustStore.hashCode());
		result = prime * result
				+ ((m_trustStorePW == null) ? 0 : m_trustStorePW.hashCode());
		result = prime * result
				+ ((m_wasServerType == null) ? 0 : m_wasServerType.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WasConnInfo other = (WasConnInfo) obj;
		if (appPassword == null) {
			if (other.appPassword != null)
				return false;
		} else if (!appPassword.equals(other.appPassword))
			return false;
		if (appPort != other.appPort)
			return false;
		if (appUserName == null) {
			if (other.appUserName != null)
				return false;
		} else if (!appUserName.equals(other.appUserName))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (m_appNdIp == null) {
			if (other.m_appNdIp != null)
				return false;
		} else if (!m_appNdIp.equals(other.m_appNdIp))
			return false;
		if (m_appNdPort != other.m_appNdPort)
			return false;
		if (m_enableSecurity != other.m_enableSecurity)
			return false;
		if (m_keyStore == null) {
			if (other.m_keyStore != null)
				return false;
		} else if (!m_keyStore.equals(other.m_keyStore))
			return false;
		if (m_keyStorePW == null) {
			if (other.m_keyStorePW != null)
				return false;
		} else if (!m_keyStorePW.equals(other.m_keyStorePW))
			return false;
		if (m_trustStore == null) {
			if (other.m_trustStore != null)
				return false;
		} else if (!m_trustStore.equals(other.m_trustStore))
			return false;
		if (m_trustStorePW == null) {
			if (other.m_trustStorePW != null)
				return false;
		} else if (!m_trustStorePW.equals(other.m_trustStorePW))
			return false;
		if (m_wasServerType == null) {
			if (other.m_wasServerType != null)
				return false;
		} else if (!m_wasServerType.equals(other.m_wasServerType))
			return false;
		return true;
	}

	public String toString() {
		return "WasConnInfo [m_enableSecurity=" + m_enableSecurity
				+ ", m_wasServerType=" + m_wasServerType + ", m_appNdIp="
				+ m_appNdIp + ", m_appNdPort=" + m_appNdPort
				+ ", m_trustStore=" + m_trustStore + ", m_keyStore="
				+ m_keyStore + ", m_trustStorePW=" + m_trustStorePW
				+ ", m_keyStorePW=" + m_keyStorePW + ", ip=" + ip
				+ ", appPort=" + appPort + ", appUserName=" + appUserName
				+ ", appPassword=" + appPassword + "]";
	}

}
