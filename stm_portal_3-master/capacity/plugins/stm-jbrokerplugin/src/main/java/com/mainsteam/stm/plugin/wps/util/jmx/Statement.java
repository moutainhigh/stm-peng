package com.mainsteam.stm.plugin.wps.util.jmx;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.wlm.ClusterData;

/**
 * 
 * 抽象不同的调用方式
 * <br>
 *  
 * <p>
 * Create on : 2012-6-27<br>
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
public abstract class Statement {
    private final static Log log = LogFactory.getLog(Statement.class);
    /**
     * JMX connection helper.
     */
    protected WPSConnectionHelper m_helper;
    
    /**
     * WPS connectionInfo
     */
    protected WPSConnectionInfo m_connInfo;
    
    /**
     * Process ID
     */
    protected String m_pid;
    
    /**
     * the cell name of server
     */
    protected String m_cellName;
    
    /**
     * the node name
     */
    protected String m_nodeName;
    
    /**
     * the server name
     */
    protected String m_serverName;
    
    /**
     * the process type
     * values: {"ManagedProcess", }
     */
    protected String m_processType;
    
    /**
     * the cell name of network deployment service.
     */
    protected String m_ndCellName;
    
    /**
     * the node name of network deployment service.
     */
    protected String m_ndNodeName;
    
    /**
     * the server name of network deployment service.
     */
    protected String m_ndServerName;
    
    /**
     * 
     */
    protected ClusterData m_clusterData;
    
    /**
     * 
     */
    private ObjectName m_clusterMgrMBean;
    
    /**
     * the flag for whether the statement has be initialized.
     */
    private boolean m_isInit;
    
    /**
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public Statement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        m_helper = helper;
        m_connInfo = connInfo;
    }
    
    /**
     * secondary structure
     * @throws PluginException 
     */
    public synchronized void init() throws PluginException {
        
        if (m_isInit) {
            return;
        }
        
        if (!m_helper.validateConnection()) {
            throw new PluginException(m_helper.getConnectedFailure());
        }
        
        try {
            m_pid = m_helper.getAttribute("pid");
            
            m_cellName = m_helper.getAttribute("cellName");
            m_nodeName = m_helper.getAttribute("nodeName");
            m_serverName = m_helper.getAttribute("name");
            
            m_processType = m_helper.getAttribute("processType");
            
            m_ndCellName = m_helper.getNDAttribut("cellName");
            m_ndNodeName = m_helper.getNDAttribut("nodeName");
            m_ndServerName = m_helper.getNDAttribut("name");

        } catch (PluginException t_e) {
        	log.error("Init statement failed!", t_e);
            throw new PluginException(t_e, "Init statement failed!");
        }
        
        m_isInit = true;
    }
    
    /**
     * execute collect statement.
     * @param operation - the getter name.
     * @param params - parameters from cmds.xml.
     * @param subname 
     * @return result
     * @throws PluginException when execute failure
     */
    public abstract Object execute(final String operation, final Map<String, String> params, final String subname)
        throws PluginException;
    
    /**
     * get MBean based on the special type 
     * @param mbeanType - type
     * @return MBean
     */
    public ObjectName getMbean(final String mbeanType) {
        try {
            String t_queryStr = "WebSphere:*,type=" + mbeanType 
                    + ",node=" + m_nodeName
                    + ",process=" + m_serverName;

            Set<?> t_oSet = m_helper.getBaseClient().queryNames(new ObjectName(t_queryStr), null);
            Iterator<?> t_it = t_oSet.iterator();
            if (t_it.hasNext())
                return (ObjectName) t_it.next();
            else {
                return null;
            }
        } catch (Throwable t_e) {
            return null;
        }
    }
    
    /**
     * getter for PID 
     * @return pid
     */
    public String getPID() {
        return m_pid;
    }
    
    /**
     * get cluster data
     * @return cluster data
     * @throws PluginException 
     */
    public ClusterData queryClusterData() throws PluginException {

        try {
            if (m_clusterMgrMBean == null) {
                m_clusterMgrMBean = this.queryClusterMgrObjectName();
            }

            if (m_clusterData == null) {
                m_clusterData = queryClusterData0();
            }
            
            return m_clusterData;

        } catch (Throwable t_e) {
        	log.error("Query cluster Data failed!", t_e);
            throw new PluginException(t_e, "");
        }
    }
    
    /**
     * get cluster manager bean
     * @return result
     * @throws MalformedObjectNameException 
     * @throws ConnectorException 
     */
    @SuppressWarnings("rawtypes")
    private ObjectName queryClusterMgrObjectName() throws MalformedObjectNameException, ConnectorException {

        StringBuilder t_moduleName = new StringBuilder("WebSphere:*,name=ClusterMgr,");
        t_moduleName.append("cell=").append(m_ndCellName).append(",node=").append(m_ndNodeName).append(",process=")
                .append(m_ndServerName);

        ObjectName t_oModule = new ObjectName(t_moduleName.toString());
        Set t_oNames = m_helper.getBaseClient().queryNames(t_oModule, null);
        
        if ("ND".equals(m_connInfo.getWasServerType())) {
            t_oNames = m_helper.getNdClient().queryNames(t_oModule, null);
        }

        ObjectName t_queryBean = null;
        if (t_oNames != null && t_oNames.size() > 0) {
            t_queryBean = (ObjectName) t_oNames.toArray()[0];
        } else {
            t_queryBean = null;
        }

        return t_queryBean;
    }
    
    /**
     * get cluster data
     * @return result
     * @throws ConnectorException  
     * @throws ReflectionException  
     * @throws MBeanException  
     * @throws InstanceNotFoundException  
     */
    private ClusterData queryClusterData0() throws InstanceNotFoundException, MBeanException, ReflectionException,
            ConnectorException {
        if (m_clusterMgrMBean == null)
            return null;

        Object[] t_params = new Object[] { m_serverName, m_nodeName };
        String[] t_signature = new String[] { "java.lang.String", "java.lang.String" };
        ClusterData t_clusterData = (ClusterData) m_helper.getNdClient().invoke(m_clusterMgrMBean,
                "retrieveClusterByMember", t_params, t_signature);

        // find the PmiModuleConfig and bind it with the data
        return t_clusterData;
    }
    
    
    
}
