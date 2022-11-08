/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.manager.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestManager;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 * 
 */
public class PluginRequestManagerImpl implements PluginRequestManager {

	private static final Log logger = LogFactory
			.getLog(PluginRequestManagerImpl.class);

	private Map<Long, PluginRequest> requestMap;

	private Map<String, Boolean> instanceMetricMap;

	/**
	 * 
	 */
	public PluginRequestManagerImpl() {
		requestMap = new HashMap<Long, PluginRequest>();
		instanceMetricMap = new HashMap<>();
	}

	public synchronized void recordPluginRequest(PluginRequest req) {
		Long requestId = req.getRequestId();
		if (requestMap.containsKey(requestId)) {
			StringBuilder builder = new StringBuilder();
			builder.append("requestId has exist requestId=").append(requestId);
			builder.append(" request.resourceInstanceId=").append(
					req.getResourceInstId());
			builder.append(" request.metricId=").append(req.getMetricId());
			builder.append(" request.requestType=").append(
					req.getPluginRequestType());
			if (logger.isErrorEnabled()) {
				logger.error(builder.toString());
			}
			throw new RuntimeException(builder.toString());
		}
		requestMap.put(requestId, req);
		if (req.getPluginRequestType() != PluginRequestEnum.discovery) {
			instanceMetricMap.put(req.getRequestInstanceMetricKey(),
					Boolean.TRUE);
		}
	}

	public synchronized PluginRequest getPluginRequest(long requestId) {
		PluginRequest req = requestMap.get(requestId);
		if (req == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("getPluginRequest request is not exist.requestId="
						+ requestId);
			}
		}
		return req;
	}

	public synchronized PluginRequest takePluginRequest(long requestId) {
		Long key = requestId;
		PluginRequest req = requestMap.get(key);
		if (req == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("takePluginRequest request is not exist.requestId="
						+ requestId);
			}
		} else if (req.getPluginRequestType() != PluginRequestEnum.trap) {
			requestMap.remove(key);
			if (req.getPluginRequestType() != PluginRequestEnum.discovery) {
				instanceMetricMap.remove(req.getRequestInstanceMetricKey());
			}
		}
		return req;
	}

	public synchronized void removeTrapPluginRequest(long requestId) {
		Long key = requestId;
		PluginRequest req = requestMap.get(key);
		if (req == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("takePluginRequest request is not exist.requestId="
						+ requestId);
			}
		} else if (req.getPluginRequestType() == PluginRequestEnum.trap) {
			requestMap.remove(key);
			instanceMetricMap.remove(req.getRequestInstanceMetricKey());
		}
	}

	@Override
	public boolean isPluginRequestInProcess(PluginRequest req) {
		return instanceMetricMap.containsKey(req.getRequestInstanceMetricKey());
	}
}
