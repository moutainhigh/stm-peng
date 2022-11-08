/**
 * 
 */
package com.mainsteam.stm.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginserver.PluginContainer;
import com.mainsteam.stm.pluginserver.adapter.PluginRequestClient;
import com.mainsteam.stm.pluginserver.adapter.PluginResponseClient;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginserver.obj.ReponseData;
import com.mainsteam.stm.pluginserver.obj.ReponseIndexData;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * @author ziw
 * 
 */
public class RequestSendTest implements PluginRequestClient {

	private static final Log logger = LogFactory.getLog(RequestSendTest.class);

	private PluginContainer container;

	private SimplePluginResponseClient pluginResponseClient;

	/**
	 * 
	 */
	public RequestSendTest() {
		pluginResponseClient = new SimplePluginResponseClient();
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public final void setExecutor(MetricExecutor executor) {
		System.out.println("<<<<<<<<<<<<<<<,setExecutor");
		executor.setRequestClient(this);
	}

	/**
	 * @param container
	 *            the container to set
	 */
	public final void setContainer(PluginContainer container) {
		this.container = container;
		this.container.setPluginResponseClient(pluginResponseClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.adapter.PluginRequestClient#
	 * executePluginRequest(java.util.List)
	 */
	@Override
	public List<ReponseData> executePluginRequest(List<PluginRequest> reqs) {
		System.out.println("send request size=" + reqs.size());
		container.handlePluginRequest(reqs);
		List<PluginResult> datas = pluginResponseClient.takeDatas(reqs.size());
		List<ReponseData> resultList = null;
		try {
			resultList = convertDatas(reqs, datas);
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	private List<ReponseData> convertDatas(List<PluginRequest> reqs,
			List<PluginResult> datas) throws PluginSessionRunException {
		Map<Long, PluginRequest> pluginRequestMap = new HashMap<>();
		for (PluginRequest req : reqs) {
			pluginRequestMap.put(req.getRequestId(), req);
		}
		List<ReponseData> resultList = new ArrayList<>(datas.size());
		for (PluginResult pluginResult : datas) {
			ReponseData d = new ReponseData();
			PluginRequest request = pluginRequestMap.get(pluginResult
					.getRequestId());
			if (request == null) {
				continue;
			}
			PluginConvertParameter parameter = request
					.getPluginConvertParameter();
			PluginResultSetConverter converter = null;
			try {
				converter = toConverter(parameter.getConverterClass());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConverterResult[] metricData = converter.convert(
					pluginResult.getResultData(), parameter.getParameter());
			if (null == metricData) {
				logger.debug("metricData is NULL RESP:" + request.getMetricId());
			} else if (logger.isDebugEnabled()) {
				logger.info("PluginRequestType:"
						+ request.getPluginRequestType());
				logger.debug("receivePluginResponse converter.convert.result="
						+ Arrays.asList(metricData));
			}
			List<ReponseIndexData> reponseIndexDatas = new ArrayList<>();
			if (metricData != null && metricData.length > 0) {
				for (ConverterResult result : metricData) {
					reponseIndexDatas.add(new ReponseIndexData(result
							.getResultData(), request
							.getConvertParameterIndexProperty(), result
							.getResultIndex()));
				}
			}
			d.setReponseIndexDatas(reponseIndexDatas);
			d.setCollectTime(request.getCollectTime());
			d.setMetricId(request.getMetricId());
			d.setResourceId(request.getResourceId());
			d.setResourceInstanceId(request.getResourceInstId());
			resultList.add(d);
		}
		printReponseData(resultList);
		return resultList;
	}

	private static PluginResultSetConverter toConverter(
			Class<? extends PluginResultSetConverter> className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return className.newInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginserver.adapter.PluginRequestClient#sendPluginRequest
	 * (java.util.List)
	 */
	@Override
	public void sendPluginRequest(List<PluginRequest> reqs) {
		System.out.println("send request size=" + reqs.size());
		container.handlePluginRequest(reqs);
		List<ReponseData> resultList = null;
		try {
			resultList = convertDatas(reqs,
					this.pluginResponseClient.takeDatas(reqs.size()));
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
		}
		printReponseData(resultList);
	}

	private void printReponseData(List<ReponseData> resultList) {
		for (ReponseData reponseData : resultList) {
			StringBuilder b = new StringBuilder();
			b.append(" InstanceId=")
					.append(reponseData.getResourceInstanceId());
			b.append(" ResourceId=").append(reponseData.getResourceId());
			b.append(" MetricId=").append(reponseData.getMetricId());
			b.append(" Value=").append(reponseData.getReponseIndexDatas());
			System.out.println(b.toString());
		}
	}

	private class SimplePluginResponseClient implements PluginResponseClient {
		private List<PluginResult> datas = new ArrayList<>();

		@Override
		public void sendPluginReponse(List<PluginResult> resultList) {
			System.out.println("get resultList is " + resultList.size());
			datas.addAll(resultList);
		}

		public List<PluginResult> takeDatas(int size) {
			while (datas.size() < size)
				;
			List<PluginResult> returnList = datas;
			datas = new ArrayList<>();
			return returnList;
		}
	}
}
