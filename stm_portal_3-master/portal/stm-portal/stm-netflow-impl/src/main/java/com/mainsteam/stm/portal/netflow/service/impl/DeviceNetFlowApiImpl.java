/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.netflow.api.IDeviceNetFlowApi;
import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowPageBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.IDeviceNetFlowDao;

/**
 * <li>文件名称: DeviceImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class DeviceNetFlowApiImpl implements IDeviceNetFlowApi {

	private IDeviceNetFlowDao deviceDao;

	@SuppressWarnings("unused")
	private ISequence seq;

	/**
	 * 取出某个设备在当前时间符合要求的终端的流量的信心
	 */

	@Override
	public DeviceNetFlowPageBo findTerminalsByDevice(DeviceConditionBo dcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(dcBo.getStarttime(),
				dcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(dcBo.getStarttime(), dcBo.getEndtime())/1000;
		dcBo.setTableSubfixTime(timePart);
		dcBo.setTimepart(timePeriod + "");
		dcBo.timeFomratChange();
		String showPagination = dcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = dcBo.getRecordCount();
		} else {
			rowCount = dcBo.getRowCount();
		}
		Page<DeviceNetFlowBo, DeviceConditionBo> page = new Page<DeviceNetFlowBo, DeviceConditionBo>(
				dcBo.getStartRow(), rowCount, dcBo);
		Whole whole = deviceDao.getAllFindTerminalsFlows(dcBo);
		DeviceNetFlowPageBo deviceNetFlowPageBo = new DeviceNetFlowPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			dcBo.setAllterminalsFlows(whole.getWholeFlows() + "");
			dcBo.setWholeFlows(whole.getWholeFlows() + "");
			dcBo.setWholePackets(whole.getWholePackets() + "");
			dcBo.setWholeConnects(whole.getWholeConnects() + "");
			deviceDao.findTerminalsByDevice(page);
			deviceNetFlowPageBo.setStartRow(page.getStartRow());
			deviceNetFlowPageBo.setRowCount(page.getRowCount());
			deviceNetFlowPageBo.setTotalRecord(page.getTotalRecord());
			deviceNetFlowPageBo.setRows(page.getDatas());
			deviceNetFlowPageBo.setSort(dcBo.getSort());
		}
		return deviceNetFlowPageBo;
	}

	@Override
	public DeviceNetFlowChartDataBo termianlChartonCloumByDevice(
			DeviceConditionBo dcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				dcBo.getStarttime(), dcBo.getEndtime(),
				dcBo.getTableSubfixTime());
		List<DeviceNetFlowChartDataModel> charList = new ArrayList<DeviceNetFlowChartDataModel>();
		DeviceNetFlowChartDataBo tchartbo = new DeviceNetFlowChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		dcBo.setTimePoints(timePoints);
		for (String terminalips : dcBo.getTerminalsIp()) {
			dcBo.setCurrentTerminals(terminalips);
			List<DeviceNetFlowBo> bo = deviceDao
					.terminalsCharOnCloumByDevice(dcBo);
			List<Double> record = new ArrayList<Double>();
			DeviceNetFlowChartDataModel tchartdataModel = new DeviceNetFlowChartDataModel();
			tchartdataModel.setName(dcBo.getCurrentTerminals());
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.DeviceNetFlowChartResultFilter(dcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setDeviceNetFlowChartDataModel(charList);
		return tchartbo;
	}

	@Override
	public DeviceNetFlowPageBo findSessionsByDevice(DeviceConditionBo dcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(dcBo.getStarttime(),
				dcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(dcBo.getStarttime(), dcBo.getEndtime())/1000;
		dcBo.setTableSubfixTime(timePart);
		dcBo.setTimepart(timePeriod + "");
		dcBo.timeFomratChange();
		String showPagination = dcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = dcBo.getRecordCount();
		} else {
			rowCount = dcBo.getRowCount();
		}
		Page<DeviceNetFlowBo, DeviceConditionBo> page = new Page<DeviceNetFlowBo, DeviceConditionBo>(
				dcBo.getStartRow(), rowCount, dcBo);
		Whole whole = deviceDao.getAllFindSessionsFlows(dcBo);
		DeviceNetFlowPageBo deviceNetFlowPageBo = new DeviceNetFlowPageBo();
		if (whole != null && !"".equals(whole.getWholeFlows())) {
			dcBo.setAllsessionFlows(whole.getWholeFlows() + "");
			dcBo.setWholeFlows(whole.getWholeFlows() + "");
			dcBo.setWholePackets(whole.getWholePackets() + "");
			dcBo.setWholeConnects(whole.getWholeConnects() + "");
			deviceDao.findSessionsByDevice(page);
			deviceNetFlowPageBo.setStartRow(page.getStartRow());
			deviceNetFlowPageBo.setRowCount(page.getRowCount());
			deviceNetFlowPageBo.setTotalRecord(page.getTotalRecord());
			deviceNetFlowPageBo.setRows(page.getDatas());
			deviceNetFlowPageBo.setSort(dcBo.getSort());
		}
		return deviceNetFlowPageBo;
	}

	@Override
	public DeviceNetFlowChartDataBo sessionChartonCloumByDevice(
			DeviceConditionBo dcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				dcBo.getStarttime(), dcBo.getEndtime(),
				dcBo.getTableSubfixTime());
		List<DeviceNetFlowChartDataModel> charList = new ArrayList<DeviceNetFlowChartDataModel>();
		DeviceNetFlowChartDataBo schartbo = new DeviceNetFlowChartDataBo();
		schartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		dcBo.setTimePoints(timePoints);
		List<Map<String, String>> currentSession = dcBo.getSessionips();

		for (Map<String, String> current : currentSession) {
			Set<String> srcset = current.keySet();
			Iterator<String> it = srcset.iterator();
			if (it.hasNext()) {
				String currentSrcIp = it.next();
				String currentDstIp = current.get(currentSrcIp);
				dcBo.setCurrentSrcIp(currentSrcIp);
				dcBo.setCurrentDstIp(currentDstIp);
				List<DeviceNetFlowBo> bo = deviceDao
						.sessionsCharOnCloumByDevice(dcBo);
				List<Double> record = new ArrayList<Double>();
				DeviceNetFlowChartDataModel schartdataModel = new DeviceNetFlowChartDataModel();
				schartdataModel.setName(dcBo.getCurrentSrcIp() + "-"
						+ dcBo.getCurrentDstIp());
				for (int i = 0; i < timePoints.size(); i++) {
					record.add(new Double(0).doubleValue());
				}

				record = NetflowUtil.DeviceNetFlowChartResultFilter(dcBo, bo,
						record, schartbo);
				schartdataModel.setData(record);
				charList.add(schartdataModel);
			}
		}
		schartbo.setDeviceNetFlowChartDataModel(charList);
		return schartbo;
	}

	@Override
	public DeviceNetFlowPageBo findNextHopsByDevice(DeviceConditionBo dcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(dcBo.getStarttime(),
				dcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(dcBo.getStarttime(), dcBo.getEndtime())/1000;
		dcBo.setTableSubfixTime(timePart);
		dcBo.setTimepart(timePeriod + "");
		dcBo.timeFomratChange();
		String showPagination = dcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = dcBo.getRecordCount();
		} else {
			rowCount = dcBo.getRowCount();
		}
		Page<DeviceNetFlowBo, DeviceConditionBo> page = new Page<DeviceNetFlowBo, DeviceConditionBo>(
				dcBo.getStartRow(), rowCount, dcBo);
		Whole whole = deviceDao.getAllFindNextHopsFlows(dcBo);
		DeviceNetFlowPageBo deviceNetFlowPageBo = new DeviceNetFlowPageBo();
		if (whole != null && !"".equals(whole.getWholeFlows())) {
			dcBo.setAllNextHopsFlows(whole.getWholeFlows() + "");
			dcBo.setWholeFlows(whole.getWholeFlows() + "");
			dcBo.setWholePackets(whole.getWholePackets() + "");
			dcBo.setWholeConnects(whole.getWholeConnects() + "");
			deviceDao.findNextHopsByDevice(page);
			deviceNetFlowPageBo.setStartRow(page.getStartRow());
			deviceNetFlowPageBo.setRowCount(page.getRowCount());
			deviceNetFlowPageBo.setTotalRecord(page.getTotalRecord());
			deviceNetFlowPageBo.setRows(page.getDatas());
			deviceNetFlowPageBo.setSort(dcBo.getSort());
		}
		return deviceNetFlowPageBo;
	}

	public DeviceNetFlowChartDataBo nextHopsChartonCloumByDevice(
			DeviceConditionBo dcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				dcBo.getStarttime(), dcBo.getEndtime(),
				dcBo.getTableSubfixTime());
		List<DeviceNetFlowChartDataModel> charList = new ArrayList<DeviceNetFlowChartDataModel>();
		DeviceNetFlowChartDataBo nhchartbo = new DeviceNetFlowChartDataBo();
		nhchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		dcBo.setTimePoints(timePoints);
		for (String nextHopips : dcBo.getNextHopsIp()) {
			dcBo.setCurrentNextHop(nextHopips);
			List<DeviceNetFlowBo> bo = deviceDao
					.nextHopsCharOnCloumByDevice(dcBo);
			List<Double> record = new ArrayList<Double>();
			DeviceNetFlowChartDataModel deviceNetFlowChartDataModel = new DeviceNetFlowChartDataModel();
			deviceNetFlowChartDataModel.setName(dcBo.getCurrentNextHop());
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.DeviceNetFlowChartResultFilter(dcBo, bo,
					record, nhchartbo);
			deviceNetFlowChartDataModel.setData(record);

			charList.add(deviceNetFlowChartDataModel);
		}
		nhchartbo.setDeviceNetFlowChartDataModel(charList);
		return nhchartbo;
	}

	@Override
	public DeviceNetFlowPageBo findIPGsByDevice(DeviceConditionBo dcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(dcBo.getStarttime(),
				dcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(dcBo.getStarttime(), dcBo.getEndtime())/1000;
		dcBo.setTableSubfixTime(timePart);
		dcBo.setTimepart(timePeriod + "");
		dcBo.timeFomratChange();
		String showPagination = dcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = dcBo.getRecordCount();
		} else {
			rowCount = dcBo.getRowCount();
		}
		Page<DeviceNetFlowBo, DeviceConditionBo> page = new Page<DeviceNetFlowBo, DeviceConditionBo>(
				dcBo.getStartRow(), rowCount, dcBo);
		Whole whole = deviceDao.getAllFindIPGsFlows(dcBo);
		DeviceNetFlowPageBo deviceNetFlowPageBo = new DeviceNetFlowPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			dcBo.setAllipgsFlows(whole.getWholeFlows() + "");
			dcBo.setWholeFlows(whole.getWholeFlows() + "");
			dcBo.setWholePackets(whole.getWholePackets() + "");
			dcBo.setWholeConnects(whole.getWholeConnects() + "");
			deviceDao.findIPGsByDevice(page);
			deviceNetFlowPageBo.setStartRow(page.getStartRow());
			deviceNetFlowPageBo.setRowCount(page.getRowCount());
			deviceNetFlowPageBo.setTotalRecord(page.getTotalRecord());
			deviceNetFlowPageBo.setRows(page.getDatas());
			deviceNetFlowPageBo.setSort(dcBo.getSort());
		}
		return deviceNetFlowPageBo;
	}

	@Override
	public DeviceNetFlowChartDataBo ipgChartonCloumByDevice(
			DeviceConditionBo dcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				dcBo.getStarttime(), dcBo.getEndtime(),
				dcBo.getTableSubfixTime());
		List<DeviceNetFlowChartDataModel> charList = new ArrayList<DeviceNetFlowChartDataModel>();
		DeviceNetFlowChartDataBo tchartbo = new DeviceNetFlowChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		dcBo.setTimePoints(timePoints);
		for (String ipgips : dcBo.getIpgsIp()) {
			dcBo.setCurrentipgIP(ipgips);
			List<DeviceNetFlowBo> bo = deviceDao.ipgsCharOnCloumByDevice(dcBo);
			List<Double> record = new ArrayList<Double>();
			DeviceNetFlowChartDataModel tchartdataModel = new DeviceNetFlowChartDataModel();
			String ipgid = dcBo.getCurrentipgIP();
			String name = deviceDao.getIPGNamebyID(ipgid);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.DeviceNetFlowChartResultFilter(dcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setDeviceNetFlowChartDataModel(charList);
		return tchartbo;
	}

	public IDeviceNetFlowDao getDeviceDao() {
		return deviceDao;
	}

	public void setDeviceDao(IDeviceNetFlowDao deviceDao) {
		this.deviceDao = deviceDao;
	}

}
