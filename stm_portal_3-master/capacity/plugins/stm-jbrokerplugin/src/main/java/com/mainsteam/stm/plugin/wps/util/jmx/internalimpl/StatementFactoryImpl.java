package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;
import com.mainsteam.stm.plugin.wps.util.jmx.StatementFactory;

/**
 * 
 * {class description}
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
public class StatementFactoryImpl extends StatementFactory {

    /**
     * the table hold the all related statement instance.
     */
    private static Map<String, Statement> s_statementTable = new WeakHashMap<String, Statement>();
    
    /**
     * the table hold all connection
     */
    private static Map<WPSConnectionInfo, WPSConnectionHelper> s_connTable =
        new HashMap<WPSConnectionInfo, WPSConnectionHelper>();

    @Override
    public synchronized Statement newStatement(final String name, final WPSConnectionInfo connInfo) {
        
//        StringBuilder t_key = new StringBuilder();
//        t_key.append(connInfo.getIp()).append('_').append(connInfo.getAppPort()).append('_').append(name);
        String t_key = connInfo.getIp() + "_" + connInfo.getAppPort() + "_" + name;
        
        Statement t_instance = s_statementTable.get(t_key);
        if (t_instance == null) {
            try {
                t_instance = createStatement(name, connInfo);
            } catch (PluginException t_e) {
                t_e.printStackTrace();
            }
            s_statementTable.put(t_key.toString(), t_instance);
        }
        
        return t_instance;
    }

    /**
     * 
     * {method description}.
     * @param name 
     * @param connInfo 
     * @return Statement 
     * @throws PluginException 
     */
    private Statement createStatement(final String name, final WPSConnectionInfo connInfo) throws PluginException {

        Statement t_ret = null;
        
        if ("SERVER".equalsIgnoreCase(name)) {
            t_ret = new ServerStatement(getHelper(connInfo), connInfo);
        } else if ("CONFIG".equals(name)) {
            t_ret = new ConfigurationStatement(getHelper(connInfo), connInfo);
        } else if ("PMI".equalsIgnoreCase(name)) {
            t_ret = new PMIStatement(getHelper(connInfo), connInfo);
        } else if ("J2EENAMEQUERY".equalsIgnoreCase(name)) {
            t_ret = new J2EENameQueryStatement(getHelper(connInfo), connInfo);
        } else if ("JDBC".equalsIgnoreCase(name)) {
            t_ret = new JdbcPoolStateStatement(getHelper(connInfo), connInfo);
        } else if ("MBeanQuery".equalsIgnoreCase(name)) {
            t_ret = new MBeanQueryStatement(getHelper(connInfo), connInfo);
        } else if ("PortletName".equals(name)) {
            t_ret = new PortletNameQueryStatement(getHelper(connInfo), connInfo);
        } else {
            t_ret = null;
        }

        return t_ret;
    }

    /**
     * create connection helper 
     * @param connInfo 
     * @return helper instance.
     */
    private WPSConnectionHelper getHelper(final WPSConnectionInfo connInfo) {
        
        WPSConnectionHelper t_instance = s_connTable.get(connInfo);
        if (t_instance == null) {
            t_instance = new WPSConnectionHelper(connInfo);
            s_connTable.put(connInfo, t_instance);
        }
        
        return t_instance;
    }


}
