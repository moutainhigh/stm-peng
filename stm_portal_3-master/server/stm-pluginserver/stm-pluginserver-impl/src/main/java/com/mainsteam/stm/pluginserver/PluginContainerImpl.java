package com.mainsteam.stm.pluginserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.adapter.PluginResponseClient;
import com.mainsteam.stm.pluginserver.cable.RunnerCoreCable;
import com.mainsteam.stm.pluginserver.cable.RunnerCoreCableManager;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.context.PluginSessionContextImpl;
import com.mainsteam.stm.pluginserver.context.PluginSessionContextManager;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginserver.message.PluginResultImpl;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPoolManager;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class PluginContainerImpl implements PluginContainer {

	private static final Log logger = LogFactory
			.getLog(PluginContainerImpl.class);

	private PluginResponseClient pluginResponseClient;

	private PluginExecutor executor;

	private PluginProcesser pluginProcesser;

	private PluginSessionPoolManager poolManager;

	private final LinkedBlockingQueue<PluginRequest> pluginRequestQueue;

	private final LinkedBlockingQueue<PluginRequest> pluginRequestEndQueue;

	private final LinkedBlockingQueue<PluginResult> pluginResultQueue;

	private PluginSessionContextManager contextManager;

	private RunnerCoreCableManager coreCableManager;

	/**
	 * 执行结果返回工作线程
	 */
	private final Runnable reponseWorker;

	/**
	 * request对应session删除线程
	 */
	private final Runnable requestEndWorker;

	/**
	 * 对执行器器进行分派数据处理线路
	 */
	private final PluginRequestDispatchCoreCableRunner pluginRequestDispatchRunner;

	private volatile boolean started;

	private CapacityService capacityService;

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	private final InstanceCollectorLogController collectorLogController = InstanceCollectorLogController
			.getInstance();

	public PluginContainerImpl() {
		pluginRequestQueue = new LinkedBlockingQueue<>();
		pluginResultQueue = new LinkedBlockingQueue<>();
		/**
		 * 用来处理关闭request对应的sesion的请求，防止其关闭过程中死锁，造成其它请求无法过来。
		 */
		pluginRequestEndQueue = new LinkedBlockingQueue<>();
		
		requestEndWorker = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						PluginRequest request = pluginRequestEndQueue.take();
						if (request == null) {
							if (logger.isErrorEnabled()) {
								logger.error("pluginRequestEndRunner find an error plugin request.");
							}
							continue;
						}
						coreCableManager.closeRunnerCoreCable(request);
						// poolManager.closePluginSessionPool(request);
						if (logger.isInfoEnabled()) {
							logger.info("run close session "
									+ request.getPluginSessionKey()
									+ " requestType="
									+ request.getPluginRequestType());
						}
					} catch (Throwable e) {
						if (logger.isErrorEnabled()) {
							logger.error("requestEndWorker run", e);
						}
					}
				}
			}
		};

		reponseWorker = new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (pluginResultQueue.size() > 0) {
						List<PluginResult> pluginResults = new ArrayList<>(
								pluginResultQueue.size());
						pluginResultQueue.drainTo(pluginResults);
						try {
							handlePluginResult(pluginResults);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("run", e);
							}
						}
					} else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		};
		
		pluginRequestDispatchRunner = new PluginRequestDispatchCoreCableRunner();
	}

	public synchronized void start() {
		if (started) {
			if (logger.isWarnEnabled()) {
				logger.warn("PluginContainer has started.");
			}
			return;
		}
		
		new Thread(reponseWorker, "pluginReponseWorker").start();
		
		new Thread(pluginRequestDispatchRunner, "pluginRequestDispatchRunner")
				.start();
		
		new Thread(requestEndWorker, "pluginRequestEndRunner").start();
		
		started = true;
		
		if (logger.isInfoEnabled()) {
			logger.info("PluginContainer started.");
		}
	}

	public void setExecutor(PluginExecutor executor) {
		this.executor = executor;
	}

	public void setPoolManager(PluginSessionPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	public void setContextManager(PluginSessionContextManager contextManager) {
		this.contextManager = contextManager;
	}

	public void setPluginResponseClient(PluginResponseClient responseClient) {
		this.pluginResponseClient = responseClient;
	}

	/**
	 * @param coreCableManager
	 *            the coreCableManager to set
	 */
	public final void setCoreCableManager(
			RunnerCoreCableManager coreCableManager) {
		this.coreCableManager = coreCableManager;
	}

	/**
	 * @param pluginProcesser
	 *            the pluginProcesser to set
	 */
	public final void setPluginProcesser(PluginProcesser pluginProcesser) {
		this.pluginProcesser = pluginProcesser;
	}

	@Override
	public PluginResponseClient getPluginResponseClient() {
		return pluginResponseClient;
	}

	@Override
	public void handlePluginRequest(List<PluginRequest> requests) {
		if (logger.isTraceEnabled()) {
			logger.trace("handlePluginRequest start requests.size="
					+ requests.size());
		}
		// malachi in 处理一个plugin请求对象 请求放入队列 给线程循环执行
		for (PluginRequest req : requests) {
			try {
				this.pluginRequestQueue.put(req);
			} catch (InterruptedException e) {
				if (logger.isErrorEnabled()) {
					logger.error("handlePluginRequest", e);
				}
			}
			if (collectorLogController.isLog(req.getResourceInstId())) {
				if (logger.isInfoEnabled()) {
					String resultMsg = buildPrintPluginRequestResult(req);
					logger.info("handlePluginRequest " + resultMsg);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("handlePluginRequest end");
		}
	}

	public int getPluginRequestToBeDispathSize() {
		return this.pluginResultQueue.size();
	}

	@Override
	public void handlePluginResult(List<PluginResult> results) {
		if (logger.isDebugEnabled()) {
			logger.debug("handlePluginResult handlePluginResult="
					+ results.size());
		}
		this.pluginResponseClient.sendPluginReponse(results);
	}

	private class PluginRequestDispatchCoreCableRunner implements Runnable {
		@Override
		public void run() {
			while (true) {
				PluginRequest request;
				PluginSessionExecutorRunnerImpl executorRunner;
				try {
					// malachi in 插件执行
					request = pluginRequestQueue.take();
					if (request == null) {
						if (logger.isErrorEnabled()) {
							logger.error("PluginRequestDispatchCoreCableRunner find an error plugin request.");
						}
						continue;
					}

					if (PluginRequestEnum.discovery_end == request
							.getPluginRequestType()) {
						pluginRequestEndQueue.put(request);
						continue;
					}
					executorRunner = new PluginSessionExecutorRunnerImpl();
					executorRunner.req = request;
					coreCableManager.matchCoreCable(request, executorRunner);
					if (collectorLogController.isLog(request
							.getResourceInstId()) && logger.isInfoEnabled()) {
						String msg = buildPrintPluginRequestResult(request);
						logger.info("matchCoreCable " + msg);
					}else if(logger.isDebugEnabled()){
						String msg = buildPrintPluginRequestResult(request);
						logger.debug("matchCoreCable " + msg);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("PluginRequestDispatchRunner error.", e);
					}
				}
			}
		}
	}

	private boolean isAvailMetric(String resourceId, String metricId) {
		ResourceMetricDef metricDef = capacityService.getResourceMetricDef(
				resourceId, metricId);
		return metricDef != null
				&& metricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric;
	}

	private class PluginSessionExecutorRunnerImpl implements PluginSessionExecutorRunner {
		public PluginRequest req;
		public RunnerCoreCable cable;
		private String theadName;
		private void updateThreadName() {
			String thName = Thread.currentThread().getName();
			this.theadName = thName;
			int index = thName.indexOf('-', cable.getMultiCoreCable().getName()
					.length() + 2);
			if (index > 0) {
				thName = thName.substring(0, index + 1)
						+ req.getPluginSessionKey()+'-'+req.getRequestId();
			} else {
				thName = thName + '-' + req.getPluginSessionKey()+'-'+req.getRequestId();
			}
			Thread.currentThread().setName(thName);
		}
		
		private void recoverThreadName(){
			Thread.currentThread().setName(theadName);
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			updateThreadName();
			PluginResult result = null;
			ResultSet resultSet = null;
			if (collectorLogController.isLog(req.getResourceInstId()) && logger.isInfoEnabled()) {
				String msg = buildPrintPluginRequestResult(req);
				logger.info("ExecutorRunner " + msg);
			}
			String msg = buildPrintPluginRequestResult(req);
			logger.info("malachi ExecutorRunner " + msg);
			PluginSessionContext context = null;
			try {
				if (req.getBatch() <= cable.getToDropBatch() && !isAvailMetric(req.getResourceId(), req.getMetricId())// 只drop非可用性指標。
						&& (req.getPluginRequestType() == PluginRequestEnum.monitor
								|| req.getPluginRequestType() == PluginRequestEnum.discovery
								|| req.getPluginRequestType() == PluginRequestEnum.immediate)) {
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder();
						b.append("drop the metric request because session init is invalid.");
						b.append(buildPrintPluginRequestResult(req, null));
						logger.info(b.toString());
					}
				} else {
					context = contextManager.getContext(req);
					PluginSessionPool pool = null;
					if (req.getPluginRequestType() == PluginRequestEnum.close_trap) {
						pool = poolManager.getPluginSessionPool(req);
						if (pool == null) {
							return;
						}
						if (logger.isInfoEnabled()) {
							StringBuilder b = new StringBuilder();
							b.append("ExecutorRunner close the metric monitor for ");
							b.append(req.getResourceInstId()).append(':').append(req.getMetricId());
							logger.info(b.toString());
						}
						try {
							pool.destory();
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("ExecutorRunner", e);
							}
						}
						return;
					} else {
						pool = poolManager.getOrCreatePluginSessionPool(req);
					}

					BaseException cause = null;
					try {
						// malachi
						resultSet = executor.executePlugin(req, context, pool);
					} catch (BaseException e) {
						if (logger.isErrorEnabled()) {
							logger.error("ExecutorRunner", e);
						}
						resultSet = new ResultSet(null, req.getResultSetMetaInfo());
						cause = e;
						if (e instanceof PluginSessionRunException) {
							resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION,
									cause.getCause());
						} else {
							resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, cause);
						}
						if (e.getCode() == ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT) {
							cable.setToDropBatch(req.getBatch());
							if (e.getCause() != null && e.getCause() instanceof BaseException) {
								cause = (BaseException) e.getCause();
							}
						}

					} catch (Throwable e) {
						if (logger.isErrorEnabled()) {
							logger.error("ExecutorRunner", e);
						}
						resultSet = new ResultSet(null, req.getResultSetMetaInfo());
						resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, e);
						cause = new PluginServerExecuteException(
								ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_EXECUTE,
								buildPrintPluginRequestResult(req, resultSet), e);
					}
					result = pluginProcesser.process(req, context, resultSet);
					if (cause != null && result != null) {
						((PluginResultImpl) result).setCause(cause);
					}
				}
			} catch (BaseException e) {
				if (logger.isErrorEnabled()) {
					logger.error("ExecutorRunner", e);
				}
				if (resultSet == null) {
					resultSet = new ResultSet(null, req.getResultSetMetaInfo());
				}
				result = new PluginResultImpl(resultSet, -1, req.getRequestId());
				((PluginResultImpl) result).setCause(e);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("ExecutorRunner", e);
				}
				if (resultSet == null) {
					resultSet = new ResultSet(null, req.getResultSetMetaInfo());
				}
				result = new PluginResultImpl(resultSet, -1, req.getRequestId());
				((PluginResultImpl) result).setCause(
						new PluginServerExecuteException(ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_EXECUTE,
								buildPrintPluginRequestResult(req, resultSet), e));
			} finally {
				if (result == null) {
					result = new PluginResultImpl(null, -1, req.getRequestId());
				}
				try {
					pluginResultQueue.put(result);
				} catch (Exception e) {
				}
				/**
				 * 用于数据线路计数
				 */
				cable.computeAvgExecuteTime(System.currentTimeMillis() - start);
				this.req = null;
				this.cable = null;
				if (context != null) {
					((PluginSessionContextImpl) context).clear();
				}
				recoverThreadName();
			}
		}

		@Override
		public RunnerCoreCable getRunnerCoreCable() {
			return this.cable;
		}

		@Override
		public PluginRequest getPluginRequest() {
			return this.req;
		}

		@Override
		public void setRunnerCoreCable(RunnerCoreCable c) {
			this.cable = c;
		}
	}

	public static String buildPrintPluginRequestResult(PluginRequest request,
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
				.append(set == null ? null : set.toString());
		return builder.toString();
	}

	public static String buildPrintPluginRequestResult(PluginRequest request) {
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
				.append(request.getMetricId());
		return builder.toString();
	}
}
