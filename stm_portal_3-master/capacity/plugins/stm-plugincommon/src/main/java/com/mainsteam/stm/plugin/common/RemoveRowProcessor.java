package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class RemoveRowProcessor implements PluginResultSetProcessor {
	
	private static final String COLUMN_NUM = "ColumnNumber";
	private static final String regex = ",";
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		ParameterValue[] Parameters = parameter.listParameterValues();
		String cn = "";
		for (ParameterValue param : Parameters) {
			if (param.getKey().equals(COLUMN_NUM)) {
				cn = param.getValue();
			}
		}
		
		List<String> n1 = Arrays.asList(cn.split(regex));
		int temp;
		for (String n : n1) {
			temp = Integer.parseInt(n)-1;
			if (temp<0) {
				continue;
			}else {
				resultSet.removeRow(temp);
			}
			
		}
		
		
	}

}
