package com.mainsteam.stm.portal.netflow.dao.alarm;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmContent;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmDataGridListBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmProfileBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmThresholdBo;

public interface IAlarmProfileDao {
	public List<AlarmDataGridListBo> getAllAlarms(
			Page<AlarmDataGridListBo, Map<String, String>> page);

	public int getAllCountAlarms(int profileId);

	public int getOneHoureAlarms(int ProfileId);

	public List<AlarmResourceBo> getIPGroup();

	public List<AlarmResourceBo> getInterface(int id);

	public List<AlarmResourceBo> getDevice();

	public List<AlarmResourceBo> getInterfaceGroup();

	public List<AlarmResourceBo> getProtocol();

	public List<AlarmResourceBo> getApplication();

	public int addAlarmBasic(AlarmProfileBo apf);

	public int addAlarmResource(AlarmProfileBo apf);

	public int insertRule(Map<String, String> map);

	public int insertApps(List<AlarmResourceBo> apps);

	public int insertApp(Map<String, String> map);

	public int insertProtos(List<AlarmResourceBo> protos);

	public int insertProto(Map<String, String> map);

	public int insertIPs(Map<String, String> map);

	public int delDevices(int profileId);

	public int delApps(int profileId);

	public int delProtos(int profileId);

	public int delIPs(int profileId);

	public int updateAlarmBasic(AlarmProfileBo apf);

	/**
	 * 更新告警状态：启用，停用
	 *
	 * @param profileId
	 * @param status
	 * @return
	 */
	public int updateAlarmStatus(int profileId, int status);

	/**
	 * 加载基本信息
	 *
	 * @param profileId
	 * @return
	 */
	public AlarmProfileBo loadBasic(int profileId);

	/**
	 * 加载阈值
	 *
	 * @param profileId
	 * @return
	 */
	public List<AlarmThresholdBo> loadThreshold(int profileId);

	public int addThresholds(List<AlarmThresholdBo> thresholds);

	public int delThresholds(int profileId);

	public int delProfiles(String[] ids);

	public int updateAlarmDeviceType(String profileId, String deviceType);

	public List<AlarmContent> getAlarmEvents();

	public int updateAlarmEventState(String[] ids);

	public AlarmResourceBo getPrfileLevel(String thresholdId);

	int getCount(String name, Integer id);

	public List<String> loadLocalInterfaces(String profileID);

	public List<String> loadLocalIPGoups(String profileId);

	public List<String> loadLocalIFGoups(String profileId);
}
