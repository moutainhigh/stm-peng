package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * 进制转换
 * 
 * @author yet
 *
 */
public class SysConvertProcessor implements PluginResultSetProcessor {

	private static final Log logger = LogFactory.getLog(SysConvertProcessor.class);

	private static Map<String, Pattern> patternMap;

	private static final String HEX_STRING = "HEX";

	private static final String DOUBLE_STRING = "DOUBLE";

	private static final String INTEGER_STRING = "INT";

	private static final String HEX_TO_BINARY = "HEXTOBINARY";// 16进制转换为2进制
	
	private static final String HEX_TO_STRING = "HEXTOSTRING";// 16进制转换为字符

	static {
		patternMap = new HashMap<String, Pattern>();
		Pattern pattern = Pattern.compile("^(?:0x)*[a-fA-F0-9]+$", Pattern.CASE_INSENSITIVE);
		patternMap.put(HEX_STRING, pattern);
		pattern = Pattern.compile("^(-?\\d+\\.\\d*[Ee]{0,1}-?\\d+)$");
		patternMap.put(DOUBLE_STRING, pattern);
		pattern = Pattern.compile("^(-?\\d+(?:[Ee]{1}-?\\d+)*)");
		patternMap.put(INTEGER_STRING, pattern);

		pattern = null;

	}

	public static void main(String[] argv) {

	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) {

		ParameterValue[] parameterValues = parameter.listParameterValues();
		Map<String, String> convertParams = new HashMap<String, String>(2);// 列名，进制转换方式
		for (ParameterValue value : parameterValues) {
			convertParams.put(value.getKey(), value.getValue());
		}
		Set<String> keySet = convertParams.keySet();
		Iterator<String> iterator = keySet.iterator();
		String convertType = null;// 当前要转换的列的方式
		int columnNumber = -1;// 当前要转换进制的列索引
		String resultValue = null;// 当前要转换的值
		for (String columnName : keySet) {
			convertType = convertParams.get(columnName);
			columnNumber = resultSet.getResultMetaInfo().indexOfColumnName(columnName);
			if (columnNumber != -1) {
				for (int i = 0; i < resultSet.getRowLength(); i++) {
					resultValue = resultSet.getValue(i, columnNumber);
					resultValue = convert(convertType, resultValue);
					resultSet.putValue(i, columnNumber, resultValue);
				}
			}else {
				if (logger.isInfoEnabled()) {
					logger.info("所选要进制转换的列名参数错误,当前插件中的key为：" + columnName);
				}
				// logger.info("所选要进制转换的列名参数错误,当前插件中的key为："+columnName);
			}

		}
	}

	/**
	 * 格式化处理 初步完成16进制数转换，后续可根据需要添加其他进制转换算法
	 * 
	 * @param type
	 *            转换类型
	 * @param initkey
	 *            原始数据的key
	 * @param value
	 * @return
	 */
	private String convert(String type, String value) {
		switch (type) {
		case HEX_TO_BINARY: // 需要转化为MacAddress方式
			if (this.validatePattern(value, HEX_STRING))
				value = hexToBinary(value);
			break;
		case HEX_TO_STRING: // 需要转化为MacAddress方式
			if (this.validatePattern(value.replace(" ", ""), HEX_STRING))
				value = hexStr2Str(value);
			break;
		}
		return value;
	}

	/**
	 * 验证初始化数据是否符合格式
	 * 
	 * @param str
	 *            获取的源数据
	 * @param type
	 *            源数据的具体进制
	 * @return
	 */
	private boolean validatePattern(String str, String type) {
		if (str == null)
			return false;
		try {
			Pattern pattern = patternMap.get(type);
			if (pattern.matcher(str).matches()) {
				return true;
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("进制数据初始判断错误,初始化数据为【" + str + "】", e);
			}
			return false;
		}
		return false;
	}

	/**
	 * 十六进制转换为二进制
	 * 
	 * @param value
	 *            十六进制源数据
	 * @return
	 */
	private String hexToBinary(String value) {
		int decimalValue = Integer.parseInt(value, 16);
		String binaryValue = Integer.toBinaryString(decimalValue);
		return binaryValue;
	}
	
	/**
	 * 十六进制转换为字符
	 * 
	 * @param value2
	 *            十六进制源数据
	 * @return
	 */
	public String hexStr2Str(String value2){    
		String value=value2.replace(" ", "");
        String str = "0123456789ABCDEF";    
        char[] hexs = value.toCharArray();    
        byte[] bytes = new byte[value.length() / 2];    
        int n;    
        for (int i = 0; i < bytes.length; i++){    
            n = str.indexOf(hexs[2 * i]) * 16;    
            n += str.indexOf(hexs[2 * i + 1]);    
            bytes[i] = (byte) (n & 0xff);    
        }    
        return new String(bytes);    
    }

}
