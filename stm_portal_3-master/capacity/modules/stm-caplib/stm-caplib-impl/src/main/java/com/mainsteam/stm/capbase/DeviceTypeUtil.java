package com.mainsteam.stm.capbase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CaplibAPIErrorCode;
import com.mainsteam.stm.caplib.dict.DeviceTypeEnum;

/**
 * 用于解析category的xml文件，获得category的节点对应的对象，即一个节点对应一个实体对象，
 * 比如DeviceType节点对应DeviceType类。
 * 
 * @author Administrator
 * 
 */
public class DeviceTypeUtil {

	private static final Log logger = LogFactory.getLog(DeviceTypeUtil.class);
	/**
	 * 分类的上级id
	 */
	private static final String DeviceType = "DeviceType";

	/** 厂商ID */
	private static final String VendorId = "VendorId";
	/** 厂商名称 */
	private static final String VendorName = "VendorName";
	/** 厂商英文名 */
	private static final String VendorNameEn = "VendorNameEn";
	/** 厂商图标 */
	private static final String VendorIcon = "VendorIcon";

	/** 模型ID */
	private static final String ResourceId = "ResourceId";

	private static final String isSystem = "isSystem";

	private static final String SysOid = "SysOid";
	/** 系列 */
	private static final String Series = "Series";
	/** 型号 */
	private static final String ModelNumber = "ModelNumber";
	/** 主机用 */
	private static final String OsType = "OsType";
	/** 拓扑用 */
	private static final String Type = "Type";
	/** 排序顺序号 */
	private static final String SortId = "SortId";

	private static String deviceTypeFileName;

	public DeviceTypeUtil() {
	}

	/**
	 * 加载category的xml文件，因为xml的元素都是叶子节点，通过parentid来指定从属关系，所以只需要遍历根节点下的 所有子节点即可。
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 返回所有的分类
	 */
	public DeviceType[] loadDeviceType(String filePath) {

		try {
			// SAXReader saxReader = new SAXReader();
			// saxReader.setEncoding("UTF-8");
			// Document doc = saxReader.read(new File(filePath));
			// Element root = doc.getRootElement();

			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(filePath);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			@SuppressWarnings("unchecked")
			List<Element> catElementList = root.elements(DeviceType);
			DeviceType[] types = new DeviceType[catElementList.size()];
			for (int i = 0; i < types.length; i++) {
				types[i] = this.initDeviceType(catElementList.get(i));
			}
			DeviceTypeUtil.deviceTypeFileName = filePath;
			return types;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static CaplibAPIErrorCode addTypeToFile(DeviceType type) {
		try {

			String fullName = DeviceTypeUtil.deviceTypeFileName;
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(fullName);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			// 打开deviceTypeFileName，并写入
			Element elem = root.addElement(DeviceType);
			// type
			if (null != type.getType()) {
				elem.addAttribute(Type, type.getType().name());
			} else {
				elem.addAttribute(Type, DeviceTypeEnum.Other.name());
			}
			elem.addAttribute(VendorId, type.getVendorId());
			elem.addAttribute(VendorName, type.getVendorName());
			elem.addAttribute(VendorNameEn, type.getVendorNameEn());
			elem.addAttribute(VendorIcon, type.getVendorIcon());
			elem.addAttribute(ResourceId, type.getResourceId());
			elem.addAttribute(SysOid, type.getSysOid());
			elem.addAttribute(OsType, type.getOsType());
			elem.addAttribute(SortId, String.valueOf(type.getSortId()));
			elem.addAttribute(isSystem, "0");
			elem.addElement(Series).addCDATA(type.getSeries()); // "<![CDATA[" +
			elem.addElement(ModelNumber).addCDATA(type.getModelNumber());

			FileOutputStream fos = new FileOutputStream(fullName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			OutputFormat of = new OutputFormat();
			of.setEncoding("UTF-8");
			of.setIndent(true);
			of.setIndent("    ");
			of.setNewlines(true);
			XMLWriter writer = new XMLWriter(osw, of);
			writer.setEscapeText(false);// cdata
			writer.write(document);
			writer.close();
			return CaplibAPIErrorCode.OK;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
		}
		return CaplibAPIErrorCode.ADD_DEVICE_TYPE_03;
	}

	/**
	 * 初始化一个DeviceType对象，包括id，name，并将它放入id-parentid对应关系的map中
	 * 
	 * @param element
	 *            一个dom树元素
	 * @return 返回一个新的DeviceType对象
	 */
	private DeviceType initDeviceType(Element element) {
		DeviceType devType = new DeviceType();

		/** 厂商ID */
		devType.setVendorId(element.attributeValue(VendorId));
		/** 厂商名称 */
		devType.setVendorName(element.attributeValue(VendorName));
		/** 厂商英文名 */
		devType.setVendorNameEn(element.attributeValue(VendorNameEn));
		/** 厂商图标 */
		devType.setVendorIcon(element.attributeValue(VendorIcon));

		/** 模型ID */
		devType.setResourceId(element.attributeValue(ResourceId));

		devType.setSysOid(element.attributeValue(SysOid));
		/** 系列 */
		devType.setSeries((String) element.element(Series).getData());
		/** 型号 */
		devType.setModelNumber((String) element.element(ModelNumber).getData());
		/** 主机用 */
		devType.setOsType(element.attributeValue(OsType));
		/** 拓扑用 */
		String strType = element.attributeValue(Type);
		if (StringUtils.isNotEmpty(strType)) {
			DeviceTypeEnum devTypeEnum = DeviceTypeEnum.valueOf(strType);
			devType.setType(devTypeEnum);
		}
		/** 排序顺序号 */
		String sortIdStr = element.attributeValue(SortId);
		if (StringUtils.isNumeric(sortIdStr)) {
			Integer sortId = Integer.valueOf(sortIdStr);
			devType.setSortId(sortId.intValue());
		}

		String isSystemStr = element.attributeValue(isSystem);
		if (null != isSystemStr && StringUtils.isNumeric(isSystemStr)) {
			Integer isSystemNum = Integer.valueOf(isSystemStr);
			if (0 == isSystemNum.intValue()) {
				devType.setSystem(false);
			} else {
				devType.setSystem(true);
			}
		}
		return devType;
	}
	public static void main(String[] argv){
		DeviceTypeUtil.deviceTypeFileName = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs/dictionary/DeviceType.xml";
		removeTypeToFile("1.3.6.1.4.1.6486.800.1.1.2.1.1.4.2.5.1");
	}
	public static CaplibAPIErrorCode removeTypeToFile(String sysoid) {

		if(null == sysoid|| sysoid.isEmpty()){
			return CaplibAPIErrorCode.ADD_DEVICE_TYPE_01;
		}
		try {

			String fullName = DeviceTypeUtil.deviceTypeFileName;
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(fullName);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			// 打开deviceTypeFileName，并写入
			@SuppressWarnings("rawtypes")
			List elements = root.elements();
			for (int i = elements.size() - 1; i >= 0; i--) {
				Element element = (Element) elements.get(i);
				String eleSysoid = element.attributeValue(SysOid);
				if (sysoid.equals(eleSysoid)) {
					elements.remove(i);
					break;
				}
			}

			FileOutputStream fos = new FileOutputStream(fullName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			OutputFormat of = new OutputFormat();
			of.setEncoding("UTF-8");
			of.setIndent(true);
			of.setIndent("    ");
			of.setNewlines(true);
			XMLWriter writer = new XMLWriter(osw, of);
			writer.setEscapeText(false);// cdata
			writer.write(document);
			writer.close();
			return CaplibAPIErrorCode.OK;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
		}
		return CaplibAPIErrorCode.ADD_DEVICE_TYPE_03;

	}

}
