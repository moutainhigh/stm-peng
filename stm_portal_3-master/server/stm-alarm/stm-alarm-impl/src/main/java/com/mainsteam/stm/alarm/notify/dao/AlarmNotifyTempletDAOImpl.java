package com.mainsteam.stm.alarm.notify.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTempletParamter;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;

public class AlarmNotifyTempletDAOImpl implements AlarmNotifyTempletDAO {
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}	
	@Override
	public List<AlarmNotifyTemplet> findTempletBySysID(SysModuleEnum sysID,AlarmProviderEnum provider) {
		Map<String,Object> param=new HashMap<>();
		param.put("sysID", sysID);
		param.put("provider", provider);
		List<AlarmNotifyTemplet> list= session.selectList("findTempletBySysID",param);
		for(AlarmNotifyTemplet tmp:list){
			List<AlarmNotifyTempletParamter> params=session.selectList("findTempletParamterByTempID",tmp.getTmpID());
			tmp.setParams(params);
		}
		return list;
	}
	@Override
	public void addTemplet(AlarmNotifyTemplet tmp) {
		session.insert("addTemplet", tmp);
		List<AlarmNotifyTempletParamter> params=tmp.getParams();
		if(params!=null && params.size()>0){
			for(AlarmNotifyTempletParamter param:params){
				session.insert("addTempletParamter", param);
			}
		}
	}

}
