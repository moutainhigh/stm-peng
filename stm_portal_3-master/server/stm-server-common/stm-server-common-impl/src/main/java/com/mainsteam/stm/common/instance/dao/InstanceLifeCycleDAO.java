package com.mainsteam.stm.common.instance.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.instance.dao.obj.InstanceLifeCycle;

public interface InstanceLifeCycleDAO {

	public void addLifeCycle(InstanceLifeCycle po);
	
	public List<InstanceLifeCycle> findLifeCycle(List<Long> instanceIDes,Date startTime,Date endTime);
	
	/**获取指定时间的资源状态
	 * @param instanceIDes
	 * @param startTime
	 * @return
	 */
	public InstanceLifeCycle findPreState(long instanceID,Date startTime);
}
