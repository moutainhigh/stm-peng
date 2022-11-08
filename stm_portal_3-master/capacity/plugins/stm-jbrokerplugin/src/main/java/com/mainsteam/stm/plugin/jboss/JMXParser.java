package com.mainsteam.stm.plugin.jboss;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class JMXParser {
	/**
	 *String[]包含jmx查询需要的ObjectName及attribute。 
	 *String为每个jmx标签的id
	 */
	private static Map<String, String[]> jmxMap = new ConcurrentHashMap<String, String[]>();
	private Log log=LogFactory.getLog(JMXParser.class);
	public JMXParser(){
		if(jmxMap == null || jmxMap.size() < 1)
			parseXml();
	}
	
	private void parseXml() {
		//String filePath = SysFilePath.getInstance().getFullFileName("deploy/pal/itm-pal-appmdl.par/ldap_dn.xml");
		SAXReader reader = new SAXReader();
		Document doc;
		try { 
			InputStream in = getClass().getClassLoader().getResourceAsStream("com.mainsteam.stm/plugin/jboss/jboss_jmx.xml");
			doc = reader.read(in);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> children = root.elements();
			if(children != null && children.size() > 0){
				jmxMap.clear();
				for(Element elm : children) {
					String[] str = {elm.attributeValue("objectName"), elm.attributeValue("attribute")};
					jmxMap.put(elm.attributeValue("id"), str);
					
				}
			}
		} catch (DocumentException e) {
			log.error(e.getMessage(), e);
		} 
		
    }
	
	public Map<String, String[]> getJmxMap(){
		return jmxMap;
	}

}
