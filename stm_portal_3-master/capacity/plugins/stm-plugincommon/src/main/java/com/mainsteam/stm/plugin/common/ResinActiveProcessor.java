package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class ResinActiveProcessor implements PluginResultSetProcessor {
	@Override
	public void process(ResultSet paramResultSet,
			ProcessParameter paramProcessParameter,
			PluginSessionContext paramPluginSessionContext)
			throws PluginSessionRunException {
		if (null != paramResultSet) {
			String acount[] = paramResultSet.getColumn(1);
			for (int i = 0; i < acount.length; i++) {
				if (Integer.parseInt(acount[i]) < 0) {
					paramResultSet.putValue(i, 1, "0");
				}
			}

		}

	}

}
