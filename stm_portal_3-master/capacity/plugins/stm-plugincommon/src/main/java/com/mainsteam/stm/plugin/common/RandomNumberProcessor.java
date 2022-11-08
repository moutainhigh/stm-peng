package com.mainsteam.stm.plugin.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * 
 * @author yet
 *
 */
public class RandomNumberProcessor implements PluginResultSetProcessor{
	private static final Log logger = LogFactory.getLog(RandomNumberProcessor.class);
	
	private static final String STARTNUMBER = "StartNumber";
	private static final String ENDNUMBER = "EndNumber";
	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context)
			throws PluginSessionRunException {
		int startNum = 0;
		int endNum = 0;
		ParameterValue[] parameterValues = parameter.listParameterValues();
		for (ParameterValue parameterValue : parameterValues) {
			if (parameterValue.getKey().equals(STARTNUMBER)) {
				startNum = Integer.valueOf(parameterValue.getValue());
			}else if (parameterValue.getKey().equals(ENDNUMBER)) {
				endNum = Integer.valueOf(parameterValue.getValue());
			}else {
				logger.debug("parameter error:"+parameterValue.getKey()+"-"+parameterValue.getValue());
			}
		}
		int randomNum = (int) Math.round(Math.random()*(endNum-startNum)+startNum);
		
		
		resultSet.putValue(0, 0, randomNum);
	}

}
