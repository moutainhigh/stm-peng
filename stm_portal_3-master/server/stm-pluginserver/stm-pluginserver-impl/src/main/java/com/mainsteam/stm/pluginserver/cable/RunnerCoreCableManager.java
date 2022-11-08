/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginserver.PluginSessionExecutorRunner;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.context.PluginSessionContextManager;
import com.mainsteam.stm.pluginserver.executor.cache.PluginSessionResultCacheByBatch;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPoolManager;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author ziw
 * 
 */
public class RunnerCoreCableManager {

	private static final Log logger = LogFactory.getLog(RunnerCoreCableManager.class);

	private Map<String, RunnerCoreCable> coreCablesMap;

	private Map<String, RunnerCoreCable> discoveryCoreCablesMap;

	private MultiRunnerCoreCableBalencer balencer;

	private PluginSessionPoolManager poolManager;

	private PluginSessionContextManager pluginSessionContextManager;

	private PluginSessionResultCacheByBatch pluginSessionResultCacheByBatch;

	private Queue<CloseCableRequest> closedSessionCables;

	/**
	 * 
	 */
	public RunnerCoreCableManager() {
		coreCablesMap = new HashMap<String, RunnerCoreCable>();
		discoveryCoreCablesMap = new HashMap<>();
		closedSessionCables = new ConcurrentLinkedQueue<>();
	}

	/**
	 * @param contextManager
	 *            the contextManager to set
	 */
	public void setPluginSessionContextManager(PluginSessionContextManager pluginSessionContextManager) {
		this.pluginSessionContextManager = pluginSessionContextManager;
	}

	/**
	 * @param balencer
	 *            the balencer to set
	 */
	public final void setBalencer(MultiRunnerCoreCableBalencer balencer) {
		this.balencer = balencer;
		balencer.setCoreCableManager(this);
	}

	/**
	 * @param poolManager
	 *            the poolManager to set
	 */
	public final void setPoolManager(PluginSessionPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	public void setPluginSessionResultCacheByBatch(PluginSessionResultCacheByBatch pluginSessionResultCacheByBatch) {
		this.pluginSessionResultCacheByBatch = pluginSessionResultCacheByBatch;
	}

	public void start() {
		// do noting.
	}

	private void removeClosedCables() {
		if (closedSessionCables.size() > 0) {
			do {
				CloseCableRequest cableCloseRequest = closedSessionCables.poll();
				if (cableCloseRequest == null) {
					break;
				}
				closeRunnerCoreCable0(cableCloseRequest.cable, cableCloseRequest.isDiscovery);
			} while (true);
		}
	}

	public RunnerCoreCable matchCoreCable(PluginRequest request, PluginSessionExecutorRunner executorRunner) {
		removeClosedCables();
		Map<String, RunnerCoreCable> map = null;
		RunnerCoreCable cable = null;
		if (request.getPluginRequestType() == PluginRequestEnum.discovery) {
			map = this.discoveryCoreCablesMap;
		} else {
			map = this.coreCablesMap;
		}
		if (map.containsKey(request.getPluginSessionKey())) {
			cable = map.get(request.getPluginSessionKey());
		} else {
			MultiRunnerCoreCable multiRunnerCoreCable = null;
			if (request.getPluginRequestType() == PluginRequestEnum.discovery) {
				multiRunnerCoreCable = balencer.getMultiRunnerCoreCableForDiscovery();
			} else {
				multiRunnerCoreCable = balencer.selectMultiRunnerCoreCableForBanlencer();
			}
			PluginSessionPool pool = poolManager.getOrCreatePluginSessionPool(request);
			cable = new RunnerCoreCable(request.getPluginSessionKey(), pool, multiRunnerCoreCable);
//			synchronized (this) {
				map.put(request.getPluginSessionKey(), cable);
//			}
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder("create new CoreCable for ");
				b.append(request.getPluginSessionKey());
				b.append(" to ").append(cable.getMultiCoreCable().getCoreCableRange());
				b.append(" resourceId=" + request.getResourceId());
				b.append(" metricId=" + request.getMetricId());
				b.append(" instanceId=" + request.getResourceInstId());
				b.append(" batch=" + request.getBatch());
				PluginInitParameter initParameter = request.getPluginInitParameter();
				Parameter[] parameters = initParameter.getParameters();
				b.append(" initParameters[\n");
				for (Parameter parameter : parameters) {
					b.append(parameter.getKey()).append('=').append(parameter.getValue()).append(' ');
				}
				b.append('\n').append(']');
				logger.info(b.toString());
			}
		}
//		if (cable.getMaxActivity() > 1) {
		pluginSessionResultCacheByBatch.prepare(request);
//		}
		executorRunner.setRunnerCoreCable(cable);
		cable.put(executorRunner);
		return cable;
	}

	public boolean closeRunnerCoreCable(RunnerCoreCable cable, boolean isDiscovery) {
		if (cable == null || cable.getRemainingSize() > 0) {
			return false;
		}
		CloseCableRequest request = new CloseCableRequest();
		request.cable = cable;
		request.isDiscovery = isDiscovery;
		this.closedSessionCables.offer(request);
		MultiRunnerCoreCable multiRunnerCoreCable = cable.getMultiCoreCable();
		if (multiRunnerCoreCable != null) {
			multiRunnerCoreCable.removeCoreCable(cable);
		}
		return true;
	}

	private class CloseCableRequest {
		private RunnerCoreCable cable;
		private boolean isDiscovery;
	}

	private boolean closeRunnerCoreCable0(RunnerCoreCable cable, boolean isDiscovery) {
		if (cable == null || cable.getRemainingSize() > 0) {
			return false;
		}
		if (isDiscovery) {
			this.discoveryCoreCablesMap.remove(cable.getKey());
		} else {
			this.coreCablesMap.remove(cable.getKey());
		}
		PluginRequest request = new PluginRequest();
		request.setPluginSessionKey(cable.getKey());
		poolManager.closePluginSessionPool(request);
		if (this.pluginSessionContextManager != null) {
			this.pluginSessionContextManager.removeSessionContext(request);
		} else if (logger.isErrorEnabled()) {
			logger.error("closeRunnerCoreCable is null");
		}
		cable.close();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("closeRunnerCoreCable cable.key=");
			b.append(cable.getKey());
			logger.info(b.toString());
		}
		return true;
	}

	public boolean closeRunnerCoreCable(PluginRequest request) {
		Map<String, RunnerCoreCable> map = null;
		boolean isDiscovery = false;
		boolean result = false;
		if (request.getPluginRequestType() == PluginRequestEnum.discovery
				|| request.getPluginRequestType() == PluginRequestEnum.discovery_end) {
			map = this.discoveryCoreCablesMap;
			isDiscovery = true;
		} else {
			map = this.coreCablesMap;
		}
		if (map.containsKey(request.getPluginSessionKey())) {
			RunnerCoreCable cable = map.get(request.getPluginSessionKey());
			result = closeRunnerCoreCable(cable, isDiscovery);
			if (result && logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder("closeRunnerCoreCable request.RequestType=");
				b.append(request.getPluginRequestType());
				logger.info(b.toString());
			}
		}
		return result;
	}
}
