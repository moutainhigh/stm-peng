package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IDeviceGroupNetflowApi;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowQueryBo;
import com.mainsteam.stm.portal.netflow.bo.HighCharts;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.PageHighchart;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.IDeviceGroupNetflowDao;
import com.mainsteam.stm.portal.netflow.po.FlowPo;

@Service("deviceGroupNetflowApi")
public class DeviceGroupNetflowApiImpl implements IDeviceGroupNetflowApi {

	@Autowired
	private IDeviceGroupNetflowDao deviceGroupNetflowDao;

	@Override
	public Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> list(
			
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page) {
		List<FlowPo> ins = this.deviceGroupNetflowDao.getInFlow(page);
		Map<Integer, DeviceGroupNetflowBo> map = new HashMap<>();
		double s = page.getCondition().getEnd()
				- page.getCondition().getStart();
		if (ins != null) {
			for (FlowPo flowPo : ins) {
				DeviceGroupNetflowBo netflow = new DeviceGroupNetflowBo();
				map.put(flowPo.getId(), netflow);
				netflow.setId(flowPo.getId());
				netflow.setName(flowPo.getName());
				netflow.setInFlow(flowPo.getFlow());
				netflow.setInPackage(flowPo.getFlowPackage());
				netflow.setInSpeed(flowPo.getFlow() / s);
			}
		}
		double z = 0.00;
		List<FlowPo> outs = this.deviceGroupNetflowDao.getOutFlow(page);
		if (outs != null) {
			for (FlowPo flowPo : outs) {
				DeviceGroupNetflowBo netflow = map.get(flowPo.getId());
				if (netflow != null) {
					netflow.setOutFlow(flowPo.getFlow());
					netflow.setOutPackage(flowPo.getFlowPackage());
					netflow.setOutSpeed(flowPo.getFlow() / s);
					z += netflow.getTFlow();
				}
			}
		}
		List<DeviceGroupNetflowBo> netflows = new ArrayList<>();
		Set<Integer> ids = map.keySet();
		if (ids != null) {
			for (Integer id : ids) {
				DeviceGroupNetflowBo n = map.get(id);
				if (z > 0) {
					n.setAccounting(n.getTFlow() / z);
				}
				netflows.add(n);
			}
		}
		page.setDatas(netflows);
		page.setTotalRecord(this.deviceGroupNetflowDao.getDeviceGroupByCount());
		List<DeviceGroupNetflowBo> dataList = page.getDatas();
		if (dataList != null) {
			int[] deviceGroupIds = new int[dataList.size()];
			for (int i = 0; i < deviceGroupIds.length; i++) {
				deviceGroupIds[i] = dataList.get(i).getId();
			}
			HighCharts highCharts = this.deviceGroupNetflowDao.getTu(page
					.getCondition().getStart(), page.getCondition().getEnd(),
					page.getCondition().getTableSuffix(), deviceGroupIds,
					"inFlow", null);
			PageHighchart<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> hPage = new PageHighchart<>();
			hPage.setHighchartsDatas(highCharts.getHighchartsDatas());
			hPage.setCategories(highCharts.getCategories());
			hPage.setCondition(page.getCondition());
			hPage.setDatas(page.getDatas());
			hPage.setOrder(page.getOrder());
			hPage.setRowCount(page.getRowCount());
			hPage.setSort(page.getSort());
			hPage.setStartRow(page.getStartRow());
			hPage.setTotalRecord(page.getTotalRecord());
			return hPage;
		}
		return page;
	}

	@Override
	public NetflowPageBo deviceListPageSelect(NetflowParamBo bo) {

		long[] seTime = TimeUtil.getQueryDate(bo.getStartTime(),
				bo.getEndTime());
		// 根据时间获得要查询的表的时间部分
		String tableSuffix = TimeUtil.getTableSuffix(bo.getStartTime(), bo.getEndTime());
		String timeInterval = String.valueOf(TimeUtil.getReduceTime(bo.getStartTime(), bo.getEndTime())/1000);
		
		//构建查询的条件
		bo.setStime(String.valueOf(seTime[0]));
		bo.setEtime(String.valueOf(seTime[1]));
		bo.setTableSuffix(tableSuffix);
		bo.setTimeInterval(timeInterval);
		
		//所有设备组的设备ID
		List<OptionBo> deviceIds = this.deviceGroupNetflowDao.findAllDeviceGroupDeviceIps();
		List<String> filteredIds = new ArrayList<String>();
		if(null != deviceIds && !deviceIds.isEmpty()) {
			for(OptionBo id : deviceIds) {
				if(null != id && id.getIds().length() > 0) {
					String[] ids = id.getIds().split(",");
					for(String i : ids) {
						if(!filteredIds.contains(i)) {
							filteredIds.add(i);
						}
					}
				}
			}
		}
		
		// 获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		
		//设置要查询的设备的IP
		StringBuilder sb = new StringBuilder();
		if(!filteredIds.isEmpty()) {
			for(String s : filteredIds) {
				sb.append(s).append(",");
			}
			if(sb.toString().length() > 0) {
				sb = sb.deleteCharAt(sb.toString().length() - 1);
			}
			
			List<OptionBo> ipsList = this.deviceGroupNetflowDao.getDeviceIpsByIds(sb.toString());
			long[] ips = new long[ipsList.size()]; 
			if(null != ipsList && !ipsList.isEmpty()) {
				for(int i=0,len=ipsList.size(); i<len; i++) {
					ips[i] = ipsList.get(i).getRouterIp();
				}
			}
			bo.setDeviceIps(ips);
			
			// 获取所有设备的总流量，用于计算占比
//		long wholeFlows = this.deviceGroupNetflowDao.findDeviceListTotalFlows(bo);
			Whole w = this.deviceGroupNetflowDao.getDeviceGroupTotals(bo);
			long wholeFlows = w.getWholeFlows();
			long wholePackets = w.getWholePackets();
			long wholeConnects = w.getWholeConnects();
			bo.setWholeFlows(String.valueOf(wholeFlows));
			bo.setWholePackets(String.valueOf(wholePackets));
			bo.setWholeConnects(String.valueOf(wholeConnects));
			
			// 构建分页查询对象
			Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), bo.getRowCount(), bo);
			
			// 执行流量查询
			this.deviceGroupNetflowDao.deviceListPageSelect(page);
			
			pageBo.setRowCount(page.getRowCount());
			pageBo.setStartRow(page.getStartRow());
			pageBo.setTotalRecord(page.getTotalRecord());
			pageBo.setRows(page.getDatas());
			
		}

		return pageBo;
	}

	@Override
	public NetflowCharWrapper deviceFlowChartPageSelect(NetflowParamBo bo) {

		// 查询图表需要的数据，包括：1.时间点列表，2，设备在每个时间点对应的流量数据
		// 根据起止时间决定查询哪张表
		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);

		// 根据起止时间，得到时间段内的时间点
		List<Long> timePoints = TimeUtil.getTimePoint(startTime, endTime,
				tableSuffix);
		List<String> timeLine = TimeUtil.getTimeLineList(timePoints,
				tableSuffix);
		List<Long> timeLongList = this.getTimePoint(TimeUtil.getTimePoint(
				startTime, endTime, tableSuffix));
		String timeParam = this.getTimeString(timeLongList);

		bo.setTimeParam(timeParam);

		// 得到排序后的设备列表
		Page<NetflowBo, NetflowParamBo> page = new Page<NetflowBo, NetflowParamBo>(bo.getStartRow(), bo.getRowCount(), bo);

		this.deviceGroupNetflowDao.deviceListPageSelect(page);
		List<NetflowBo> datas = page.getDatas();

		// 根据设备列表查询查询每太设备在每个时间点的总流量，通过设备名分组
		List<NetflowChartBo> retList = new ArrayList<NetflowChartBo>();
		if (null != datas && !datas.isEmpty()) {
			for (NetflowBo e : datas) {
				if (e.getIp() != null && !"".equals(e.getIp())) {
					String[] ipsArray = e.getIp().split(",");
					long[] ips = new long[ipsArray.length];
					for (int i = 0; i < ipsArray.length; i++) {
						ips[i] = Long.parseLong(ipsArray[i]);
					}
					bo.setDeviceIps(ips);
				}
				List<NetflowBo> bos = this.deviceGroupNetflowDao
						.queryDeviceFlowOfTimePoint(bo);
				List<Double> rows = NetflowUtil.filterSortResult(bo.getSort(),
						bos, timeLongList.size());
				if (null == bos || bos.isEmpty()) {
					for (int i = 0; i < timeLongList.size(); i++) {
						rows.add(0.0);
					}
				}
				String name = NetflowUtil.getName(e.getName());
				retList.add(new NetflowChartBo(name, rows));
			}
		}

		Map<String, Object> filterDisplayInfo4HC = NetflowUtil
				.filterDisplayInfo4HC(bo.getSort());

		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setSortColumn((String) filterDisplayInfo4HC.get("sortColumn"));
		ret.setyAxisName((String) filterDisplayInfo4HC.get("yAxisName"));
		ret.setBos(retList);
		ret.setTimeLine(timeLine);

		return ret;
	}

	private List<Long> getTimePoint(List<Long> src) {
		List<Long> dst = new ArrayList<Long>();
		if (null != src && !src.isEmpty()) {
			for (long s : src) {
				dst.add(s / 1000);
			}
		}
		return dst;
	}

	private String getTimeString(List<Long> src) {
		StringBuffer sb = new StringBuffer();
		if (null != src && !src.isEmpty()) {
			for (long d : src) {
				sb.append(d).append(",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public String getDeviceIdsByGroupId(Long deviceGroupId) {
		String deviceIds =  deviceGroupNetflowDao.getDeviceIdsByGroupId(deviceGroupId);
		if(null != deviceIds && 0 != deviceIds.length()) {
			String[] idArray = deviceIds.split(",");
			StringBuilder ips = new StringBuilder();
			for(String id : idArray) {
				//查询设备表，获得数字型IP
				Long ip = deviceGroupNetflowDao.getDeviceIpById(id);
				if(null != ip) {
					ips.append(ip).append(",");
				}
			}
			if(ips.length() > 0) {
				ips = ips.deleteCharAt(ips.length()-1);
				return ips.toString();
			}
		}
		return "";
	}
}
