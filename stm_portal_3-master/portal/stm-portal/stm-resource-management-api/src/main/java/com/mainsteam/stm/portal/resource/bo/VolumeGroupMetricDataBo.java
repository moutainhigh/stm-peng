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

public class VolumeGroupMetricDataBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7934607265523163277L;
	/**
	 * 
	 */
    private final static String NULL_DATAGRID_VALUE = "--";
  
    private Long instanceId;
  
    //卷组名称
	private String volumeGroupName = NULL_DATAGRID_VALUE;
	
	//卷组状态
	private String volumeGroupState = NULL_DATAGRID_VALUE;
	
	//每个物理分区大小
	private String ppSize = NULL_DATAGRID_VALUE;
	private String ppSizeValue = "-1";
	
	//物理总分区大小
	private String totalPpSize = NULL_DATAGRID_VALUE;
	private String totalPpSizeValue = "-1";
	
	//逻辑卷数
	private String lvsNumber = NULL_DATAGRID_VALUE;
	private String lvsNumberValue = "-1";
	
	//物理卷数
	private String pvsNumber = NULL_DATAGRID_VALUE;
	private String pvsNumberValue = "-1";
	
	public String getVolumeGroupName() {
		return volumeGroupName;
	}

	public void setVolumeGroupName(String volumeGroupName) {
		this.volumeGroupName = volumeGroupName;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getVolumeGroupState() {
		return volumeGroupState;
	}

	public void setVolumeGroupState(String volumeGroupState) {
		this.volumeGroupState = volumeGroupState;
	}

	public String getTotalPpSize() {
		return totalPpSize;
	}

	public void setTotalPpSize(String totalPpSize) {
		this.totalPpSize = totalPpSize;
	}

	public String getTotalPpSizeValue() {
		return totalPpSizeValue;
	}

	public void setTotalPpSizeValue(String totalPpSizeValue) {
		this.totalPpSizeValue = totalPpSizeValue;
	}

	public String getLvsNumber() {
		return lvsNumber;
	}

	public void setLvsNumber(String lvsNumber) {
		this.lvsNumber = lvsNumber;
	}

	public String getLvsNumberValue() {
		return lvsNumberValue;
	}

	public void setLvsNumberValue(String lvsNumberValue) {
		this.lvsNumberValue = lvsNumberValue;
	}

	public String getPvsNumber() {
		return pvsNumber;
	}

	public void setPvsNumber(String pvsNumber) {
		this.pvsNumber = pvsNumber;
	}

	public String getPvsNumberValue() {
		return pvsNumberValue;
	}

	public void setPvsNumberValue(String pvsNumberValue) {
		this.pvsNumberValue = pvsNumberValue;
	}

	public String getPpSize() {
		return ppSize;
	}

	public void setPpSize(String ppSize) {
		this.ppSize = ppSize;
	}

	public String getPpSizeValue() {
		return ppSizeValue;
	}

	public void setPpSizeValue(String ppSizeValue) {
		this.ppSizeValue = ppSizeValue;
	}
	
	
}
