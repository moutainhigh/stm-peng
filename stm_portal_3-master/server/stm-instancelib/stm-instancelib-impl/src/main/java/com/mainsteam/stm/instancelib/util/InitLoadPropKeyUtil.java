package com.mainsteam.stm.instancelib.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class InitLoadPropKeyUtil {

	private static final Log logger = LogFactory
			.getLog(InitLoadPropKeyUtil.class);

	
	private static final String XMLPATH = "/config/initLoadResourceInstanceProp.xml";
	
	private Document document;
	
	public void start(){
		if (logger.isInfoEnabled()) {
			logger.info("start load initXmlPropkey start");
		}
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getResourceAsStream(XMLPATH);
			SAXReader reader = new SAXReader();
			document = reader.read(inputStream);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get initLoadResourceInstanceProp.xml error!", e);
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("start", e);
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("start load initXmlPropkey end");
		}
	}
	
	public List<String> getModuleKeys(){
		List<String> moduleProps = null;
		List<?> list = document.selectNodes("//module");  
		if(list != null && !list.isEmpty()){
			moduleProps = new ArrayList<String>(4);
			Iterator<?> iter=list.iterator(); 
			while(iter.hasNext()){
				Element element=(Element)iter.next();
				moduleProps.add(element.getText());
			}
		}
		return moduleProps;
	}
	
	public List<String> getDiscoverKeys(){
		List<String> discoverProps = null;
		List<?> list = document.selectNodes("//discover");
		if(list != null && !list.isEmpty()){
			discoverProps = new ArrayList<String>();
			Iterator<?> iter=list.iterator(); 
			while(iter.hasNext()){
				Element element=(Element)iter.next();
				discoverProps.add(element.getText());
			}
		}
		return discoverProps;
	}
}
