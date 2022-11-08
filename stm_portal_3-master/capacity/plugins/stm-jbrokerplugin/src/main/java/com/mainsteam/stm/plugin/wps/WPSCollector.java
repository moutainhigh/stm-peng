package com.mainsteam.stm.plugin.wps;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.wps.util.jmx.Statement;
import com.mainsteam.stm.plugin.wps.util.jmx.StatementFactory;

@SuppressWarnings({"rawtypes","unchecked"})
public class WPSCollector {
	private static final Log log = LogFactory.getLog(WPSCollector.class);
   
	public String getAvailability(WPSConnectionInfo connInfo) {
		try {
			String avail = getByCmdId("wps.serverStatus", connInfo).toString();
			if(avail.equalsIgnoreCase("STARTED")) {
				return "1";
			}else {
				return "0";
			}
		}catch(Exception ex) {
			log.debug("getAvailability failed", ex);
			return "0";
		}
	}
	
	public String getJVMMEMRate(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.jvmmemrate", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.jvmmemrate");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			String value2 = ((String[])tmp.get(params.get("filterValue2")))[0];
			String result = formatDouble(Double.parseDouble(value1)*100/Double.parseDouble(value2));
			log.debug("getJVMMEMRate:" + result);
			return result;
		}catch(Exception ex) {
			log.debug("getJVMMEMRate failed", ex);
			return "0";
		}
	}
	
	
	public String getTotalJvmMemSize(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.heapSize", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.heapSize");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return formatDouble(Double.parseDouble(value1)/1024);
		}catch(Exception ex) {
			log.debug("getAvailability failed", ex);
			return "0";
		}
	}
	
	public String getUsedJvmMemSize(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.usedMemory", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.usedMemory");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return formatDouble(Double.parseDouble(value1)/1024);
		}catch(Exception ex) {
			log.debug("getAvailability failed", ex);
			return "0";
		}
	}
	
	
	public String getAvgWaitTime(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.avgWaitTime", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.avgWaitTime");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return value1;
		}catch(Exception ex) {
			log.debug("getAvgWaitTime failed", ex);
			return "0";
		}
	}
	
	public String getMaxJvmMemSize(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.jvmMaxSize", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.jvmMaxSize");
			String value1 = ((List<Map>)obj).get(0).get(params.get("filterKey")).toString();
			return value1;
		}catch(Exception ex) {
			log.debug("getMaxJvmMemSize failed", ex);
			return "0";
		}
	}
	
	public String getMinJvmMemSize(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.jvmMinSize", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.jvmMinSize");
			String value1 = ((List<Map>)obj).get(0).get(params.get("filterKey")).toString();
			return value1;
		}catch(Exception ex) {
			log.debug("getMinJvmMemSize failed", ex);
			return "0";
		}
	}
	
	public String getNumberOfWebApplication(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.numberWebApp", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.numberWebApp");
			int count = 0;
			for(Map row : (List<Map>)obj) {
				String filterKey = params.get("filterKey");
				String filterValue = params.get("filterValue");
				if(filterValue.equals(row.get(filterKey)))
					count ++;
			}
			return ""+count;
		}catch(Exception ex) {
			log.debug("getNumberOfWebApplication failed", ex);
			return "0";
		}
	}
	
	public String getResDisplayName(WPSConnectionInfo connInfo) {
		try {
			return getByCmdId("wps.displayName", connInfo).toString();
		}catch(Exception ex) {
			log.debug("getResDisplayName failed", ex);
			return " ";
		}
	}
	
	public String getServerName(WPSConnectionInfo connInfo) {
		try {
			return getByCmdId("wps.serverName", connInfo).toString();
		}catch(Exception ex) {
			log.debug("getServerName failed", ex);
			return " ";
		}
		
	}
	
	public String getSystemUpTime(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.systemUptime", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.systemUptime");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return value1;
		}catch(Exception ex) {
			log.debug("getSystemUpTime failed", ex);
			return "0";
		}
	}
	
	public String getLdapPort(WPSConnectionInfo connInfo) {
		try {
			List<Map> obj = (List<Map>) getByCmdId("wps.ldapPort", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.ldapPort");
			String str = obj.get(0).get(params.get("filterKey")).toString();
			return str;
		}catch(Exception ex) {
			log.debug("getLdapPort failed", ex);
			return "0";
		}
	}
	
	public String getLdapIp(WPSConnectionInfo connInfo) {
		try {
			List<Map> obj = (List<Map>) getByCmdId("wps.ldapIp", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.ldapIp");
			String str = obj.get(0).get(params.get("filterKey")).toString();
			return str;
		}catch(Exception ex) {
			log.debug("getLdapIp failed", ex);
			return "0";
		}
	}
	
	public String getWasVersion(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.version", connInfo);
			return obj.toString();
		}catch(Exception ex) {
			log.debug("getWasVersion failed", ex);
			return " ";
		}
	}
	
	public String getHome(WPSConnectionInfo connInfo) {
		try {
			List<Map> obj = (List<Map>) getByCmdId("wps.home", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.home");
			return obj.get(0).get(params.get("field")).toString();
		}catch(Exception ex) {
			log.debug("getHome failed", ex);
			return " ";
		}
	}
	
	public String getCellName(WPSConnectionInfo connInfo) {
		try {
			return getByCmdId("wps.cell", connInfo).toString();
		}catch(Exception ex) {
			log.debug("getCellName failed", ex);
			return " ";
		}
	}
	
	public String getNodeName(WPSConnectionInfo connInfo) {
		try {
			return getByCmdId("wps.node", connInfo).toString();
		}catch(Exception ex) {
			log.debug("getNodeName failed", ex);
			return " ";
		}
	}
	
	public String getPid(WPSConnectionInfo connInfo) {
		try {
			return getByCmdId("wps.pid", connInfo).toString();
		}catch(Exception ex) {
			log.debug("getPid failed", ex);
			return " ";
		}
	}
	
	public String getOnlineUserCount(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.onlineUsers", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.onlineUsers");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return value1;	
		}catch(Exception ex) {
			log.debug("getOnlineUserCount failed", ex);
			return "0";
		}
	}
	
	public String getCurConnUserCount(WPSConnectionInfo connInfo) {
		try {
			Object obj = getByCmdId("wps.currentConnUser", connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.currentConnUser");
			Map<String, String[]> tmp = ((Map<String, Map>)obj).get(params.get("filterKey"));
			String value1 = ((String[])tmp.get(params.get("filterValue")))[0];
			return value1;	
		}catch(Exception ex) {
			log.debug("getCurConnUserCount failed", ex);
			return "0";
		}
	}
	
	public String getJdbcJndiNames(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = (List<Map>) getByCmdId("wps.jndi.name", connInfo);
			return concateJdbcResult(ret, "wps.jndi.name");
		}catch(Exception ex) {
			log.debug("getJdbcJndiNames failed", ex);
			return "0";
		}
	}
	
	public String getJdbcMaxUtilRatio(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.datasource.maxutil", connInfo);
			List<Map> ret = getJdbcDsIdMap(connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.provider.name");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				key = subStrJdbcDSId(key);
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJdbcMaxUtilRatio failed", ex);
			return "0";
		}
	}
	
	public String getJdbcAvgConnWaitTime(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.datasource.waittime", connInfo);
			List<Map> ret = getJdbcDsIdMap(connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.provider.name");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				key = subStrJdbcDSId(key);
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJdbcAvgConnWaitTime failed", ex);
			return "0";
		}
	}
	
	public String getJdbcDsId(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = (List<Map>) getByCmdId("wps.datasource.id", connInfo);
			String dsID =  concateJdbcResult(ret, "wps.datasource.id");
			log.debug("getJdbcDsId:" + dsID);
			return dsID;
		}catch(Exception ex) {
			log.debug("getJdbcDsId failed", ex);
			return " ";
		}
	}
	
	public String getJdbcAvailability(WPSConnectionInfo connInfo) { 
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.pool.available", connInfo);
			StringBuilder sb = new StringBuilder();
			for(String key : obj.keySet()) {
				String value = obj.get(key);
				key = subStrJdbcDSId(key);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJdbcAvailability failed", ex);
			return "0";
		}
	}
	
	public String getJdbcMaxConnCount(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = (List<Map>) getByCmdId("wps.datasource.maxconn", connInfo);
			return concateJdbcResult(ret, "wps.datasource.maxconn");
		}catch(Exception ex) {
			log.debug("getJdbcMaxConnCount failed", ex);
			return "0";
		}
	}
	
	public String getJdbcProviderName(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = getJdbcDsIdMap(connInfo);
			return concateJdbcResult(ret, "wps.provider.name");
		}catch(Exception ex) {
			log.debug("getJdbcProviderName failed", ex);
			return " ";
		}
	}
	
	public String getJdbcMinConnCount(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = (List<Map>) getByCmdId("wps.datasource.minconn", connInfo);
			return concateJdbcResult(ret, "wps.datasource.minconn");
		}catch(Exception ex) {
			log.debug("getJdbcMinConnCount failed", ex);
			return "0";
		}
	}
	
	public String getJdbcPoolType(WPSConnectionInfo connInfo) {
		try {
			List<Map> ret = (List<Map>) getByCmdId("wps.datasource.type", connInfo);
			return concateJdbcResult(ret, "wps.datasource.type");
		}catch(Exception ex) {
			log.debug("getJdbcPoolType failed", ex);
			return " ";
		}
	}
	
	public String getJdbcFreePoolSize(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.datasource.freePoolSize", connInfo);
			List<Map> ret = getJdbcDsIdMap(connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.provider.name");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				key = subStrJdbcDSId(key);
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJdbcFreePoolSize failed", ex);
			return "0";
		}
	}
	
	public String getJdbcConnPoolSize(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.datasource.connPoolSize", connInfo);
			List<Map> ret = getJdbcDsIdMap(connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.provider.name");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				key = subStrJdbcDSId(key);
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJdbcConnPoolSize failed", ex);
			return "0";
		}
	}
	
	private List<Map> jdbcDsId;
	public List<Map> getJdbcDsIdMap(WPSConnectionInfo connInfo) {
		if(jdbcDsId == null) {
			jdbcDsId = (List<Map>) getByCmdId("wps.provider.name", connInfo);
		}
		return jdbcDsId;
	}
	
	public String getJDBCUsedUtilRatio(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.datasource.usedutil", connInfo);
			List<Map> ret = getJdbcDsIdMap(connInfo);
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.provider.name");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				key = subStrJdbcDSId(key);
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getJDBCUsedUtilRatio failed", ex);
			return "0";
		}
	}
	
	private List<Map> portletIdMap = new ArrayList<Map>();
	private List<Map> getPortletIdMap(WPSConnectionInfo connInfo) {
		if(portletIdMap != null) {
			return portletIdMap;
		}
			
		List<Map> portletIdMap = (List<Map>) getByCmdId("wps.portlet.portletId", connInfo);
		Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
		for(Map map : portletIdMap) {
			String value = (String) map.get(params.get("field"));
			map.put(params.get("field"), getMD5Str(value));
		}
		return portletIdMap;
	}
	
	private String getMD5Str(String src) {
		return DigestUtils.md5Hex(src);
	}
	
	public String getPortletId(WPSConnectionInfo connInfo) {
		try {
			portletIdMap = null;
			List<Map> data = getPortletIdMap(connInfo);
			return concateJdbcResult(data, "wps.portlet.portletId");
		}catch(Exception ex) {
			log.debug("getPortletId failed", ex);
			return " ";
		}
	}
	
	public String getPortletName(WPSConnectionInfo connInfo) {
		try {
			List<Map> data = (List<Map>) getByCmdId("wps.portlet.name", connInfo);
			return concateJdbcResult(data, "wps.portlet.name");
		}catch(Exception ex) {
			log.debug("getPortletName failed", ex);
			return " ";
		}
	}
	
	public String getPorletAppName(WPSConnectionInfo connInfo) {
		try {
			List<Map> data = (List<Map>) getByCmdId("wps.portlet.appname", connInfo);
			return concateJdbcResult(data, "wps.portlet.appname");
		}catch(Exception ex) {
			log.debug("getPorletAppName failed", ex);
			return "";
		}
	}
	
	public String getPorletCurErrorCount(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.portlet.currErrorCount", connInfo);
			List<Map> ret = (List<Map>) getByCmdId("wps.portlet.portletId", connInfo);		
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				key = getMD5Str(key);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getPorletCurErrorCount failed", ex);
			return "";
		}
	}
	
	public String getPorletResponseTime(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.portlet.responseTime", connInfo);
			List<Map> ret = (List<Map>) getByCmdId("wps.portlet.portletId", connInfo);		
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				key = getMD5Str(key);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getPorletResponseTime failed", ex);
			return "";
		}
	}
	
	public String getPorletCurRequestConut(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.portlet.currRequestCount", connInfo);
			List<Map> ret = (List<Map>) getByCmdId("wps.portlet.portletId", connInfo);		
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
			StringBuilder sb = new StringBuilder();
			for(Map map : ret) {
				String key = (String) map.get(params.get("index"));
				String proName = (String) map.get(params.get("field"));
				String value = obj.get(proName);
				key = getMD5Str(key);
				sb.append(key + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getPorletCurRequestConut failed", ex);
			return "";
		}
	}
	
	public String getAppId(WPSConnectionInfo connInfo) {
		try {
			List<Map> data = (List<Map>) getByCmdId("wps.webapp.j2eeWarName", connInfo);
			StringBuilder sb = new StringBuilder();
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
			for(Map map : data) {
				String key = (String) map.get(params.get("index"));
				String value = (String) map.get(params.get("field"));
				
				sb.append(getMD5Str(key) + "=" + getMD5Str(value) + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getAppId failed", ex);
			return "";
		}
		
	}
	
	public String getAppJ2EEName(WPSConnectionInfo connInfo) {
		try {
			List<Map> data = (List<Map>) getByCmdId("wps.webapp.j2eeWarName", connInfo);
			return concateJdbcResult(data, "wps.webapp.j2eeWarName");
		}catch(Exception ex) {
			log.debug("getAppJ2EEName failed", ex);
			return "";
		}
	}
	
	public String getAppAvailability(WPSConnectionInfo connInfo) {
		try {
			List<Map> data = (List<Map>) getByCmdId("wps.webapp.appAvailability", connInfo);
			StringBuilder sb = new StringBuilder();
			Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties("wps.portlet.portletId");
			for(Map map : data) {
				String j2eeName = (String) map.get(params.get("index"));
				sb.append(getMD5Str(j2eeName) + "=" + 1 + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getAppAvailability failed", ex);
			return "";
		}
	}
	
	public String getAppActiveSessionCount(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> data =  (Map<String, String>) getByCmdId("wps.webapp.activeSessionCount", connInfo);
			StringBuilder sb = new StringBuilder();
			for(String key : data.keySet()) {
				sb.append(getMD5Str(key) + "=" + data.get(key) + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getAppActiveSessionCount failed", ex);
			return "";
		}
	}
	
	public String getAppLiveSessionCount(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> data =  (Map<String, String>) getByCmdId("wps.webapp.liveSessionCount", connInfo);
			StringBuilder sb = new StringBuilder();
			for(String key : data.keySet()) {
				sb.append(getMD5Str(key) + "=" + data.get(key) + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getAppLiveSessionCount failed", ex);
			return "";
		}
	}
	
	public String getWebNonExistSessionCount(WPSConnectionInfo connInfo) {
		try {
			Map<String, String> obj = (Map<String, String>) getByCmdId("wps.webapp.noExistSessionCount", connInfo);
			StringBuilder sb = new StringBuilder();
			for(String key : obj.keySet()) {
				String value = obj.get(key);
				sb.append(getMD5Str(key) + "=" + value + "\n");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}catch(Exception ex) {
			log.debug("getWebNonExistSessionCount failed", ex);
			return "";
		}
	}
	
	public Object getByCmdId(String cmdId, WPSConnectionInfo connInfo) {
		Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties(cmdId);
		Statement serverStmt = StatementFactory.newInstance().newStatement(params.get("statement"), connInfo);
		serverStmt.init();
		return serverStmt.execute(params.get("operation"), params, params.get("subname"));	
	}
	
	private String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}
	
	private String concateJdbcResult(List<Map> data, String cmdId) {
		Map<String, String> params = CmdXMlHelper.getInstance().getCmdProperties(cmdId);
		StringBuilder sb = new StringBuilder();
		for(Map map : data) {
			String key = (String) map.get(params.get("index"));
			String value = (String) map.get(params.get("field"));
			if(cmdId.equals("wps.datasource.id")) {
				key = subStrJdbcDSId(key);
				value = subStrJdbcDSId(value);
			}else if(cmdId.startsWith("wps.datasource") || cmdId.startsWith("wps.jndi") || cmdId.startsWith("wps.provider")) {
				key = subStrJdbcDSId(key);
			} else if((cmdId.startsWith("wps.portlet") && !cmdId.equals("wps.portlet.portletId")) ||
					cmdId.startsWith("wps.webapp")) {
				key = getMD5Str(key);
			} 
			sb.append(key + "=" + value + "\n");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	private String subStrJdbcDSId(String id) {
		return id.substring(id.indexOf("#") + 1);
//		return "";
	}
	
	public boolean checkConnectInfo(WPSConnectionInfo connInfo) {
		String avail = getByCmdId("wps.serverStatus", connInfo).toString();
		return avail.equalsIgnoreCase("STARTED");
	}
	
	public static void main(String[] args) {
		WPSCollector col = new WPSCollector();
		WPSConnectionInfo connInfo = new WPSConnectionInfo("172.16.7.60", 10033, "wasadmin", "password");
		connInfo.setEnableSecurity(true);
		connInfo.setWasServerType("BASE");
		long start = System.currentTimeMillis();
		System.out.println(col.getAvailability(connInfo));
//		System.out.println(col.getJVMMEMRate(connInfo));
//		System.out.println(col.getTotalJvmMemSize(connInfo));
//		System.out.println(col.getUsedJvmMemSize(connInfo));
//		System.out.println(col.getAvgWaitTime(connInfo));
//		System.out.println("max jvm:" + col.getMaxJvmMemSize(connInfo));
//		System.out.println("min jvm:" + col.getMinJvmMemSize(connInfo));
//		System.out.println(col.getNumberOfWebApplication(connInfo));
//		System.out.println(col.getResDisplayName(connInfo));
//		System.out.println(col.getServerName(connInfo));
//		System.out.println(col.getSystemUpTime(connInfo));
//		System.out.println(col.getLdapPort(connInfo));
//		System.out.println("ldap ip:"+col.getLdapIp(connInfo));
//		System.out.println(col.getWasVersion(connInfo));
//		System.out.println(col.getHome(connInfo));
//		System.out.println(col.getCellName(connInfo));
//		System.out.println(col.getNodeName(connInfo));
//		System.out.println(col.getPid(connInfo));
//		System.out.println(col.getOnlineUserCount(connInfo));
//		System.out.println(col.getCurConnUserCount(connInfo));
		long end = System.currentTimeMillis();
		System.out.println("time cost=" + ((end-start)/1000));
//		System.out.println("----------");
//		System.out.println(col.getJdbcJndiNames(connInfo));
//		System.out.println(col.getJdbcMaxUtilRatio(connInfo));
//		System.out.println(col.getJdbcAvgConnWaitTime(connInfo));
//		System.out.println(col.getJdbcAvailability(connInfo));
//		System.out.println(col.getJdbcMaxConnCount(connInfo));
//		System.out.println(col.getJdbcProviderName(connInfo));
//		System.out.println(col.getJdbcMinConnCount(connInfo));
//		System.out.println(col.getJdbcPoolType(connInfo));
//		System.out.println(col.getJdbcFreePoolSize(connInfo));
//		System.out.println(col.getJdbcFreePoolSize(connInfo));
//		System.out.println(col.getJdbcConnPoolSize(connInfo));
//		System.out.println(col.getJDBCUsedUtilRatio(connInfo));
		start = System.currentTimeMillis();
		System.out.println("time cost=" + ((start-end)/1000));
		System.out.println("----------");
//		System.out.println(col.getPortletId(connInfo));
//		System.out.println(col.getPortletName(connInfo));
//		System.out.println(col.getPorletCurErrorCount(connInfo));
//		System.out.println(col.getPorletAppName(connInfo));
//		System.out.println(col.getPorletResponseTime(connInfo));
//		System.out.println(col.getPorletCurRequestConut(connInfo));
//		System.out.println(col.getNumberOfWebApplication(connInfo));
		System.out.println(col.getAppId(connInfo));
		System.out.println(col.getAppJ2EEName(connInfo));
		System.out.println(col.getAppAvailability(connInfo));
		System.out.println(col.getAppActiveSessionCount(connInfo));
		System.out.println(col.getAppLiveSessionCount(connInfo));
		end = System.currentTimeMillis();
		System.out.println("time cost=" + ((end-start)/1000));
	}
}
