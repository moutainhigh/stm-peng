/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.device.IDeviceFlowApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.IDeviceFlowDao;

/**
 * <li>文件名称: IDeviceApiImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月26日
 * @author   lil
 */
public class IDeviceFlowApiImpl implements IDeviceFlowApi {
	
	private IDeviceFlowDao deviceFlowDao;
	
	/**
	 * @return the deviceFlowDao
	 */
	public IDeviceFlowDao getDeviceFlowDao() {
		return deviceFlowDao;
	}

	/**
	 * @param deviceFlowDao the deviceFlowDao to set
	 */
	public void setDeviceFlowDao(IDeviceFlowDao deviceFlowDao) {
		this.deviceFlowDao = deviceFlowDao;
	}

	@Override
	public NetflowPageBo deviceListPageSelect(NetflowParamBo bo) {
		
		long[] seTime = TimeUtil.getQueryDate(bo.getStartTime(), bo.getEndTime());
		//根据时间获得要查询的表的时间部分
		String tableSuffix = TimeUtil.getTableSuffix(bo.getStartTime(), bo.getEndTime());
		String timeInterval = String.valueOf(TimeUtil.getReduceTime(bo.getStartTime(), bo.getEndTime())/1000);
		
		//构建查询的条件
		bo.setStime(String.valueOf(seTime[0]));
		bo.setEtime(String.valueOf(seTime[1]));
		bo.setTableSuffix(tableSuffix);
		bo.setTimeInterval(timeInterval);

		//获取所有设备的总流量，用于计算占比
		Whole whole = deviceFlowDao.findDeviceListTotalFlows(bo);
		bo.setWholeFlows(String.valueOf(whole.getWholeFlows()));
		bo.setWholePackets(String.valueOf(whole.getWholePackets()));
		bo.setWholeConnects(String.valueOf(whole.getWholeConnects()));
		//构建分页查询对象
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), bo.getRowCount(), bo);
		
		//执行流量查询
		deviceFlowDao.deviceListPageSelect(page);
		
		//获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		pageBo.setRows(page.getDatas());
		
		return pageBo;
	}
	
	/**
	 * 得到时间除以1000后的集合，数据库使用该时间
	 * 
	 * @param src
	 * @return
	 * List<Long>    返回类型
	 */
	public List<Long> getTimePoint(List<Long> src) {
		List<Long> dst = new ArrayList<Long>();
		if(null != src && !src.isEmpty()) {
			for(long s : src) {
				dst.add(s / 1000);
			}
		}
		return dst;
	}
	
	private String getTimeString(List<Long> src) {
		StringBuffer sb = new StringBuffer();
		if(null != src && !src.isEmpty()) {
			for(long d : src) {
				sb.append(d).append(",");
			}
			sb = sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	
	/**
	 * 
	 */
	@Override
	public NetflowCharWrapper deviceFlowChartPageSelect(NetflowParamBo bo) {
		
		//查询图表需要的数据，包括：1.时间点列表，2，设备在每个时间点对应的流量数据
		//根据起止时间决定查询哪张表
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);
		
		//根据起止时间，得到时间段内的时间点
		List<Long> timePoints = TimeUtil.getTimePoint(startTime, endTime, tableSuffix);
		List<String> timeLine = TimeUtil.getTimeLineList(timePoints, tableSuffix);
		List<Long> timeLongList = this.getTimePoint(TimeUtil.getTimePoint(startTime, endTime, tableSuffix));
		String timeParam = this.getTimeString(timeLongList);
		
		bo.setTimeParam(timeParam);
		
		//得到排序后的设备列表
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), bo.getRowCount(), bo);
		deviceFlowDao.deviceListPageSelect(page);
		List<NetflowBo> datas = page.getDatas();
		
		//根据设备列表查询查询每太设备在每个时间点的总流量，通过设备名分组
		List<NetflowChartBo> retList = new ArrayList<NetflowChartBo>();
		if(null != datas && !datas.isEmpty()) {
			for(NetflowBo e : datas) {
				if (e.getIp() != null && !"".equals(e.getIp())) {
					String[] ipsArray = e.getIp().split(",");
					long[] ips = new long[ipsArray.length];
					for (int i = 0; i < ipsArray.length; i++) {
						ips[i] = Long.parseLong(ipsArray[i]);
					}
					bo.setDeviceIps(ips);
					bo.setDeviceIp(ips);
				}
				List<NetflowBo> bos = deviceFlowDao.queryDeviceFlowOfTimePoint(bo);
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
		ret.setUnit((String)filterDisplayInfo4HC.get("sortColumn"));
		ret.setSortColumn((String)filterDisplayInfo4HC.get("sortColumn"));
		ret.setyAxisName((String)filterDisplayInfo4HC.get("yAxisName"));
		ret.setBos(retList);
		ret.setTimeLine(timeLine);

		return ret;
	}

}
