package com.mainsteam.stm.portal.resource.bo;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileInfo;

/**
 * <li>文件名称: DefaultStrategyPageBo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
public class StrategyPageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ProfileInfo>  profileInfos;
	
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
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<ProfileInfo> getProfileInfos() {
		return profileInfos;
	}
	public void setProfileInfos(List<ProfileInfo> profileInfos) {
		this.profileInfos = profileInfos;
	}
	
}
