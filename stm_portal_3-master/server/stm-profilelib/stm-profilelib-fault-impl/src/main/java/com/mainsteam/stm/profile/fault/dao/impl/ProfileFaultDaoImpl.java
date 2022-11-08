package com.mainsteam.stm.profile.fault.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultDao;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;

public class ProfileFaultDaoImpl implements ProfileFaultDao {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public List<Profilefault> pageSelect(Page<Profilefault, Profilefault> page) {
		return session.selectList("pageSelectProfileFault", page);
	}

	@Override
	public ProfileFaultRelation get(long profieId) {
		return session.selectOne("getProfileFault", profieId);
	}

	@Override
	public int insert(Profilefault profilefault) {
		return session.insert("insertProfileFault", profilefault);
	}

	@Override
	public int update(Profilefault profilefault) {
		
		return session.update("updateProfileFault", profilefault);
	}
	
	@Override
	public int updateState(long profileId) {
		return session.update("updateProfileFaultState", profileId);
	}

	@Override
	public int batchDel(Long[] profileIds) {
		return session.delete("batchDeleteProfileFault",profileIds);
	}

	@Override
	public Profilefault queryProfilefaultByInstanceAndMetric(String instanceId,
			String metricId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceId", instanceId);
		params.put("metricId", metricId);
		return session.selectOne("queryProfilefaultByInstanceAndMetric",params);
	}

	

	

}
