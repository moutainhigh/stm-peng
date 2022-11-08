/**
 * 
 */
package com.mainsteam.stm.metric.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;

/**
 * @author ziw
 * 
 */
public interface CustomMetricChangeDAO {
	/**
	 * 
	 * @param changeDOs
	 */
	public void addCustomMetricChangeDOs(List<CustomMetricChangeDO> changeDOs);

	/**
	 * 查询需要生效的自定义指标改变记录
	 * 
	 * @param limit
	 * @return
	 */
	public List<CustomMetricChangeDO> selectChangeDOsWithNotApply(int limit);

	/**
	 * 将生效的记录状态改变
	 * 
	 * @param change_id
	 */
	public void updateCustomMetricChangeDOToApply(long[] change_id);
	
	
	public void remove(Date expireDate);
}
