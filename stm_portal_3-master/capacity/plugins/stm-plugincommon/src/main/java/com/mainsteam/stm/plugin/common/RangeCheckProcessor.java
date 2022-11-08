package com.mainsteam.stm.plugin.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class RangeCheckProcessor implements PluginResultSetProcessor {

	private static final Log logger = LogFactory.getLog(RangeCheckProcessor.class);

	private static final String RANGE_PROCESSOR_KEY = "range";
	private static final String COLUMN_NAME = "ColumnName";
	private static final String STRATEGY = "Strategy";

	private static final String STRATEGY_REPEATALL = "RepeatAll";
	private static final String STRATEGY_IGNOREROW = "IgnoreRow";
	private static final String DEFAULT_STRATEGY = STRATEGY_REPEATALL;
	private static final Set<String> STRATEGY_SET;
	static {
		STRATEGY_SET = new HashSet<String>();
		STRATEGY_SET.add(STRATEGY_REPEATALL);
		STRATEGY_SET.add(STRATEGY_IGNOREROW);
	}

	private static final String EQ = "==";
	private static final String NE = "!=";
	private static final String LT = "<";
	private static final String LE = "<=";
	private static final String GT = ">";
	private static final String GE = ">=";
	private static final Set<String> OPERATOR_SET;
	static {
		OPERATOR_SET = new HashSet<String>();
		OPERATOR_SET.add(EQ);
		OPERATOR_SET.add(NE);
		OPERATOR_SET.add(LT);
		OPERATOR_SET.add(LE);
		OPERATOR_SET.add(GT);
		OPERATOR_SET.add(GE);
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {

		HashSet<String> columnNames = new HashSet<String>();
		ArrayList<Range> rangeList = new ArrayList<Range>();
		String strategy = DEFAULT_STRATEGY;
		for (ParameterValue parameterValue : parameter.listParameterValues()) {
			switch (parameterValue.getKey()) {
			case COLUMN_NAME:
				columnNames.add(parameterValue.getValue());
				break;
			case STRATEGY:
				if (STRATEGY_SET.contains(parameterValue.getValue())) {
					strategy = parameterValue.getValue();
				} else {
					if (logger.isWarnEnabled())
						logger.warn("Unsupported strategy: " + parameterValue.getValue());
				}
				break;
			default:
				try {
					rangeList.add(new Range(parameterValue.getKey(), new BigDecimal(parameterValue.getValue())));
				} catch (Exception e) {
					if (logger.isWarnEnabled())
						logger.warn("Invalid range: " + parameterValue.getKey() + ", " + parameterValue.getValue());
				}
			}
		}
		switch (strategy) {
		case STRATEGY_REPEATALL:
			repeatAll(resultSet, context, columnNames, rangeList);
			break;
		case STRATEGY_IGNOREROW:
			ignoreRow(resultSet, columnNames, rangeList);
			break;
		default:
			break;
		}

	}

	private void ignoreRow(ResultSet resultSet, HashSet<String> columnNames, ArrayList<Range> rangeList) {
		int row = 0;
		while (row < resultSet.getRowLength()) {
			boolean inValid = false;
			String[] rowValues = resultSet.getRow(row);
			for (int column = 0; column < rowValues.length; ++column) {
				if (columnNames.contains(resultSet.getResultMetaInfo().getColumnName(column))) {
					try {
						BigDecimal num = new BigDecimal(rowValues[column]);
						if (inValid(num, rangeList)) {
							inValid = true;
						}
					} catch (Exception ignore) {
						if (logger.isWarnEnabled())
							logger.warn("A error occur when compare", ignore);
					}
					if (inValid) {
						break;
					}
				}
			}
			if (inValid) {
				resultSet.removeRow(row);
			} else {
				row++;
			}
		}
	}

	private void repeatAll(ResultSet resultSet, PluginSessionContext context, HashSet<String> columnNames, ArrayList<Range> rangeList) {
		String runtimeValueKey = RANGE_PROCESSOR_KEY + "-" + context.getResourceInstanceId() + "-" + context.getMetricId();
		boolean inValid = false;
		for (String columnName : columnNames) {
			for (String colunmnValue : resultSet.getColumn(columnName)) {
				try {
					BigDecimal columnNum = new BigDecimal(colunmnValue);
					if (inValid(columnNum, rangeList)) {
						inValid = true;
					}
				} catch (Exception ignore) {
					if (logger.isWarnEnabled())
						logger.warn("A error occur when compare", ignore);
				}
				if (inValid)
					break;
			}
			if (inValid)
				break;
		}
		if (inValid) {
			if ((context.getRuntimeValue(runtimeValueKey) != null)) {
				ResultSet lastResultSet = (ResultSet) context.getRuntimeValue(runtimeValueKey);
				ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
				ResultSetMetaInfo lastMetaInfo = lastResultSet.getResultMetaInfo();
				metaInfo.removeAllColumnNames();
				for (String columnString : lastMetaInfo.getColumnNames()) {
					metaInfo.addColumnName(columnString);
				}
				resultSet.clearRows();
				for (int row = 0; row < lastResultSet.getRowLength(); ++row) {
					resultSet.addRow(lastResultSet.getRow(row));
				}
			} else {
				resultSet.clearRows();
			}
		} else {
			context.setRuntimeValue(runtimeValueKey, resultSet.clone());
		}
	}

	private boolean inValid(BigDecimal columnNum, ArrayList<Range> rangeList) {
		for (Range range : rangeList) {
			switch (range.operator) {
			case EQ:
				if (columnNum.compareTo(range.num) != 0)
					return true;
				break;
			case NE:
				if (columnNum.compareTo(range.num) == 0)
					return true;
				break;
			case LT:
				if (columnNum.compareTo(range.num) >= 0)
					return true;
				break;
			case LE:
				if (columnNum.compareTo(range.num) > 0)
					return true;
				break;
			case GT:
				if (columnNum.compareTo(range.num) <= 0)
					return true;
				break;
			case GE:
				if (columnNum.compareTo(range.num) < 0)
					return true;
				break;
			default:
				break;
			}
		}
		return false;
	}

	private static class Range {
		private final String operator;
		private final BigDecimal num;

		private Range(String operator, BigDecimal num) {
			if (!OPERATOR_SET.contains(operator))
				throw new IllegalArgumentException("Unsupported operator: " + operator);
			this.operator = operator;
			this.num = num;
		}
	}

}
