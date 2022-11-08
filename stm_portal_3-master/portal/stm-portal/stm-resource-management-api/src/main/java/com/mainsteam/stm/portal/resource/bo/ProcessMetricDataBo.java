package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;


/**
 * <li>文件名称: ProcessMetricDataVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月17日
 * @author   pengl
 */
public class ProcessMetricDataBo implements Serializable{

	/**
	 * 
	 */
	private final static String NULL_DATAGRID_VALUE = "--";
	
	private static final long serialVersionUID = -842440575817999532L;
	//资源实例ID
	private long resourceId;
	
	//进程pid
	private String pid = NULL_DATAGRID_VALUE;
	
	//进程备注
	private String processRemark = NULL_DATAGRID_VALUE;
	
	//进程可用性
	private String processAvail = NULL_DATAGRID_VALUE;
	
	//cpu利用率
	private String processCPUAvgRate = NULL_DATAGRID_VALUE;
	
	private String processCPUAvgRateValue = "-1";
	
	//物理内存
	private String physicalMemory = NULL_DATAGRID_VALUE;
	
	private String physicalMemoryValue = "-1";
	
	//虚拟映像
	private String visualMemory = NULL_DATAGRID_VALUE;
	
	private String visualMemoryValue = "-1";
	
	//命令项
	private String processCommand = NULL_DATAGRID_VALUE;
	
	//进程路径
	private String processPath = NULL_DATAGRID_VALUE;

	public String getProcessPath() {
		return processPath;
	}

	public void setProcessPath(String processPath) {
		this.processPath = processPath;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getProcessRemark() {
		return processRemark;
	}

	public void setProcessRemark(String processRemark) {
		this.processRemark = processRemark;
	}

	public String getProcessAvail() {
		return processAvail;
	}

	public void setProcessAvail(String processAvail) {
		this.processAvail = processAvail;
	}

	public String getProcessCPUAvgRate() {
		return processCPUAvgRate;
	}

	public void setProcessCPUAvgRate(String processCPUAvgRate) {
		this.processCPUAvgRate = processCPUAvgRate;
	}

	public String getPhysicalMemory() {
		return physicalMemory;
	}

	public void setPhysicalMemory(String physicalMemory) {
		this.physicalMemory = physicalMemory;
	}

	public String getVisualMemory() {
		return visualMemory;
	}

	public void setVisualMemory(String visualMemory) {
		this.visualMemory = visualMemory;
	}

	public String getProcessCommand() {
		return processCommand;
	}

	public void setProcessCommand(String processCommand) {
		this.processCommand = processCommand;
	}

	public String getProcessCPUAvgRateValue() {
		return processCPUAvgRateValue;
	}

	public void setProcessCPUAvgRateValue(String processCPUAvgRateValue) {
		this.processCPUAvgRateValue = processCPUAvgRateValue;
	}

	public String getPhysicalMemoryValue() {
		return physicalMemoryValue;
	}

	public void setPhysicalMemoryValue(String physicalMemoryValue) {
		this.physicalMemoryValue = physicalMemoryValue;
	}

	public String getVisualMemoryValue() {
		return visualMemoryValue;
	}

	public void setVisualMemoryValue(String visualMemoryValue) {
		this.visualMemoryValue = visualMemoryValue;
	}
	
	
	
}
