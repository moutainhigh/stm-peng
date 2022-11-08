package com.mainsteam.stm.portal.report.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryInstanceDao;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryInstancePo;

public class IReportTemplateDirectoryInstanceDaoImpl extends BaseDao<ReportTemplateDirectoryInstancePo> implements IReportTemplateDirectoryInstanceDao {

	public IReportTemplateDirectoryInstanceDaoImpl(SqlSessionTemplate session) {
		super(session, IReportTemplateDirectoryInstanceDao.class.getName());
	}

	@Override
	public int deleteInstanceRelationByDirectoryId(
			long reportTemplateDirectoryId) {
		return getSession().delete(getNamespace() + "deleteInstanceRelationByDirectoryId",  reportTemplateDirectoryId);
	}

	@Override
	public int deleteInstanceRelationByInstanceId(long[] ids) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteInstanceRelationByInstanceId",  ids);
	}

}
