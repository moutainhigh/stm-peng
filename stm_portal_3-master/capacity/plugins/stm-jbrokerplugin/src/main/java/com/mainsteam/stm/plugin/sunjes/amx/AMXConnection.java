package com.mainsteam.stm.plugin.sunjes.amx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;
import com.sun.appserv.management.client.AppserverConnectionSource;
import com.sun.appserv.management.client.TLSParams;
import com.sun.appserv.management.client.TrustAnyTrustManager;

/**
 * 使用SUN JMX 接口 AMX连接类<br>
 */
public class AMXConnection {

	private static final Log logger = LogFactory.getLog(AMXConnection.class);
	/**
	 * <code>instance</code> - 连接实例
	 */
	private static AMXConnection s_instance = new AMXConnection();

	/**
	 * Constructors.
	 */
	public AMXConnection() {
		// cacheMap = new HashMap<String, AppserverConnectionSource>();
	}

	/**
	 * 返回单例
	 * 
	 * @return AMXConnection
	 */
	public static synchronized AMXConnection getInstance() {
		if (s_instance == null) {
			s_instance = new AMXConnection();
		}
		return s_instance;
	}

	/**
	 * getConnectionSource
	 * 
	 * @param connInfo
	 *            -连接信息
	 * @return AppserverConnectionSource @
	 */
	public AppserverConnectionSource getConnectionSource(
			final SunJESConnInfo connInfo) {

		String t_host = connInfo.getIp();
		int t_port = connInfo.getPort();
		String t_user = connInfo.getUserName();
		String t_passwd = connInfo.getPassword();

		return getConnection(t_host, t_port, t_user, t_passwd);
	}

	/**
	 * 获取连接对象
	 * 
	 * @param host
	 *            host
	 * @param port
	 *            port
	 * @param user
	 *            user
	 * @param passwd
	 *            passwd
	 * @return AppserverConnectionSource @
	 */
	public AppserverConnectionSource getConnectionSource(final String host,
			final int port, final String user, final String passwd) {
		return getConnection(host, port, user, passwd);
	}

	/**
	 * 获取连接对象
	 * 
	 * @param host
	 *            
	 * @param port
	 *            
	 * @param user
	 *            
	 * @param passwd
	 *            
	 * @return AppserverConnectionSource @
	 */
	private AppserverConnectionSource getConnection(final String host,
			final int port, final String user, final String passwd) {
		AppserverConnectionSource t_connectionSource = null;
		try {
			t_connectionSource = getConnectionNonTLS(host, port, user, passwd);
			t_connectionSource.getDomainRoot();
			t_connectionSource.getMBeanServerConnection(false);
		} catch (Throwable t_e) {
			logger.error(t_e.getMessage(), t_e);
			try {
				t_connectionSource = getConnectionWithTLS(host, port, user,
						passwd);
				t_connectionSource.getMBeanServerConnection(false);
			} catch (Throwable t_e1) {
				logger.error(t_e1.getMessage(), t_e1);
			}
		}

		return t_connectionSource;
	}

	/**
	 * 获取连接对象
	 * 
	 * @param host
	 *            
	 * @param port
	 *            
	 * @param user
	 *            
	 * @param passwd
	 *           
	 * @return AppserverConnectionSource @
	 */
	private AppserverConnectionSource getConnectionNonTLS(final String host,
			final int port, final String user, final String passwd) {
		try {
			return new AppserverConnectionSource(
					AppserverConnectionSource.PROTOCOL_RMI, host, port, user,
					passwd, null, null);
		} catch (Throwable t_e) {
			logger.error(t_e.getMessage(), t_e);
			return null;
		}
	}

	/**
	 * 获取连接对象
	 * 
	 * @param host
	 *            host
	 * @param port
	 *            port
	 * @param user
	 *            user
	 * @param passwd
	 *            passwd
	 * @return AppserverConnectionSource @
	 */
	private AppserverConnectionSource getConnectionWithTLS(final String host,
			final int port, final String user, final String passwd) {
		try {
			TLSParams t_tlsParams = new TLSParams(
					TrustAnyTrustManager.getInstanceArray(), null);
			return new AppserverConnectionSource(
					AppserverConnectionSource.PROTOCOL_RMI, host, port, user,
					passwd, t_tlsParams, null);
		} catch (Throwable t_e) {
			logger.error(t_e.getMessage(), t_e);
			return null;
		}
	}

}
