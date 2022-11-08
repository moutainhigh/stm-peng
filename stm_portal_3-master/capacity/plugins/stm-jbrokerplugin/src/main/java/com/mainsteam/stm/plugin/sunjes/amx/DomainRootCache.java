package com.mainsteam.stm.plugin.sunjes.amx;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;
import com.sun.appserv.management.DomainRoot;
import com.sun.appserv.management.client.AppserverConnectionSource;

/**
 * jmx根域对象<br>
 * <p>
 * Create on : 2014-1-29<br>
 * <p>
 * </p>
 * <br>
 * 
 * @version sunjes v1.0
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public final class DomainRootCache {

	/**
	 * <code>S_KEY_SPLIT</code> - KEY分隔符
	 */
	private static final String S_KEY_SPLIT = "#";

	/**
	 * <code>cacheMap</code> - 缓存map
	 */
	private static Map<String, DomainRoot> s_cachemap = null;

	/**
	 * <code>S_INSTANCE</code> - 单实例
	 */
	private static DomainRootCache s_instance = new DomainRootCache();

	/**
	 * Constructors.
	 */
	private DomainRootCache() {
		// s_cachemap = new HashMap<String, DomainRoot>();
		setCachemap(new HashMap<String, DomainRoot>());
	}

	/**
	 * 
	 * the getter of cachemap
	 * 
	 * @return s_cachemap
	 */
	public static Map<String, DomainRoot> getCachemap() {
		return s_cachemap;
	}

	/**
	 * 
	 * the setter of cachemap
	 * 
	 * @param cachemap
	 */
	public static void setCachemap(final Map<String, DomainRoot> cachemap) {
		DomainRootCache.s_cachemap = cachemap;
	}

	/**
	 * 实例
	 * 
	 * @return DomainRootCache
	 */
	public static synchronized DomainRootCache getInstance() {
		if (s_instance == null) {
			s_instance = new DomainRootCache();
		}
		return s_instance;
	}

	/**
	 * 获得根实例
	 * 
	 * @param connInfo
	 *            连接信息
	 * @param conn
	 *            连接
	 * @return DomainRoot
	 */
	public DomainRoot getDomainRoot(final SunJESConnInfo connInfo,
			final AppserverConnectionSource conn) {
		synchronized (s_cachemap) {
			if (s_cachemap.containsKey(getKey(connInfo))) {
				return s_cachemap.get(getKey(connInfo));
			}
		}
		DomainRoot t_root = null;
		try {
			t_root = conn.getDomainRoot();
		} catch (Throwable t_e) {
			// S_LOG.error("", t_e);
		}
		if (t_root != null) {
			synchronized (s_cachemap) {
				s_cachemap.put(getKey(connInfo), t_root);
			}
		}
		return s_cachemap.get(getKey(connInfo));
	}

	/**
	 * {method description}.
	 * 
	 * @param host
	 *            主机ip
	 * @param port
	 *            端口
	 * @param usr
	 *            用户名
	 * @param passwd
	 *            密码
	 * @param conn
	 *            连接
	 * @return DomainRoot
	 */
	public DomainRoot getDomainRoot(final String host, final int port,
			final String usr, final String passwd,
			final AppserverConnectionSource conn) {
		synchronized (s_cachemap) {
			if (s_cachemap.containsKey(getKey(host, port, usr, passwd))) {
				return s_cachemap.get(getKey(host, port, usr, passwd));
			}
		}
		DomainRoot t_root = null;
		try {
			t_root = conn.getDomainRoot();
		} catch (Throwable t_e) {
			// S_LOG.error("", t_e);
		}
		if (t_root != null) {
			synchronized (s_cachemap) {
				s_cachemap.put(getKey(host, port, usr, passwd), t_root);
			}
		}
		return s_cachemap.get(getKey(host, port, usr, passwd));
	}

	/**
	 * {method description}.
	 * 
	 * @param host
	 *            主机ip
	 * @param port
	 *            端口
	 * @param user
	 *            用户名
	 * @param passwd
	 *            密码
	 * @return DomainRoot
	 */
	private String getKey(final String host, final int port, final String user,
			final String passwd) {
		StringBuffer t_ss = new StringBuffer();
		t_ss.append(host).append(S_KEY_SPLIT);
		t_ss.append(String.valueOf(port)).append(S_KEY_SPLIT);
		t_ss.append(user).append(S_KEY_SPLIT);
		t_ss.append(passwd);
		return t_ss.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param connInfo
	 *            连接信息
	 * @return String
	 */
	private String getKey(final SunJESConnInfo connInfo) {
		StringBuffer t_ss = new StringBuffer();
		t_ss.append(connInfo.getIp()).append(S_KEY_SPLIT);
		t_ss.append(connInfo.getPort()).append(S_KEY_SPLIT);
		t_ss.append(connInfo.getUserName()).append(S_KEY_SPLIT);
		t_ss.append(connInfo.getPassword());
		return t_ss.toString();
	}

}
