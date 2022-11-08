package com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao;

import java.util.List;

import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

public interface ProfileDao {

	public List<ProfileInfoPO> queryAllParentProfiles();
	
	public List<ProfileInfoPO> queryProfileByParent(long parentId);
	
	public List<ProfileMetricPO> queryMetricsByProfile(long profileId);
	
	public List<ProfileInfoPO> queryProfileInfoPOsByResourceId(String resourceId);
	
	
	public List<ProfileInfoPO> queryProfileInfoById(long profileId);
	
	public List<ProfileInfoPO> queryProfilInfoByResourceId(String resourceId);
	
	
	public List<ProfileInstancePo> queryProfileInstanceRel(long profileId);
	
	public List<ProfileInstancePo> queryProfileInstanceLastRel(long profileId);
	
	public List<ProfileInstancePo> queryProfileInstanceRelByResourceId(String resourceId);
	
	public List<ProfileInstancePo> queryProfileInstanceLastRelByResourceId(String resourceId);
	
	int deleteProfileInstanceRel(long profileId);
	
	int deleteProfileInstanceLastRel(long profileId);
	
	int deleteProfileInstanceRelByResourceId(String resourceId);
	
	int deleteProfileInstanceLastRelByResourceId(String resourceId);
	
}
