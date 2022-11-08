package com.mainsteam.stm.portal.report.dao;

import java.util.List;

import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryInstancePo;

public interface IReportTemplateDirectoryInstanceDao {
	
	int insert(ReportTemplateDirectoryInstancePo reportTemplateDirectoryInstancePo);
	
	int deleteInstanceRelationByDirectoryId(long reportTemplateDirectoryId);
	
	int deleteInstanceRelationByInstanceId(long[] ids);
	
	List<ReportTemplateDirectoryInstancePo> select(ReportTemplateDirectoryInstancePo reportTemplateDirectoryInstancePo);
	
}
