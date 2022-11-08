package com.mainsteam.stm.portal.alarm.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.portal.alarm.api.IRemoteDataQueryRecordApi;
import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;
import com.mainsteam.stm.portal.alarm.dao.IRemoteDataQueryRecordDao;

@Service("remoteDataQueryRecordService")
public class RemoteDataQueryRecordImpl implements IRemoteDataQueryRecordApi{
	private static final Logger logger = LoggerFactory.getLogger(RemoteDataQueryRecordImpl.class);
	
	private ISequence reportSeq;
	
	@Resource
	private IRemoteDataQueryRecordDao remoteDataQueryRecordDao;
	
	
	@Override
	public int add(RemoteDataQueryRecord rdqr){
		rdqr.setRecordId(reportSeq.next());
		return remoteDataQueryRecordDao.add(rdqr);
	}
	
	@Override
	public List<RemoteDataQueryRecord> loadByIp(String ip){
		return remoteDataQueryRecordDao.loadByIp(ip);
	}
	
	@Override
	public int update(RemoteDataQueryRecord rdqr){
		return remoteDataQueryRecordDao.update(rdqr);
	}
	
	@Override
	public int addORupdate(RemoteDataQueryRecord rdqr){
		if(null==rdqr.getRecordId()){
			return remoteDataQueryRecordDao.add(rdqr);
		}
		return remoteDataQueryRecordDao.update(rdqr);
	}
	
	@Override
	public int delete(Long[] recordIds){
		return remoteDataQueryRecordDao.delete(recordIds);
	}

	public void setRemoteDataQueryRecordDao(
			IRemoteDataQueryRecordDao remoteDataQueryRecordDao) {
		this.remoteDataQueryRecordDao = remoteDataQueryRecordDao;
	}
	
	
	@Autowired
	public RemoteDataQueryRecordImpl(SequenceFactory sequenceFactory){
		this.reportSeq=sequenceFactory.getSeq("stm_alarm_remote_data_query_record");
	}
}
