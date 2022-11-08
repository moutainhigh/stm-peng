/**
 * 
 */
package com.mainsteam.stm.transfer.obj;

/**
 * @author ziw
 *
 */
public enum TransferDataType {
	MetricData,DCSHeartbeatData,ResourceInstance,ResourceInstancesForLink,RefreshResourceInstance,
	CreateProfile,AddToMonitor,RemoveMonitor,DeleteProfile,UpdateMetricFrequency,
	UpdateMetricMonitor,TOPO_NODE,TOPO_LINK,TOPO_MSG,SyslogTrap,SnmpTrap;
}
