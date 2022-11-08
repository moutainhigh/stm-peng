/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * @author ziw
 * 
 */
public class PluginResultSetProcessorTestImpl implements
		PluginResultSetProcessor {

	private static final Log logger = LogFactory
			.getLog(PluginResultSetProcessorTestImpl.class);

	/**
	 * 
	 */
	public PluginResultSetProcessorTestImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor#process(com
	 * .mainsteam.oc.pluginprocessor.ResultSet,
	 * com.mainsteam.stm.pluginprocessor.PluginParameter)
	 */
	@Override
	public void process(ResultSet r, ProcessParameter arg1,
			PluginSessionContext context) {
		if (logger.isInfoEnabled()) {
			logger.info("remove all data but the first row.");
		}
		int rowLength = r.getRowLength();
		for (int i = rowLength - 1; i > 0; i--) {
			rowLength = r.getRowLength();
			if (logger.isDebugEnabled()) {
				logger.debug("process remove row " + i);
			}
			r.removeRow(i);
		}
	}
}
