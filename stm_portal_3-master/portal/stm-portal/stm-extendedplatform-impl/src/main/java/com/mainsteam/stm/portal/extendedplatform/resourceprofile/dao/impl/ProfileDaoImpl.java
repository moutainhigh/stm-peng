package com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.ProfileDao;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

public class ProfileDaoImpl implements ProfileDao {

	private SqlSession session;
	
	@Override
	public List<ProfileInfoPO> queryAllParentProfiles() {
		return session.selectList("queryAllParentProfiles");
	}

	@Override
	public List<ProfileInfoPO> queryProfileByParent(long parentId) {
		return null;
	}

	@Override
	public List<ProfileMetricPO> queryMetricsByProfile(long profileId) {
		return session.selectList("queryMetricsByProfile",profileId);
	}

	
	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public List<ProfileInfoPO> queryProfileInfoPOsByResourceId(String resourceId) {
		return session.selectList("queryProfileInfoPOsByResourceId", resourceId);
	}

	@Override
	public List<ProfileInfoPO> queryProfileInfoById(long profileId) {
		return session.selectList("queryProfileInfoById", profileId);
	}

	@Override
	public List<ProfileInfoPO> queryProfilInfoByResourceId(String resourceId) {
		return session.selectList("queryProfilInfoByResourceId", resourceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceRel(long profileId) {
		return session.selectList("queryProfileInstanceRel",profileId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastRel(long profileId) {
		return session.selectList("queryProfileInstanceLastRel", profileId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceRelByResourceId(String resourceId) {
		return session.selectList("queryProfileInstanceRelByResourceId", resourceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastRelByResourceId(String resourceId) {
		return session.selectList("queryProfileInstanceLastRelByResourceId", resourceId);
	}

	@Override
	public int deleteProfileInstanceRel(long profileId) {
		return session.delete("com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.ProfileDao.deleteProfileInstanceRel", profileId);
	}

	@Override
	public int deleteProfileInstanceLastRel(long profileId) {
		return session.delete("com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.ProfileDao.deleteProfileInstanceLastRel", profileId);
	}

	@Override
	public int deleteProfileInstanceRelByResourceId(String resourceId) {
		return session.delete("deleteProfileInstanceRelByResourceId", resourceId);
	}

	@Override
	public int deleteProfileInstanceLastRelByResourceId(String resourceId) {
		return session.delete("deleteProfileInstanceLastRelByResourceId", resourceId);
	}
}
