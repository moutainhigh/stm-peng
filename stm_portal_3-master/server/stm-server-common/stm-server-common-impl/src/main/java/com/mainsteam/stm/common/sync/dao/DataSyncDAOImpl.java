package com.mainsteam.stm.common.sync.dao;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;


public class DataSyncDAOImpl implements DataSyncDAO {
	private static Log logger=LogFactory.getLog(DataSyncDAOImpl.class);
	private SqlSession session;
	private ISequence sequence;
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}	
	
	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public void save(DataSyncPO po) {
		long  seq=sequence.next();
		
		po.setId(seq);
		if(po.getCreateTime() == null)
			po.setCreateTime(new Date());
		if(po.getUpdateTime() == null)
			po.setUpdateTime(new Date());
		session.insert("saveSync",po);
	}

	@Override
	public DataSyncPO catchOne(DataSyncTypeEnum type) {
		return session.selectOne("catchOne",type);
	}
	@Override
	public List<DataSyncPO> catchList(DataSyncTypeEnum type) {
		return session.selectList("selectList",type);
	}

	@Override
	public List<DataSyncPO> catchList(List<DataSyncTypeEnum> typeEnums) {
		return session.selectList("selectBatch", typeEnums);
	}

	@Override
	public boolean update(DataSyncPO po) {
		return session.update("updateSync", po)>0;
	}
	
	@Override
	public void updateForRunning(List<Long> ides) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long id : ides) {
				Map<String, Object> map = new HashMap<String, Object>();  
				map.put("id", id);
				map.put("updateTime", Calendar.getInstance().getTime());
				batchSession.update("updateForRunning", map);
			}
			batchSession.commit();
		}
	}
	

	@Override
	public void delete(long id) {
		session.delete("deleteSync", id);
	}

	@Override
	public void delete(List<Long> ids) {
		int total = ids.size();
		if(total < 1000){
			session.delete("deleteBatch", ids);
		}else{ //为了兼容oracle和达梦数据库，oracle预先的in长度为1000，达梦最多2048
			int fromIndex = 0;
			int toIndex = 1000;
			int mod = total % 1000;
			while (toIndex <= total) {
				List<Long> subList = ids.subList(fromIndex, toIndex);
				session.delete("deleteBatch", subList);
				fromIndex = toIndex;
				toIndex += 1000;
				if(toIndex > total){
					toIndex = toIndex - 1000 + mod;
				}
			}
		}
	}

	@Override
	public List<DataSyncPO> catchList(DataSyncTypeEnum type, int start, int limit) {
		
		Page<DataSyncPO, Object> page = new Page<DataSyncPO, Object>(start, limit);
		
		page.setCondition(type);
		
		return session.selectList("selectListLimit", page);
	}
}
