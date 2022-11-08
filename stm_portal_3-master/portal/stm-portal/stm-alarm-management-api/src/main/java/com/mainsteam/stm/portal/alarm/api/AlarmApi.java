package com.mainsteam.stm.portal.alarm.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgeBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgePageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmNotifyPageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmPageBo;

/**
 * <li>文件名称: AlarmApi.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月
 * @author xhf
 */
public interface AlarmApi {

	/**
	 * 告警分页查询
	 * @return
	 */
	AlarmPageBo getAlarmList(long startRow,long rowNumber,AlarmEventQuery2 condition);
	
	/**
	 * 通过告警ID查询已恢复告警对象
	 * @param alarmId
	 * @param alarmType 0、所有 1、未恢复 2、已恢复
	 * @return
	 */
	AlarmEvent getAlarmById(long alarmId, String alarmType);
	/**
	 * 查询告警的影响和根源
	 * @param alarmId
	 * @return
	 */
	public Map<String, String> getAlarmRelationship(long alarmId);
	/**
	 * 通过告警ID查询发送信息
	 * @param alarmId
	 * @return
	 */
	AlarmNotifyPageBo getAlarmNotify(long startRecord,long pageSize,long alarmId);
	
	/**
	 * 通过告警ID查询知识库关联信息
	 * @param alarmId
	 * @return
	 */
	public List<AlarmKnowledgeBo> queryAlarmKnowledgeList(long alarmId);
	
	/**
	 * 知识库关联分页查询
	 * @param startRecord
	 * @param pageSize
	 * @param alarmId
	 * @return
	 */
	public AlarmKnowledgePageBo getAlarmKnowledge(long startRecord,long pageSize,long alarmId);
	
	/**
	 * 获取快照文件
	 * @param fileId
	 * @return
	 */
	public Map<String, String> getSnapShotFile(long snapshotFileId, long recoverFileId);
	/***
	 * 
	 * @param ids
	 */
	public String SureAlarmByIds(Long[] ids);
	
	
	public String  confirmAlarmEvent(String alarmInfo,String type);
}
