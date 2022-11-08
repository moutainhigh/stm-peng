/**
 * 
 */
package com.mainsteam.stm.metric.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.metric.CustomMetricChangeService;
import com.mainsteam.stm.metric.dao.CustomMetricChangeResultDAO;
import com.mainsteam.stm.metric.dao.CustomMetricChangeDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeResultDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;
import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.obj.CustomMetricChangeResult;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
public class CustomMetricChangeServiceImpl implements CustomMetricChangeService {

	private static final Log logger = LogFactory
			.getLog(CustomMetricChangeService.class);

	private CustomMetricChangeDAO changeDAO;

	private CustomMetricChangeResultDAO changeResultDAO;
	
	private ISequence changeSeq;

	/**
	 * 
	 */
	public CustomMetricChangeServiceImpl() {
	}
	

	/**
	 * @param changeSeq the changeSeq to set
	 */
	public final void setChangeSeq(ISequence changeSeq) {
		this.changeSeq = changeSeq;
	}



	/**
	 * @param changeDAO
	 *            the changeDAO to set
	 */
	public final void setChangeDAO(CustomMetricChangeDAO changeDAO) {
		this.changeDAO = changeDAO;
	}

	/**
	 * @param changeResultDAO
	 *            the changeResultDAO to set
	 */
	public final void setChangeResultDAO(
			CustomMetricChangeResultDAO changeResultDAO) {
		this.changeResultDAO = changeResultDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.CustomMetricChangeService#
	 * selectChangeDOsWithNotApply(int)
	 */
	@Override
	public List<CustomMetricChange> selectChangesWithNotApply(int limit) {
		if (logger.isTraceEnabled()) {
			logger.trace("selectChangeDOsWithNotApply start limit=" + limit);
		}
		List<CustomMetricChange> changes = null;
		List<CustomMetricChangeDO> changeDOs = changeDAO
				.selectChangeDOsWithNotApply(limit);
		if (changeDOs != null && changeDOs.size() > 0) {
			changes = new ArrayList<>(changeDOs.size());
			for (CustomMetricChangeDO customMetricChangeDO : changeDOs) {
				CustomMetricChange change = new CustomMetricChange();
				change.setChangeId(customMetricChangeDO.getChange_id());
				change.setChangeTime(customMetricChangeDO.getChange_time());
				change.setInstanceId(customMetricChangeDO.getInstance_id() == null ? -1
						: customMetricChangeDO.getInstance_id());
				change.setMetricId(customMetricChangeDO.getMetric_id());
				change.setOccurTime(customMetricChangeDO.getOccur_time());
				change.setOperateState(customMetricChangeDO.getOperate_state() == 1);
				change.setOperateMode(customMetricChangeDO.getOperateMode());
				change.setPluginId(customMetricChangeDO.getPlugin_id());
				changes.add(change);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("selectChangeDOsWithNotApply end");
		}
		return changes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.CustomMetricChangeService#
	 * updateCustomMetricChangeDOToApply(long[])
	 */
	@Override
	public void updateCustomMetricChangeToApply(long[] changeIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomMetricChangeDOToApply start");
		}
		changeDAO.updateCustomMetricChangeDOToApply(changeIds);
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomMetricChangeDOToApply end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.CustomMetricChangeService#
	 * getCustomMetricChangeResults(long[])
	 */
	@Override
	public List<CustomMetricChangeResult> getCustomMetricChangeResults(
			long[] changeIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomMetricChangeResults start");
		}
		if (changeIds == null || changeIds.length <= 0) {
			return null;
		}
		List<CustomMetricChangeResult> metricChangeResults = null;
		List<CustomMetricChangeResultDO> applyDOs = changeResultDAO
				.select(changeIds);
		if (applyDOs != null && applyDOs.size() > 0) {
			metricChangeResults = new ArrayList<>(applyDOs.size());
			for (CustomMetricChangeResultDO customMetricChangeApplyDO : applyDOs) {
				CustomMetricChangeResult changeResult = new CustomMetricChangeResult();
				changeResult.setChangeId(customMetricChangeApplyDO
						.getChange_id());
				changeResult.setDcsGroupId(customMetricChangeApplyDO
						.getDcs_group_id());
				changeResult.setOperateTime(customMetricChangeApplyDO
						.getOperate_time());
				changeResult.setResultState(customMetricChangeApplyDO
						.getResult_state() == 1);
				metricChangeResults.add(changeResult);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomMetricChangeResults end");
		}
		return metricChangeResults;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.CustomMetricChangeService#
	 * insertCustomMetricChangeResults(java.util.List)
	 */
	@Override
	public void insertCustomMetricChangeResults(
			List<CustomMetricChangeResult> changeResults) {
		if (logger.isTraceEnabled()) {
			logger.trace("insertCustomMetricChangeResults start");
		}
		if (changeResults == null || changeResults.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("insertCustomMetricChangeResults emtpy parameter.");
			}
			return;
		}
		List<CustomMetricChangeResultDO> applyDOs = new ArrayList<>(
				changeResults.size());
		for (CustomMetricChangeResult customMetricChangeResult : changeResults) {
			CustomMetricChangeResultDO applyDO = new CustomMetricChangeResultDO();
			applyDO.setChange_id(customMetricChangeResult.getChangeId());
			applyDO.setDcs_group_id(customMetricChangeResult.getDcsGroupId());
			applyDO.setOperate_time(customMetricChangeResult.getOperateTime());
			applyDO.setResult_state(customMetricChangeResult.isResultState() ? 1
					: 0);
			applyDOs.add(applyDO);
		}
		changeResultDAO.insert(applyDOs);
		if (logger.isTraceEnabled()) {
			logger.trace("insertCustomMetricChangeResults end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.metric.CustomMetricChangeService#removeCustomMetricChanges
	 * (java.util.Date)
	 */
	@Override
	public void removeCustomMetricChanges(Date expireDate) {
		if (logger.isInfoEnabled()) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			logger.info("removeCustomMetricChanges start expireDate="
					+ format.format(expireDate));
		}
		changeResultDAO.remove(expireDate);
		changeDAO.remove(expireDate);
		if (logger.isInfoEnabled()) {
			logger.info("removeCustomMetricChanges end");
		}
	}

	@Override
	public void updateCustomMetricChangeResults(
			List<CustomMetricChangeResult> changeResults) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomMetricChangeResults start");
		}
		if (changeResults == null || changeResults.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("updateCustomMetricChangeResults emtpy parameter.");
			}
			return;
		}
		List<CustomMetricChangeResultDO> applyDOs = new ArrayList<>(
				changeResults.size());
		for (CustomMetricChangeResult customMetricChangeResult : changeResults) {
			CustomMetricChangeResultDO applyDO = new CustomMetricChangeResultDO();
			applyDO.setChange_id(customMetricChangeResult.getChangeId());
			applyDO.setDcs_group_id(customMetricChangeResult.getDcsGroupId());
			applyDO.setOperate_time(customMetricChangeResult.getOperateTime());
			applyDOs.add(applyDO);
		}
		changeResultDAO.updateCustomMetricChangeDOToApply(applyDOs);
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomMetricChangeResults end");
		}
	}

	@Override
	public void occurCustomMetricChanges(List<CustomMetricChange> changes) {
		if (logger.isTraceEnabled()) {
			logger.trace("insertCustomMetricChanges start");
		}
		if(changes == null || changes.isEmpty()){
			if (logger.isWarnEnabled()) {
				logger.warn("insertCustomMetricChanges Empty changes to insert.");
			}
			return;
		}
		List<CustomMetricChangeDO> changesDOs =  new ArrayList<>();
		Date current = new Date();
		for (CustomMetricChange change : changes) {
			CustomMetricChangeDO cdo = new CustomMetricChangeDO();
			cdo.setChange_id(changeSeq.next());
			cdo.setChange_time(current);
			cdo.setInstance_id(change.getChangeId());
			cdo.setMetric_id(change.getMetricId());
			cdo.setOccur_time(current);
			cdo.setOperate_state(0);
			cdo.setOperateMode(change.getOperateMode());
			cdo.setPlugin_id(change.getPluginId());
			changesDOs.add(cdo);
		}
		changeDAO.addCustomMetricChangeDOs(changesDOs);
		if (logger.isTraceEnabled()) {
			logger.trace("insertCustomMetricChanges end");
		}
	}
}
