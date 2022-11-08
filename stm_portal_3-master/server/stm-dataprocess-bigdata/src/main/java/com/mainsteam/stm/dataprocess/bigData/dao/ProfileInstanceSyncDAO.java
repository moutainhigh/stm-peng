package com.mainsteam.stm.dataprocess.bigData.dao;

import java.util.List;

import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ProfileInstanceDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ProfileInstanceSyncDAO {

	List<ProfileInstanceDO> getAllProfileInstance(Page<ProfileInstanceDO,ProfileInstanceDO> page) throws Exception;
}
