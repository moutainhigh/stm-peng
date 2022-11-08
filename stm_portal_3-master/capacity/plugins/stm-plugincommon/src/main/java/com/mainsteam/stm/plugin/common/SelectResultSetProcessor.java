package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
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

public class SelectResultSetProcessor implements PluginResultSetProcessor {

	private static final String OPERATION = "operation";
	private static final String OPERATION2 = "operation2";

	private static final String S_AS_AS_S = "\\s+as|AS\\s+";

	private static final String DIFFER_COUNT = "DIFFER_COUNT";

	private static final String LAST = "LAST";

	private static final String FIRST = "FIRST";

	private static final String MIN = "MIN";

	private static final String MAX = "MAX";

	private static final String JOIN = "JOIN";

	private static final String AVG = "AVG";

	private static final String COUNT = "COUNT";

	private static final String SUM = "SUM";

	private static final String SUB_EXPRESSION = "subExpression";

	private static final String DOUBLE = "double";

	private static final String NOT_CONVERTER = "notConverter";

	private static final String IS_CONTINUE = "isContinue";

	private static final String EVALUATE = "evaluate";

	private static final String TEST_EVALUATE = "testEvaluate";

	private static final String REPLACE_COMMA = "replaceComma";

	private static final String SELECT = "SELECT";
	
	private static final String OPTIONAL = "Optional";

	private static final Log logger = LogFactory
			.getLog(SelectResultSetProcessor.class);

	private static Map<String, Pattern> patternMap;

	private static JexlEngine engine;

	static {
		patternMap = new HashMap<String, Pattern>();
		Pattern pattern = Pattern.compile("\\w+\\(\\w+,\\s*\\w+\\)",
				Pattern.CASE_INSENSITIVE);
		patternMap.put(REPLACE_COMMA, pattern);
		pattern = Pattern
				.compile(
						"\\+|-|\\*|/|\\||\\&|%|\\?|sum\\(|count\\(|avg\\(|join\\(|max\\(|min\\(|last\\(|first\\(|differ_count\\(",
						Pattern.CASE_INSENSITIVE);
		patternMap.put(TEST_EVALUATE, pattern);
		// pattern =
		// Pattern.compile("((sum|count|avg|join|max|min|last|first|differ_count)\\(\\s*\\w+[.\\d]*{1}\\s*[\\+*/-]*\\s*\\w+[.\\d]*{1}\\s*\\))",
		// Pattern.CASE_INSENSITIVE);
		pattern = Pattern
				.compile(
						"((sum|count|avg|join|max|min|last|first|differ_count)\\([^)]+\\))",
						Pattern.CASE_INSENSITIVE);
		patternMap.put(EVALUATE, pattern);
		pattern = Pattern
				.compile(
						"^(sum|count|avg|join|max|min|last|first|differ_count)(\\([^)]+\\))",
						Pattern.CASE_INSENSITIVE);
		patternMap.put(SUB_EXPRESSION, pattern);
		pattern = Pattern.compile("\\+|-|\\*|/|\\||\\&|%|\\?");
		patternMap.put(IS_CONTINUE, pattern);
		pattern = Pattern.compile("^(-?\\d+.?\\d*[Ee]{0,1}-?\\d+)$");
		patternMap.put(DOUBLE, pattern);
		pattern = Pattern.compile("\\s*[^/]+(\\s*[+*-]\\s*[^/]+\\s*)+?");
		// 如果是如果只是存在加减乘法则不需要将int转化成double进行运算
		patternMap.put(NOT_CONVERTER, pattern);
		// 四则运算
		pattern = Pattern.compile("\\+|-|\\*|/|%");
		patternMap.put(OPERATION, pattern);
		
		// 四则运算
		pattern = Pattern.compile("\\d+\\s*[+*-/]\\s*\\d+");
		patternMap.put(OPERATION2, pattern);
		

		pattern = null;

		engine = new JexlEngine();
		engine.setLenient(false);
		engine.setSilent(false);
	}

	public static void main(String[] argv) {

		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.putValue(0, 0, "PCIe2 2-port 10GbE SR Adapter (a21910071410d003) ");
		//pluginResultSet.putValue(0, 1, "20");
//		pluginResultSet.putValue(0, 2, "10");
//		
//		pluginResultSet.putValue(0, 3, "100");

	//	pluginResultSet.putValue(1, 0, "up");
		//pluginResultSet.putValue(1, 1, "11");
//		pluginResultSet.putValue(1, 2, "10");
//		pluginResultSet.putValue(1, 3, "100");
//		pluginResultSet.putValue(2, 0, "5");
//		pluginResultSet.putValue(2, 1, "100");
//		pluginResultSet.putValue(2, 2, "10");
//		pluginResultSet.putValue(2, 3, "100");
//		 pluginResultSet.putValue(3, 0, "5");
//		 pluginResultSet.putValue(3, 1, "100");
//		 pluginResultSet.putValue(3, 2, "10");
//		 pluginResultSet.putValue(3, 3, "100");
//		 pluginResultSet.putValue(4, 0, "5");
//		 pluginResultSet.putValue(4, 1, "100");
//		 pluginResultSet.putValue(4, 2, "10");
//		 pluginResultSet.putValue(4, 3, "100");
//		 pluginResultSet.putValue(5, 0, "5");
//		 pluginResultSet.putValue(5, 1, "100");
//		 pluginResultSet.putValue(5, 2, "10");
//		 pluginResultSet.putValue(5, 3, "100");

		List<String> list = new ArrayList<String>();
		//list.add("avail");
		list.add("ifInfo");
		//list.add("ifUnit");
//		list.add("ifOutDiscards");
//		list.add("ifOutUcastPkts");
		// list.add("ifInErrors");

		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(list);
		ResultSet resultset = new ResultSet(pluginResultSet, metaInfo);

		SelectResultSetProcessor processor = new SelectResultSetProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue param = new ParameterValue();
		param.setKey(SELECT);
		// param.setValue("SELECT (ifSpeed1_ifSpeed2!=0?(ifSpeed1_ifSpeed2<500000?(ifSpeed1_ifSpeed2*1000000):ifSpeed1_ifSpeed2):(ifSpeed2_ifSpeed1<500000?(ifSpeed2_ifSpeed1*1000000):ifSpeed2_ifSpeed1)) as ifSpeed");
		// param.setValue("SELECT ((sum(ifInDiscards)/(sum(ifInUcastPkts_ifHCInUcastPkts)+sum(ifInNucastPkts+ifInDiscards+ifInErrors)))*100) as inDiscardsRate");
		// param.setValue("SELECT differ_count(ifInUcastPkts_ifHCInUcastPkts)");
		param.setValue("SELECT first(ifInfo) as ifType");
		
//		ParameterValue param1 = new ParameterValue();
//		param1.setType("ResourceProperty");
//		param1.setKey("ifSpeed");
//		param1.setValue(null);
		
		
		parameter.addParameter(param);
		//parameter.addParameter(param1);
		
		processor.process(resultset, parameter, null);
		System.out.println(resultset);

	}

	/**
	 * Select选择器将对结果集进行运算，对于一个结果集其实就是一个二维表（类似数据库表），第一行必须是表头（对应表字段名）
	 * 通过对应的表头才能获取到某行某列的数据。在这个选择器中，将支持主要的基本运算，建议按照标准的SQL语句的写法来。
	 * 实际上，我们也是通过正则表达式来解析SQL语句（注：只有SELECT部分）
	 */
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) {

		// if(logger.isDebugEnabled()) {
		// String[] columnNames =
		// resultSet.getResultMetaInfo().getColumnNames();
		// boolean flag = false;
		// if(columnNames != null && StringUtils.equals(columnNames.toString(),
		// "totalMemSize,cpuRate")) {
		// flag = true;
		// }
		//
		// }
		ParameterValue[] Parameters = parameter.listParameterValues();
		String sentence = "";
		Map<String, String> replaces = new HashMap<String, String>(2, 0.5f);// 参数替换值
		Map<String, String> optionalSelects = new HashMap<String, String>(4);//可选的选择器
		for (ParameterValue param : Parameters) {
			if (StringUtils.equalsIgnoreCase(SELECT, param.getKey())) {//默认选择表达式
				sentence = param.getValue();
			} else if(StringUtils.equalsIgnoreCase(param.getType(), OPTIONAL)){//可选表达式
				optionalSelects.put(param.getKey(), param.getValue());
			} else {
				replaces.put(param.getKey(), param.getValue());
			}
			
		}
		
		if(!optionalSelects.isEmpty()) {//如果备选选择表达式不为空的话，则需要从备选中挑出指定的表达式
			String select = null;
			String removeKey = null;
			if(!replaces.isEmpty()) {
				Set<String> keySet = replaces.keySet();
				Iterator<String> iterator = keySet.iterator();
				while(iterator.hasNext()) {
					String key = iterator.next();
					String optionalKey = replaces.get(key);
					select = optionalSelects.get(optionalKey);
					if(StringUtils.isNotBlank(select)) {
						removeKey = key;
						break;
					}
				}
				
			}
			if(StringUtils.isNotBlank(select)) {//如果匹配到可选表达式，则将默认的表达式清除
				sentence = select;
				replaces.remove(removeKey); //删除表达式指定参数，不用于参数替换
			}
			
		}

		if (null != sentence
				&& StringUtils.startsWithIgnoreCase(sentence.trim(), SELECT)) {
			sentence = StringUtils.removeStartIgnoreCase(sentence, SELECT);
			sentence = StringUtils.trim(sentence);
			if (!replaces.isEmpty()) {// 参数替换
				if(logger.isDebugEnabled()) {
					logger.debug("SelectProcessor SQL before replace : " + sentence + ";\r\n SelectProcessor resultSet is :"
								+ resultSet);
				}
				StringBuffer sb = new StringBuffer();
				Set<String> keySet = replaces.keySet();
				Iterator<String> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					// 如果参数值为空，则替换成1，因为1乘以任何数字都是本身
					if(replaces.get(key) != null) {
						sentence = StringUtils
								.replace(sentence, "${" + key + "}", replaces.get(key));
					}else{//参数替换有问题，肯定会导致计算出错
						if(logger.isWarnEnabled()) {
							try {
								logger.warn("SelectProcessor SQL : " + sentence + ";\r\n SelectProcessor resultSet is :"
									+ resultSet.clone() + " \r\n" + "Null Parameters is [" + key + " : null].");
							} catch (Exception e) {}
						}
						resultSet.clearRows();
						return ;
						
					}
					sb.append(key).append(" : ").append(replaces.get(key)).append(";");
				}
				
				if(logger.isDebugEnabled()) {
					logger.debug("SelectProcessor SQL after replaced : " + sentence + ";\r\n SelectProcessor resultSet is :"
								+ resultSet + " \r\n" + "Replace Parameters : [" + sb.toString() + "]");
				}
			}

			/*
			 * 解析一个稍微复杂的类SQL字符串，首先需要将括号内的逗号提取出来，然后按照字段间的逗号分隔,将提取出来的部分用Map存储
			 */
			
			long replaceCurrentTime = System.currentTimeMillis();
			Pattern pattern = patternMap.get(REPLACE_COMMA);
			Matcher matcher = pattern.matcher(sentence);
			Map<String, String> map = new HashMap<String, String>(2, 0.5f);
			int matchFind = 0;
			while (matcher.find()) {
				String matchStr = matcher.group(matchFind);
				map.put("${MatchPattern" + matchFind + "}", matchStr);
				sentence = StringUtils.replaceOnce(sentence, matchStr,
						"${MatchPattern" + matchFind + "}");
				matchFind++;
			}
			if(logger.isInfoEnabled()) {
				long replaceAfterTime = System.currentTimeMillis();
				long costs = replaceAfterTime - replaceCurrentTime;
				if(costs > 1000) {
					logger.info("SelectProcessor replace pattern costs time :" + costs + " ms. Expresssion is [" + 
							sentence + "]. \r\nResultSet is " + resultSet);
				}
			}
			/*
			 * 将类似sum(price1, price2)的字符串先进行替换，然后再按照逗号进行分割
			 */
			String[] haveColumns = StringUtils.split(sentence, ",");
			List<String> columnList = new ArrayList<String>(5);
			for (String columnStr : haveColumns) {
				if (!map.isEmpty()) {
					Set<String> keySet = map.keySet();
					Iterator<String> iterator = keySet.iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
						if (columnStr.contains(key)) {
							columnStr = StringUtils.replaceOnce(columnStr, key, map.get(key));
							sentence = StringUtils.replaceOnce(sentence, key, map.get(key));
						}
					}
				}
				columnList.add(columnStr.trim());
			}
			/*
			 * 至此按照逗号分隔完成，Set里面都是字段，接下来需要对各个字段进行解析，同样是通过正则表达式来完成。
			 */
			this.splitField(columnList, resultSet, sentence);

		}

	}

	/**
	 * 按正则表达式解析字段。 通过as来分隔
	 * 
	 * @param fieldList
	 *            字段列表
	 * @param resultSet
	 *            返回结果集
	 * @param expr
	 *            选择器表达式
	 */
	private void splitField(List<String> fieldList, ResultSet resultSet, String expr) {

		// 使用as分隔，分隔表达式和别名存储在String数组中，key依然是字段值
		Map<String, String[]> splitAsMap = new LinkedHashMap<String, String[]>(6, 0.5f);
		for (String field : fieldList) {
			String[] entry = field.split(S_AS_AS_S);
			if (entry != null) {
				splitAsMap.put(field, entry);
			}
		}

		// 遍历columnNames，匹配到Map中的key值，然后用resultSet里面的值进行替换
		String[] columns = resultSet.getResultMetaInfo().getColumnNames();
		if (!splitAsMap.isEmpty()) {

			Set<String> keySet = splitAsMap.keySet();
			for (String columnTitle : columns) {
				Iterator<String> keyIterator = keySet.iterator();
				boolean isExist = false;// 判断当前表头是否存在所有需要的字段中，如果不存在则需要删除相应列的数据
				while (keyIterator.hasNext()) {
					String key = keyIterator.next();
					if (key.contains(columnTitle)) { // 如果字段值中包含表头，则根据表头获取当前列的值
						isExist = true;
					}
				}
				if (!isExist) {
					resultSet.removeColumnByTitle(columnTitle);
				}

			}

			// 需要一个map来保存字段名称与包含的列
			Map<String, List<String>> columnTitlesMap = new HashMap<String, List<String>>(
					6, 0.5f);
			Iterator<String> keyIterator = keySet.iterator();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				List<String> columnList = new ArrayList<String>(3);
				for (String columnTitle : columns) {
					if (key.contains(columnTitle)) {
						columnList.add(columnTitle.trim());
					}
				}
				columnTitlesMap.put(key, columnList);
			}
			/*
			 * 在这里需要判断是否需要进入计算阶段，因为很大一部分使用选择器都是过滤其中的某些列，而并不需要计算。但是需要考虑到使用as|
			 * AS进行重命名的情况
			 * 使用正则表达式进行判断是否存在运算符或者某些特定的函数（例如sum,count等），如果不存在则直接返回过滤即可。
			 */
			long testEvCurrentTime = System.currentTimeMillis();
			boolean isContinueEvaluate = this.testEvaluate(expr, resultSet, splitAsMap);
			if(logger.isInfoEnabled()) {
				long replaceAfterTime = System.currentTimeMillis();
				long costs = replaceAfterTime - testEvCurrentTime;
				if(costs > 1000) {
					logger.info("SelectProcessor testEvaluate method costs time :" + costs + " ms. Expresssion is [" + 
							expr + "]. \r\nResultSet is " + resultSet);
				}
			}
			//当所有需要的列都获取得到值时，然后遍历splitAsMap，使用其中的value用表达式进行计算，另取一个方法
			if (isContinueEvaluate)
				this.evaluate(splitAsMap, columnTitlesMap, resultSet);

		}

	}

	/**
	 * 判断是否进行下一步的数学运算
	 * 
	 * @param expr
	 *            select表达式
	 * @param resultSet
	 *            结果集
	 * @param splitMap
	 *            使用as进行切割的map
	 * @return true表示没有匹配到，false需要下一步运算
	 */
	private boolean testEvaluate(String expr, ResultSet resultSet,
			Map<String, String[]> splitMap) {
		Pattern pattern = patternMap.get(TEST_EVALUATE);
		Matcher matcher = pattern.matcher(expr);
		if (!matcher.find()) {// 进行meta的替换
			Set<String> keySet = splitMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String[] values = splitMap.get(key);
				if (values != null && values.length == 2) {
					String alias = values[1];
					int columnIndex = resultSet.getResultMetaInfo()
							.indexOfColumnName(values[0].trim());
					if (columnIndex != -1)
						resultSet.getResultMetaInfo().setColumnName(
								columnIndex, alias.trim());
				}
			}
			// System.out.println(resultSet);
			return false;
		} else
			return true;
	}

	/**
	 * 表达式计算
	 * 
	 * @param splitMap
	 *            字段名与使用As切割后的数组
	 * @param columnTitlesMap
	 *            key:select字段名，value为字段中包含的列
	 * @param result
	 *            结果集
	 */
	private void evaluate(Map<String, String[]> splitMap,
			Map<String, List<String>> columnTitlesMap, ResultSet result) {

		Set<String> splitKeySet = splitMap.keySet();
		Iterator<String> splitIterator = splitKeySet.iterator();
		// 保存一个临时结果集(Map)，最终赋值给最终结果集。因为直接在原有结果集中更新数据会比较复杂
		Map<String, List<String>> tempMap = new LinkedHashMap<String, List<String>>(6, 0.5f);
		// 匹配求/列统计/平均值等表达式
		/*
		 * 因为select表达式可能支持多个类似sum等这个的操作，所有需要先将类似sum的表达式先去计算，然后再进行最后一步的运算
		 */
		Pattern sumPattern = patternMap.get(EVALUATE);
		while (splitIterator.hasNext()) {
			String key = splitIterator.next();
			String[] fieldValues = splitMap.get(key);
			List<String> tempList = new ArrayList<String>(6);
			if (fieldValues != null && fieldValues.length > 0) {
				
				long  subExpressionCurrent = System.currentTimeMillis();
				
				String expression = fieldValues[0];
				List<String> columnTitleList = columnTitlesMap.get(key);
				Matcher sumMatcher = sumPattern.matcher(expression);
				// 保存子表达式map
				List<String> subExpressions = new ArrayList<String>(6);
				while (sumMatcher.find()) {
					subExpressions.add(sumMatcher.group(1));
				}
				
				if(logger.isInfoEnabled()) {
					long replaceAfterTime = System.currentTimeMillis();
					long costs = replaceAfterTime - subExpressionCurrent;
					if(costs > 1000) {
						logger.info("SelectProcessor evaluate pattern costs time :" + costs + " ms.\r\nResultSet is " + result);
					}
				}

				if (!subExpressions.isEmpty()) {
					long dealWithSubExpression = System.currentTimeMillis();
					
					List<String[]> subExpressionList = dealWithSubExpression(
							subExpressions, result, columnTitleList);
					if(logger.isInfoEnabled()) {
						long replaceAfterTime = System.currentTimeMillis();
						long costs = replaceAfterTime - dealWithSubExpression;
						if(costs > 1000) {
							logger.info("SelectProcessor dealWithSubExpression method costs time :" + costs + " ms. \r\nResultSet is " + result);
						}
					}
					if (!subExpressionList.isEmpty()) {
						for (String[] replaceStrs : subExpressionList) {
							expression = StringUtils.replaceOnce(expression,
									replaceStrs[0], replaceStrs[1]);
						}
					}
				}
				/*
				 * 经过子资源表达式后再进行最后一道运算机制，这里需要注意的是，如果我们的表达式如果不在包含需要运算的其他列了，则不再需要在重新赋值
				 * 根据原有结果集的行遍历，保证新旧结果集行一致
				 */
				boolean flag = false;
				for (String columnTitle : columnTitleList) {
					// 根据列名获取当前列的位置
					if (expression.contains(columnTitle)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					// 这里需要判断是否需要进行四则运算，可能存在只需要选择一列的情况，并不需要进行四则运算
					Pattern goPattern = patternMap.get(IS_CONTINUE);
					Matcher goMatcher = goPattern.matcher(expression);
					if (!goMatcher.find()) {
						try {
							String[] strs = result.getColumn(expression.trim());
							tempList = Arrays.asList(strs);
						} catch (RuntimeException e) {
							if (logger.isWarnEnabled()) {
								logger.warn("select Processor can't get columns [" + expression
										+ "]," + e.getMessage(), e);
							}
							tempList.add(null);
						}
					} else {
						// 当表达式只存在简单的加减乘法时，则不需要将int转换成double进行处理
						Pattern notConverter = patternMap.get(NOT_CONVERTER);
						boolean isConverter = true;
						if (notConverter.matcher(expression).matches())
							isConverter = false;
						/*
						 * 因为前面已经进行子表达式计算，并且可能会合并了相关的行数，比如sum计算后，相关的列就已经合并成了一行。
						 * 为了计算准确，需要将不需要的列在结果集去除，
						 * 需要在接来的运算中，根据表达式中的包含的列名取得最大的行数再遍历计算
						 */
						List<String> removeColumns = new ArrayList<String>(3);
						for (String columnTitle : columnTitleList) {
							if (!StringUtils.contains(expression, columnTitle)) {
								result.removeColumnByTitle(columnTitle);
								removeColumns.add(columnTitle);
							}
						}

						columnTitleList.removeAll(removeColumns);
						
						long current = System.currentTimeMillis();
						for (int i = 0; i < result.getRowLength(); i++) {
							JexlContext jexlContext = new MapContext();
							boolean isGet = false; // 标志是否取得对应行列的值，如果当前行列值为空，则进行下一个运算
							if (result.getRow(i) == null
									|| result.getRow(i).length == 0)
								continue;
							for (String columnTitle : columnTitleList) {
								// 根据列名获取当前列的位置
								int column = result.getResultMetaInfo()
										.indexOfColumnName(columnTitle);
								String str = result.getValue(i, column);
								// 因为结果集中存在一些默认为Null的值，所以需要判断如果进行四则运算的话，则需要过滤结果为null的值
								if (patternMap.get(OPERATION)
										.matcher(expression).find()
										&& str == null)
									continue;
								// if(str != null){
								try {
									if (isConverter) {
										double d = Double.parseDouble(str);
										jexlContext.set(columnTitle, d);
									} else {
										jexlContext
												.set(columnTitle, str.trim());
									}

								} catch (Exception e) {
									jexlContext.set(columnTitle, str);
								}
								isGet = true;
								// }
								// else{ //没有取到则进行下一步循环
								// isGet = false;
								// //jexlContext.set(columnTitle, str);
								// }
							}
							if (!isGet)
								continue;

							try {

								Expression e = (Expression) engine
										.createExpression(expression);
								Object obj = (Object) e.evaluate(jexlContext);
								tempList.add(obj != null ? obj.toString()
										: null);
							} catch (Exception e) {
								// 当出现异常的运算条件时，抛出此异常。例如，一个整数“除以零”时，抛出此类的一个实例。这时我们设置为0.
								if (e.getCause() instanceof ArithmeticException) {
									//logger.error("ArithmeticException:" + expression);
									tempList.add("0");
								} else {
									logger.error(
											"SelectResultSetProcessor Exception:["
													+ expression + "].\r\n"
													+ e.getMessage(), e);
									tempList.add(null);
								}
							}
						}
						
						if(logger.isInfoEnabled()) {
							long replaceAfterTime = System.currentTimeMillis();
							long costs = replaceAfterTime - current;
							if(costs > 1000) {
								logger.info("SelectProcessor isContinue jexl costs time :" + costs + " ms. \r\nResultSet is " + result);
							}
						}
					}
				} else { // 这里需要判断是否需要进行四则运算
					long current = System.currentTimeMillis();
					Pattern goPattern = patternMap.get(OPERATION2);
					Matcher goMatcher = goPattern.matcher(expression);
					if (goMatcher.find()) {
						try {
							JexlContext jexlContext = new MapContext();
							Expression e = engine.createExpression(expression);
							Object obj = e.evaluate(jexlContext);
							tempList.add(obj != null ? obj.toString() : null);
						} catch (Exception e) {
							if (logger.isWarnEnabled())
								logger.warn("Select处理器错误,当前表达式为【" + expression
										+ "】", e);
							// 当出现异常的运算条件时，抛出此异常。例如，一个整数“除以零”时，抛出此类的一个实例。这时我们设置为0.
							if (e.getCause() != null
									&& e.getCause() instanceof ArithmeticException)
								tempList.add("0");
							else
								tempList.add(null);
						}
					} else {
						// 过滤掉（）
						expression = expression.trim().replace("(", "")
								.replace(")", "");
						tempList.add(expression);
					}
					
					if(logger.isInfoEnabled()) {
						long replaceAfterTime = System.currentTimeMillis();
						long costs = replaceAfterTime - current;
						if(costs > 1000) {
							logger.info("SelectProcessor isNot Continue jexl costs time :" + costs + " ms. \r\nResultSet is " + result);
						}
					}

				}

				// 添加新结果集的表头
				if (fieldValues.length == 2) {
					String alias = fieldValues[1];
					if (StringUtils.isEmpty(alias))
						tempMap.put(fieldValues[0], tempList);
					else
						tempMap.put(alias, tempList);
				} else {
					tempMap.put(fieldValues[0], tempList);
				}
			}
		}
		// 将result清空。
		result.clearRows();
		result.getResultMetaInfo().removeAllColumnNames();
		Set<String> columnSet = tempMap.keySet();
		Iterator<String> columnIterator = columnSet.iterator();
		int columnIndex = 0;

		while (columnIterator.hasNext()) {
			String key = columnIterator.next();
			List<String> valueList = tempMap.get(key);
			for (int i = 0; i < valueList.size(); i++) {
				String str = valueList.get(i);
				result.putValue(i, columnIndex, str);
			}
			result.getResultMetaInfo().addColumnName(key.trim());
			columnIndex++;
		}
		// System.out.println(result.toString());
	}

	/**
	 * 处理select中的所有的子表达式运算
	 * 
	 * @param subExpressions
	 *            子表达式集合
	 * @param result
	 *            原始结果集
	 * @param columnTitles
	 *            该子表达式包含结果集中的所有列
	 * @return 返回一个包含子表达式名称和一个子表达式的返回值的集合
	 */
	private List<String[]> dealWithSubExpression(List<String> subExpressions,
			ResultSet result, List<String> columnTitles) {

		List<String[]> resultList = new ArrayList<String[]>(
				subExpressions.size());
		for (String expression : subExpressions) {
			Pattern subPattern = patternMap.get(SUB_EXPRESSION);
			Matcher subMatcher = subPattern.matcher(expression);
			String differentEv = "";
			String subEx = "";
			if (subMatcher.find()) {
				if (subMatcher.groupCount() == 2) {
					differentEv = subMatcher.group(1);
					subEx = subMatcher.group(2);
				}
			}
			// 进行除去sum等里面的运算
			if (subEx != null) {
				long subCurrentTime = System.currentTimeMillis();
				// 保存运算后的临时结果集，还需要用来进行下一步的运算
				List<String> tempResults = new ArrayList<String>(
						result.getColumnLength());
				// 根据原有结果集的行遍历，保证新旧结果集行一致
				for (int i = 0; i < result.getRowLength(); i++) {
					JexlContext jexlContext = new MapContext();
					boolean isGet = false;
					for (String columnTitle : columnTitles) {
						// 根据列名获取当前列的位置
						int column = result.getResultMetaInfo()
								.indexOfColumnName(columnTitle);
						String str = result.getValue(i, column);
						if (str != null) {
							try {
								double d = Double.parseDouble(str);
								jexlContext.set(columnTitle, d);
							} catch (NumberFormatException e) {
								jexlContext.set(columnTitle, str);
							}
							isGet = true;
						} else if (!patternMap.get(OPERATION).matcher(subEx)
								.find())
							jexlContext.set(columnTitle, str);
					}
					if (!isGet)
						continue;
					String str = null;
					try {
						Expression e = engine.createExpression(subEx);
						Object obj = e.evaluate(jexlContext);
						if (obj != null)
							str = obj.toString();
					} catch (Exception e) {
						if (logger.isWarnEnabled())
							logger.warn("Select处理器错误,当前计算公式为：【" + subEx + "】", e);
					}
					tempResults.add(str);
				}
				
				if(logger.isInfoEnabled()) {
					long replaceAfterTime = System.currentTimeMillis();
					long costs = replaceAfterTime - subCurrentTime;
					if(costs > 1000) {
						logger.info("SelectProcessor dealWithSubExpression method jexl costs time :" + costs + " ms.\r\nResultSet is " + result);
					}
				}

				if (differentEv != null) {
					differentEv = differentEv.toUpperCase();
					switch (differentEv) {
					case SUM:// 列求和
						this.sum(tempResults);
						break;
					case COUNT:// 求列总数
						this.count(tempResults);
						break;
					case AVG:// 列平均数
						this.avg(tempResults);
						break;
					case JOIN:// 合并列
						this.join(tempResults);
						break;
					case MAX:// 列最大数
						this.max(tempResults);
						break;
					case MIN:// 列最小数
						this.min(tempResults);
						break;
					case FIRST:// 列第一个数
						this.first(tempResults);
						break;
					case LAST:// 列最后一个数
						this.last(tempResults);
						break;
					case DIFFER_COUNT:// 除去相同的值，列总数
						this.differCount(tempResults);
						break;
					default:
						break;
					}
				}
				// 注：如果是整数，则需要将数字之间的逗号（,）去掉，否则计算会报错
				String tempStr = null;
				try {
					tempStr = tempResults.get(0);
				} catch (IndexOutOfBoundsException e) {
				}
				if (tempStr != null)
					tempStr = StringUtils.replace(tempStr, ",", "");
				String[] singleResult = new String[] { expression, tempStr };
				resultList.add(singleResult);

			}

		}
		return resultList;

	}

	/**
	 * 求和运算
	 * 
	 * @param list
	 */
	private void sum(List<String> list) {

		String[] array = new String[list.size()];
		list.toArray(array);
		// Pattern pattern = Pattern.compile("^\\d+\\.?\\d+$");
		double resultDouble = 0.0d;
		for (int i = 0; i < array.length; i++) {
			try {
				if (array[i] != null) {
					double d = Double.parseDouble(array[i]);
					resultDouble += d;
				}
			} catch (Exception e) {
				continue;
			}
		}
		list.clear();
		list.add(resultDouble + "");

	}

	/**
	 * 统计某些列的个数
	 * 
	 * @param list
	 * @param expr
	 *            运算表达式
	 */
	private void count(List<String> list) {
		int count = list.size();
		list.clear();
		list.add(count + "");
	}

	/**
	 * 求平均值
	 * 
	 * @param list
	 *            求平均值的列表
	 * @param expr
	 *            运算表达式
	 */
	private void avg(List<String> list) {
		int count = list.size();
		if (count == 0) {
			list.clear();
			list.add(null);
			return;
		}

		double resultDouble = 0.0d;
		for (int i = 0; i < list.size(); i++) {
			try {
				if (list.get(i) != null) {
					double d = Double.parseDouble(list.get(i));
					resultDouble += d;
				}
			} catch (Exception e) {
				continue;
			}
		}

		resultDouble = resultDouble / count;
		list.clear();
		list.add(resultDouble + "");

	}

	/**
	 * 使用;进行字符串拼接
	 * 
	 * @param list
	 */
	private void join(List<String> list) {
		if (list.isEmpty())
			list.add(null);
		else {
			StringBuffer sb = new StringBuffer();
			for (String str : list) {
				if (StringUtils.isNotBlank(str))
					sb.append(StringUtils.trim(str)).append(";");
			}
			list.clear();
			String result = sb.toString();
			list.add(result.substring(0, result.length() - 1));
		}
	}

	/**
	 * 取得最大一个数字
	 * 
	 * @param list
	 */
	private void max(List<String> list) {
//		去掉list中不是数字的数据
		for (int i = 0; i < list.size(); i++) {
			try {
				Double.parseDouble(list.get(i));
			} catch (Exception e) {
				list.remove(i);
				i--;
			}
		}
		if (list.isEmpty()) {
			list.add(null);
		} else {
			int index = 0;
			double first = 0.0;
			try {// 需要屏蔽不合格式的字符串
				first = Double.parseDouble(list.get(index));
				while (index < list.size()) {
					double temp = Double.parseDouble(list.get(index));
					if (first < temp)
						first = temp;
					index++;
				}
			} catch (NumberFormatException e) {
				index++;
			}
			list.clear();
			list.add(first + "");
		}
	}

	/**
	 * 取得最小的一个数字
	 * 
	 * @param list
	 */
	private void min(List<String> list) {
		if (list.isEmpty()) {
			list.add(null);
		} else {
			int index = 0;
			double first = 0.0;
			try {// 需要屏蔽不合格式的字符串
				first = Double.parseDouble(list.get(index));
				while (index < list.size()) {
					double temp = Double.parseDouble(list.get(index));
					if (first > temp)
						first = temp;
					index++;
				}
			} catch (NumberFormatException e) {
				index++;
			}
			list.clear();
			list.add(first + "");
		}
	}

	/**
	 * 取得最后一个值
	 * 
	 * @param list
	 */
	private void last(List<String> list) {
		if (list.isEmpty()) {
			list.add(null);
		} else {
			String str = list.get(list.size() - 1);
			list.clear();
			list.add(str);
		}
	}

	/**
	 * 取得第一个值
	 * 
	 * @param list
	 */
	private void first(List<String> list) {
		if (list.isEmpty()) {
			list.add(null);
		} else {
			String str = list.get(0);
			list.clear();
			list.add(str);
		}
	}

	/**
	 * 统计列数，过滤重复值
	 */
	private void differCount(List<String> list) {
		if (list != null) {
			List<String> repeatList = new ArrayList<String>(5);
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i);
				if (repeatList.contains(str))
					continue;
				else {
					for (int j = i + 1; j < list.size(); j++) {
						String other = list.get(j);
						if (str != null && str.equals(other)) {
							repeatList.add(other);
						}

					}
				}
			}
			int noRepeat = list.size() - repeatList.size();
			list.clear();
			list.add(noRepeat + "");
		} else {
			list = new ArrayList<String>(0);
			list.add("0");
		}
	}

}
