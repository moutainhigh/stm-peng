package com.mainsteam.stm.plugin.ldap;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv3;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

/**
 * @author xiaop_000
 * LDAPQuery <br>
 * 用于LDAP查询
 */
public abstract class LDAPQuery {

	private static final String AVAILABLE = "1";
	private static final String UNAVAILABLE = "0";
	private static final Log logger = LogFactory.getLog(LDAPQuery.class);

	/**
	 * query according the ACI.
	 * @param connInfo 连接信息
	 * @param dn LDAP别名（路径）
	 *            -such as, cn=monitor
	 * @param attrs 查找包含的属性集
	 *            attrs
	 * @param attr 需要查找的属性
	 * @return result
	 */
	public static String query(final LDAPConnection t_conn,
			final String dn, final String[] attrs, final String attr) throws Exception {
		
		if(t_conn == null)
			throw new NullPointerException("LDAPConnection连接为空...");

		String attrValues = "";
		try {
			final LDAPSearchResults t_res = t_conn.search(dn, LDAPv3.SCOPE_BASE, "objectclass=*", attrs, false);

			/* Iterate through the results and put into list */
			while (t_res.hasMoreElements()) {
					/* Get the next directory entry. */
					LDAPEntry foundEntry = null;
					try {
						foundEntry = t_res.next();
					} catch (final LDAPReferralException t_e) {
						logger.warn("LDAPReferralException: " + t_e.getMessage(), t_e);
						continue;
					} catch (final LDAPException t_e2) {
						logger.warn( "LDAPException: " + t_e2.getMessage(), t_e2);
						continue;
					}
					LDAPAttribute attribute = foundEntry.getAttribute(attr);
					String[] attributes = attribute.getStringValueArray();
					
					for(int i = 0; i < attributes.length; i++){
						attrValues += attributes[i];
						if(i != attributes.length-1)
							attrValues += ";";
					}
					
				}
		} catch (final Throwable t_e) {
			logger.error("ERR_PLUGIN_LDAP_CONNECTION_QUERY_ERROR" + "\n" + dn
					+ ">>>" + Arrays.toString(attrs), t_e);
			throw new Exception(t_e);
		} 
		return attrValues;
	}
	
	/**
	 * 测试Directory Server 服务可用性。如果可连接则表明可用，否则不可用
	 * @param cmd
	 * @param username
	 * @param password
	 * @param host
	 * @param port
	 * @return
	 */
	protected String isAvailable(JBrokerParameter parameter){
		LDAPConnection t_conn = new LDAPConnection();
		/* Connect to the server. */
		try {
			t_conn.connect(parameter.getIp(), parameter.getPort());
			LDAPSearchConstraints t_constraints = t_conn.getSearchConstraints();
			t_constraints.setTimeLimit(parameter.getTimeout());
			t_conn.setSearchConstraints(t_conn.getSearchConstraints());
			t_conn.authenticate(parameter.getUsername(), parameter.getPassword());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.warn(e);
			return UNAVAILABLE;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			logger.warn(e);
			return UNAVAILABLE;
		} finally{
			try {
				t_conn.disconnect();
			} catch (LDAPException e) {
				// TODO Auto-generated catch block
				logger.warn(e);
			}finally{
				t_conn = null;
			}
		}
		return AVAILABLE;
	}
	
}
