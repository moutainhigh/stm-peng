package com.mainsteam.stm.auditlog.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.auditlog.bo.AuditlogQueryBo;
import com.mainsteam.stm.auditlog.bo.AuditlogRuleBo;
import com.mainsteam.stm.auditlog.dao.IAuditlogDao;

public class AuditlogDaoImpl extends BaseDao<AuditlogBo> implements IAuditlogDao {
	public AuditlogDaoImpl(SqlSessionTemplate session){
		super(session,IAuditlogDao.class.getName());
	}
	


	@Override
	public List<AuditlogBo> pageSelect(Page<AuditlogBo, AuditlogQueryBo> page) {
		List<AuditlogBo> bos= getSession().selectList(getNamespace()+"pageSelect", page);
		return bos;
	}



	@Override
	public List<AuditlogBo> selectAllList(AuditlogQueryBo condition) {
		return getSession().selectList(getNamespace()+"selectAllList",condition);
	}



	@Override
	public int deleteSelect(long[] ids) {
		return getSession().update(getNamespace()+"deleteSelect",ids);
	}



	@Override
	public int deleteAll() {
		return getSession().update(getNamespace()+"deleteAll");
	}



	@Override
	public List<AuditlogBo> selectBuList(Page<AuditlogBo, AuditlogQueryBo> page) {
		List<AuditlogBo> bos = getSession().selectList(getNamespace()+"selectBuList",page);
		return bos;
	}



	@Override
	public int queryBuCount(AuditlogBo auditlogBo) {
		return super.getSession().selectOne("queryBuCount",auditlogBo);
	}



	@Override
	public int insertBuAuditlog(List<AuditlogBo> auditlogBo) {
		return super.getSession().insert(getNamespace()+"insertBuAuditlog",auditlogBo);
	}



	@Override
	public List<AuditlogQueryBo> queryLastTime() {
		return getSession().selectList(getNamespace()+"queryLastTime");
	}



	@Override
	public List<AuditlogBo> queryBulist(Page<AuditlogBo, AuditlogQueryBo> page) {
		List<AuditlogBo> bos = getSession().selectList(getNamespace()+"queryBulist",page);
		return bos;
	}



	@Override
	public int updateAuditlog(AuditlogBo auditlogBo) {
		return getSession().update(getNamespace()+"updateAuditlog",auditlogBo);
	}



	@Override
	public int insertBuAuditlogRule(AuditlogRuleBo auditlogRuleBo) {
		return getSession().insert(getNamespace()+"insertBuAuditlogRule",auditlogRuleBo);
	}



	@Override
	public List<AuditlogRuleBo> selectAuditlogRule() {
		return getSession().selectList(getNamespace()+"selectAuditlogRule");
	}



	@Override
	public int updateAuditlogRule(AuditlogRuleBo auditlogRuleBo) {
		return getSession().update(getNamespace()+"updateAuditlogRule",auditlogRuleBo);
	}

}
