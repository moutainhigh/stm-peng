package com.mainsteam.stm.portal.netflow.api.alarm;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmDataGridListBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmProfileBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmThresholdBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.InterfaceTreeBo;

public interface IAlarmProfileApi {
	public Page<AlarmDataGridListBo, Map<String, String>> getAllAlarms(
			String RowCount, String StartRow, String AlarmsName, String order);

	public List<AlarmResourceBo> getIPGroup();

	public List<InterfaceTreeBo> getInterface();

	public List<AlarmResourceBo> getInterfaceGroup();

	public List<AlarmResourceBo> getProtocol();

	public List<AlarmResourceBo> getApplication();

	/**
	 * 更新状态
	 *
	 * @param profileId
	 * @param status
	 * @return
	 */
	int updateStatus(int profileId, int status);

	/**
	 * 加载基本信息
	 *
	 * @param profileId
	 * @return
	 */
	AlarmProfileBo loadBasic(int profileId);

	/**
	 * 加载阈值
	 *
	 * @param profileId
	 * @return
	 */
	List<AlarmThresholdBo> loadThreshold(int profileId);

	public int addAlarmBasic(AlarmProfileBo apf);

	public int addAlarmResource(AlarmProfileBo apf);

	public int addAlarmRules(AlarmProfileBo apf);

	public int updateAlarmBasic(AlarmProfileBo apf);

	public int addThresholds(List<AlarmThresholdBo> thresholds);

	public int delProfiles(String[] profileIds);

	int getCount(String name, Integer id);

	public List<InterfaceTreeBo> loadInterfaces(String profileID,String position);

	public List<AlarmResourceBo> loadIPGroups(String profileId, String position);

	public List<AlarmResourceBo> loadIFGroups(String profileId, String string);

}
