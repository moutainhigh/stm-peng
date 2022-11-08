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
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class SubInstResultSetConverter implements PluginResultSetConverter {

	private static final String NULL_PARAMETER_REVEIVED = "NULL parameter reveived in classSubInstResultSetConverter";
	
	private static final Log logger = LogFactory.getLog(SubInstResultSetConverter.class);

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
		ParameterValue titleIndex = parameter
				.getParameterValueByKey("IndexColumnTitle");

		ParameterValue valueIndex = parameter
				.getParameterValueByKey("ValueColumnTitle");

		ParameterValue instPropertyValue = parameter
				.getParameterValueByKey("InstPropertyKey");
		
		if (null != instPropertyValue && StringUtils.isNotEmpty(instPropertyValue.getValue())) {
			String propertyKeyValue = instPropertyValue.getValue();
			String[] indexs = resultSet.getColumn(titleIndex.getValue());
			String[] datas = resultSet.getColumn(valueIndex.getValue());
			for (int i = 0; i < indexs.length; i++) {
				if (StringUtils.equals(StringUtils.trim(indexs[i]), StringUtils.trim(propertyKeyValue))) {
					String str = this.formatDouble(datas[i]);
					ConverterResult result = new ConverterResult(indexs[i], new String[] {str});
					return new ConverterResult[] { result };
				}
			}
		} else {
			String[] indexs = resultSet.getColumn(titleIndex.getValue());
			String[] datas = resultSet.getColumn(valueIndex.getValue());
			
			List<ConverterResult> converterList = new ArrayList<ConverterResult>(resultSet.getRowLength());
			//临时容错机制。原因是当ConverterResult的初始size与实际的size不相同时，线程将处理等待状态，这是server的bug
			for (int i = 0; i < indexs.length; i++) {
				if(StringUtils.isNotBlank(datas[i])){
					ConverterResult result = new ConverterResult(indexs[i], new String[] { datas[i] });
					converterList.add(result);
				}
			}
			ConverterResult[] results = new ConverterResult[converterList.size()];
			return converterList.toArray(results);
		}
		return null;
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

}
