package com.mainsteam.stm.executor.generate.detail;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.handler.PluginResultMetaInfo;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

public class PluginResultMetaInfoGenerator {

	public PluginResultMetaInfoGenerator() {
	}

	public void generatePluginResultMetaInfo(PluginRequest request,
			MetricCollect collect) {
		PluginResultMetaInfo pluginResultMetaInfo = collect
				.getPluginResultMetaInfo();
		if (pluginResultMetaInfo != null) {
			ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
			String[] columnNames = pluginResultMetaInfo.getColumns();
			if (columnNames != null && columnNames.length > 0) {
				for (int i = 0; i < columnNames.length; i++) {
					metaInfo.addColumnName(columnNames[i]);
				}
			}
			request.setResultSetMetaInfo(metaInfo);
		}
	}
}
