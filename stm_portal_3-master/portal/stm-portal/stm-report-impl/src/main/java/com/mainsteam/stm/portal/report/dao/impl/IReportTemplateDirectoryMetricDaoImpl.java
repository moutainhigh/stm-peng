package com.mainsteam.stm.portal.report.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryMetricDao;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryMetricPo;

public class IReportTemplateDirectoryMetricDaoImpl extends BaseDao<ReportTemplateDirectoryMetricPo> implements
		IReportTemplateDirectoryMetricDao {

	public IReportTemplateDirectoryMetricDaoImpl(SqlSessionTemplate session) {
		super(session, IReportTemplateDirectoryMetricDao.class.getName());
	}

	@Override
	public int deleteMetricRelationByDirectoryId(
			long reportTemplateDirectoryId) {
		return getSession().delete(getNamespace() + "deleteMetricRelationByDirectoryId", reportTemplateDirectoryId);
	}

}
