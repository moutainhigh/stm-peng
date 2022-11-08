/**
 * 
 */
package com.mainsteam.stm.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.generate.PluginRequestGenerator;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginserver.adapter.PluginRequestClient;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;
import com.mainsteam.stm.pluginserver.obj.ReponseIndexData;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;

/**
 * @author ziw
 * 
 */
public class MetricExecutorImpl implements MetricExecutor, BeanPostProcessor {

	private static final Log logger = LogFactory
			.getLog(MetricExecutorImpl.class);

	private CapacityService capacityService;
	private PluginRequestClient requestClient;
	private PluginRequestGenerator pluginRequestGenerator;
	private MetricExecuteFilter[] executeFilters;

	/**
	 * 
	 */
	public MetricExecutorImpl() {
	}

	public void start() throws Exception {
		// do something
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setRequestClient(PluginRequestClient requestClient) {
		this.requestClient = requestClient;
	}
	
	public void setPluginRequestGenerator(
			PluginRequestGenerator pluginRequestGenerator) {
		this.pluginRequestGenerator = pluginRequestGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.executor.MetricExecutor#catchDiscoveryMetricDatas(
	 * java.util.List, java.lang.String)
	 */
	@Override
	public List<DiscoveryMetricData> catchDiscoveryMetricDatas(
			List<MetricDiscoveryParameter> bindParameters, String discoveryWay)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("catchDiscoveryMetricDatas start");
		}
		List metricDatas = null;
		// malachi in 采集已开始 绑定参数
		List<PluginRequest>[] requests = pluginRequestGenerator
				.buildPluginRequestsByDiscovery(bindParameters, discoveryWay);
		List<ReponseData> calculateDatas = null;
		try {
			// malachi in 采集已开始 dcs调用插件
			calculateDatas = requestClient.executePluginRequest(requests[0]);
		} catch (PluginServerExecuteException e) {
			if (logger.isErrorEnabled()) {
				logger.error("catchDiscoveryMetricDatas", e);
			}
			throw new MetricExecutorException(e);
		} finally {
			requestClient.sendPluginRequest(requests[1]);
		}
		if (calculateDatas != null && calculateDatas.size() > 0) {
			metricDatas = new ArrayList<>(calculateDatas.size());
			for (ReponseData rd : calculateDatas) {
				if (rd.getCause() != null) {
					String resourceId = rd.getResourceId();
					ResourceDef resourceDef = capacityService
							.getResourceDefById(resourceId);
					if (resourceDef != null
							&& resourceDef.getParentResourceDef() == null) {
						if (logger.isErrorEnabled()) {
							logger.error("parent catchDiscoveryMetricDatas",
									rd.getCause());
						}
					} else {
						if (logger.isErrorEnabled()) {
							logger.error("child catchDiscoveryMetricDatas",
									rd.getCause());
						}
						continue;
					}
				}
				List<ReponseIndexData> reponseIndexDatas = rd
						.getReponseIndexDatas();
				if (reponseIndexDatas != null && reponseIndexDatas.size() > 0) {
					for (ReponseIndexData reponseIndexData : reponseIndexDatas) {
						DiscoveryMetricData data = new DiscoveryMetricData();
						data.setData(reponseIndexData.getData());
						data.setMetricId(rd.getMetricId());
						data.setResourceId(rd.getResourceId());
						data.setCollectTime(rd.getCollectTime());
						data.setIndexPropertyName(reponseIndexData
								.getIndexPropertyName());
						data.setIndexPropertyValue(reponseIndexData
								.getIndexPropertyValue());
						metricDatas.add(data);
					}
				}
			}
		} else {
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERROR_DISCOVERY_EMPTY_METRIC_VALUE_FOR_INSTANCE,
					"can't find any result,plase check!");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("catchDiscoveryMetricDatas end");
		}
		return metricDatas;
	}

	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID) throws MetricExecutorException {
		return catchRealtimeMetricData(instanceID,metricID,null);
	}
	

	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID,Map<String, String> params) throws MetricExecutorException {
		if (logger.isDebugEnabled()) {
			logger.debug("catchRealtimeMetricData start");
		}
		PluginRequest request = pluginRequestGenerator.generatePluginRequest(instanceID, metricID);
		request.setCollectTime(new Date());
		request.setPluginRequestType(PluginRequestEnum.immediate);
		
		if(params!=null){
			PluginArrayExecutorParameter  pluginExecutorParameter=(PluginArrayExecutorParameter ) request.getPluginExecutorParameter();
			
			Parameter[] pms=pluginExecutorParameter.getParameters();
			int index=pms.length;
			Parameter[] newPms=new Parameter[pms.length+params.size()];
			System.arraycopy(pms, 0, newPms, 0,index );
			
			for(Entry<String, String> en:params.entrySet()){
				ParameterValue pm=new ParameterValue();
				pm.setKey(en.getKey());
				pm.setValue(en.getValue());
				newPms[index++]=pm;
			}
			
			pluginExecutorParameter.setParameters(newPms);
			request.setPluginExecutorParameter(pluginExecutorParameter);
		}

		List<PluginRequest> requests = new ArrayList<>(1);
		requests.add(request);
		List<ReponseData> calculateDatas = null;
		try {
			calculateDatas = requestClient.executePluginRequest(requests);
		} catch (PluginServerExecuteException e) {
			if (logger.isErrorEnabled()) {
				logger.error("catchRealtimeMetricData", e);
			}
			throw new MetricExecutorException(e);
		}
		if (calculateDatas != null && calculateDatas.size() > 0) {
			ReponseData rd = calculateDatas.get(0);
		
			if (rd.getCause() != null) {
				if (logger.isErrorEnabled()) {
					logger.error("catchRealtimeMetricData", rd.getCause());
				}
			}
			MetricData data = new MetricData();
			List<ReponseIndexData> reponseIndexDatas = rd.getReponseIndexDatas();
			if (reponseIndexDatas != null && reponseIndexDatas.size() > 0) {
				data.setData(reponseIndexDatas.get(0).getData());
			}
			data.setMetricId(rd.getMetricId());
			data.setResourceId(rd.getResourceId());
			data.setCollectTime(rd.getCollectTime());
			data.setResourceInstanceId(rd.getResourceInstanceId());
			
			if (logger.isDebugEnabled())
				logger.debug("catchRealtimeMetricData end,the result:\n"+JSON.toJSONString(data));
			
			return data;
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("catchRealtimeMetricData can't find value,please check!");
			}
			return null;
		}
	}
	
	@Override
	public void execute(List<MetricExecuteParameter> parameters) {
		if (logger.isDebugEnabled()) {
			logger.debug("execute start parameters.size=" + parameters.size());
		}
		try {
			List<PluginRequest> pluginRequests = pluginRequestGenerator
					.generatePluginRequest(parameters);
			for (PluginRequest pluginRequest : pluginRequests) {
				pluginRequest.setPluginRequestType(PluginRequestEnum.monitor);
			}
			requestClient.sendPluginRequest(pluginRequests);
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("requestClient.sendPluginRequest", e);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("execute end");
		}
	}

	@Override
	public List<MetricData> catchRealtimeMetricData(
			List<InstanceMetricExecuteParameter> parameters)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("catchRealtimeMetricData for InstanceMetricExecuteParameter start");
		}
		if (parameters == null || parameters.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("catchRealtimeMetricData empty parameters.");
			}
			return null;
		}
		List<PluginRequest> requests = pluginRequestGenerator
				.generateInstanceMetricPluginRequest(parameters);
		if (requests != null && requests.size() > 0) {
			for (PluginRequest request : requests) {
				request.setPluginRequestType(PluginRequestEnum.immediate);
			}
		} else {
			return null;
		}
		List<ReponseData> calculateDatas = null;
		try {
			calculateDatas = requestClient.executePluginRequest(requests);
		} catch (PluginServerExecuteException e) {
			if (logger.isErrorEnabled()) {
				logger.error("catchRealtimeMetricData", e);
			}
			throw new MetricExecutorException(e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("catchRealtimeMetricData for InstanceMetricExecuteParameter end");
		}
		List<MetricData> datas = null;
		if (calculateDatas != null && calculateDatas.size() > 0) {
			datas = new ArrayList<>(calculateDatas.size());
			for (ReponseData rd : calculateDatas) {
				// if (rd.getCause() != null) {
				// throw new MetricExecutorException(rd.getCause());
				// }
				if (rd.getCause() != null) {
					if (logger.isErrorEnabled()) {
						logger.error("catchRealtimeMetricData", rd.getCause());
					}
				}
				MetricData data = new MetricData();
				List<ReponseIndexData> reponseIndexDatas = rd
						.getReponseIndexDatas();
				if (reponseIndexDatas != null && reponseIndexDatas.size() > 0) {
					data.setData(reponseIndexDatas.get(0).getData());
				}
				data.setMetricId(rd.getMetricId());
				data.setResourceId(rd.getResourceId());
				data.setCollectTime(rd.getCollectTime());
				data.setResourceInstanceId(rd.getResourceInstanceId());
				datas.add(data);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("catchRealtimeMetricData  for InstanceMetricExecuteParameter can't find value,please check!");
			}
		}
		return datas;
	}

	@Override
	public synchronized Object postProcessAfterInitialization(Object bean,
			String name) throws BeansException {
		if (bean instanceof MetricExecuteFilter) {
			if (executeFilters == null) {
				executeFilters = new MetricExecuteFilter[1];
				executeFilters[0] = (MetricExecuteFilter) bean;
				if (logger.isInfoEnabled()) {
					logger.info("find one executeFilter " + bean.getClass());
				}
			} else {
				int index = search((MetricExecuteFilter) bean);
				if (index < 0) {
					MetricExecuteFilter[] newExecuteFilters = new MetricExecuteFilter[executeFilters.length + 1];
					System.arraycopy(executeFilters, 0, newExecuteFilters, 0,
							executeFilters.length);
					newExecuteFilters[executeFilters.length] = (MetricExecuteFilter) bean;
					executeFilters = newExecuteFilters;
					if (logger.isInfoEnabled()) {
						logger.info("find one executeFilter " + bean.getClass());
					}
				}
			}
			this.pluginRequestGenerator.setExecuteFilters(executeFilters);
		}
		return bean;
	}

	private int search(MetricExecuteFilter f) {
		for (int i = 0; i < executeFilters.length; i++) {
			if (executeFilters[i] == f) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

	@Override
	public String getNetworkSystemOid(MetricDiscoveryParameter parameter)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("getNetworkSystemOid start");
		}
		String result = null;
		List<PluginRequest> snmpOidRequests = new ArrayList<>();
		List<PluginRequest> snmpOidRequestsEnd = new ArrayList<>();
		PluginRequest[] snmpOidRequest = pluginRequestGenerator
				.generateSystemOidRequest(parameter);
		if (snmpOidRequest != null && snmpOidRequest.length > 0) {
			snmpOidRequests.add(snmpOidRequest[0]);
			snmpOidRequestsEnd.add(snmpOidRequest[1]);
		}
		if (snmpOidRequests.size() > 0) {
			List<ReponseData> calculateDatas = null;
			try {
				calculateDatas = requestClient
						.executePluginRequest(snmpOidRequests);
			} catch (BaseException e) {
				if (logger.isErrorEnabled()) {
					logger.error("getNetworkSystemOid", e);
				}
				throw new MetricExecutorException(e);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getNetworkSystemOid", e);
				}
			} finally {
				requestClient.sendPluginRequest(snmpOidRequestsEnd);
			}
			if (calculateDatas != null && calculateDatas.size() > 0) {
				ReponseData rd = calculateDatas.get(0);
				if (rd.getCause() != null) {
					StringBuilder b = new StringBuilder("rd.getCause() != null,getNetworkSystemOid");
					b.append(rd.getResourceId());
					b.append(" 's systemoid is empty.");
					b.append(" discoveryParameter=").append(
							parameter.getDiscoveryInfo());
					String msg = b.toString();
					if (logger.isErrorEnabled()) {
						logger.error(msg, rd.getCause());
					}
					throw new MetricExecutorException(msg, rd.getCause());
				}
				List<ReponseIndexData> reponseIndexDatas = rd
						.getReponseIndexDatas();
				if (reponseIndexDatas != null && reponseIndexDatas.size() > 0) {
					String[] data = reponseIndexDatas.get(0).getData();
					if (data != null && data.length > 0) {
						parameter.getDiscoveryInfo().put(
								MetricDiscoveryParameter.SYSTEM_OID_NAME,
								data[0]);
						result = data[0];
					} else {
						StringBuilder b = new StringBuilder(
								"reponseIndexDatas.get(0).getData(),getNetworkSystemOid");
						b.append(rd.getResourceId());
						b.append(" 's systemoid is empty.");
						b.append(" discoveryParameter=").append(
								parameter.getDiscoveryInfo());
						String msg = b.toString();
						if (logger.isErrorEnabled()) {
							logger.error(msg, rd.getCause());
						}
					}
				} else {
					StringBuilder b = new StringBuilder("reponseIndexDatas == null,getNetworkSystemOid");
					b.append(rd.getResourceId());
					b.append(" 's systemoid is empty.");
					b.append(" discoveryParameter=").append(
							parameter.getDiscoveryInfo());
					String msg = b.toString();
					if (logger.isErrorEnabled()) {
						logger.error(msg, rd.getCause());
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getNetworkSystemOid end.sysoid=" + result);
		}
		return result;
	}

	@Override
	public MetricData catchMetricDataWithCustomPlugin(
			CustomMetricPluginParameter customMetricPluginParameter)
			throws MetricExecutorException {
		if (logger.isTraceEnabled()) {
			logger.trace("getMetricDataWithCustomPlugin start");
		}
		PluginRequest request = pluginRequestGenerator
				.generatePluginRequest(customMetricPluginParameter);
		if (request == null) {
			return null;
		}
		request.setCollectTime(new Date());
		request.setPluginRequestType(PluginRequestEnum.immediate);

		List<PluginRequest> requests = new ArrayList<>(1);
		requests.add(request);
		List<ReponseData> calculateDatas = null;
		try {
			calculateDatas = requestClient.executePluginRequest(requests);
		} catch (PluginServerExecuteException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getMetricDataWithCustomPlugin", e);
			}
			throw new MetricExecutorException(e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getMetricDataWithCustomPlugin end");
		}
		if (calculateDatas != null && calculateDatas.size() > 0) {
			ReponseData rd = calculateDatas.get(0);
			if (rd.getCause() != null) {
				// throw new MetricExecutorException(rd.getCause());
				if (logger.isErrorEnabled()) {
					logger.error("getMetricDataWithCustomPlugin", rd.getCause());
				}
			}
			MetricData data = new MetricData();
			List<ReponseIndexData> reponseIndexDatas = rd
					.getReponseIndexDatas();
			if (reponseIndexDatas != null && reponseIndexDatas.size() > 0) {
				data.setData(reponseIndexDatas.get(0).getData());
			}
			data.setMetricId(rd.getMetricId());
			data.setResourceId(rd.getResourceId());
			data.setCollectTime(rd.getCollectTime());
			data.setResourceInstanceId(rd.getResourceInstanceId());
			return data;
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("getMetricDataWithCustomPlugin can't find value,please check!");
			}
			return null;
		}
	}
}
