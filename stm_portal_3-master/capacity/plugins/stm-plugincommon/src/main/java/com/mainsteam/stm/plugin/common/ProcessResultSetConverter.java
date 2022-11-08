package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * 针对进程子资源进行特殊处理
 * 
 * @author xiaop_000
 * 
 */
public class ProcessResultSetConverter implements PluginResultSetConverter {
	
	private static final Log logger = LogFactory.getLog(ProcessResultSetConverter.class);

	private static final String EMPTY_VALUE_OF_INST_PROPERTY_VALUE = "Empty value of instPropertyValue reveived in classProcessResultSetConverter";
	private static final String NULL_INST_PROPERTY = "NULL instPropertyValue reveived in classProcessResultSetConverter";
	private static final String INST_PROPERTY_KEY = "InstPropertyKey";
	public static final String PROCESS_PID = "pid";
	private static final String COMMAND2 = "command";
	private static final String EQUAL = "=";
	private static final String REGEX = ";";
	private static final String PROCESS_COMMAND = "processCommand=";
	private static final String PROCESS_ID = "processId=";
	private static final String NULL_VALUE_COLUMN_TITLE = "NULL ValueColumnTitle defined in classProcessResultSetConverter";
	private static final String NULL_PARAMETER_REVEIVED = "NULL parameter reveived in classProcessResultSetConverter";
	private static final String METRIC_VAL_NOTAVAIL = "0";
	private static final String METRIC_VAL_AVAIL = "1";

	@Override
	public ConverterResult[] convert(ResultSet resultSet,
			ProcessParameter parameter) throws PluginSessionRunException {

		if (null == resultSet) {
			return null;
		}

		if (null == parameter) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,
					NULL_PARAMETER_REVEIVED);
		}

		ParameterValue columnParameter = parameter
				.getParameterValueByKey("ValueColumnTitle");
		if (null == columnParameter) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NOTITLE,
					NULL_VALUE_COLUMN_TITLE);
		}
		// 获取需要展示的所有列
		String[] columnValues = resultSet.getColumn(columnParameter.getValue());

		ParameterValue instPropertyValue = parameter
				.getParameterValueByKey(INST_PROPERTY_KEY);
		if (null == instPropertyValue) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLINSTVALUE,
					NULL_INST_PROPERTY);
		}

		String instKey = instPropertyValue.getValue();
		if (StringUtils.isEmpty(instKey)) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_EMPTYINSTKEY,
					EMPTY_VALUE_OF_INST_PROPERTY_VALUE);
		}

		String[] filterStrs = instKey.split(REGEX);
		// 获取进程Id或者进程命令
		String processId = null;
		String command = null;
		for (String str : filterStrs) {
			if (StringUtils.startsWithIgnoreCase(str, PROCESS_ID)) {
				processId = str.split(EQUAL)[1];
			} else if (StringUtils.startsWithIgnoreCase(str, PROCESS_COMMAND)) {
				command = str.split(EQUAL)[1];
			}
		}

		ConverterResult[] results = new ConverterResult[1];
		boolean hasProcessId = false;
		boolean hasCommand = false;
		boolean isExist = false;
		int index = -1;
		String[] pidColumns = null;
		String[] commandColumns = null;
		if (StringUtils.isNotEmpty(processId)) {
			hasProcessId = true;
			pidColumns = resultSet.getColumn(PROCESS_PID);
		}
		if (StringUtils.isNotEmpty(command)) {
			hasCommand = true;
			commandColumns = resultSet.getColumn(COMMAND2);
		}
		// 如果进程id和命令都有
		if (hasProcessId && hasCommand) {
			for (int i = 0; i < commandColumns.length; i++) {
				if (StringUtils.equals(command.trim(), commandColumns[i].trim())
						&& StringUtils.equals(processId.trim(), pidColumns[i].trim())) {
					isExist = true;
					index = i;
					break;
				}
			}
		} else if (hasCommand) {// 只判断命令
			for (int i = 0; i < commandColumns.length; i++) {
				if (StringUtils.equals(command.trim(), commandColumns[i].trim())) {
					isExist = true;
					index = i;
					break;
				}
			}

		} else if (hasProcessId) {// 只判断进程ID
			for (int i = 0; i < pidColumns.length; i++) {
				if (StringUtils.equals(processId.trim(), pidColumns[i].trim())) {
					isExist = true;
					index = i;
					break;
				}
			}
		}
		// 判断是不是可用性
		String dataValue = null;
		if (resultSet.getResultMetaInfo().indexOfColumnName("avail") >= 0) {
			if (isExist) {
				dataValue = METRIC_VAL_AVAIL;
			} else {
				dataValue = METRIC_VAL_NOTAVAIL;
			}
		} else {
			if (index >= 0) {
				dataValue = columnValues[index];
				dataValue = this.formatDouble(dataValue);
			}
		}
		ConverterResult result = new ConverterResult(instKey,
				new String[] { dataValue });
		results[0] = result;
		return results;
	}
	
	/**
	 * 格式化double型数据
	 * @param result
	 */
	private String formatDouble(String str) {
		
		if(str != null) {
			//匹配double，包括科学计数法
			String doubleRegex = "^(-?\\d+\\.\\d*[Ee]{0,1}-?\\d+)$";
			Pattern doublePattern = Pattern.compile(doubleRegex);
			//匹配整数，包括科学计数法
			String intRegex = "^(-?\\d+(?:[Ee]{1}-?\\d+)*)$";
			Pattern intPattern = Pattern.compile(intRegex);
			if(doublePattern.matcher(str).matches()){
				try{
					BigDecimal bg = new BigDecimal(str);
					str = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				}catch(NumberFormatException e){
					logger.warn("Double数据格式化错误," + e.getMessage(), e);
				}
			}else if (intPattern.matcher(str).matches()) {
				try{
					BigDecimal bg = new BigDecimal(str);
					str = bg.toPlainString();
				}catch(NumberFormatException e){
					logger.warn("Integer数据格式化错误," + e.getMessage(), e);
				}
			}
				
		}
		return str;
		
	}

	public static void main(String[] args) throws PluginSessionRunException {

		PluginResultSet pluginSet = new PluginResultSet();
		List<String> list = new ArrayList<String>();
		list.add("pid");
		list.add("user");
		list.add(COMMAND2);
		ResultSetMetaInfo meta = new ResultSetMetaInfo(list);
		ResultSet resultSet = new ResultSet(pluginSet, meta);
		resultSet.putValue(0, 0, "2");
		resultSet.putValue(0, 1, "root1");
		resultSet.putValue(0, 2, "[migration/0]");

		resultSet.putValue(1, 0, "3");
		resultSet.putValue(1, 1, "root2");
		resultSet.putValue(1, 2, "[ksoftirqd/0]");

		resultSet.putValue(2, 0, METRIC_VAL_NOTAVAIL);
		resultSet.putValue(2, 1, "root3");
		resultSet.putValue(2, 2, "[watchdog/0]");

		ProcessParameter paramter = new ProcessParameter();

		ParameterValue value1 = new ParameterValue();
		value1.setKey("ValueColumnTitle");
		value1.setValue("user");
		paramter.addParameter(value1);

		ParameterValue value2 = new ParameterValue();
		value2.setKey(INST_PROPERTY_KEY);
		// value2.setValue("processId=6);
		// value2.setValue("processId=3);
		value2.setValue("processId=3;command=[ksoftirqd/0]");
		paramter.addParameter(value2);

		ProcessResultSetConverter converter = new ProcessResultSetConverter();
		ConverterResult[] results = converter.convert(resultSet, paramter);
		if (results != null) {
			for (ConverterResult result : results) {
				System.out.println(result.getResultIndex());
				System.out.println(result.getResultData()[0]);
			}
		} else {
			System.err.println("获取错误！！！");
		}

	}

}
