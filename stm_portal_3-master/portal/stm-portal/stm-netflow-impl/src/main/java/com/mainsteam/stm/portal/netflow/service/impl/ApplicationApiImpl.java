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
import com.mainsteam.stm.portal.netflow.api.IApplicationApi;
import com.mainsteam.stm.portal.netflow.bo.ApplicationModelBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationPageBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.IApplicationDao;

/**
 * <li>文件名称: DeviceImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class ApplicationApiImpl implements IApplicationApi {

	private IApplicationDao appDao;

	public IApplicationDao getAppDao() {
		return appDao;
	}

	public void setAppDao(IApplicationDao appDao) {
		this.appDao = appDao;
	}

	private ISequence seq;

	/**
	 * 取出某个设备在当前时间符合要求的终端的流量的信心
	 */

	public ApplicationPageBo getAllApplication(ApplicationConditionBo apcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(apcBo.getStarttime(),
				apcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(apcBo.getStarttime(), apcBo.getEndtime())/1000;
		apcBo.setTableSubfixTime(timePart);
		apcBo.setTimepart(timePeriod + "");
		apcBo.timeFomratChange();
		String showPagination = apcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = apcBo.getRecordCount();
		} else {
			rowCount = apcBo.getRowCount();
		}
		Page<ApplicationModelBo, ApplicationConditionBo> page = new Page<ApplicationModelBo, ApplicationConditionBo>(
				apcBo.getStartRow(), rowCount, apcBo);
		Whole whole = appDao.getAllApplicationFlows(apcBo);
		ApplicationPageBo applicationPageBo = new ApplicationPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			apcBo.setAllapplicationflows(whole.getWholeFlows() + "");
			apcBo.setWholeFlows(whole.getWholeFlows());
			apcBo.setWholePackets(whole.getWholePackets());
			apcBo.setWholeConnects(whole.getWholeConnects());
			appDao.getAllApplications(page);
			applicationPageBo.setStartRow(page.getStartRow());
			applicationPageBo.setRowCount(page.getRowCount());
			applicationPageBo.setTotalRecord(page.getTotalRecord());
			applicationPageBo.setRows(page.getDatas());
			applicationPageBo.setSort(apcBo.getSort());
		}
		return applicationPageBo;
	}

	@Override
	public ApplicationChartDataBo getApplicationChartData(
			ApplicationConditionBo apcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				apcBo.getStarttime(), apcBo.getEndtime(),
				apcBo.getTableSubfixTime());
		List<ApplicationChartDataModel> charList = new ArrayList<ApplicationChartDataModel>();
		ApplicationChartDataBo tchartbo = new ApplicationChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		apcBo.setTimePoints(timePoints);
		for (String appips : apcBo.getApplicationsIp()) {
			apcBo.setCurrentapplication(appips);
			List<ApplicationModelBo> bo = appDao.getApplicationChartData(apcBo);
			List<Double> record = new ArrayList<Double>();
			ApplicationChartDataModel tchartdataModel = new ApplicationChartDataModel();
			String appid = apcBo.getCurrentapplication();
			String name = appDao.getApplicationNameByID(appid);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.ApplicationChartResultFilter(apcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setApplicationChartDataModel(charList);
		return tchartbo;
	}

	public ApplicationPageBo getProtocolByApp(ApplicationConditionBo apcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(apcBo.getStarttime(),
				apcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(apcBo.getStarttime(), apcBo.getEndtime())/1000;
		apcBo.setTableSubfixTime(timePart);
		apcBo.setTimepart(timePeriod + "");
		apcBo.timeFomratChange();
		String showPagination = apcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = apcBo.getRecordCount();
		} else {
			rowCount = apcBo.getRowCount();
		}
		Page<ApplicationModelBo, ApplicationConditionBo> page = new Page<ApplicationModelBo, ApplicationConditionBo>(
				apcBo.getStartRow(), rowCount, apcBo);
		Whole whole = appDao.getAllProtocolFlowsByApp(apcBo);
		ApplicationPageBo applicationPageBo = new ApplicationPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			apcBo.setAllprotoflows(whole.getWholeFlows() + "");
			apcBo.setWholeFlows(whole.getWholeFlows());
			apcBo.setWholePackets(whole.getWholePackets());
			apcBo.setWholeConnects(whole.getWholeConnects());
			appDao.getProtocolByApp(page);
			applicationPageBo.setStartRow(page.getStartRow());
			applicationPageBo.setRowCount(page.getRowCount());
			applicationPageBo.setTotalRecord(page.getTotalRecord());
			applicationPageBo.setRows(page.getDatas());
			applicationPageBo.setSort(apcBo.getSort());
		}
		return applicationPageBo;
	}

	@Override
	public ApplicationChartDataBo getProtocolChartDataByApp(
			ApplicationConditionBo apcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				apcBo.getStarttime(), apcBo.getEndtime(),
				apcBo.getTableSubfixTime());
		List<ApplicationChartDataModel> charList = new ArrayList<ApplicationChartDataModel>();
		ApplicationChartDataBo tchartbo = new ApplicationChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		apcBo.setTimePoints(timePoints);
		for (String proto : apcBo.getProtosIp()) {
			apcBo.setCurrentproto(proto);
			List<ApplicationModelBo> bo = appDao
					.getProtocolChartDataByApp(apcBo);
			List<Double> record = new ArrayList<Double>();
			ApplicationChartDataModel tchartdataModel = new ApplicationChartDataModel();
			String name = appDao.getProtocolNameByIDByApp(proto);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.ApplicationChartResultFilter(apcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setApplicationChartDataModel(charList);
		return tchartbo;
	}

	@Override
	public ApplicationPageBo getTerminalsByApp(ApplicationConditionBo apcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(apcBo.getStarttime(),
				apcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(apcBo.getStarttime(), apcBo.getEndtime())/1000;
		apcBo.setTableSubfixTime(timePart);
		apcBo.setTimepart(timePeriod + "");
		apcBo.timeFomratChange();
		String showPagination = apcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = apcBo.getRecordCount();
		} else {
			rowCount = apcBo.getRowCount();
		}
		Page<ApplicationModelBo, ApplicationConditionBo> page = new Page<ApplicationModelBo, ApplicationConditionBo>(
				apcBo.getStartRow(), rowCount, apcBo);
		Whole whole = appDao.getAllTerminalsFlows(apcBo);
		ApplicationPageBo applicationPageBo = new ApplicationPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			apcBo.setAllterminalsFlows(whole.getWholeFlows() + "");
			apcBo.setWholeFlows(whole.getWholeFlows());
			apcBo.setWholePackets(whole.getWholePackets());
			apcBo.setWholeConnects(whole.getWholeConnects());
			appDao.getTerminalsByApp(page);
			applicationPageBo.setStartRow(page.getStartRow());
			applicationPageBo.setRowCount(page.getRowCount());
			applicationPageBo.setTotalRecord(page.getTotalRecord());
			applicationPageBo.setRows(page.getDatas());
			applicationPageBo.setSort(apcBo.getSort());
		}
		return applicationPageBo;
	}

	@Override
	public ApplicationChartDataBo getTerminalChartonDataByApp(
			ApplicationConditionBo apcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				apcBo.getStarttime(), apcBo.getEndtime(),
				apcBo.getTableSubfixTime());
		List<ApplicationChartDataModel> charList = new ArrayList<ApplicationChartDataModel>();
		ApplicationChartDataBo tchartbo = new ApplicationChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		apcBo.setTimePoints(timePoints);
		for (String terminalips : apcBo.getTerminalsIp()) {
			apcBo.setCurrentTerminals(terminalips);
			List<ApplicationModelBo> bo = appDao.getTerminalsChartData(apcBo);
			List<Double> record = new ArrayList<Double>();
			ApplicationChartDataModel tchartdataModel = new ApplicationChartDataModel();
			tchartdataModel.setName(apcBo.getCurrentTerminals());
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.ApplicationChartResultFilter(apcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);

			charList.add(tchartdataModel);
		}
		tchartbo.setApplicationChartDataModel(charList);
		return tchartbo;
	}

	@Override
	public ApplicationPageBo getSessionsByApp(ApplicationConditionBo apcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(apcBo.getStarttime(),
				apcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(apcBo.getStarttime(), apcBo.getEndtime())/1000;
		apcBo.setTableSubfixTime(timePart);
		apcBo.setTimepart(timePeriod + "");
		apcBo.timeFomratChange();
		String showPagination = apcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = apcBo.getRecordCount();
		} else {
			rowCount = apcBo.getRowCount();
		}
		Page<ApplicationModelBo, ApplicationConditionBo> page = new Page<ApplicationModelBo, ApplicationConditionBo>(
				apcBo.getStartRow(), rowCount, apcBo);
		Whole whole = appDao.getAllSessionsFlows(apcBo);
		ApplicationPageBo applicationPageBo = new ApplicationPageBo();
		if (whole != null && !"".equals(whole.getWholeFlows())) {
			apcBo.setAllsessionFlows(whole.getWholeFlows() + "");
			apcBo.setWholeFlows(whole.getWholeFlows());
			apcBo.setWholePackets(whole.getWholePackets());
			apcBo.setWholeConnects(whole.getWholeConnects());
			appDao.getSessionsByApp(page);
			applicationPageBo.setStartRow(page.getStartRow());
			applicationPageBo.setRowCount(page.getRowCount());
			applicationPageBo.setTotalRecord(page.getTotalRecord());
			applicationPageBo.setRows(page.getDatas());
			applicationPageBo.setSort(apcBo.getSort());
		}
		return applicationPageBo;
	}

	@Override
	public ApplicationChartDataBo getSessionChartonDataByApp(
			ApplicationConditionBo apcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				apcBo.getStarttime(), apcBo.getEndtime(),
				apcBo.getTableSubfixTime());
		List<ApplicationChartDataModel> charList = new ArrayList<ApplicationChartDataModel>();
		ApplicationChartDataBo schartbo = new ApplicationChartDataBo();
		schartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		apcBo.setTimePoints(timePoints);
		List<Map<String, String>> currentSession = apcBo.getSessionips();

		for (Map<String, String> current : currentSession) {
			Set<String> srcset = current.keySet();
			Iterator it = srcset.iterator();
			if (it.hasNext()) {
				String currentSrcIp = (String) it.next();
				String currentDstIp = current.get(currentSrcIp);
				apcBo.setCurrentSrcIp(currentSrcIp);
				apcBo.setCurrentDstIp(currentDstIp);
				List<ApplicationModelBo> bo = appDao.getSessionsCharData(apcBo);
				List<Double> record = new ArrayList<Double>();
				ApplicationChartDataModel schartdataModel = new ApplicationChartDataModel();
				schartdataModel.setName(apcBo.getCurrentSrcIp() + "-"
						+ apcBo.getCurrentDstIp());
				for (int i = 0; i < timePoints.size(); i++) {
					record.add(new Double(0).doubleValue());
				}

				record = NetflowUtil.ApplicationChartResultFilter(apcBo, bo,
						record, schartbo);
				schartdataModel.setData(record);
				charList.add(schartdataModel);
			}
		}
		schartbo.setApplicationChartDataModel(charList);
		return schartbo;
	}

	@Override
	public ApplicationPageBo getIPGsByApp(ApplicationConditionBo apcBo) {
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(apcBo.getStarttime(),
				apcBo.getEndtime());
		//long timePeriod = TimeUtil.getTimeByNames(timePart);
		long timePeriod = TimeUtil.getReduceTime(apcBo.getStarttime(), apcBo.getEndtime())/1000;
		apcBo.setTableSubfixTime(timePart);
		apcBo.setTimepart(timePeriod + "");
		apcBo.timeFomratChange();
		String showPagination = apcBo.getShowpagination();
		if (showPagination != null && "false".equals(showPagination)) {
			rowCount = apcBo.getRecordCount();
		} else {
			rowCount = apcBo.getRowCount();
		}
		Page<ApplicationModelBo, ApplicationConditionBo> page = new Page<ApplicationModelBo, ApplicationConditionBo>(
				apcBo.getStartRow(), rowCount, apcBo);
		Whole whole = appDao.getAllIPGsFlowsByApp(apcBo);
		ApplicationPageBo applicationPageBo = new ApplicationPageBo();

		if (whole != null && !"".equals(whole.getWholeFlows())) {
			apcBo.setAllipgsFlows(whole.getWholeFlows() + "");
			apcBo.setWholeFlows(whole.getWholeFlows());
			apcBo.setWholePackets(whole.getWholePackets());
			apcBo.setWholeConnects(whole.getWholeConnects());
			appDao.getIPGsByApp(page);
			List<ApplicationModelBo> datas = page.getDatas();
			for(int loop = 0; loop < datas.size(); loop++){
				ApplicationModelBo bo = datas.get(loop);
				if(bo.getIpgroup_id()==null||"".equals(bo.getIpgroup_id())){
					datas.remove(loop);
					page.setTotalRecord(page.getTotalRecord()-1);
				}
			}
			page.setDatas(datas);
			applicationPageBo.setStartRow(page.getStartRow());
			applicationPageBo.setRowCount(page.getRowCount());
			applicationPageBo.setTotalRecord(page.getTotalRecord());
			applicationPageBo.setRows(page.getDatas());
			applicationPageBo.setSort(apcBo.getSort());
		}
		return applicationPageBo;
	}

	@Override
	public ApplicationChartDataBo getipgChartDataByApp(
			ApplicationConditionBo apcBo) {
		List<Long> timePoints = (List<Long>) TimeUtil.getTimePoint(
				apcBo.getStarttime(), apcBo.getEndtime(),
				apcBo.getTableSubfixTime());
		List<ApplicationChartDataModel> charList = new ArrayList<ApplicationChartDataModel>();
		ApplicationChartDataBo tchartbo = new ApplicationChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		apcBo.setTimePoints(timePoints);
		for (String ipgips : apcBo.getIpgsIp()) {
			apcBo.setCurrentipgIP(ipgips);
			List<ApplicationModelBo> bo = appDao.getIPGsCharDataByApp(apcBo);
			List<Double> record = new ArrayList<Double>();
			ApplicationChartDataModel tchartdataModel = new ApplicationChartDataModel();
			String ipgid = apcBo.getCurrentipgIP();
			String name = appDao.getIPGNamebyID(ipgid);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.ApplicationChartResultFilter(apcBo, bo,
					record, tchartbo);
			tchartdataModel.setData(record);
			charList.add(tchartdataModel);
		}
		tchartbo.setApplicationChartDataModel(charList);
		return tchartbo;
	}

}
