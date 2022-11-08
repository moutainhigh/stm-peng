package com.mainsteam.stm.dataprocess.bigData.dao;

import java.util.List;

import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ResourceInstanceSyncDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ResourceIntanceSyncDAO {

	public List<ResourceInstanceSyncDO> getAllResourceInstance(Page<ResourceInstanceSyncDO,ResourceInstanceSyncDO> page) throws Exception;

	
}
