package com.mainsteam.stm.portal.report.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.XMLWriter;

import com.mainsteam.stm.util.FileUtil;

public class ReportModelUtil {
	
	private static final Logger log = Logger.getLogger(ReportModelUtil.class);
	
	/**
	 * 创建一个模版
	 * 
	 * @param document
	 * @param reportName
	 * @param width
	 * @param height
	 * @param margin
	 * @return
	 */
	public static Element createReport(Document document, String reportName,
			int width, int height, int margin) {
		Element root = document.addElement("jasperReport",
				"http://jasperreports.sourceforge.net/jasperreports");
		root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addAttribute(
				"xsi:schemaLocation",
				"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd");
		root.addAttribute("name", reportName);
		root.addAttribute("language", "groovy");
		root.addAttribute("pageWidth", String.valueOf(width));
		root.addAttribute("pageHeight", String.valueOf(height));
		root.addAttribute("orientation", "Landscape");
		root.addAttribute("columnWidth", String.valueOf(width - (margin * 2)));
		root.addAttribute("leftMargin", String.valueOf(margin));
		root.addAttribute("rightMargin", String.valueOf(margin));
		root.addAttribute("topMargin", String.valueOf(margin));
		root.addAttribute("bottomMargin", String.valueOf(margin));
		root.addAttribute("uuid", getUUID());
		return root;
	}

	/**
	 * 新增一个parameter
	 * 
	 * @param document
	 * @param name
	 * @param clazz
	 * @return
	 */
	public static Element addParameter(Document document, String name, String clazz) {
		Element root = document.getRootElement();
		List<Element> elements = root.elements();
		Element element = DocumentHelper.createElement(QName.get("parameter", root.getNamespace()));
		element.addAttribute("name", name);
		element.addAttribute("class", clazz);
		elements.add(0, element);
		return element;
	}
	
	/**
	 * 新增一个field
	 * 
	 * @param document
	 * @param name
	 * @param clazz
	 * @return
	 */
	public static Element addField(Document document, String name, String clazz, String bandName) {
		Element root = document.getRootElement();
		List<Element> elements = root.elements();
		Element elem = DocumentHelper.createElement(QName.get("field", root.getNamespace()));
		elem.addAttribute("name", name);
		elem.addAttribute("class", clazz);
		int index = 0;
		for(Element element : elements){
			if(bandName.equals(element.getName())){
				elements.add(index, elem);
				break;
			}
			index++;
		}
		return elem;
	}
	/**
	 * 获取报表当前宽度
	 * 
	 * @param document
	 * @return
	 */
	public static int getRepCurWidth(Document document) {
		Element root = document.getRootElement();
		return Integer.valueOf(root.attributeValue("columnWidth"));
	}

	/**
	 * 增加报表高度
	 * 
	 * @param document
	 * @param height
	 */
	public static void addRepHeight(Document document, Element band, int height) {
		Element root = document.getRootElement();
		String pageHeight = root.attributeValue("pageHeight");
		root.attribute("pageHeight").setValue(String.valueOf(Integer.valueOf(pageHeight) + height));
		band.attribute("height").setValue(String.valueOf(height));
	}

	public static Element findDetailElement(Element root){
		return root.element("detail");
	}
	
	public static Element addBorderElement(Element parentElement, double d){
		String line = String.valueOf(d);
		Element box = parentElement.addElement("box");
		box.addElement("pen").addAttribute("lineWidth", line);
		box.addElement("topPen").addAttribute("lineWidth", line);
		box.addElement("leftPen").addAttribute("lineWidth", line);
		box.addElement("bottomPen").addAttribute("lineWidth", line);
		box.addElement("rightPen").addAttribute("lineWidth", line);
		return box;
	}
	
	/**
	 * 生成UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	public static String createNewReportName(int counter){
		return "v" + counter;
	}
	/**
	 * 动态创建一个javabean
	 * 
	 * @param beanName
	 * @param propMap
	 * @return
	 */
	public static DynaClass createJavaBean(String beanName, Map<String, Class> propMap) {
		Set<String> set = propMap.keySet();
		Iterator<String> iter = set.iterator();
		int index = 0;
		DynaProperty[] pros = new DynaProperty[propMap.size()];
		while(iter.hasNext()){
			String key = iter.next();
			pros[index++] = new DynaProperty(key, propMap.get(key));
		}
		DynaClass dynaC = new BasicDynaClass(beanName, null, pros);
		return dynaC;
	}

	public static int compile(InputStream jrxmlIn, OutputStream jasperOut) {
		int resultCode = 0;
		try {
			JasperCompileManager.compileReportToStream(jrxmlIn, jasperOut);
		} catch (JRException e) {
			resultCode = 1;
			e.printStackTrace();
		}
		return resultCode;
	}
	public static int compile(String jrxmlFile, String jasperFile) {
		int resultCode = 0;
		try {
			JasperCompileManager.compileReportToFile(jrxmlFile, jasperFile);
		} catch (JRException e) {
			resultCode = 1;
			e.printStackTrace();
		}
		return resultCode;
	}
	
	/**
	 * 写出所有jrxml
	 * 
	 * @param subReportDoc
	 */
	public static File writeJrxmlFile(Document document, String userId){
		File file = null;
		try {
			String fileName = document.getRootElement().attributeValue("name");
			file = createTempFile(userId, fileName);
			XMLWriter writer = new XMLWriter(new FileOutputStream(file));
			writer.setEscapeText(false);
			writer.write(document);  
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static File createTempFile(String userId, String fileName){
		File file = null;
		// 创建文件夹
		try {
			String classPath = ReportModelUtil.class.getName().replace(".", File.separator);
			String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			filePath = filePath + classPath + File.separator + userId + File.separator + fileName;
			filePath = URLDecoder.decode(filePath, "UTF-8");
			// 创建文件
			file = new File(filePath);
			if(!file.exists())
				FileUtil.createFile(file);
		} catch (IOException e) {
			log.error("create template file exception", e);
		}
		return file;
	}
}
