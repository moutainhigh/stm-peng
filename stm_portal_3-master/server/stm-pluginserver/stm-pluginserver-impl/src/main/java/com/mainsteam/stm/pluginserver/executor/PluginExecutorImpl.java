/**
 * 
 */
package com.mainsteam.stm.pluginserver.executor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.PluginExecutor;
import com.mainsteam.stm.pluginserver.executor.cache.PluginSessionResultCacheByBatch;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginMapExecutorParameter;

/**
 * 调用PluginSession执行，并对数据进行处理
 * 
 * @author ziw
 * 
 */
public class PluginExecutorImpl implements PluginExecutor {

	private static final Log logger = LogFactory
			.getLog(PluginExecutorImpl.class);

	private PluginSessionResultCacheByBatch pluginSessionResultCacheByBatch;

	private InstanceCollectorLogController collectorLogController = InstanceCollectorLogController
			.getInstance();

	/**
	 * 
	 */
	public PluginExecutorImpl() {
	}

	/**
	 * @param pluginSessionResultCacheByBatch
	 *            the pluginSessionResultCacheByBatch to set
	 */
	public final void setPluginSessionResultCacheByBatch(
			PluginSessionResultCacheByBatch pluginSessionResultCacheByBatch) {
		this.pluginSessionResultCacheByBatch = pluginSessionResultCacheByBatch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginserver.PluginExecutor#executePlugin(com.mainsteam
	 * .oc.pluginserver.message.PluginRequest,
	 * com.mainsteam.stm.pluginsession.PluginSessionContext,
	 * com.mainsteam.stm.pluginsession.PluginSession)
	 */
	@Override
	public ResultSet executePlugin(PluginRequest request,
			PluginSessionContext context, PluginSessionPool pool)
			throws PluginSessionRunException {
		if (request == null) {
			throw new RuntimeException("executePlugin request is null.");
		}
		if (pool == null) {
			throw new RuntimeException("executePlugin session pool is null.");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("executePlugin start");
		}
		ResultSet set = null;
		PluginResultSet resultSet = pluginSessionResultCacheByBatch
				.selectCachedResultSet(request);
		boolean isNeedLock = pool.getMaxActive() > 1;
		/**
		 * 如果已经能从缓存中拿到数据，直接返回。
		 */
		if (resultSet == null) {
			try {
				if (isNeedLock) {
					pluginSessionResultCacheByBatch.lock(request);
				}
				resultSet = pluginSessionResultCacheByBatch
						.selectCachedResultSet(request);
				if (resultSet == null) {
					PluginSession session = null;
					try {
						session = pool.borrowSession();
					} catch (Throwable e) {
						String message = buildPrintPluginRequestResult(request,
								resultSet);
						if (logger.isErrorEnabled()) {
							logger.error("executePlugin borrowSession \r\n"
									+ message, e);
						}
						throw new PluginSessionRunException(
								ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT,
								message, e);
					}
					try {
						long temp = System.currentTimeMillis();
						resultSet = session.execute(
								request.getPluginExecutorParameter(), context);
						if (resultSet == null && logger.isErrorEnabled()) {
							logger.error("executePlugin session.execute end. pluginResult=\r\n"
									+ buildPrintPluginRequestResult(request,
											resultSet)
									+ "\nlossTime="
									+ (System.currentTimeMillis() - temp));
						} else if (logger.isDebugEnabled()) {
							logger.debug("executePlugin session.execute end. pluginResult=\r\n"
									+ buildPrintPluginRequestResult(request,
											resultSet)
									+ "\nlossTime="
									+ (System.currentTimeMillis() - temp));
						} else if (logger.isInfoEnabled()
								&& collectorLogController.isLog(request
										.getResourceInstId())) {
							logger.info("executePlugin session.execute end. pluginResult=\r\n"
									+ buildPrintPluginRequestResult(request,
											resultSet)
									+ "\nlossTime="
									+ (System.currentTimeMillis() - temp));
						}
						pluginSessionResultCacheByBatch.cacheResultSet(request,
								resultSet);
					} catch (BaseException e) {
						String message = buildPrintPluginRequestResult(request,
								resultSet);
						if (logger.isErrorEnabled()) {
							logger.error("executePlugin\r\n" + message, e);
						}
						throw new PluginSessionRunException(message, e);
					} catch (Throwable e) {
						String message = buildPrintPluginRequestResult(request,
								resultSet);
						if (logger.isErrorEnabled()) {
							logger.error("executePlugin\r\n" + message, e);
						}
						throw new PluginSessionRunException(
								ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_EXECUTE,
								message, e);
					} finally {
						if (session != null) {
							try {
								pool.returnSession(session);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("executePlugin", e);
								}
							}
						}
					}
				}
			} catch (RuntimeException e) {
				String message = buildPrintPluginRequestResult(request,
						resultSet);
				if (logger.isErrorEnabled()) {
					logger.error("executePlugin\r\n" + message, e);
				}
				throw new PluginSessionRunException(
						ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_EXECUTE,
						message, e);
			} finally {
				if (isNeedLock) {
					pluginSessionResultCacheByBatch.unlock(request);
				}
			}
		}
		if (resultSet != null) {
			set = new ResultSet(resultSet, request.getResultSetMetaInfo());
		} else {
			set = new ResultSet(null, request.getResultSetMetaInfo());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("executePlugin end");
		}
		return set;
	}

	private String buildPrintPluginRequestResult(PluginRequest request,
			PluginResultSet set) {
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
				.append(set == null ? null : set.toString())
				.append(" executeParameter=[");

		PluginExecutorParameter<?> parameter = request
				.getPluginExecutorParameter();
		if (parameter != null) {
			if (parameter instanceof PluginArrayExecutorParameter) {
				Parameter[] parameters = ((PluginArrayExecutorParameter) parameter)
						.getParameters();
				if (parameters != null && parameters.length > 0) {
					Parameter exeP = parameters[0];
					if (exeP != null) {
						builder.append('{').append(exeP.getKey()).append(',')
								.append(exeP.getValue()).append('}');
					} else {
						builder.append("{null}");
					}
					for (int i = 1; i < parameters.length; i++) {
						exeP = parameters[i];
						builder.append(',');
						if (exeP != null) {
							builder.append('{').append(exeP.getKey())
									.append(',').append(exeP.getValue())
									.append('}');
						} else {
							builder.append("{null}");
						}
					}
				}
			} else if (parameter instanceof PluginMapExecutorParameter) {
				Map<String, Parameter> parameters = ((PluginMapExecutorParameter) parameter)
						.getParameters();
				if (parameters != null && parameters.size() > 0) {
					boolean notFirst = false;
					for (Parameter exeP : parameters.values()) {
						if (notFirst) {
							builder.append(',');
						} else {
							notFirst = true;
						}
						if (exeP != null) {
							builder.append('{').append(exeP.getKey())
									.append(',').append(exeP.getValue())
									.append('}');
						} else {
							builder.append("{null}");
						}
					}
				}
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
