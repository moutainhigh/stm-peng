package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class RegexTransProcessor implements PluginResultSetProcessor {

	private static final String COLUMN = "Column";
	private static final String PATTEN = "Patten";
	private static final String TARGET = "Target";
	private static final String DEFAULT = "Default";
	private static final String SKIP_NULL = "SkipNull";

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> patterns = new ArrayList<String>();
		ArrayList<String> targets = new ArrayList<String>();
		String defaultTarget = null;
		boolean skipNull = true;
		for (ParameterValue parameterValue : parameter.listParameterValues()) {
			switch (parameterValue.getKey()) {
			case COLUMN:
				columns.add(parameterValue.getValue());
				break;
			case PATTEN:
				patterns.add(parameterValue.getValue());
				break;
			case TARGET:
				targets.add(parameterValue.getValue());
				break;
			case DEFAULT:
				defaultTarget = parameterValue.getValue();
				break;
			case SKIP_NULL:
				skipNull = Boolean.valueOf(parameterValue.getValue());
				break;
			default:
				break;
			}
		}
		for (int column = 0; column < resultSet.getColumnLength(); ++column) {
			if (!columns.contains(resultSet.getResultMetaInfo().getColumnName(column)))
				continue;
			String[] values = resultSet.getColumn(column);
			for (int row = 0; row < values.length; ++row) {
				if (values[row] == null) {
					if (!skipNull) {
						resultSet.putValue(row, column, defaultTarget);
					}
				} else {
					boolean matched = false;
					for (int i = 0; i < patterns.size(); ++i) {
						if (values[row].matches(patterns.get(i))) {
							resultSet.putValue(row, column, targets.get(i));
							matched = true;
							break;
						}
					}
					if (!matched) {
						resultSet.putValue(row, column, defaultTarget);
					}
				}
			}
		}
	}

}
