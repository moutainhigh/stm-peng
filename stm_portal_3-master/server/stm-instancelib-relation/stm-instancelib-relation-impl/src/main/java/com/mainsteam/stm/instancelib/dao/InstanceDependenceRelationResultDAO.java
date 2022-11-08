package com.mainsteam.stm.instancelib.dao;

import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependenceResultPO;

/**
 * 专家系统关系结果DAO类
 * @author xiaoruqiang
 *
 */
public interface InstanceDependenceRelationResultDAO {

	public InstanceDependenceResultPO getDependenceResultByAlarmEventIdAndType(long alarmEventId,String type) throws Exception;
	
	public void insertDependenceResult(InstanceDependenceResultPO instanceDependenceResultPO) throws Exception;
	
	public void removeDependenceResultByAlarmEventId(long alarmEventId) throws Exception;
}
