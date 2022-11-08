package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

/**
 * <li>文件名称: UserPage.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月23日
 * @author ziwenwen
 */
public class DevicePageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<DeviceBo> deviceBos;

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<DeviceBo> getDeviceBos() {
		return deviceBos;
	}

	public void setDeviceBos(List<DeviceBo> deviceBos) {
		this.deviceBos = deviceBos;
	}

	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
}
