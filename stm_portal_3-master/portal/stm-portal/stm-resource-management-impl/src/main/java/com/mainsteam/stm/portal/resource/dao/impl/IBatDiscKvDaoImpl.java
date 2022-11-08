package com.mainsteam.stm.portal.resource.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.resource.bo.BatdisckvBo;
import com.mainsteam.stm.portal.resource.dao.IBatDiscKvDao;

public class IBatDiscKvDaoImpl extends BaseDao<BatdisckvBo> implements
		IBatDiscKvDao {

	public IBatDiscKvDaoImpl(SqlSessionTemplate session) {
		super(session, IBatDiscKvDao.class.getName());
	}

	@Override
	public List<BatdisckvBo> getList() {
		return getSession().selectList(getNamespace() + "getList");
	}

	@Override
	public int initBatDiscKvTable(List<BatdisckvBo> list) {
		deleteAll();
		return super.batchInsert(list);
	}
	
	private void deleteAll(){
		getSession().delete(getNamespace() + "deleteAll");
	}
}
