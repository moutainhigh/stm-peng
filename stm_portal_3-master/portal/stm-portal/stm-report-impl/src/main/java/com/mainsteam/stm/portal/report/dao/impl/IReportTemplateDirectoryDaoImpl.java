package com.mainsteam.stm.portal.report.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryDao;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryPo;

public class IReportTemplateDirectoryDaoImpl extends BaseDao<ReportTemplateDirectoryPo> implements
		IReportTemplateDirectoryDao {

	public IReportTemplateDirectoryDaoImpl(SqlSessionTemplate session) {
		super(session, IReportTemplateDirectoryDao.class.getName());
	}

	@Override
	public List<Long> selectDirectoryIdByTemplateId(long reportTemplateId) {
		List<Long> directoryIds = getSession().selectList(getNamespace() + "selectDirectoryIdByTemplateId", reportTemplateId);
		return directoryIds;
	}

	@Override
	public List<Long> selectTemplateIdByTemplateId(long[] ids) {
		List<Long> templateIds = getSession().selectList(getNamespace() + "selectTemplateIdByTemplateId", ids);
		return templateIds;
	}

}
