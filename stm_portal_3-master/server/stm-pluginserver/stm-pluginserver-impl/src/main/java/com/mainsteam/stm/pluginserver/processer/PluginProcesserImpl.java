/**
 * 
 */
package com.mainsteam.stm.pluginserver.processer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.PluginProcesser;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginserver.message.PluginResultImpl;
import com.mainsteam.stm.pluginserver.process.manage.ProcessBeanManagerImpl;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * @author ziw
 * 
 */
public class PluginProcesserImpl implements PluginProcesser {

	private static final Log logger = LogFactory.getLog(PluginProcesser.class);

	private ProcessBeanManagerImpl processBeanManager;

	private InstanceCollectorLogController collectorLogController = InstanceCollectorLogController
			.getInstance();

	public void setProcessBeanManager(ProcessBeanManagerImpl processBeanManager) {
		this.processBeanManager = processBeanManager;
	}

	/**
	 * 
	 */
	public PluginProcesserImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginserver.PluginProcesser#process(com.mainsteam
	 * .oc.pluginserver.message.PluginRequest,
	 * com.mainsteam.stm.pluginsession.PluginSessionContext,
	 * com.mainsteam.stm.pluginprocessor.ResultSet)
	 */
	@Override
	public PluginResult process(PluginRequest request,
			PluginSessionContext context, ResultSet set)
			throws PluginServerExecuteException {
		if (logger.isTraceEnabled()) {
			logger.trace("process pluginresult start");
		}
		/**
		 * 下面，对结果集数据进行处理
		 * 
		 */
		PluginProcessParameter[] processParameters = request
				.getPluginProcessParameters();
		if (processParameters != null && processParameters.length > 0) {
			for (PluginProcessParameter pluginProcessParameter : processParameters) {
				long temp = System.currentTimeMillis();
				try {
					PluginResultSetProcessor processor = processBeanManager
							.getPluginResultSetProcessor(pluginProcessParameter
									.getProcessorClass());
					if (logger.isDebugEnabled()) {
						logger.debug("executePlugin processor start."
								+ processor);
					}
					processor.process(set,
							pluginProcessParameter.getParameter(), context);
					if (logger.isDebugEnabled()) {
						StringBuilder builder = new StringBuilder();
						builder.append("process end.processor=").append(
								processor.getClass());
						builder.append(" process.resultset=\r\n").append(
								buildPrintPluginRequestResult(request, set,
										pluginProcessParameter));
						logger.debug(builder.toString()+"\nlossTime="+(System.currentTimeMillis()-temp));
					} else if (logger.isInfoEnabled()
							&& collectorLogController.isLog(request
									.getResourceInstId())) {
						StringBuilder builder = new StringBuilder();
						builder.append("process end.processor=").append(
								processor.getClass());
						builder.append(" process.resultset=\r\n").append(
								buildPrintPluginRequestResult(request, set,
										pluginProcessParameter)+"\nlossTime="+(System.currentTimeMillis()-temp));
						logger.info(builder.toString());
					}
				} catch (Throwable e) {
					String msg = buildPrintPluginRequestResult(request, set,
							pluginProcessParameter);
					if (logger.isErrorEnabled()) {
						logger.error("executePlugin error."+"\nlossTime="+(System.currentTimeMillis()-temp) + msg, e);
					}
					if (e instanceof BaseException) {
						throw new PluginServerExecuteException(msg, e);
					} else {
						throw new PluginServerExecuteException(
								ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PROCESSER_EXECUTE,
								msg, e);
					}
				}
			}
		}
		PluginResult result = new PluginResultImpl(set, 0,
				request.getRequestId());
		if (logger.isTraceEnabled()) {
			logger.trace("process pluginresult end");
		}
		return result;
	}

	private String buildPrintPluginRequestResult(PluginRequest request,
			PluginResultSet set, PluginProcessParameter pluginProcessParameter) {
		StringBuilder builder = new StringBuilder();
		builder.append(" request.getRequestId()=")
				.append(request.getRequestId()).append(" request.getBatch()=")
				.append(request.getBatch())
				.append(" request.getPluginRequestType()=")
				.append(request.getPluginRequestType())
				.append(" request.getResourceId()=")
				.append(request.getResourceId())
				.append(" request.getResourceInstId()=")
				.append(request.getResourceInstId())
				.append(" request.getMetricId()=")
				.append(request.getMetricId()).append('\n')
				.append(" resultset=")
				.append(set == null ? null : set.toString());

		builder.append('\n').append(" processer=")
				.append(pluginProcessParameter.getProcessorClass());
		ProcessParameter processParameter = pluginProcessParameter
				.getParameter();
		builder.append('\n').append(" processParameter=").append('[');
		if (processParameter != null) {
			ParameterValue[] ps = processParameter.listParameterValues();
			if (ps != null && ps.length > 0) {
				builder.append('{').append(ps[0].getKey()).append(',')
						.append(ps[0].getValue()).append('}');
				for (int i = 1; i < ps.length; i++) {
					builder.append(',').append('{').append(ps[i].getKey())
							.append(',').append(ps[i].getValue()).append('}');
				}
			}
		}
		builder.append(']');
		return builder.toString();
	}

}
