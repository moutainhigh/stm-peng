package com.mainsteam.stm.plugin.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
/**
 * 主要用于子资源的资源名称没有确切数据时调用
 * @author Administrator
 *
 */
public class ChangeValueProcessor implements PluginResultSetProcessor {
	private static final String VALUES = "Change";
	private static final String CHANGECOLUMN = "changeColumn";
	
	private static final Log logger = LogFactory.getLog(ChangeValueProcessor.class);

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context)
			throws PluginSessionRunException {
		ParameterValue[] params = parameter.listParameterValues();
		String changeValue = null;
		String changeColumn = null;
		for(ParameterValue value : params) {
			if(StringUtils.equalsIgnoreCase(value.getKey(), VALUES)){
				changeValue = value.getValue();
			}else if(StringUtils.equalsIgnoreCase(value.getKey(), CHANGECOLUMN)){
				changeColumn = value.getValue();
			}
		}
		
		if (changeValue == null || changeColumn == null) {
			logger.error("value is null");
		}
		int rowSize = resultSet.getRowLength();
		for (int i = 0; i < rowSize; i++) {
			resultSet.putValue(i, changeColumn, changeValue+i);
		}
		

	}

}
