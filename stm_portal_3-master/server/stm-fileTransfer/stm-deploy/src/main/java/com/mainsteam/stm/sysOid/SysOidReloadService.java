package com.mainsteam.stm.sysOid;

import java.util.List;

import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.sysOid.obj.ModuleBo;

public interface SysOidReloadService {

	List<DeployRecord> getDeployRecordBySourceID(String sourceID);
	
	public List<DeployRecord> sysOidReload(final ModuleBo moduleBo);
}
