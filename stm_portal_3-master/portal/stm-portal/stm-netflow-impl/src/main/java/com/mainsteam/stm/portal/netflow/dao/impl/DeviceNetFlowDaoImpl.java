/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.IDeviceNetFlowDao;

/**
 * <li>文件名称: DeviceDaoImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

@SuppressWarnings("rawtypes")
public class DeviceNetFlowDaoImpl extends BaseDao implements IDeviceNetFlowDao {

	@Override
	public List<DeviceNetFlowBo> findTerminalsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page) {
		return getSession().selectList(
				getNamespace() + "findTerminalsByDevice", page);
	}

	public DeviceNetFlowDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceNetFlowDao.class.getName());
	}

	@Override
	public Whole getAllFindTerminalsFlows(DeviceConditionBo dcBo) {
		return getSession()
				.selectOne(
						getNamespace()
								+ "findTerminalsByDeviceAllTerminalsFlows",
						dcBo);
	}

	@Override
	public List<DeviceNetFlowBo> terminalsCharOnCloumByDevice(
			DeviceConditionBo dttBo) {

		return getSession().selectList(
				getNamespace() + "terminalChartOnCloumByDevice", dttBo);
	}

	public List<DeviceNetFlowBo> findSessionsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page) {
		return getSession().selectList(getNamespace() + "findSessionsByDevice",
				page);
	}

	public Whole getAllFindSessionsFlows(DeviceConditionBo dtcBo) {
		return getSession().selectOne(
				getNamespace() + "findSessionsByDeviceAllSessionsFlows", dtcBo);
	}

	public List<DeviceNetFlowBo> sessionsCharOnCloumByDevice(
			DeviceConditionBo dtcBo) {
		return getSession().selectList(
				getNamespace() + "sessionChartOnCloumByDevice", dtcBo);
	}

	public List<DeviceNetFlowBo> findNextHopsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page) {
		return getSession().selectList(getNamespace() + "findNextHopsByDevice",
				page);
	}

	public Whole getAllFindNextHopsFlows(DeviceConditionBo dtcBo) {
		return getSession().selectOne(
				getNamespace() + "findNextHopsByDeviceAllNextHopsFlows", dtcBo);
	}

	public List<DeviceNetFlowBo> nextHopsCharOnCloumByDevice(
			DeviceConditionBo dtcBo) {
		return getSession().selectList(
				getNamespace() + "nextHopsChartOnCloumByDevice", dtcBo);
	}

	public List<DeviceNetFlowBo> findIPGsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page) {
		return getSession().selectList(getNamespace() + "findIPGsByDevice",
				page);
	}

	public Whole getAllFindIPGsFlows(DeviceConditionBo dtcBo) {
		return getSession().selectOne(
				getNamespace() + "findIPGsByDeviceAllIPGsFlows", dtcBo);
	}

	public List<DeviceNetFlowBo> ipgsCharOnCloumByDevice(DeviceConditionBo dtcBo) {
		return getSession().selectList(
				getNamespace() + "IPGChartOnCloumByDevice", dtcBo);
	}

	public String getIPGNamebyID(String ipgid) {
		return getSession()
				.selectOne(getNamespace() + "getipgsnamebyid", ipgid);
	}
}
