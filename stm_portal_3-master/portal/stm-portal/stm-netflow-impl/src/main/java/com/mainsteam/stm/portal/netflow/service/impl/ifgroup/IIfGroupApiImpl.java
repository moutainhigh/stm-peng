/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl.ifgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.ifgroup.IIfGroupApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.ifgroup.IIfGroupDao;

/**
 * <li>文件名称: IIfGroupApiImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public class IIfGroupApiImpl implements IIfGroupApi {
	
	private IIfGroupDao ifGroupDao;
	
	/**
	 * @return the ifGroupDao
	 */
	public IIfGroupDao getIfGroupDao() {
		return ifGroupDao;
	}

	/**
	 * @param ifGroupDao the ifGroupDao to set
	 */
	public void setIfGroupDao(IIfGroupDao ifGroupDao) {
		this.ifGroupDao = ifGroupDao;
	}

	@Override
	public NetflowPageBo ifGroupPageSelect(NetflowParamBo bo) {
		
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

		NetflowPageBo pageBo = new NetflowPageBo();

		//接口组接口ids
		List<OptionBo> srcIds = this.ifGroupDao.getIfGroupIfIds();
		List<String> idsList = new ArrayList<String>();
		if(null != srcIds && !srcIds.isEmpty()) {
			for(OptionBo b : srcIds) {
				String idsStr = b.getIds();
				if(null != idsStr && idsStr.length() > 0) {
					String[] idsArray = idsStr.split(",");
					for(String id : idsArray) {
						if(!idsList.contains(id)) {
							idsList.add(id);
						}
					}
				}
			}
		}
		
		if(!idsList.isEmpty()) {
			//凭借id，“，”间隔
			StringBuilder sb = new StringBuilder();
			for(String id : idsList) {
				sb.append(id).append(",");
			}
			if(sb.length()>0) {
				sb = sb.deleteCharAt(sb.length() - 1);
				bo.setIfId(sb.toString());
				
				//获取某一设备的应用总流量
//				long wholeFlows = ifGroupDao.getTotalIfGroupNetflows(bo);
				Whole w = ifGroupDao.getIfGroupTotals(bo);
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
				ifGroupDao.ifGroupPageSelect(page);
				
				//获取查询结果
				pageBo.setRowCount(page.getRowCount());
				pageBo.setStartRow(page.getStartRow());
				pageBo.setTotalRecord(page.getTotalRecord());
				pageBo.setRows(page.getDatas());
			}
		}
		
		return pageBo;
	}

	@Override
	public NetflowCharWrapper getIfGroupChartData(NetflowParamBo bo) {
		//查询图表需要的数据，包括：1.时间点列表，2，设备在每个时间点对应的流量数据
		//根据起止时间决定查询哪张表
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);
		
		
		//根据起止时间，得到时间段内的时间点
		//List<String> timeLine = TimeUtil.getTimeHm(bo.getStartTime(), bo.getEndTime(), tableSuffix);
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
		ifGroupDao.ifGroupPageSelect(page);
		List<NetflowBo> datas = page.getDatas();
		
		//根据设备列表查询查询每太设备在每个时间点的总流量，通过设备名分组
		List<NetflowChartBo> retList = new ArrayList<NetflowChartBo>();
		if(null != datas && !datas.isEmpty()) {
			for(NetflowBo e : datas) {
				bo.setIfGroupId(e.getIfGroupId());
				List<NetflowBo> bos = ifGroupDao.getIfGroupChartData(bo);
				List<Double> rows = NetflowUtil.filterSortResult(bo.getSort(), bos, timeLongList.size());
				if(null == bos || bos.isEmpty()) {
					for(int i=0; i<timeLongList.size(); i++) {
						rows.add(0.0);
					}
				}
				String name = NetflowUtil.getName(e.getName());
				retList.add(new NetflowChartBo(name, rows));
			}
		}
		
		Map<String, Object> filterDisplayInfo4HC = NetflowUtil.filterDisplayInfo4HC(bo.getSort());
		
		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setSortColumn((String)filterDisplayInfo4HC.get("sortColumn"));
		ret.setyAxisName((String)filterDisplayInfo4HC.get("yAxisName"));
		ret.setBos(retList);
		ret.setTimeLine(timeLine);

		return ret;
	}
	
	@Override
	public String getIfIdsByGroupId(Long ifGroupId) {
		String ids = this.ifGroupDao.getIfIdsByGroupId(ifGroupId);
		if(null != ids && !"".equals(ids) && 0 != ids.length()) {
			return ids.trim();
		}
		return "";
	}

}
