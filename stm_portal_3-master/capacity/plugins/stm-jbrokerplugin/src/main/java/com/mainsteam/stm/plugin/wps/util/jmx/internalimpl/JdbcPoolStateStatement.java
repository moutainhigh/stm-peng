package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.websphere.management.exception.ConnectorException;
import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;

/**
 * 
 * JDBCPool ȡֵ���÷���
 * <br>
 *  
 * <p>
 * Create on : 2012-9-13<br>
 * <p>
 * </p>
 * <br>
 * @author chuzunying@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps-6.2.0 v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public class JdbcPoolStateStatement extends Statement {

    /**
     * 1
     */
    private static final String S_AVAIL = "1";

    /**
     * 0
     */
    private static final String S_0 = "0";
    
    /**
     * S_NO_AVAIL_-1
     */
    private static final String S_NO_AVAIL = "0";

    /**
     *  
     */
    private ObjectName m_serverHelper;
    
    /**
     * 
     */
    private ConnectorException m_failure;
    
    /**
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public JdbcPoolStateStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object execute(final String operation, final Map<String, String> params, final String subname)
        throws PluginException {
        
        if (!validateConnection()) {
            return new String[] { S_0 };
        }
        
        try {
            
            if (m_serverHelper == null) {
                initDataSourceCfgHelper();
            }
            
            ConfigurationStatement t_statement = new ConfigurationStatement(m_helper, m_connInfo);
            t_statement.init();
            
//            Matric t_ret = new Matric();
//            CollectResult<String> t_ret = new CollectResult<String>();
            Map<String, String> t_map = new HashMap<String, String>();
            
            List<Map> t_idsSource = t_statement.execute("DataSource", new HashMap<String, String>(), "");
            for (Map t_row : t_idsSource) {
                String t_id = (String) t_row.get(params.get("dependon"));
                Object t_result;
                try {
                    t_result = m_helper.getBaseClient().invoke(m_serverHelper, "testConnection",
                            new String[] {t_id}, new String[] { "java.lang.String" });
                } catch (Throwable t_e) {
                    t_result = S_NO_AVAIL;
                }

                t_map.put(t_id, S_0.equals(t_result.toString()) ? S_AVAIL : S_NO_AVAIL);
            }
            
            
//            return t_ret;
            return t_map;

        } catch (Throwable t_t) {

            throw new PluginException(t_t);
        }
    }

    /**
     * init  
     * @throws MalformedObjectNameException failure
     * @throws ConnectorException failure
     */
    private void initDataSourceCfgHelper() throws MalformedObjectNameException, ConnectorException {

        ObjectName t_queryName = new ObjectName("WebSphere:type=DataSourceCfgHelper,*");
        
        Set<?> t_set = m_helper.getBaseClient().queryNames(t_queryName, null);
        
        // for ND you need to specify which node/process you would like to test
        // from
        // eg run in the server
        // ND: ObjectName queryName = new
        // OjectName("WebSphere:cell=catNetwork,node=cat,process=server1,type=DataSourceCfgHelper,*");
        // eg run in the node agent
        // ND: ObjectName queryName = new
        // ObjectName("WebSphere:cell=catNetwork,node=cat,process=nodeagent,type=DataSourceCfgHelper,*");
        // eg run in the Deployment Manager
        // ND: ObjectName queryName = new
        // ObjectName("WebSphere:cell=catNetwork,node=catManager,process=dmgr,type=DataSourceCfgHelper,*");
        Iterator<?> t_it = t_set.iterator();
        while (t_it.hasNext()) {
            // use the first MBean that is found
            m_serverHelper = (ObjectName) t_it.next();
        }
    }
    
    
    /**
     * validate connection, if the connection was invalid, create a new
     * @return whether the connection available.
     */
    private boolean validateConnection() {

        if (m_failure == null) {
            return true;
        }
        
        try {
            initDataSourceCfgHelper();
        } catch (MalformedObjectNameException t_e) {
            t_e.printStackTrace();
        } catch (ConnectorException t_e) {
            m_failure = t_e;
        }

        return m_failure == null;
    }

}
