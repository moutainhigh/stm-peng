package com.mainsteam.stm.plugin.common;

import java.util.TreeSet;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

public class ColumnPasteProcessor implements PluginResultSetProcessor {

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,PluginSessionContext context) {
		if (null == resultSet || resultSet.getColumnLength() < 1) {
			return;
		}
		String filterValue = "";
		if (null != parameter) {
			ParameterValue paraValue = parameter
					.getParameterValueByKey("FILTER");
			if (null != paraValue) {
				filterValue = paraValue.getValue();
			}
		}
		TreeSet<String> set = new TreeSet<String>();
		String[] values = resultSet.getColumn(resultSet.getColumnLength() - 1);
		if (null != values) {
			for (String value : values) {
				if (null != filterValue && filterValue.contains(value)) {
					continue;
				}
				set.add(value);
				// if (!valueList.contains(value)) {
				// valueList.add(value);
				// }
			}
		}
		resultSet.clearRows();
		if (!set.isEmpty()) {
			String min = set.first();
			String max = set.last();
			String allStr = min + "-" + max;
			resultSet.putValue(0, 0, allStr);
		}else{
			resultSet.putValue(0, 0, "");
		}
		
		
		return;
	}

}
