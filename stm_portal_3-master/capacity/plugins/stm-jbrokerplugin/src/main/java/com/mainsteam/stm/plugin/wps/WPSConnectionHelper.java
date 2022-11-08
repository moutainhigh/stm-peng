package com.mainsteam.stm.plugin.wps;

import java.util.Map;
import java.util.WeakHashMap;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;
/**
 * 
 * {class description}
 * <br>
 *  
 * <p>
 * Create on : 2012-7-22<br>
 * <p>
 * </p>
 * <br>
 * @author chuzunying@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public class WPSConnectionHelper {
    /**
     * cache for attribute
     */
    private final Map<String, String> m_cachedAttributes = new WeakHashMap<String, String>();
    
    /**
     * WPS connection information
     */
    private final WPSConnectionInfo m_connInfo;
    
    /**
     * WebSphere Admin Client which connect to base service.
     */
    private AdminClient m_baseClient;
    
    /**
     * WebSphere Admin Client which connect to Network deployment service
     */
    private AdminClient m_ndClient; 
    
    /**
     * the excepitn when client connect to server failure.
     */
    private ConnectorException m_connectedFailure;
    
    /**
     * Constructors.
     * @param connInfo connection information
     */
    public WPSConnectionHelper(final WPSConnectionInfo connInfo) {
        this.m_connInfo = connInfo;
    }
    
    /**
     * the getter for client. 
     * @return client
     * @throws ConnectorException 
     */
    public AdminClient getBaseClient() throws ConnectorException {
        
        if (m_baseClient == null) {
            try {
                m_baseClient = AdminClientFactory.createAdminClient(m_connInfo.toBaseProperties());
            } catch (ConnectorException t_e) {
                m_connectedFailure = t_e;
                throw t_e;
            }
        }
        
        return m_baseClient;
    }
    
    /**
     * the getter for network deployment client. 
     * @return client
     * @throws ConnectorException 
     */
    public AdminClient getNdClient() throws ConnectorException {
        
        if (m_ndClient == null) {
            try {
                m_ndClient = AdminClientFactory.createAdminClient(m_connInfo.toNDProperties());
            } catch (ConnectorException t_e) {
                m_connectedFailure = t_e;
                throw t_e;
            }
        }
        
        return m_ndClient;
    }
    
    /**
     * create client and connect to server.
     */
    public void createClient() {
        try {
            m_baseClient = AdminClientFactory.createAdminClient(m_connInfo.toBaseProperties());
            if ("ND".equals(m_connInfo.getWasServerType())) {
                m_ndClient = AdminClientFactory.createAdminClient(m_connInfo.toNDProperties());
            }
            m_connectedFailure = null;
        } catch (ConnectorException t_e) {
        	t_e.printStackTrace();
            m_connectedFailure = t_e;
            m_baseClient = null;
            m_ndClient = null;
            throw new PluginException(t_e);
        }
    }
    /**
     * get attribute from server.
     * @param attr attribute name
     * @return value.
     * @throws PluginException 
     */
    public String getAttribute(final String attr) throws PluginException {

        String t_ret = m_cachedAttributes.get(attr);
        if (t_ret == null) {
            t_ret = getAttribute(m_baseClient, attr);
            m_cachedAttributes.put(attr, t_ret);
        }
        
        return t_ret;
    }
    
    /**
     * get attribute from nd service. 
     * @param attr - attribute name.
     * @return value.
     * @throws PluginException 
     */
    public String getNDAttribut(final String attr) throws PluginException {
        
        String t_ret = m_cachedAttributes.get(attr);
        if (t_ret == null) {
            t_ret = getAttribute(m_ndClient, attr);
            m_cachedAttributes.put(attr, t_ret);
        }

        return t_ret;
    }
    
    /**
     * get attribute base on the different client instance.
     * @param client - client connection
     * @param attr   - attribute name.
     * @return value.
     * @throws PluginException 
     */
    private String getAttribute(final AdminClient client, final String attr) throws PluginException {
        try {
            return client.getAttribute(client.getServerMBean(), attr).toString();

        } catch (Throwable t_e) {
            throw new PluginException(t_e);
        } 
    }
    
    /**
     * validate connection, if the connection was invalid, create a new
     * @return whether the connection available.
     */
    public boolean validateConnection() {

        if (m_baseClient == null) {
            createClient();
        }

        if (m_connectedFailure != null) {
            // recreate client
            createClient();
        }

        return m_connectedFailure == null;
    }

    /**
     * the getter for failure
     * @return connector exception
     */
    public ConnectorException getConnectedFailure() {
        return m_connectedFailure;
    }

}
