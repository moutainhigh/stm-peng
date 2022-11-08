package com.mainsteam.stm.simple.manager.workbench.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.simple.manager.workbench.dao.IWorkbenchDao;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ExpectBo;

@Repository("managerWorkbenchDao")
public class WorkbenchDaoImpl extends BaseDao<ExpectBo> implements IWorkbenchDao {

	@Autowired
	public WorkbenchDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IWorkbenchDao.class.getName());
	}

	@Override
	public ExpectBo get(Long id) {
		return getSession().selectOne(getNamespace()+"get",id);
	}

	@Override
	public List<ExpectBo> select(Long reportId) {
		return getSession().selectList(getNamespace()+"select",reportId);
	}

	@Override
	public int delete(long id) {
		return getSession().delete(getNamespace()+"delete",id);
	}
	
	

}
