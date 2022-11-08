package com.mainsteam.stm.capvalidate.endecoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

/**
 * Java递归遍历XML所有元素
 * 
 * @author Administrator
 * @version 3, Nov 13, 2014]
 * @see
 * @since OC 4.x
 */
public class XmlParser {
	// private static Map xmlmap = new HashMap();
	// 存储xml元素信息的容器
	private static ArrayList<Leaf> elemList = new ArrayList<Leaf>();

	public static void main(String args[]) throws DocumentException {
		XmlParser test = new XmlParser();
		String path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs/resources/application/exchange2007/collect.xml";
		// 读取XML文件
		SAXReader reader = new SAXReader();
		Document doc = reader.read(path);
		// 获取XML根元素
		Element root = doc.getRootElement();
		test.getElementList(root);
		String x = test.getListString(elemList);
		System.out.println("-----------解析结果------------\n" + x);
	}

	/**
	 * 获取节点所有属性值 <功能详细描述>
	 * 
	 * @param element
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String getNoteAttribute(Element element) {
		String xattribute = "";
		DefaultAttribute e = null;
		@SuppressWarnings("unchecked")
		List<DefaultAttribute> list = element.attributes();
		for (int i = 0; i < list.size(); i++) {
			e = (DefaultAttribute) list.get(i);

			if (e.getName().equals("value")) {
				System.err.println("name = " + e.getName() + ", value = "
						+ e.getText());
			}

			xattribute += " [name = " + e.getName() + ", value = "
					+ e.getText() + "]";
		}
		return xattribute;
	}

	/**
	 * 递归遍历方法 <功能详细描述>
	 * 
	 * @param element
	 * @see [类、类#方法、类#成员]
	 */
	public void getElementList(Element element) {
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		// 没有子元素
		if (elements.isEmpty()) {
			String xpath = element.getPath();
			String value = element.getTextTrim();
			elemList.add(new Leaf(getNoteAttribute(element), xpath, value));
		} else {
			// 有子元素
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element elem = (Element) it.next();
				// 递归遍历
				getElementList(elem);
			}
		}
	}

	public String getListString(List<Leaf> elemList) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<Leaf> it = elemList.iterator(); it.hasNext();) {
			Leaf leaf = (Leaf) it.next();
			sb.append("xpath: " + leaf.getXpath()).append(", value: ")
					.append(leaf.getValue());
			if (!"".equals(leaf.getXattribute())) {
				sb.append(", Attribute: ").append(leaf.getXattribute());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}

/**
 * xml节点数据结构
 */
class Leaf {
	// 节点属性
	private String xattribute;

	// 节点PATH
	private String xpath;

	// 节点值
	private String value;

	public Leaf(String xattribute, String xpath, String value) {
		this.xattribute = xattribute;
		this.xpath = xpath;
		this.value = value;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getXattribute() {
		return xattribute;
	}

	public void setXattribute(String xattribute) {
		this.xattribute = xattribute;
	}
}
