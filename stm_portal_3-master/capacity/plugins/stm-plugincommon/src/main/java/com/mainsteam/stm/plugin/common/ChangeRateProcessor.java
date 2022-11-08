package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlArithmetic;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * 变化率处理器
 * 
 * @author xiaop_000
 *
 */
public class ChangeRateProcessor implements PluginResultSetProcessor {

	private static final String SNMP_UNIT_KEY = "SNMPUnit";
	private static final String LAST_COLLECT_TIME = "lastCollectTime";
	private static final String LAST_RESULT_SET = "lastResultSet";
	private static final String COLUMNINDEX = "COLUMNINDEX";
	private static final String IGNORED = "IGNORED";
	private static final String CHGV = "CHGV";
	private static final String CHG = "CHG";
	private static final String CHGRA = "CHGRA";
	private static final String CHGR = "CHGR";
	private static final String ROUND_CHG = "ROUND_CHG";
	private static final String ROUND_CHGV = "ROUND_CHGV";
	private static final String FUNCTION = "FUNCTION";
	private static final String ROUND_PREFIX = "ROUND_";
	private static final String MOD = "MOD";
	private static final String ZERO = "0";
	private static final String SPLIT = ",";
	private static final int SCALE = 2;
	
	private static final int SNMP_VERSION_1 = 0; // snmp version 1
	
	private static final String COUNTER64 = "COUNTER64";
	private static final String COUNTER32 = "COUNTER32";
	
	
	
	private static final Log logger = LogFactory.getLog(ChangeRateProcessor.class);

	private static final JexlEngine jexl;
	static {
		JexlArithmetic jexlArithmetic = new JexlArithmetic(false) {

			@Override
			public Object bitwiseXor(Object left, Object right) {
				return Math.pow(Double.parseDouble(left.toString()), Double.parseDouble(right.toString()));
			}
		};
		jexl = new JexlEngine(null, jexlArithmetic, null, null);
		jexl.setCache(512);
		jexl.setLenient(false); // do not treat null as zero
		jexl.setSilent(false); // throw exception
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {

		// get the last resultSet
		Object lastRuntimeValue = context.getRuntimeValue(context.getResourceInstanceId() + "-" + context.getMetricId());
		
		// save the current resultSet before calculating
		Map<String, Object> tempMap = new HashMap<String, Object>(2, 0.5f);
		tempMap.put(LAST_COLLECT_TIME, context.getMetricCollectTime());
		ResultSet lastResultSet = null;
		lastResultSet = (ResultSet) resultSet.clone();
		tempMap.put(LAST_RESULT_SET, lastResultSet);
		context.setRuntimeValue(context.getResourceInstanceId() + "-" + context.getMetricId(), tempMap);

		// column indices will be skipped when calculating
		ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
		HashSet<Integer> indices = new HashSet<Integer>();
		List<ParameterValue> indexParameterValues = parameter.getParameterValuesByKey(COLUMNINDEX);
		if (indexParameterValues != null) {
			if (metaInfo == null) {
				if (logger.isWarnEnabled())
					logger.warn("No resultset metainfo when indicating the column " + indexParameterValues);
			} else {
				for (ParameterValue indexParameterValue : indexParameterValues) {
					String indexName = indexParameterValue.getValue();
					int index = metaInfo.indexOfColumnName(indexName);
					if (index < 0) {
						if (logger.isWarnEnabled())
							logger.warn("No such column " + indexName);
					} else
						indices.add(index);
				}
			}
		}

		// ignored columns will be skipped when calculating
		HashSet<Integer> ignored = new HashSet<Integer>();
		List<ParameterValue> ignoredParameterValues = parameter.getParameterValuesByKey(IGNORED);
		if (ignoredParameterValues != null) {
			if (metaInfo == null) {
				if (logger.isWarnEnabled())
					logger.warn("No resultset metainfo when indicating the column " + ignoredParameterValues);
			} else {
				for (ParameterValue ignoredParameterValue : ignoredParameterValues) {
					String ignoredName = ignoredParameterValue.getValue();
					int ignoredIndex = metaInfo.indexOfColumnName(ignoredName);
					if (ignoredIndex < 0) {
						if (logger.isWarnEnabled())
							logger.warn("No such column " + ignoredName);
					} else
						ignored.add(ignoredIndex);
				}
			}
		}
		if (lastRuntimeValue == null) { // first collecting
			for (int row = 0; row < resultSet.getRowLength(); ++row)
				for (int column = 0; column < resultSet.getColumnLength(); ++column)
					if (!indices.contains(column) && !ignored.contains(column)) {
						resultSet.putValue(row, column, resultSet.getValue(row, column) == null ? null : ZERO);
					}
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> lastMap = (HashMap<String, Object>) lastRuntimeValue;
			ResultSet preResultSet = (ResultSet) lastMap.get(LAST_RESULT_SET);
			// calculate the interval between two collections
			long preSeconds = ((Date) lastMap.get(LAST_COLLECT_TIME)).getTime();
			long currentSeconds = context.getMetricCollectTime().getTime();
			BigDecimal interval = new BigDecimal(currentSeconds - preSeconds).divide(new BigDecimal("1000"));//表示第二次和第一次采集指标值时间间隔

			String function = parameter.getParameterValueByKey(FUNCTION).getValue();
			HashSet<Integer> matched = new HashSet<Integer>(); // the row in previous result have matched
			for (int row = 0; row < resultSet.getRowLength(); ++row) {
				int preRow = -1;
				// indicate whether the current row can match any row in previous result
				boolean missMatch = false;
				// find the corresponding row in the previous result
				if (indices.isEmpty()) {
					preRow = row;
					if (preResultSet.getRowLength() <= preRow)
						missMatch = true;
				} else {
					for (int column = 0; column < resultSet.getColumnLength(); ++column)
						if (indices.contains(column) && resultSet.getValue(row, column) == null) {
							missMatch = true;
							break;
						}
					if (!missMatch) {
						for (int j = 0; j < preResultSet.getRowLength(); ++j) {
							if (matched.contains(j))
								continue;
							boolean rowMatch = true;
							for (int column = 0; column < resultSet.getColumnLength(); ++column)
								if (indices.contains(column) && !resultSet.getValue(row, column).equals(preResultSet.getValue(j, column))) {
									rowMatch = false;
									break;
								}
							if (rowMatch) {
								preRow = j;
								break;
							}
						}
					}
					if (preRow < 0)
						missMatch = true;
				}

				// calculate
				for (int column = 0; column < resultSet.getColumnLength(); ++column) {
					if (!indices.contains(column) && !ignored.contains(column)) {
						if (missMatch || resultSet.getValue(row, column) == null || preResultSet.getValue(preRow, column) == null) {
							resultSet.putValue(row, column, null);
							continue;
						}
						
						BigDecimal currentValue = null;
						BigDecimal preValue = null;
						try {
							currentValue = new BigDecimal(resultSet.getValue(row, column));
							preValue = new BigDecimal(preResultSet.getValue(preRow, column));
						} catch (NumberFormatException e) {
							if (logger.isWarnEnabled())
								logger.warn("resultSet BigDecimal NumberFormatException ."+resultSet.getValue(row, column)+"-"+preResultSet.getValue(preRow, column));
							resultSet.putValue(row, column, null);
							continue;
						}
						// when MOD exists, adjust the current value to ensure
						// that it will not be less than the previous one
						if (function.startsWith(ROUND_PREFIX) && currentValue.compareTo(preValue) < 0) {
							
							ParameterValue parameterValue = null;
							Object snmpUnitObj = resultSet.getExtraValue(SNMP_UNIT_KEY);
							String modString = null;
							if(snmpUnitObj != null) {
								int snmpVersion = (Integer)snmpUnitObj;
								if(snmpVersion == SNMP_VERSION_1) {//counter32
									parameterValue = parameter.getParameterValueByKey(COUNTER32);
								}else { //counter64
									parameterValue = parameter.getParameterValueByKey(COUNTER64);
								}
								if(parameterValue != null)
									modString = parameterValue.getValue();
							}
							if(StringUtils.isBlank(modString))
								modString = parameter.getParameterValueByKey(MOD).getValue();
							
							String[] modValues = modString.split(SPLIT);
							for (int k = 0; k < modValues.length - 1; ++k) {
								Expression expression = jexl.createExpression(modValues[k].trim());
								BigDecimal mod = new BigDecimal(expression.evaluate(null).toString());
								if (currentValue.add(mod).compareTo(preValue) >= 0) {
									currentValue = currentValue.add(mod);
									break;
								}
							}
							Expression expression = jexl.createExpression(modValues[modValues.length - 1].trim());
							BigDecimal mod = new BigDecimal(expression.evaluate(null).toString());
							while (currentValue.compareTo(preValue) < 0) {
								currentValue = currentValue.add(mod);
							}
						}
						switch (function) {
						case ROUND_CHG: // 翻转变化量
						case CHG: // 变化量
							resultSet.putValue(row, column, currentValue.subtract(preValue).toString());
							break;
						case ROUND_CHGV: // 翻转的变化速率
						case CHGV: // 变化速率
							resultSet.putValue(row, column, currentValue.subtract(preValue).divide(interval, SCALE, BigDecimal.ROUND_HALF_EVEN).toString());
							break;
						case CHGR: // 变化比率
							resultSet.putValue(
									row,
									column,
									preValue.compareTo(new BigDecimal(ZERO)) == 0 ? ZERO : currentValue.subtract(preValue)
											.divide(preValue, SCALE, BigDecimal.ROUND_HALF_EVEN).toString());
							break;
						case CHGRA: // 绝对比率
							resultSet.putValue(
									row,
									column,
									preValue.compareTo(new BigDecimal(ZERO)) == 0 ? ZERO : currentValue.subtract(preValue)
											.divide(preValue, SCALE, BigDecimal.ROUND_HALF_EVEN).abs().toString());
							break;
						}
					}
				}
			}
		}
	}

}
