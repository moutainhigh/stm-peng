package com.mainsteam.stm.portal.alarm.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgeBo;

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
public class AlarmKnowledgePageVo implements BasePageVo{

	
	private static final long serialVersionUID = 1L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private AlarmKnowledgeBo condition;
	private List<AlarmKnowledgeBo> alarmKnowledge;
  
	public List<AlarmKnowledgeBo> getAlarmKnowledge() {
		return alarmKnowledge;
	}
	public void setAlarmKnowledge(List<AlarmKnowledgeBo> alarmKnowledge) {
		this.alarmKnowledge = alarmKnowledge;
	}
	public AlarmKnowledgeBo getCondition() {
		return condition;
	}
	public void setCondition(AlarmKnowledgeBo condition) {
		this.condition = condition;
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

	
	
	
	@Override
	public long getTotal() {
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(alarmKnowledge!=null){
			return this.alarmKnowledge;
		}
		return new ArrayList<AlarmKnowledgeBo>();
	}
	
}
