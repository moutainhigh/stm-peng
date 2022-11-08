package com.mainsteam.stm.metric.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.metric.cache.CustomMetricCache;
import com.mainsteam.stm.metric.dao.CustomMetricBindDAO;
import com.mainsteam.stm.metric.dao.CustomMetricChangeDAO;
import com.mainsteam.stm.metric.dao.CustomMetricCollectDAO;
import com.mainsteam.stm.metric.dao.CustomMetricDAO;
import com.mainsteam.stm.metric.dao.CustomMetricDataWayDAO;
import com.mainsteam.stm.metric.dao.CustomMetricThresholdDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricCollectDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDataWayDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricThresholdDO;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.interceptor.CustomMetricChangeManager;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomChangeData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricDataProcess;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricQuery;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;

public class CustomMetricServiceImpl implements
		CustomMetricServiceInnerInterface {

	private static final Log logger = LogFactory
			.getLog(CustomMetricServiceImpl.class);

	private CustomMetricBindDAO customMetricBindDAO;

	private CustomMetricCollectDAO customMetricCollectDAO;

	private CustomMetricDAO customMetricDAO;

	private CustomMetricThresholdDAO customMetricThresholdDAO;

	private CustomMetricChangeDAO changeDAO;

	private CustomMetricDataWayDAO customMetricDataWayDAO;

	private ISequence changeSeq;

	private ISequence metricSeq;

	private ISequence metricCollectSeq;

	private CustomMetricCache customMetricCache;
	
	//自定义指标是否告警通知
	private CustomMetricChangeManager customMetricAlarmManager;
	
	//自定义指标是否监控通知
	private CustomMetricChangeManager customMetricMonitorManager;
	
	//自定义指标取消绑定资源通知
	private CustomMetricChangeManager customResourceCancelManager;
	
	/**
	 * @param changeDAO
	 *            the changeDAO to set
	 */
	public final void setChangeDAO(CustomMetricChangeDAO changeDAO) {
		this.changeDAO = changeDAO;
	}

	

	public void setCustomMetricAlarmManager(
			CustomMetricChangeManager customMetricAlarmManager) {
		this.customMetricAlarmManager = customMetricAlarmManager;
	}



	public void setCustomMetricMonitorManager(
			CustomMetricChangeManager customMetricMonitorManager) {
		this.customMetricMonitorManager = customMetricMonitorManager;
	}



	public void setCustomResourceCancelManager(
			CustomMetricChangeManager customResourceCancelManager) {
		this.customResourceCancelManager = customResourceCancelManager;
	}
	

	/**
	 * @return the customMetricCache
	 */
	public final CustomMetricCache getCustomMetricCache() {
		return customMetricCache;
	}

	/**
	 * @param changeSeq
	 *            the changeSeq to set
	 */
	public final void setChangeSeq(ISequence changeSeq) {
		this.changeSeq = changeSeq;
	}

	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("Initialize the load CustomMetricServiceImpl cache");
		}
		customMetricCache = new CustomMetricCache();
		try {
			List<CustomMetricBind> customMetricBinds = loadAllCustomMetricBinds();
			List<CustomMetric> customMetrics = loadAllCustomMetrics();
			customMetricCache.start(customMetricBinds, customMetrics);
			if(CollectionUtils.isNotEmpty(customMetrics) && logger.isDebugEnabled()){
				logger.debug("Initialize the load cache sizecustomMetricBinds and customMetrics size  = "
							+ customMetricBinds.size()+","+customMetrics.size());
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadAllCustomMetrics in db ", e);
			}
		}

	}

	private List<CustomMetricBind> loadAllCustomMetricBinds() {
		List<CustomMetricBindDO> customMetricBindDOs = customMetricBindDAO
				.getAllCustomMetricBinds();
		if (customMetricBindDOs != null && !customMetricBindDOs.isEmpty()) {
			List<CustomMetricBind> customMetricBinds = new ArrayList<>();
			for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
				CustomMetricBind customMetricBind = metricBindConverToBO(customMetricBindDO);
				customMetricBinds.add(customMetricBind);
			}
			return customMetricBinds;
		}
		return null;
	}

	private List<CustomMetric> loadAllCustomMetrics() throws Exception {
		List<CustomMetricDO> customMetricDOs = customMetricDAO
				.getAllCustomMetricDOs();
		List<CustomMetric> customMetrics = new ArrayList<CustomMetric>();
		if (customMetricDOs != null && !customMetricDOs.isEmpty()) {
			for (CustomMetricDO customMetricDO : customMetricDOs) {
				String metricId = customMetricDO.getCustomMetricId();
				CustomMetric customMetric = new CustomMetric();
				CustomMetricInfo info = metricConvertTOBO(customMetricDO);
				customMetric.setCustomMetricInfo(info);
				// 查找指标阈值
				List<CustomMetricThresholdDO> customMetricThresholdDO = customMetricThresholdDAO
						.getCustomMetricThresholdsByMetricId(metricId);
				if (customMetricThresholdDO != null
						&& !customMetricThresholdDO.isEmpty()) {
					List<CustomMetricThreshold> customMetricThresholds = new ArrayList<>(
							customMetricThresholdDO.size());
					for (CustomMetricThresholdDO customMetricThresholdDO2 : customMetricThresholdDO) {
						customMetricThresholds
								.add(metricThresholdConvertToBO(customMetricThresholdDO2));
					}
					customMetric
							.setCustomMetricThresholds(customMetricThresholds);
				}
				// 查找指标采集参数
				List<CustomMetricCollectDO> customMetricCollectDOs = customMetricCollectDAO
						.getMetricCollectDOById(Arrays.asList(metricId));
				if (customMetricCollectDOs != null
						&& !customMetricCollectDOs.isEmpty()) {
					List<CustomMetricCollectParameter> customMetricCollectParameters = new ArrayList<>(
							customMetricCollectDOs.size());
					for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
						customMetricCollectParameters
								.add(metricCollectConverToBO(customMetricCollectDO));
					}
					customMetric
							.setCustomMetricCollectParameters(customMetricCollectParameters);
				}
				// 查找指标数据处理方式
				List<CustomMetricDataWayDO> customMetricDataWayDOs = customMetricDataWayDAO
						.getCustomMetricDataWayByMetric(metricId);
				if (customMetricDataWayDOs != null
						&& !customMetricDataWayDOs.isEmpty()) {
					CustomMetricDataProcess customMetricDataProcess = null;
					for (CustomMetricDataWayDO customMetricDataWayDO : customMetricDataWayDOs) {
						customMetricDataProcess = metricDataWayConverToBO(customMetricDataWayDO);
						// TODO 现在只支持一个指标一个插件
						break;
					}
					customMetric
							.setCustomMetricDataProcess(customMetricDataProcess);
				}
				customMetrics.add(customMetric);
			}
		}
		return customMetrics;
	}

	private CustomMetricDO metricConvertToDO(CustomMetricInfo metric) {
		CustomMetricDO customMetricDO = new CustomMetricDO();
		customMetricDO.setCustomMetricId(metric.getId());
		customMetricDO.setCustomMetricName(metric.getName());
		if (metric.getStyle() != null) {
			customMetricDO.setCustomMetricStyle(metric.getStyle().name());
		}
		customMetricDO.setCustomMetricUnit(metric.getUnit());
		customMetricDO.setFlapping(metric.getFlapping());
		if (metric.getFreq() != null) {
			customMetricDO.setFreq(metric.getFreq().name());
		}
		customMetricDO.setIsAlert(metric.isAlert() ? "1" : "0");
		customMetricDO.setIsMonitor(metric.isMonitor() ? "1" : "0");
		customMetricDO.setUpdateTime(new Date());
		return customMetricDO;
	}

	private CustomMetricInfo metricConvertTOBO(CustomMetricDO customMetricDO) {
		CustomMetricInfo customMetric = new CustomMetricInfo();
		customMetric.setAlert("1".equals(customMetricDO.getIsAlert()));
		customMetric.setMonitor("1".equals(customMetricDO.getIsMonitor()));
		customMetric.setFlapping(customMetricDO.getFlapping());
		customMetric.setFreq(FrequentEnum.valueOf(customMetricDO.getFreq()));
		customMetric.setId(customMetricDO.getCustomMetricId());
		customMetric.setName(customMetricDO.getCustomMetricName());
		customMetric.setStyle(MetricTypeEnum.valueOf(customMetricDO
				.getCustomMetricStyle()));
		customMetric.setUnit(customMetricDO.getCustomMetricUnit());
		customMetric.setUpdateTime(customMetricDO.getUpdateTime());
		return customMetric;
	}

	private CustomMetricThresholdDO metricThresholdConvertToDO(
			CustomMetricThreshold customMetricThreshold) {
		CustomMetricThresholdDO customMetricThresholdDO = new CustomMetricThresholdDO();
		customMetricThresholdDO.setExpressionDesc(customMetricThreshold
				.getThresholdExpression());
		if(customMetricThreshold
				.getOperator() != null){
			customMetricThresholdDO.setExpressionOperator(customMetricThreshold
					.getOperator().toString());
		}
		customMetricThresholdDO
				.setMetricId(customMetricThreshold.getMetricId());
		customMetricThresholdDO.setMetricState(customMetricThreshold
				.getMetricState().toString());
		customMetricThresholdDO.setThresholdValue(customMetricThreshold
				.getThresholdValue());
		customMetricThresholdDO.setAlarmTemplate(customMetricThreshold.getAlarmTemplate());
		return customMetricThresholdDO;
	}

	private CustomMetricThreshold metricThresholdConvertToBO(
			CustomMetricThresholdDO customMetricThresholdDO) {
		CustomMetricThreshold customMetricThreshold = new CustomMetricThreshold();
		customMetricThreshold.setThresholdExpression(customMetricThresholdDO
				.getExpressionDesc());
		if(customMetricThresholdDO.getExpressionOperator() != null){
			customMetricThreshold.setOperator(OperatorEnum
					.fromString(customMetricThresholdDO.getExpressionOperator()));
		}
		customMetricThreshold
				.setMetricId(customMetricThresholdDO.getMetricId());
		customMetricThreshold.setMetricState(MetricStateEnum
				.valueOf(customMetricThresholdDO.getMetricState().toString()));
		customMetricThreshold.setThresholdValue(customMetricThresholdDO
				.getThresholdValue());
		customMetricThreshold.setAlarmTemplate(customMetricThresholdDO.getAlarmTemplate());
		return customMetricThreshold;
	}

	private CustomMetricCollectDO metricCollectConverToDO(
			CustomMetricCollectParameter customMetricCollectParameter) {
		CustomMetricCollectDO customMetricCollectDO = new CustomMetricCollectDO();
		customMetricCollectDO.setMetricId(customMetricCollectParameter
				.getMetricId());
		customMetricCollectDO.setParamKey(customMetricCollectParameter
				.getParameterKey());
		customMetricCollectDO.setParamType(customMetricCollectParameter
				.getParameterType());
		customMetricCollectDO.setParamValue(customMetricCollectParameter
				.getParameterValue());
		customMetricCollectDO.setPluginId(customMetricCollectParameter
				.getPluginId());
		return customMetricCollectDO;
	}

	private CustomMetricCollectParameter metricCollectConverToBO(
			CustomMetricCollectDO customMetricCollectDO) {
		CustomMetricCollectParameter customMetricCollectParameter = new CustomMetricCollectParameter();
		customMetricCollectParameter.setMetricId(customMetricCollectDO
				.getMetricId());
		customMetricCollectParameter.setParameterKey(customMetricCollectDO
				.getParamKey());
		customMetricCollectParameter.setParameterType(customMetricCollectDO
				.getParamType());
		customMetricCollectParameter.setParameterValue(customMetricCollectDO
				.getParamValue());
		customMetricCollectParameter.setPluginId(customMetricCollectDO
				.getPluginId());
		return customMetricCollectParameter;
	}

	private CustomMetricBindDO metricBindConverToDO(
			CustomMetricBind customMetricBind) {
		CustomMetricBindDO customMetricBindDO = new CustomMetricBindDO();
		customMetricBindDO.setInstanceId(customMetricBind.getInstanceId());
		customMetricBindDO.setMetricId(customMetricBind.getMetricId());
		customMetricBindDO.setPluginId(customMetricBind.getPluginId());
		return customMetricBindDO;
	}

	private CustomMetricBind metricBindConverToBO(
			CustomMetricBindDO CustomMetricBindDO) {
		CustomMetricBind customMetricBind = new CustomMetricBind();
		customMetricBind.setInstanceId(CustomMetricBindDO.getInstanceId());
		customMetricBind.setMetricId(CustomMetricBindDO.getMetricId());
		customMetricBind.setPluginId(CustomMetricBindDO.getPluginId());
		return customMetricBind;
	}

	private CustomMetricDataProcess metricDataWayConverToBO(
			CustomMetricDataWayDO customMetricDataWayDO) {
		CustomMetricDataProcess customMetricDataProcess = new CustomMetricDataProcess();
		customMetricDataProcess
				.setDataProcessWay(CustomMetricDataProcessWayEnum
						.valueOf(customMetricDataWayDO.getDataWay()));
		customMetricDataProcess
				.setMetricId(customMetricDataWayDO.getMetricId());
		customMetricDataProcess
				.setPluginId(customMetricDataWayDO.getPluginId());
		return customMetricDataProcess;
	}

	private CustomMetricDataWayDO metricDataWayConverToDO(
			CustomMetricDataProcess customMetricDataProcess) {
		CustomMetricDataWayDO customMetricDataWayDO = new CustomMetricDataWayDO();
		customMetricDataWayDO.setDataWay(customMetricDataProcess
				.getDataProcessWay().name());
		customMetricDataWayDO
				.setMetricId(customMetricDataProcess.getMetricId());
		customMetricDataWayDO
				.setPluginId(customMetricDataProcess.getPluginId());
		return customMetricDataWayDO;
	}

	@Override
	public String createCustomMetric(CustomMetric metric)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("createCustomMetric start name="
					+ metric.getCustomMetricInfo().getName());
		}

		// 封装指标基本信息
		CustomMetricDO customMetricDO = metricConvertToDO(metric
				.getCustomMetricInfo());
		String metricId = String.valueOf(metricSeq.next());
		customMetricDO.setCustomMetricId(metricId);

		// 封装指标参数
		List<CustomMetricCollectParameter> metricCollectParameters = metric
				.getCustomMetricCollectParameters();

		List<CustomMetricCollectDO> customMetricCollectDOs = new ArrayList<>(
				metricCollectParameters.size());
		for (int i = 0; i < metric.getCustomMetricCollectParameters().size(); i++) {
			CustomMetricCollectParameter customMetricCollectParameter = metricCollectParameters
					.get(i);
			CustomMetricCollectDO customMetricCollectDO = metricCollectConverToDO(customMetricCollectParameter);
			customMetricCollectDO.setMetricId(metricId);
			customMetricCollectDO.setPluginId(customMetricCollectParameter
					.getPluginId());
			customMetricCollectDO.setMetricCollectId(metricCollectSeq.next());
			customMetricCollectDOs.add(customMetricCollectDO);
		}

		// 封装指标阈值
		List<CustomMetricThreshold> customMetricThreshold = metric
				.getCustomMetricThresholds();
		List<CustomMetricThresholdDO> customMetricThresholdDOs = new ArrayList<>(
				customMetricThreshold.size());

		for (CustomMetricThreshold tempCustomMetricThreshold : customMetricThreshold) {
			CustomMetricThresholdDO customMetricThresholdDO = metricThresholdConvertToDO(tempCustomMetricThreshold);
			customMetricThresholdDO.setMetricId(metricId);
			customMetricThresholdDOs.add(customMetricThresholdDO);
		}
		// 封装数据采集方式
		CustomMetricDataWayDO customMetricDataWayDO = metricDataWayConverToDO(metric
				.getCustomMetricDataProcess());
		customMetricDataWayDO.setMetricId(metricId);
		try {
			// 添加指标值
			customMetricDAO.insertMetric(customMetricDO);
			// 添加阈值
			customMetricThresholdDAO
					.insertCustomMetricThresholds(customMetricThresholdDOs);
			// 添加采集参数
			customMetricCollectDAO.insertMetricCOllects(customMetricCollectDOs);
			// 添加采集数据方式
			customMetricDataWayDAO.insertMetricDataWay(customMetricDataWayDO);

			customMetricCache.cacheCustomMetric(metric);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("createCustomMetric error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"createCustomMetric error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("createCustomMetric end name="
					+ metric.getCustomMetricInfo().getName());
		}
		return metricId;
	}

	@Override
	public CustomMetric getCustomMetric(String metricId)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetrics start metricId public CustomMetric getCustomMetric ="
					+ metricId);
		}
		CustomMetric customMetric = customMetricCache.getCustomMetric(metricId);
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetric customMetric in cache = "
					+ customMetric.getCustomMetricInfo().getId());
		}
		if (customMetric == null) {
			try {
				// 查找指标基本信息
				CustomMetricDO customMetricDO = customMetricDAO
						.getMetricDOById(metricId);
				if (customMetricDO != null) {
					customMetric = new CustomMetric();
					CustomMetricInfo info = metricConvertTOBO(customMetricDO);
					customMetric.setCustomMetricInfo(info);
					// 查找指标阈值
					List<CustomMetricThresholdDO> customMetricThresholdDO = customMetricThresholdDAO
							.getCustomMetricThresholdsByMetricId(metricId);
					if (customMetricThresholdDO != null
							&& !customMetricThresholdDO.isEmpty()) {
						List<CustomMetricThreshold> customMetricThresholds = new ArrayList<>(
								customMetricThresholdDO.size());
						for (CustomMetricThresholdDO customMetricThresholdDO2 : customMetricThresholdDO) {
							customMetricThresholds
									.add(metricThresholdConvertToBO(customMetricThresholdDO2));
						}
						customMetric
								.setCustomMetricThresholds(customMetricThresholds);
					}
					// 查找指标采集参数
					List<CustomMetricCollectDO> customMetricCollectDOs = customMetricCollectDAO
							.getMetricCollectDOById(Arrays.asList(metricId));
					if (customMetricCollectDOs != null
							&& !customMetricCollectDOs.isEmpty()) {
						List<CustomMetricCollectParameter> customMetricCollectParameters = new ArrayList<>(
								customMetricCollectDOs.size());
						for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
							customMetricCollectParameters
									.add(metricCollectConverToBO(customMetricCollectDO));
						}
						customMetric
								.setCustomMetricCollectParameters(customMetricCollectParameters);
					}
					// 查找指标数据处理方式
					List<CustomMetricDataWayDO> customMetricDataWayDOs = customMetricDataWayDAO
							.getCustomMetricDataWayByMetric(metricId);
					if (customMetricDataWayDOs != null
							&& !customMetricDataWayDOs.isEmpty()) {
						CustomMetricDataProcess customMetricDataProcess = null;
						for (CustomMetricDataWayDO customMetricDataWayDO : customMetricDataWayDOs) {
							customMetricDataProcess = metricDataWayConverToBO(customMetricDataWayDO);
							// TODO 现在只支持一个指标一个插件
							break;
						}
						customMetric
								.setCustomMetricDataProcess(customMetricDataProcess);
					}
					customMetricCache.cacheCustomMetric(customMetric);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomMetric error!", e);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetrics end metricId=" + metricId);
		}
		return customMetric;
	}

	@Override
	public List<CustomMetric> getCustomMetrics(
			CustomMetricQuery customMetricQuery, int startRow, int pageSize) {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetrics start startRow=" + startRow);
		}
		CustomMetricDO query = null;
		if (customMetricQuery != null) {
			query = new CustomMetricDO();
			query.setCustomMetricName(customMetricQuery.getCustomMetricName());
			if (customMetricQuery.getCustomMetricStyle() != null) {
				query.setCustomMetricStyle(customMetricQuery
						.getCustomMetricStyle().name());
			}
			if (customMetricQuery.getIsAlert() != null) {
				query.setIsAlert(customMetricQuery.getIsAlert().booleanValue() ? "1"
						: "0");
			}
			if (customMetricQuery.getIsMonitor() != null) {
				query.setIsMonitor(customMetricQuery.getIsMonitor()
						.booleanValue() ? "1" : "0");
			}
		}
		Page<CustomMetricDO, CustomMetricDO> page = new Page<>(startRow,
				pageSize, query);
		List<CustomMetric> customMetrics = null;
		try {
			List<CustomMetricDO> customMetricDOs = customMetricDAO
					.getAllMetric(page);
			if (customMetricDOs != null && !customMetricDOs.isEmpty()) {
				customMetrics = new ArrayList<CustomMetric>(
						customMetricDOs.size());
				for (CustomMetricDO customMetricDO : customMetricDOs) {
					CustomMetric customMetric = customMetricCache
							.getCustomMetric(customMetricDO.getCustomMetricId());
					if (customMetric == null) {
						customMetric = new CustomMetric();
						CustomMetricInfo info = metricConvertTOBO(customMetricDO);
						customMetric.setCustomMetricInfo(info);
						String metricId = customMetricDO.getCustomMetricId();
						// 查找指标阈值
						List<CustomMetricThresholdDO> customMetricThresholdDO = customMetricThresholdDAO
								.getCustomMetricThresholdsByMetricId(metricId);
						if (customMetricThresholdDO != null
								&& !customMetricThresholdDO.isEmpty()) {
							List<CustomMetricThreshold> customMetricThresholds = new ArrayList<>(
									customMetricThresholdDO.size());
							for (CustomMetricThresholdDO customMetricThresholdDO2 : customMetricThresholdDO) {
								customMetricThresholds
										.add(metricThresholdConvertToBO(customMetricThresholdDO2));
							}
							customMetric
									.setCustomMetricThresholds(customMetricThresholds);
						}
						// 查找指标采集参数
						List<CustomMetricCollectDO> customMetricCollectDOs = customMetricCollectDAO
								.getMetricCollectDOById(Arrays.asList(metricId));
						if (customMetricCollectDOs != null
								&& !customMetricCollectDOs.isEmpty()) {
							List<CustomMetricCollectParameter> customMetricCollectParameters = new ArrayList<>(
									customMetricCollectDOs.size());
							for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
								customMetricCollectParameters
										.add(metricCollectConverToBO(customMetricCollectDO));
							}
							customMetric
									.setCustomMetricCollectParameters(customMetricCollectParameters);
						}
						// 查找指标数据处理方式
						List<CustomMetricDataWayDO> customMetricDataWayDOs = customMetricDataWayDAO
								.getCustomMetricDataWayByMetric(metricId);
						if (customMetricDataWayDOs != null
								&& !customMetricDataWayDOs.isEmpty()) {
							CustomMetricDataProcess customMetricDataProcess = null;
							for (CustomMetricDataWayDO customMetricDataWayDO : customMetricDataWayDOs) {
								customMetricDataProcess = metricDataWayConverToBO(customMetricDataWayDO);
								// TODO 现在只支持一个指标一个插件
								break;
							}
							customMetric
									.setCustomMetricDataProcess(customMetricDataProcess);
						}
						customMetricCache.cacheCustomMetric(customMetric);
					}
					customMetrics.add(customMetric);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getCustomMetrics error!", e);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetrics end startRow=" + startRow);
		}
		return customMetrics;
	}

	@Override
	public List<CustomMetric> getCustomMetricsByInstanceId(long instanceId)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricsByInstanceId start instanceId="
					+ instanceId);
		}
		HashSet<String> metrics = null;
		String[] metricId_pluginIds = customMetricCache
				.getMetricPlugins(instanceId);
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricsByInstanceId in cache = "
					+ Arrays.toString(metricId_pluginIds));
		}
		if (metricId_pluginIds == null) {
			List<CustomMetricBindDO> customMetricBindDOs = null;
			try {
				customMetricBindDOs = customMetricBindDAO
						.getCustomMetricBindByInstanceId(instanceId);
				if (customMetricBindDOs != null
						&& !customMetricBindDOs.isEmpty()) {
					metrics = new HashSet<>(customMetricBindDOs.size());
					metricId_pluginIds = new String[customMetricBindDOs.size() * 2];
					int index = 0;
					for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
						metrics.add(customMetricBindDO.getMetricId());
						metricId_pluginIds[index++] = customMetricBindDO
								.getMetricId();
						metricId_pluginIds[index++] = customMetricBindDO
								.getPluginId();
					}
					customMetricCache.setMetricPlugins(instanceId,
							metricId_pluginIds);
					if (logger.isDebugEnabled()) {
						logger.debug("getCustomMetricsByInstanceId cache = "
								+ Arrays.toString(metricId_pluginIds));
					}
				} else {
					customMetricCache.setMetricPlugins(instanceId,
							new String[0]);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomMetricBindByInstanceId error!", e);
				}
				throw new CustomMetricException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getCustomMetricBindByInstanceId error!");
			}
		} else if (metricId_pluginIds.length > 0) {
			metrics = new HashSet<>(metricId_pluginIds.length / 2);
			for (int i = 0; i < metricId_pluginIds.length; i += 2) {
				metrics.add(metricId_pluginIds[i]);
			}
		}
		List<CustomMetric> customMetrics = null;
		if (metrics != null && !metrics.isEmpty()) {
			customMetrics = new ArrayList<>(metrics.size());
			for (String metricId : metrics) {
				CustomMetric customMetric = getCustomMetric(metricId);
				if (customMetric != null) {
					customMetrics.add(customMetric);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricsByInstanceId end instanceId="
					+ instanceId);
		}
		return customMetrics;
	}

	@Override
	public void updateCustomMericSetting(CollectMeticSetting collectMeticSetting)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMericSetting start metricId="
					+ collectMeticSetting.getMetricId());
		}
		CustomMetric customMetric = customMetricCache
				.getCustomMetric(collectMeticSetting.getMetricId());
		CustomMetricDO customMetricDO = new CustomMetricDO();
		customMetricDO.setCustomMetricId(collectMeticSetting.getMetricId());
		boolean isUpdateMonitorInfo = false;
		if (collectMeticSetting.getAlert() != null) {
			customMetricDO.setIsAlert(collectMeticSetting.getAlert()
					.booleanValue() ? "1" : "0");
			if (customMetric != null) {
				customMetric.getCustomMetricInfo().setAlert(
						collectMeticSetting.getAlert().booleanValue());
				List<CustomChangeData> changeDatas = new ArrayList<>(1);
				CustomChangeData data = new CustomChangeData();
				data.setIsMonitor(collectMeticSetting.getAlert());
				data.setMetricId(collectMeticSetting.getMetricId());
				changeDatas.add(data);
				//-告警改变了，通知
				customMetricAlarmManager.doMetricChangeInterceptor(changeDatas);
			}
		}
		if (collectMeticSetting.getMonitor() != null) {
			customMetricDO.setIsMonitor(collectMeticSetting.getMonitor()
					.booleanValue() ? "1" : "0");
			isUpdateMonitorInfo = true;
			if (customMetric != null) {
				customMetric.getCustomMetricInfo().setMonitor(
						collectMeticSetting.getMonitor().booleanValue());
				List<CustomChangeData> changeDatas = new ArrayList<>(1);
				CustomChangeData data = new CustomChangeData();
				data.setIsMonitor(collectMeticSetting.getMonitor());
				data.setMetricId(collectMeticSetting.getMetricId());
				changeDatas.add(data);
				//-指标监控改变了，通知
				customMetricMonitorManager.doMetricChangeInterceptor(changeDatas);
			}
		}
		if (collectMeticSetting.getFlapping() != null) {
			customMetricDO.setFlapping(collectMeticSetting.getFlapping()
					.intValue());
			if (customMetric != null) {
				customMetric.getCustomMetricInfo().setFlapping(
						collectMeticSetting.getFlapping().intValue());
			}
		}
		if (collectMeticSetting.getFreq() != null) {
			customMetricDO.setFreq(collectMeticSetting.getFreq().name());
			isUpdateMonitorInfo = true;
			if (customMetric != null) {
				customMetric.getCustomMetricInfo().setFreq(
						collectMeticSetting.getFreq());
			}
		}
		try {
			customMetricDAO.updateMetric(customMetricDO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateMetric error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateMetric error!");
		}
		// 同步到采集器记录
		if (isUpdateMonitorInfo) {
			Date changeDate = new Date();
			CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
			changeDO.setChange_id(changeSeq.next());
			changeDO.setChange_time(changeDate);
			changeDO.setOccur_time(changeDate);
			changeDO.setOperate_state(0);
			changeDO.setOperateMode(CustomMetricChangeEnum.METRIC_BASIC_UPDATE
					.name());
			changeDO.setMetric_id(collectMeticSetting.getMetricId());
			List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(1);
			metricChangeDOs.add(changeDO);
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
		}
		if (customMetric != null) {
			customMetricCache.updateCustomMetric(customMetric);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMericSetting end metricId="
					+ collectMeticSetting.getMetricId());
		}
	}

	@Override
	public List<CustomMetricCollectParameter> getCustomMetricCollectsByMetricId(
			String metricId) throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollectsByInstanceId start metricId="
					+ metricId);
		}
		List<CustomMetricCollectParameter> customMetricCollectParameters = null;
		CustomMetric customMetric = customMetricCache.getCustomMetric(metricId);
		if (customMetric == null) {
			try {
				List<CustomMetricCollectDO> customMetricCollectDOs = customMetricCollectDAO
						.getMetricCollectDOById(Arrays.asList(metricId));
				if (customMetricCollectDOs != null
						&& !customMetricCollectDOs.isEmpty()) {
					customMetricCollectParameters = new ArrayList<>(
							customMetricCollectDOs.size());
					for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
						customMetricCollectParameters
								.add(metricCollectConverToBO(customMetricCollectDO));
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getMetricCollectDOById", e);
				}
				throw new CustomMetricException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getMetricCollectDOById error!");
			}
		} else {
			customMetricCollectParameters = customMetric
					.getCustomMetricCollectParameters();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollectsByInstanceId end metricId="
					+ metricId);
		}
		return customMetricCollectParameters;
	}

	@Override
	public List<CustomMetricCollectParameter> getCustomMetricCollectsByInstanceId(
			long instanceId) throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollectsByInstanceId start instanceId="
					+ instanceId);
		}
		List<CustomMetricCollectParameter> customMetricCollectParameters = null;
		List<CustomMetric> metrics = getCustomMetricsByInstanceId(instanceId);
		if (metrics != null && metrics.size() > 0) {
			customMetricCollectParameters = new ArrayList<>(metrics.size() * 3);
			for (CustomMetric customMetric : metrics) {
				if (customMetric.getCustomMetricCollectParameters() != null) {
					customMetricCollectParameters.addAll(customMetric
							.getCustomMetricCollectParameters());
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollectsByInstanceId end instanceId="
					+ instanceId);
		}
		return customMetricCollectParameters;
	}

	@Override
	public List<CustomMetricBind> getCustomMetricBindsByMetricId(String metricid)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricBindsByMetricId start metricid="
					+ metricid);
		}
		List<CustomMetricBind> customMetricBinds = null;
		String[] instanceid_pluginids = customMetricCache
				.getInstancePlugins(metricid);
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricBindsByMetricId instanceid_pluginids in cache = "
					+ Arrays.toString(instanceid_pluginids));
		}

		if (instanceid_pluginids == null) {
			List<CustomMetricBindDO> customMetricBindDOs = null;
			try {
				customMetricBindDOs = customMetricBindDAO
						.getCustomMetricBindByMetric(metricid);
				if (customMetricBindDOs != null
						&& !customMetricBindDOs.isEmpty()) {
					customMetricBinds = new ArrayList<>(
							customMetricBindDOs.size());
					instanceid_pluginids = new String[customMetricBindDOs
							.size() * 2];
					int index = 0;
					for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
						customMetricBinds
								.add(metricBindConverToBO(customMetricBindDO));
						instanceid_pluginids[index++] = String
								.valueOf(customMetricBindDO.getInstanceId());
						instanceid_pluginids[index++] = String
								.valueOf(customMetricBindDO.getPluginId());
					}
					customMetricCache.setInstancePlugins(metricid,
							instanceid_pluginids);
				} else {
					customMetricCache.setInstancePlugins(metricid,
							new String[0]);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomMetricBindByInstanceId error!", e);
				}
				throw new CustomMetricException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getCustomMetricBindByInstanceId error!");
			}
		} else if (instanceid_pluginids.length > 0) {
			customMetricBinds = new ArrayList<>(instanceid_pluginids.length / 2);
			for (int i = 0; i < instanceid_pluginids.length; i += 2) {
				CustomMetricBind customMetricBind = new CustomMetricBind();
				customMetricBind.setInstanceId(Long
						.parseLong(instanceid_pluginids[i]));
				customMetricBind.setMetricId(metricid);
				customMetricBind.setPluginId(instanceid_pluginids[i + 1]);
				customMetricBinds.add(customMetricBind);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricBindsByMetricId end metricid="
					+ metricid);
		}
		return customMetricBinds;
	}

	@Override
	public void updateCustomMericSettings(
			List<CollectMeticSetting> collectMeticSettings)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMericSettings start");
		}
		if (collectMeticSettings == null || collectMeticSettings.isEmpty()) {
			return;
		}
		List<CustomMetricDO> customMetricDOs = new ArrayList<>(
				collectMeticSettings.size());
		List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
				collectMeticSettings.size());
		Date changeDate = new Date();
		List<CustomMetric> updatedCustomMetricCaches = new ArrayList<>(
				collectMeticSettings.size());
		List<CustomChangeData> alarmChangeDatas = new ArrayList<CustomChangeData>();
		List<CustomChangeData> monitorChangeDatas = new ArrayList<CustomChangeData>();
		for (CollectMeticSetting collectMeticSetting : collectMeticSettings) {
			CustomMetric customMetric = customMetricCache
					.getCustomMetric(collectMeticSetting.getMetricId());
			CustomMetricDO customMetricDO = new CustomMetricDO();
			customMetricDO.setCustomMetricId(collectMeticSetting.getMetricId());
			boolean isUpdateMonitorInfo = false;
			if (collectMeticSetting.getAlert() != null) {
				customMetricDO.setIsAlert(collectMeticSetting.getAlert() ? "1"
						: "0");
				if (customMetric != null) {
					customMetric.getCustomMetricInfo().setAlert(
							collectMeticSetting.getAlert().booleanValue());
					CustomChangeData data = new CustomChangeData();
					data.setIsAlarm(collectMeticSetting.getAlert());
					data.setMetricId(collectMeticSetting.getMetricId());
					alarmChangeDatas.add(data);
				}
			}
			if (collectMeticSetting.getFlapping() != null) {
				customMetricDO.setFlapping(collectMeticSetting.getFlapping());
				if (customMetric != null) {
					customMetric.getCustomMetricInfo().setFlapping(
							collectMeticSetting.getFlapping().intValue());
				}
			}
			if (collectMeticSetting.getFreq() != null) {
				customMetricDO.setFreq(collectMeticSetting.getFreq().name());
				isUpdateMonitorInfo = true;
				if (customMetric != null) {
					customMetric.getCustomMetricInfo().setFreq(
							collectMeticSetting.getFreq());
				}
			}
			if (collectMeticSetting.getMonitor() != null) {
				customMetricDO
						.setIsMonitor(collectMeticSetting.getMonitor() ? "1"
								: "0");
				isUpdateMonitorInfo = true;
				if (customMetric != null) {
					customMetric.getCustomMetricInfo().setMonitor(
							collectMeticSetting.getMonitor().booleanValue());
					CustomChangeData data = new CustomChangeData();
					data.setIsMonitor(collectMeticSetting.getAlert());
					data.setMetricId(collectMeticSetting.getMetricId());
					monitorChangeDatas.add(data);
				}
			}
			customMetricDOs.add(customMetricDO);
			if (isUpdateMonitorInfo) {
				if (customMetric != null) {
					updatedCustomMetricCaches.add(customMetric);
				}
				CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
				changeDO.setChange_id(changeSeq.next());
				changeDO.setChange_time(changeDate);
				changeDO.setOccur_time(changeDate);
				changeDO.setOperate_state(0);
				changeDO.setOperateMode(CustomMetricChangeEnum.METRIC_BASIC_UPDATE
						.name());
				changeDO.setMetric_id(collectMeticSetting.getMetricId());
				metricChangeDOs.add(changeDO);
			}
		}
		try {
			customMetricDAO.updateMetrics(customMetricDOs);
			if (metricChangeDOs.size() > 0) {
				changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
			}
			for (CustomMetric toCacheMetric : updatedCustomMetricCaches) {
				customMetricCache.updateCustomMetric(toCacheMetric);
			}
			//-告警改变了，通知
			if(CollectionUtils.isNotEmpty(alarmChangeDatas)){
				customMetricAlarmManager.doMetricChangeInterceptor(alarmChangeDatas);
			}
			if(CollectionUtils.isNotEmpty(monitorChangeDatas)){
				customMetricMonitorManager.doMetricChangeInterceptor(monitorChangeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateCustomMericSettings error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateCustomMericSettings error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMericSettings end");
		}
	}

	@Override
	public void updateCustomMetricBasicInfo(CustomMetricInfo metric)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricBasicInfo start");
		}
		CustomMetricDO customMetricDO = metricConvertToDO(metric);
		try {
			customMetricDAO.updateMetric(customMetricDO);
			List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(1);
			Date changeDate = new Date();
			CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
			changeDO.setChange_id(changeSeq.next());
			changeDO.setChange_time(changeDate);
			changeDO.setOccur_time(changeDate);
			changeDO.setOperate_state(0);
			changeDO.setOperateMode(CustomMetricChangeEnum.METRIC_BASIC_UPDATE
					.name());
			changeDO.setMetric_id(metric.getId());
			metricChangeDOs.add(changeDO);
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);

			CustomMetric cacheMetric = customMetricCache.getCustomMetric(metric
					.getId());
			if (cacheMetric != null) {
				metric.setUpdateTime(customMetricDO.getUpdateTime());
				cacheMetric.setCustomMetricInfo(metric);
				customMetricCache.updateCustomMetric(cacheMetric);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateCustomMetricBasicInfo error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateCustomMetricBasicInfo error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricBasicInfo start");
		}
	}

	@Override
	public void deleteCustomMetric(String metricId)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("deleteCustomMetric start metricId=" + metricId);
		}
		try {
			customMetricDAO.removeMetricById(metricId);
			// 删除阈值
			customMetricThresholdDAO.removeThresholdsByMetricId(metricId);
			// 删除采集回来的值
			customMetricCollectDAO.removeMetricCollectByMetricId(metricId);
			customMetricDataWayDAO.removeMetricDataWaybyMetricId(metricId);
			List<CustomMetricBindDO> selectBindDOs = customMetricBindDAO
					.getCustomMetricBindByMetric(metricId);
			List<CustomChangeData> changeDatas = new ArrayList<>(5);
			
			if (CollectionUtils.isNotEmpty(selectBindDOs)) {
				CustomChangeData data = new CustomChangeData();
				data.setMetricId(metricId);
				
				// 删除绑定关系
				CustomMetricBindDO customMetricBindDOQuery = new CustomMetricBindDO();
				customMetricBindDOQuery.setMetricId(metricId);
				customMetricBindDAO
						.removeMetricBindbyDO(customMetricBindDOQuery);

				List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
						selectBindDOs.size());
				Date changeDate = new Date();
				for (CustomMetricBindDO customMetricBindDO : selectBindDOs) {
					CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
					changeDO.setChange_id(changeSeq.next());
					changeDO.setChange_time(changeDate);
					changeDO.setOccur_time(changeDate);
					changeDO.setOperate_state(0);
					changeDO.setOperateMode(CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND
							.name());
					changeDO.setInstance_id(customMetricBindDO.getInstanceId());
					changeDO.setMetric_id(customMetricBindDO.getMetricId());
					changeDO.setPlugin_id(customMetricBindDO.getPluginId());
					metricChangeDOs.add(changeDO);
					//构造通知数据
					if(data.getCancelInstanceIds() == null){
						data.setCancelInstanceIds(new ArrayList<Long>());
					}
					data.getCancelInstanceIds().add(customMetricBindDO.getInstanceId());
				}
				changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
				
				//构造通知数据
				changeDatas.add(data);
			}
			customMetricCache.removeCustomMetric(metricId);
			if(CollectionUtils.isNotEmpty(changeDatas)){
				//通知解除绑定操作
				customResourceCancelManager.doMetricChangeInterceptor(changeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteCustomMetric error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"deleteCustomMetric error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("deleteCustomMetric end metricId=" + metricId);
		}
	}

	@Override
	public void deleteCustomMetric(List<String> metricIds)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("deleteCustomMetric start metricIds=" + metricIds);
		}
		try {
			customMetricDAO.removeMetricByIds(metricIds);
			customMetricThresholdDAO.removeThresholdsByMetricIds(metricIds);
			customMetricCollectDAO.removeMetricCollectByMetricIds(metricIds);
			customMetricDataWayDAO.removeMetricDataWaybyMetricIds(metricIds);
			List<CustomMetricBindDO> selectBindDOs = customMetricBindDAO
					.getCustomMetricBindByMetrics(metricIds);
			//临时存放指标跟绑定资源的数量
			Map<String,List<Long>> cancelInstance = null;
			if (CollectionUtils.isNotEmpty(selectBindDOs)) {
				cancelInstance = new HashMap<String, List<Long>>();
				List<CustomMetricBindDO> customMetricBindDOs = new ArrayList<>();
				for (String metricId : metricIds) {
					CustomMetricBindDO customMetricBindDO = new CustomMetricBindDO();
					customMetricBindDO.setMetricId(metricId);
					customMetricBindDOs.add(customMetricBindDO);
					cancelInstance.put(metricId, null);
				}
				customMetricBindDAO.removeMetricBindbyDOs(customMetricBindDOs);

				List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
						customMetricBindDOs.size());
				Date changeDate = new Date();
				for (CustomMetricBindDO customMetricBindDO : selectBindDOs) {
					CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
					changeDO.setChange_id(changeSeq.next());
					changeDO.setChange_time(changeDate);
					changeDO.setOccur_time(changeDate);
					changeDO.setOperate_state(0);
					changeDO.setOperateMode(CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND
							.name());
					changeDO.setInstance_id(customMetricBindDO.getInstanceId());
					changeDO.setMetric_id(customMetricBindDO.getMetricId());
					changeDO.setPlugin_id(customMetricBindDO.getPluginId());
					metricChangeDOs.add(changeDO);
					
					List<Long> instanceids = cancelInstance.get(customMetricBindDO.getMetricId());
					if(instanceids == null){
						instanceids = new ArrayList<>(5);
					}
					instanceids.add(customMetricBindDO.getInstanceId());
				}
				changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
			}
			for (String metricId : metricIds) {
				customMetricCache.removeCustomMetric(metricId);
			}
			List<CustomChangeData> changeDatas = new ArrayList<>();
			//通知
			for (Entry<String, List<Long>> temp : cancelInstance.entrySet()) {
				//没有绑定资源的不通知
				if(temp.getValue() != null && !temp.getValue().isEmpty()){
					CustomChangeData data = new CustomChangeData();
					data.setMetricId(temp.getKey());
					data.setCancelInstanceIds(temp.getValue());
					changeDatas.add(data);
				}
			}
			//通知解除绑定操作
			if(CollectionUtils.isNotEmpty(changeDatas)){
				customResourceCancelManager.doMetricChangeInterceptor(changeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteCustomMetric error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"deleteCustomMetric error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("deleteCustomMetric end metricId=" + metricIds);
		}
	}

	@Override
	public void updateCustomMetricThreshold(
			List<CustomMetricThreshold> thresholds)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricThreshold start");
		}
		if (thresholds == null || thresholds.isEmpty()) {
			return;
		}
		List<CustomMetricThresholdDO> customMetricThresholdDOs = new ArrayList<>(
				thresholds.size());
		Set<String> metricIdList = new HashSet<>();
		Map<String, List<CustomMetricThreshold>> metricThresholdMap = new HashMap<>();
		for (CustomMetricThreshold customMetricThreshold : thresholds) {
			CustomMetricThresholdDO customMetricThresholdDO = metricThresholdConvertToDO(customMetricThreshold);
			customMetricThresholdDOs.add(customMetricThresholdDO);
			if (metricThresholdMap.containsKey(customMetricThreshold
					.getMetricId())) {
				metricThresholdMap.get(customMetricThreshold.getMetricId())
						.add(customMetricThreshold);
			} else {
				List<CustomMetricThreshold> list = new ArrayList<>();
				list.add(customMetricThreshold);
				metricThresholdMap.put(customMetricThreshold.getMetricId(),
						list);
			}
			metricIdList.add(customMetricThreshold.getMetricId());
		}
		try {
			customMetricThresholdDAO
					.removeThresholdsByMetricIds(new ArrayList<>(metricIdList));
			customMetricThresholdDAO
					.insertCustomMetricThresholds(customMetricThresholdDOs);
			for (String metricId : metricIdList) {
				CustomMetric cacheMetric = customMetricCache
						.getCustomMetric(metricId);
				if (cacheMetric != null) {
					cacheMetric.setCustomMetricThresholds(metricThresholdMap
							.get(metricId));
					customMetricCache.updateCustomMetric(cacheMetric);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateCustomMetricThreshold error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateCustomMetricThreshold error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricThreshold end");
		}

	}

	@Override
	public List<CustomMetricThreshold> getCustomMetricThresholds(String metricId)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricThresholds start metricId=" + metricId);
		}
		List<CustomMetricThreshold> customMetricThresholds = null;

		CustomMetric cacheMetric = customMetricCache.getCustomMetric(metricId);
		if (cacheMetric == null) {
			List<CustomMetricThresholdDO> customMetricThresholdDOs = null;
			try {
				customMetricThresholdDOs = customMetricThresholdDAO
						.getCustomMetricThresholdsByMetricId(metricId);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomMetricThresholds error!", e);
				}
				throw new CustomMetricException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getCustomMetricThresholds error!");
			}
			if (customMetricThresholdDOs != null
					&& !customMetricThresholdDOs.isEmpty()) {
				customMetricThresholds = new ArrayList<>(
						customMetricThresholdDOs.size());
				for (CustomMetricThresholdDO customMetricThresholdDO : customMetricThresholdDOs) {
					CustomMetricThreshold customMetricThreshold = metricThresholdConvertToBO(customMetricThresholdDO);
					customMetricThresholds.add(customMetricThreshold);
				}
			}
		} else {
			customMetricThresholds = cacheMetric.getCustomMetricThresholds();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricThresholds end metricId=" + metricId);
		}
		return customMetricThresholds;
	}

	@Override
	public void updateCustomMetricCollects(String metricId, String pluginId,
			List<CustomMetricCollectParameter> collectParameters)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricCollects start metricId=" + metricId
					+ " pluginId=" + pluginId);
		}
		if (collectParameters == null || collectParameters.isEmpty()) {
			return;
		}
		List<CustomMetricCollectDO> customMetricCollectDOs = new ArrayList<>(
				collectParameters.size());
		for (int i = 0; i < collectParameters.size(); i++) {
			CustomMetricCollectParameter customMetricCollectParameter = collectParameters
					.get(i);
			customMetricCollectParameter.setPluginId(pluginId);
			customMetricCollectParameter.setMetricId(metricId);
			CustomMetricCollectDO customMetricCollectDO = metricCollectConverToDO(customMetricCollectParameter);
			customMetricCollectDO.setMetricId(metricId);
			customMetricCollectDO.setPluginId(pluginId);
			customMetricCollectDO.setMetricCollectId(metricCollectSeq.next());
			customMetricCollectDOs.add(customMetricCollectDO);
		}
		try {
			customMetricCollectDAO.removeMetricCollectByMetricIdAndPluginId(
					metricId, pluginId);
			customMetricCollectDAO.insertMetricCOllects(customMetricCollectDOs);

			List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(1);
			Date changeDate = new Date();
			CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
			changeDO.setChange_id(changeSeq.next());
			changeDO.setChange_time(changeDate);
			changeDO.setOccur_time(changeDate);
			changeDO.setOperate_state(0);
			changeDO.setOperateMode(CustomMetricChangeEnum.CHANGE_METRIC_PLUGIN_COLLECT
					.name());
			changeDO.setMetric_id(metricId);
			changeDO.setPlugin_id(pluginId);
			metricChangeDOs.add(changeDO);
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);

			CustomMetric cacheMetric = customMetricCache
					.getCustomMetric(metricId);
			if (cacheMetric != null) {
				List<CustomMetricCollectParameter> oldParameters = cacheMetric
						.getCustomMetricCollectParameters();
				List<CustomMetricCollectParameter> newParameters = new ArrayList<>(
						collectParameters);
				if (oldParameters != null && oldParameters.size() > 0) {
					for (CustomMetricCollectParameter p : oldParameters) {
						if (p.getPluginId().equals(pluginId) == false) {
							newParameters.add(p);
						}
					}
				}
				cacheMetric.setCustomMetricCollectParameters(newParameters);
				customMetricCache.updateCustomMetric(cacheMetric);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("setCustomMetricCollects error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"setCustomMetricCollects error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("updateCustomMetricCollects end metricId=" + metricId
					+ " pluginId=" + pluginId);
		}

	}

	@Override
	public List<CustomMetricCollectParameter> getCustomMetricCollects(
			String metricId, String pluginId) throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollects start metricId=" + metricId
					+ " pluginId=" + pluginId);
		}
		List<CustomMetricCollectParameter> customMetricCollectParameters = null;
		CustomMetric cacheMetric = customMetricCache.getCustomMetric(metricId);
		if (cacheMetric != null) {
			customMetricCollectParameters = cacheMetric
					.getCustomMetricCollectParameters();
		} else {
			List<CustomMetricCollectDO> customMetricCollectDOs = null;
			try {
				customMetricCollectDOs = customMetricCollectDAO
						.getMetricCollectDOByMetricIdAndpluginId(metricId,
								pluginId);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomMetricCollects error!", e);
				}
				throw new CustomMetricException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getCustomMetricCollects error!");
			}
			if (customMetricCollectDOs != null
					&& !customMetricCollectDOs.isEmpty()) {
				customMetricCollectParameters = new ArrayList<>(
						customMetricCollectDOs.size());
				for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
					CustomMetricCollectParameter customMetricCollectParameter = metricCollectConverToBO(customMetricCollectDO);
					customMetricCollectParameters
							.add(customMetricCollectParameter);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricCollects end metricId=" + metricId
					+ " pluginId=" + pluginId);
		}
		return customMetricCollectParameters;
	}

	@Override
	public void addCustomMetricBinds(List<CustomMetricBind> customMetricBinds)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("addCustomMetricBinds start size = "
					+ customMetricBinds.size());
		}
		if (customMetricBinds == null || customMetricBinds.isEmpty()) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("addCustomMetricBinds start customMetricBinds = " + customMetricBinds);
		}
		List<CustomMetricBindDO> customMetricBindDOs = new ArrayList<>(
				customMetricBinds.size());
		List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
				customMetricBindDOs.size());
		Date changeDate = new Date();
		for (CustomMetricBind customMetricBind : customMetricBinds) {
			CustomMetricBindDO customMetricBindDO = metricBindConverToDO(customMetricBind);
			customMetricBindDOs.add(customMetricBindDO);

			CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
			changeDO.setChange_id(changeSeq.next());
			changeDO.setChange_time(changeDate);
			changeDO.setOccur_time(changeDate);
			changeDO.setOperate_state(0);
			changeDO.setOperateMode(CustomMetricChangeEnum.ADD_METRIC_PLUGIN_BIND
					.name());
			changeDO.setInstance_id(customMetricBind.getInstanceId());
			changeDO.setMetric_id(customMetricBind.getMetricId());
			changeDO.setPlugin_id(customMetricBind.getPluginId());
			metricChangeDOs.add(changeDO);
		}
		try {
			customMetricBindDAO.insertMetricBinds(customMetricBindDOs);
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
			customMetricCache.addCacheCustomMetricBinds(customMetricBinds);

			if (logger.isDebugEnabled()) {
				List<Long> is = new ArrayList<Long>();
				List<String> ms = new ArrayList<String>();
				for (CustomMetricBind customMetricBind : customMetricBinds) {
					long instanceId = customMetricBind.getInstanceId();
					String metricId = customMetricBind.getMetricId();
					if (!is.contains(instanceId)) {
						is.add(instanceId);
						String[] metricPlugins = customMetricCache
								.getMetricPlugins(instanceId);
						StringBuilder sb = new StringBuilder("addCustomMetricBinds instanceId in addcache = ");
						sb.append(instanceId).append(",");
						sb.append(Arrays.toString(metricPlugins));
						String bs = sb.toString();
						logger.debug(bs);
					}
					if (!ms.contains(metricId)) {
						ms.add(metricId);
						String[] instancePlugins = customMetricCache
								.getInstancePlugins(metricId);
						StringBuilder sb = new StringBuilder("addCustomMetricBinds instanceId in addcache = ");
						sb.append(metricId).append(",");
						sb.append(Arrays.toString(instancePlugins));
						String bs = sb.toString();
						logger.debug(bs);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addCustomMetricBinds error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"addCustomMetricBinds error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("addCustomMetricBinds end");
		}
	}

	@Override
	public void deleteCustomMetricBinds(List<CustomMetricBind> customMetricBinds)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			for (CustomMetricBind cbBind : customMetricBinds) {
				long instanceId = cbBind.getInstanceId();
				String[] metricId_pluginIds = customMetricCache
						.getMetricPlugins(instanceId);
				logger.debug("deleteCustomMetricBinds cache start metricId_pluginIds = "
						+ Arrays.toString(metricId_pluginIds));
			}
			logger.debug("deleteCustomMetricBinds start customMetricBinds = "
					+ customMetricBinds.size());
		}
		if (customMetricBinds == null || customMetricBinds.isEmpty()) {
			return;
		}
		List<CustomMetricBindDO> customMetricBindDOs = new ArrayList<>(
				customMetricBinds.size());
		List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
				customMetricBindDOs.size());
		Date changeDate = new Date();
		//临时存放指标跟绑定资源的数量
		Map<String,List<Long>> cancelInstance = new HashMap<String, List<Long>>(5);
		for (CustomMetricBind customMetricBind : customMetricBinds) {
			String metricId = customMetricBind.getMetricId();
			CustomMetricBindDO customMetricBindDO = metricBindConverToDO(customMetricBind);
			customMetricBindDOs.add(customMetricBindDO);

			CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
			changeDO.setChange_id(changeSeq.next());
			changeDO.setChange_time(changeDate);
			changeDO.setOccur_time(changeDate);
			changeDO.setOperate_state(0);
			changeDO.setOperateMode(CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND
					.name());
			changeDO.setInstance_id(customMetricBind.getInstanceId());
			changeDO.setMetric_id(metricId);
			changeDO.setPlugin_id(customMetricBind.getPluginId());
			metricChangeDOs.add(changeDO);
			List<Long> instanceids = cancelInstance.get(metricId);
			if(instanceids == null){
				instanceids = new ArrayList<>(5);
				cancelInstance.put(metricId, instanceids);
			}
			instanceids.add(customMetricBindDO.getInstanceId());
		}
		try {
			customMetricBindDAO.removeMetricBindbyDOs(customMetricBindDOs);
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
			customMetricCache.deleteCacheCustomMetricBinds(customMetricBinds);
			List<CustomChangeData> changeDatas = new ArrayList<>();
			//通知
			for (Entry<String, List<Long>> temp : cancelInstance.entrySet()) {
				//没有绑定资源的不通知
				if(CollectionUtils.isNotEmpty(temp.getValue())){
					CustomChangeData data = new CustomChangeData();
					data.setMetricId(temp.getKey());
					data.setCancelInstanceIds(temp.getValue());
					changeDatas.add(data);
				}
			}
			//通知解除绑定操作
			if(CollectionUtils.isNotEmpty(changeDatas)){
				customResourceCancelManager.doMetricChangeInterceptor(changeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteCustomMetricBinds error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"deleteCustomMetricBinds error!");
		}
		if (logger.isDebugEnabled()) {
			for (CustomMetricBind cbBind : customMetricBinds) {
				long instanceId = cbBind.getInstanceId();
				String[] metricId_pluginIds = customMetricCache
						.getMetricPlugins(instanceId);
				logger.debug("deleteCustomMetricBinds cache end metricId_pluginIds = "
						+ Arrays.toString(metricId_pluginIds));
			}
			logger.debug("deleteCustomMetricBinds end customMetricBinds = "
					+ customMetricBinds.size());
		}
	}

	@Override
	public void clearCustomMetricBinds(String metricId, String pluginId)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("clearCustomMetricBinds start metricId=" + metricId
					+ " pluginId=" + pluginId);
		}
		CustomMetricBindDO customMetricBindDO = new CustomMetricBindDO();
		customMetricBindDO.setMetricId(metricId);
		customMetricBindDO.setPluginId(pluginId);
		try {
			List<CustomMetricBindDO> customMetricBindDOs = customMetricBindDAO
					.getCustomMetricBindByMetricAndPluginId(metricId, pluginId);
			customMetricBindDAO.removeMetricBindbyDO(customMetricBindDO);
			//临时存放指标跟绑定资源的数量
			Map<String,List<Long>> cancelInstance = new HashMap<String, List<Long>>(5);
			if (customMetricBindDOs != null && !customMetricBindDOs.isEmpty()) {
				List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
						customMetricBindDOs.size());
				List<CustomMetricBind> customMetricBinds = new ArrayList<>();
				Date changeDate = new Date();
				for (CustomMetricBindDO bind : customMetricBindDOs) {
					CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
					changeDO.setChange_id(changeSeq.next());
					changeDO.setChange_time(changeDate);
					changeDO.setOccur_time(changeDate);
					changeDO.setOperate_state(0);
					changeDO.setOperateMode(CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND
							.name());
					changeDO.setInstance_id(bind.getInstanceId());
					changeDO.setMetric_id(bind.getMetricId());
					changeDO.setPlugin_id(bind.getPluginId());
					metricChangeDOs.add(changeDO);
					CustomMetricBind customMetricBind = metricBindConverToBO(bind);
					customMetricBinds.add(customMetricBind);
					
					List<Long> tempInstanceIds = cancelInstance.get(bind.getMetricId());
					if(tempInstanceIds == null){
						tempInstanceIds = new ArrayList<Long>(5);
						cancelInstance.put(bind.getMetricId(), tempInstanceIds);
					}
					tempInstanceIds.add(bind.getInstanceId());
				}
				changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
				customMetricCache.deleteCacheCustomMetricBinds(customMetricBinds);
				if (logger.isDebugEnabled()) {
					String[] instanceId_pluginIds = customMetricCache
							.getInstancePlugins(metricId);
					StringBuilder sb = new StringBuilder("clearCustomMetricBinds metricId end in cache = ");
					sb.append(metricId).append(",");
					sb.append(Arrays.toString(instanceId_pluginIds));
					String bs = sb.toString();
					logger.debug(bs);
					List<Long> is = new ArrayList<Long>();
					for (CustomMetricBindDO cMetricBindDO : customMetricBindDOs) {
						long instanceId = cMetricBindDO.getInstanceId();
						if (!is.contains(instanceId)) {
							is.add(instanceId);
							String[] metricPlugins = customMetricCache
									.getMetricPlugins(instanceId);
							StringBuilder sb1 = new StringBuilder("clearCustomMetricBinds instanceId end in addcache = ");
							sb1.append(instanceId).append(",");
							sb1.append(Arrays.toString(metricPlugins));
							String bs1 = sb1.toString();
							logger.debug(bs1);
						}
					}
				}
			}
			List<CustomChangeData> changeDatas = new ArrayList<>();
			//通知
			for (Entry<String, List<Long>> temp : cancelInstance.entrySet()) {
				//没有绑定资源的不通知
				if(CollectionUtils.isNotEmpty(temp.getValue())){
					CustomChangeData data = new CustomChangeData();
					data.setMetricId(temp.getKey());
					data.setCancelInstanceIds(temp.getValue());
					changeDatas.add(data);
				}
			}
			customMetricCache.removeCacheCustomMetricBinds(metricId, pluginId);
			//通知解除绑定操作
			if(CollectionUtils.isNotEmpty(changeDatas)){
				customResourceCancelManager.doResourceMonitorChangeInterceptor(changeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("clearCustomMetricBinds error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"clearCustomMetricBinds error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("clearCustomMetricBinds end metricId=" + metricId
					+ " pluginId=" + pluginId);
		}
	}

	@Override
	public void clearCustomMetricBindsByInstanceIds(List<Long> instanceIds)
			throws CustomMetricException {
		if (logger.isDebugEnabled()) {
			logger.debug("clearCustomMetricBindsByInstanceIds start=" + instanceIds);
		}
		if(CollectionUtils.isEmpty(instanceIds)){
			if (logger.isDebugEnabled()) {
				logger.debug("clearCustomMetricBindsByInstanceIds end=" + instanceIds);
			}
			return;
		}
		try {
			List<CustomMetricBindDO> customMetricBindDOs = customMetricBindDAO.getCustomMetricBindByInstanceIds(instanceIds);
			if( customMetricBindDOs == null || customMetricBindDOs.isEmpty()){
				return;
			}
			customMetricBindDAO.removeMetricBindbyInstanceIds(instanceIds);
			List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>(
					instanceIds.size());
			Date changeDate = new Date();
			for (Long instanceIdObj : instanceIds) {
				CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
				changeDO.setChange_id(changeSeq.next());
				changeDO.setChange_time(changeDate);
				changeDO.setOccur_time(changeDate);
				changeDO.setOperate_state(0);
				changeDO.setOperateMode(CustomMetricChangeEnum.INSTANCE_CANCEL_MONITOR
						.name());
				changeDO.setInstance_id(instanceIdObj);
				metricChangeDOs.add(changeDO);
			}
			changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
			
			for (Long instanceIdObj : instanceIds) {
				customMetricCache.clearCacheCustomMetricBinds(instanceIdObj);
			}
			//临时存放指标跟绑定资源的数量
			Map<String,List<Long>> cancelInstance = new HashMap<String, List<Long>>(5);
			for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
				List<Long> tempInstanceIds = cancelInstance.get(customMetricBindDO.getMetricId());
				if(tempInstanceIds == null){
					tempInstanceIds = new ArrayList<Long>(5);
					cancelInstance.put(customMetricBindDO.getMetricId(), tempInstanceIds);
				}
				tempInstanceIds.add(customMetricBindDO.getInstanceId());
			}
			List<CustomChangeData> changeDatas = new ArrayList<>();
			//通知
			for (Entry<String, List<Long>> temp : cancelInstance.entrySet()) {
				//没有绑定资源的不通知
				if(CollectionUtils.isNotEmpty(temp.getValue())){
					CustomChangeData data = new CustomChangeData();
					data.setMetricId(temp.getKey());
					data.setCancelInstanceIds(temp.getValue());
					changeDatas.add(data);
				}
			}
			//通知解除绑定操作
			if(CollectionUtils.isNotEmpty(changeDatas)){
				customResourceCancelManager.doResourceMonitorChangeInterceptor(changeDatas);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("clearCustomMetricBindsByInstanceIds error!", e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"clearCustomMetricBindsByInstanceIds error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("clearCustomMetricBindsByInstanceIds end=" + instanceIds);
		}
	}

	public void setCustomMetricBindDAO(CustomMetricBindDAO customMetricBindDAO) {
		this.customMetricBindDAO = customMetricBindDAO;
	}

	public void setCustomMetricCollectDAO(
			CustomMetricCollectDAO customMetricCollectDAO) {
		this.customMetricCollectDAO = customMetricCollectDAO;
	}

	public void setCustomMetricDAO(CustomMetricDAO customMetricDAO) {
		this.customMetricDAO = customMetricDAO;
	}

	public void setCustomMetricThresholdDAO(
			CustomMetricThresholdDAO customMetricThresholdDAO) {
		this.customMetricThresholdDAO = customMetricThresholdDAO;
	}

	public void setMetricSeq(ISequence metricSeq) {
		this.metricSeq = metricSeq;
	}

	public void setMetricCollectSeq(ISequence metricCollectSeq) {
		this.metricCollectSeq = metricCollectSeq;
	}

	public void setCustomMetricDataWayDAO(
			CustomMetricDataWayDAO customMetricDataWayDAO) {
		this.customMetricDataWayDAO = customMetricDataWayDAO;
	}

	@Override
	public int getCustomMetricsCount(CustomMetricQuery customMetricQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricsCount start");
		}
		CustomMetricDO query = null;
		if (customMetricQuery != null) {
			query = new CustomMetricDO();
			query.setCustomMetricName(customMetricQuery.getCustomMetricName());
			if (customMetricQuery.getCustomMetricStyle() != null) {
				query.setCustomMetricStyle(customMetricQuery
						.getCustomMetricStyle().name());
			}
			if (customMetricQuery.getIsAlert() != null) {
				query.setIsAlert(customMetricQuery.getIsAlert().booleanValue() ? "1"
						: "0");
			}
			if (customMetricQuery.getIsMonitor() != null) {
				query.setIsMonitor(customMetricQuery.getIsMonitor()
						.booleanValue() ? "1" : "0");
			}
		}
		int count = customMetricDAO.getCustomMetricsCount(query);
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomMetricsCount end");
		}
		return count;
	}

	@Override
	public void updateCustomMetricDataProcess(
			CustomMetricDataProcess dataProcess) throws CustomMetricException {
		CustomMetricDataWayDO customMetricDataWayDO = metricDataWayConverToDO(dataProcess);
		try {
			customMetricDataWayDAO
					.removeMetricDataWaybyDO(customMetricDataWayDO);
			customMetricDataWayDAO.insertMetricDataWay(customMetricDataWayDO);
			CustomMetric metric = customMetricCache.getCustomMetric(dataProcess
					.getMetricId());
			metric.setCustomMetricDataProcess(dataProcess);
			customMetricCache.updateCustomMetric(metric);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateCustomMetricDataProcess error:" + e);
			}
			throw new CustomMetricException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateCustomMetricDataProcess error:" + e);
		}
	}

}
