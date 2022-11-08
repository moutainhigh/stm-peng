/**
 * 
 */
package com.mainsteam.stm.metric;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.obj.CustomMetricChangeResult;

/**
 * @author ziw
 * 
 */
public interface CustomMetricChangeService {
	/**
	 * 查询需要发布的自定义指标
	 * 
	 * @param limit
	 *            查询的记录个数
	 * @return List<CustomMetricChange>
	 */
	public List<CustomMetricChange> selectChangesWithNotApply(int limit);
	
	/**
	 * 添加自定义指标改变记录
	 * 
	 * @param changes
	 */
	public void occurCustomMetricChanges(List<CustomMetricChange> changes);

	/**
	 * 将生效的记录状态改变
	 * 
	 * @param change_id
	 */
	public void updateCustomMetricChangeToApply(long[] changeIds);

	/**
	 * 根据changeid，查找对不同dms的部署结果
	 * 
	 * @param changeIds
	 * @return List<CustomMetricChangeResult>
	 */
	public List<CustomMetricChangeResult> getCustomMetricChangeResults(
			long[] changeIds);

	/**
	 * 添加修改的结果
	 * 
	 * @param changeResults
	 *            部署结果
	 */
	public void insertCustomMetricChangeResults(
			List<CustomMetricChangeResult> changeResults);
	
	/**
	 * 更新修改的结果
	 * 
	 * @param changeResults
	 *            部署结果
	 */
	public void updateCustomMetricChangeResults(
			List<CustomMetricChangeResult> changeResults);

	/**
	 * 将指定日期前的成功记录删除
	 * 
	 * @param expireDate
	 *            过期日期
	 */
	public void removeCustomMetricChanges(Date expireDate);
}
