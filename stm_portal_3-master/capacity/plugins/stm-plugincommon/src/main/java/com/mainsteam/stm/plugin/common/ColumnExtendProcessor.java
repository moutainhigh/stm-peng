package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class ColumnExtendProcessor implements PluginResultSetProcessor {
	
	private static final String COLUMN_NAME = "ColumnName";
	
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		String columnName = parameter.getParameterValueByKey(COLUMN_NAME).getValue();
		int columnIndex = resultSet.getResultMetaInfo().indexOfColumnName(columnName);
		String[] column = resultSet.getColumn(columnIndex);
		String value = column[0];
		for (int i = 0; i < resultSet.getRowLength(); ++i) {
			resultSet.putValue(i, columnIndex, value);
		}
	}

}
