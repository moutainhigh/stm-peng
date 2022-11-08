/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestMonitor;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;
import com.mainsteam.stm.pluginserver.obj.ReponseIndexData;

/**
 * @author ziw
 * 
 */
public class PluginRequestMonitorImpl implements PluginRequestMonitor {

	private static final Log logger = LogFactory
			.getLog(PluginRequestMonitor.class);
	private static final long TIMEOUT_OFFSET = 10 * 60 * 1000;// 10 MINUTES
	private static final PluginServerExecuteException TIMEOUT_EXCEPTION = new PluginServerExecuteException(
			ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_TIMEOUT, "Timeout:"
					+ TIMEOUT_OFFSET);
	private Map<Long, ReponseData> monitorMap;
	private int count = 0;
	private long batch = -1;

	public PluginRequestMonitorImpl(List<PluginRequest> requests) {
		monitorMap = new HashMap<Long, ReponseData>(requests.size());
		for (PluginRequest pluginRequest : requests) {
			if (logger.isDebugEnabled()) {
				logger.debug("PluginRequestMonitor reqeustId="
						+ pluginRequest.getRequestId());
			}
			List<ReponseIndexData> reponseIndexDatas = null;
			ReponseData data = new ReponseData(pluginRequest.getMetricId(),
					pluginRequest.getResourceInstId(),
					pluginRequest.getResourceId(),
					pluginRequest.getCollectTime(), TIMEOUT_EXCEPTION,
					reponseIndexDatas, pluginRequest.getRequestId());
			monitorMap.put(pluginRequest.getRequestId(), data);
			if (batch < 0) {
				batch = pluginRequest.getBatch();
			}
		}
		count = requests.size();
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("recieveRequest batch=").append(batch).append(" count=")
					.append(count);
			logger.debug(b.toString());
		}
	}

	public void forceFinish() {
		this.count = 0;
		monitorMap.clear();
		monitorMap = null;
	}

	public boolean isFinished() {
		return count <= 0;
	}

	public synchronized void recieveResult(ReponseData result, Long requestId) {
		if (monitorMap.containsKey(requestId)) {
			monitorMap.put(requestId, result);
			count--;
			if (logger.isDebugEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("recieveResult batch=").append(batch)
						.append(" count=").append(count);
				logger.debug(b.toString());
			}
			if (isFinished()) {
				synchronized (this) {
					this.notifyAll();
				}
			}
		}
	}

	public List<ReponseData> waitCalculateDatas()
			throws PluginServerExecuteException {
		synchronized (this) {
			try {
				this.wait(TIMEOUT_OFFSET);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.count = 0;
		}
		/**
		 * 超时也返回，返回已经收到的指标数据。
		 */
		if (!isFinished()) {
			if (logger.isErrorEnabled()) {
				logger.error("waitCalculateDatas Timeout.");
			}
		}
		// if (isFinished()) {
		return new ArrayList<>(monitorMap.values());
		// }
		// throw new PluginServerExecuteException(
		// ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_TIMEOUT, "Timeout:"
		// + TIMEOUT_OFFSET);
	}
}
