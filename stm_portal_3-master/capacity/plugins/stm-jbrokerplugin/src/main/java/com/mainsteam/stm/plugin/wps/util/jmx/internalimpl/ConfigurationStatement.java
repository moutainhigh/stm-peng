package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.configservice.SystemAttributes;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

/**
 * 
 * Configuration ȡֵ���÷���
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
public class ConfigurationStatement extends Statement {

    /**
     * valid attributes
     */
    protected static Set<String> s_attrSet = new HashSet<String>();
    
    static {
        s_attrSet.add("JavaVirtualMachine[maximumHeapSize]");
        s_attrSet.add("JavaVirtualMachine[initialHeapSize]");
        s_attrSet.add("TuningParams[maxInMemorySessionCount]");
        s_attrSet.add("DataSource[_Websphere_Config_Data_Id]");
        s_attrSet.add("DataSource[_Websphere_Config_Data_Type]");
        s_attrSet.add("DataSource[jndiName]");
        s_attrSet.add("DataSource[PARENT_NAME]");
        s_attrSet.add("DataSource.connectionPool[maxConnections]");
        s_attrSet.add("DataSource.connectionPool[minConnections]");
        s_attrSet.add("LDAPUserRegistry.hosts[port]");
        s_attrSet.add("LDAPUserRegistry.hosts[host]");
        s_attrSet.add("VariableMap[value]");
    }

    /**
     * 
     * Configuration Constructors.
     * @param helper 
     * @param connInfo 
     */
    public ConfigurationStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Map> execute(final String operation, final Map<String, String> params, final String subname) 
        throws PluginException {

        String t_filterKey = params.get("filterKey");
        if (t_filterKey == null) {
            t_filterKey = "";
        }
        String t_filterVal = params.get("filterValue");
        if (t_filterVal == null) {
            t_filterVal = "";
        }
        String t_field = params.get("field");
        
        return convert2Result(getFromConfiguration(operation, t_filterKey, t_filterVal, t_field));
    }
    
    /**
     * get configuration from ND service.
     * @param operation type
     * @param filterKey filter
     * @param filterVal filter
     * @param field the field need be fetched.
     * @return result
     * @throws PluginException failure.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map[] getFromConfiguration(final String operation, final String filterKey,
            final String filterVal, final String field) throws PluginException {

        List<Map> t_ret = new ArrayList<Map>();
        
        try {
            ConfigService t_configService = null;
            if ("ND".equals(m_connInfo.getWasServerType())) {
                t_configService = new ConfigServiceProxy(m_helper.getNdClient());
            } else if ("BASE".equals(m_connInfo.getWasServerType())) {
                t_configService = new ConfigServiceProxy(m_helper.getBaseClient());
            }
//            ConfigService t_configService = new ConfigServiceProxy(m_helper.getNdClient());
            
            List<ObjectName> t_configObjects = new ArrayList<ObjectName>();
            if (t_configService != null) {
                t_configObjects = queryConfigObject(operation, t_configService);
            }
            
            for (ObjectName t_configObject : t_configObjects) {
                
                AttributeList t_attrs = t_configService.getAttributes(null, t_configObject, null, true);
                t_attrs = getWpsHomeAttrList(t_attrs, filterKey, filterVal, field);
//                String t_id = ConfigServiceHelper.getConfigDataId(t_configObject).toString();
                AttributeListCollector t_filter = new AttributeListCollector(s_attrSet);
                t_filter.doFilter(operation, t_attrs);
                Map t_configInstanceMap = t_filter.getResultMap();
                if(operation.equals("VariableMap")) {
                	t_ret.add(t_configInstanceMap);
                	return t_ret.toArray(new Map[0]);
                }
                
                // {1} add parent name in arrayList, create a key use ConfigTYPE
                // + [ + parentName + ]
                // this parent name is used for get jdbc provider name by given
                // a jdbc pool id.
                try {

                    ObjectName t_parentObjectName = t_configService.getRelationship(null, t_configObject, "parent")[0];
                    String t_parentName = t_parentObjectName
                            .getKeyProperty(SystemAttributes._WEBSPHERE_CONFIG_DATA_DISPLAY_NAME);

                    t_configInstanceMap.put(new StringBuilder(operation).append("[PARENT_NAME]").toString(),
                            t_parentName);

                } catch (Throwable t_e) {
                    // it is not always can get parent datainfo, just skip
                    // it for common requirment.
                    throw new PluginException(t_e, "");
                }

                t_ret.add(t_configInstanceMap);
                
            }

            return t_ret.toArray(new Map[0]);
            
        } catch (Throwable t_e) {

            throw new PluginException(t_e,"get configuration Failed");
        }
    }
    
    @SuppressWarnings("unchecked")
	private AttributeList getWpsHomeAttrList(AttributeList atts, String filterKey, String filterValue, String field) {
    	if(!"VariableMap[value]".equals(field))
    		return atts;
    	
    	for(Object attr : atts) {
    		if(!(((Attribute)attr).getValue() instanceof List)) 
    			continue;
    		
    		List<AttributeList> entries = (List<AttributeList>) (((Attribute)attr).getValue());
    		for(AttributeList varAttrs : entries) {
    			for(Object tmpObj : varAttrs) {
    				Attribute varAttr = (Attribute)tmpObj;
    				String key = varAttr.getName();
    				String value = varAttr.getValue().toString();
    				if(filterKey.equals(key) && filterValue.equals(value)) {
    					return varAttrs;
    				}
    			}
    		}
    	}
    	
    	return null;
    }
    
    /**
     * do query 
     * @param configType 
     * @param configService 
     * @return result
     * @throws ConfigServiceException 
     * @throws ConnectorException 
     */
    private List<ObjectName> queryConfigObject(final String configType, final ConfigService configService)
        throws ConfigServiceException, ConnectorException {

        ObjectName[] t_configObjects = queryConfigRawObject(configType, configService);

        List<ObjectName> t_selectedResource = new ArrayList<ObjectName>();

        for (ObjectName t_configObjectItem : t_configObjects) {

            Properties t_props = ConfigServiceHelper.getObjectLocation(t_configObjectItem);

            String t_node = (String) t_props.get("node");
            String t_server = (String) t_props.get("server");

            if (isCurrentServer(t_node, t_server)) {
                // select resource in server usable scope.
                t_selectedResource.add(t_configObjectItem);
            }

        }

        return t_selectedResource;
    }
    
    /**
     * is the data belong this server. 
     * @param node 
     * @param server 
     * @return result
     */
    private boolean isCurrentServer(final String node, final String server) {
        return (server == null && node == null) || (m_nodeName.equals(node) && server == null)
                || (m_nodeName.equals(node) && m_serverName.equals(server));
    }  
    
    /**
     * get query result from WAS
     * @param configType 
     * @param configService 
     * @return raw data
     * @throws ConfigServiceException 
     * @throws ConnectorException 
     */
    private ObjectName[] queryConfigRawObject(final String configType, final ConfigService configService)
        throws ConfigServiceException, ConnectorException {

        String t_oConfigSuffix = "Cell=" + m_cellName;
        String t_oNode = ":Node=" + m_nodeName;
        String t_oServer = ":Server=" + m_serverName;

        StringBuilder t_configScope = new StringBuilder(t_oConfigSuffix);
        if (!configType.equals("LDAPUserRegistry")) {
            t_configScope.append(t_oNode);
        }
        
        
        if (!configType.equals("DataSource") && !configType.equals("LDAPUserRegistry")
                && !configType.equals("NamedEndPoint") && !configType.equals("VariableMap")) {
            t_configScope.append(t_oServer);
        } 

        ObjectName[] t_configNames = configService.resolve(null, t_configScope.toString());

        ObjectName t_pattern = ConfigServiceHelper.createObjectName(null, configType);

        ObjectName[] t_configObject = configService.queryConfigObjects(null, t_configNames[0], t_pattern, null);
        
        return t_configObject;
    }
    
    /**
     * convert Map to CollectResult
     * @param rawdata 
     * @return collect result.
     */
    @SuppressWarnings("rawtypes")
    protected List<Map> convert2Result(final Map[] rawdata) {
        
        List<Map> t_ret = new ArrayList<Map>();
        for (Map t_each : rawdata) {
            t_ret.add(t_each);
        }
        
        return t_ret;
    }

}
