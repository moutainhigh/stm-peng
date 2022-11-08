package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <li>文件名称: XmlUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月25日
 * @author   ziwenwen
 */
public class XmlUtil {

	/**
	 * 解析器
	 */
	private static final SAXReader reader = new SAXReader();
	
	/**
	 * 方法用于读取配置文件，并获取根节点
	 * @param path
	 */
	public static Element getRootElement(final String filePath) {
		File file=new File(filePath);
		Element root=null;
    	try {
    		root=reader.read(file)//读取报表配置文件
    			.getRootElement();//获取配置文件根节点
		} catch (DocumentException e) {
			System.out.println("解析xml文件路径错误！");
			e.printStackTrace();
		}
		return root;
	}
	
	public static Element getRootElement(final InputStream in) {
		BufferedReader bin=new BufferedReader(new InputStreamReader(in));
		StringBuilder sb=new StringBuilder(IConstant.empty_);
		String str;
		try {
			while ((str=bin.readLine())!=null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return XmlUtil.getRootElementByText(sb.toString());
	}
	
	/**
	 * 方法用于根据xml字符串创建一个文档对象
	 * @param path
	 */
	public static Document getDocByText(final String xml) {
		Element rootElement=getRootElementByText(xml);
		Document doc=DocumentHelper.createDocument();
		doc.add(rootElement.detach());
		return doc;
	}
	
	/**
	 * 方法用于读取配置文件，并创建一个文档对象
	 * @param path
	 */
	public static Document getDoc(final String filePath) {
		File file=new File(filePath);
		Document doc=null;
    	try {
    		doc=reader.read(file);//读取报表配置文件
		} catch (DocumentException e) {
			System.out.println("解析xml文件路径错误！");
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * 将字符串解析为xml文档
	 * @param xmlString
	 * @return
	 */
	public static Element getRootElementByText(String xmlString){
		Element root=null;
		try {
			root=DocumentHelper.parseText(xmlString).getRootElement();
		} catch (DocumentException e) {
			System.out.println("解析xml字符串格式错误！");
			e.printStackTrace();
		}
		return root;
	}
	/**
	 * 根据指定的根节点名创建一个根节点
	 * @param rootName
	 * @return Element
	 */
	public static Element createRoot(String rootName){
		return DocumentHelper.createElement(rootName);
	}
	
	/**
	 * 清空子节点
	 */
	@SuppressWarnings("unchecked")
	public static Element empty(Element es){
		for(Element e:(List<Element>) es.elements()){
			es.remove(e);
		}
		return es;
	}
	
	/**
	 * 将e2中的所有子节点移动到e1中
	 */
	@SuppressWarnings("unchecked")
	public static void move(Element e1,Element e2){
		for(Element e:(List<Element>) e2.elements()){
			e1.add(e.detach());
		}
	}
	
	/**
	 * 将文档写入一个文件中
	 * @param doc 要写入的文档
	 * @param filePath 文件路径
	 */
	public static void writeToFile(Document doc,String filePath){
		OutputStreamWriter fw;
		try {
			fw = new OutputStreamWriter(new FileOutputStream(filePath),"utf-8");
			fw.write(doc.asXML());
			fw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


