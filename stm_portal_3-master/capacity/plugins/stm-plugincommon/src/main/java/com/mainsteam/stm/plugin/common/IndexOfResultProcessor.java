package com.mainsteam.stm.plugin.common;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class IndexOfResultProcessor implements PluginResultSetProcessor {
	private static final Log logger = LogFactory.getLog(IndexOfResultProcessor.class);
	
	private static final String SELECT_INDEX="SelectIndex";
	private static final String CONTENT_SEPERATOR = "\\s+";
	private static final String INDEX_SEPERATOR = ",";
	
	
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		if (logger.isDebugEnabled())
			logger.debug("RowFilterProcessor Process Starts.");
		ParameterValue[] params = parameter.listParameterValues();
		String indice[] = null;
		for(ParameterValue parameterValue : params){
			if(StringUtils.equalsIgnoreCase(parameterValue.getKey(), SELECT_INDEX)){
				indice = parameterValue.getValue().split(INDEX_SEPERATOR);
			}
		}
		String[] result=resultSet.getValue(0,0).trim().split(CONTENT_SEPERATOR);
		for (int i = 0; i < indice.length; ++i) {
			try {
				
				int index = Integer.valueOf(indice[i]);
				if(index<0){
					resultSet.putValue(0, i, result[result.length+index]);
				}else {
					resultSet.putValue(0, i, result[index-1]);
				}
				
			} catch (NumberFormatException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("invalid number " + indice[i], e);
				}
			}
		}
	}

}
