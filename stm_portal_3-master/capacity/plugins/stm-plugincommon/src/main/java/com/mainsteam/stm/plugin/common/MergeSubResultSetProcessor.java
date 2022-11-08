package com.mainsteam.stm.plugin.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class MergeSubResultSetProcessor implements PluginResultSetProcessor {

	private static final Log logger = LogFactory.getLog(MergeSubResultSetProcessor.class);
	private static final String KEY_TABLE = "Table";
	private static final String KEY_JOIN = "Join";
	private static final String AS = "AS";
	private static final String SPLIT = ",";
	private static final String DOT = ".";
	private static final String SPACE = " ";
	private static final String NAME_REGEX = "^[_a-zA-Z][_a-zA-Z0-9]*";
	private static final String CLAUSE_REGEX = "\\G(LEFT |RIGHT )?JOIN (\\S+) ON (\\S+) = (\\S+) ?";
	private static final int CLAUSE_GROUPCOUNT = 4;
	private static final int JOIN_TYPE = 1;
	private static final int TABLE_NAME = 2;
	private static final int LEXPRESSION = 3;
	private static final int REXPRESSION = 4;
	private static final String LEFT_JOIN = "LEFT";
	private static final String RIGHT_JOIN = "RIGHT";
	private static final String INNER_JOIN = null;
	
	
	private ResultSet targetResult, sourceResult;
	private Map<String, Set<String>> tableMap;
	
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		
		sourceResult = (ResultSet) resultSet.clone();
		targetResult = resultSet;
		
		tableMap = tableParser(parameter.getParameterValuesByKey(KEY_TABLE), resultSet.getResultMetaInfo());
		
		List<ParameterValue> joinList = parameter.getParameterValuesByKey(KEY_JOIN);
		if (joinList.size() > 1)
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Too many Join key parameters");
		String joinString = joinList.get(0).getValue();
		
		
		HashSet<String> usedTableSet = new HashSet<String>();
		int last = -1;
		
		String tableName = joinString.substring(0, joinString.indexOf(SPACE));
		if (!tableMap.containsKey(tableName))
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "No such table " + tableName + " in " + joinString);
		initialize(tableName);
		usedTableSet.add(tableName);
		joinString = joinString.substring(joinString.indexOf(SPACE) + SPACE.length());
		
		Pattern pattern = Pattern.compile(CLAUSE_REGEX);
		Matcher matcher = pattern.matcher(joinString);
		while (matcher.find()){
			if (matcher.groupCount() != CLAUSE_GROUPCOUNT)
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid Join clause expression " + joinString);
			tableName = matcher.group(TABLE_NAME);
			if (!tableMap.containsKey(tableName))
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "No such table " + tableName + " in " + joinString);
			
			String lExpression = matcher.group(LEXPRESSION);
			String rExpression = matcher.group(REXPRESSION);
			int lDOTPos = lExpression.indexOf(DOT);
			int rDOTPos = rExpression.indexOf(DOT);
			if (lDOTPos < 0 || rDOTPos < 0)
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid equation expreesion : " + lExpression + " = " + rExpression);
			
			String lTableName = lExpression.substring(0, lDOTPos);
			String lColumn = lExpression.substring(lDOTPos + DOT.length());
			String rTableName = rExpression.substring(0, rDOTPos);
			String rColumn = rExpression.substring(rDOTPos + DOT.length());
			if (!tableMap.containsKey(lTableName) || !tableMap.containsKey(rTableName) || !tableMap.get(lTableName).contains(lColumn) || !tableMap.get(rTableName).contains(rColumn))
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid equation expreesion : " + lExpression + " = " + rExpression);
			
			String lKey, rKey;
			if (lTableName.equals(tableName) && usedTableSet.contains(rTableName)){
				lKey = rColumn; rKey = lColumn;
			} else if (rTableName.equals(tableName) && usedTableSet.contains(lTableName)){
				lKey = lColumn; rKey = rColumn;
			} else {
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid equation expreesion : " + lExpression + " = " + rExpression);
			}
			
			if (matcher.group(JOIN_TYPE) == INNER_JOIN)
				innerJoin(tableName, lKey, rKey);
			else {
				switch (matcher.group(JOIN_TYPE)) {
				case LEFT_JOIN:
					leftJoin(tableName, lKey, rKey);
					break;
				case RIGHT_JOIN:
					rightJoin(tableName, lKey, rKey);
					break;
				default:
					if (logger.isWarnEnabled())
						logger.warn("Unknown Join Type : " + matcher.group(JOIN_TYPE));
					break;
				}
			}
			last = matcher.end();
			usedTableSet.add(tableName);
		}
		if (last != joinString.length())
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid Join clause expression " + joinString);
	}

	private void initialize(String tableName) {
		targetResult.getResultMetaInfo().removeAllColumnNames();
		targetResult.clearRows();
		ResultSetMetaInfo targetMetaInfo = targetResult.getResultMetaInfo();
		ResultSetMetaInfo sourceMetaInfo = sourceResult.getResultMetaInfo();
		Set<String> rColumnSet= tableMap.get(tableName);
		Iterator<String> rIterator = rColumnSet.iterator();
		while (rIterator.hasNext()){
			String columnName = rIterator.next();
			targetMetaInfo.addColumnName(columnName);
			int column = sourceMetaInfo.indexOfColumnName(columnName);
			for (int row = 0; row < sourceResult.getRowLength(); ++row){
				targetResult.putValue(row, columnName, sourceResult.getValue(row, column));
			}
		}
		
	}

	private void rightJoin(String tableName, String lKey, String rKey) {
		// TODO Auto-generated method stub
	}

	private void leftJoin(String tableName, String lKey, String rKey) {
		// TODO Auto-generated method stub
	}

	private void innerJoin(String tableName, String lKey, String rKey) {
		ResultSetMetaInfo targetMetaInfo = targetResult.getResultMetaInfo();
		ResultSetMetaInfo sourceMetaInfo = sourceResult.getResultMetaInfo();
		int lIndex = targetMetaInfo.indexOfColumnName(lKey);
		int rIndex = sourceMetaInfo.indexOfColumnName(rKey);
		Set<String> rColumnSet= tableMap.get(tableName);
		Iterator<String> rIterator = rColumnSet.iterator();
		while (rIterator.hasNext())
			targetMetaInfo.addColumnName(rIterator.next());
		for (int targetRow = 0; targetRow < targetResult.getRowLength(); ++targetRow){
			String lValue = targetResult.getValue(targetRow, lIndex);
			if (lValue == null){
				targetResult.removeRow(targetRow); --targetRow;
			} else {
				int rRow = -1;
				for (int sourceRow = 0; sourceRow < sourceResult.getRowLength(); ++sourceRow){
					String rValue = sourceResult.getValue(sourceRow, rIndex);
					if (lValue.equals(rValue)){
						rRow = sourceRow; break;
					}
				}
				if (rRow < 0){
					targetResult.removeRow(targetRow); --targetRow;
				} else {
					rIterator = rColumnSet.iterator();
					while (rIterator.hasNext()){
						String columnName =  rIterator.next();
						targetResult.putValue(targetRow, columnName, sourceResult.getValue(rRow, sourceMetaInfo.indexOfColumnName(columnName)));
					}
				}
			}
		}	
	}

	private Map<String, Set<String>> tableParser(List<ParameterValue> groupList, ResultSetMetaInfo metaInfo) throws PluginSessionRunException {
		HashMap<String, Set<String>> ret = new HashMap<String, Set<String>>();
		HashSet<String> usedcolumnSet = new HashSet<String>();
		for (ParameterValue group : groupList) {
			String columnString = group.getValue();
			int ASPos = columnString.indexOf(AS);
			if (ASPos < 0)
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Keyword " + AS + "not found in " + columnString);
			String tableName = columnString.substring(ASPos + AS.length()).trim();
			if (!tableName.matches(NAME_REGEX))
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid table name in " + columnString);
			if (ret.containsKey(tableName))
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Repeated table name in " + columnString);
			String[] tableColumns = columnString.substring(0, ASPos).split(SPLIT);
			HashSet<String> tableColumnSet = new HashSet<String>();
			for (String tableColumn : tableColumns) {
				tableColumn = tableColumn.trim();
				if (metaInfo.indexOfColumnName(tableColumn) < 0)
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Invalid column name " + tableColumn + " in " + columnString);
				if (usedcolumnSet.contains(tableColumn))
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Repeated column name " + tableColumn + " in " + columnString);
				usedcolumnSet.add(tableColumn);
				tableColumnSet.add(tableColumn);
			}
			ret.put(tableName, tableColumnSet);
		}
		if (metaInfo.getColumnLength() > usedcolumnSet.size())
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER, "Impossbile to seperate the resultSet because of insufficient column parameters");
		return ret;
	}
}
