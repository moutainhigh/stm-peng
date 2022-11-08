package com.mainsteam.stm.portal.alarm.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;
import com.mainsteam.stm.portal.alarm.dao.IRemoteDataQueryRecordDao;

@Repository("remoteDataQueryRecordDao")
public class RemoteDataQueryRecordDaoImpl extends BaseDao<RemoteDataQueryRecord> implements IRemoteDataQueryRecordDao {
	@Autowired
	public RemoteDataQueryRecordDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IRemoteDataQueryRecordDao.class.getName());
	}
	
	@Override
	public int add(RemoteDataQueryRecord rdqr) {
		return getSession().insert(getNamespace()+"insert", rdqr);
	}
	
	@Override
	public List<RemoteDataQueryRecord> loadByIp(String ip) {
		
		return getSession().selectList(getNamespace()+"selectByIp", ip);
	}
	
	@Override
	public int update(RemoteDataQueryRecord entity) {
		return getSession().update(getNamespace()+"update",entity);
	}
	
	@Override
	public int delete(Long[] recordIds) {
		return getSession().insert(getNamespace()+"delete", recordIds);
	}
}
