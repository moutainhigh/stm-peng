package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;
import com.ibm.websphere.management.exception.ConnectorException;


/**
 * 
 * J2EEName ȡֵ���÷���
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
public class J2EENameQueryStatement extends Statement {

    /**
     * 
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public J2EENameQueryStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Map> execute(final String operation, final Map<String, String> params, final String subname)
        throws PluginException {
        
        Set t_oNames;
        try {
            
            t_oNames = getQueryObjectName(params);
            if (t_oNames == null || t_oNames.size() == 0) {
                return null;
            }
            
            List<Map> t_ret = new ArrayList<Map>();
            
            for (Object t_oName : t_oNames) {
                ObjectName t_each = (ObjectName) t_oName;
                Hashtable<String, String> t_map = t_each.getKeyPropertyList();

                t_ret.add(t_map);
            }

            return t_ret;
            
        } catch (MalformedObjectNameException t_e) {
            return null;
        } catch (ConnectorException t_e) {
            throw new PluginException(t_e, "get j2ee name failed!");
        }
    }
    
    /**
     * fetch raw from server
     * @param parameters 
     * @return result
     * @throws MalformedObjectNameException failure
     * @throws ConnectorException failure
     */
    @SuppressWarnings("rawtypes")
    private Set getQueryObjectName(final Map<String, String> parameters) throws MalformedObjectNameException,
        ConnectorException {

        String t_key = parameters.get("filterKey");
        String t_val = parameters.get("filterValue");
        
        StringBuilder t_moduleName = new StringBuilder("WebSphere:*,");
        t_moduleName.append(t_key).append('=').append(t_val).append(',');
        
        t_moduleName.append("cell=").append(m_cellName).append(',');
        t_moduleName.append("node=").append(m_nodeName).append(',');
        t_moduleName.append("process=").append(m_serverName);

        ObjectName t_oModule = new ObjectName(t_moduleName.toString());
        Set t_oNames = m_helper.getBaseClient().queryNames(t_oModule, null);
        
        return t_oNames;
    }

}
