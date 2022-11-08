/**
 * 
 */
package com.mainsteam.stm.metric.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeResultDO;

/**
 * @author ziw
 *
 */
public interface CustomMetricChangeResultDAO {
	public List<CustomMetricChangeResultDO> select(long[] changeIds);
	
	public void insert(List<CustomMetricChangeResultDO> applyDOs);
	
	/**
	 * 将生效的记录状态改变
	 * 
	 * @param change_id
	 */
	public void updateCustomMetricChangeDOToApply(List<CustomMetricChangeResultDO> applyDOs);
	
	public void remove(Date expireDate);
}
