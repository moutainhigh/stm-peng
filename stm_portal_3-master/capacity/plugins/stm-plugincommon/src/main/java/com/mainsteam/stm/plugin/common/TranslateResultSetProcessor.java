package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

public class TranslateResultSetProcessor implements PluginResultSetProcessor {
	
	private static final String RESULT_IS_EMPTY = "ISEMPTY";

	public static void main(String[] argv) {

		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.putValue(0, 0, "2");// ==>99
		pluginResultSet.putValue(0, 1, "111");
		pluginResultSet.putValue(1, 0, "3");// ==>99.9
		pluginResultSet.putValue(1, 1, "222");
		pluginResultSet.putValue(2, 0, "4");// ==>99.9
		pluginResultSet.putValue(2, 1, "333");
		pluginResultSet.putValue(3, 0, "5");// ==>99.99
		pluginResultSet.putValue(3, 1, "444");

		List<String> list = new ArrayList<String>();
		list.add("col1");
		list.add("col2");

		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(list);
		ResultSet resultset = new ResultSet(pluginResultSet, metaInfo);

		TranslateResultSetProcessor processor = new TranslateResultSetProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue param = new ParameterValue();
		param.setKey("col1");
		param.setValue("2,背景;3,4,北京;南京");
		parameter.addParameter(param);
		processor.process(resultset, parameter,null);
		System.out.println(resultset);
	}

	/**
	 * Select选择器将对结果集进行运算，对于一个结果集其实就是一个二维表（类似数据库表），第一行必须是表头（对应表字段名）
	 * 通过对应的表头才能获取到某行某列的数据。在这个选择器中，将支持主要的基本运算，建议按照标准的SQL语句的写法来。
	 * 实际上，我们也是通过正则表达式来解析SQL语句（注：只有SELECT部分）
	 */
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,PluginSessionContext context) {
		//主资源可用性指标判断，这里仅判断结果集是否为空，为空则返回0
		if (parameter.listParameterValues()[0].getKey() == RESULT_IS_EMPTY) {
			if (resultSet == null || resultSet.getRowLength() == 0) {
				resultSet.putValue(0, 0, Availability.UNAVAILABLE_CODE);
			}else {
				resultSet.clear();
				resultSet.putValue(0, 0, Availability.AVAILABLE_CODE);
			}
			return;
		}
		
		if(resultSet == null || resultSet.getRowLength() == 0)
			return ;
		
		List<ParameterValue> emptyValues = parameter
				.getParameterValuesByEmptyKey();
		// 如果key有空值，只转最后1列
		if (CollectionUtils
				.isNotEmpty(emptyValues)) {
			ParameterValue emptyValue = emptyValues.get(0);
			String columnValues = emptyValue.getValue();
			Map<String, String> keyvalueMap = new HashMap<String, String>();
			String[] values = StringUtils.split(columnValues, ';');
			String defaultValue = "";
			for (String value : values) {
				String[] keyvalues = StringUtils.split(value, ',');
				if (1 == keyvalues.length) {
					defaultValue = keyvalues[0];
				} else {
					String toValue = keyvalues[keyvalues.length - 1];
					for (int i = 0; i < keyvalues.length - 1; i++) {
						keyvalueMap.put(keyvalues[i], toValue);
					}
				}
			}
			keyvalueMap.put("_DEFAULT_", defaultValue);

			int col = resultSet.getColumnLength() - 1;
			for (int row = 0; row < resultSet.getRowLength(); row++) {
				String value = resultSet.getValue(row, col);
				String defVal = keyvalueMap.get("_DEFAULT_");
				if (null == defVal) {
					defVal = "";
				}
				if (value == null){
					continue;
				}
				if (keyvalueMap.containsKey(value)) {
					String toValue = keyvalueMap.get(value);
					resultSet.putValue(row, col, toValue);
				} else {
					//$source表示保持原有的值
					if(!StringUtils.equals(defVal, "$source") && StringUtils.isNotEmpty(defVal)){
						resultSet.putValue(row, col, defVal);
					}
					
				}
			}
			return;
		}

		// 否则按照metainfo转
		ParameterValue[] paraValues = parameter.listParameterValues();
		Map<String, Map<String, String>> columnMap = new HashMap<String, Map<String, String>>();
		if (null != paraValues) {
			String defaultValue = "";
			for (ParameterValue pValue : paraValues) {
				Map<String, String> keyvalueMap = new HashMap<String, String>();
				String column = pValue.getKey();
				String columnValues = pValue.getValue();
				String[] values = StringUtils.split(columnValues, ';');
				for (String value : values) {
					String[] keyvalues = StringUtils.split(value, ',');
					if (1 == keyvalues.length) {
						defaultValue = keyvalues[0];
					} else {
						String toValue = keyvalues[keyvalues.length - 1];
						for (int i = 0; i < keyvalues.length - 1; i++) {
							keyvalueMap.put(keyvalues[i], toValue);
						}
					}
				}
				
				keyvalueMap.put("_DEFAULT_", defaultValue);

				columnMap.put(column, keyvalueMap);
			}
		}
		if (columnMap.isEmpty()) {
			return;
		}

		for (int col = 0; col < resultSet.getColumnLength(); col++) {
			String columnTitle = resultSet.getResultMetaInfo().getColumnName(
					col);
			if (columnMap.containsKey(columnTitle)) {
				Map<String, String> keyValMap = columnMap.get(columnTitle);
				for (int row = 0; row < resultSet.getRowLength(); row++) {
					String value = resultSet.getValue(row, col);
					String defVal = keyValMap.get("_DEFAULT_");
					if (null == defVal) {
						defVal = "";
					}
					if (value == null){
						continue;
					}
					if (keyValMap.containsKey(value)) {
						String toValue = keyValMap.get(value);
						resultSet.putValue(row, col, toValue);
					} else {
						//$source表示保持原有的值
						if(!StringUtils.equals(defVal, "$source") && StringUtils.isNotEmpty(defVal)){
							resultSet.putValue(row, col, defVal);
						}
					}
				}
			}
		}

	}

}
