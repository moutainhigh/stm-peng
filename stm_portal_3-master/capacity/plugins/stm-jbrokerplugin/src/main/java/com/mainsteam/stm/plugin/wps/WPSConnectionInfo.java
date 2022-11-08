package com.mainsteam.stm.plugin.wps;

import java.io.Serializable;
import java.util.Properties;

import com.ibm.websphere.management.AdminClient;
//import com.qwserv.itm.api.ServiceAccess;
/**
 * This is the plugin to the basic connection information
 * <br>
 *  
 * <p>
 * Create on : 2012-4-19<br>
 * <p>
 * </p>
 * <br>
 * @author xujie@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public class WPSConnectionInfo implements Serializable{

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
     * Digital certificate library
     * e.g. DummyClientTrustFile.jks
     */
    private String m_trustStore;
    /**
     * Digital certificate library
     * e.g. DummyClientKeyFile.jks
     */
    private String m_keyStore;
    /**
     * Digital certificate library
     * e.g. "WebAS"
     */
    private String m_trustStorePW;
    /**
     * Digital certificate library
     * e.g. "WebAS"
     */
    private String m_keyStorePW;
    
    private String ip;
    private int appPort;
    private String appUserName;
    private String appPassword;
    
    
//    /**
//     * <code>Client Key File</code> -
//     */
//    private String m_dummyClientKeyFile;
//    
//    /**
//     * <code>Client Trust File</code> -
//     */
//    private String m_dummyClientTrustFile;
    
//    /**
//     * 
//     * {method description}.
//     * @return m_dummyClientKeyFile
//     */
//    public String getDummyClientKeyFile() {
//        return m_dummyClientKeyFile;
//    }
//
//    /**
//     * 
//     * {method description}.
//     * @param dummyClientKeyFile Client Key File
//     */
//    public void setDummyClientKeyFile(final String dummyClientKeyFile) {
//        this.m_dummyClientKeyFile = dummyClientKeyFile;
//    }
//
//    /**
//     * 
//     * {method description}.
//     * @return m_dummyClientTrustFile
//     */
//    public String getDummyClientTrustFile() {
//        return m_dummyClientTrustFile;
//    }
//
//    /**
//     * 
//     * {method description}.
//     * @param dummyClientTrustFile Client Trust File
//     */
//    public void setDummyClientTrustFile(final String dummyClientTrustFile) {
//        this.m_dummyClientTrustFile = dummyClientTrustFile;
//    }

    
    public WPSConnectionInfo(String ip, int appPort, String appUserName,
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
     * @return value
     */
    public boolean isEnableSecurity() {
        return m_enableSecurity;
    }

    /**
     * the setter of security enabled.
     * @param enableSecurity - enable or not.
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
     * @param wasServerType - {parameter description}.
     */
    public void setWasServerType(final String wasServerType) {
        m_wasServerType = wasServerType;
    }

    /**
     * @return appNdIp - {return content description}
     */
    public String getAppNdIp() {
        return m_appNdIp;
    }

    /**
     * @param appNdIp - {parameter description}.
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
     * @param appNdPort - {parameter description}.
     */
    public void setAppNdPort(final int appNdPort) {
        this.m_appNdPort = appNdPort;
    }
    
    /**
     * the getter of Digital certificate library.
     * @return value
     */
    public String getTrustStore() {
        return m_trustStore;
    }

    /**
     * the setter of Digital certificate library 
     * @param trustStore - value
     */
    public void setTrustStore(final String trustStore) {
        this.m_trustStore = trustStore;
    }

    /**
     * the getter of Digital certificate library.
     * @return value
     */
    public String getKeyStore() {
        return m_keyStore;
    }

    /**
     * the setter of Digital certificate library 
     * @param keyStore - value
     */
    public void setKeyStore(final String keyStore) {
        this.m_keyStore = keyStore;
    }

    /**
     * the getter of Digital certificate library.
     * @return value
     */
    public String getTrustStorePW() {
        return m_trustStorePW;
    }

    /**
     * the setter of Digital certificate library 
     * @param trustStorePW - value
     */
    public void setTrustStorePW(final String trustStorePW) {
        this.m_trustStorePW = trustStorePW;
    }

    /**
     * the getter of Digital certificate library.
     * @return value
     */
    public String getKeyStorePW() {
        return m_keyStorePW;
    }

    /**
     * the setter of Digital certificate library 
     * @param keyStorePW - value
     */
    public void setKeyStorePW(final String keyStorePW) {
        this.m_keyStorePW = keyStorePW;
    }
    
//    public String getDummyKeyStorePath() {
//    	return ServiceAccess.getSystemSupportService().getHomeDir() + "/deploy/pal/itm-pal-appmdl.par/";
//    }
    
    
    /**
     * convert connection information to properties.
     * @return result
     */
    public Properties toBaseProperties() {
        
        Properties t_prop = new Properties();

        t_prop.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        t_prop.setProperty(AdminClient.CONNECTOR_HOST, getIp());
        t_prop.setProperty(AdminClient.CONNECTOR_PORT, Integer.toString(getAppPort()));
        if(getAppUserName()!=null && getAppPassword().length() != 0) {
        t_prop.setProperty(AdminClient.USERNAME, getAppUserName());
        t_prop.setProperty(AdminClient.PASSWORD, getAppPassword());
        }
        if (isEnableSecurity()) {
            t_prop.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");

            t_prop.setProperty("javax.net.ssl.trustStore", m_trustStore);
            t_prop.setProperty("javax.net.ssl.keyStore", m_keyStore);
            t_prop.setProperty("javax.net.ssl.trustStorePassword", m_trustStorePW);
            t_prop.setProperty("javax.net.ssl.keyStorePassword", m_keyStorePW);
            
            
//            t_prop.put("javax.net.ssl.keyStore",
//                    "F:\\OC4\\WebSphereSecurity\\DummyClientKeyFile.jks");
//            t_prop.put("javax.net.ssl.trustStore",
//                    "F:\\OC4\\WebSphereSecurity\\DummyClientTrustFile.jks");
            
//            t_prop.put("javax.net.ssl.keyStore",getDummyKeyStorePath() + "DummyClientKeyFile.jks");
//            t_prop.put("javax.net.ssl.trustStore",getDummyKeyStorePath() + "DummyClientTrustFile.jks");
            
//            t_prop.put("javax.net.ssl.keyStorePassword", "WebAS");
//            t_prop.put("javax.net.ssl.trustStorePassword", "WebAS");
        }
        
        return t_prop;
    }
    
    /**
     * convert connection information to properties.
     * @return result
     */
    public Properties toNDProperties() {
        
        Properties t_prop = new Properties();

        t_prop.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
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
            t_prop.setProperty("javax.net.ssl.trustStorePassword", m_trustStorePW);
            t_prop.setProperty("javax.net.ssl.keyStorePassword", m_keyStorePW);
            
//            t_prop.put("javax.net.ssl.keyStore", "E:\\DummyFile\\was6.0\\172.17.160.17\\DummyClientKeyFile.jks");
//            t_prop.put("javax.net.ssl.trustStore",
//                    "E:\\DummyFile\\was6.0\\172.17.160.17\\DummyClientTrustFile.jks");
            
//            t_prop.put("javax.net.ssl.keyStore",getDummyKeyStorePath() + "DummyClientKeyFile.jks");
//            t_prop.put("javax.net.ssl.trustStore",getDummyKeyStorePath() + "DummyClientTrustFile.jks");
            
            t_prop.put("javax.net.ssl.keyStorePassword", "WebAS");
            t_prop.put("javax.net.ssl.trustStorePassword", "WebAS");
        }
        
        return t_prop;
    }
    
    
    
}
