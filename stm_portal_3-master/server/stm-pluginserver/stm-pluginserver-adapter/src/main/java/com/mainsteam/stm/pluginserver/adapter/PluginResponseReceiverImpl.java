/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.adapter.exception.PluginServerAdapterException;
import com.mainsteam.stm.pluginserver.adapter.interceptor.MetricDataManager;
import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestManager;
import com.mainsteam.stm.pluginserver.adapter.manager.RequestReponseSynch;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.manager.ProcessBeanManager;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginserver.obj.ReponseData;
import com.mainsteam.stm.pluginserver.obj.ReponseIndexData;
import com.mainsteam.stm.pluginserver.util.InstanceCollectorLogController;
import com.mainsteam.stm.pluginserver.util.InstanceMonitorCheck;
import com.mainsteam.stm.transfer.MetricDataTransferSender;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * @author ziw
 * 
 */
public class PluginResponseReceiverImpl implements PluginResponseReceiver,BeanPostProcessor {

	private static final Log logger = LogFactory.getLog(PluginResponseReceiverImpl.class);

	private ProcessBeanManager processBeanManager;

	private PluginRequestManager requestManager;

	private RequestReponseSynch reponseSynch;
	
	private MetricDataTransferSender metricDataTransferSender;

	private InstanceCollectorLogController collectorLogController = InstanceCollectorLogController.getInstance();

	private InstanceMonitorCheck instanceMonitorCheck;
	
	private MetricDataManager metricDataManager;
	
	/**
	 * 
	 */
	public PluginResponseReceiverImpl() {
	}

	public void setMetricDataManager(MetricDataManager metricDataManager) {
		this.metricDataManager = metricDataManager;
	}

	public void setMetricDataTransferSender(
			MetricDataTransferSender metricDataTransferSender) {
		this.metricDataTransferSender = metricDataTransferSender;
	}
	
	public void setProcessBeanManager(ProcessBeanManager processBeanManager) {
		this.processBeanManager = processBeanManager;
	}

	public void setReponseSynch(RequestReponseSynch reponseSynch) {
		this.reponseSynch = reponseSynch;
	}

	public void setRequestManager(PluginRequestManager requestManager) {
		this.requestManager = requestManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.adapter.PluginResponseReceiver#
	 * receivePluginResponse
	 * (com.mainsteam.stm.pluginserver.message.PluginResult)
	 */
	@Override
	public void receivePluginResponse(List<PluginResult> responses) {
		if (logger.isTraceEnabled()) {
			logger.trace("receivePluginResponse start responses.size="
					+ responses.size());
		}
		for (PluginResult response : responses) {
			PluginRequest request = requestManager.takePluginRequest(response
					.getRequestId());
			// 针对不同的请求类型，进行不同的处理。
			// 获取request的对象
			PluginConvertParameter parameter = request
					.getPluginConvertParameter();
			ConverterResult[] metricData = null;
			BaseException cause = response.getCause();
			try {
				PluginResultSetConverter converter = processBeanManager
						.getPluginResultSetConverter(parameter
								.getConverterClass());
				metricData = converter.convert(response.getResultData(),
						parameter.getParameter());
				if (logger.isDebugEnabled()) {
					logger.debug("receivePluginResponse converter.convert.result="
							+ buildPrintPluginRequestResult(request,
									metricData, parameter));
				} else if (logger.isInfoEnabled()
						&& collectorLogController.isLog(request
								.getResourceInstId())) {
					logger.info("receivePluginResponse converter.convert.result="
							+ buildPrintPluginRequestResult(request,
									metricData, parameter));
				}
			} catch (Throwable e) {
				String msg = buildPrintPluginRequestResult(request,
						response.getResultData(), parameter);
				if (logger.isErrorEnabled()) {
					logger.error("receivePluginResponse " + msg, e);
				}
				if (e instanceof BaseException) {
					cause = new PluginServerAdapterException(msg, e);
				} else {
					cause = new PluginServerAdapterException(
							ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_CONVERTER_EXECUTE,
							msg, e);
				}
			}
			if (request.getPluginRequestType() == PluginRequestEnum.monitor
					|| request.getPluginRequestType() == PluginRequestEnum.trap) {
				String[] metricValue = null;
				if (metricData != null && metricData.length > 0) {
					metricValue = metricData[0].getResultData();
				}
				MetricCalculateData data = new MetricCalculateData(metricValue,
						request.getMetricId(), request.getResourceInstId(),
						request.getCollectTime(), request.getResourceId());
				data.setProfileId(request.getProfileId());
				data.setTimelineId(request.getTimelineId());
				TransferData transferData = new TransferData();
				transferData.setData(data);
				transferData.setDataType(TransferDataType.MetricData);
				data.setCustomMetric(request.isCustomMetric());
				cause = null;
				try {
					metricDataTransferSender.sendData(transferData);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error("receivePluginResponse sendData error.", e);
					}
				}
				//指标采集值通知
				try {
					MetricCalculateData copyData = new MetricData(metricValue,
							request.getMetricId(), request.getResourceInstId(),
							request.getCollectTime(), request.getResourceId());
					metricDataManager.doInterceptor(copyData);
				} catch (Throwable e1) {
					if (logger.isErrorEnabled()) {
						logger.error("receivePluginResponse error.", e1);
					}
				}
				if(instanceMonitorCheck!=null){
					instanceMonitorCheck.check(data);
				}
				
			} else {
				List<ReponseIndexData> reponseIndexDatas;
				if (metricData != null && metricData.length > 0) {
					reponseIndexDatas = new ArrayList<>(metricData.length);
					for (ConverterResult result : metricData) {
						reponseIndexDatas.add(new ReponseIndexData(result
								.getResultData(), request
								.getConvertParameterIndexProperty(), result
								.getResultIndex()));
					}
				} else {
					reponseIndexDatas = new ArrayList<>(0);
				}
				ReponseData data = new ReponseData(request.getMetricId(),
						request.getResourceInstId(), request.getResourceId(),
						request.getCollectTime(), cause, reponseIndexDatas,
						request.getRequestId());
				try {
					reponseSynch
							.recieveMetricData(request.getRequestId(), data);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"receivePluginResponse recieveMetricData error.",
								e);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("receivePluginResponse end");
		}
	}

	private String buildPrintPluginRequestResult(PluginRequest request,
			ConverterResult[] metricData, PluginConvertParameter parameter) {
		StringBuilder builder = new StringBuilder();
		builder.append('\n').append(" request.getRequestId()=")
				.append(request.getRequestId()).append(" request.getBatch()=")
				.append(request.getBatch()).append('\n')
				.append(" request.getPluginRequestType()=")
				.append(request.getPluginRequestType()).append('\n')
				.append(" request.getResourceId()=")
				.append(request.getResourceId())
				.append(" request.getResourceInstId()=")
				.append(request.getResourceInstId())
				.append(" request.getMetricId()=")
				.append(request.getMetricId()).append('\n')
				.append(" resultset=");
		if (metricData == null) {
			String s = null;
			builder.append(s);
		} else {
			builder.append('[');
			for (ConverterResult converterResult : metricData) {
				builder.append('{').append(converterResult.getResultIndex())
						.append(',');
				builder.append(Arrays.toString(converterResult.getResultData()));
				builder.append('}');
			}
			builder.append(']');
		}
		// .append(metricData == null ? null : Arrays.toString(metricData));

		builder.append('\n').append(" converter=")
				.append(parameter.getConverterClass());
		ProcessParameter processParameter = parameter.getParameter();
		builder.append('\n').append(" converterParameter=").append('[');
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

	private String buildPrintPluginRequestResult(PluginRequest request,
			ResultSet set, PluginConvertParameter parameter) {
		StringBuilder builder = new StringBuilder();
		builder.append(" request.getRequestId()=")
				.append(request.getRequestId()).append(" request.getBatch()=")
				.append(request.getBatch()).append('\n')
				.append(" request.getPluginRequestType()=")
				.append(request.getPluginRequestType()).append('\n')
				.append(" request.getResourceId()=")
				.append(request.getResourceId())
				.append(" request.getResourceInstId()=")
				.append(request.getResourceInstId())
				.append(" request.getMetricId()=")
				.append(request.getMetricId()).append('\n')
				.append(" resultset=")
				.append(set == null ? null : set.toString());
		if (parameter != null) {
			builder.append('\n').append(" converter=")
					.append(parameter.getConverterClass());
			ProcessParameter processParameter = parameter.getParameter();
			builder.append('\n').append(" converterParameter=").append('[');
			if (processParameter != null) {
				ParameterValue[] ps = processParameter.listParameterValues();
				if (ps != null && ps.length > 0) {
					builder.append('{').append(ps[0].getKey()).append(',')
							.append(ps[0].getValue()).append('}');
					for (int i = 1; i < ps.length; i++) {
						builder.append(',').append('{').append(ps[i].getKey())
								.append(',').append(ps[i].getValue())
								.append('}');
					}
				}
			}
		}
		builder.append(']');
		return builder.toString();
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		if (bean instanceof InstanceMonitorCheck) {
			instanceMonitorCheck=(InstanceMonitorCheck) bean;
		}
		return bean;
	}
}
