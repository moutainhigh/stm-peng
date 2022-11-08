/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.inf.IIfNextHopApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfNetflowDao;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfNextHopDao;

/**
 * <li>文件名称: IIfNextHopApiImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfNextHopApiImpl implements IIfNextHopApi {
	
	private IIfNextHopDao ifNextHopDao;
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
	 * @return the ifNextHopDao
	 */
	public IIfNextHopDao getIfNextHopDao() {
		return ifNextHopDao;
	}

	/**
	 * @param ifNextHopDao the ifNextHopDao to set
	 */
	public void setIfNextHopDao(IIfNextHopDao ifNextHopDao) {
		this.ifNextHopDao = ifNextHopDao;
	}

	@Override
	public NetflowPageBo ifNextHopPageSelect(NetflowParamBo bo) {
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
//		long wholeFlows = ifNextHopDao.getTotalIfNextHopNetflows(bo);
		Whole w = ifNextHopDao.getIfNextHopTotal(bo);
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
		ifNextHopDao.ifNextHopPageSelect(page);
		
		//获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		pageBo.setRows(page.getDatas());
		
		return pageBo;
	}

	@Override
	public NetflowCharWrapper getIfNextHopChartData(NetflowParamBo bo) {
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		//根据时间获得要查询的表的时间部分
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);
		
		//根据起止时间，得到时间段内的时间点
		List<String> timeLine = TimeUtil.getTimeHm(bo.getStartTime(), bo.getEndTime(), tableSuffix);
		List<Long> timeLongList = TimeUtil.getTimePointDevide1000(TimeUtil.getTimePoint(startTime, endTime, tableSuffix));
		String timeParam = TimeUtil.getTimeString(timeLongList);

		bo.setTimeParam(timeParam);
		
		//得到排序后的设备列表
		long rowCount = bo.getRowCount();
		if(!bo.isNeedPagination()) {
			rowCount = bo.getQuerySize();
		}
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), rowCount, bo);
		ifNextHopDao.ifNextHopPageSelect(page);
		List<NetflowBo> datas = page.getDatas();
		
		List<NetflowChartBo> list = new ArrayList<NetflowChartBo>();
		if(null != datas && !datas.isEmpty()) {
			for(NetflowBo e : datas) {
				bo.setNextHop(e.getNextHop());
				List<NetflowBo> bos = ifNextHopDao.getIfNextHopChartData(bo);
				List<Double> data = NetflowUtil.filterSortResult(bo.getSort(), bos, timeLongList.size());
				if(null == data || data.isEmpty()) {
					for(int i=0; i<timeLongList.size(); i++) {
						data.add(0.0);
					}
				}
				list.add(new NetflowChartBo(e.getNextHop(), data));
			}
		}
		
		Map<String, Object> m = NetflowUtil.filterDisplayInfo4HC(bo.getSort());
		
		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setUnit((String)m.get("sortColumn"));
		ret.setBos(list);
		ret.setTimeLine(timeLine);
		ret.setSortColumn((String)m.get("sortColumn"));
		ret.setyAxisName((String)m.get("yAxisName"));
		
		return ret;
	}

}
