package com.mainsteam.stm.caplib.collect;

import java.util.UUID;
import com.mainsteam.stm.caplib.dict.BoxStyleEnum;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.PluginParameter;

public class DemoMetricCollect extends MetricCollect {

	/**
	 * 
	 */
	private static final long serialVersionUID = -655205557537938745L;

	public DemoMetricCollect() {
		this.setCollectType("demo");
		this.setDefault(true);
		
		PluginDef plugin = new PluginDef();
		plugin.setId("DemoPlugin");
		plugin.setName("DemoPlugin");
		plugin.setClassUrl("com.mainsteam.stm.plugin.demo.DemoPluginSession");
		plugin.setMaxActiveSession("10");
		plugin.setMaxIdleSession("0");
		plugin.setSessionTTL("10000");
		plugin.setObjectCacheTimeout("10000");

		PluginInitParameter[] initParameters = new PluginInitParameter[2];
		initParameters[0] = new PluginInitParameter();
		initParameters[0].setId("demoGrpBox");
		initParameters[0].setName("Demo配置信息");
		initParameters[0].setBoxStyle(BoxStyleEnum.GroupBox);
		initParameters[0].setDisplay(true);
		initParameters[0].setDisplayOrder("0");
		initParameters[0].setHelpInfoId("Demo配置信息");
		initParameters[1] = new PluginInitParameter();
		initParameters[1].setId("demoConfigFile");
		initParameters[1].setName("Demo配置文件路径");
		initParameters[1].setBoxStyle(BoxStyleEnum.Input);
		initParameters[1].setDisplay(true);
		initParameters[1].setDisplayOrder("1");
		initParameters[1].setSessionKey(true);
		initParameters[1].setEdit(true);
		initParameters[1].setMustInput(true);
		initParameters[1].setHelpInfoId("Demo配置文件路径");
		plugin.setPluginInitParameters(initParameters);
		this.setPlugin(plugin);
		
//		PluginResultMetaInfo pluginResultMetaInfo = new PluginResultMetaInfo();
//		pluginResultMetaInfo.setColumns("");
//		this.setPluginResultMetaInfo(pluginResultMetaInfo);
		
		
//		PluginParameter pluginParameter = new PluginParameter();
//		pluginParameter.setType(ParameterTypeEnum.ArrayType);
//		ParameterDef parameters[] = new ParameterDef[1];
//		parameters[0] = new ParameterDef();
//		parameters[0].setKey("uuid");
//		parameters[0].setValue(UUID.randomUUID().toString());
//		pluginParameter.setParameters(parameters);
//		this.setPluginParameter(pluginParameter);
		
		
		PluginDataHandler[] pluginDataHandlers = new PluginDataHandler[1];
		PluginDataHandler pluginDataHandler = new PluginDataHandler();
		pluginDataHandler.setClassFullName("com.mainsteam.stm.plugin.demo.DemoDataProcessor");
		pluginDataHandlers[0] = pluginDataHandler;
		this.setPluginDataHandlers(pluginDataHandlers);
		
		PluginDataConverter converter = new PluginDataConverter();
		converter.setClassFullName("com.mainsteam.stm.plugin.common.DefaultResultSetConverter");
		this.setPluginDataConverter(converter);
	}
	
}
