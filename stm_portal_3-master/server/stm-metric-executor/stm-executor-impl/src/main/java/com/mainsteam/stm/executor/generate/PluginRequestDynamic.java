package com.mainsteam.stm.executor.generate;

import java.util.Map;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.executor.generate.callback.PluginRequestLazyGeneratorCallback;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class PluginRequestDynamic extends PluginRequest {
	private boolean isBindParameters = false;
	private PluginRequestLazyGeneratorCallback callback;

	public PluginRequestDynamic() {
	}

	public void setCallback(PluginRequestLazyGeneratorCallback callback) {
		this.callback = callback;
	}

	private synchronized void bindParameters() {
		if (!isBindParameters) {
			try {
				callback.generate();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			isBindParameters = true;
		}
	}

	@Override
	public PluginConvertParameter getPluginConvertParameter() {
		if(super.getPluginConvertParameter() == null){
			bindParameters();
		}
		return super.getPluginConvertParameter();
	}

	@Override
	public PluginExecutorParameter<?> getPluginExecutorParameter() {
		if(super.getPluginExecutorParameter() == null){
			bindParameters();
		}
		return super.getPluginExecutorParameter();
	}

	@Override
	public PluginInitParameter getPluginInitParameter() {
		if(super.getPluginInitParameter() == null){
			bindParameters();
		}
		return super.getPluginInitParameter();
	}

	@Override
	public PluginProcessParameter[] getPluginProcessParameters() {
		if(super.getPluginProcessParameters() == null){
			bindParameters();
		}
		return super.getPluginProcessParameters();
	}

	@Override
	public ResultSetMetaInfo getResultSetMetaInfo() {
		if(super.getResultSetMetaInfo() == null){
			bindParameters();
		}
		return super.getResultSetMetaInfo();
	}
}
