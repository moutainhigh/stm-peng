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
public class CustomMetricDataProcessMaxProcesser implements
		PluginResultSetProcessor {

	/**
	 * 
	 */
	public CustomMetricDataProcessMaxProcesser() {
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
		double maxValue = Double.MIN_VALUE;
		int rowSize = resultSet.getRowLength();
		String actualMaxValue = null;
		if (rowSize > 0) {
			for (int i = 0; i < rowSize; i++) {
				String value = resultSet.getValue(i, valueColumnIndex);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (NumberUtils.isNumber(value)) {
					double numberValue = NumberUtils.toDouble(value);
					if (maxValue < numberValue) {
						maxValue = numberValue;
						actualMaxValue = value;
					}
				}
			}
		}
		resultSet.clearRows();
		resultSet.addRow(new String[] { null, actualMaxValue });
	}
}
