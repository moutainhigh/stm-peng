package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;


/**
 * <li>文件名称: FileMetricDataBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月2日
 * @author   tongpl
 */

public class LogicalVolumeMetricDataBo implements Serializable{
	  /**
	 * 
	 */
	  private static final long serialVersionUID = -2878361309134915816L;
	  
	  /**
		 * 
		 */
	  private final static String NULL_DATAGRID_VALUE = "--";
	  
	  private Long instanceId;
	  //逻辑卷名
	  private String logicalShowName = NULL_DATAGRID_VALUE;
	  private String logicalName = NULL_DATAGRID_VALUE;
	  
	  //类型
	  private String logicalType= NULL_DATAGRID_VALUE;
	  //逻辑卷中当前逻辑分区的数目
	  private String logicalLPs = NULL_DATAGRID_VALUE;
	  private String logicalLPsValue = "-1";
	  
	  //逻辑卷中当前物理分区的数目
	  private String logicalPPs = NULL_DATAGRID_VALUE;
	  private String logicalPPsValue = "-1";
	  
	  //每个物理分区大小
	  private String logicalPPSize = NULL_DATAGRID_VALUE;
	  private String logicalPPSizeValue = "-1";
	  
	  //逻辑卷状态
	  private String logicalState = NULL_DATAGRID_VALUE;
	  
	  //所属卷组名称
	  private String logicalVGroupName = NULL_DATAGRID_VALUE;
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getLogicalType() {
		return logicalType;
	}
	public void setLogicalType(String logicalType) {
		this.logicalType = logicalType;
	}
	public String getLogicalLPs() {
		return logicalLPs;
	}
	public void setLogicalLPs(String logicalLPs) {
		this.logicalLPs = logicalLPs;
	}
	public String getLogicalLPsValue() {
		return logicalLPsValue;
	}
	public void setLogicalLPsValue(String logicalLPsValue) {
		this.logicalLPsValue = logicalLPsValue;
	}
	public String getLogicalPPs() {
		return logicalPPs;
	}
	public void setLogicalPPs(String logicalPPs) {
		this.logicalPPs = logicalPPs;
	}
	public String getLogicalPPsValue() {
		return logicalPPsValue;
	}
	public void setLogicalPPsValue(String logicalPPsValue) {
		this.logicalPPsValue = logicalPPsValue;
	}
	public String getLogicalState() {
		return logicalState;
	}
	public void setLogicalState(String logicalState) {
		this.logicalState = logicalState;
	}
	public String getLogicalShowName() {
		return logicalShowName;
	}
	public void setLogicalShowName(String logicalShowName) {
		this.logicalShowName = logicalShowName;
	}
	public String getLogicalPPSize() {
		return logicalPPSize;
	}
	public void setLogicalPPSize(String logicalPPSize) {
		this.logicalPPSize = logicalPPSize;
	}
	public String getLogicalPPSizeValue() {
		return logicalPPSizeValue;
	}
	public void setLogicalPPSizeValue(String logicalPPSizeValue) {
		this.logicalPPSizeValue = logicalPPSizeValue;
	}
	public String getLogicalVGroupName() {
		return logicalVGroupName;
	}
	public void setLogicalVGroupName(String logicalVGroupName) {
		this.logicalVGroupName = logicalVGroupName;
	}
	
}
