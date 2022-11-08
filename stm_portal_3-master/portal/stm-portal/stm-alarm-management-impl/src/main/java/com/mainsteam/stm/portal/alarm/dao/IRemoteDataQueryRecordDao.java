package com.mainsteam.stm.portal.alarm.dao;

import java.util.List;

import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;


public interface IRemoteDataQueryRecordDao {
	/*
	 * add
	 * */
	public int add(RemoteDataQueryRecord rdqr);
	/*
	 * loadByIp
	 * */
	public List<RemoteDataQueryRecord> loadByIp(String ip);
	/*
	 * update
	 * */
	public int update(RemoteDataQueryRecord rdqr);
	
	/*
	 * delete
	 * */
	public int delete(Long[] recordIds);
}
