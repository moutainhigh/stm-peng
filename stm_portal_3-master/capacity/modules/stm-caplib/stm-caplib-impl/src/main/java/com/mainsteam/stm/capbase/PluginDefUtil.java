package com.mainsteam.stm.capbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.dict.BoxStyleEnum;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.SupportValue;

/**
 * 解析插件xml，不同的类型的采集方法就是一个采集插件，对应需要采集参数，这些参数统一都配置在xml文件中，即plugin文件夹 下的xml文件
 * 
 * @author liuyun
 * 
 */
public class PluginDefUtil {

	private static final Log logger = LogFactory.getLog(PluginDefUtil.class);

	public PluginDefUtil() {
	}

	/**
	 * 解析SnmpPlugin.xml插件
	 * 
	 * @param file
	 *            文件
	 * @return 返回PluginDef对象
	 */
	public PluginDef loadPlugin(File file) {
		try {
//			SAXReader saxReader = new SAXReader();
//			saxReader.setEncoding("UTF-8");
//			Document doc = saxReader.read(file);
//			Element root = doc.getRootElement();

			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(file.getPath()); 
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8"); 
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点		
			
			PluginDef plugin = this.initPlugin(root);
			return plugin;
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 初始化一个插件
	 * 
	 * @param element
	 * @return
	 */
	private PluginDef initPlugin(Element element) {
		PluginDef plugin = new PluginDef();

		plugin.setId(element.attributeValue("id"));
		plugin.setName(element.attributeValue("name"));
		plugin.setClassUrl(element.attributeValue("class"));
		plugin.setMaxActiveSession(element.elementTextTrim("MaxActiveSession"));
		plugin.setMaxIdleSession(element.elementTextTrim("MaxIdleSession"));
		plugin.setSessionTTL(element.elementTextTrim("SessionTTL"));
		plugin.setObjectCacheTimeout(element
				.elementTextTrim("ObjectCacheTimeout"));

		@SuppressWarnings("unchecked")
		List<Element> parElementList = element.element("PluginInitParameters")
				.elements("parameter");
		PluginInitParameter[] initParameters = new PluginInitParameter[parElementList
				.size()];
		for (int i = 0; i < initParameters.length; i++) {
			initParameters[i] = this.initPluginInitParameter(parElementList
					.get(i));
		}
		plugin.setPluginInitParameters(initParameters);

		return plugin;
	}

	/**
	 * 根据Plugin节点，解析其子节点
	 * 
	 * @param element
	 *            节点对象
	 */
	private PluginInitParameter initPluginInitParameter(Element element) {
		PluginInitParameter parameter = new PluginInitParameter();

		parameter.setId(element.attributeValue("id"));

		String boxStyleText = element.attributeValue("boxStyle");
		if (StringUtils.isNotEmpty(boxStyleText)) {
			BoxStyleEnum boxStyle = BoxStyleEnum.valueOf(boxStyleText);
			parameter.setBoxStyle(boxStyle);
		} else {
			parameter.setBoxStyle(BoxStyleEnum.Input);
		}

		parameter.setName(element.attributeValue("name"));
		parameter.setValueValidate(element.attributeValue("valueValidate"));
		parameter.setDisplayOrder(element.attributeValue("displayOrder"));
		parameter.setGroup(element.attributeValue("group"));
		parameter.setType(element.attributeValue("type"));

		String unit = element.attributeValue("unit");
		if (unit != null) {
			if (unit.trim().equals("s")) {
				parameter.setUnitEnum(TimeUnit.SECONDS);
			} else if (unit.trim().equals("ms")) {
				parameter.setUnitEnum(TimeUnit.MICROSECONDS);
			} else if (unit.trim().equals("m")) {
				parameter.setUnitEnum(TimeUnit.MINUTES);
			}
		}
		String isSessionKey = element.attributeValue("isSessionKey");
		if (isSessionKey != null) {
			if (isSessionKey.trim().equalsIgnoreCase("true")) {
				parameter.setSessionKey(true);
			}
		}

		String isEditStr = element.attributeValue("isEdit");
		if (StringUtils.isNotEmpty(isEditStr)) {
			boolean isEdit = Boolean.valueOf(isEditStr);
			parameter.setEdit(isEdit);
		} else {
			parameter.setEdit(true);
		}

		String mustInputStr = element.attributeValue("mustInput");
		if (StringUtils.isNotEmpty(mustInputStr)) {
			boolean mustInput = Boolean.valueOf(mustInputStr);
			parameter.setMustInput(mustInput);
		}

		String isPasswordStr = element.attributeValue("isPassword");
		if (StringUtils.isNotEmpty(isPasswordStr)) {
			boolean isPassword = Boolean.valueOf(isPasswordStr);
			parameter.setPassword(isPassword);
		} else {
			parameter.setPassword(false);
		}

		String isDisplayStr = element.attributeValue("isDisplay");
		if (StringUtils.isNotEmpty(isDisplayStr)) {
			boolean isDisplay = Boolean.valueOf(isDisplayStr);
			parameter.setDisplay(isDisplay);
		} else {
			parameter.setDisplay(true);
		}

		parameter.setDefaultValue(element.elementTextTrim("DefaultValue"));
		parameter.setHelpInfoId(element.elementTextTrim("HelpInfo"));
		parameter.setErrorInfo(element.elementTextTrim("errorInfo"));

		Element element2 = element.element("SupportValues");
		if (null != element2) {
			@SuppressWarnings("unchecked")
			List<Element> supportElementList = element2
					.elements("SupportValue");
			SupportValue[] supportValues = new SupportValue[supportElementList
					.size()];
			for (int i = 0; i < supportValues.length; i++) {
				supportValues[i] = this.initSupportValue(supportElementList
						.get(i));
			}
			parameter.setSupportValues(supportValues);
		}
		return parameter;
	}

	/**
	 * 根据parameter节点，解析其子节点
	 * 
	 * @param element
	 *            节点对象
	 */
	private SupportValue initSupportValue(Element element) {
		SupportValue sv = new SupportValue();
		sv.setName(element.elementTextTrim("Name"));
		sv.setValue(element.elementTextTrim("Value"));
		sv.setHideGroup(element.elementTextTrim("HideGroup"));
		sv.setShowGroup(element.elementTextTrim("ShowGroup"));
		return sv;
	}

}
