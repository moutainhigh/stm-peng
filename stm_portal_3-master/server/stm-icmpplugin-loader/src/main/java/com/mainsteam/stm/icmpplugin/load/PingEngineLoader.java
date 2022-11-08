/**
 * 
 */
package com.mainsteam.stm.icmpplugin.load;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricAvailQueryServiceMBean;
import com.mainsteam.stm.common.metric.obj.AvailMetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.plugin.icmp.core.PingEngine;
/**
 * @author ziw
 * 
 */
public class PingEngineLoader {

	private static final Log logger = LogFactory
			.getLog(PingEngineLoader.class);

	/**
	 * 批量处理的最大个数
	 */
	private static final int BATCH_SIZE = 50000;

	private OCRPCClient client;

	private LocaleNodeService localeNodeService;

	private ResourceInstanceService resourceInstanceService;

	private CapacityService capacityService;

	/**
	 * @param capacityService
	 *            the capacityService to set
	 */
	public final void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	/**
	 * @param localeNodeService
	 *            the localeNodeService to set
	 */
	public final void setLocaleNodeService(LocaleNodeService localeNodeService) {
		this.localeNodeService = localeNodeService;
	}

	/**
	 * @param resourceInstanceService
	 *            the resourceInstanceService to set
	 */
	public final void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * 
	 */
	public PingEngineLoader() {
	}

	public void start() throws IOException, NodeException {
		if (logger.isInfoEnabled()) {
			logger.info("PingEngineLoader start.");
		}
		try {
			loadPingIps();
		} catch (IOException | NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("error to PingEngineLoader start.", e);
			}
			throw e;
		} catch(Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("error to PingEngineLoader start.", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("PingEngineLoader end.");
		}
	}

	public void loadPingIps() throws IOException, NodeException {
		MetricAvailQueryServiceMBean availQueryServiceMBean = client
				.getParentRemoteSerivce(NodeFunc.processer,
						MetricAvailQueryServiceMBean.class);
		Node currentNode = localeNodeService.getCurrentNode();
		if (currentNode != null) {
			int start = 0;
			List<AvailMetricData> metricDatas = null;
			//do {
				metricDatas = availQueryServiceMBean
						.getParentInsanceAvailMetricDatas(
								currentNode.getGroupId(), start, BATCH_SIZE);
				if(logger.isInfoEnabled()){
					StringBuilder b = new StringBuilder(100);
					b.append("query avail");
					b.append(" start=").append(start);
					b.append("nodeGroupId=").append(currentNode.getGroupId());
					logger.info(b);
				}
				if (metricDatas != null && metricDatas.size() > 0) {
					applyMetricDatas(metricDatas);
					start += metricDatas.size();
				}else{
					if(logger.isInfoEnabled()){
						logger.info("metricDatas is empty ... method end");
					}
				}
			//} while (metricDatas != null && metricDatas.size() <= BATCH_SIZE);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("loadPingIps currentNode is null.");
			}
		}
	}

	private void applyMetricDatas(List<AvailMetricData> metricDatas) {
		if (logger.isInfoEnabled()) {
			logger.info("loadPingIps metricDatas.size=" + metricDatas.size());
		}
		Map<String, Boolean> ipsMap = new HashMap<String, Boolean>(
				metricDatas.size());
		for (AvailMetricData availMetricData : metricDatas) {
			ResourceInstance instance = null;
			try {
				instance = resourceInstanceService
						.getResourceInstance(availMetricData.getInstanceId());
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadPingIps", e);
				}
			}
			/**
			 * 判断是否是设备
			 */
			if (instance == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("loadPingIps instance is not exist.instanceId="
							+ availMetricData.getInstanceId());
				}
				continue;
			}
			if (StringUtils.isEmpty(instance.getShowIP())) {
				if (logger.isWarnEnabled()) {
					logger.warn("loadPingIps DiscoverIP is not exist.instanceId="
							+ availMetricData.getInstanceId());
				}
				continue;
			}
			ResourceMetricDef resourceMetricDef = capacityService
					.getResourceMetricDef(instance.getResourceId(),
							availMetricData.getMetricId());
			if (resourceMetricDef == null) {
				if (logger.isWarnEnabled()) {
					StringBuilder b = new StringBuilder(
							"loadPingIps resourceMetricDef is not exist.instanceId=");
					b.append(availMetricData.getInstanceId());
					b.append(" metricId=")
							.append(availMetricData.getMetricId());
					logger.warn(b.toString());
				}
				continue;
			}
			String discoveryWay = null;
			if (instance.getDiscoverWay() != null) {
				discoveryWay = instance.getDiscoverWay().name();
			}
			MetricCollect collect = resourceMetricDef.getMetricPluginByType(
					discoveryWay, null);
			if (collect == null) {
				if (logger.isWarnEnabled()) {
					StringBuilder b = new StringBuilder(
							"loadPingIps MetricCollect is not exist.instanceId=");
					b.append(availMetricData.getInstanceId());
					b.append(" metricId=")
							.append(availMetricData.getMetricId());
					b.append(" discoveryWay=").append(discoveryWay);
					logger.warn(b.toString());
				}
				continue;
			}
			if (collect.getPlugin() == null) {
				if (logger.isWarnEnabled()) {
					StringBuilder b = new StringBuilder(
							"loadPingIps collect.getPlugin is not exist.instanceId=");
					b.append(availMetricData.getInstanceId());
					b.append(" metricId=")
							.append(availMetricData.getMetricId());
					b.append(" discoveryWay=").append(discoveryWay);
					logger.warn(b.toString());
				}
				continue;
			}
			if (PluginIdEnum.IcmpPlugin.name().equals(
					collect.getPlugin().getId())) {
				Boolean state = "1".equals(availMetricData
						.getMetricValue());
				ipsMap.put(instance.getShowIP(), state);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(500);
					b.append("loadPingIps instanceId = ");
					b.append(availMetricData.getInstanceId());
					b.append(" ip=");
					b.append(instance.getShowIP()).append(" metricId=")
							.append(availMetricData.getMetricId())
							.append(" state=").append(state);
					logger.info(b.toString());
				}
			} else {
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(
							"loadPingIps metric collect is not icmp.ip=");
					b.append(instance.getShowIP()).append(" metricId=")
							.append(availMetricData.getMetricId())
							.append(" metricValue=")
							.append(availMetricData.getMetricValue()).append(" pluginId=")
							.append(collect.getPlugin().getId());
					 b.append(" instanceId=");
							b.append(availMetricData.getInstanceId());
					logger.info(b.toString());
				}
			}
		}
		if(PingEngine.init()){
			if (!ipsMap.isEmpty()) {
				// 4.2.3 版本之前 使用PingEngine.getInstance().initIpAlivesStates(ipsMap);
				PingEngine.initTargetState(ipsMap);
			}
		}
	}
}
