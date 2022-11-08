/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * @author ziw
 * 
 */
public class CustomMetricDataProcessAvgProcesser implements
		PluginResultSetProcessor {

	/**
	 * 
	 */
	public CustomMetricDataProcessAvgProcesser() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor#process(com
	 * .mainsteam.oc.pluginprocessor.ResultSet,
	 * com.mainsteam.stm.pluginprocessor.ProcessParameter,
	 * com.mainsteam.stm.pluginsession.PluginSessionContext)
	 */
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		int columnCount = resultSet.getColumnLength();
		int valueColumnIndex = 0;
		if (columnCount > 1) {
			valueColumnIndex = 1;
		}
		double allValue = 0.0d;
		int rowSize = resultSet.getRowLength();
		String actualValue = null;
		if (rowSize > 1) {
			int count = 0;
			for (int i = 0; i < rowSize; i++) {
				String value = resultSet.getValue(i, valueColumnIndex);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (NumberUtils.isNumber(value)) {
					double numberValue = NumberUtils.toDouble(value);
					allValue += numberValue;
					count++;
				}
			}
			if (count > 0) {
				actualValue = String.valueOf(allValue / count);
			} else {
				actualValue = resultSet.getValue(0, valueColumnIndex);
			}
		} else if (rowSize == 1) {
			actualValue = resultSet.getValue(0, valueColumnIndex);
		}
		resultSet.clearRows();
		resultSet.addRow(new String[] { null, actualValue });
	}
}
