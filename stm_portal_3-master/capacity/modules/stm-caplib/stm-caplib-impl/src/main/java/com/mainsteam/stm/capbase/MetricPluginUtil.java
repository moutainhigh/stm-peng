package com.mainsteam.stm.capbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginConnectSetting;
import com.mainsteam.stm.caplib.plugin.PluginInitParaChanger;
import com.mainsteam.stm.caplib.plugin.PluginParameter;

/**
 * 资源与插件解析类，对应collect.xml.在这个类中主要处理指标与采集插件、指标与处理器、指标与转换器之间的关系。
 * 
 * @author
 * 
 */
public class MetricPluginUtil {
	
	private static final Log logger = LogFactory.getLog(MetricPluginUtil.class);

	private static final String TRUE = "TRUE";

	private static final String SYSOID = "Sysoid";

	private static final String PLUGIN_INIT_PARAMETER = "PluginInitParameter";
	/**
	 * MetricPlugin节点名称
	 */
	private static final String METRIC_PLUGIN = "MetricPlugin";
	/**
	 * PluginClassAlias节点属性
	 */
	private static final String CLASS = "class";
	/**
	 * 节点属性
	 */
	private static final String ID2 = "id";
	/**
	 * 节点名称，用于定义指标采集值的处理器及转换器
	 */
	private static final String PLUGIN_CLASS_ALIAS = "PluginClassAlias";
	/**
	 * 节点名称
	 */
	private static final String PLUGIN_CLASS_ALIAS_LIST = "PluginClassAliasList";

	/**
	 * 节点名称
	 */
	private static final String GLOBAL_METRIC_SETTING = "GlobalMetricSetting";
	/**
	 * 插件id，对应plugin目下的xml的Plugin节点的id属性
	 */
	private static final String PLUGINID2 = "pluginid";
	/**
	 * 指标id，对应Resource.xml中的Metric节点的id属性，在同一个xml文件中，该id可重复。通过metricid与plugid
	 * 确定在同一个文件中的唯一值。
	 */
	private static final String METRICID2 = "metricid";

	private static final String COLLECT_TYPE = "collectType";
	/**
	 * 节点名称，插件参数
	 */
	private static final String PLUGIN_PARAMETER = "PluginParameter";
	/**
	 * 
	 */
	private static final String PLUGIN_RESULT_META_INFO = "PluginResultMetaInfo";
	/**
	 * Parameter节点属性，指标采集的具体命令。
	 */
	private static final String VALUE = "value";
	/**
	 * 节点属性
	 */
	private static final String TYPE = "type";
	/**
	 * Parameter节点属性
	 */
	private static final String KEY = "key";
	/**
	 * 指定采集值返回的列
	 */
	private static final String COLUMNS = "columns";
	/**
	 * 节点名称
	 */
	private static final String PARAMETER = "Parameter";
	/**
	 * 与PluginClassAlias中的id对应
	 */
	private static final String CLASS_KEY = "classKey";
	/**
	 * 节点名称，指标采集值处理器
	 */
	private static final String PLUGIN_DATA_HANDLER = "PluginDataHandler";
	/**
	 * 节点名称
	 */
	private static final String PLUGIN_DATA_HANDLERS = "PluginDataHandlers";
	/**
	 * 节点名称，指标采集值转换器
	 */
	private static final String PLUGIN_DATA_CONVERTER = "PluginDataConverter";
	/**
	 * 节点名称，指标采集值默认转换器
	 */
	private static final String DEFAULT_CONVERTER = "defaultConverter";
	/**
	 * 节点名称，指标采集值默认处理器
	 */
	private static final String DEFAULT_PROCESSOR = "defaultProcessor";

	private static final Set<String> skipKeys = new HashSet<String>();
	static {
		skipKeys.add("startmode");
		skipKeys.add("startname");
//		skipKeys.add("column2");
	}
	/**
	 * 指标map，指标id+插件ip为key，一个采集对象为value
	 */
	private Map<String, MetricCollect> metricPluginMap;
	/**
	 * 处理器、转换器集合
	 */
	private Map<String, String> dataHandlerMap;

	/**
	 * 处理器、转换器集合
	 */
	private Map<String, PluginConnectSetting> gloableInitParamMap;

	private List<PluginInitParaChanger> pluginInitParaChangers;

	private boolean isEncrypt = false;

	public MetricPluginUtil() {
	}

	/**
	 * 解析collect.xml
	 * 
	 * @param filePath
	 *            文件所在路径
	 * @return 返回以指标id+插件id为key，采集对象为value的集合
	 */
	@SuppressWarnings("unchecked")
	public Map<String, MetricCollect> loadMetricPlugin(String filePath,
			List<String> sysoids) {
		metricPluginMap = new HashMap<String, MetricCollect>();
		dataHandlerMap = new HashMap<String, String>();

		gloableInitParamMap = new HashMap<String, PluginConnectSetting>();
		pluginInitParaChangers = new ArrayList<PluginInitParaChanger>();
		try {
			// File file = new File(filePath);
			// SAXReader saxReader = new SAXReader();
			// saxReader.setEncoding("UTF-8");
			// Document doc = saxReader.read(file);
			// Element root = doc.getRootElement();
			boolean isDefault = false;
			File file = new File(filePath);
			if (file.getName().equals("collect.xml")) {
				isDefault = true;
			}

			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(filePath);
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点

			Element elementGlobal = root.element(GLOBAL_METRIC_SETTING);
			if (null != elementGlobal) {
				// 是否加密
				Attribute isEncryptElem = elementGlobal
						.attribute(CapacityConst.IS_ENCRYPT);

				if (null != isEncryptElem) {
					if (TRUE.equalsIgnoreCase(isEncryptElem.getText())) {
						isEncrypt = true;
					}
				}
				List<Element> globalSysoidList = elementGlobal.elements(SYSOID);
				if (null != globalSysoidList && null != sysoids) {
					for (Element globalSysoid : globalSysoidList) {
						String sysoid = globalSysoid.getText();
						sysoids.add(sysoid);
					}
				}

				List<Element> globalSettingList = elementGlobal
						.elements(PLUGIN_INIT_PARAMETER);
				if (null != globalSettingList) {
					for (int i = 0; i < globalSettingList.size(); i++) {
						String pluginid = globalSettingList.get(i)
								.attributeValue("pluginid");
						String parameterId = globalSettingList.get(i)
								.attributeValue("parameterId");
						String parameterValue = globalSettingList.get(i)
								.attributeValue("parameterValue");
						PluginConnectSetting setting = new PluginConnectSetting();
						setting.setParameterId(parameterId);
						setting.setParameterValue(parameterValue);
						if (null != parameterId && parameterId.length() > 0) {
							gloableInitParamMap.put(pluginid + ","
									+ parameterId, setting);
						}
					}
				}

				// <ChangePluginInitParameter pluginid="JdbcPlugin"
				// parameterId="inputInstallPath" parameterProperty="isDisplay"
				// propertyValue="TRUE" />
				List<Element> globalChangeParameterList = elementGlobal
						.elements("ChangePluginInitParameter");
				if (null != globalChangeParameterList) {
					for (int i = 0; i < globalChangeParameterList.size(); i++) {

						String pluginId = globalChangeParameterList.get(i)
								.attributeValue("pluginid");
						String parameterId = globalChangeParameterList.get(i)
								.attributeValue("parameterId");
						String parameterProperty = globalChangeParameterList
								.get(i).attributeValue("parameterProperty");
						String propertyValue = globalChangeParameterList.get(i)
								.attributeValue("propertyValue");

						PluginInitParaChanger changer = new PluginInitParaChanger();
						changer.setPluginId(pluginId);
						changer.setParameterId(parameterId);
						changer.setPropertyId(parameterProperty);
						changer.setPropertyValue(propertyValue);
						if (StringUtils.isNotEmpty(pluginId)
								&& StringUtils.isNotEmpty(parameterId)
								&& StringUtils.isNotEmpty(parameterProperty)) {
							pluginInitParaChangers.add(changer);
						}
					}
				}

			}
			
			if(root.element(PLUGIN_CLASS_ALIAS_LIST) == null) {
				System.out.println();
			}
			List<Element> dataHandlerList = root.element(
					PLUGIN_CLASS_ALIAS_LIST).elements(PLUGIN_CLASS_ALIAS);
			for (int i = 0; i < dataHandlerList.size(); i++) {
				Element dataHandlerElement = dataHandlerList.get(i);
				String id = dataHandlerElement.attributeValue(ID2);
				String classFullName = dataHandlerElement.attributeValue(CLASS);
				dataHandlerMap.put(id, classFullName);
			}

			List<Element> mpElementList = root.elements(METRIC_PLUGIN);
			for (int i = 0; i < mpElementList.size(); i++) {
				this.putMetricPluginMap(mpElementList.get(i), isDefault);
			}

			return metricPluginMap;
		} catch (Exception e) {
			logger.error(e.getMessage() + ". File path :" + filePath, e);
		} 

		return null;
	}

	/**
	 * 根据MetricPlugin节点，解析其子节点
	 * 
	 * @param element
	 *            节点对象
	 * @param isDefault
	 */
	@SuppressWarnings("unchecked")
	private void putMetricPluginMap(Element element, boolean isDefault) {
		MetricCollect metricPlugin = new MetricCollect();
		metricPlugin.setDefault(isDefault);

		String metricId = getAttrValue(element, METRICID2);
		String pluginId = getAttrValue(element, PLUGINID2);
		// 指标id+插件id才能确定一个指标的唯一性，因为同一个指标可能有多种采集方式
		metricPluginMap.put(metricId + "," + pluginId, metricPlugin);

		String collectType = getAttrValue(element, COLLECT_TYPE);
		metricPlugin.setCollectType(collectType);

		Element pluginParameterElement = element.element(PLUGIN_PARAMETER);
		String typeStr = getAttrValue(pluginParameterElement, TYPE);
		List<Element> parameterElementList = pluginParameterElement
				.elements(PARAMETER);
		ParameterDef[] parameters = new ParameterDef[parameterElementList
				.size()];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = this.initParameter(parameterElementList.get(i));
		}

		PluginParameter pluginParameter = new PluginParameter();
		ParameterTypeEnum typeEnum = ParameterTypeEnum.valueOf(typeStr);
		pluginParameter.setType(typeEnum);
		pluginParameter.setParameters(parameters);
		metricPlugin.setPluginParameter(pluginParameter);

		// 取得返回值表头信息。一般这种情况都是查询出结果可能有多列，每列都对应响应的列头
		Element pluginResultMetaInfoElement = element
				.element(PLUGIN_RESULT_META_INFO);
		if (pluginResultMetaInfoElement != null) {
			metricPlugin.setPluginResultMetaInfo(this
					.initPluginResultMetaInfo(pluginResultMetaInfoElement));
		}

		Element pluginDataHandlersElement = element
				.element(PLUGIN_DATA_HANDLERS);
		if (pluginDataHandlersElement != null) {
			List<Element> PluginDataHandlerList = pluginDataHandlersElement
					.elements(PLUGIN_DATA_HANDLER);
			if (PluginDataHandlerList != null
					&& PluginDataHandlerList.size() > 0) {
				PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[PluginDataHandlerList
						.size()];
				for (int i = 0; i < pluginDataHandlers.length; i++) {
					pluginDataHandlers[i] = this
							.initPluginDataHandler(PluginDataHandlerList.get(i));
				}
				metricPlugin.setPluginDataHandlers(pluginDataHandlers);
			}
		}
		if (null == metricPlugin.getPluginDataHandlers()) {
			PluginDataHandler pluginDataHandler = new PluginDataHandler();
			pluginDataHandler.setClassFullName(this.dataHandlerMap
					.get(DEFAULT_PROCESSOR));
			PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[1];
			pluginDataHandlers[0] = pluginDataHandler;
			metricPlugin.setPluginDataHandlers(pluginDataHandlers);
		}

		Element pluginDataConverterElement = element
				.element(PLUGIN_DATA_CONVERTER);
		if (null != pluginDataConverterElement) {

			PluginDataConverter converter = new PluginDataConverter();
			String classKey = getAttrValue(pluginDataConverterElement,
					CLASS_KEY);
			converter.setClassFullName(this.dataHandlerMap.get(classKey));
			List<Element> parameterList = pluginDataConverterElement
					.elements(PARAMETER);
			if (parameterList != null && parameterList.size() > 0) {
				ParameterDef[] converterParameters = new ParameterDef[parameterList
						.size()];
				for (int i = 0; i < parameterList.size(); i++) {
					converterParameters[i] = this.initParameter(parameterList
							.get(i));
				}
				converter.setParameterDefs(converterParameters);
			}
			metricPlugin.setPluginDataConverter(converter);
		}

		if (null == metricPlugin.getPluginDataConverter()) {
			PluginDataConverter converter = new PluginDataConverter();
			converter.setClassFullName(this.dataHandlerMap
					.get(DEFAULT_CONVERTER));
			metricPlugin.setPluginDataConverter(converter);
		}

		if (!gloableInitParamMap.isEmpty()) {
			List<PluginConnectSetting> localSettings = new ArrayList<PluginConnectSetting>();
			for (Entry<String, PluginConnectSetting> entry : gloableInitParamMap
					.entrySet()) {
				String key = entry.getKey();
				PluginConnectSetting setting = entry.getValue();
				if (key.contains(pluginId + ",")) {
					localSettings.add(setting);
				}
			}

			PluginConnectSetting[] arraySetting = new PluginConnectSetting[localSettings
					.size()];
			metricPlugin.setPluginConnectSettings(localSettings
					.toArray(arraySetting));
		}

	}

	/**
	 * 初始化结果处理器或者转换器
	 * 
	 * @param element
	 *            相应节点
	 * @return 返回处理器对象或者转换器对象
	 */
	private PluginDataHandler initPluginDataHandler(Element element) {
		PluginDataHandler pluginDataHandler = new PluginDataHandler();

		String classKey = getAttrValue(element, CLASS_KEY);
		pluginDataHandler.setClassFullName(this.dataHandlerMap.get(classKey));

		@SuppressWarnings("unchecked")
		List<Element> parameterList = element.elements(PARAMETER);
		if (parameterList != null && parameterList.size() > 0) {
			ParameterDef[] parameters = new ParameterDef[parameterList.size()];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = this.initParameter(parameterList.get(i));
			}
			pluginDataHandler.setParameterDefs(parameters);
		}

		return pluginDataHandler;
	}

	/**
	 * 初始化结果集表头信息
	 * 
	 * @param element
	 *            相应节点
	 * @return 返回表头对象
	 */
	private PluginResultMetaInfo initPluginResultMetaInfo(Element element) {
		
		String attributeValue = element.attributeValue(COLUMNS);
		if (StringUtils.isEmpty(attributeValue)) {
			return null;
		} else {
			PluginResultMetaInfo pluginResultMetaInfo = new PluginResultMetaInfo();
			pluginResultMetaInfo.setColumns(attributeValue);
			return pluginResultMetaInfo;
		}

	}

	/**
	 * 初始化Parameter节点对象
	 * 
	 * @param element
	 *            Parameter节点
	 * @return 返回Parameter对应的对象
	 */
	private ParameterDef initParameter(Element element) {
		ParameterDef parameter = new ParameterDef();

		String attrValue = getAttrValue(element, KEY);
		parameter.setKey(attrValue);
		if(StringUtils.equals(element.getParent().getName(), "PluginDataHandler") 
				|| StringUtils.equals(element.getParent().getName(), "PluginDataConverter")) {
			parameter.setValue(element.attributeValue(VALUE));
		}else{
			parameter.setValue(getAttrValue(element, VALUE));
		}
//		if (skipKeys.contains(attrValue)) {
//			parameter.setValue(element.attributeValue(VALUE));
//		} else {
//			parameter.setValue(getAttrValue(element, VALUE));
//		}
		String typeStr = getAttrValue(element, TYPE);
		if (!StringUtils.isEmpty(typeStr)) {
			ValueTypeEnum typeEnum = ValueTypeEnum.valueOf(typeStr);
			parameter.setType(typeEnum);
		}

		return parameter;
	}

	public List<PluginInitParaChanger> getPluginInitParaChangers() {
		return pluginInitParaChangers;
	}

	

	private String getAttrValue(Element element, String attrKey) {
		String text = element.attributeValue(attrKey);
		if (null == text || text.isEmpty()) {
			return "";
		}
		if (isEncrypt) {
			return TextUtil.decrypt(text, CapacityConst.ENDECODER_KEY);
		}
		return text;
	}
}
