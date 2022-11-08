/**
 * 
 */
package com.mainsteam.stm.plugin.weblogic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class WebAppResCollector {
	private static final Log debug = LogFactory.getLog(WebAppResCollector.class);
	public static final String[] WEBAPP_TYPES={"", "WebAppComponentRuntime", ""};
	public static final String[] SERVLETS_TYPES={"", "WebAppComponentRuntime", "", ""};
	/**
	 * 会话上下文
	 * @param oneobj
	 * @return
	 */
	public static String getContextRoot(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#ContextRoot", WEBAPP_TYPES);
	}
	/**
	 * Web应用状态
	 * @param oneobj
	 * @return  有DEPLOYED, UNDEPLOYED, ERROR 3种状态
	 */
	public static String getWebAppStatus(JBrokerParameter oneobj) {
		String result = WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Status", WEBAPP_TYPES);
		return result;
	}
	/**
	 * 当前打开的Session数
	 * @param oneobj
	 * @return
	 */
	public static String getMaxSessionCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionsOpenedTotalCount", WEBAPP_TYPES);
	}
	/**
	 * Servlet个数
	 * @param oneobj
	 * @return
	 */
	public static String getServletCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Servlets", WEBAPP_TYPES);
	}
	/**
	 * Cookie生命周期
	 * @param oneobj
	 * @return
	 */
	public static String getSessionCookieMaxAgeSecs(JBrokerParameter oneobj) {
		String result = WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionTimeoutSecs", WEBAPP_TYPES);
		return result;
	}
	/**
	 * 当前活动的Session数
	 * @param oneobj
	 * @return
	 */
	public static String getCurActiveSessionCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#OpenSessionsCurrentCount", WEBAPP_TYPES);
	}
	/**
	 * Cookie域
	 * @param oneobj
	 * @return
	 */
	public static String getSessionCookieDomain(JBrokerParameter oneobj) {
		String result = WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionCookieDomain", WEBAPP_TYPES);
		return result;
	}
	/**
	 * ID
	 * @param oneobj
	 * @return
	 */
	public static String getWebAppId(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#ComponentName", WEBAPP_TYPES);
	}
	/**
	 * 应用名称
	 * @param oneobj
	 * @return
	 */
	public static String getWebAppName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Name", WEBAPP_TYPES);
	}
	/**
	 * 获取webapp Kpiid
	 * @param oneobj
	 * @return
	 */
	public static String getWebAppKpiid(JBrokerParameter oneobj){
		String nameStr = WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Name", WEBAPP_TYPES);
		String[] names = nameStr.split("\\n");
		StringBuffer sb = new StringBuffer();
		if(names != null){
			for(String name:names){
				try{
					sb.append(name.split("=")[0]);
					sb.append("\n");
				}catch(Exception e){
					debug.debug("get webapp kpiid exception:", e);
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 当前总会话数
	 * @param oneobj
	 * @return
	 */
	public static String getTotalSessionCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionsOpenedTotalCount", WEBAPP_TYPES);
	}
	/**
	 * Cookie路径
	 * @param oneobj
	 * @return
	 */
	public static String getSessionCookiePath(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionCookiePath", WEBAPP_TYPES);
	}
	/**
	 * 会话数最高值
	 * @param oneobj
	 * @return
	 */
	public static String getHighestSessionCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#OpenSessionsHighCount", WEBAPP_TYPES);
	}
	/**
	 * Cookie名称
	 * @param oneobj
	 * @return
	 */
	public static String getSessionCookieName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#SessionCookieName", WEBAPP_TYPES);
	}
	/**
	 * servlet名称
	 * @param oneobj
	 * @return
	 */
	public static String getServletName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Servlets@#ServletName", SERVLETS_TYPES);
	}
	/**
	 * servlet名称
	 * @param oneobj
	 * @return
	 */
	public static String getServletKpiid(JBrokerParameter oneobj) {
		String nameStr = WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Servlets@#Name", SERVLETS_TYPES);
		String[] names = nameStr.split("\\n");
		StringBuffer sb = new StringBuffer();
		if(names != null){
			for(String name:names){
				try{
					sb.append(name.split("=")[0]);
					sb.append("\n");
				}catch(Exception e){
					debug.debug("get webapp kpiid exception:", e);
				}
			}
		}
		return sb.toString();
	}
	/**
	 * servlet的上下文
	 * @param oneobj
	 * @return
	 */
	public static String getContextPath(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Servlets@#ContextPath", SERVLETS_TYPES);
	}
	/**
	 * Servlet路径
	 * @param oneobj
	 * @return
	 */
	public static String getServletPath(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getWebAppValues(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#Servlets@#ServletPath", SERVLETS_TYPES);
	}
}
