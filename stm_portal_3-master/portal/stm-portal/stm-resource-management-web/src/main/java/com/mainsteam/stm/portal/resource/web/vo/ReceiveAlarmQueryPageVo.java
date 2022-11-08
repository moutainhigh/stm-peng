package com.mainsteam.stm.portal.resource.web.vo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * <li>文件名称: ReceiveAlarmQueryPageVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li> 
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   tpl
 */
public class ReceiveAlarmQueryPageVo implements BasePageVo{

	private static final long serialVersionUID = 3140055021449325524L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private long userID;
	private String parentProfileID;
	private String childProfileID;
	private ReceiveAlarmQueryVo condition;
	private List<ReceiveAlarmQueryVo> receiveAQs;
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
	public String getParentProfileID() {
		return parentProfileID;
	}
	public void setParentProfileID(String parentProfileID) {
		this.parentProfileID = parentProfileID;
	}
	public String getChildProfileID() {
		return childProfileID;
	}
	public void setChildProfileID(String childProfileID) {
		this.childProfileID = childProfileID;
	}
	public ReceiveAlarmQueryVo getCondition() {
		return condition;
	}
	public void setCondition(ReceiveAlarmQueryVo condition) {
		this.condition = condition;
	}
	public List<ReceiveAlarmQueryVo> getReceiveAQs() {
		return receiveAQs;
	}
	public void setReceiveAQs(List<ReceiveAlarmQueryVo> receiveAQs) {
		this.receiveAQs = receiveAQs;
	}
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		return this.receiveAQs;
	}
}
