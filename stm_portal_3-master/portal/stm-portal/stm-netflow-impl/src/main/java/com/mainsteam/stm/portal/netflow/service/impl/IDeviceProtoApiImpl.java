/**
 * 
 */
package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.device.IDeviceProtoApi;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowChartBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.common.NetflowUtil;
import com.mainsteam.stm.portal.netflow.common.TimeUtil;
import com.mainsteam.stm.portal.netflow.dao.IDeviceProtoDao;

/**
 * <li>文件名称: IDeviceAppApiImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
public class IDeviceProtoApiImpl implements IDeviceProtoApi {

	private IDeviceProtoDao deviceProtoDao;

	/**
	 * @return the deviceProtoDao
	 */
	public IDeviceProtoDao getDeviceProtoDao() {
		return deviceProtoDao;
	}

	/**
	 * @param deviceProtoDao
	 *            the deviceProtoDao to set
	 */
	public void setDeviceProtoDao(IDeviceProtoDao deviceProtoDao) {
		this.deviceProtoDao = deviceProtoDao;
	}

	@Override
	public NetflowPageBo deviceProtoNetflowPageSelect(NetflowParamBo bo) {

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
		Whole whole = deviceProtoDao.getTotalProtoNetflowsOfDevice(bo);
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
		deviceProtoDao.deviceProtoNetflowPageSelect(page);

		// 获取查询结果
		NetflowPageBo pageBo = new NetflowPageBo();
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		pageBo.setRows(page.getDatas());

		return pageBo;
	}

	@Override
	public NetflowCharWrapper getDeviceProtoChartData(NetflowParamBo bo) {

		String startTime = bo.getStartTime();
		String endTime = bo.getEndTime();
		String tableSuffix = TimeUtil.getTableSuffix(startTime, endTime);

		// 根据起止时间，得到时间段内的时间点
		List<String> timeLine = TimeUtil.getTimeHm(bo.getStartTime(),
				bo.getEndTime(), tableSuffix);
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
		deviceProtoDao.deviceProtoNetflowPageSelect(page);
		List<NetflowBo> datas = page.getDatas();

		List<NetflowChartBo> list = new ArrayList<NetflowChartBo>();
		if (null != datas && !datas.isEmpty()) {
			for (NetflowBo e : datas) {
				bo.setProtoId(e.getProtoId());
				List<NetflowBo> bos = deviceProtoDao
						.queryDeviceProtoNetflowOfTimepoint(bo);
				List<Double> data = NetflowUtil.filterSortResult(bo.getSort(),
						bos, timeLongList.size());
				if (null == data || data.isEmpty()) {
					for (int i = 0; i < timeLongList.size(); i++) {
						data.add(0.0);
					}
				}
				list.add(new NetflowChartBo(e.getName(), data));
			}
		}

		Map<String, Object> m = NetflowUtil.filterDisplayInfo4HC(bo.getSort());

		NetflowCharWrapper ret = new NetflowCharWrapper();
		ret.setUnit((String)m.get("sortColumn"));
		ret.setBos(list);
		ret.setTimeLine(timeLine);
		ret.setSortColumn((String) m.get("sortColumn"));
		ret.setyAxisName((String) m.get("yAxisName"));

		return ret;
	}

}
