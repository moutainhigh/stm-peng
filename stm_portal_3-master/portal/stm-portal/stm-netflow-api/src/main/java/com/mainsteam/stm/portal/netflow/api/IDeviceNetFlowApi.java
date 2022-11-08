package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.DeviceConditionBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceNetFlowPageBo;




public interface IDeviceNetFlowApi {
	public DeviceNetFlowPageBo findTerminalsByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowChartDataBo termianlChartonCloumByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowPageBo findSessionsByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowChartDataBo sessionChartonCloumByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowPageBo findNextHopsByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowChartDataBo nextHopsChartonCloumByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowPageBo findIPGsByDevice(DeviceConditionBo dcBo);
	public DeviceNetFlowChartDataBo ipgChartonCloumByDevice(DeviceConditionBo dcBo);
}
