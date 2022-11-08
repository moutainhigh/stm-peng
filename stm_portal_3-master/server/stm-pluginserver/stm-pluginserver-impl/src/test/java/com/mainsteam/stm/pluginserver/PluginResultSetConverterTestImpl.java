/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.ResultSet;

/**
 * @author ziw
 * 
 */
public class PluginResultSetConverterTestImpl implements
		PluginResultSetConverter {

	private static final Log logger = LogFactory
			.getLog(PluginResultSetConverterTestImpl.class);

	/**
	 * 
	 */
	public PluginResultSetConverterTestImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginprocessor.PluginResultSetConverter#convert(com
	 * .mainsteam.oc.pluginprocessor.ResultSet,
	 * com.mainsteam.stm.pluginprocessor.PluginParameter)
	 */
	@Override
	public ConverterResult[] convert(ResultSet arg0, ProcessParameter arg1) {
		if (logger.isInfoEnabled()) {
			logger.info("convert. start use the fistrow,firstcolumn as metric data.");
		}
		String[] result = new String[1];
		result[0] = arg0.getRow(0)[0];
		ConverterResult[] resultData = new ConverterResult[1];
		resultData[0] = new ConverterResult(null, result);
		return resultData;
	}

}
