package com.mainsteam.stm.portal.report.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDao;
import com.mainsteam.stm.portal.report.po.ReportTemplatePo;

public class IReportTemplateDaoImpl extends BaseDao<ReportTemplatePo> implements IReportTemplateDao {

	public IReportTemplateDaoImpl(SqlSessionTemplate session) {
		super(session, IReportTemplateDao.class.getName());
	}
	
	public List<ReportTemplatePo> getReportTemplateListByType(int type){
		return getSession().selectList(getNamespace() + "getReportTemplateListByType", type);
	}

	@Override
	public int logicDelete(Long reportTemplateId) {
		return getSession().update(getNamespace()+"logicDelete",reportTemplateId);
	}

}
