package com.mainsteam.stm.capvalidate.endecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.mainsteam.stm.capbase.TextUtil;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.util.FileUtil;

public class CapEncoder {

	private static final String TRUE = "TRUE";

	// private static final Log logger = LogFactory.getLog(CapEncoder.class);

	private static final Set<String> skipKeys = new HashSet<String>();
	// <Parameter type="" key="startmode"
	// value="Manual,手动;Auto,自动;Disabled,禁用"/>
	// <Parameter type="" key="startname"
	// value="localSystem,本地系统;LocalSystem,本地系统;NT
	// AUTHORITY\LocalService,本地服务;NT Authority\LocalService,本地服务;NT
	// AUTHORITY\networkService,网络服务;NT AUTHORITY\NetworkService,网络服务;NT
	// Authority\NetworkService,网络服务"/>
	private static final Set<String> skipMetricIds = new HashSet<String>();
	static {
		skipMetricIds.add("name");
		skipMetricIds.add("description");
		skipMetricIds.add("unit");
		skipMetricIds.add("operator");
		skipMetricIds.add("expression");
	}
	static {
		skipKeys.add("startmode");
		skipKeys.add("startname");
		// skipKeys.add("column2");
	}

	public static void encoderFile(File file, String toPath) {
		boolean isCollect = false;
		boolean isResource = false;
		if (file.getName().contains("collect")) {
			isCollect = true;
		}
		if (file.getName().contains("resource")) {
			isResource = true;
		}
		if (!isResource && !isCollect) {
			return;
		}

		SAXReader reader = new SAXReader();
		Document doc = null;
		String fn = file.getPath();
		try {
			InputStream ifile = new FileInputStream(fn);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			doc = reader.read(ir);
		} catch (FileNotFoundException e) {
			System.err.println(fn);
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			System.err.println(fn);
			e.printStackTrace();
			return;
		} catch (DocumentException e) {
			System.err.println(fn);
			e.printStackTrace();
			return;
		} catch (Exception e) {
			System.err.println(fn);
			e.printStackTrace();
			return;
		}
		Element root = doc.getRootElement();// 得到根节点

		// SAXReader saxReader = new SAXReader();
		// Document doc = null;
		// try {
		// doc = saxReader.read(file);
		// } catch (DocumentException e) {
		// logger.error(e.getMessage(), e);
		// return;
		// }
		// Element root = doc.getRootElement();
		Element settingElement = root.element("GlobalMetricSetting");
		Attribute isEncrypt = settingElement
				.attribute(CapacityConst.IS_ENCRYPT);
		if (null == isEncrypt) {
			settingElement.addAttribute(CapacityConst.IS_ENCRYPT, TRUE);
		} else {
			if (!TRUE.equalsIgnoreCase(isEncrypt.getText())) {
				isEncrypt.setText(TRUE);
			}
		}
		if (isResource) {
			@SuppressWarnings("unchecked")
			List<Element> childElements = root
					.elements(CapacityConst.TAG_RESOURCE);
			for (Element childElement : childElements) {
				encoderEntry(childElement);
			}
		}
		if (isCollect) {
			@SuppressWarnings("unchecked")
			List<Element> childElements = root
					.elements(CapacityConst.TAG_METRIC_PLUGIN);
			for (Element childElement : childElements) {
				encoderEntry(childElement);
			}
		}
		String fullPath = file.getPath();
		int lastIndex = fullPath.lastIndexOf(File.separator);
		lastIndex = fullPath.lastIndexOf(File.separator, lastIndex - 1);
		lastIndex = fullPath.lastIndexOf(File.separator, lastIndex - 1);
		String substring = fullPath.substring(lastIndex);
		String toFilePath = toPath + substring;
		writeXmlFile(new File(toFilePath), doc);
		System.out.println("from:" + file.getPath() + "\r\nto:" + toFilePath);
	}

	private static void writeXmlFile(File file, Document doc) {
		try {
			FileUtil.createFile(file);
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			OutputFormat of = new OutputFormat();
			of.setEncoding("UTF-8");
			of.setIndent(true);
			of.setIndent("    ");
			of.setNewlines(true);
			XMLWriter writer = new XMLWriter(osw, of);
			writer.setEscapeText(false);// cdata
			writer.write(doc);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void encoderEntry(Element elem) {
		boolean isPara = false;
		if ("Parameter".equals(elem.getName())) {
			isPara = true;
		}

		boolean isSkip = false;
		if (isPara) {
//			String keyString = elem.attributeValue("key");
//			if (skipKeys.contains(keyString)) {
//				isSkip = true;
//			}
			if(StringUtils.equals(elem.getParent().getName(), "PluginDataHandler") 
					|| StringUtils.equals(elem.getParent().getName(), "PluginDataConverter")) {
				isSkip = true;
			}
		}
		if(StringUtils.equals(elem.getName(), "PluginResultMetaInfo"))
			isSkip = true;

		@SuppressWarnings("unchecked")
		List<Attribute> attrs = elem.attributes();
		if (null != attrs) {
			for (Attribute attr : attrs) {
				String val = attr.getValue();
				if (isSkip && ("value".equals(attr.getName()) || "columns".equals(attr.getName()))) {
					System.err.println(val);
				} else {

					if (skipMetricIds.contains(attr.getName())) {
						// System.err.println(val);
					} else {
						elem.getName();
						attr.setValue(TextUtil.encrypt(val,
								CapacityConst.ENDECODER_KEY));
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		List<Element> childElements = elem.elements();
		if (null == childElements || childElements.isEmpty()) {
			String text = elem.getText();
			if (null != text && !text.isEmpty()) {
				elem.setText(TextUtil
						.encrypt(text, CapacityConst.ENDECODER_KEY));
			}
		} else {
			for (Element childElement : childElements) {
				encoderEntry(childElement);
			}
		}
		return;
	}

	public static void main1(String[] argv) {
		String toPath = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs/resources";
		File collectFile = new File(
				"/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/tools/4.1docs/host/linux/collect.xml");
		encoderFile(collectFile, toPath);
	}

	public static void main(String[] argv) {
		// String oc4Path = "E:\\oc\\Capacity\\cap_libs";
		// String username = System.getProperty("user.name");
		// if ("sunsht".equals(username)) {
		// oc4Path =
		// "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
		// }
		// System.setProperty("caplibs.path", oc4Path);
		//
		// String caplibsPath = OSUtil.getEnv("caplibs.path");
		//
		// if (!caplibsPath.endsWith(File.separator)) {
		// caplibsPath += File.separator;
		// }
		// String caplibsPathWiserv = caplibsPath + "resources" +
		// File.separator;
		// 加载collect和resource
		if (argv.length < 2) {
			System.out.println("please input fromPath toPath,example:");
			System.out
					.println("/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/tools/4.1docs  /Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs/resources");
			System.exit(0);
		}
		String fromPath = argv[0];
		String toPath = argv[1];
		String[] extensions = new String[] { "xml" };
		List<File> collectFiles = (List<File>) FileUtils.listFiles(new File(
				fromPath), extensions, true);

		for (File collectFile : collectFiles) {
			encoderFile(collectFile, toPath);
		}
	}
}
