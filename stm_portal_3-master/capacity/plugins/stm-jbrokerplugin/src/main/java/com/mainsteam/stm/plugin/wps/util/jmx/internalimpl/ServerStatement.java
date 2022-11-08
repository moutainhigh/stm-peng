package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;

/**
 * 
 * the statement for get server information
 * <br>
 *  
 * <p>
 * Create on : 2012-6-28<br>
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
public class ServerStatement extends Statement {

    /**
     * 
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public ServerStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }

    @Override
    public String execute(final String getter, final Map<String, String> params, final String subname) 
        throws PluginException {

        if (!m_helper.validateConnection()) {
            return "";
        }
        
        String t_result;

        String t_mode = params.get("mode");
        
        if ("attribute".equalsIgnoreCase(t_mode)) {

            // extract attribute name
            StringBuilder t_sb = new StringBuilder();
            String t_attr = getter;
            if (getter.startsWith("get")) {
                t_attr = getter.substring("get".length());
                Character.toLowerCase(t_attr.charAt(0));
                t_sb.append(Character.toLowerCase(t_attr.charAt(0)));
                t_sb.append(t_attr.substring(1));
            }

            t_result = m_helper.getAttribute(t_sb.toString());
            
            
        } else if ("getter".equalsIgnoreCase(t_mode)) {
            t_result = invokeGetter(getter);

        } else {
            throw new PluginException("Unknown mode (" + t_mode + ")");
        }
        
        return t_result;
    }
    
    /**
     * Invoke the getter method 
     * @param operationName - method name
     * 
     * @return result.
     */
    private String invokeGetter(final String operationName) {

        try {
            AdminClient t_baseClient = m_helper.getBaseClient();
//            AdminClient t_ndClient = m_helper.getNdClient();

            if ("BASE".equals(m_connInfo.getWasServerType())) {
                ObjectName t_oModule = t_baseClient.getServerMBean();
                String t_ret = t_oModule.getKeyProperty("version");
                return t_ret;
            }
            String t_xmlResult = t_baseClient.invoke(t_baseClient.getServerMBean(), operationName,
                    new Object[] { "BASE" }, new String[] { "java.lang.String" }).toString();
            

            if ("ND".equals(m_connInfo.getWasServerType())) {
                AdminClient t_ndClient = m_helper.getNdClient();
                if (t_xmlResult == null || t_xmlResult.length() == 0) {
                    t_xmlResult = t_ndClient.invoke(t_ndClient.getServerMBean(), operationName, new Object[] { "ND" },
                            new String[] { "java.lang.String" }).toString();
                }
            }

            return transformXml2String(t_xmlResult, operationName); 

        } catch (InstanceNotFoundException t_e) {
            t_e.printStackTrace();
        } catch (MBeanException t_e) {
//            S_LOGGER.error("ERR_PLUGIN_WAS_CONNECTION_QUERY_ERROR" + "\n MBean unknown (" + operationName + ")", t_e);
        } catch (ReflectionException t_e) {
            t_e.printStackTrace();
        } catch (ConnectorException t_e) {
            t_e.printStackTrace();
        }

        return null;
    }
    
    /**
     * convert xml data to a String.
     * @param xml server return value.
     * @param operation method name
     * @return string format result 
     */
    protected String transformXml2String(String xml, final String operation) {

        JSONObject t_json;

        try {
        	xml = xml.replaceAll("<!DOCTYPE product PUBLIC(.*)dtd(.*)", "");
        	t_json = (JSONObject) new XMLSerializer().read(xml);
//            t_json = XML.toJSONObject(xml);
        } catch (JSONException t_e) {
            return xml;
        }

        String t_ret;

        if ("getProductVersion".equalsIgnoreCase(operation)) {
            try {
            	
                t_ret = t_json.getString("version");
            } catch (JSONException t_e) {
                t_ret = xml;
            }
        } else {
            t_ret = null;
        }

        return t_ret;

    }

}
