/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.inf.IIfProtoApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfNetflowDao;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfProtoDao;

/**
 * <li>文件名称: IIfProtoApiImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfProtoApiImpl implements IIfProtoApi {
	
	private IIfProtoDao ifProtoDao;
	private IIfNetflowDao ifNetflowDao;
	
	/**
	 * @return the ifNetflowDao
	 */
	public IIfNetflowDao getIfNetflowDao() {
		return ifNetflowDao;
	}

	/**
	 * @param ifNetflowDao the ifNetflowDao to set
	 */
	public void setIfNetflowDao(IIfNetflowDao ifNetflowDao) {
		this.ifNetflowDao = ifNetflowDao;
	}

	/**
	 * @return the ifProtoDao
	 */
	public IIfProtoDao getIfProtoDao() {
		return ifProtoDao;
	}

	/**
	 * @param ifProtoDao the ifProtoDao to set
	 */
	public void setIfProtoDao(IIfProtoDao ifProtoDao) {
		this.ifProtoDao = ifProtoDao;
	}

	@Override
	public NetflowPageBo ifProtoPageSelect(NetflowParamBo bo) {
		long[] seTime = TimeUtil.getQueryDate(bo.getStartTime(), bo.getEndTime());
		
		//根据时间获得要查询的表的时间部分
		String tableSuffix = TimeUtil.getTableSuffix(bo.getStartTime(), bo.getEndTime());
		//String timeInterval = String.valueOf(TimeUtil.getTimeByNames(tableSuffix.toUpperCase()));
		long timeInterval = TimeUtil.getReduceTime(bo.getStartTime(), bo.getEndTime())/1000;
		//构建查询的条件
		bo.setStime(String.valueOf(seTime[0]));
		bo.setEtime(String.valueOf(seTime[1]));
		bo.setTableSuffix(tableSuffix);
		bo.setTimeInterval(String.valueOf(timeInterval));

		//如果是接口组，根据接口组ID查询所有接口ID
//		if(null != bo.getIfGroupId() && !"".equals(bo.getIfGroupId())) {
//			List<Long> ifList = ifNetflowDao.getIfIdsByIfGroupId(Long.parseLong(bo.getIfGroupId()));
//			String ifIds = NetflowUtil.getIfIdsString(ifList);
//			if(null != ifIds && !"".equals(ifIds) && 0 != ifIds.length()) {
//				bo.setIfId(ifIds.trim());
//			}
//		}
		
		//获取某一设备的应用总流量
//		long wholeFlows = ifProtoDao.getTotalIfProtoNetflows(bo);
		Whole w = ifProtoDao.getIfProtoTotals(bo);
		long wholeFlows = w.getWholeFlows();
		long wholePackets = w.getWholePackets();
		long wholeConnects = w.getWholeConnects();
		bo.setWholeFlows(String.valueOf(wholeFlows));
		bo.setWholePackets(String.valueOf(wholePackets));
		bo.setWholeConnects(String.valueOf(wholeConnects));
		
		//构建分页查询对象
		long rowCount = bo.getRowCount();
		if(!bo.isNeedPagination()) {
			rowCount = bo.getQuerySize();
		}
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), rowCount, bo);
		
		//执行流量查询
		ifProtoDao.ifProtoPageSelect(page);
		
		//获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		pageBo.setRows(page.getDatas());
		
		return pageBo;
	}

	@Override
	public NetflowCharWrapper getIfProtoChartData(NetflowParamBo bo) {
		//查询图表需要的数据，包括：1.时间点列表，2，设备在每个时间点对应的流量数据
		//根据起止时间决定查询哪张表
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);
		
		//根据起止时间，得到时间段内的时间点
		List<Long> timePoints = TimeUtil.getTimePoint(startTime, endTime, tableSuffix);
		List<String> timeLine = TimeUtil.getTimeLineList(timePoints, tableSuffix);
		List<Long> timeLongList = TimeUtil.getTimePointDevide1000(TimeUtil.getTimePoint(startTime, endTime, tableSuffix));
		String timeParam = TimeUtil.getTimeString(timeLongList);
		
		bo.setTimeParam(timeParam);
		
		//得到排序后的设备列表
		long rowCount = bo.getRowCount();
		if(!bo.isNeedPagination()) {
			rowCount = bo.getQuerySize();
		}
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), rowCount, bo);
		ifProtoDao.ifProtoPageSelect(page);
		List<NetflowBo> datas = page.getDatas();
		
		//根据设备列表查询查询每太设备在每个时间点的总流量，通过设备名分组
		List<NetflowChartBo> retList = new ArrayList<NetflowChartBo>();
		if(null != datas && !datas.isEmpty()) {
			for(NetflowBo e : datas) {
				bo.setProtoId(e.getProtoId());
				List<NetflowBo> bos = ifProtoDao.getIfProtoChartData(bo);
				List<Double> rows = NetflowUtil.filterSortResult(bo.getSort(), bos, timeLongList.size());
				if(null == bos || bos.isEmpty()) {
					for(int i=0; i<timeLongList.size(); i++) {
						rows.add(0.0);
					}
				}
				retList.add(new NetflowChartBo(e.getName(), rows));
			}
		}
		
		Map<String, Object> filterDisplayInfo4HC = NetflowUtil.filterDisplayInfo4HC(bo.getSort());
		
		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setUnit((String)filterDisplayInfo4HC.get("sortColumn"));
		ret.setSortColumn((String)filterDisplayInfo4HC.get("sortColumn"));
		ret.setyAxisName((String)filterDisplayInfo4HC.get("yAxisName"));
		ret.setBos(retList);
		ret.setTimeLine(timeLine);

		return ret;
	}

}
