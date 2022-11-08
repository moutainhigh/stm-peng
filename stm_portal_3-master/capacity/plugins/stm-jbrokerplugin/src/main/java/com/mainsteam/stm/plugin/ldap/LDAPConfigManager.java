package com.mainsteam.stm.plugin.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author xiaop_000 解析cmd（LDAP）配置文件，并放入缓存。使用时从map中取得cn及attribute
 */
public final class LDAPConfigManager {

	// private final String ROOT = "cmds"; //xml中根节点

	private static final String LDAP_DN_XML = "com.mainsteam.stm/plugin/ldap/ldap_dn.xml";

	private final String LDAP = "ldap"; // ldap服务器节点

	private final String LDAP_TYPE = "type"; // ldap服务器类型，例如IBM，SUN

	private final String CMD = "cmd"; // 对应xml文件中的cmd节点

	private static final Map<String, Map<String, String[]>> CMDS = new ConcurrentHashMap<String, Map<String, String[]>>();

	private final String ID = "id"; // 每条命令的id

	private final String DN = "dn";// property中的dn属性

	private final String ATTRIBUTE = "attribute";// property中的attribute属性

	private static final Log logger = LogFactory
			.getLog(LDAPConfigManager.class);

	/**
	 * xPath解析xml
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void parseCmdXml() {
		SAXReader reader = new SAXReader();
		try {

			InputStream in = getClass().getClassLoader().getResourceAsStream(
					LDAP_DN_XML);

			Document doc = reader.read(in);
			if (doc != null) {
				String typeXpath = "//" + LDAP; // LDAP 服务器类型
				List<Element> typeList = doc.selectNodes(typeXpath);
				if (typeList != null && typeList.size() > 0) {
					CMDS.clear();
					for (Element e : typeList) {
						Map<String, String[]> map = new HashMap<String, String[]>();
						List<Element> cmdList = e.selectNodes(CMD);
						if (cmdList != null && cmdList.size() > 0) {
							for (Element cmd : cmdList) {
								String cmdId = cmd.attributeValue(ID);
								String[] dnStr = { cmd.attributeValue(DN),
										cmd.attributeValue(ATTRIBUTE) };
								map.put(cmdId, dnStr); // 组装完成一个LDAP服务器类型的一个DN
							}
						}
						CMDS.put(e.attributeValue(LDAP_TYPE), map);
					}
				}
			}
		} catch (DocumentException e) {
			logger.error(e);
		}
	}

	/**
	 * 获取所有命令.
	 * 
	 * @return - Map<String, Map<String, String[]>>
	 */
	public static Map<String, Map<String, String[]>> getCmds() {
		if (CMDS.size() == 0) {
			new LDAPConfigManager().parseCmdXml();
		}
		return CMDS;
	}

	/**
	 * {method description}.
	 * 
	 * @param pluginId
	 *            pluginId, 根据LDAPtype的类型返回相应的命令
	 * @return Map<String, String[]>
	 */
	public static Map<String, String[]> getCmds(final String ldapType) {
		if (StringUtils.isBlank(ldapType)) {
			return null;
		}
		if (CMDS.size() == 0) {
			new LDAPConfigManager().parseCmdXml();
		}
		return CMDS.get(ldapType);
	}

	public static void main(String[] args) {
		LDAPConfigManager config = new LDAPConfigManager();
		config.parseCmdXml();

		if (CMDS != null) {
			Set<String> typeSet = CMDS.keySet();
			if (typeSet != null) {
				Iterator<String> iterator = typeSet.iterator();
				while (iterator.hasNext()) {
					Object type = iterator.next();
					System.out.println(type.toString());

					Map<String, String[]> cmdMap = CMDS.get(type);
					Set<String> cmdSet = cmdMap.keySet();
					Iterator<String> cmdIterator = cmdSet.iterator();
					while (cmdIterator.hasNext()) {
						Object cmd = cmdIterator.next();
						String[] strs = cmdMap.get(cmd);
						System.out.println(strs.length);
						for (String str : strs) {
							System.out.println(str);
						}
					}
				}
			}
		}
	}

}
