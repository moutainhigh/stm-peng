package com.mainsteam.stm.plugin.common;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class ReliableProcessor implements PluginResultSetProcessor {

	private static final String RELIABLE_PROCESSOR_KEY = "reliable";
	private static final String COLUMN_NAME = "ColumnName";

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		String runtimeValueKey = RELIABLE_PROCESSOR_KEY + "-" + context.getResourceInstanceId() + "-" + context.getMetricId();
		Object lastRuntimeValue = context.getRuntimeValue(runtimeValueKey);
		if (lastRuntimeValue != null) {
			ResultSet lastResultSet = (ResultSet) lastRuntimeValue;
			String columnName = parameter.getParameterValueByKey(COLUMN_NAME).getValue();
			String[] lastStrings = lastResultSet.getColumn(columnName);
			if (lastStrings.length > 0) {
				String[] currentStrings = resultSet.getColumn(columnName);
				if (currentStrings.length < 0 || StringUtils.equals(lastStrings[0], currentStrings[0])) {
					// if last result set isn't null, and we can get the column to compare between the current and the last,
					// when the current and the last column is the same, we think the last is more reliable than the current.
					ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
					ResultSetMetaInfo lastMetaInfo = lastResultSet.getResultMetaInfo();
					metaInfo.removeAllColumnNames();
					for (String columnString : lastMetaInfo.getColumnNames()) {
						metaInfo.addColumnName(columnString);
					}
					resultSet.clearRows();
					for (int row = 0; row < lastResultSet.getRowLength(); ++row) {
						resultSet.addRow(lastResultSet.getRow(row));
					}
				}
			}
		}
		context.setRuntimeValue(runtimeValueKey, resultSet.clone());
	}

}
