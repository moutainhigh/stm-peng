package com.mainsteam.stm.plugin.sunjes.amx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;
import com.sun.appserv.management.DomainRoot;
import com.sun.appserv.management.client.AppserverConnectionSource;
import com.sun.appserv.management.config.ConfigDottedNames;
import com.sun.appserv.management.config.WebModuleConfig;
import com.sun.appserv.management.j2ee.J2EEDomain;
import com.sun.appserv.management.j2ee.J2EEServer;
import com.sun.appserv.management.monitor.MonitoringDottedNames;
import com.sun.appserv.management.monitor.MonitoringRoot;
import com.sun.appserv.management.monitor.ServerRootMonitor;

/**
 * SUN JES AMX协议的封装
 */
public class AMXManager {

    private static final String SUN_APPSERV_TYPE_ENGINE = "com.sun.appserv:type=Engine";

	/**
     * <code>S_AMX_GLOBAL_CACHE</code> - 緩存
     */
    public static final Map<String, Object> S_AMX_GLOBAL_CACHE = Collections
            .synchronizedMap(new HashMap<String, Object>());

    /**
     * <code>S_JES_ALIVE_KEY</code> - 活动标示
     */
    public static final String S_JES_ALIVE_KEY = "GLOBAL_KEY_JES_ALIVE";

    /**
     * <code>S_JES_CONNSOURCE_KEY</code> - 连接表示
     */
    public static final String S_JES_CONNSOURCE_KEY = "GLOBAL_KEY_JES_CONNSOURCE";

    /**
     * <code>S_KEY_SPLIT</code> - KEY字符串分隔符
     */
    private static final String S_KEY_SPLIT = "_";
    /**
     * <code>connectionSource</code> - 连接器
     */
    private AppserverConnectionSource m_connectionSource = null;

    /**
     * <code>m_domainRoot</code> - {description}.
     */
    private DomainRoot m_domainRoot = null;

    /**
     * <code>m_connInfo</code> - {m_connInfo}.
     */
//    private SunJESConnInfo m_connInfo = null;

    /**
     * <code>m_host</code> - {m_host}.
     */
    private String m_host = "";

    /**
     * <code>m_port</code> - {m_port}.
     */
    private int m_port;

    /**
     * <code>m_user</code> - {m_user}.
     */
    private String m_user = "";

    /**
     * <code>m_passwd</code> - {m_passwd}.
     */
    private String m_passwd = "";

    /**
     * <code>m_instanceName</code> - {m_instanceName}.
     */
    private String m_instanceName = null;

    /**
     * Constructors.
     * @param connInfo SunJESConnInfo
     */
    public AMXManager(final SunJESConnInfo connInfo) {
//        this.m_connInfo = connInfo;

        this.m_host = connInfo.getIp();
        this.m_port = connInfo.getPort();
        this.m_user = connInfo.getUserName();
        this.m_passwd = connInfo.getPassword();
        this.m_instanceName = connInfo.getInstanceName();
        getConnection();
    }

    /**
     * Constructors.
     * @param host ip
     * @param port 端口
     * @param usr 用户名
     * @param passwd 密码
     * @param instance 实例名
     */
    public AMXManager(final String host, final int port, final String usr, final String passwd, final String instance) {
        this.m_host = host;
        this.m_port = port;
        this.m_user = usr;
        this.m_passwd = passwd;
        this.m_instanceName = instance;

        getConnection();

    }

    // ====================================================================
    // AMX连接相关的方法。避免起许多线程及当资源不可用后无法重新连接
    // ====================================================================

    /**
     * 检查连接是否可用
     * @return 可用true，不可以false
     */
    public boolean checkAlive() {
        boolean t_result = false;
        try {
            Object t_sv = this.getAppVersion();
            if (null != t_sv) {
                t_result = true;
            } else {
                t_result = false;
            }
        } catch (Throwable t_e) {
            t_result = false;
        }
        return t_result;
    }

    /**
     * 初始化连接
     * @throws Exception Exception
     */
    public void initConnection() throws Exception {
        try {
            this.m_connectionSource = getConnectionSource(this.m_host, this.m_port, this.m_user, this.m_passwd);
            this.m_domainRoot = this.m_connectionSource.getDomainRoot();
        } catch (Throwable t_e) {
        	t_e.printStackTrace();
        }
    }

    /**
     * 获取连接
     */
    public void getConnection() {
     // 如果上次连接正常
        if (isLasttimeJesAlive()) {
            try {
                this.m_connectionSource = getConnSource();
                this.m_domainRoot = this.m_connectionSource.getDomainRoot();
            } catch (Throwable t_e1) {
                try {
                    this.initConnection();
                    this.setConnSource(this.m_connectionSource);
                    this.setIsCurrentJesAlive(true);
                } catch (Throwable t_e2) {
                 // 本次连接错误
                    this.setIsCurrentJesAlive(false);
                    this.setConnSource(null);
                }
            }
        } else {
            try {
                this.initConnection();
                this.setConnSource(this.m_connectionSource);
                this.setIsCurrentJesAlive(true);
            } catch (Throwable t_e) {
                this.setIsCurrentJesAlive(false);
                this.setConnSource(null);
            }
        }
    }

    /**
     * 实例key
     * @return String
     */
    private String getInstanceKey() {
        return S_KEY_SPLIT + this.m_host + S_KEY_SPLIT + this.m_port + S_KEY_SPLIT + this.m_instanceName;
    }

    /**
     * 最后一次是否存活
     * @return boolean
     */
    private boolean isLasttimeJesAlive() {
        Object t_obj = S_AMX_GLOBAL_CACHE.get(S_JES_ALIVE_KEY + getInstanceKey());
        if (t_obj == null)
            return false;
        else return ((Boolean) t_obj).booleanValue();
    }

    /**
     * 设置当前是否可用
     * @param b 可用true
     */
    private void setIsCurrentJesAlive(final boolean b) {
        boolean t_avail = b;
        S_AMX_GLOBAL_CACHE.put(S_JES_ALIVE_KEY + getInstanceKey(), t_avail);
    }

    /**
     * 设置连接
     * @param conn 连接对象
     */
    private void setConnSource(final AppserverConnectionSource conn) {
        S_AMX_GLOBAL_CACHE.put(S_JES_CONNSOURCE_KEY + getInstanceKey(), conn);
    }

    /**
     * 获取连接
     * @return 连接对象
     */
    private AppserverConnectionSource getConnSource() {
        return (AppserverConnectionSource) S_AMX_GLOBAL_CACHE.get(S_JES_CONNSOURCE_KEY + getInstanceKey());
    }

    // ====================================================================
    // AMX Root相关
    // ====================================================================

    /**
     * AMX MonitorRoot
     * 
     * @return MonitoringRoot
     */
    public MonitoringRoot getMonitoringRoot() {
        if (m_domainRoot != null)
            return m_domainRoot.getMonitoringRoot();
        return null;
    }
    
    /**
     * AMX {@link DomainRoot}
     * @return DomainRoot
     */
    public DomainRoot getDomainRoot() {
    	return m_domainRoot;
    }

    /**
     * AMX ServerRootMonitor
     * 
     * @return ServerRootMonitor
     */
    public ServerRootMonitor getServerRootMonitor() {
        if (getMonitoringRoot() != null) {
            return (ServerRootMonitor) getMonitoringRoot().getServerRootMonitorMap().get(this.m_instanceName);
        }
        return null;
    }

    /**
     * 获取JES版本
     * 
     * @return Object
     * @ 
     */
    public Object getAppVersion()  {
        JMXConnector t_connector = null;
        try {
            J2EEDomain domain = m_domainRoot.getJ2EEDomain();
            J2EEServer server = (J2EEServer) domain.getServerMap().get(this.m_instanceName);
            return server.getserverVersion();
        } catch (Throwable t_e) {
        	return "";
        } finally {
            try {
                if(null != t_connector){
                    t_connector.close();
                }
            } catch (IOException t_e) {
            }
        }
    }

    // ====================================================================
    // 获取相应子资源下的所有资源名称
    // ====================================================================

    /**
     * {method description}.
     * @return List
     */
    public List<String> getConnectorConnectionPoolNames() {
        List<String> t_result = null;
        if (getServerRootMonitor() != null) {
        	@SuppressWarnings("unchecked")
			Map<String,Object> t_map = getServerRootMonitor().getConnectorConnectionPoolMonitorMap();
            if (t_map != null && t_map.size() > 0) {
                t_result = new ArrayList<String>();
                Iterator<String> t_it = t_map.keySet().iterator();
                while (t_it.hasNext())
                    t_result.add(String.valueOf(t_it.next()));
            }
        }
        return t_result;
    }

    /**
     * {method description}.
     * @return List
     */
    public List<String> getJDBCConnectionPoolNames() {
        List<String> t_result = null;
        if (getServerRootMonitor() != null) {
        	@SuppressWarnings("unchecked")
        	Map<String,Object> t_map = getServerRootMonitor().getJDBCConnectionPoolMonitorMap();
            if (t_map != null && t_map.size() > 0) {
                t_result = new ArrayList<String>();
                Iterator<String> t_it = t_map.keySet().iterator();
                while (t_it.hasNext())
                    t_result.add(String.valueOf(t_it.next()));
            }
        }

        return t_result;
    }

    /**
     * {method description}.
     * @return Map
     * @ 
     */
    public Map<String,String> getWebModuleVirtualServerName()  {
        Map<String, String> t_res = null;
        if (getServerRootMonitor() != null) {
        	@SuppressWarnings("unchecked")
        	Map<String,Object> t_map = getServerRootMonitor().getWebModuleVirtualServerMonitorMap();
            String t_value = null;
            try {
                if (t_map != null && t_map.size() > 0) {
                    t_res = new HashMap<String, String>();
                    Iterator<String> t_it = t_map.keySet().iterator();
                    while (t_it.hasNext()) {
                        t_value = t_it.next().toString();
                        int t_i = t_value.lastIndexOf("/");
                        if (t_i != -1)
                            t_value = t_value.substring(t_i + 1);
                        t_res.put(t_value, t_value);
                    }

                }
            } catch (Throwable t_e) {
            }
        }
        return t_res;
    }

    /**
     * 当前实例的线程池名称
     * 
     * @return List
     */
    public List<String> getThreadPoolNames() {
        List<String> t_result = null;
        if (getServerRootMonitor() != null) {
        	@SuppressWarnings("unchecked")
        	Map<String,Object> t_map = getServerRootMonitor().getThreadPoolMonitorMap();
            if (t_map != null && t_map.size() > 0) {
                t_result = new ArrayList<String>();
                Iterator<String> t_it = t_map.keySet().iterator();
                while (t_it.hasNext()){
                    String t_name = t_it.next().toString();
                    t_name = t_name.substring("orb.threadpool.".length());
                  
                    t_result.add(String.valueOf(t_name));
                }
            }
        }
        return t_result;
    }

    // ====================================================================
    // WebModule
    // ====================================================================

    /**
     * {method description}.
     * @return Map
     */
    @SuppressWarnings("unchecked")
	private Map<String,WebModuleConfig> getWebModuleConfigMap() {
        return m_domainRoot.getDomainConfig().getWebModuleConfigMap();
    }

    /**
     * {method description}.
     * @param webappName webappName
     * @return WebModuleConfig
     */
    public WebModuleConfig getWebModuleConfig(final String webappName) {
        return (WebModuleConfig) getWebModuleConfigMap().get(webappName);
    }

    // ====================================================================
    // 根据DottedNames，获取相应监控或配置指标的属性值
    // ====================================================================

    /**
     * {method description}.
     * @param dottedName dottedName
     * @return Object
     */
    public Object getConfigDottedNameValue(final String dottedName) {
        if (getConfigDottedNames() != null) {
            Attribute t_attr = (Attribute) getConfigDottedNames().dottedNameGet(dottedName);
            if (t_attr != null)
                return t_attr.getValue();
            return null;
        }
        return null;
    }

    /**
     * {method description}.
     * @param dottedName dottedName
     * @return Object
     */
    public Object getMonitoringDottedNameValue(final String dottedName) {
        if (getMonitoringDottedNames() != null) {
            Attribute t_attr = (Attribute) getMonitoringDottedNames().dottedNameGet(dottedName);
            if (t_attr != null) {
                return t_attr.getValue();
            }
            return null;
        }
        return null;
    }

    // ====================================================================
    // MBeanServerConnection
    // ====================================================================

    /**
     * {method description}.
     * @param forceNew forceNew
     * @return MBeanServerConnection
     * @ 
     */
    public MBeanServerConnection getMBeanServerConnection(final boolean forceNew)  {
        try {
            return this.m_connectionSource.getMBeanServerConnection(forceNew);
        } catch (IOException t_e) {
        	return null;
        }
    }

    // ====================================================================
    // 私有方法
    // ====================================================================

    /**
     * {method description}.
     * @param host ip
     * @param port 端口
     * @param usr 用户名
     * @param passwd 密码
     * @return AppserverConnectionSource
     * @ 
     */
    private AppserverConnectionSource getConnectionSource(final String host, final int port, final String usr,
            final String passwd)  {
        if (this.m_connectionSource == null)
            this.m_connectionSource = AMXConnection.getInstance().getConnectionSource(host, port, usr, passwd);

        return this.m_connectionSource;
    }

    /**
     * {method description}.
     * @return ConfigDottedNames
     */
    private ConfigDottedNames getConfigDottedNames() {
        if (m_domainRoot != null)
            return m_domainRoot.getConfigDottedNames();
        return null;

    }

    /**
     * {method description}.
     * @return MonitoringDottedNames
     */
    private MonitoringDottedNames getMonitoringDottedNames() {
        if (m_domainRoot != null)
            return m_domainRoot.getMonitoringDottedNames();
        return null;
    }


    /**
     * {method description}.
     * @return String
     * @throws Exception Exception
     */
    public String getDomainName() throws Exception {
        ObjectName t_objName = new ObjectName(SUN_APPSERV_TYPE_ENGINE);
        MBeanServerConnection t_mBeanConn = getMBeanServerConnection(false);
        String t_fullDomainPath = (String) t_mBeanConn.getAttribute(t_objName, "baseDir");
        int t_idx = t_fullDomainPath.lastIndexOf('/');
        if (-1 == t_idx) {
            t_idx = t_fullDomainPath.lastIndexOf('\\');
        }

        String t_domainName = t_fullDomainPath.substring(t_idx + 1);
        return t_domainName;
    }

}
