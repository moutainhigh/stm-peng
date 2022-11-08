package com.mainsteam.stm.plugin.was;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.configservice.SystemAttributes;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

@SuppressWarnings("rawtypes")
public class ConfigCollector {

	private static final Log logger = LogFactory.getLog(ConfigCollector.class);

	protected static Set<String> s_attrSet = new HashSet<String>();// valid
																	// attributes
	private AdminClient client;
	private AdminClient ndClient;

	protected String m_cellName;
	protected String m_nodeName;
	protected String m_serverName;

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
		s_attrSet.add("ThreadPool[maximumSize]");
		s_attrSet.add("ThreadPool[name]");
		s_attrSet.add("NamedEndPoint.endPoint[port]");
		s_attrSet.add("NamedEndPoint[endPointName]");
		// s_attrSet.add("LDAPUserRegistry.hosts[port]");
		// s_attrSet.add("LDAPUserRegistry.hosts[host]");
		// s_attrSet.add("VariableMap[value]");
	}

	public ConfigCollector(AdminClient client, AdminClient ndClient) {
		this.client = client;
		this.ndClient = ndClient;
		init();
	}

	private void init() {
		try {
			m_cellName = client.getAttribute(client.getServerMBean(),
					"cellName").toString();
			m_nodeName = client.getAttribute(client.getServerMBean(),
					"nodeName").toString();
			m_serverName = client.getAttribute(client.getServerMBean(), "name")
					.toString();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Fail to initialize configcollector", e);
			}
		}
	}

	public List<Map> execute(final String operation, final String filterKey,
			final String filterValue) throws InstanceNotFoundException,
			ConfigServiceException, ConnectorException {
		List<Map> maps = convert2Result(getFromConfiguration(operation));
		if ((filterKey != null && !filterKey.equals(""))
				|| (filterValue != null && !filterValue.equals(""))) {
			doFilter(maps, filterKey, filterValue);
		}
		return maps;
	}

	private void doFilter(List<Map> maps, String filterKey, String filterValue) {
		for (int i = maps.size() - 1; i >= 0; i--) {
			Map row = maps.get(i);
			if (!filterValue.equals(row.get(filterKey))) {
				maps.remove(i);
			}
		}
	}

	/**
	 * get configuration from ND service.
	 * 
	 * @param operation
	 *            type
	 * @return result
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws ConfigServiceException
	 */
	
	@SuppressWarnings("unchecked")
	private Map[] getFromConfiguration(final String operation)
			throws InstanceNotFoundException, ConnectorException,
			ConfigServiceException {
		List<Map> t_ret = new ArrayList<Map>();
		ConfigService t_configService = null;
		if (ndClient != null) {
			t_configService = new ConfigServiceProxy(ndClient);
		} else {
			t_configService = new ConfigServiceProxy(client);
		}
		long start = System.currentTimeMillis();
		List<ObjectName> t_configObjects = new ArrayList<ObjectName>();
		if (t_configService != null) {
			t_configObjects = queryConfigObject(operation, t_configService);
		}
		long end = System.currentTimeMillis();
		if (logger.isInfoEnabled()){
			logger.info("Get config obj:" + (end - start));
		}
		start = System.currentTimeMillis();
		for (ObjectName t_configObject : t_configObjects) {
			AttributeList t_attrs = t_configService.getAttributes(null,
					t_configObject, null, true);
			AttributeListCollector t_filter = new AttributeListCollector(
					s_attrSet);
			t_filter.doFilter(operation, t_attrs);
			Map t_configInstanceMap = t_filter.getResultMap();
			if (operation.equals("VariableMap")) {
				t_ret.add(t_configInstanceMap);
				return t_ret.toArray(new Map[0]);
			}
			if (operation.equals("DataSource")) {
				ObjectName t_parentObjectName = t_configService
						.getRelationship(null, t_configObject, "parent")[0];
				String t_parentName = t_parentObjectName
						.getKeyProperty(SystemAttributes._WEBSPHERE_CONFIG_DATA_DISPLAY_NAME);
				t_configInstanceMap.put(
						new StringBuilder(operation).append("[PARENT_NAME]")
								.toString(), t_parentName);
			}
			t_ret.add(t_configInstanceMap);
		}
		end = System.currentTimeMillis();
		if (logger.isInfoEnabled()){
			logger.info("Get config attr:" + (end - start));
		}
		return t_ret.toArray(new Map[0]);
	}

	/**
	 * do query
	 * 
	 * @param configType
	 * @param configService
	 * @return result
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private List<ObjectName> queryConfigObject(final String configType,
			final ConfigService configService) throws ConfigServiceException,
			ConnectorException {

		ObjectName[] t_configObjects = queryConfigRawObject(configType,
				configService);

		List<ObjectName> t_selectedResource = new ArrayList<ObjectName>();

		for (ObjectName t_configObjectItem : t_configObjects) {

			Properties t_props = ConfigServiceHelper
					.getObjectLocation(t_configObjectItem);

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
	 * whether the data belong to the target.
	 * 
	 * @param node
	 * @param server
	 * @return result
	 */
	private boolean isCurrentServer(final String node, final String server) {
		return (server == null && node == null)
				|| (m_nodeName.equals(node) && server == null)
				|| (m_nodeName.equals(node) && m_serverName.equals(server));
	}

	/**
	 * get query result from WAS
	 * 
	 * @param configType
	 * @param configService
	 * @return raw data
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private ObjectName[] queryConfigRawObject(final String configType,
			final ConfigService configService) throws ConfigServiceException,
			ConnectorException {

		String t_oConfigSuffix = "Cell=" + m_cellName;
		String t_oNode = ":Node=" + m_nodeName;
		String t_oServer = ":Server=" + m_serverName;

		StringBuilder t_configScope = new StringBuilder(t_oConfigSuffix);
		if (!configType.equals("LDAPUserRegistry")) {
			t_configScope.append(t_oNode);
		}

		if (!configType.equals("DataSource")
				&& !configType.equals("LDAPUserRegistry")
				&& !configType.equals("NamedEndPoint")
				&& !configType.equals("VariableMap")) {
			t_configScope.append(t_oServer);
		}

		ObjectName[] t_configNames = configService.resolve(null,
				t_configScope.toString());

		ObjectName t_pattern = ConfigServiceHelper.createObjectName(null,
				configType);

		ObjectName[] t_configObject = configService.queryConfigObjects(null,
				t_configNames[0], t_pattern, null);

		return t_configObject;
	}

	/**
	 * convert Map to CollectResult
	 * 
	 * @param rawdata
	 * @return collect result.
	 */
	protected List<Map> convert2Result(final Map[] rawdata) {

		List<Map> t_ret = new ArrayList<Map>();
		for (Map t_each : rawdata) {
			t_ret.add(t_each);
		}
		return t_ret;
	}

}
