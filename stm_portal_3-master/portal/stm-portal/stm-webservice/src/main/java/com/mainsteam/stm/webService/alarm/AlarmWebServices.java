package com.mainsteam.stm.webService.alarm;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;

@WebService(name = "alarmWebServices", targetNamespace = "http://www.mainsteam.com/ms/alarmService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AlarmWebServices {

	@WebMethod
	@WebResult
	public AlarmSyncResult syncAlarm(@WebParam() AlarmSyncBean alarmBean);

	/**
	 * 第三方告警绑定OC4资源
	 * @param alarmBindResourceBean
	 * @return
     */
	@WebMethod
	@WebResult
	public AlarmSyncResult bindAlarmWithResource(@WebParam() AlarmBindResourceBean alarmBindResourceBean);

	// 获取最近一小时所有告警信息
	@WebMethod(action = "getAllAlarmInCurrentOnehour")
	public String getAllAlarmInCurrentOnehour();

	// 根据告警级别获取告警信息
	@WebMethod(action = "getAlarmByAlarmLevel")
	public String getAlarmByAlarmLevel(@WebParam(name = "level") String level);

	// 根据时间段获取告警信息
	@WebMethod(action = "getAlarmByTime")
	public String getAlarmByTime(@WebParam(name = "start") String start, @WebParam(name = "end") String end);

	// 根据告警类型获取告警信息
	@WebMethod(action = "getAlarmByAlarmType")
	public String getAlarmByAlarmType(@WebParam(name = "alarmType") String alarmType);

	// 根据资源类型获取告警信息
	@WebMethod(action = "getAlarmByResourceType")
	public String getAlarmByResourceType(@WebParam(name = "resourceId") String resourceId);

	// 根据资源实例ID获取告警信息
	@WebMethod(action = "getNotRecoveredAlarmByInstanceId")
	public String getNotRecoveredAlarmByInstanceId(@WebParam(name="instanceId") String instanceId);
	
	@WebMethod(action = "getAlarmByInstanceId")
	public String getAlarmByInstanceId(@WebParam(name="instanceId") String instanceId);
	
	
	// 分页查询告警数据
	@WebMethod(action = "getAlarmListByPage")
	public String getAlarmListByPage(@WebParam() AlarmQueryPageBean alarmQueryPage);

	@WebMethod
	@WebResult
	public List<AlarmRule> getAlarmRulesByProfileIdByNetflow(@WebParam() long id);

	@WebMethod
	public void deleteAlarmRuleById(@WebParam() long[] ids);

	@WebMethod
	public void notify(@WebParam() AlarmNotifyBean alarmNotify);
	
	/**
	 * iwatch 告警信息定制接口
	 * 
	 * @return
	 */
	@WebMethod(action = "getParentResourceCriticalAlarm")
	public AlarmParentResourceDownResult getParentResourceCriticalAlarm();
}
