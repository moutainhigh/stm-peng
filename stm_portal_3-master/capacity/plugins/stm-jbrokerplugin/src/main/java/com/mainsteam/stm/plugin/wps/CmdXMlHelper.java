package com.mainsteam.stm.plugin.wps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CmdXMlHelper {
	private static CmdXMlHelper instance;
	private CmdXMlHelper() {
		init();
	}
	
	public synchronized static CmdXMlHelper getInstance() {
		if(instance == null) {
			instance = new CmdXMlHelper();
		}
		return instance;
	}
	
	public void init() {
		try {
			Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(CmdXMlHelper.class.getResourceAsStream("cmds.xml"));
			Element root = doc.getRootElement();
			List<Element> children = root.getChildren();
			for(Element elm : children) {
				String id = elm.getAttributeValue("id");
				Map<String, String> props = new HashMap<String, String>();
				result.put(id, props);
				props.put("cmdid", id);
				Element propsElem = elm.getChild("properties");
				List<Element> propChildren = propsElem.getChildren();
				for(Element e : propChildren) {
					String name = e.getAttributeValue("name");
					StringBuilder sb = new StringBuilder();
					for(Content con : (List<Content>)e.getContent()) {
						sb.append(con.getValue());
					}
					String value = sb.toString().trim();
					props.put(name, value);
				}
			}
			cmds = result;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Map<String, String>> cmds; 
	
	public Map<String, Map<String, String>> getCmds() {
		return cmds;
	}
	
	public Map<String, String> getCmdProperties(String cmdId) {
		return cmds.get(cmdId);
	}
}
