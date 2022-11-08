package com.mainsteam.stm.dataprocess.bigData.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ProfileThresholdSyncDO;

public interface ProfileThresholdSyncDAO {

	List<ProfileThresholdSyncDO> getAllThreshold(Page<ProfileThresholdSyncDO,ProfileThresholdSyncDO> page) throws Exception;
}
