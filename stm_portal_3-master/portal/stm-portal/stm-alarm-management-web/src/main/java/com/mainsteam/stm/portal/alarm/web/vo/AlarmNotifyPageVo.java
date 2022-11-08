package com.mainsteam.stm.portal.alarm.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * <li>文件名称: AlarmNotifyPageVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年10月
 * @author xhf
 */
public class AlarmNotifyPageVo implements BasePageVo{

	
	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private AlarmNotifyVo condition;
	private List<AlarmNotifyVo> alarmNotify;
	public List<AlarmNotifyVo> getAlarmNotify() {
		return alarmNotify;
	}
	public void setAlarmNotify(List<AlarmNotifyVo> alarmNotify) {
		this.alarmNotify = alarmNotify;
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
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	public AlarmNotifyVo getCondition() {
		return condition;
	}
	public void setCondition(AlarmNotifyVo condition) {
		this.condition = condition;
	}
	
	
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(alarmNotify!=null){
			return this.alarmNotify;
		}
		return new ArrayList<AlarmNotifyVo>();
	}
	
}
