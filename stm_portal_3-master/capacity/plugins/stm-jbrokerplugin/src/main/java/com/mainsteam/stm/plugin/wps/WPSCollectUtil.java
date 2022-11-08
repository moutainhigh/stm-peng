package com.mainsteam.stm.plugin.wps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class WPSCollectUtil {
	Log log = LogFactory.getLog(WPSCollectUtil.class);
	public static String getAvailability(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAvailability(connInfo);
	}

	public static String getJVMMEMRate(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJVMMEMRate(connInfo);
	}

	public static String getTotalJvmMemSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getTotalJvmMemSize(connInfo);
	}

	public static String getUsedJvmMemSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getUsedJvmMemSize(connInfo);
	}

	public static String getAvgWaitTime(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAvgWaitTime(connInfo);
	}

	public static String getMaxJvmMemSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getMaxJvmMemSize(connInfo);
	}

	public static String getMinJvmMemSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getMinJvmMemSize(connInfo);
	}

	public static String getNumberOfWebApplication(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getNumberOfWebApplication(connInfo);
	}

	public static String getResDisplayName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getResDisplayName(connInfo);
	}

	public static String getServerName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getServerName(connInfo);
	}

	public static String getSystemUpTime(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getSystemUpTime(connInfo);
	}

	public static String getLdapPort(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getLdapPort(connInfo);
	}

	public static String getLdapIp(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getLdapIp(connInfo);
	}

	public static String getWasVersion(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getWasVersion(connInfo);
	}

	public static String getHome(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getHome(connInfo);
	}

	public static String getCellName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getCellName(connInfo);
	}

	public static String getPid(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPid(connInfo);
	}

	public static String getOnlineUserCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getOnlineUserCount(connInfo);
	}

	public static String getCurConnUserCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getCurConnUserCount(connInfo);
	}

	public static String getJdbcJndiNames(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcJndiNames(connInfo);
	}

	public static String getJdbcMaxUtilRatio(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcMaxUtilRatio(connInfo);
	}

	public static String getJdbcAvgConnWaitTime(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcAvgConnWaitTime(connInfo);
	}

	public static String getJdbcDsId(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcDsId(connInfo);
	}

	public static String getJdbcAvailability(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcAvailability(connInfo);
	}

	public static String getJdbcMaxConnCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcMaxConnCount(connInfo);
	}

	public static String getJdbcProviderName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcProviderName(connInfo);
	}

	public static String getJdbcMinConnCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcMinConnCount(connInfo);
	}

	public static String getJdbcPoolType(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcPoolType(connInfo);
	}

	public static String getJdbcFreePoolSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcFreePoolSize(connInfo);
	}

	public static String getJdbcConnPoolSize(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJdbcConnPoolSize(connInfo);
	}
	
	public static String getJDBCUsedUtilRatio(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getJDBCUsedUtilRatio(connInfo);
	}

	public static String getPortletId(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPortletId(connInfo);
	}

	public static String getPortletName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPortletName(connInfo);
	}

	public static String getPorletAppName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPorletAppName(connInfo);
	}

	public static String getPorletCurErrorCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPorletCurErrorCount(connInfo);
	}

	public static String getPorletResponseTime(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPorletResponseTime(connInfo);
	}

	public static String getPorletCurRequestConut(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getPorletCurRequestConut(connInfo);
	}
	
	public static String getAppId(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAppId(connInfo);
	}

	public static String getAppJ2EEName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAppJ2EEName(connInfo);
	}

	public static String getAppAvailability(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAppAvailability(connInfo);
	}

	public static String getAppActiveSessionCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAppActiveSessionCount(connInfo);
	}

	public static String getAppLiveSessionCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getAppLiveSessionCount(connInfo);
	}

	public static String getWebNonExistSessionCount(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getWebNonExistSessionCount(connInfo);
	}

	public static String getNodeName(JBrokerParameter parameter) {
		WPSConnectionInfo connInfo = getWPSConnectionInfo(parameter);
		return getCollector(connInfo).getNodeName(connInfo);
	}
	
	private static Map<String, WPSCollector> instance = new ConcurrentHashMap<String, WPSCollector>();

	public static WPSCollector getCollector(WPSConnectionInfo connInfo) {
		String key = connInfo.getIp()+":"+connInfo.getAppPort()+"_"+connInfo.getAppUserName()+"_"+connInfo.getAppPassword();
		if(instance.get(key) == null) {
			instance.put(key, new WPSCollector());
		}
		return instance.get(key);
	}

	public static WPSConnectionInfo getWPSConnectionInfo(JBrokerParameter parameter) {
		String ip = parameter.getIp();
		int port =	parameter.getPort();
		String login = parameter.getUsername();
		String password = parameter.getPassword();
		String wasServerType = parameter.getIBMWebSphereParameter().getDeployType().toUpperCase();
		String isSecOn = parameter.getIBMWebSphereParameter().getIsSecurity();
		boolean enableSecurity = isSecOn.equals("1");
		WPSConnectionInfo connInfo = new WPSConnectionInfo(ip, port, login,
				password);
		connInfo.setWasServerType(wasServerType);
		connInfo.setEnableSecurity(enableSecurity);
		
		connInfo.setAppNdIp(parameter.getIBMWebSphereParameter().getAppDmgrIp());
    	connInfo.setAppNdPort(parameter.getIBMWebSphereParameter().getAppDmgrPort());
		
		connInfo.setKeyStore(parameter.getIBMWebSphereParameter().getKeyStorePath());
		connInfo.setTrustStore(parameter.getIBMWebSphereParameter().getTrustStorePath());
		connInfo.setKeyStorePW(parameter.getIBMWebSphereParameter().getKeyStorePassword());
		connInfo.setTrustStorePW(parameter.getIBMWebSphereParameter().getTrustStorePassword());
		
		return connInfo;
	}
	
/*	public WPSCollectUtil() {} 
	
	public int checkKeystore(String keystorePath, String keyPwd) {
    	if(keystorePath==null || keystorePath.trim().equals(""))
    		return 0;
    	
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis = new FileInputStream(keystorePath);
			ks.load(fis, keyPwd.toCharArray());
			return 0;
		} catch (FileNotFoundException e) {
			log.error("Can not find file:" + keystorePath, e);
			e.printStackTrace();
			return AppErrorCode.ERROR_501;
		} catch (IOException e) {
			log.error("Read file error:" + keystorePath, e);
			e.printStackTrace();
			return AppErrorCode.ERROR_502;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return AppErrorCode.ERROR_404;
    }

	public boolean isAppOnline(String ip, Map<String, String> connInfos,
			AppErrorCode retValue) {
		String port = connInfos.get("appPort");
		String login = connInfos.get("appUser");
		String password = connInfos.get("appPassword");
		String wasServerType = connInfos.get("deployType");
		String isSecOn = connInfos.get("isSecurity");
		int appPort = Integer.parseInt(port);
		boolean enableSecurity = isSecOn.equals("1");
		WPSConnectionInfo connInfo = new WPSConnectionInfo(ip, appPort, login,
				password);
		connInfo.setWasServerType(wasServerType);
		connInfo.setEnableSecurity(enableSecurity);
		
		connInfo.setKeyStore(connInfos.get("keyStorePath"));
		connInfo.setKeyStorePW(connInfos.get("keyStorePassword"));
		connInfo.setTrustStore(connInfos.get("trustStorePath"));
		connInfo.setTrustStorePW(connInfos.get("trustStorePassword"));
		
		connInfo.setAppNdIp(connInfos.get("appDmgrIp"));
		int ndPort = Integer.parseInt(connInfos.get("appDmgrPort"));
		connInfo.setAppNdPort(ndPort);
		
		try {
			int keystotre = checkKeystore(connInfo.getKeyStore(), connInfo.getKeyStorePW());
			if(keystotre!=0) {
				retValue.setErrorCode(keystotre);
				return false;
			}
			int truststotre = checkKeystore(connInfo.getTrustStore(), connInfo.getTrustStorePW());
			if(truststotre!=0) {
				retValue.setErrorCode(truststotre);
				return false;
			}
			return getCollector(connInfo).checkConnectInfo(connInfo);
		} catch(Exception ex) {
			if(ex.getMessage()!=null && ex.getMessage().contains("����ƾ֤�����ƾ֤Ϊ��")) {
				retValue.setErrorCode(AppErrorCode.ERROR_401);
			} else {
				retValue.setErrorCode(AppErrorCode.ERROR_404);
			}
			log.error("Connect to websphere potal server failed!", ex);
		}
		return false;
	}*/
}
