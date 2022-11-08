package com.mainsteam.stm.common.sync.dao;

import java.util.List;

import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;


public interface DataSyncDAO {

	/**
	 * @param po
	 */
	void save(DataSyncPO po);
	/**
	 * @return
	 */
	DataSyncPO catchOne (DataSyncTypeEnum type);
	
	/**
	 * @return
	 */
	List<DataSyncPO> catchList (DataSyncTypeEnum type);

	List<DataSyncPO> catchList(List<DataSyncTypeEnum> typeEnums);
	
	/**
	 * 每次查询多少条数据
	 * @param start
	 * @param limit
	 * @return
	 */
	List<DataSyncPO> catchList (DataSyncTypeEnum type, int start, int limit);
	
	/**
	 * @param po
	 * @return
	 */
	boolean update(DataSyncPO po);
	
	void updateForRunning(List<Long> ides);
	
	/**
	 * @param id
	 * @return
	 */
	void delete(long id);

	void delete(List<Long> ids);
	
}
