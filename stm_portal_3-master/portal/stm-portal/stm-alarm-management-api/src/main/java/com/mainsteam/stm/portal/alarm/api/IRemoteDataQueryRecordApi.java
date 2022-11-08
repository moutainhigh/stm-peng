package com.mainsteam.stm.portal.alarm.api;

import java.util.List;

import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;


public interface IRemoteDataQueryRecordApi {
	
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
	
	/*
	 * add  or  update
	 * */
	public int addORupdate(RemoteDataQueryRecord rdqr);
}
