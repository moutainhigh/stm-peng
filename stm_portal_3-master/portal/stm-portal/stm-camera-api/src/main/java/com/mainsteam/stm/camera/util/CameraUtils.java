package com.mainsteam.stm.camera.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CameraUtils {

	private static Logger logger = Logger.getLogger(CameraUtils.class);

	/**
	 * 通过XML的节点，标签名称得到标签的值
	 * @param elements
	 * @param key
	 * @return
	 */
	public static String getValueFromXML(List<Element> elements, String key) {
		String value = null;
		for (int i = 0; i < elements.size(); i++) {
			Element element = (Element) elements.get(i);
			String name = element.getName();
			value = element.getText();
			if (name.equals(key)) {
				return value;
			}
		}
		return value;
	}

	/**
	 * 得到file文件的节点
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getListFromXML(String file) {
		List<Element> elements = null;
		String fileName = System.getProperty("catalina.home") + File.separator + "common" + File.separator + "classes"
				+ File.separator + "config" + File.separator + file;
		logger.info("资源文件路径是"+fileName);
		try {
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(fileName);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);
			Element root = document.getRootElement();
			elements = root.elements();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取XML信息失败", e);
		}
		return elements;
	}

}
