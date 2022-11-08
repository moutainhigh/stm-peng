/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceDao.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public interface IDeviceNetFlowDao {
	public List<DeviceNetFlowBo> findTerminalsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page);

	public Whole getAllFindTerminalsFlows(DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> terminalsCharOnCloumByDevice(
			DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> findSessionsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page);

	public Whole getAllFindSessionsFlows(DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> sessionsCharOnCloumByDevice(
			DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> findNextHopsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page);

	public Whole getAllFindNextHopsFlows(DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> nextHopsCharOnCloumByDevice(
			DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> findIPGsByDevice(
			Page<DeviceNetFlowBo, DeviceConditionBo> page);

	public Whole getAllFindIPGsFlows(DeviceConditionBo dtcBo);

	public List<DeviceNetFlowBo> ipgsCharOnCloumByDevice(DeviceConditionBo dtcBo);

	public String getIPGNamebyID(String ipgid);
}
