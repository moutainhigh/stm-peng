package com.mainsteam.stm.portal.netflow.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowQueryBo;
import com.mainsteam.stm.portal.netflow.bo.HighCharts;
import com.mainsteam.stm.portal.netflow.bo.HighchartsData;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.IDeviceGroupNetflowDao;
import com.mainsteam.stm.portal.netflow.po.FlowPo;

@Repository("deviceGroupNetflowDao")
public class DeviceGroupNetflowDaoImpl implements IDeviceGroupNetflowDao {

	@Resource(name = "portal_netflow_sqlSession")
	private SqlSession sqlSession;

	@Override
	public List<FlowPo> getInFlow(
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page) {
		return this.sqlSession.selectList("queryDeviceGroupNetflowIN", page);
	}

	@Override
	public List<FlowPo> getOutFlow(
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page) {
		return this.sqlSession.selectList("queryDeviceGroupNetflowOut", page);
	}

	@Override
	public int getDeviceGroupByCount() {
		return this.sqlSession.selectOne("getDeviceGroupByCount");
	}

	@Override
	public HighCharts getTu(long start, long end, String tableSuffix,
			int[] deviceGroupIds, String order, String asc) {
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		map.put("tableSuffix", tableSuffix);
		map.put("deviceGroupIds", deviceGroupIds);
		map.put("order", order);
		map.put("asc", asc);
		List<Map<String, Object>> data = this.sqlSession.selectList(
				"getDeviceGroupNetflowByTu", map);
		List<String> categories = new ArrayList<>();
		if (data != null) {
			List<HighchartsData> list = new ArrayList<>();
			int oldId = -1;
			HighchartsData h = null;
			for (Map<String, Object> m : data) {
				Date date = new Date((Integer) m.get("acq_time") * 1000l);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String timeStr = sdf.format(date);
				if (!categories.contains(timeStr)) {
					categories.add(timeStr);
				}
				int id = (Integer) m.get("id");
				if (oldId != id) {
					h = new HighchartsData();
					list.add(h);
					h.setName((String) m.get("name"));
					oldId = id;
				}
				if ("inFlow".equals(order)) {
					h.getData().add(
							((BigDecimal) m.get("flow")).doubleValue()
									/ (1024 * 1024.0));
				} else if ("inFlowPackage".equals(order)) {
					h.getData().add(
							((BigDecimal) m.get("flowPackage")).doubleValue()
									/ (1024 * 1024.0));
				}
			}
			HighCharts highCharts = new HighCharts();
			highCharts.setHighchartsDatas(list);
			highCharts.setCategories(categories);
			return highCharts;
		}
		return null;
	}

	@Override
	public long findDeviceListTotalFlows(NetflowParamBo bo) {
		Object object = this.sqlSession.selectOne(
				"findDeviceListTotalFlowsByDeviceGroup", bo);
		return Long.parseLong(object.toString());
	}

	@Override
	public List<NetflowBo> deviceListPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return this.sqlSession.selectList("deviceListPageSelectByDeviceGroup",
				page);
	}

	@Override
	public List<NetflowBo> queryDeviceFlowOfTimePoint(NetflowParamBo bo) {
		return this.sqlSession.selectList(
				"queryDeviceFlowOfTimePointByDeviceGroup", bo);
	}

	@Override
	public String getDeviceIdsByGroupId(Long deviceGroupId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", deviceGroupId);
		return this.sqlSession.selectOne("getDeviceIdsByGroupId", map);
	}

	@Override
	public Long getDeviceIpById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return this.sqlSession.selectOne("getDeviceIpById", map);
	}

	@Override
	public List<OptionBo> findAllDeviceGroupDeviceIps() {
		return this.sqlSession.selectList("findAllDeviceGroupDeviceIps");
	}

	@Override
	public List<OptionBo> getDeviceIpsByIds(String ids) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("ids", ids);
		return this.sqlSession.selectList("getDeviceIpsByIds", m);
	}

	@Override
	public Whole getDeviceGroupTotals(NetflowParamBo bo) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(bo);
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			NetflowParamBo newBo = (NetflowParamBo) ois.readObject();
			if (newBo != null && newBo.getDeviceIps() != null
					&& newBo.getDeviceIps().length == 0) {
				newBo.setDeviceIps(null);
			}
			return this.sqlSession.selectOne("getDeviceGroupTotals", newBo);
		} catch (IOException | ClassNotFoundException e) {
		}
		return null;
	}
}
