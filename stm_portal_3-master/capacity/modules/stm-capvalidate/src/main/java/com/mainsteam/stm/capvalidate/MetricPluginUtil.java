package com.mainsteam.stm.capvalidate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginParameter;

public class MetricPluginUtil {

	private static final String METRIC_PLUGIN = "MetricPlugin";
	private static final String CLASS = "class";
	private static final String ID2 = "id";
	private static final String PLUGIN_CLASS_ALIAS = "PluginClassAlias";
	private static final String PLUGIN_CLASS_ALIAS_LIST = "PluginClassAliasList";
	private static final String PLUGINID2 = "pluginid";
	private static final String METRICID2 = "metricid";
	private static final String COLLECT_TYPE = "collectType";
	private static final String PLUGIN_PARAMETER = "PluginParameter";
	private static final String PLUGIN_RESULT_META_INFO = "PluginResultMetaInfo";
	private static final String VALUE = "value";
	private static final String TYPE = "type";
	private static final String KEY = "key";
	private static final String COLUMNS = "columns";
	private static final String PARAMETER = "Parameter";
	private static final String CLASS_KEY = "classKey";
	private static final String PLUGIN_DATA_HANDLER = "PluginDataHandler";
	private static final String PLUGIN_DATA_HANDLERS = "PluginDataHandlers";
	private static final String PLUGIN_DATA_CONVERTER = "PluginDataConverter";
	private static final String DEFAULT_CONVERTER = "defaultConverter";
	private static final String DEFAULT_PROCESSOR = "defaultProcessor";
	private Map<String, MetricCollect> metricPluginMap;
	private Map<String, String> dataHandlerMap;
	private List<String> validateError = new ArrayList<String>();

	public MetricPluginUtil() {
	}

	@SuppressWarnings("unchecked")
	public Map<String, MetricCollect> loadMetricPlugin(String filePath) {
		metricPluginMap = new HashMap<String, MetricCollect>();
		dataHandlerMap = new HashMap<String, String>();
		try {
			File file = new File(filePath);
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(file);
			Element root = doc.getRootElement();
			
			List<Element> dataHandlerList = root.element(PLUGIN_CLASS_ALIAS_LIST).elements(PLUGIN_CLASS_ALIAS);
			for (int i = 0; i < dataHandlerList.size(); i++) {
				String id = dataHandlerList.get(i).attributeValue(ID2);
				String classFullName = dataHandlerList.get(i).attributeValue(CLASS);
				try {
					Class.forName(classFullName);
				} catch (ClassNotFoundException e) {
					this.validateError.add("文件" + filePath + "节点" + dataHandlerList.get(i).getPath()
							+ "的属性class值" + classFullName + "不能找到对应的Class");
				}
				dataHandlerMap.put(id, classFullName);
			}
			
			List<Element> mpElementList = root.elements(METRIC_PLUGIN);
			for (int i = 0; i < mpElementList.size(); i++) {
				this.putMetricPluginMap(mpElementList.get(i), filePath);
			}

			return metricPluginMap;
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void putMetricPluginMap(Element element, String filePath) {
		MetricCollect metricPlugin = new MetricCollect();

		String metricId = element.attributeValue(METRICID2);
		String pluginId = element.attributeValue(PLUGINID2);
		metricPluginMap.put(metricId + "," + pluginId, metricPlugin);

		String collectType = element.attributeValue(COLLECT_TYPE);
		metricPlugin.setCollectType(collectType);

		Element pluginParameterElement = element.element(PLUGIN_PARAMETER);
		String typeStr = pluginParameterElement.attributeValue(TYPE);
		
		List<Element> parameterElementList = pluginParameterElement
				.elements(PARAMETER);
		ParameterDef[] parameters = new ParameterDef[parameterElementList
				.size()];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = this.initParameter(parameterElementList.get(i));
			if(StringUtils.isBlank(parameters[i].getValue()) && !StringUtils.equals("DN", parameters[i].getKey())){
				this.validateError.add("文件" + filePath + "中，metricId为" + metricId + "缺少采集命令或者传入参数为空");
			}
			
		}

		PluginParameter pluginParameter = new PluginParameter();
		ParameterTypeEnum typeEnum = ParameterTypeEnum.valueOf(typeStr);
		pluginParameter.setType(typeEnum);
		pluginParameter.setParameters(parameters);
		metricPlugin.setPluginParameter(pluginParameter);

		Element pluginResultMetaInfoElement = element.element(PLUGIN_RESULT_META_INFO);
		if (pluginResultMetaInfoElement != null) {
			metricPlugin.setPluginResultMetaInfo(this.initPluginResultMetaInfo(pluginResultMetaInfoElement));
		}

		Element pluginDataHandlersElement = element.element(PLUGIN_DATA_HANDLERS);
		if (pluginDataHandlersElement != null) {
			List<Element> PluginDataHandlerList = pluginDataHandlersElement.elements(PLUGIN_DATA_HANDLER);
			if (PluginDataHandlerList != null
					&& PluginDataHandlerList.size() > 0) {
				PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[PluginDataHandlerList.size()];
				for (int i = 0; i < pluginDataHandlers.length; i++) {
					pluginDataHandlers[i] = this.initPluginDataHandler(PluginDataHandlerList.get(i));
				}
				metricPlugin.setPluginDataHandlers(pluginDataHandlers);
			}
		}
		if(null == metricPlugin.getPluginDataHandlers()){
			PluginDataHandler pluginDataHandler = new PluginDataHandler();
			pluginDataHandler.setClassFullName(this.dataHandlerMap.get(DEFAULT_PROCESSOR));
			PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[1];
			pluginDataHandlers[0] = pluginDataHandler;
			metricPlugin.setPluginDataHandlers(pluginDataHandlers);
		}
		
		
		Element pluginDataConverterElement = element.element(PLUGIN_DATA_CONVERTER);
		if (null != pluginDataConverterElement) {
			
			PluginDataConverter converter = new PluginDataConverter();
			String classKey = pluginDataConverterElement.attributeValue(CLASS_KEY);
			converter.setClassFullName(this.dataHandlerMap.get(classKey));
			List<Element> parameterList = element.elements(PARAMETER);
			if (parameterList != null && parameterList.size() > 0) {
				ParameterDef[] converterParameters = new ParameterDef[parameterList.size()];
				for (int i = 0; i < parameters.length; i++) {
					parameters[i] = this.initParameter(parameterList.get(i));
				}
				converter.setParameterDefs(converterParameters);
			}
			metricPlugin.setPluginDataConverter(converter);
		}
		
		if(null == metricPlugin.getPluginDataConverter()){
			PluginDataConverter converter = new PluginDataConverter();
			converter.setClassFullName(this.dataHandlerMap.get(DEFAULT_CONVERTER));
			metricPlugin.setPluginDataConverter(converter);
		}
	}

	private PluginDataHandler initPluginDataHandler(Element element) {
		PluginDataHandler pluginDataHandler = new PluginDataHandler();

		String classKey = element.attributeValue(CLASS_KEY);
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

	private PluginResultMetaInfo initPluginResultMetaInfo(Element element) {
		PluginResultMetaInfo pluginResultMetaInfo = new PluginResultMetaInfo();

		pluginResultMetaInfo.setColumns(element.attributeValue(COLUMNS));

		return pluginResultMetaInfo;
	}

	private ParameterDef initParameter(Element element) {
		ParameterDef parameter = new ParameterDef();

		parameter.setKey(element.attributeValue(KEY));
		String typeStr = element.attributeValue(TYPE);
		if (!StringUtils.isEmpty(typeStr)) {
			ValueTypeEnum typeEnum = ValueTypeEnum.valueOf(typeStr);
			parameter.setType(typeEnum);
		}
		String attValue = element.attributeValue(VALUE);
		parameter.setValue(attValue);

		return parameter;
	}

	public List<String> getValidateError() {
		return validateError;
	}

	
}
