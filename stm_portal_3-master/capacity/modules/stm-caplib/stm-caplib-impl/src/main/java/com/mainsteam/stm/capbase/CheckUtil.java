package com.mainsteam.stm.capbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.CheckResultEnum;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.dict.XmlCheckResult;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginParameter;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceInstedDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;
import com.mainsteam.stm.util.OSUtil;

public class CheckUtil {
	private static final String CAPLIBS_PATH = "caplibs.path";
	private static final String RESOURCE_XML = "resource.xml";
	private static final String _1_3_6_1_4_1 = "1.3.6.1.4.1.";
	private static final String TRUE = "TRUE";
	// category.xml
	private static final String CATEGORY = "Category";
	private static final String ID = "id";
	private static final String PARENTID = "parentid";
	// devicetypes.xml
	private static final String DeviceType = "DeviceType";
	private static final String ResourceId = "ResourceId";
	// resource.xml
	private static final String METRIC = "Metric";
	private static final String METRICS = "Metrics";
	private static final String PROPERTY = "Property";
	private static final String PROPERTIES = "Properties";
	private static final String METRICID = "metricid";
	private static final String INSTANCE_NAME = "InstanceName";
	private static final String INSTANCE_ID = "InstanceId";
	private static final String INSTANTIATION = "Instantiation";
	// collect.xml
	private static final String GLOBAL_METRIC_SETTING = "GlobalMetricSetting";
	private static final String SYSOID = "Sysoid";
	private static final String METRIC_PLUGIN = "MetricPlugin";
	private static final String CLASS = "class";
	private static final String PLUGIN_CLASS_ALIAS = "PluginClassAlias";
	private static final String PLUGIN_CLASS_ALIAS_LIST = "PluginClassAliasList";
	private static final String PLUGINID = "pluginid";
	private static final String COLLECT_TYPE = "collectType";
	private static final String PLUGIN_PARAMETER = "PluginParameter";
	private static final String VALUE = "value";
	private static final String TYPE = "type";
	private static final String KEY = "key";
	private static final String PARAMETER = "Parameter";
	private static final String CLASS_KEY = "classKey";
	private static final String PLUGIN_DATA_HANDLER = "PluginDataHandler";
	private static final String PLUGIN_DATA_HANDLERS = "PluginDataHandlers";
	private static final String PLUGIN_DATA_CONVERTER = "PluginDataConverter";
	private static final String DEFAULT_PROCESSOR = "defaultProcessor";

	public static void main(String[] argv) throws Exception {

		String oc4Path = OSUtil.getEnv(CAPLIBS_PATH);// /Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
		if (argv.length > 0) {
			oc4Path = argv[0];
		}
		if (null == oc4Path || oc4Path.isEmpty()) {
			System.out.println("Please set ENV: caplibs.path");
			System.exit(1);
		}
		System.out.println(CAPLIBS_PATH + ":" + oc4Path);
		System.setProperty(CAPLIBS_PATH, oc4Path);
		CapacityServiceImpl impl = new CapacityServiceImpl();
		List<XmlCheckResult> list = impl.checkAllFiles(oc4Path);
		System.out.println("size of config error(1 is ok):" + list.size());
		for (XmlCheckResult result : list) {
			System.out.println(result.toString());
		}
	}

	public static List<XmlCheckResult> checkAllFiles(String caplibPath) {

		List<XmlCheckResult> list = new ArrayList<XmlCheckResult>();
		String caplibsPath = caplibPath;
		if (StringUtils.isEmpty(caplibsPath)) {
			caplibsPath = OSUtil.getEnv(CAPLIBS_PATH);
		}
		if (StringUtils.isEmpty(caplibsPath)) {
			list.add(new XmlCheckResult(CheckResultEnum.DIRECTORY_NOT_FOUND,
					"", "please check caplibs.path setting."));
			return list;
		}
		if (!caplibsPath.endsWith(File.separator)) {
			caplibsPath += File.separator;
		}
		String caplibsPathDict = caplibsPath + "dictionary" + File.separator;
		// 1.check category file
		String cateFile = caplibsPathDict + "Category.xml";
		Map<String, String> mapCateIds = new HashMap<String, String>();
		XmlCheckResult checkCateResult = CheckUtil.checkCategory(cateFile,
				mapCateIds);
		CheckUtil.checkResult(list, checkCateResult);
		// 2.check type file
		String typeFile = caplibsPathDict + "DeviceType.xml";
		List<String> listTypeIds = new ArrayList<String>();
		XmlCheckResult checkTypeResult = CheckUtil.checkTypes(typeFile,
				listTypeIds);
		CheckUtil.checkResult(list, checkTypeResult);
		// 3. check plugins
		String pathPlugins = caplibsPath + "plugins" + File.separator;
		List<String> listPluginIds = new ArrayList<String>();
		List<XmlCheckResult> checkPluginResults = CheckUtil.checkPlugins(
				pathPlugins, listPluginIds);
		if (!CollectionUtils.isEmpty(checkPluginResults)) {
			list.addAll(checkPluginResults);
		}
		// 4. check resource and collects
		String resourcePath = caplibsPath + "resources" + File.separator;

		String[] extensions = new String[] { "xml", "XML" };
		List<File> collectFiles = (List<File>) FileUtils.listFiles(new File(
				resourcePath), extensions, true);

		for (File file : collectFiles) {
			if (file.getName().contains("resource")) {
				File dir = new File(file.getParent());
				File[] allFiles = dir.listFiles();
				if (allFiles.length >= 2) {
					String[] collectFileNames = new String[allFiles.length - 1];
					int idx = 0;
					for (File fileOne : allFiles) {
						if (RESOURCE_XML.equalsIgnoreCase(fileOne.getName())) {
							continue;
						}
						collectFileNames[idx] = fileOne.getPath();
						idx++;
					}
					List<XmlCheckResult> results = CheckUtil
							.checkResourceCollect(file.getPath(),
									collectFileNames, mapCateIds, listTypeIds,
									listPluginIds);
					if (!CollectionUtils.isEmpty(results)) {
						list.addAll(results);
					}
				} else {
					XmlCheckResult e = new XmlCheckResult(
							CheckResultEnum.FILE_NOT_FOUND, file.getName(),
							"no collect.xml");
					list.add(e);
				}
			}
		}
		if (list.isEmpty()) {
			list.add(XmlCheckResult.OK_RESULT);
		}
		return list;
	}

	public static List<XmlCheckResult> checkResourceCollect(
			String resourceFileName, String[] collectFileNames,
			Map<String, String> mapCateIds, List<String> listTypeIds,
			List<String> listPluginIds) {
		List<XmlCheckResult> resultList = new ArrayList<XmlCheckResult>();
		List<ResourceDef> resources = new ArrayList<ResourceDef>();
		List<XmlCheckResult> resResult = checkResource(resourceFileName,
				resources);
		List<String> metricIds = new ArrayList<String>();
		for (ResourceDef res : resources) {
			List<String> mIds = new ArrayList<String>();
			ResourceMetricDef[] metrics = res.getMetricDefs();
			for (ResourceMetricDef metric : metrics) {
				mIds.add(metric.getId());
			}
			metricIds.addAll(mIds);
		}
		if (resResult.size() == 1
				&& CheckResultEnum.OK.equals(resResult.get(0).getResultEnum())) {
			for (String collectFileName : collectFileNames) {
				XmlCheckResult cResult = checkCollect(collectFileName,
						metricIds, mapCateIds, listPluginIds, listTypeIds);
				if (!CheckResultEnum.OK.equals(cResult.getResultEnum())) {
					resultList.add(cResult);
				}
			}
		} else {
			resultList.addAll(resResult);
		}
		return resultList;
	}

	public static List<XmlCheckResult> checkPlugins(String pathPlugins,
			List<String> listPluginIds) {
		List<XmlCheckResult> list = new ArrayList<XmlCheckResult>();
		File pathFile = new File(pathPlugins);
		Collection<File> fileNames = FileUtils.listFiles(pathFile,
				new String[] { "xml", "XML" }, false);
		for (File file : fileNames) {
			try {
				SAXReader reader = new SAXReader();
				InputStream ifile = new FileInputStream(file.getPath());
				InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
				reader.setEncoding("UTF-8");
				Document document = reader.read(ir);// 读取XML文件
				Element root = document.getRootElement();// 得到根节点
				listPluginIds.add(root.attributeValue("id"));
				continue;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			XmlCheckResult e1 = new XmlCheckResult(
					CheckResultEnum.FILE_FORMAT_ERROR, file.getName(), "");
			list.add(e1);
		}
		return list;
	}

	public static XmlCheckResult checkTypes(String cateFile,
			List<String> listTypeIds) {
		try {
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(cateFile);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			@SuppressWarnings("unchecked")
			List<Element> catElementList = root.elements(DeviceType);
			CategoryDef[] categorys = new CategoryDef[catElementList.size()];
			for (int i = 0; i < categorys.length; i++) {
				Element catElement = catElementList.get(i);
				String resourceId = catElement.attributeValue(ResourceId);
				listTypeIds.add(resourceId);
			}
			return XmlCheckResult.OK_RESULT;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		XmlCheckResult e = new XmlCheckResult(
				CheckResultEnum.FILE_FORMAT_ERROR, cateFile, "");
		return e;
	}

	public static void checkResult(List<XmlCheckResult> list,
			XmlCheckResult result) {
		if (null != result) {
			if (!CheckResultEnum.OK.equals(result.getResultEnum())) {
				list.add(result);
			}
		}
	}

	public static XmlCheckResult checkCategory(String cateFile,
			Map<String, String> listCateIds) {
		try {
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(cateFile);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			@SuppressWarnings("unchecked")
			List<Element> catElementList = root.elements(CATEGORY);
			CategoryDef[] categorys = new CategoryDef[catElementList.size()];
			for (int i = 0; i < categorys.length; i++) {
				Element catElement = catElementList.get(i);
				String cateId = catElement.attributeValue(ID);
				String parentId = catElement.attributeValue(PARENTID);
				listCateIds.put(cateId, parentId);
			}
			return XmlCheckResult.OK_RESULT;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		XmlCheckResult e = new XmlCheckResult(
				CheckResultEnum.FILE_FORMAT_ERROR, cateFile, "");
		return e;
	}

	public static List<XmlCheckResult> checkResource(String resourceFileName,
			List<ResourceDef> resources) {
		List<XmlCheckResult> list = new ArrayList<XmlCheckResult>();
		// 1.判断文件格式是否正确
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(resourceFileName);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			document = reader.read(ir);
		} catch (FileNotFoundException e) {
			list.add(new XmlCheckResult(CheckResultEnum.FILE_NOT_FOUND,
					resourceFileName, "FileNotFoundException:"));
		} catch (UnsupportedEncodingException e) {
			list.add(new XmlCheckResult(CheckResultEnum.FILE_FORMAT_ERROR,
					resourceFileName, "UnsupportedEncodingException:"
							+ e.getMessage()));
		} catch (DocumentException e) {
			list.add(new XmlCheckResult(CheckResultEnum.FILE_FORMAT_ERROR,
					resourceFileName, "DocumentException:" + e.getMessage()));
		}
		if (null == document) {
			return list;
		}
		Element root = document.getRootElement();// 得到根节点
		Element settingElement = root.element("GlobalMetricSetting");
		if (null == settingElement) {
			list.add(new XmlCheckResult(CheckResultEnum.NO_GLOBALMETRICSETTING,
					resourceFileName, "null == settingElement:"));
			return list;
		}
		// 2.判断gloable格式是否正确
		boolean isEncrypt = false;
		Attribute isEncryptElem = settingElement
				.attribute(CapacityConst.IS_ENCRYPT);
		if (null != isEncryptElem) {
			if (TRUE.equalsIgnoreCase(isEncryptElem.getText())) {
				isEncrypt = true;
			}
		}
		try {
			Boolean.parseBoolean(settingElement
					.elementTextTrim("GlobalIsAlert"));
			Integer.parseInt(settingElement
					.elementTextTrim("GlobalDefaultFlapping"));
			Boolean.parseBoolean(settingElement.elementTextTrim("GlobalIsEdit"));
			Boolean.parseBoolean(settingElement
					.elementTextTrim("GlobalIsMonitor"));
		} catch (NumberFormatException e1) {
			list.add(new XmlCheckResult(
					CheckResultEnum.CONFIG_ERROR_GLOBALMETRICSETTING,
					resourceFileName, "CONFIG_ERROR_GLOBALMETRICSETTING:"
							+ e1.getMessage()));
		}
		String defaultMonitorFreqStr = settingElement
				.elementTextTrim("GlobalDefaultMonitorFreq");
		try {
			FrequentEnum.valueOf(defaultMonitorFreqStr);
		} catch (Exception e) {
			list.add(new XmlCheckResult(CheckResultEnum.NONE_SUPPORT_FREQ,
					resourceFileName, "FrequentEnum not support:"
							+ defaultMonitorFreqStr));
		}

		String supportMonitorFreqStr = settingElement
				.elementTextTrim("GlobalsupportMonitorFreq");
		FrequentEnum[] frequentEnums = null;
		if (supportMonitorFreqStr != null) {
			String[] suppStrs = StringUtils.split(supportMonitorFreqStr, ",");
			frequentEnums = new FrequentEnum[suppStrs.length];
			for (int i = 0; i < suppStrs.length; i++) {
				FrequentEnum frequentEnum = null;
				try {
					frequentEnum = FrequentEnum.valueOf(suppStrs[i]);
				} catch (Exception e) {
					list.add(new XmlCheckResult(
							CheckResultEnum.NONE_SUPPORT_FREQ,
							resourceFileName, "FrequentEnum not support:"
									+ suppStrs[i]));
				}
				frequentEnums[i] = frequentEnum;
			}
		}
		// 3.判断模型和指标格式是否正确
		@SuppressWarnings("unchecked")
		List<Element> resElementList = root.elements("Resource");
		if (CollectionUtils.isEmpty(resElementList)) {
			list.add(new XmlCheckResult(CheckResultEnum.NONE_RESOURCE_ELEMENTS,
					resourceFileName, "NONE_RESOURCE_ELEMENTS:"));
			return list;
		}
		ResourceDef mainRes = null;
		for (Element element : resElementList) {
			ResourceDef resourceDef = new ResourceDef();
			String resourceIdAttrValue = getAttrValue4Check(element, ID,
					isEncrypt);
			resourceDef.setId(resourceIdAttrValue);
			String parentId = getAttrValue4Check(element, PARENTID, isEncrypt);
			if (StringUtils.isEmpty(parentId)) {
				mainRes = resourceDef;
				resourceDef.setMain(true);
			} else {
				resourceDef.setParentResourceDef(mainRes);
			}
			// 指标定义
			@SuppressWarnings("unchecked")
			List<Element> metricElementList = element.element(METRICS)
					.elements(METRIC);
			if (CollectionUtils.isEmpty(metricElementList)) {
				list.add(new XmlCheckResult(
						CheckResultEnum.NONE_METRIC_ELEMENTS, resourceFileName,
						"NONE_METRIC_ELEMENTS:"));
				return list;
			}
			List<ResourceMetricDef> metricDefs = new ArrayList<ResourceMetricDef>();
			for (int i = 0; i < metricElementList.size(); i++) {
				Element metricElement = metricElementList.get(i);
				XmlCheckResult metricResult = checkMetric(resourceFileName,
						metricDefs, isEncrypt, frequentEnums, metricElement,
						resourceDef);
				if (null != metricResult
						&& !XmlCheckResult.OK_RESULT.equals(metricResult)) {
					list.add(metricResult);
				}
			}
			resourceDef.setMetricDefs(metricDefs
					.toArray(new ResourceMetricDef[metricDefs.size()]));
			// 属性
			if (null != element.element(PROPERTIES)) {
				checkProp(isEncrypt, element, resourceDef, metricDefs);
			}
			Element insElement = element.element(INSTANTIATION);
			if (insElement != null) {
				checkInst(isEncrypt, element, resourceDef);
			} else {
				list.add(new XmlCheckResult(
						CheckResultEnum.CONFIG_ERROR_INSTANT, resourceFileName,
						"CONFIG_ERROR_INSTANT:"));
			}
			resources.add(resourceDef);
		}
		return list;
	}

	private static void checkInst(boolean isEncrypt, Element element,
			ResourceDef resourceDef) {
		ResourceInstedDef insDef = new ResourceInstedDef();
		insDef.setInstanceId(getAttrValue4Check(element, INSTANCE_ID, isEncrypt));
		insDef.setInstanceName(getAttrValue4Check(element, INSTANCE_NAME,
				isEncrypt));
		resourceDef.setInstantiationDef(insDef);
	}

	private static void checkProp(boolean isEncrypt, Element element,
			ResourceDef resourceDef, List<ResourceMetricDef> metricDefs) {
		@SuppressWarnings("unchecked")
		List<Element> propElementList = element.element(PROPERTIES).elements(
				PROPERTY);
		if (null != propElementList) {
			ResourcePropertyDef[] propertyDefs = new ResourcePropertyDef[propElementList
					.size()];
			for (int idxProp = 0; idxProp < propertyDefs.length; idxProp++) {

				ResourcePropertyDef propertyDef = new ResourcePropertyDef();
				propertyDefs[idxProp] = propertyDef;
				propertyDef.setId(getAttrValue4Check(element, ID, isEncrypt));
				String metricidStr = getAttrValue4Check(element, METRICID,
						isEncrypt);
				if (StringUtils.isNotEmpty(metricidStr)) {
					for (int idxForMetric = 0; idxForMetric < metricDefs.size(); idxForMetric++) {
						ResourceMetricDef resourceMetricDef = metricDefs
								.get(idxForMetric);
						if (resourceMetricDef.getId().equalsIgnoreCase(
								metricidStr)) {
							propertyDef.setResourceMetric(resourceMetricDef);
							break;
						}
					}
				}
			}
			resourceDef.setPropertyDefs(propertyDefs);
		}
	}

	private static XmlCheckResult checkMetric(String fileName,
			List<ResourceMetricDef> metricDefs, boolean isEncrypt,
			FrequentEnum[] frequentEnums, Element element,
			ResourceDef resourceDef) {
		ResourceMetricDef metricDef = new ResourceMetricDef();

		String metricId = getAttrValue4Check(element, ID, isEncrypt);
		metricDef.setId(metricId);

		try {
			String isDisplayStr = getTextValue4Check(element, "IsDisplay",
					isEncrypt);
			if (StringUtils.isNotEmpty(isDisplayStr)) {
				Boolean isDisplay = Boolean.valueOf(isDisplayStr);
				metricDef.setDisplay(isDisplay);
			}
			String displayOrderStr = getAttrValue4Check(
					element.element("IsDisplay"), "displayOrder", isEncrypt);
			Integer displayOrder = Integer.valueOf(displayOrderStr);
			metricDef.setDisplayOrder(String.valueOf(displayOrder));

			String metricTypeStr = getAttrValue4Check(element, "style",
					isEncrypt);
			if (StringUtils.isNotEmpty(metricTypeStr)) {
				MetricTypeEnum metricType = MetricTypeEnum
						.valueOf(metricTypeStr);
				metricDef.setMetricType(metricType);
			}
			String isTableStr = getAttrValue4Check(element, "isTable",
					isEncrypt);
			if (StringUtils.isNotEmpty(isTableStr)) {
				Boolean isTable = Boolean.parseBoolean(isTableStr);
				metricDef.setIsTable(isTable);
			}
			String isMonitorStr = getTextValue4Check(element, "IsMonitor",
					isEncrypt);
			if (StringUtils.isNotEmpty(isMonitorStr)) {
				metricDef.setMonitor(Boolean.parseBoolean(isMonitorStr));
			}
			String isEditStr = getTextValue4Check(element, "IsEdit", isEncrypt);
			if (StringUtils.isNotEmpty(isEditStr)) {
				metricDef.setEdit(Boolean.parseBoolean(isEditStr));
			}
			String isAlertStr = getTextValue4Check(element, "IsAlert",
					isEncrypt);
			if (StringUtils.isNotEmpty(isAlertStr)) {
				metricDef.setAlert(Boolean.parseBoolean(isAlertStr));
			}
			String defaultFlapping = getTextValue4Check(element,
					"DefaultFlapping", isEncrypt);
			if (StringUtils.isNotEmpty(defaultFlapping)) {
				metricDef.setDefaultFlapping(Integer.parseInt(defaultFlapping));
			}

			String resDefaultMonitorFreqStr = getTextValue4Check(element,
					"DefaultMonitorFreq", isEncrypt);
			if (resDefaultMonitorFreqStr != null) {
				FrequentEnum frequentEnum = FrequentEnum
						.valueOf(resDefaultMonitorFreqStr);
				metricDef.setDefaultMonitorFreq(frequentEnum);
			}
			String monitorFreqStr = getTextValue4Check(element,
					"SupportMonitorFreq", isEncrypt);
			FrequentEnum[] resFrequentEnums = null;
			if (monitorFreqStr != null) {
				String[] suppStrs = StringUtils.split(monitorFreqStr, ",");
				resFrequentEnums = new FrequentEnum[suppStrs.length];
				for (int idx = 0; idx < suppStrs.length; idx++) {
					FrequentEnum frequentEnum = FrequentEnum
							.valueOf(suppStrs[idx]);
					resFrequentEnums[idx] = frequentEnum;
				}
				metricDef.setSupportMonitorFreqs(frequentEnums);
			}
		} catch (Exception e1) {
			return new XmlCheckResult(
					CheckResultEnum.CONFIG_ERROR_NUMBER_OR_BOOL, fileName,
					"metricId:" + metricId + " " + e1.getMessage());
		}
		metricDef.setResourceDef(resourceDef);
		if (MetricTypeEnum.PerformanceMetric.equals(metricDef.getMetricType())) {
			Element thersholdsElement = element.element("Thresholds");
			if (thersholdsElement != null) {
				@SuppressWarnings("unchecked")
				List<Element> thersholdList = thersholdsElement
						.elements("Threshold");
				ThresholdDef[] thresHoldDefs = new ThresholdDef[thersholdList
						.size()];
				for (int idx = 0; idx < thresHoldDefs.length; idx++) {
					ThresholdDef thresHoldDef = new ThresholdDef();
					thresHoldDefs[idx] = thresHoldDef;
					Element thElement = thersholdList.get(idx);
					String defaultValueStr = getAttrValue4Check(thElement,
							"defaultvalue", isEncrypt);
					try {
						Double.parseDouble(defaultValueStr);
						thresHoldDef.setDefaultvalue(defaultValueStr);
						String operatorStr = thElement
								.attributeValue("operator");
						OperatorEnum fromString = null;

						fromString = OperatorEnum.fromString(operatorStr);

						thresHoldDef.setOperator(fromString);
						String stateidStr = getAttrValue4Check(thElement,
								"stateid", isEncrypt);
						thresHoldDef.setState(PerfMetricStateEnum
								.valueOf(stateidStr));
					} catch (Exception e) {
						return new XmlCheckResult(
								CheckResultEnum.CONFIG_ERROR_THRESHOLD,
								fileName, metricId);
					}
				}
				metricDef.setThresholdDefs(thresHoldDefs);
			}
		}
		metricDefs.add(metricDef);
		return XmlCheckResult.OK_RESULT;
	}

	@SuppressWarnings("unchecked")
	public static XmlCheckResult checkCollect(String resourceFileName,
			List<String> metricIds, Map<String, String> mapCateIds,
			Collection<String> pluginIds, List<String> listTypeIds) {

		SAXReader reader = new SAXReader();
		Document document;
		try {
			InputStream ifile = new FileInputStream(resourceFileName);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			document = reader.read(ir);
		} catch (FileNotFoundException e) {
			return new XmlCheckResult(CheckResultEnum.FILE_NOT_FOUND,
					resourceFileName, "FileNotFoundException:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			return new XmlCheckResult(CheckResultEnum.FILE_FORMAT_ERROR,
					resourceFileName, "UnsupportedEncodingException:"
							+ e.getMessage());
		} catch (DocumentException e) {
			return new XmlCheckResult(CheckResultEnum.FILE_FORMAT_ERROR,
					resourceFileName, "DocumentException:" + e.getMessage());
		}
		Element root = document.getRootElement();
		Boolean isEncrypt = Boolean.FALSE;
		Element elementGlobal = root.element(GLOBAL_METRIC_SETTING);
		if (null != elementGlobal) {
			XmlCheckResult cgResult = checkCollectGlobal(resourceFileName,
					pluginIds, isEncrypt, elementGlobal);
			if (null != cgResult
					&& !CheckResultEnum.OK.equals(cgResult.getResultEnum())) {
				return cgResult;
			}
		}

		Map<String, String> dataHandlerMap = new HashMap<String, String>();
		List<Element> dataHandlerList = root.element(PLUGIN_CLASS_ALIAS_LIST)
				.elements(PLUGIN_CLASS_ALIAS);
		for (int i = 0; i < dataHandlerList.size(); i++) {
			Element dataHandlerElement = dataHandlerList.get(i);
			String id = dataHandlerElement.attributeValue(ID);
			String classFullName = dataHandlerElement.attributeValue(CLASS);
			dataHandlerMap.put(id, classFullName);
		}

		List<Element> mpElementList = root.elements(METRIC_PLUGIN);
		List<MetricCollect> metricCollectList = new ArrayList<MetricCollect>();
		List<String> collMetricIds = new ArrayList<String>();
		List<String> collPluginIds = new ArrayList<String>();
		for (int i = 0; i < mpElementList.size(); i++) {
			Element mpElement = mpElementList.get(i);
			MetricCollect metricPlugin = new MetricCollect();
			String metricId = getAttrValue4Check(mpElement, METRICID, isEncrypt);
			collMetricIds.add(metricId);

			String pluginId = getAttrValue4Check(mpElement, PLUGINID, isEncrypt);
			collPluginIds.add(pluginId);

			// 指标id+插件id才能确定一个指标的唯一性，因为同一个指标可能有多种采集方式
			metricCollectList.add(metricPlugin);

			String collectType = getAttrValue4Check(mpElement, COLLECT_TYPE,
					isEncrypt);
			metricPlugin.setCollectType(collectType);

			Element pluginParameterElement = mpElement
					.element(PLUGIN_PARAMETER);
			String typeStr = getTextValue4Check(pluginParameterElement, TYPE,
					isEncrypt);
			List<Element> parameterElementList = pluginParameterElement
					.elements(PARAMETER);
			ParameterDef[] parameters = new ParameterDef[parameterElementList
					.size()];
			for (int idxPE = 0; idxPE < parameters.length; idxPE++) {

				Element elemn = parameterElementList.get(i);
				ParameterDef parameter = new ParameterDef();
				parameters[i] = parameter;
				parameter.setKey(getTextValue4Check(elemn, KEY, isEncrypt));
				String typeStrPE = getTextValue4Check(elemn, TYPE, isEncrypt);
				if (!StringUtils.isEmpty(typeStrPE)) {
					ValueTypeEnum typeEnum = ValueTypeEnum.valueOf(typeStrPE);
					parameter.setType(typeEnum);
				}
				parameter.setValue(getTextValue4Check(elemn, VALUE, isEncrypt));
			}

			PluginParameter pluginParameter = new PluginParameter();
			ParameterTypeEnum typeEnum = ParameterTypeEnum.valueOf(typeStr);
			pluginParameter.setType(typeEnum);
			pluginParameter.setParameters(parameters);
			metricPlugin.setPluginParameter(pluginParameter);

			Element pluginDataHandlersElement = mpElement
					.element(PLUGIN_DATA_HANDLERS);
			if (pluginDataHandlersElement != null) {
				List<Element> PluginDataHandlerList = pluginDataHandlersElement
						.elements(PLUGIN_DATA_HANDLER);
				if (PluginDataHandlerList != null
						&& PluginDataHandlerList.size() > 0) {
					PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[PluginDataHandlerList
							.size()];
					for (int idxPDH = 0; idxPDH < pluginDataHandlers.length; idxPDH++) {

						Element elePDH = PluginDataHandlerList.get(idxPDH);
						PluginDataHandler pluginDataHandler = new PluginDataHandler();
						pluginDataHandlers[idxPDH] = pluginDataHandler;

						String classKey = getTextValue4Check(elePDH, CLASS_KEY,
								isEncrypt);
						pluginDataHandler.setClassFullName(dataHandlerMap
								.get(classKey));

						List<Element> parameterList = elePDH
								.elements(PARAMETER);
						if (parameterList != null && parameterList.size() > 0) {
							ParameterDef[] parametersPDH = new ParameterDef[parameterList
									.size()];
							for (int idxCPL = 0; idxCPL < parameters.length; idxCPL++) {
								Element elemn = parameterList.get(i);
								ParameterDef parameter = new ParameterDef();
								parametersPDH[idxCPL] = parameter;
								parameter.setKey(getTextValue4Check(elemn, KEY,
										isEncrypt));
								String typeStrPE = getTextValue4Check(elemn,
										TYPE, isEncrypt);
								if (!StringUtils.isEmpty(typeStrPE)) {
									ValueTypeEnum typeEnumCPL = ValueTypeEnum
											.valueOf(typeStrPE);
									parameter.setType(typeEnumCPL);
								}
								parameter.setValue(getTextValue4Check(elemn,
										VALUE, isEncrypt));
							}
							pluginDataHandler.setParameterDefs(parameters);
						}

					}
					metricPlugin.setPluginDataHandlers(pluginDataHandlers);
				}
			}
			if (null == metricPlugin.getPluginDataHandlers()) {
				PluginDataHandler pluginDataHandler = new PluginDataHandler();
				pluginDataHandler.setClassFullName(dataHandlerMap
						.get(DEFAULT_PROCESSOR));
				PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[1];
				pluginDataHandlers[0] = pluginDataHandler;
				metricPlugin.setPluginDataHandlers(pluginDataHandlers);
			}

			Element pluginDataConverterElement = mpElement
					.element(PLUGIN_DATA_CONVERTER);
			if (null != pluginDataConverterElement) {

				PluginDataConverter converter = new PluginDataConverter();
				String classKey = getTextValue4Check(
						pluginDataConverterElement, CLASS_KEY, isEncrypt);
				converter.setClassFullName(dataHandlerMap.get(classKey));
				List<Element> parameterList = pluginDataConverterElement
						.elements(PARAMETER);
				if (parameterList != null && parameterList.size() > 0) {
					ParameterDef[] converterParameters = new ParameterDef[parameterList
							.size()];
					for (int idxCP = 0; idxCP < parameterList.size(); idxCP++) {
						Element elemn = parameterList.get(idxCP);
						ParameterDef parameter = new ParameterDef();
						converterParameters[idxCP] = parameter;
						parameter.setKey(getTextValue4Check(elemn, KEY,
								isEncrypt));
						String typeStrPE = getTextValue4Check(elemn, TYPE,
								isEncrypt);
						if (!StringUtils.isEmpty(typeStrPE)) {
							ValueTypeEnum typeEnumCPL = ValueTypeEnum
									.valueOf(typeStrPE);
							parameter.setType(typeEnumCPL);
						}
						parameter.setValue(getTextValue4Check(elemn, VALUE,
								isEncrypt));
					}
					converter.setParameterDefs(converterParameters);
				}
				metricPlugin.setPluginDataConverter(converter);
			}
		}
		if (!metricIds.containsAll(collMetricIds)) {
			XmlCheckResult e = new XmlCheckResult(
					CheckResultEnum.ERROR_METRICID, resourceFileName, "");
			return e;
		}
		if (!pluginIds.containsAll(collPluginIds)) {
			XmlCheckResult e = new XmlCheckResult(
					CheckResultEnum.ERROR_PLUGINID, resourceFileName, "");
			return e;
		}
		return XmlCheckResult.OK_RESULT;
	}

	@SuppressWarnings("unchecked")
	private static XmlCheckResult checkCollectGlobal(String resourceFileName,
			Collection<String> pluginIds, Boolean isEncrypt,
			Element elementGlobal) {
		// 是否加密
		Attribute isEncryptElem = elementGlobal
				.attribute(CapacityConst.IS_ENCRYPT);

		if (null != isEncryptElem) {
			if (TRUE.equalsIgnoreCase(isEncryptElem.getText())) {
				isEncrypt = Boolean.TRUE;
			} else {
				if (("FALSE".equalsIgnoreCase(isEncryptElem.getText()))) {
					isEncrypt = Boolean.FALSE;
				} else {
					return new XmlCheckResult(
							CheckResultEnum.CONFIG_ERROR_GLOBALMETRICSETTING,
							resourceFileName, "GLOBALMETRICSETTING is Error");
				}
			}
		}
		List<Element> globalSysoidList = elementGlobal.elements(SYSOID);
		if (null != globalSysoidList) {
			for (Element sysoidElem : globalSysoidList) {
				String sysoid = sysoidElem.getText();
				if (!StringUtils.contains(sysoid, _1_3_6_1_4_1)) {
					return new XmlCheckResult(
							CheckResultEnum.CONFIG_ERROR_SYSOID,
							resourceFileName, "sysoidConfigError:");
				}
			}
		}

		List<Element> globalChangeParameterList = elementGlobal
				.elements("ChangePluginInitParameter");

		if (null != globalChangeParameterList) {
			for (int i = 0; i < globalChangeParameterList.size(); i++) {

				String pluginId = globalChangeParameterList.get(i)
						.attributeValue("pluginid");
				if (!pluginIds.contains(pluginId)) {
					return new XmlCheckResult(
							CheckResultEnum.CONFIG_ERROR_GLOBALMETRICSETTING,
							resourceFileName,
							"CONFIG_ERROR_GLOBALMETRICSETTING:no this pluginId");
				}
			}
		}
		return XmlCheckResult.OK_RESULT;
	}

	private static String getAttrValue4Check(Element element, String attrKey,
			boolean isEncrypt) {
		if (null == element) {
			return "";
		}
		String text = element.attributeValue(attrKey);
		if (isEncrypt) {
			return TextUtil.decrypt(text, CapacityConst.ENDECODER_KEY);
		}
		return text;
	}

	private static String getTextValue4Check(Element element, String attrKey,
			boolean isEncrypt) {
		if (null == element) {
			return "";
		}
		String text = element.elementTextTrim(attrKey);
		if (isEncrypt) {
			return TextUtil.decrypt(text, CapacityConst.ENDECODER_KEY);
		}
		return text;
	}
}
