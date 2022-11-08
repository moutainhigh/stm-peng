package com.mainsteam.stm.deploy.dao;

import java.util.List;

import com.mainsteam.stm.deploy.obj.DeployRecord;

public interface DeployRecordDAO {

	void save(DeployRecord record);
	
	
	List<DeployRecord> getDeployRecordBySourceID(String sourceID);
}
