package com.mainsteam.stm.capvalidate;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.dict.BoxStyleEnum;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.SupportValue;

public class PluginDefUtil {

	public PluginDefUtil() {
	}

	public PluginDef loadPlugin(File file) {
		try {
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(file);
			Element root = doc.getRootElement();
			PluginDef plugin = this.initPlugin(root);
			return plugin;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

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

	private SupportValue initSupportValue(Element element) {
		SupportValue sv = new SupportValue();
		sv.setName(element.elementTextTrim("Name"));
		sv.setValue(element.elementTextTrim("Value"));
		sv.setHideGroup(element.elementTextTrim("HideGroup"));
		sv.setShowGroup(element.elementTextTrim("ShowGroup"));
		return sv;
	}

}
