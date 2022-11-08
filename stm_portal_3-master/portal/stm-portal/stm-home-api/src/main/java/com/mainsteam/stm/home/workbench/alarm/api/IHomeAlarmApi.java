package com.mainsteam.stm.home.workbench.alarm.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.obj.AlarmEvent;


/**
 * <li>文件名称: HomeAlarmApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月17日
 * @author   zhangjunfeng
 */
public interface IHomeAlarmApi {

	/** 
	 * 统计指定资源在当天的告警数量
	* @Title: getHomeAlarmData 
	* @Description: TODO(通过资源类型获取首页告警一览数据)
	* @param resource
	* @return
	* @return HomeAlarmBo    返回类型 
	* @throws 
	*/
	public Map<String,Integer> getHomeAlarmData(List<Long> resourceIds);
	public Map<String,Integer> getHomeAlarmDataById(Long resourceIds);
	public List<AlarmEvent> getHomeOneAlarm(Long resourceIds);
	
	/** s
	 * 统计指定时间段内指定资源的告警数量
	 * 
	* @param resourceIds资源实例id
	* @param start 起始时间
	* @param end 结束时间
	* @return
	* @return HomeAlarmBo    返回类型 
	* @throws 
	*/
	public Map<String,Integer> getHomeAlarmData(List<Long> resourceIds, Date start, Date end);
}
