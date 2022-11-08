package com.mainsteam.stm.portal.netflow.dao.impl.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmContent;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmDataGridListBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmProfileBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmThresholdBo;
import com.mainsteam.stm.portal.netflow.dao.alarm.IAlarmProfileDao;

@SuppressWarnings("rawtypes")
public class AlarmProfileDaoImpl extends BaseDao implements IAlarmProfileDao {

	public AlarmProfileDaoImpl(SqlSessionTemplate session) {
		super(session, IAlarmProfileDao.class.getName());
	}

	@Override
	public List<AlarmDataGridListBo> getAllAlarms(
			Page<AlarmDataGridListBo, Map<String, String>> page) {
		return super.getSession().selectList("getAllAlarms", page);
	}

	@Override
	public int getAllCountAlarms(int profileId) {

		return super.getSession().selectOne("getAllEventCount", profileId);
	}

	@Override
	public int getOneHoureAlarms(int ProfileId) {
		return super.getSession().selectOne("getOneHourCount", ProfileId);
	}

	@Override
	public List<AlarmResourceBo> getIPGroup() {

		return super.getSession().selectList("getIPGroups");
	}

	@Override
	public List<AlarmResourceBo> getInterface(int id) {
		return super.getSession().selectList("getInterface", id);
	}

	@Override
	public List<AlarmResourceBo> getInterfaceGroup() {
		return super.getSession().selectList("getInterfaceGroup");
	}

	@Override
	public List<AlarmResourceBo> getDevice() {
		return super.getSession().selectList("getDevices");
	}

	@Override
	public List<AlarmResourceBo> getProtocol() {
		return super.getSession().selectList("getProtocol");
	}

	@Override
	public List<AlarmResourceBo> getApplication() {
		return super.getSession().selectList("getApplications");
	}

	@Override
	public int addAlarmBasic(AlarmProfileBo apf) {
		return super.getSession().insert("addAlarmBasic", apf);
	}

	@Override
	public int updateAlarmBasic(AlarmProfileBo apf) {
		return super.getSession().update("updateAlarmBasic", apf);
	}

	@Override
	public int addAlarmResource(AlarmProfileBo apf) {
		List<AlarmResourceBo> list = new ArrayList<AlarmResourceBo>();
		List<String> ids = apf.getIds();
		int profleId = new Integer(apf.getProfileId());
		for (String s : ids) {
			AlarmResourceBo bo = new AlarmResourceBo();
			bo.setId(String.valueOf(profleId));// 构造了一个用于存储的对象，id存储了数据库中profileID字段的值
			bo.setName(s);// s是对应的资源的ID
			list.add(bo);
		}
		if (list.size() > 0) {
			return super.getSession().insert("addAlarmResource", list);
		} else {
			return 0;
		}

	}

	@Override
	public int insertRule(Map<String, String> map) {// 用来更新profile表中，与流入流出，数据包和和速度等相关
		return super.getSession().update("netflowupdateProfile", map);
	}

	@Override
	public int insertApps(List<AlarmResourceBo> apps) {// 批量插入多个应用
		return super.getSession().insert("insertApps", apps);
	}

	@Override
	public int insertApp(Map<String, String> map) {// 插入单个应用
		return super.getSession().insert("insertApp", map);
	}

	@Override
	public int insertProtos(List<AlarmResourceBo> protos) {// 批量插入多个协议
		return super.getSession().insert("insertProtos", protos);
	}

	@Override
	public int insertProto(Map<String, String> map) {// 插入单个协议
		return super.getSession().insert("insertProto", map);
	}

	@Override
	public int insertIPs(Map<String, String> map) {// 插入ip
		return super.getSession().insert("insertIP", map);
	}

	@Override
	public int delDevices(int profileId) {
		return super.getSession().delete("delDevices", profileId);
	}

	@Override
	public int delApps(int profileId) {
		return super.getSession().delete("delApps", profileId);
	}

	@Override
	public int delProtos(int profileId) {
		return super.getSession().delete("delProtos", profileId);
	}

	@Override
	public int delIPs(int profileId) {
		return super.getSession().delete("delIps", profileId);
	}

	@Override
	public int updateAlarmStatus(int profileId, int status) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("profileId", profileId);
		m.put("status", status);
		return super.getSession().update("updateStatus", m);
	}

	@Override
	public int addThresholds(List<AlarmThresholdBo> thresholds) {
		return super.getSession().insert("addThresholds", thresholds);
	}

	@Override
	public int delThresholds(int profileId) {
		return super.getSession().delete("delThresholds", profileId);
	}

	@Override
	public int delProfiles(String[] ids) {
		return super.getSession().delete("delProfiles", ids);
	}

	@Override
	public AlarmProfileBo loadBasic(int profileId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("profileId", profileId);
		return super.getSession().selectOne("loadBasicAlarmInfo", param);
	}

	@Override
	public List<AlarmThresholdBo> loadThreshold(int profileId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("profileId", profileId);
		return super.getSession().selectList("loadThreshold", param);
	}

	@Override
	public int updateAlarmDeviceType(String profileId, String deviceType) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("profileId", profileId);
		map.put("deviceType", deviceType);
		return super.getSession().update("updateDeviceType", map);
	}

	@Override
	public List<AlarmContent> getAlarmEvents() {
		return super.getSession().selectList("getAlarms");
	}

	@Override
	public int updateAlarmEventState(String[] ids) {
		return super.getSession().update("updateAlarmEventState", ids);
	}

	@Override
	public AlarmResourceBo getPrfileLevel(String thresholdId) {
		return super.getSession().selectOne("getPrfileLevel", thresholdId);
	}

	@Override
	public int getCount(String name, Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("id", id);
		return super.getSession().selectOne("alarm_getCount", map);
	}

	@Override
	public List<String> loadLocalInterfaces(String profileID) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("profileID", profileID);
		return super.getSession().selectList("loadLocalInterfaces",map);
	}

	@Override
	public List<String> loadLocalIPGoups(String profileId) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("profileID", profileId);
		return super.getSession().selectList("loadLocalInterfaces",map);
	}
	
	@Override
	public List<String> loadLocalIFGoups(String profileId) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("profileID", profileId);
		return super.getSession().selectList("loadLocalInterfaces",map);
	}
	
	

}
