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

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * 按列进行格式化，可以根据需要设置多个列进行格式化，以“,”隔开配置在Parameter中的key中。 如果没有配置key，默认格式化最后一列。
 * 
 * @author xiaop_000
 *
 */
public class FormatProcessor implements PluginResultSetProcessor {

	private static final String STR_ZERO = "0";

	private static final String _99_96 = "99.96";

	private static final Log logger = LogFactory
			.getLog(SelectResultSetProcessor.class);

	private static Map<String, Pattern> patternMap;

	private static final String HEX_STRING = "HEX";

	private static final String TO_MACADDRESS = "MACADDRESS";

	private static final String TO_INTEGER = "INTEGER";

	private static final String TO_IP = "IP";

	private static final String TO_HUNDRED = "TO_HUNDRED";

	private static final String TO_POSITIVE = "TO_POSITIVE";

	private static final String SEPARATOR = ",";

	private static final String TO_DEC = "DECIMAL";

	private static final String DOUBLE_STRING = "DOUBLE";

	private static final String INTEGER_STRING = "INT";
	
	private static final String HEX_START_STRING = "0X";
	
	private static final String IP_SEPARATOR = ".";
	
	private static final String MACADDRESS_SEPARATOR = ":";
	

	static {
		patternMap = new HashMap<String, Pattern>();
		Pattern pattern = Pattern.compile("^(?:0x)*[a-fA-F0-9]+$",
				Pattern.CASE_INSENSITIVE);
		patternMap.put(HEX_STRING, pattern);
		pattern = Pattern.compile("^(-?\\d+\\.\\d*[Ee]{0,1}-?\\d+)$");
		patternMap.put(DOUBLE_STRING, pattern);
		pattern = Pattern.compile("^(-?\\d+(?:[Ee]{1}-?\\d+)*)");
		patternMap.put(INTEGER_STRING, pattern);

		pattern = null;

	}

	public static void main(String[] argv) {

		System.out.println(patternMap.size());

		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.putValue(0, 0, "35.0");
		pluginResultSet.putValue(0, 1, "1");
		pluginResultSet.putValue(0, 2, "idel");
		// pluginResultSet.putValue(0, 3, "200");
		pluginResultSet.putValue(1, 0, "110");
		pluginResultSet.putValue(1, 1, "2");
		pluginResultSet.putValue(1, 2, "System");
		//pluginResultSet.putValue(1, 3, "100");
		pluginResultSet.putValue(2, 0, "100");
		pluginResultSet.putValue(2, 1, "3");
		pluginResultSet.putValue(2, 2, "System Idle");
		//pluginResultSet.putValue(2, 3, "100");
		pluginResultSet.putValue(3, 0, "80");
		pluginResultSet.putValue(3, 1, "4");
		pluginResultSet.putValue(3, 2, "mysqld");
		// pluginResultSet.putValue(3, 3, "0");

		List<String> list = new ArrayList<String>();
		list.add("PercentProcessorTime");
		list.add("pid");
		list.add("command");

		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(list);
		ResultSet resultset = new ResultSet(pluginResultSet, metaInfo);

		FormatProcessor processor = new FormatProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue param1 = new ParameterValue();
		param1.setKey("");
		param1.setValue("TO_HUNDRED");
		parameter.addParameter(param1);

		processor.process(resultset, parameter, null);
		System.out.println(resultset);

		
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) {

		ParameterValue[] params = parameter.listParameterValues();
		Map<String, String> regularParams = new HashMap<String, String>(2); 
		for (ParameterValue value : params) {
			regularParams.put(value.getKey(), value.getValue());
		}

		Set<String> keySet = regularParams.keySet();
		if (keySet != null && keySet.size() > 0) {

			// 将格式化后的数据存入Map中。key为原结果集中的行列坐标，value为格式化后的值
			Map<String, String> resultMap = new HashMap<String, String>(2);

			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String[] columnNames = null;
				int circleTotal = 1;// 循环次数
				if (StringUtils.isNotBlank(key)) {
					columnNames = key.split(SEPARATOR);
					circleTotal = columnNames.length;
				}
				int currentCircle = 0;// 当前循环次数
				do {
					try {
						int column = resultSet.getColumnLength() - 1;
						if (columnNames != null) {
							column = resultSet.getResultMetaInfo()
									.indexOfColumnName(
											columnNames[currentCircle]);
						}
						for (int i = 0; i < resultSet.getRowLength(); i++) {
							String value = resultSet.getValue(i, column);
							String matchesKey = this.validatePattern(value);
							String result = this.dealWithPattern(
									regularParams.get(key), matchesKey, value);
							resultMap.put(i + SEPARATOR + column, result);
						}
					} catch (Throwable e) {
						if(logger.isWarnEnabled()){
							logger.warn("Format Processor deal error. " + e.getMessage(), e);
						}
						continue;
					} finally{
						currentCircle++;
					}
				} while (currentCircle < circleTotal);

			}

			if (resultMap.size() > 0) {
				Set<String> tempSet = resultMap.keySet();
				Iterator<String> tempIterator = tempSet.iterator();
				while (tempIterator.hasNext()) {
					String key = tempIterator.next();
					int row = Integer.parseInt(key.split(SEPARATOR)[0]);
					int column = Integer.parseInt(key.split(SEPARATOR)[1]);
					resultSet.putValue(row, column, resultMap.get(key));
				}

			}

		}

	}

	/**
	 * 格式化处理
	 * 
	 * @param key
	 *            处理后的key
	 * @param initkey
	 *            原始数据的key
	 * @param value
	 * @return
	 */
	private String dealWithPattern(String key, String initkey, String value) {
		switch (key) {
		case TO_MACADDRESS: // 需要转化为MacAddress方式
			if (StringUtils.equals(initkey, HEX_STRING)) {// 原始数据为十六进制
				value = this.hexToMacAddress(value);
			}
			break;
		case TO_IP:
			if (StringUtils.equals(initkey, HEX_STRING)) {// 原始数据为十六进制
				value = this.hexToIpAddress(value);
			}
			break;
		case TO_DEC:
			if (StringUtils.equals(initkey, HEX_STRING)) {// 原始数据为十六进制
				value = this.hexToDecimal(value);
			}
			break;
		case TO_INTEGER:
			if (StringUtils.equals(initkey, DOUBLE_STRING)
					|| StringUtils.equals(initkey, INTEGER_STRING)) {
				value = this.FormatDoubleToInt(value);
			}
			break;
		case TO_HUNDRED:
			if (StringUtils.equals(initkey, INTEGER_STRING)
					|| StringUtils.equals(initkey, DOUBLE_STRING)) {// 原始数据为double或int
				value = this.numberToHandred(value);
			}
			break;
		case TO_POSITIVE:
			if (StringUtils.equals(initkey, INTEGER_STRING)
					|| StringUtils.equals(initkey, DOUBLE_STRING)) {// 原始数据为double或int
				value = this.numberToPositive(value);
			}
			break;
		}
		return value;
	}

	private String numberToPositive(String value) {
		try {
			Double d = Double.valueOf(value);
			if (d.doubleValue() < 0) {
				return STR_ZERO;
			}
			return String.valueOf(d);
		} catch (NumberFormatException e) {
			return STR_ZERO;
		}
	}

	private String numberToHandred(String value) {
		try {
			Double d = Double.valueOf(value);
			if (d.doubleValue() < 0){
				return STR_ZERO;
			}
			if(d.doubleValue() > 100) {
				return _99_96;
			}
			return String.valueOf(d);
		} catch (NumberFormatException e) {
			return _99_96;
		}
	}

	/**
	 * 验证初始化数据是否符合格式
	 * 
	 * @param str
	 * @return
	 */
	private String validatePattern(String str) {
		if(str == null)
			return str;
		try {
			Set<String> keySet = patternMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				Pattern pattern = patternMap.get(key);
				if (pattern.matcher(str).matches()) {
					return key;
				}
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("格式化数据错误,初始化数据为【" + str + "】", e);
			}
			return null;
		}
		return null;
	}

	/**
	 * 十六进制数据转换为MacAddress
	 * 
	 * @param str
	 */
	String hexToMacAddress(String str) {
		// 去除0x的字符串开头
		if(StringUtils.isNotBlank(str)) {
			if(StringUtils.startsWithIgnoreCase(str, HEX_START_STRING))
				str = str.substring(2);
		}else
			return str;
		// 每隔2字符插入：
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = 0; i < str.length() / 2 - 1; i++) {
			sb.insert((i + 1) * 2 + i, MACADDRESS_SEPARATOR);
		}
		return sb.toString();
	}

	/**
	 * 十六进制转十进制
	 * 
	 * @param str
	 * @return
	 */
	private String hexToDecimal(String str) {
		// 去除0x的字符串开头
		if(StringUtils.isNotBlank(str)) {
			if(StringUtils.startsWithIgnoreCase(str, HEX_START_STRING))
				str = str.substring(2);
		}else
			return str;
		// 每隔2字符进行分割
		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		if (chars != null) {
			for (int i = 0; i < chars.length / 2; i++) {
				try {
					int subInt = Integer.parseInt(new String(chars, i * 2, 2),
							16);
					sb.append(subInt);
				} catch (Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn("格式化数据错误,初始化数据为【" + str + "】", e);
					}
					return str;
				}
			}
			return sb.toString();
		}
		return str;
	}

	/**
	 * 十六进制转为ip地址
	 * 
	 * @param str
	 * @return
	 */
	String hexToIpAddress(String str) {
		// 去除0x的字符串开头
		if(StringUtils.isNotBlank(str)) {
			if(StringUtils.startsWithIgnoreCase(str, HEX_START_STRING))
				str = str.substring(2);
		}else
			return str;
		// 每隔2字符进行分割
		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		if (chars != null) {
			for (int i = 0; i < chars.length / 2; i++) {
				try {
					int subIp = Integer.parseInt(new String(chars, i * 2, 2),
							16);
					sb.append(subIp);
					if (i != chars.length / 2 - 1)
						sb.append(IP_SEPARATOR);
				} catch (Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn("格式化数据错误,初始化数据为【" + str + "】", e);
					}
					return str;
				}
			}
			return sb.toString();
		}
		return str;
	}

	/**
	 * 格式化double为int，去掉小数位
	 * 
	 * @param str
	 * @return
	 */
	private String FormatDoubleToInt(String str) {
		if (str != null) {
			try {
				BigDecimal bg = new BigDecimal(str);
				str = bg.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString();
			} catch (NumberFormatException e) {
				logger.warn("Double数据格式化错误," + e.getMessage(), e);
			}
		}
		return str;
	}

}
