/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.instance.dao.MetricAvailQueryDAO;
import com.mainsteam.stm.common.instance.dao.obj.MetricAvailDataDO;
import com.mainsteam.stm.common.metric.obj.AvailMetricData;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author ziw
 * 
 */
public class MetricAvailQueryService implements MetricAvailQueryServiceMBean {

	private static final Log logger = LogFactory
			.getLog(MetricAvailQueryService.class);
	private MetricAvailQueryDAO availQueryDAO;

	/**
	 * 
	 */
	public MetricAvailQueryService() {
	}

	/**
	 * @param availQueryDAO
	 *            the availQueryDAO to set
	 */
	public final void setAvailQueryDAO(MetricAvailQueryDAO availQueryDAO) {
		this.availQueryDAO = availQueryDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.common.metric.MetricAvailQueryServiceMBean#
	 * getParentInsanceAvailMetricDatas(int)
	 */
	@Override
	public List<AvailMetricData> getParentInsanceAvailMetricDatas(int nodeGroupId,int start,int length) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(
					"getParentInsanceAvailMetricDatas start=");
			b.append(start);
			b.append(" nodeGroupId=");
			b.append(nodeGroupId);
			b.append(" length=").append(length);
			logger.info(b.toString());
		}
		List<AvailMetricData> availMetricDatas = null;
		Page<MetricAvailDataDO, String> page = new Page<>(start,
				length);
		page.setCondition(String.valueOf(nodeGroupId));
		List<MetricAvailDataDO> dataDOs = availQueryDAO.getAvailDataDOs(page);
		if (dataDOs != null && dataDOs.size() > 0) {
			availMetricDatas = new ArrayList<>();
			for (MetricAvailDataDO metricAvailDataDO : dataDOs) {
				availMetricDatas.add(new AvailMetricData(metricAvailDataDO
						.getInstanceId(), metricAvailDataDO.getMetricId(),
						metricAvailDataDO.getMetricValue()));
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("getParentInsanceAvailMetricDatas end.availMetricDatas.size="
					+ (availMetricDatas == null ? -1 : availMetricDatas.size()));
		}
		return availMetricDatas;
	}

}
