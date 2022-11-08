package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class ColumnAddProcessor implements PluginResultSetProcessor {
	
	private static final String COLUMN_NAME = "ColumnName";
	private static final String COLUMN_VALUE = "ColumnValue";
	
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		String columnName = parameter.getParameterValueByKey(COLUMN_NAME).getValue();
		ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
		metaInfo.addColumnName(columnName);
			
		int columnIndex = resultSet.getColumnLength();
		int row = 0;
		for (ParameterValue parameterValue : parameter.getParameterValuesByKey(COLUMN_VALUE)) {
			resultSet.putValue(row ++, columnIndex - 1, parameterValue.getValue());
		}
		
	}

}
