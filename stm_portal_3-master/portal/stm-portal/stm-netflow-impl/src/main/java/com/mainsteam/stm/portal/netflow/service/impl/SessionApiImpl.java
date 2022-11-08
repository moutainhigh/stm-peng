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
import com.mainsteam.stm.portal.netflow.api.ISessionApi;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataModel;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionPageBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.ISessionDao;

/**
 * <li>文件名称: DeviceImpl.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */


public class SessionApiImpl implements ISessionApi{
	
	private ISessionDao sessionDao;
	
	

	public ISessionDao getSessionDao() {
		return sessionDao;
	}

	public void setSessionDao(ISessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	/**
	 * 取出某个设备在当前时间符合要求的终端的流量的信心
	 */
	
	@Override
	public SessionPageBo getAllSession(SessionConditionBo scBo){
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(scBo.getStarttime(), scBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(scBo.getStarttime(),scBo.getEndtime())/1000;
		scBo.setTableSubfixTime(timePart);
		scBo.setTimepart(timePeriod+"");
		scBo.timeFomratChange();
		String showPagination = scBo.getShowpagination();
		if(showPagination!=null&&"false".equals(showPagination)){
			rowCount = scBo.getRecordCount();
		}else{
			rowCount = scBo.getRowCount();
		}
		Page<SessionBo,SessionConditionBo> page = new Page<SessionBo,SessionConditionBo>(scBo.getStartRow(),rowCount,scBo);
		SessionPageBo sessionPageBo = new SessionPageBo();
//		String allSessionsFlows = sessionDao.getAllSessionFlows(scBo);
		Whole w = sessionDao.getSessionTotals(scBo);
		if(null != w) {
			long allSessionsFlows = w.getWholeFlows();
			long allsessionPackets = w.getWholePackets();
			long allsessionConnects = w.getWholeConnects();
			
			scBo.setAllsessionFlows(allSessionsFlows+"");
			scBo.setAllsessionPackets(allsessionPackets + "");
			scBo.setAllsessionConnects(allsessionConnects + "");
			
//		if(allSessionsFlows != null && !"".equals(allSessionsFlows)){
			sessionDao.getAllSessions(page);
			sessionPageBo.setStartRow(page.getStartRow());
			sessionPageBo.setRowCount(page.getRowCount());
			sessionPageBo.setTotalRecord(page.getTotalRecord());
			sessionPageBo.setRows(page.getDatas());
			sessionPageBo.setSort(scBo.getSort());
//		}
		}
		return sessionPageBo;
	}
	
	@Override
	public SessionChartDataBo getSessionChartData(SessionConditionBo scBo){
		List<Long> timePoints = (List<Long>)TimeUtil.getTimePoint(scBo.getStarttime(), scBo.getEndtime(), scBo.getTableSubfixTime());
		List<SessionChartDataModel>  charList = new ArrayList<SessionChartDataModel>();
		SessionChartDataBo schartbo = new SessionChartDataBo();
		schartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		scBo.setTimePoints(timePoints);
		List<Map<String,String>> currentSession = scBo.getSessionips();
		
		for(Map<String,String> current: currentSession){
			Set<String> srcset = current.keySet();
			Iterator it = srcset.iterator();
			if(it.hasNext()){
				String currentSrcIp = (String)it.next();
				String currentDstIp = current.get(currentSrcIp);
				scBo.setCurrentSrcIp(currentSrcIp);
				scBo.setCurrentDstIp(currentDstIp);
				List<SessionBo> bo = sessionDao.getSessionChartData(scBo);
				List<Double> record = new ArrayList<Double>();
				SessionChartDataModel schartdataModel = new SessionChartDataModel();
				String flows = null;
				schartdataModel.setName(scBo.getCurrentSrcIp()+"-"+scBo.getCurrentDstIp());
				for (int i = 0; i < timePoints.size(); i++) {
					record.add(new Double(0).doubleValue());
				}

				record = NetflowUtil.SessionChartResultFilter(scBo, bo, record, schartbo);
				schartdataModel.setData(record);	
				charList.add(schartdataModel);
			}
		}
		schartbo.setSessionChartDataModel(charList);
		return schartbo;
	}


	public SessionPageBo getProtocolBySession(SessionConditionBo scBo){
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(scBo.getStarttime(), scBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(scBo.getStarttime(),scBo.getEndtime())/1000;;
		scBo.setTableSubfixTime(timePart);
		scBo.setTimepart(timePeriod+"");
		scBo.timeFomratChange();
		String showPagination = scBo.getShowpagination();
		if(showPagination!=null&&"false".equals(showPagination)){
			rowCount = scBo.getRecordCount();
		}else{
			rowCount = scBo.getRowCount();
		}
		Page<SessionBo,SessionConditionBo> page = new Page<SessionBo,SessionConditionBo>(scBo.getStartRow(),rowCount,scBo);
//		String allProtocolFlows = sessionDao.getAllProtoFlowsBySession(scBo);
		//added by kevin
		Whole w = sessionDao.getSessionProtoTotals(scBo);
		long allSessionProtoFlows = w.getWholeFlows();
		long allSessionProtoPackets = w.getWholePackets();
		long allSessionProtoConnects = w.getWholeConnects();
		
		SessionPageBo sessionPageBo = new SessionPageBo();
		if(null != w) {
//		if(allProtocolFlows != null && !"".equals(allProtocolFlows)){
			scBo.setAllsessionFlows(allSessionProtoFlows+"");
			scBo.setAllsessionPackets(allSessionProtoPackets+"");
			scBo.setAllsessionConnects(allSessionProtoConnects+"");
			sessionDao.getProtoBySession(page);
			sessionPageBo.setStartRow(page.getStartRow());
			sessionPageBo.setRowCount(page.getRowCount());
			sessionPageBo.setTotalRecord(page.getTotalRecord());
			sessionPageBo.setRows(page.getDatas());
			sessionPageBo.setSort(scBo.getSort());
//		}
		}
		return sessionPageBo;
	}
	
	@Override
	public SessionChartDataBo getProtocolChartDataBySession(SessionConditionBo scBo){
		List<Long> timePoints = (List<Long>)TimeUtil.getTimePoint(scBo.getStarttime(), scBo.getEndtime(), scBo.getTableSubfixTime());
		List<SessionChartDataModel>  charList = new ArrayList<SessionChartDataModel>();
		SessionChartDataBo tchartbo = new SessionChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		scBo.setTimePoints(timePoints);
		for(String proto : scBo.getProtosIp()){
			scBo.setCurrentproto(proto);
			List<SessionBo> bo = sessionDao.getProtoChartDataBySession(scBo);
			List<Double> record = new ArrayList<Double>();
			SessionChartDataModel tchartdataModel = new SessionChartDataModel();
			String flows = null;
			String name = sessionDao.getProtocolNameByIDBySession(proto);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.SessionChartResultFilter(scBo, bo, record, tchartbo);
			tchartdataModel.setData(record);
			
			charList.add(tchartdataModel);
		}
		tchartbo.setSessionChartDataModel(charList);;
		return tchartbo;
	}

	
	
	public SessionPageBo getAppBySession(SessionConditionBo scBo){
		long rowCount = 0;
		String timePart = TimeUtil.getTableSuffix(scBo.getStarttime(), scBo.getEndtime());
		long timePeriod = TimeUtil.getReduceTime(scBo.getStarttime(),scBo.getEndtime())/1000;
		scBo.setTableSubfixTime(timePart);
		scBo.setTimepart(timePeriod+"");
		scBo.timeFomratChange();
		String showPagination = scBo.getShowpagination();
		if(showPagination!=null&&"false".equals(showPagination)){
			rowCount = scBo.getRecordCount();
		}else{
			rowCount = scBo.getRowCount();
		}
		Page<SessionBo,SessionConditionBo> page = new Page<SessionBo,SessionConditionBo>(scBo.getStartRow(),rowCount,scBo);
//		String allApplicationFlows = sessionDao.getAllAppFlowsBySession(scBo);
		Whole w = sessionDao.getSessionAppTotals(scBo);
		long wholeFlows = w.getWholeFlows();
		long wholePackets = w.getWholePackets();
		long wholeConnects = w.getWholeConnects();
		
		SessionPageBo sessionPageBo = new SessionPageBo();
		if(null != w) {
			
//		if(allApplicationFlows != null && !"".equals(allApplicationFlows)){
//			scBo.setAllapplicationflows(allApplicationFlows);
			scBo.setAllsessionFlows(wholeFlows+"");
			scBo.setAllsessionPackets(wholePackets+"");
			scBo.setAllsessionConnects(wholeConnects+"");
			
			sessionDao.getAppBySession(page);
			sessionPageBo.setStartRow(page.getStartRow());
			sessionPageBo.setRowCount(page.getRowCount());
			sessionPageBo.setTotalRecord(page.getTotalRecord());
			sessionPageBo.setRows(page.getDatas());
			sessionPageBo.setSort(scBo.getSort());
//		}
		}
		return sessionPageBo;
	}
	
	@Override
	public SessionChartDataBo getAppChartDataBySession(SessionConditionBo scBo){
		List<Long> timePoints = (List<Long>)TimeUtil.getTimePoint(scBo.getStarttime(), scBo.getEndtime(), scBo.getTableSubfixTime());
		List<SessionChartDataModel>  charList = new ArrayList<SessionChartDataModel>();
		SessionChartDataBo tchartbo = new SessionChartDataBo();
		tchartbo.setTimepoints(TimeUtil.getRealTimePoint(timePoints));
		scBo.setTimePoints(timePoints);
		for(String appips : scBo.getApplicationsIp()){
			scBo.setCurrentapplication(appips);
			List<SessionBo> bo = sessionDao.getSessionChartData(scBo);
			List<Double> record = new ArrayList<Double>();
			SessionChartDataModel tchartdataModel = new SessionChartDataModel();
			String flows = null;
			String appid = scBo.getCurrentapplication();
			String name = sessionDao.getAppNameByIDBySession(appid);
			tchartdataModel.setName(name);
			for (int i = 0; i < timePoints.size(); i++) {
				record.add(new Double(0).doubleValue());
			}

			record = NetflowUtil.SessionChartResultFilter(scBo, bo, record, tchartbo);
			tchartdataModel.setData(record);
			
			charList.add(tchartdataModel);
		}
		tchartbo.setSessionChartDataModel(charList);
		return tchartbo;
	}
	
}
