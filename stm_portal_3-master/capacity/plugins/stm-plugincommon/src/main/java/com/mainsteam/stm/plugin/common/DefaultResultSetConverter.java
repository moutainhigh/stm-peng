package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;

public class DefaultResultSetConverter implements PluginResultSetConverter {
	
	private static final Log logger = LogFactory.getLog(DefaultResultSetConverter.class);

	@Override
	public ConverterResult[] convert(ResultSet resultSet,
			ProcessParameter parameter) {
		if (null == resultSet) {
			return null;
		}
		
		int columnSize = resultSet.getColumnLength();
		if (columnSize <= 0) {
			return null;
		}
		//默认返回ResultSet的最后一列的值
		try{
			String[] result = resultSet.getColumn(resultSet.getColumnLength()-1);
			this.formatDouble(result);
 			return new ConverterResult[]{new ConverterResult(null, result)};
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return null;
		}
		
	}
	
	/**
	 * 格式化double型数据
	 * @param result
	 */
	private void formatDouble(String[] result) {
		
		if(result != null) {
			//匹配double，包括科学计数法
			String doubleRegex = "^(-?\\d+\\.\\d+[Ee]{0,1}-?\\d+)$";
			Pattern doublePattern = Pattern.compile(doubleRegex);
			//匹配整数，包括科学计数法
			String intRegex = "^(-?\\d+(?:[Ee]{1}-?\\d+)*)$";
			Pattern intPattern = Pattern.compile(intRegex);
			for(int i = 0; i < result.length; i++) {
				String str = result[i];
				if (str == null)
					continue;
				if(doublePattern.matcher(str).matches()){
					try{
						BigDecimal bg = new BigDecimal(str);
						str = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
					}catch(NumberFormatException e){
						logger.warn("Double数据格式化错误," + e.getMessage(), e);
					}
					result[i] = str;
				}else if (intPattern.matcher(str).matches()) {
					try{
						BigDecimal bg = new BigDecimal(str);
						str = bg.toPlainString();
					}catch(NumberFormatException e){
						logger.warn("Integer数据格式化错误," + e.getMessage(), e);
					}
					result[i] = str;
					
				}
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		DefaultResultSetConverter converter = new DefaultResultSetConverter();
		
		String[] strs = new String[]{"5.8"};
		
		converter.formatDouble(strs);
		System.out.println(strs[0]);
		
		
		
	}

}
