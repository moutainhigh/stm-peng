/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.executor.MetricExecutor;
import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestManager;
import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestMonitor;
import com.mainsteam.stm.pluginserver.adapter.manager.RequestReponseSynch;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;

/**
 * 插件请求客户端
 * 
 * @author ziw
 * 
 */
public class PluginRequestClientImpl extends Thread implements
		PluginRequestClient {
	private static final Log logger = LogFactory
			.getLog(PluginRequestClientImpl.class);

	private PluginRequestReceiver receiver;

	private PluginRequestManager pluginRequestManager;

	private RequestReponseSynch reponseSynch;

	private LinkedList<PluginRequest> pluginRequestsList;

	private InstanceCollectorLogController collectorLogController = InstanceCollectorLogController
			.getInstance();
	
	@Override
	public void start() {
		super.setName("PluginRequestClient");
		pluginRequestsList = new LinkedList<>();
		super.start();
	}

	public void setReponseSynch(RequestReponseSynch reponseSynch) {
		this.reponseSynch = reponseSynch;
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public final void setExecutor(MetricExecutor executor) {
		executor.setRequestClient(this);
	}

	public void setReceiver(PluginRequestReceiver receiver) {
		this.receiver = receiver;
	}

	public void setPluginRequestManager(
			PluginRequestManager pluginRequestManager) {
		this.pluginRequestManager = pluginRequestManager;
	}

	/**
	 * 
	 */
	public PluginRequestClientImpl() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginserver.PluginRequestClient#sendPluginRequest
	 * (com.mainsteam.stm.pluginserver.message.PluginRequest)
	 */
	@Override
	public void sendPluginRequest(List<PluginRequest> requests) {
		for (Iterator<PluginRequest> iterator = requests.iterator(); iterator
				.hasNext();) {
			PluginRequest pluginRequest = iterator.next();
			/**
			 * 如果上一次的结果还没有返回，则丢弃这次请求。
			 */
			if (pluginRequestManager.isPluginRequestInProcess(pluginRequest)) {
				if (logger.isWarnEnabled()) {
					String msg = buildPrintPluginRequestResult(pluginRequest);
					logger.warn("sendPluginRequest drop." + msg);
				}
				iterator.remove();
				continue;
			}

			if (PluginRequestEnum.discovery_end == pluginRequest
					.getPluginRequestType()
					|| PluginRequestEnum.close_trap == pluginRequest
							.getPluginRequestType()) {
				continue;
			}
			// 缓存request对象
			pluginRequestManager.recordPluginRequest(pluginRequest);
			if (collectorLogController.isLog(pluginRequest
					.getResourceInstId()) && logger.isInfoEnabled()) {
				String msg = buildPrintPluginRequestResult(pluginRequest);
				logger.info("sendPluginRequest " + msg);
			}
		}
		if (requests.size() > 0) {
			synchronized (this) {
				pluginRequestsList.addAll(requests);
				this.notify();
			}
		}
	}

	@Override
	public List<ReponseData> executePluginRequest(List<PluginRequest> requests)
			throws PluginServerExecuteException {
		if (requests == null || requests.size() < 1) {
			logger.warn("executePluginRequest,but requests is null,please check!");
			return new ArrayList<>();
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("executePluginRequest requests.size="
						+ requests.size());
			}
		}
		// 缓存request对象
		for (PluginRequest pluginRequest : requests) {
			if (PluginRequestEnum.discovery_end == pluginRequest
					.getPluginRequestType()
					|| PluginRequestEnum.close_trap == pluginRequest
							.getPluginRequestType()) {
				continue;
			}
			// malachi in 插件请求执行 将请求记录到map
			pluginRequestManager.recordPluginRequest(pluginRequest);
			if (collectorLogController.isLog(pluginRequest
					.getResourceInstId()) && logger.isInfoEnabled()) {
				String msg = buildPrintPluginRequestResult(pluginRequest);
				logger.info("executePluginRequest " + msg);
			}
			String msg = buildPrintPluginRequestResult(pluginRequest);
			logger.info("malachi executePluginRequest " + msg);
		}

		PluginRequestMonitor monitor = reponseSynch
				.createPluginRequestMonitor(requests);
		synchronized (this) {
			pluginRequestsList.addAll(requests);
			this.notify();
		}
		return monitor.waitCalculateDatas();
	}

	@Override
	public void run() {
		while (true) {
			List<PluginRequest> requests = takeRequest();
			if (requests != null && requests.size() > 0) {
				// malachi in 插件请求执行 采集请求执行
				receiver.receivePluginRequest(requests);
			}
			synchronized (this) {
				if (this.pluginRequestsList.size() < 1) {
					try {
						this.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private List<PluginRequest> takeRequest() {
		List<PluginRequest> takeReturnList = null;
		synchronized (this) {
			if (this.pluginRequestsList.size() > 0) {
				takeReturnList = this.pluginRequestsList;
				this.pluginRequestsList = new LinkedList<>();
			}
		}
		return takeReturnList;
	}
}
