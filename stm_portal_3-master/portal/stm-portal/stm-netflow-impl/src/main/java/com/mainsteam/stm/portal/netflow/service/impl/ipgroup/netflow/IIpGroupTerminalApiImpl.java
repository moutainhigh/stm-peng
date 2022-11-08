/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl.ipgroup.netflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.ipgroup.netflow.IIpGroupTerminalApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.ipgroup.netflow.IIpGroupTerminalDao;

/**
 * <li>文件名称: IIpGroupTerminalApiImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口流量统计</li> <li>
 * 其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月23日
 * @author lil
 */
public class IIpGroupTerminalApiImpl implements IIpGroupTerminalApi {

	private IIpGroupTerminalDao ipGroupTerminalDao;

	/**
	 * @return the ipGroupTerminalDao
	 */
	public IIpGroupTerminalDao getIpGroupTerminalDao() {
		return ipGroupTerminalDao;
	}

	/**
	 * @param ipGroupTerminalDao
	 *            the ipGroupTerminalDao to set
	 */
	public void setIpGroupTerminalDao(IIpGroupTerminalDao ipGroupTerminalDao) {
		this.ipGroupTerminalDao = ipGroupTerminalDao;
	}

	@Override
	public NetflowPageBo ipGroupTerminalPageSelect(NetflowParamBo bo) {
		long[] seTime = TimeUtil.getQueryDate(bo.getStartTime(),
				bo.getEndTime());

		// 根据时间获得要查询的表的时间部分
		String tableSuffix = TimeUtil.getTableSuffix(bo.getStartTime(),
				bo.getEndTime());
		String timeInterval = String.valueOf(TimeUtil.getReduceTime(bo.getStartTime(), bo.getEndTime())/1000);

		// 构建查询的条件
		bo.setStime(String.valueOf(seTime[0]));
		bo.setEtime(String.valueOf(seTime[1]));
		bo.setTableSuffix(tableSuffix);
		bo.setTimeInterval(timeInterval);

		// 获取某一设备的应用总流量
		Whole whole = ipGroupTerminalDao.getTotalIpGroupTerminalNetflows(bo);
		bo.setWholeFlows(String.valueOf(whole.getWholeFlows()));
		bo.setWholePackets(whole.getWholePackets() + "");
		bo.setWholeConnects(whole.getWholeConnects() + "");

		// 构建分页查询对象
		long rowCount = bo.getRowCount();
		if (!bo.isNeedPagination()) {
			rowCount = bo.getQuerySize();
		}
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(
				bo.getStartRow(), rowCount, bo);

		// 执行流量查询
		ipGroupTerminalDao.ipGroupTerminalPageSelect(page);

		// 获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		pageBo.setRows(page.getDatas());

		return pageBo;
	}

	@Override
	public NetflowCharWrapper getIpGroupTerminalChartData(NetflowParamBo bo) {
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);

		// 根据起止时间，得到时间段内的时间点
		List<Long> timePoints = TimeUtil.getTimePoint(startTime, endTime,
				tableSuffix);
		List<String> timeLine = TimeUtil.getTimeLineList(timePoints,
				tableSuffix);
		List<Long> timeLongList = TimeUtil.getTimePointDevide1000(TimeUtil
				.getTimePoint(startTime, endTime, tableSuffix));
		String timeParam = TimeUtil.getTimeString(timeLongList);

		bo.setTimeParam(timeParam);

		// 得到排序后的设备列表
		long rowCount = bo.getRowCount();
		if (!bo.isNeedPagination()) {
			rowCount = bo.getQuerySize();
		}
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(
				bo.getStartRow(), rowCount, bo);
		ipGroupTerminalDao.ipGroupTerminalPageSelect(page);
		List<NetflowBo> datas = page.getDatas();

		// 根据设备列表查询查询每太设备在每个时间点的总流量，通过设备名分组
		List<NetflowChartBo> retList = new ArrayList<NetflowChartBo>();
		if (null != datas && !datas.isEmpty()) {
			for (NetflowBo e : datas) {
				bo.setTerminalIp(e.getTerminalIp());
				List<NetflowBo> bos = ipGroupTerminalDao
						.getIpGroupTerminalChartData(bo);
				List<Double> rows = NetflowUtil.filterSortResult(bo.getSort(),
						bos, timeLongList.size());
				if (null == bos || bos.isEmpty()) {
					for (int i = 0; i < timeLongList.size(); i++) {
						rows.add(0.0);
					}
				}
				retList.add(new NetflowChartBo(e.getTerminalIp(), rows));
			}
		}

		Map<String, Object> filterDisplayInfo4HC = NetflowUtil
				.filterDisplayInfo4HC(bo.getSort());

		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setUnit((String) filterDisplayInfo4HC.get("sortColumn"));
		ret.setSortColumn((String) filterDisplayInfo4HC.get("sortColumn"));
		ret.setyAxisName((String) filterDisplayInfo4HC.get("yAxisName"));
		ret.setBos(retList);
		ret.setTimeLine(timeLine);

		return ret;
	}

}
