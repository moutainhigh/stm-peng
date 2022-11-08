package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.List;

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
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class RowFilterByInstPropertyProcessor implements PluginResultSetProcessor {
	
	private static final String INST_PROPERTY_KEY = "InstPropertyKey"; //实例属性值，用于子资源实例与结果集索引值进行比较
	private static final String INDEX_COLUMN_TITLE = "IndexColumnTitle";//结果集索引列名，根据列表取出当前索引列
	private static final Log logger = LogFactory.getLog(RowFilterByInstPropertyProcessor.class);
	
	public static void main(String[] argv) throws Exception {

		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.putValue(0, 0, "0");

		List<String> list = new ArrayList<String>();
		list.add("NICPhysAddress");

		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(list);
		ResultSet resultset = new ResultSet(pluginResultSet, metaInfo);

		RowFilterByInstPropertyProcessor processor = new RowFilterByInstPropertyProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue param = new ParameterValue();
		param.setKey("MacAddress");
		param.setValue("");
		parameter.addParameter(param);
		processor.process(resultset, parameter, null);
		System.out.println(resultset);

	}
	
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		
		if(resultSet == null)
			return ;
		if (logger.isDebugEnabled()){
			logger.debug("RowFilterByInstPropertyProcessor Process Starts.");
		}
		ParameterValue[] parameterValues = parameter.listParameterValues();
		String instancePropertyKey = null;
		String indexColumnTitle = null;
		for (ParameterValue parameterValue : parameterValues){
			switch (parameterValue.getKey()){
			case INDEX_COLUMN_TITLE:
				indexColumnTitle = StringUtils.trimToEmpty(parameterValue.getValue());
				break;
			case INST_PROPERTY_KEY:
				instancePropertyKey = StringUtils.trimToEmpty(parameterValue.getValue());
				break;
			default:
				if (logger.isWarnEnabled()){
					logger.warn("Invalid Process Parameter Key " + parameterValue.getKey() + " for RowFilterByInstPropertyProcessor.");
				}
			}
		}
		
		String[] indexColumnValues = resultSet.getColumn(indexColumnTitle);
		if(indexColumnValues != null && StringUtils.isNotBlank(instancePropertyKey)) {
			int keepRowIndex = -1;
			for(int i = 0; i < indexColumnValues.length ;i++) {
				if(StringUtils.equals(instancePropertyKey, StringUtils.trimToEmpty(indexColumnValues[i]))){
					keepRowIndex = i;
					break;
				}
			}
			if(keepRowIndex == -1){
				if(logger.isWarnEnabled()){
					logger.warn("RowFilterByInstPropertyProcessor clear resultSet. instancePropertyKey is " + instancePropertyKey +
							", indexColumnTitle is " + indexColumnTitle + ".\r\n ResultSet is " + resultSet);
				}
				resultSet.clearRows();
				return ;
			}
			String[] rowValues = resultSet.getRow(keepRowIndex);
			if(rowValues != null) {
				resultSet.clearRows();
				resultSet.addRow(rowValues);
			}
		}else{
			if(logger.isWarnEnabled()){
				logger.warn("RowFilterByInstPropertyProcessor can not get column index. instancePropertyKey is " + instancePropertyKey +
						", indexColumnTitle is " + indexColumnTitle + ".\r\n ResultSet is " + resultSet);
			}
		}
		
		if (logger.isDebugEnabled()){
			logger.debug("RowFilterProcessor Process Finished.");
		}
	}

}
