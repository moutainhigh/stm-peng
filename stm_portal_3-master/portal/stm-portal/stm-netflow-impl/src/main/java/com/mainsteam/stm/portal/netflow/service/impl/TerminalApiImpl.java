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
import com.mainsteam.stm.portal.netflow.api.ITerminalApi;
import com.mainsteam.stm.portal.netflow.bo.TerminalBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalPageBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.ITerminalDao;

/**
 * <li>文件名称: DeviceImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class TerminalApiImpl implements ITerminalApi {

	private ITerminalDao terminalDao;

	public ITerminalDao getTerminalDao() {
		return terminalDao;
	}

	public void setTerminalDao(ITerminalDao terminalDao) {
		this.terminalDao = terminalDao;
	}

	@SuppressWarnings("unused")
	private ISequence seq;

	/**
	 * 取出某个设备在当前时间符合要求的终端的流量的信心
	 */

	@Override
	public TerminalPageBo getAllTerminal(TerminalConditionBo tcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(tcBo.getStarttime(),
				tcBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(tcBo.getStarttime(),tcBo.getEndtime())/1000;
		tcBo.setTableSubfixTime(timePart);
		tcBo.setTimepart(timePeriod + "");
		tcBo.timeFomratChange();
		String showPagination = tcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = tcBo.getRecordCount();
		} else {
			rowCount = tcBo.getRowCount();
		}
		Page<TerminalBo, TerminalConditionBo> page = new Page<TerminalBo, TerminalConditionBo>(
				tcBo.getStartRow(), rowCount, tcBo);
		Whole whole = terminalDao.getAllTerminalsFlows(tcBo);
		TerminalPageBo terminalPageBo = new TerminalPageBo();

		if (whole != null && whole.getWholeFlows() != null
				&& !"".equals(whole.getWholeFlows())) {
			tcBo.setAllterminalsFlows(whole.getWholeFlows() + "");
			tcBo.setWholeFlows(whole.getWholeFlows());
			tcBo.setWholePackets(whole.getWholePackets());
			tcBo.setWholeConnects(whole.getWholeConnects());
			terminalDao.getAllTerminals(page);
			terminalPageBo.setStartRow(page.getStartRow());
			terminalPageBo.setRowCount(page.getRowCount());
			terminalPageBo.setTotalRecord(page.getTotalRecord());
			terminalPageBo.setRows(page.getDatas());
			terminalPageBo.setSort(tcBo.getSort());
		}
		return terminalPageBo;
	}

	@Override
	public TerminalChartDataBo getTerminalChartData(TerminalConditionBo tcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				tcBo.getStarttime(), tcBo.getEndtime(),
				tcBo.getTableSubfixTime());
		List<TerminalChartDataModel> charList = new ArrayList<TerminalChartDataModel>();
		TerminalChartDataBo tchartbo = new TerminalChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		tcBo.setTimePoints(timePoints);
		for (String terminalips : tcBo.getTerminalsIp()) {
			tcBo.setCurrentTerminals(terminalips);
			List<TerminalBo> bo = terminalDao.getTerminalsChartData(tcBo);
			List<Double> record = new ArrayList<Double>();
			TerminalChartDataModel tchartdataModel = new TerminalChartDataModel();

			tchartdataModel.setName(tcBo.getCurrentTerminals());
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.TerminalChartResultFilter(tcBo, bo, record,
					tchartbo);

			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setTerminalChartDataModel(charList);
		return tchartbo;
	}

	public TerminalPageBo getAppByTerminal(TerminalConditionBo tcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(tcBo.getStarttime(),
				tcBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(tcBo.getStarttime(),tcBo.getEndtime())/1000;
		tcBo.setTableSubfixTime(timePart);
		tcBo.setTimepart(timePeriod + "");
		tcBo.timeFomratChange();
		String showPagination = tcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = tcBo.getRecordCount();
		} else {
			rowCount = tcBo.getRowCount();
		}
		Page<TerminalBo, TerminalConditionBo> page = new Page<TerminalBo, TerminalConditionBo>(
				tcBo.getStartRow(), rowCount, tcBo);
		Whole whole = terminalDao.getAppFlowsByTerminal(tcBo);
		TerminalPageBo terminalPageBo = new TerminalPageBo();

		if (whole != null && whole.getWholeFlows() != null
				&& !"".equals(whole.getWholeFlows())) {
			tcBo.setAllapplicationflows(whole.getWholeFlows() + "");
			tcBo.setWholeFlows(whole.getWholeFlows());
			tcBo.setWholePackets(whole.getWholePackets());
			tcBo.setWholeConnects(whole.getWholeConnects());
			terminalDao.getAppByTerminal(page);
			terminalPageBo.setStartRow(page.getStartRow());
			terminalPageBo.setRowCount(page.getRowCount());
			terminalPageBo.setTotalRecord(page.getTotalRecord());
			terminalPageBo.setRows(page.getDatas());
			terminalPageBo.setSort(tcBo.getSort());
		}
		return terminalPageBo;
	}

	@Override
	public TerminalChartDataBo getAppChartDataByTerminal(
			TerminalConditionBo tcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				tcBo.getStarttime(), tcBo.getEndtime(),
				tcBo.getTableSubfixTime());
		List<TerminalChartDataModel> charList = new ArrayList<TerminalChartDataModel>();
		TerminalChartDataBo tchartbo = new TerminalChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		tcBo.setTimePoints(timePoints);
		for (String appips : tcBo.getApplicationsIp()) {
			tcBo.setCurrentapplication(appips);
			List<TerminalBo> bo = terminalDao.getAppChartDataByTerminal(tcBo);
			List<Double> record = new ArrayList<Double>();
			TerminalChartDataModel tchartdataModel = new TerminalChartDataModel();
			String appid = tcBo.getCurrentapplication();
			String name = terminalDao.getAppNameByIDByTerminal(appid);
			tchartdataModel.setName(name);

			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}
			record = NetflowUtil.TerminalChartResultFilter(tcBo, bo, record,
					tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setTerminalChartDataModel(charList);
		return tchartbo;
	}

	@Override
	public TerminalPageBo getSessionsByTerminal(TerminalConditionBo tcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(tcBo.getStarttime(),
				tcBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(tcBo.getStarttime(),tcBo.getEndtime())/1000;
		tcBo.setTableSubfixTime(timePart);
		tcBo.setTimepart(timePeriod + "");
		tcBo.timeFomratChange();
		String showPagination = tcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = tcBo.getRecordCount();
		} else {
			rowCount = tcBo.getRowCount();
		}
		Page<TerminalBo, TerminalConditionBo> page = new Page<TerminalBo, TerminalConditionBo>(
				tcBo.getStartRow(), rowCount, tcBo);
		Whole whole = terminalDao.getAllSessionsFlowsByTerminal(tcBo);
		tcBo.setAllsessionFlows(whole.getWholeFlows() + "");
		tcBo.setWholeFlows(whole.getWholeFlows());
		tcBo.setWholePackets(whole.getWholePackets());
		tcBo.setWholeConnects(whole.getWholeConnects());
		TerminalPageBo terminalPageBo = new TerminalPageBo();
		if (whole != null && !"".equals(whole.getWholeFlows())) {
			terminalDao.getSessionsByTerminal(page);
			terminalPageBo.setStartRow(page.getStartRow());
			terminalPageBo.setRowCount(page.getRowCount());
			terminalPageBo.setTotalRecord(page.getTotalRecord());
			terminalPageBo.setRows(page.getDatas());
			terminalPageBo.setSort(tcBo.getSort());
		}
		return terminalPageBo;
	}

	@Override
	public TerminalChartDataBo getSessionChartonDataByTerminal(
			TerminalConditionBo tcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				tcBo.getStarttime(), tcBo.getEndtime(),
				tcBo.getTableSubfixTime());
		List<TerminalChartDataModel> charList = new ArrayList<TerminalChartDataModel>();
		TerminalChartDataBo tchartbo = new TerminalChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		tcBo.setTimePoints(timePoints);
		List<Map<String, String>> currentSession = tcBo.getSessionips();
		for (Map<String, String> current : currentSession) {
			Set<String> srcset = current.keySet();
			Iterator<String> it = srcset.iterator();
			if (it.hasNext()) {
				String currentSrcIp = (String) it.next();
				String currentDstIp = current.get(currentSrcIp);
				tcBo.setCurrentSrcIp(currentSrcIp);
				tcBo.setCurrentDstIp(currentDstIp);
				List<TerminalBo> bo = terminalDao
						.getSessionsCharDataByTerminal(tcBo);
				List<Double> record = new ArrayList<Double>();
				TerminalChartDataModel tchartdataModel = new TerminalChartDataModel();
				tchartdataModel.setName(tcBo.getCurrentSrcIp() + "-"
						+ tcBo.getCurrentDstIp());

				for (int i = 0; i < timePoints.size(); i++) {
					record.add(new Double(0).doubleValue());
				}

				record = NetflowUtil.TerminalChartResultFilter(tcBo, bo,
						record, tchartbo);
				tchartdataModel.setData(record);
				charList.add(tchartdataModel);
			}
		}
		tchartbo.setTerminalChartDataModel(charList);
		return tchartbo;
	}

	public TerminalPageBo getProtocolByTerminal(TerminalConditionBo tcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(tcBo.getStarttime(),
				tcBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(tcBo.getStarttime(),tcBo.getEndtime())/1000;
		tcBo.setTableSubfixTime(timePart);
		tcBo.setTimepart(timePeriod + "");
		tcBo.timeFomratChange();
		String showPagination = tcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = tcBo.getRecordCount();
		} else {
			rowCount = tcBo.getRowCount();
		}
		Page<TerminalBo, TerminalConditionBo> page = new Page<TerminalBo, TerminalConditionBo>(
				tcBo.getStartRow(), rowCount, tcBo);
		Whole whole = terminalDao.getAllProtocolFlowsByTerminal(tcBo);
		TerminalPageBo terminalPageBo = new TerminalPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			tcBo.setAllprotoflows(whole.getWholeFlows() + "");
			tcBo.setWholeFlows(whole.getWholeFlows());
			tcBo.setWholePackets(whole.getWholePackets());
			tcBo.setWholeConnects(whole.getWholeConnects());
			terminalDao.getProtocolByTerminal(page);
			terminalPageBo.setStartRow(page.getStartRow());
			terminalPageBo.setRowCount(page.getRowCount());
			terminalPageBo.setTotalRecord(page.getTotalRecord());
			terminalPageBo.setRows(page.getDatas());
			terminalPageBo.setSort(tcBo.getSort());
		}
		return terminalPageBo;
	}

	@Override
	public TerminalChartDataBo getProtocolChartDataByTerminal(
			TerminalConditionBo tcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				tcBo.getStarttime(), tcBo.getEndtime(),
				tcBo.getTableSubfixTime());
		List<TerminalChartDataModel> charList = new ArrayList<TerminalChartDataModel>();
		TerminalChartDataBo tchartbo = new TerminalChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		tcBo.setTimePoints(timePoints);
		for (String proto : tcBo.getProtosIp()) {
			tcBo.setCurrentproto(proto);
			List<TerminalBo> bo = terminalDao
					.getProtocolChartDataByTerminal(tcBo);
			List<Double> record = new ArrayList<Double>();
			TerminalChartDataModel tchartdataModel = new TerminalChartDataModel();
			String name = terminalDao.getProtocolNameByIDByTerminal(proto);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.TerminalChartResultFilter(tcBo, bo, record,
					tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setTerminalChartDataModel(charList);
		return tchartbo;
	}

	public TerminalPageBo getTosByTerminal(TerminalConditionBo tcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(tcBo.getStarttime(),
				tcBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(tcBo.getStarttime(),tcBo.getEndtime())/1000;
		tcBo.setTableSubfixTime(timePart);
		tcBo.setTimepart(timePeriod + "");
		tcBo.timeFomratChange();
		String showPagination = tcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = tcBo.getRecordCount();
		} else {
			rowCount = tcBo.getRowCount();
		}
		Page<TerminalBo, TerminalConditionBo> page = new Page<TerminalBo, TerminalConditionBo>(
				tcBo.getStartRow(), rowCount, tcBo);
		Whole whole = terminalDao.getAllTosFlowsByTerminal(tcBo);
		TerminalPageBo terminalPageBo = new TerminalPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			tcBo.setAllprotoflows(whole.getWholeFlows() + "");
			tcBo.setWholeFlows(whole.getWholeFlows());
			tcBo.setWholePackets(whole.getWholePackets());
			tcBo.setWholeConnects(whole.getWholeConnects());
			terminalDao.getTosByTerminal(page);
			terminalPageBo.setStartRow(page.getStartRow());
			terminalPageBo.setRowCount(page.getRowCount());
			terminalPageBo.setTotalRecord(page.getTotalRecord());
			terminalPageBo.setRows(page.getDatas());
			terminalPageBo.setSort(tcBo.getSort());
		}
		return terminalPageBo;
	}

	@Override
	public TerminalChartDataBo getTosChartDataByTerminal(
			TerminalConditionBo tcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				tcBo.getStarttime(), tcBo.getEndtime(),
				tcBo.getTableSubfixTime());
		List<TerminalChartDataModel> charList = new ArrayList<TerminalChartDataModel>();
		TerminalChartDataBo tchartbo = new TerminalChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		tcBo.setTimePoints(timePoints);
		for (String tos : tcBo.getTosids()) {
			tcBo.setCurrentproto(tos);
			List<TerminalBo> bo = terminalDao.getTosChartDataByTerminal(tcBo);
			List<Double> record = new ArrayList<Double>();
			TerminalChartDataModel tchartdataModel = new TerminalChartDataModel();
			String name = terminalDao.getTosNameByIDByTerminal(tos);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.TerminalChartResultFilter(tcBo, bo, record,
					tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setTerminalChartDataModel(charList);
		return tchartbo;
	}

}
